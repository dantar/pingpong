package it.dantar.games.pingpong;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.DroppedStalePlayerSseDto;
import it.dantar.games.pingpong.dto.FantascattiCardDto;
import it.dantar.games.pingpong.dto.FantascattiNewGuessSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerPicksPieceSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerReadySseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.models.FantascattiGame;
import it.dantar.games.pingpong.models.FantascattiPiece;
import it.dantar.games.pingpong.models.Player;

@Service
public class FantascattiService {

	Map<String, FantascattiGame> games = new HashMap<String, FantascattiGame>();
	
	static final float CHANCE_OF_TWIN = 0.25f;
	
	static final FantascattiPiece[] PIECES = {
			new FantascattiPiece().setColor("blue").setShape("sword"),
			new FantascattiPiece().setColor("green").setShape("shield"),
			new FantascattiPiece().setColor("red").setShape("dragon"),
			new FantascattiPiece().setColor("yellow").setShape("gold"),
			new FantascattiPiece().setColor("black").setShape("chest")
	};
	
	static final long TIMEOUT = 1000*60*60*4L;
	
	public FantascattiGame newGame(TableDto table) {
		FantascattiGame game = new FantascattiGame()
				.setTableId(table.getUuid());
		game.getPlayers().add(new Player().setDto(table.getOwner()));
		table.getSeats().forEach(s -> {
			game.getPlayers().add(new Player().setDto(s.getPlayer()));
		});
		this.games.put(game.getTableId(), game);
		return game;
	}
	
	public SseEmitter newPlayerSse(String gameId, String playerId) {
		return gamePlayers(gameId)
				.get(playerId)
				.setEmitter(new SseEmitter(TIMEOUT))
				.getEmitter();
	}

	private Map<String, Player> gamePlayers(String gameId) {
		return this.games.get(gameId).getPlayers().stream()
				.collect(Collectors.toMap(p->p.getDto().getUuid(), Function.identity()));
	}

	public void playerReady(String gameId, PlayerDto player) {
		FantascattiGame game = this.games.get(gameId);
		game.getReady().add(player.getUuid());
		this.broadcastMessageToPlayers(gameId, new FantascattiPlayerReadySseDto().setPlayer(player));			
		if (game.getReady().size() >= game.getPlayers().size()) {
			game.getReady().clear();
			game.setGuess(randomGuess());
			this.broadcastMessageToPlayers(gameId, new FantascattiNewGuessSseDto().setGuess(game.getGuess()));			
		}
	}

	private FantascattiCardDto randomGuess() {
		List<FantascattiPiece> pieces = Arrays.asList(PIECES);
		Collections.shuffle(pieces);
		FantascattiCardDto result = new FantascattiCardDto()
				.setCorrect(pieces.get(0).getShape());
		if (Math.random() < CHANCE_OF_TWIN) {
			result
			.setColor1(pieces.get(0).getColor())
			.setColor2(pieces.get(1).getColor())
			.setShape1(pieces.get(0).getShape())
			.setShape2(pieces.get(2).getShape());
		} else {
			result
			.setColor1(pieces.get(1).getColor())
			.setColor2(pieces.get(2).getColor())
			.setShape1(pieces.get(3).getShape())
			.setShape2(pieces.get(4).getShape());
		}
		return result;
	}

	private void broadcastMessageToPlayers(String gameId, SseDto message) {
		Set<Player> stale = new HashSet<>();
		this.games.get(gameId).getPlayers().stream()
		.filter(p->p.getEmitter()!=null)
		.forEach(p -> {
			try {
				p.getEmitter().send(SseEmitter.event().data(message, MediaType.APPLICATION_JSON));
			} catch (IOException e) {
				p.getEmitter().completeWithError(e);
				p.setEmitter(null);
				stale.add(p);
			}
		});
		if (stale.size() > 0) {
			stale.forEach(player -> {
				broadcastMessageToPlayers(gameId, 
						new DroppedStalePlayerSseDto().setPlayers(
								stale
								.stream()
								.map(p->p.getDto())
								.collect(Collectors.toList())));
			});
		}
	}

	public Boolean playerPicksPiece(String gameId, String playerId, FantascattiPiece piece) {
		this.broadcastMessageToPlayers(gameId, new FantascattiPlayerPicksPieceSseDto()
				.setPlayer(gamePlayers(gameId).get(playerId).getDto())
				.setPiece(piece)
				);
		return true;
	}
	
}
