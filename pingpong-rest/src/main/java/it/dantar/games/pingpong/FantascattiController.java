package it.dantar.games.pingpong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.dantar.games.pingpong.dto.PlayerDto;

@RestController
public class FantascattiController {

	@Autowired
	FantascattiService fantascattiService;
	
	@PostMapping("/fantascatti/{uuid}/ready")
	public PlayerDto postTable(@PathVariable String uuid, @RequestBody PlayerDto player) {
		fantascattiService.playerReady(uuid, player);
		return player;
	}

}
