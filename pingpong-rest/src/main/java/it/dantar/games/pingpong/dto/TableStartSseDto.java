package it.dantar.games.pingpong.dto;

public class TableStartSseDto extends SseDto {

	public TableStartSseDto() {
		super();
		this.setCode("table-start");
	}

	TableDto table;
	
	public TableDto getTable() {
		return table;
	}
	public TableStartSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}

}
