package it.dantar.games.pingpong;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.dantar.games.pingpong.dto.FantascattiCardDto;
import it.dantar.games.pingpong.dto.FantascattiNewGuessSseDto;
import it.dantar.games.pingpong.dto.FantascattiPlayerPicksPieceSseDto;
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
		game.getReady().add(player.getUuid());
		this.tablesService.broadcastMessageToTable(gameId, new FantascattiPlayerReadySseDto().setPlayer(player));			
		if (game.getReady().size() >= tablesService.getTable(gameId).getSeats().size()+1) {
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
			game.getScore().put(playerId, game.getScore().get(playerId) == null ? 1 : game.getScore().get(playerId) +1);
		}
		this.tablesService.broadcastMessageToTable(game.getTableId(), new FantascattiPlayerPicksPieceSseDto()
				.setPlayer(tablesService.getPlayer(playerId))
				.setPiece(piece)
				.setScore(correct ? game.getScore() : null)
				);
		game.getPicks().add(playerId);
		if (game.getPicks().size() >= this.tablesService.getTable(gameId).getSeats().size()+1) {
			game.getPicks().clear();
			game.getReady().clear();
		}
		return true;
	}

	private void nextTurn(String gameId) {
		FantascattiGame game = this.games.get(gameId);
		game.getPicks().clear();
		game.getReady().clear();
		game.setGuess(randomGuess());
		tablesService.broadcastMessageToTable(gameId, new FantascattiNewGuessSseDto().setGuess(game.getGuess()));			
	}
	
}
