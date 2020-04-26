package it.dantar.games.pingpong.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.dantar.games.pingpong.dto.FantascattiCardDto;

public class FantascattiGame {

	List<Player> players;
	Set<String> ready = new HashSet<>();
	FantascattiCardDto guess;
	Map<String, Integer> score = new HashMap<>();

	public List<Player> getPlayers() {
		return players;
	}
	public FantascattiGame setPlayers(List<Player> players) {
		this.players = players;
		return this;
	}
	public FantascattiCardDto getGuess() {
		return guess;
	}
	public FantascattiGame setGuess(FantascattiCardDto guess) {
		this.guess = guess;
		return this;
	}
	public Set<String> getReady() {
		return ready;
	}
	public FantascattiGame setReady(Set<String> ready) {
		this.ready = ready;
		return this;
	}
	public Map<String, Integer> getScore() {
		return score;
	}
	public void setScore(Map<String, Integer> score) {
		this.score = score;
	}
	
}
