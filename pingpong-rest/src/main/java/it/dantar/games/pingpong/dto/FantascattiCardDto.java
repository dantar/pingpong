package it.dantar.games.pingpong.dto;

public class FantascattiCardDto {

	String correctShape;
	String shape1;
	String shape2;
	String color1;
	String color2;
	
	public String getCorrect() {
		return correctShape;
	}
	public FantascattiCardDto setCorrect(String correct) {
		this.correctShape = correct;
		return this;
	}
	public String getShape1() {
		return shape1;
	}
	public FantascattiCardDto setShape1(String shape1) {
		this.shape1 = shape1;
		return this;
	}
	public String getShape2() {
		return shape2;
	}
	public FantascattiCardDto setShape2(String shape2) {
		this.shape2 = shape2;
		return this;
	}
	public String getColor1() {
		return color1;
	}
	public FantascattiCardDto setColor1(String color1) {
		this.color1 = color1;
		return this;
	}
	public String getColor2() {
		return color2;
	}
	public FantascattiCardDto setColor2(String color2) {
		this.color2 = color2;
		return this;
	}

}
