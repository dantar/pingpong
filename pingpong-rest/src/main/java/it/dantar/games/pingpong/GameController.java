package it.dantar.games.pingpong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class GameController {

	@Autowired
	PingpongService pingpongService;
	
	@GetMapping("/hello")
	public String joinGame() {
		pingpongService.broadcastMessage("hello!");
		return "ok";
	}

}
