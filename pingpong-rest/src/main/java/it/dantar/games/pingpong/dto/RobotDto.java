package it.dantar.games.pingpong.dto;

public class RobotDto {

	String name;
	String uuid;
	AvatarDto avatar;

	public String getName() {
		return name;
	}
	public RobotDto setName(String name) {
		this.name = name;
		return this;
	}
	public String getUuid() {
		return uuid;
	}
	public RobotDto setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	public AvatarDto getAvatar() {
		return avatar;
	}
	public RobotDto setAvatar(AvatarDto avatar) {
		this.avatar = avatar;
		return this;
	}
	
}
