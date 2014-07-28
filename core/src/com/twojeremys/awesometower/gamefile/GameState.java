package com.twojeremys.awesometower.gamefile;

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
	private float elapsedTime;

	public GameState(){
		//Starting gold
		this.gold = 5000;
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

	public float getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	public void addToElapsedTime(float elapsedTime){
		this.elapsedTime += elapsedTime;
	}
	
	
}
