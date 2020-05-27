package it.dantar.games.pingpong;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.dantar.games.pingpong.dto.FantascattiCardDto;
import it.dantar.games.pingpong.dto.FantascattiNewGuessSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerPicksPieceSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerQuitSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerReadySseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.models.FantascattiGame;
import it.dantar.games.pingpong.models.FantascattiPiece;

@Service
public class FantascattiService {

	@Autowired
	TablesService tablesService;
	
	Map<String, FantascattiGame> games = new HashMap<String, FantascattiGame>();
	
	static final float CHANCE_OF_TWIN = 0.25f;
	
	static final FantascattiPiece[] PIECES = {
			new FantascattiPiece().setColor("blue").setShape("sword"),
			new FantascattiPiece().setColor("green").setShape("shield"),
			new FantascattiPiece().setColor("red").setShape("dragon"),
			new FantascattiPiece().setColor("yellow").setShape("gold"),
			new FantascattiPiece().setColor("brown").setShape("chest")
	};
	
	static final long TIMEOUT = 1000*60*60*4L;
	
	public FantascattiGame newGame(TableDto table) {
		FantascattiGame game = new FantascattiGame()
				.setTableId(table.getUuid());
		this.games.put(game.getTableId(), game);
		return game;
	}
	
	public void playerReady(String gameId, PlayerDto player) {
		FantascattiGame game = this.games.get(gameId);
		game.setNoWait(
				this.tablesService.getTable(gameId).getSeats()
				.stream()
				.filter(s -> s.getRobot() != null && s.getRobot().getName().equals("Woren"))
				.count() > 0
		);
		game.getReady().add(player.getUuid());
		this.tablesService.broadcastMessageToTable(gameId, new FantascattiPlayerReadySseDto().setPlayer(player));			
		if (game.getReady().size() >= tablesService.getTable(gameId).getSeats()
				.stream()
				.filter(s -> s.getPlayer() != null)
				.count()+1) {
			this.nextTurn(gameId);
		}
	}

	private FantascattiCardDto randomGuess() {
		List<FantascattiPiece> pieces = Arrays.asList(PIECES);
		Collections.shuffle(pieces);
		FantascattiCardDto result = new FantascattiCardDto()
				.setCorrect(pieces.get(0).getShape());
		if (Math.random() < CHANCE_OF_TWIN) {
			if (pieces.get(0).getShape().compareTo(pieces.get(1).getShape()) < 0) {				
				result
				.setColor1(pieces.get(0).getColor())
				.setColor2(pieces.get(2).getColor())
				.setShape1(pieces.get(0).getShape())
				.setShape2(pieces.get(1).getShape());
			} else {
				result
				.setColor1(pieces.get(2).getColor())
				.setColor2(pieces.get(0).getColor())
				.setShape1(pieces.get(1).getShape())
				.setShape2(pieces.get(0).getShape());
			}
		} else {
			result
			.setColor1(pieces.get(1).getColor())
			.setColor2(pieces.get(2).getColor())
			.setShape1(pieces.get(3).getShape())
			.setShape2(pieces.get(4).getShape());
		}
		return result;
	}

	public Boolean playerPicksPiece(String gameId, String playerId, FantascattiPiece piece) {
		FantascattiGame game = this.games.get(gameId);
		boolean correct = piece.getShape().equals(game.getGuess().getCorrect());
		if (correct) {
			Logger.getLogger(this.getClass().getSimpleName()).info("Score for " + playerId + ": " + game.getScore().get(playerId));
			game.getScore().put(playerId, game.getScore().get(playerId) == null ? 1 : game.getScore().get(playerId) +1);
			Logger.getLogger(this.getClass().getSimpleName()).info("now goes to " + game.getScore().get(playerId));
		}
		FantascattiPlayerPicksPieceSseDto event = new FantascattiPlayerPicksPieceSseDto()
				.setPlayer(tablesService.getPlayer(playerId))
				.setPiece(piece)
				;
		game.getPicks().add(playerId);
		if (correct || game.getPicks().size() >= this.tablesService.getTable(gameId).getSeats().size()+1) {
			game.getPicks().clear();
			game.getReady().clear();
			event.setScore(game.getScore());
			if (game.getNoWait()) {				
				game.setGuess(randomGuess());
				event.setGuess(game.getGuess())
				;
			}
		}
		this.tablesService.broadcastMessageToTable(game.getTableId(), event);
		return true;
	}

	private void nextTurn(String gameId) {
		FantascattiGame game = this.games.get(gameId);
		game.getPicks().clear();
		game.getReady().clear();
		game.setGuess(randomGuess());
		tablesService.broadcastMessageToTable(gameId, new FantascattiNewGuessSseDto().setGuess(game.getGuess()));			
	}

	public void playerQuit(String uuid, PlayerDto player) {
		FantascattiGame game = this.games.get(uuid);
		TableDto table = this.tablesService.getTable(game.getTableId());
		if (table.getOwner().getUuid().equals(player.getUuid())) {
			// remove game altogether
			this.games.remove(uuid);
			this.tablesService.removeTable(table);
		} else {
			// update game
			table.setSeats(table.getSeats().stream()
					.filter(s-> s.getPlayer()== null || !s.getPlayer().getUuid().equals(player.getUuid()))
					.collect(Collectors.toList())
					);
			_removePlayerFromSet(player.getUuid(), game.getPicks());
			_removePlayerFromSet(player.getUuid(), game.getReady());
			if (game.getScore().containsKey(player.getUuid())) game.getScore().remove(player.getUuid());
		}
		tablesService.broadcastMessage(new FantascattiPlayerQuitSseDto()
				.setPlayer(player)
				.setTable(table)
				);
	}

	private void _removePlayerFromSet(String uuid, Collection<String> uuids) {
		if (uuid.contains(uuid)) uuids.remove(uuid);
	}
	
}
