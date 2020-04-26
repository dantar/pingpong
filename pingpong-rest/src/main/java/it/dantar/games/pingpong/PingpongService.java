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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.DroppedStalePlayerSseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.models.Player;

@Service
public class PingpongService {

	// interesting async:
	// private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

	List<SseEmitter> emitters = new ArrayList<SseEmitter>();
	
	Map<String, Player> players = new HashMap<String, Player>();
	
	static final long TIMEOUT = 1000*60*60*4L;
	
	public SseEmitter newPlayerSse(String uuid) {
		return players.get(uuid)
				.setEmitter(new SseEmitter(TIMEOUT))
				.getEmitter();
	}

	public void broadcastMessage(SseDto message) {
		Set<Player> stale = new HashSet<>();
		players.forEach((uuid, player) -> {			
			SseEmitter emitter = player.getEmitter();
			if (emitter == null) return;
			try {
				emitter.send(SseEmitter.event()
						.data(message, MediaType.APPLICATION_JSON)
						);
				Logger.getLogger(this.getClass().getName()).info(String.format("Sse sent to %s", uuid));
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).info(String.format("Stale emitter: %s", uuid));
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

	public List<PlayerDto> listPlayers() {
		return this.players.entrySet().stream()
				.map(entry -> entry.getValue().getDto())
				.collect(Collectors.toList());
	}
	
}
