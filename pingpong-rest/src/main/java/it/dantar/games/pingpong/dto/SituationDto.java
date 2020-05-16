package it.dantar.games.pingpong.dto;

import java.util.List;

public class SituationDto {

	List<PlayerDto> players;
	List<TableDto> tables;
	
	public List<PlayerDto> getPlayers() {
		return players;
	}
	public SituationDto setPlayers(List<PlayerDto> players) {
		this.players = players;
		return this;
	}
	public List<TableDto> getTables() {
		return tables;
	}
	public SituationDto setTables(List<TableDto> tables) {
		this.tables = tables;
		return this;
	}
	
}
