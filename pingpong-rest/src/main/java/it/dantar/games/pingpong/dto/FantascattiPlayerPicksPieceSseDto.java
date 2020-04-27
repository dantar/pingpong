package it.dantar.games.pingpong.dto;

import it.dantar.games.pingpong.models.FantascattiPiece;

public class FantascattiPlayerPicksPieceSseDto extends SseDto {

	public FantascattiPlayerPicksPieceSseDto() {
		super();
		this.setCode("pick-piece");
	}

	PlayerDto player;
	FantascattiPiece piece;

	public PlayerDto getPlayer() {
		return player;
	}
	public FantascattiPlayerPicksPieceSseDto setPlayer(PlayerDto player) {
		this.player = player;
		return this;
	}
	public FantascattiPiece getPiece() {
		return piece;
	}
	public FantascattiPlayerPicksPieceSseDto setPiece(FantascattiPiece piece) {
		this.piece = piece;
		return this;
	}

}
