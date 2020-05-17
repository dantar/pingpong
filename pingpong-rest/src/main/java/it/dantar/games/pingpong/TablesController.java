package it.dantar.games.pingpong;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.AvailableTableSseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.SituationDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.dto.TableStartSseDto;

@RestController
public class TablesController {

	@Autowired
	TablesService pingpongService;

	@PostMapping("/register")
	public PlayerDto register(@RequestBody PlayerDto player) {
		Logger.getLogger(this.getClass().getName()).info(String.format("Register player %s %s", player.getName(), player.getUuid()));
		this.pingpongService.register(player);
		return player;
	}

	@GetMapping("/sse/request/{uuid}/{nocache}")
	public SseEmitter playerSseRequest(@PathVariable String uuid, @PathVariable String nocache) {
		Logger.getLogger(this.getClass().getName()).info(String.format("New SSE for player %s", uuid));
		return pingpongService.requestPlayerSse(uuid);
	}

	@PostMapping("/sse/ack")
	public PlayerDto confirmSse(@RequestBody PlayerDto player) {
		Logger.getLogger(this.getClass().getName()).info(String.format("Ack SSE for player %s %s", player.getName(), player.getUuid()));
		pingpongService.ackPlayerSse(player);
		return player;
	}
	
	@GetMapping("/hello")
	public void joinGame() {
		pingpongService.broadcastMessage(new SseDto().setCode("hello"));
	}

	@PostMapping("/table")
	public TableDto postTable(@RequestBody TableDto table) {
		this.pingpongService.newTable(table);
		pingpongService.broadcastMessage(new AvailableTableSseDto().setTable(table));
		return table;
	}

	@GetMapping("/table/{uuid}")
	public TableDto postTable(@PathVariable String uuid) {
		return this.pingpongService.getTable(uuid);
	}

	@PostMapping("/table/{gameId}/drop")
	public TableDto postTableDrop(@PathVariable String gameId) {
		return this.pingpongService.tableDrop(gameId);
	}

	@PostMapping("/table/{gameId}/invite")
	public TableDto postTablePlayerInvitation(@PathVariable String gameId, @RequestBody PlayerDto player) {
		return this.pingpongService.tablePlayerInvitation(gameId, player);
	}

	@PostMapping("/table/{gameId}/accept")
	public TableDto postTablePlayerAccept(@PathVariable String gameId, @RequestBody PlayerDto player) {
		return pingpongService.tablePlayerAccept(gameId, player);
	}

	@PostMapping("/table/{gameId}/reject")
	public TableDto postTablePlayerReject(@PathVariable String gameId, @RequestBody PlayerDto player) {
		return this.pingpongService.tablePlayerReject(gameId, player);
	}

	@GetMapping("/situation")
	public SituationDto getSituation() {
		return new SituationDto()
				.setPlayers(pingpongService.listPlayers())
				.setTables(pingpongService.listAllTables())
				;
	}

	@PostMapping("/table/{tableId}/start")
	public TableDto startTable(@PathVariable String tableId) {
		TableDto table = pingpongService.getTable(tableId);
		pingpongService.broadcastMessageToTable(table, new TableStartSseDto().setTable(table));
		return table;
	}

}
