package it.dantar.games.pingpong.dto;

public class RegisterPlayerSseDto extends SseDto {

	public RegisterPlayerSseDto() {
		super();
		this.setCode("register-player");
	}

	PlayerDto player;
	TableDto table;

	public PlayerDto getPlayer() {
		return player;
	}
	public RegisterPlayerSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	public TableDto getTable() {
		return table;
	}
	public RegisterPlayerSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}
	
}
