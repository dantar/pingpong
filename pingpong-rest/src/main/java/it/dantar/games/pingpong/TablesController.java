package it.dantar.games.pingpong;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.AvailableTableSseDto;
import it.dantar.games.pingpong.dto.PlayerDto;
import it.dantar.games.pingpong.dto.RegisterPlayerSseDto;
import it.dantar.games.pingpong.dto.SeatDto;
import it.dantar.games.pingpong.dto.SseDto;
import it.dantar.games.pingpong.dto.TableDto;
import it.dantar.games.pingpong.dto.TablePlayerAcceptSseDto;
import it.dantar.games.pingpong.dto.TablePlayerInvitationSseDto;

@RestController
public class TablesController {

	@Autowired
	TablesService pingpongService;

	@GetMapping("/sse/{uuid}")
	public SseEmitter playerSse(@PathVariable String uuid) {
		return pingpongService.newPlayerSse(uuid);
	}

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

	@PostMapping("/table")
	public TableDto postTable(@RequestBody TableDto table) {
		this.pingpongService.newTable(table);
		pingpongService.broadcastMessageToTable(table, new AvailableTableSseDto().setTable(table));
		return table;
	}

	@GetMapping("/table/{uuid}")
	public TableDto postTable(@PathVariable String uuid) {
		return this.pingpongService.getTable(uuid);
	}

	@PostMapping("/table/{gameId}/invite")
	public TableDto postTablePlayerInvitation(@PathVariable String gameId, @RequestBody PlayerDto player) {
		TableDto table = this.pingpongService.getTable(gameId).addSeat(new SeatDto()
				.setOpen(false)
				.setPending(true)
				.setPlayer(player)
				);
		pingpongService.broadcastMessageToTable(table, new TablePlayerInvitationSseDto()
				.setTable(table)
				.setPlayer(player));
		return table;
	}

	@PostMapping("/table/{gameId}/accept")
	public TableDto postTablePlayerAccept(@PathVariable String gameId, @RequestBody PlayerDto player) {
		TableDto table = this.pingpongService.getTable(gameId);
		table.getSeats().stream()
		.filter(s -> s.getPlayer()!=null)
		.collect(Collectors.toMap(s->s.getPlayer().getUuid(), Function.identity()))
		.get(player.getUuid())
		.setPending(false);
		pingpongService.broadcastMessageToTable(table, new TablePlayerAcceptSseDto()
				.setTable(table)
				.setPlayer(player)
				.setAccepted(true));
		return table;
	}

	@PostMapping("/table/{gameId}/reject")
	public TableDto postTablePlayerReject(@PathVariable String gameId, @RequestBody PlayerDto player) {
		TableDto table = this.pingpongService.getTable(gameId);
		SeatDto seat = table.getSeats().stream()
		.filter(s -> s.getPlayer()!=null)
		.collect(Collectors.toMap(s->s.getPlayer().getUuid(), Function.identity()))
		.get(player.getUuid());
		table.getSeats().remove(seat);
		pingpongService.broadcastMessageToTable(table, new TablePlayerAcceptSseDto()
				.setTable(table)
				.setPlayer(player)
				.setAccepted(false));
		return table;
	}

	@GetMapping("/players")
	public List<PlayerDto> getPlayers() {
		return pingpongService.listPlayers();
	}

	@GetMapping("/tables")
	public List<TableDto> getTables() {
		return pingpongService.listTables();
	}

}
