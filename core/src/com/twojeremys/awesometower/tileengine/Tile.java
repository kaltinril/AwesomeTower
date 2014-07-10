package com.twojeremys.awesometower.tileengine;

/*
 *   Example of how this works
[ ] [ ] [R] [ ]
[ ] [Y] [Y] [ ]
[R] [ ] [ ] [ ]
[ ] [ ] [ ] [ ]
[G] [G] [ ] [ ]
[ ] [ ] [R] [ ]

G:Green (Child):
 ID: -2
 parentTile = G:Green (Parent)
 all other stats are null

G:Green (Parent):
 ID: 2
 parentTile = null
 Store all statistics*/

public class Tile {
	// A Negative value indicates a "child" tile
	private int ID;
	
	private Tile parentTile;
	
	//TODO: Add stats for tile (income, cost, health/dirt, noise, popularity, etc)
	
	public Tile(int iD){
		this.ID = iD;
		this.parentTile = null;
	}
	
	public Tile(int iD, Tile parentTile){
		this.ID = iD;
		this.parentTile = parentTile;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Tile getParentTile() {
		return parentTile;
	}

	public void setParentTile(Tile parentTile) {
		this.parentTile = parentTile;
	}

	
	
}
