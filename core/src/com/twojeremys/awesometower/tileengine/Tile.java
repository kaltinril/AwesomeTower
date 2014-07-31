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
	
	// The ID of the image coming from TileProperties.id
	private int ID;
	
	//If null then it IS the parent
	private Tile parentTile;
	
	//Holds the stats for the tile
	//Null if child object
	private TileStats tileStats;
	
	//Created to allow GameSave to load, otherwise got this bug
	//ERROR: Couldn't Load from External Storage [awesomeTowerSave.txt]: Class cannot be created (missing no-arg constructor): com.twojeremys.awesometower.tileengine.Tile
	public Tile(){
		this(-1);
	}
	
	public Tile(int iD){
		this(iD, null);
	}
	
	public Tile(int iD, Tile parentTile){
		this.ID = iD;
		this.parentTile = parentTile;
		this.tileStats = new TileStats(iD);
	}

	public int getID() {
		return ID;
	}

	public Tile getParentTile() {
		return parentTile;
	}

	public void setParentTile(Tile parentTile) {
		this.parentTile = parentTile;
	}
	
	public boolean hasParent() {
		return this.parentTile != null;
	}

	//Returns the stats for the tile
	//may come from a parent if called on a child
	public TileStats getTileStats() {
		if (hasParent()) {
			return this.parentTile.getTileStats();
		}
		return tileStats;
	}

	public void setTileStats(TileStats tileStats) {
		if (this.hasParent()) {
			this.parentTile.setTileStats(tileStats);
		} else {
			this.tileStats = tileStats;
		}
	}

	
	
}
