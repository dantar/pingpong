package it.dantar.games.pingpong.dto;

public class TableDropDto extends SseDto {

	public TableDropDto() {
		super();
		this.setCode("table-drop");
	}

	TableDto table;

	public TableDto getTable() {
		return table;
	}
	public TableDropDto setTable(TableDto table) {
		this.table = table;
		return this;
	}

}
