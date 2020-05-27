package it.dantar.games.pingpong.dto;

import java.util.Map;

import it.dantar.games.pingpong.models.FantascattiPiece;

public class FantascattiPlayerPicksPieceSseDto extends SseDto {

	public FantascattiPlayerPicksPieceSseDto() {
		super();
		this.setCode("pick-piece");
	}

	PlayerDto player;
	FantascattiPiece piece;
	Map<String, Integer> score;
	FantascattiCardDto guess;

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
	public Map<String, Integer> getScore() {
		return score;
	}
	public FantascattiPlayerPicksPieceSseDto setScore(Map<String, Integer> score) {
		this.score = score;
		return this;
	}
	public FantascattiCardDto getGuess() {
		return guess;
	}
	public FantascattiPlayerPicksPieceSseDto setGuess(FantascattiCardDto guess) {
		this.guess = guess;
		return this;
	}

}
