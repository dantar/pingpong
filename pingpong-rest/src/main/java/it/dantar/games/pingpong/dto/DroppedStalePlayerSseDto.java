package it.dantar.games.pingpong.dto;

import java.util.List;

public class DroppedStalePlayerSseDto extends SseDto {

	List<PlayerDto> players;
	
	public DroppedStalePlayerSseDto() {
		super();
		this.setCode("stale-players");
	}

	public List<PlayerDto> getPlayers() {
		return players;
	}
	public DroppedStalePlayerSseDto setPlayers(List<PlayerDto> players) {
		this.players = players;
		return this;
	}

	
}
