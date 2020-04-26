package it.dantar.games.pingpong.dto;

import java.util.ArrayList;
import java.util.List;

public class TableDto {

	PlayerDto owner;
	List<SeatDto> seats = new ArrayList<>();
	String uuid;

	public PlayerDto getOwner() {
		return owner;
	}
	public void setOwner(PlayerDto owner) {
		this.owner = owner;
	}
	public String getUuid() {
		return uuid;
	}
	public TableDto setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	public List<SeatDto> getSeats() {
		return seats;
	}
	public TableDto setSeats(List<SeatDto> seats) {
		this.seats = seats;
		return this;
	}
	
}
