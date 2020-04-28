package it.dantar.games.pingpong.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.dantar.games.pingpong.dto.FantascattiCardDto;

public class FantascattiGame {

	List<Player> players = new ArrayList<>();
	Set<String> ready = new HashSet<>();
	Set<String> picks = new HashSet<>();
	FantascattiCardDto guess;
	Map<String, Integer> score = new HashMap<>();
	String tableId;

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
	public FantascattiGame setScore(Map<String, Integer> score) {
		this.score = score;
		return this;
	}
	public String getTableId() {
		return tableId;
	}
	public FantascattiGame setTableId(String tableId) {
		this.tableId = tableId;
		return this;
	}
	public Set<String> getPicks() {
		return picks;
	}
	public void setPicks(Set<String> picks) {
		this.picks = picks;
	}
	
}
