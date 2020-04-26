package it.dantar.games.pingpong.dto;

public class RegisterPlayerSseDto extends SseDto {

	public RegisterPlayerSseDto() {
		super();
		this.setCode("register-player");
	}

	PlayerDto player;

	public PlayerDto getPlayer() {
		return player;
	}
	public RegisterPlayerSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	
	
}
