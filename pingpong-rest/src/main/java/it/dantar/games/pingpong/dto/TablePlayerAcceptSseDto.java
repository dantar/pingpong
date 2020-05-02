package it.dantar.games.pingpong.dto;

public class TablePlayerAcceptSseDto extends SseDto {

	public TablePlayerAcceptSseDto() {
		super();
		this.setCode("table-accept");
	}

	TableDto table;
	PlayerDto player;
	Boolean accepted;

	public TableDto getTable() {
		return table;
	}
	public TablePlayerAcceptSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}
	public PlayerDto getPlayer() {
		return player;
	}
	public TablePlayerAcceptSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	public Boolean getAccepted() {
		return accepted;
	}
	public TablePlayerAcceptSseDto setAccepted(Boolean accepted) {
		this.accepted = accepted;
		return this;
	}

}
