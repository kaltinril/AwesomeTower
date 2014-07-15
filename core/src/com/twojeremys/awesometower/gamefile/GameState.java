package com.twojeremys.awesometower.gamefile;

import com.twojeremys.awesometower.tileengine.Tile;

//Create a class to contain references to only the data needed to save and restart games.

public class GameState {
	
	//NOTE: No updates should be made directly to GameState.Tiles, instead perform the 
	// action on the tileMap which references this.
	private Tile[][] tiles;
	
	//TODO ENHANCE add more information that needs to be retained

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	
}
