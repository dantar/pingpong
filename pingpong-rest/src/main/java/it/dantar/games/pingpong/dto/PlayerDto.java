package it.dantar.games.pingpong.dto;

public class PlayerDto {

	String name;
	String uuid;

	public String getName() {
		return name;
	}
	public PlayerDto setName(String name) {
		this.name = name;
		return this;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
