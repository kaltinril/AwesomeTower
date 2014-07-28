package com.twojeremys.awesometower.gamefile;

import com.badlogic.gdx.utils.Array;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.Person;
import com.twojeremys.awesometower.tileengine.Tile;

//Create a class to contain references to only the data needed to save and restart games.

public class GameState {
	
	//NOTE: No updates should be made directly to GameState.Tiles, instead perform the 
	// action on the tileMap which references this.
	private Tile[][] tiles;
	
	//TODO ENHANCE add more information that needs to be retained
	private float gold;
	private int population;
	private int income;
	private int expense;
	private float elapsedSeconds;
	
	private Array<Person> people;

	public GameState(){
		this.gold = Constants.STARTING_GOLD;	//Starting gold
		this.people = new Array<Person>(false, 0);
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	public float getGold() {
		return gold;
	}
	
	public boolean setGold(float gold) {
		//Limit gold so you can't go negative
		if (gold > 0){
			this.gold = gold;
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean takeGold(float takeAmount){
		return setGold(this.gold - takeAmount);
	}

	public void giveGold(float giveAmount){
		this.setGold(this.getGold() + giveAmount);
	}
	
	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	public int getExpense() {
		return expense;
	}

	public void setExpense(int expense) {
		this.expense = expense;
	}

	public float getElapsedSeconds() {
		return elapsedSeconds;
	}

	public void setElapsedSeconds(float elapsedSeconds) {
		this.elapsedSeconds = elapsedSeconds;
	}
	
	public void addToElapsedSeconds(float elapsedSeconds){
		this.elapsedSeconds += elapsedSeconds;
	}
	
	
}
