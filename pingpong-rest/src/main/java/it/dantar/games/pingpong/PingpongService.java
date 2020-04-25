package it.dantar.games.pingpong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class PingpongService {

	List<SseEmitter>players = new ArrayList<SseEmitter>();
	long timeout = 1000*60*60*4L;
	
	public SseEmitter newPlayerSse() {
		SseEmitter emitter = new SseEmitter(timeout);
		players.add(emitter);
		Logger.getLogger(this.getClass().getName()).info(String.format("Total registerd emitters: %s", players.size()));
		return emitter;
	}

	public void broadcastMessage(String message) {
		List<SseEmitter> stale = new ArrayList<>();
		players.forEach(p -> {
			try {
				p.send(message);
				Logger.getLogger(this.getClass().getName()).info(String.format("Message sent!: %s", players.size()));
			} catch (IOException e) {
				p.completeWithError(e);
				stale.add(p);
				Logger.getLogger(this.getClass().getName()).info(String.format("Stale emitter: %s", players.size()));
			}
		});
		stale.forEach(s->{			
			players.remove(s);
		});
		Logger.getLogger(this.getClass().getName()).info(String.format("Removed stale emitters: %s", players.size()));
	}
	
}
