package com.twojeremys.awesometower.gamefile;

import com.twojeremys.awesometower.tileengine.Tile;

//Create a class to contain references to only the data needed to save and restart games.

public class GameState {
	
	//NOTE: No updates should be made directly to GameState.Tiles, instead perform the 
	// action on the tileMap which references this.
	private Tile[][] tiles;
	
	//TODO ENHANCE add more information that needs to be retained
	private int gold;

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

	
	public int getGold() {
		return gold;
	}
	

	public boolean setGold(int gold) {
		//Limit gold so you can't go negative
		if (gold > 0){
			this.gold = gold;
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean takeGold(int takeAmount){
		return setGold(this.gold - takeAmount);
	}
	
	
}
