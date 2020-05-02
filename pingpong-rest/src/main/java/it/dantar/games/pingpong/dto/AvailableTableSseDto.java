package it.dantar.games.pingpong.dto;

public class AvailableTableSseDto extends SseDto {

	public AvailableTableSseDto() {
		super();
		this.setCode("available-table");
	}

	TableDto table;
	
	public TableDto getTable() {
		return table;
	}
	public AvailableTableSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}

}
