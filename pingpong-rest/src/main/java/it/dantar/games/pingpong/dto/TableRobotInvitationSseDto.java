package it.dantar.games.pingpong.dto;

public class TableRobotInvitationSseDto extends SseDto {

	public TableRobotInvitationSseDto() {
		super();
		this.setCode("table-robot");
	}

	TableDto table;
	RobotDto robot;

	public TableDto getTable() {
		return table;
	}
	public TableRobotInvitationSseDto setTable(TableDto table) {
		this.table = table;
		return this;
	}
	public RobotDto getRobot() {
		return robot;
	}
	public TableRobotInvitationSseDto setRobot(RobotDto robot) {
		this.robot = robot;
		return this;
	}

}
