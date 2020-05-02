package it.dantar.games.pingpong.dto;

public class TablePlayerInvitationSseDto extends SseDto {

	public TablePlayerInvitationSseDto() {
		super();
		this.setCode("table-invitation");
	}

	TableDto table;
	PlayerDto player;

	public TableDto getTable() {
		return table;
	}
	public TablePlayerInvitationSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}
	public PlayerDto getPlayer() {
		return player;
	}
	public TablePlayerInvitationSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}

}
