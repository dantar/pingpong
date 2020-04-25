package it.dantar.games.pingpong;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@CrossOrigin
public class SseController {
	
    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    List<SseEmitter> players = new ArrayList<>();
    
    @Autowired
    PingpongService pingpongService;
    
	@GetMapping("/sse")
	public SseEmitter handleSse() {
		return pingpongService.newPlayerSse();
	}
	
}

		