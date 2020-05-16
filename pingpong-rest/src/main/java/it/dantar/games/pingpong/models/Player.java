package it.dantar.games.pingpong.models;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.PlayerDto;

public class Player {

	PlayerDto dto;
	SseEmitter emitter;
	Boolean ack = false;
	
	public PlayerDto getDto() {
		return dto;
	}
	public Player setDto(PlayerDto dto) {
		this.dto = dto;
		return this;
	}
	public SseEmitter getEmitter() {
		return emitter;
	}
	public Player setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
		return this;
	}
	public Boolean getAck() {
		return ack;
	}
	public Player setAck(Boolean ack) {
		this.ack = ack;
		return this;
	}
	
}
