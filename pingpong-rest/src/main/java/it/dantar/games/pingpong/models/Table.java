package it.dantar.games.pingpong.models;

import it.dantar.games.pingpong.dto.TableDto;

public class Table {

	TableDto dto;
	
	public TableDto getDto() {
		return dto;
	}
	public Table setDto(TableDto dto) {
		this.dto = dto;
		return this;
	}
	
}
