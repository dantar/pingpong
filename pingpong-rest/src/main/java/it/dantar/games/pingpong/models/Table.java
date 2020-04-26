package it.dantar.games.pingpong.models;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import it.dantar.games.pingpong.dto.TableDto;

public class Table {

	TableDto dto;
	SseEmitter emitter;
	
	public TableDto getDto() {
		return dto;
	}
	public Table setDto(TableDto dto) {
		this.dto = dto;
		return this;
	}
	public SseEmitter getEmitter() {
		return emitter;
	}
	public Table setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
		return this;
	}
	
}
