package it.dantar.games.pingpong;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class SseController {
	
    List<SseEmitter> players = new ArrayList<>();
    
    @Autowired
    PingpongService pingpongService;
    
	@GetMapping("/sse/{uuid}")
	public SseEmitter playerSse(@PathVariable String uuid) {
		return pingpongService.newPlayerSse(uuid);
	}

	@GetMapping("/table/sse/{uuid}")
	public SseEmitter tableSse(@PathVariable String uuid) {
		return pingpongService.newTableSse(uuid);
	}

}

		