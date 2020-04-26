package it.dantar.games.pingpong;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.RegisterPlayerSseDto;
import it.dantar.games.pingpong.dto.SseDto;

@RestController
public class GameController {

	@Autowired
	PingpongService pingpongService;
	
	@GetMapping("/hello")
	public void joinGame() {
		pingpongService.broadcastMessage(new SseDto().setCode("hello"));
	}

	@PostMapping("/register")
	public PlayerDto register(@RequestBody PlayerDto player) {
		this.pingpongService.register(player);
		pingpongService.broadcastMessage(new RegisterPlayerSseDto().setPlayer(player));
		return player;
	}

	@GetMapping("/players")
	public List<PlayerDto> getPlayers() {
		return pingpongService.listPlayers();
	}


}
