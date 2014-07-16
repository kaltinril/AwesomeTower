package com.twojeremys.awesometower.tileengine;

import com.twojeremys.awesometower.Category;


//This class creates a way to store extra information about tiles
//NOTE: all KEYs in the file must exist in the class.
//NOTE: Adding new variables to the class does not break existing files (Backwards compatible), just stores NULL.

public class TileProperties {
	
	private int ID;     				//The Numeric lookup value for this tile
	private String atlasName; 			//Name from the Atlas file
	private int tileSpanX;  			//The number of horizontal tiles the image will span
	private int tileSpanY;  			//The number of vertical tiles the image will span
	private boolean blockable; 			//Can this type of tile block placement of other tiles?
	private Category category; 			//The categoryName this tile should be placed in
	private String displayName;			//The name shown in the UI
	
	//TODO ENHANCEMENT Perhaps add a AtlasRegion to this, and store the Atlas details for the tile in the properties.  Less lookup needed at that point

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public String getAtlasName() {
		return atlasName;
	}

	public void setAtlasName(String atlasName) {
		this.atlasName = atlasName;
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
