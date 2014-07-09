package com.twojeremys.awesometower.tileengine;


//This class creates a way to store extra information about tiles
//NOTE: all KEYs in the file must exist in the class.
//NOTE: Adding new variables to the class does not break existing files (Backwards compatible), just stores NULL.

public class TileProperties {
	
	 private int ID;     		//The Numeric lookup value for this tile
	 private String name; 		//Name from the Atlas file
	 private int tileSpanX;  	//The number of horizontal tiles the image will span
	 private int tileSpanY;  	//The number of vertical tiles the image will span
	 private boolean blockable; //Can this type of tile block placement of other tiles?
	 
	
	public TileProperties(){
		
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTileSpanX() {
		return tileSpanX;
	}

	public void setTileSpanX(int tileSpanX) {
		this.tileSpanX = tileSpanX;
	}

	public int getTileSpanY() {
		return tileSpanY;
	}

	public void setTileSpanY(int tileSpanY) {
		this.tileSpanY = tileSpanY;
	}

	public boolean isBlockable() {
		return blockable;
	}

	public void setBlockable(boolean blockable) {
		this.blockable = blockable;
	}
	
}
