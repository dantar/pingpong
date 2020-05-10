package it.dantar.games.pingpong.dto;

public class FantascattiPlayerQuitSseDto extends SseDto {

	public FantascattiPlayerQuitSseDto() {
		super();
		this.setCode("player-quit");
	}

	PlayerDto player;
	TableDto table;

	public PlayerDto getPlayer() {
		return player;
	}
	public FantascattiPlayerQuitSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	public TableDto getTable() {
		return table;
	}
	public FantascattiPlayerQuitSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}

}
