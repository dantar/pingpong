package it.dantar.games.pingpong.dto;

public class TableUpdateSseDto extends SseDto {

	public TableUpdateSseDto() {
		super();
		this.setCode("table-update");
	}

	TableDto table;

	public TableDto getTable() {
		return table;
	}
	public TableUpdateSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}

}
