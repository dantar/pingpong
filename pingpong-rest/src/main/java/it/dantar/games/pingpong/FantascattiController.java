package it.dantar.games.pingpong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.models.FantascattiPiece;

@RestController
public class FantascattiController {

	@Autowired
	FantascattiService fantascattiService;

	@GetMapping("/fantascatti/sse/{gameId}/{playerId}")
	public SseEmitter tableSse(@PathVariable String gameId, @PathVariable String playerId) {
		return fantascattiService.newPlayerSse(gameId, playerId);
	}

	@PostMapping("/fantascatti/{uuid}/ready")
	public PlayerDto postTable(@PathVariable String uuid, @RequestBody PlayerDto player) {
		fantascattiService.playerReady(uuid, player);
		return player;
	}

	@PostMapping("/fantascatti/{gameId}/{playerId}/pick")
	public FantascattiPiece pickPiece(@PathVariable String gameId, @PathVariable String playerId, @RequestBody FantascattiPiece piece) {
		fantascattiService.playerPicksPiece(gameId, playerId, piece);
		return piece;
	}

}
