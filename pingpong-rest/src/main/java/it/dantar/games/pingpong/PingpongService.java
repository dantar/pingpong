package it.dantar.games.pingpong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class PingpongService {

	// interesting async:
	// private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

	List<SseEmitter> emitters = new ArrayList<SseEmitter>();
	
	Map<String, Player> players = new HashMap<String, Player>();
	Map<String, Table> tables = new HashMap<String, Table>();
	
	static final long TIMEOUT = 1000*60*60*4L;
	
	public SseEmitter newPlayerSse(String uuid) {
		return players.get(uuid)
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
		dto.setUuid(UUID.randomUUID().toString());
		this.players.put(dto.getUuid(), new Player().setDto(dto));
	}

	public void newTable(TableDto table) {
		table.setUuid(UUID.randomUUID().toString());
		this.tables.put(table.getUuid(), new Table().setDto(table));
	}

	public TableDto getTable(String uuid) {
		return this.tables.get(uuid).getDto();
	}

	public List<PlayerDto> listPlayers() {
		return this.players.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}

	public List<TableDto> listTables() {
		return this.tables.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}

	public void broadcastMessageToTable(TableDto table, SseDto message) {
		List<SeatDto> openSeats = this.tables.get(table.getUuid()).getDto().getSeats().stream().filter(s->s.getOpen()).collect(Collectors.toList());
		if (openSeats.size() > 0) {
			this.broadcastMessage(message);
		} else {
			this.broadcastMessageToPlayers(message, this.tables.get(table.getUuid()).getDto()
					.getSeats()
					.stream()
					.filter(s->s.getPending() && s.getPlayer()!=null)
					.map(s->this.players.get(s.getPlayer().getUuid())) );;
		}
	}

}
