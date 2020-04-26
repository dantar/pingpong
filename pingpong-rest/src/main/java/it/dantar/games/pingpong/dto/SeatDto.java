package it.dantar.games.pingpong.dto;

public class SeatDto {

	PlayerDto player;
	Boolean pending;
	Boolean open;
	
	public PlayerDto getPlayer() {
		return player;
	}
	public void setPlayer(PlayerDto player) {
		this.player = player;
	}
	public Boolean getPending() {
		return pending;
	}
	public void setPending(Boolean pending) {
		this.pending = pending;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}

}
