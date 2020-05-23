package it.dantar.games.pingpong.dto;

public class SeatDto {

	PlayerDto player;
	RobotDto robot;
	Boolean pending;
	Boolean open;
	
	public PlayerDto getPlayer() {
		return player;
	}
	public SeatDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	public Boolean getPending() {
		return pending;
	}
	public SeatDto setPending(Boolean pending) {
		this.pending = pending;
		return this;
	}
	public Boolean getOpen() {
		return open;
	}
	public SeatDto setOpen(Boolean open) {
		this.open = open;
		return this;
	}
	public RobotDto getRobot() {
		return robot;
	}
	public SeatDto setRobot(RobotDto robot) {
		this.robot = robot;
		return this;
	}

}
