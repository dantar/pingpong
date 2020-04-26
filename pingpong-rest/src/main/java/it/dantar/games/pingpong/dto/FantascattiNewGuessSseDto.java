package it.dantar.games.pingpong.dto;

public class FantascattiNewGuessSseDto extends SseDto {

	public FantascattiNewGuessSseDto() {
		super();
		this.setCode("new-guess");
	}

	FantascattiCardDto guess;

	public FantascattiCardDto getGuess() {
		return guess;
	}
	public FantascattiNewGuessSseDto setGuess(FantascattiCardDto guess) {
		this.guess = guess;
		return this;
	}

}
