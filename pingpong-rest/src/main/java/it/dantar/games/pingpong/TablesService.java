package it.dantar.games.pingpong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.DroppedStalePlayerSseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.RegisterPlayerSseDto;
import it.dantar.games.pingpong.dto.SeatDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.dto.TablePlayerAcceptSseDto;
import it.dantar.games.pingpong.dto.TablePlayerInvitationSseDto;
import it.dantar.games.pingpong.models.Player;
import it.dantar.games.pingpong.models.Table;

@Service
public class TablesService {

	// interesting async:
	// private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

	List<SseEmitter> emitters = new ArrayList<SseEmitter>();
	
	Map<String, Player> players = new HashMap<String, Player>();
	Map<String, Table> tables = new HashMap<String, Table>();
	Map<String, Table> owners = new HashMap<String, Table>();
	Map<String, Table> seats = new HashMap<String, Table>();
	
	static final long TIMEOUT = 1000*60*60*2L;
	
	@Autowired
	FantascattiService fantascattiService;

	public void register(PlayerDto dto) {
		Player player;
		if (dto.getUuid() == null || dto.getUuid().isEmpty()) {
			player = new Player().setDto(dto
					.setUuid(UUID.randomUUID().toString()));
		} else {
			player = new Player().setDto(dto);
		}
		this.players.put(player.getDto().getUuid(), player);			
	}

	public SseEmitter requestPlayerSse(String uuid) {
		Player player = players.get(uuid);
		if (player == null) {
			throw new RuntimeException("Player does not exist");
		}
		return requestPlayerSse(player);
	}

	private SseEmitter requestPlayerSse(Player player) {
		SseEmitter emitter = new SseEmitter(TIMEOUT);
		Logger logger = Logger.getLogger(this.getClass().getName());
		emitter.onCompletion(new Runnable() {
			@Override
			public void run() {
				logger.info(String.format("onCompletion server event for %s", player.getDto().getUuid()));
				//players.remove(player.getDto().getUuid());
			}
		});
		player.setEmitter(emitter);
		return emitter;
	}

	@Scheduled(fixedRate = 1000*30L)
	public void refreshSse() {
		Logger.getLogger(this.getClass().getName()).info("sending ping");
		this.broadcastMessage(new SseDto().setCode("ping"));
	}
	
	public void ackPlayerSse(PlayerDto player) {
		Table table = this.owners.get(player.getUuid());
		if (table == null)
			table = this.seats.get(player.getUuid());
		this.broadcastMessage(new RegisterPlayerSseDto()
				.setPlayer(player)
				.setTable(table == null? null : table.getDto())
				);
	}

	public void broadcastMessage(SseDto message) {
		broadcastMessageToPlayers(message, players.values().stream());
	}

	private void broadcastMessageToPlayers(SseDto message, Stream<Player> streamOfPlayers) {
		Set<Player> stale = new HashSet<>();
		Set<Player> sent = new HashSet<>();
		streamOfPlayers.forEach(player -> {
			SseEmitter emitter = player.getEmitter();
			if (emitter == null) return;
			try {
				emitter.send(SseEmitter.event()
						.data(message, MediaType.APPLICATION_JSON)
						);
				sent.add(player);
			} catch (IOException | IllegalStateException e) {
				emitter.completeWithError(e);
				player.setEmitter(null);
				stale.add(player);
			}
		});
		Logger.getLogger(this.getClass().getName()).info(String.format("Sse %s sent to %s players", message.getCode(), sent.size()));
		if (stale.size() > 0) {
			Logger.getLogger(this.getClass().getName()).info(String.format("Stale emitters: %s", stale.size()));
			stale.forEach(player -> {
				String playerUuid = player.getDto().getUuid();
				Table table = this.owners.get(playerUuid);
				if (table != null) {
					this.owners.remove(playerUuid);
					this.tables.remove(table.getDto().getUuid());
				}
				this.players.remove(playerUuid);
			});
			this.broadcastMessage(new DroppedStalePlayerSseDto()
					.setPlayers(
							stale.stream()
							.map(player -> player.getDto())
							.collect(Collectors.toList())));
		}
	}

	public void newTable(TableDto table) {
		table.setUuid(UUID.randomUUID().toString());
		fantascattiService.newGame(table);
		this.tables.put(table.getUuid(), new Table().setDto(table));
		this.owners.put(table.getOwner().getUuid(), this.tables.get(table.getUuid()));
	}

	public TableDto getTable(String uuid) {
		return this.tables.get(uuid).getDto();
	}

	public void removeTable(TableDto table) {
		this.owners.remove(table.getOwner().getUuid());
		this.tables.remove(table.getUuid());
	}

	public PlayerDto getPlayer(String uuid) {
		return this.players.get(uuid).getDto();
	}

	public List<PlayerDto> listPlayers() {
		return this.players.entrySet().stream()
				.filter(p -> p.getValue().getEmitter() != null)
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}

	public void broadcastMessageToTable(TableDto tableDto, SseDto message) {
		String tableId = tableDto.getUuid();
		broadcastMessageToTable(tableId, message);
	}

	public void broadcastMessageToTable(String tableId, SseDto message) {
		Table table = this.tables.get(tableId);
		List<SeatDto> openSeats = this.tables.get(tableId).getDto().getSeats().stream().filter(s->s.getOpen()).collect(Collectors.toList());
		if (openSeats.size() > 0) {
			this.broadcastMessage(message);
		} else {
			Stream<Player> seats = this.tables.get(tableId).getDto()
					.getSeats()
					.stream()
					.filter(s -> s.getPlayer()!=null)
					.map(s->this.players.get(s.getPlayer().getUuid()));
			this.broadcastMessageToPlayers(message, Stream.concat(
					seats, 
					Arrays.asList(this.players.get(table.getDto().getOwner().getUuid())).stream() 
					));
		}
	}

	public void broadcastMessageToPlayer(PlayerDto player, SseDto message) {
		this.broadcastMessageToPlayers(message, Arrays.asList(this.players.get(player.getUuid())).stream());
	}

	public List<TableDto> listAllTables() {
		return this.tables.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}

	public TableDto tablePlayerAccept(String gameId, PlayerDto player) {
		TableDto table = getTable(gameId);
		table.getSeats().stream()
		.filter(s -> s.getPlayer()!=null)
		.collect(Collectors.toMap(s->s.getPlayer().getUuid(), Function.identity()))
		.get(player.getUuid())
		.setPending(false);
		broadcastMessage(new TablePlayerAcceptSseDto()
				.setTable(table)
				.setPlayer(player)
				.setAccepted(true));
		return table;
	}

	public TableDto tablePlayerReject(String gameId, PlayerDto player) {
		TableDto table = getTable(gameId);
		SeatDto seat = table.getSeats().stream()
		.filter(s -> s.getPlayer()!=null)
		.collect(Collectors.toMap(s->s.getPlayer().getUuid(), Function.identity()))
		.get(player.getUuid());
		table.getSeats().remove(seat);
		seats.remove(player.getUuid());
		broadcastMessage(new TablePlayerAcceptSseDto()
				.setTable(table)
				.setPlayer(player)
				.setAccepted(false));
		return table;
	}

	public TableDto tablePlayerInvitation(String gameId, PlayerDto player) {
		seats.put(player.getUuid(), this.tables.get(gameId));
		TableDto table = getTable(gameId).addSeat(new SeatDto()
				.setOpen(false)
				.setPending(true)
				.setPlayer(player)
				);
		broadcastMessage(new TablePlayerInvitationSseDto()
				.setTable(table)
				.setPlayer(player));
		return table;
	}

}
