package it.dantar.games.pingpong.dto;

public class FantascattiPlayerReadySseDto extends SseDto {

	public FantascattiPlayerReadySseDto() {
		super();
		this.setCode("player-ready");
	}

	PlayerDto player;

	public PlayerDto getPlayer() {
		return player;
	}
	public FantascattiPlayerReadySseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}

}
