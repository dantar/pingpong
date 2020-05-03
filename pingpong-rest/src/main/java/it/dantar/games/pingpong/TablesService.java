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
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.DroppedStalePlayerSseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.SeatDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.models.Player;
import it.dantar.games.pingpong.models.Table;

@Service
public class TablesService {

	// interesting async:
	// private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

	List<SseEmitter> emitters = new ArrayList<SseEmitter>();
	
	Map<String, Player> players = new HashMap<String, Player>();
	Map<String, Table> tables = new HashMap<String, Table>();
	
	static final long TIMEOUT = 1000*60*60*4L;
	
	@Autowired
	FantascattiService fantascattiService;

	public SseEmitter newPlayerSse(String uuid) {
		Player player = players.get(uuid);
		if (player == null) {
			player = new Player().setDto(new PlayerDto().setUuid(uuid));
			this.players.put(uuid, player);
		}
		return player
				.setEmitter(new SseEmitter(TIMEOUT))
				.getEmitter();
	}

	public SseEmitter newTableSse(String uuid) {
		return tables.get(uuid)
				.setEmitter(new SseEmitter(TIMEOUT))
				.getEmitter();
	}

	public void broadcastMessage(SseDto message) {
		broadcastMessageToPlayers(message, players.values().stream());
	}

	private void broadcastMessageToPlayers(SseDto message, Stream<Player> streamOfPlayers) {
		Set<Player> stale = new HashSet<>();
		streamOfPlayers.forEach(player -> {
			SseEmitter emitter = player.getEmitter();
			if (emitter == null) return;
			try {
				emitter.send(SseEmitter.event()
						.data(message, MediaType.APPLICATION_JSON)
						);
				Logger.getLogger(this.getClass().getName()).info(String.format("Sse sent to %s", player.getDto().getUuid()));
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).info(String.format("Stale emitter: %s", player.getDto().getUuid()));
				emitter.completeWithError(e);
				player.setEmitter(null);
				stale.add(player);
			}
		});
		if (stale.size() > 0) {
			stale.forEach(player -> {
				this.players.remove(player.getDto().getUuid());
			});
			this.broadcastMessage(new DroppedStalePlayerSseDto()
					.setPlayers(
							stale.stream()
							.map(player -> player.getDto())
							.collect(Collectors.toList())));
		}
	}

	public void register(PlayerDto dto) {
		Player player;
		if (dto.getUuid() == null || dto.getUuid().isEmpty()) {
			player = new Player().setDto(dto
					.setUuid(UUID.randomUUID().toString()));
			this.players.put(player.getDto().getUuid(), player);			
		} else {
			player = this.players.get(dto.getUuid());
			if (player == null) {
				this.players.put(dto.getUuid(), new Player().setDto(dto));
			}
		}
	}

	public void newTable(TableDto table) {
		table.setUuid(UUID.randomUUID().toString());
		fantascattiService.newGame(table);
		this.tables.put(table.getUuid(), new Table().setDto(table));
	}

	public TableDto getTable(String uuid) {
		return this.tables.get(uuid).getDto();
	}
	
	public PlayerDto getPlayer(String uuid) {
		return this.players.get(uuid).getDto();
	}

	public List<PlayerDto> listPlayers() {
		return this.players.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}

	public List<TableDto> listPlayerTables(String playerId) {
		return this.tables.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.filter(t -> {
					if (t.getOwner().getUuid().equals(playerId)) return true;
					for (SeatDto seatDto : t.getSeats()) {
						if (seatDto.getOpen()) return true;
						if (seatDto.getPlayer() != null && seatDto.getPlayer().getUuid().equals(playerId)) return true; 
					}
					return false;
				})
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

}
