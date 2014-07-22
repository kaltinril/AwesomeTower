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
	private float purchaseCost;			//The cost to purchase the item
	private int maxEmployees;			//maximum number of employees
	private int maxPatrons;				//maximum number of simultaneous patrons
	private int maxResidents;			//maximum number of residents
	private float noiseFactor;			//noise generated by tile
	private float noiseTolerance;		//tolerance of noise from surrounding tiles
	private float incomeAmount;			//income received per game cycle
	private float saleAmount;			//income received if sold; once sold it cannot be resold/closed/etc this is not the real estate market game.

	//Placement restriction information
	
	//Number of tiles that need to be below this (horizontally)
	// set to tileSpanX value to require all tiles below to be filled
	private int requiredTilesBelow;
	
	//To allow restriction of placement to these floors
	// If null allows all floors
	private int[] whiteListFloors;
	private int[] blackListFloors;
	
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

	public float getPurchaseCost() {
		return purchaseCost;
	}

	public void setPurchaseCost(int purchaseCost) {
		this.purchaseCost = purchaseCost;
	}

	
	public int[] getWhiteListFloors() {
		return whiteListFloors;
	}

	public void setWhiteListFloors(int[] whiteListFloors) {
		this.whiteListFloors = whiteListFloors;
	}
	
	public int[] getBlackListFloors() {
		return blackListFloors;
	}

	public void setBlackListFloors(int[] blackListFloors) {
		this.blackListFloors = blackListFloors;
	}

	public int getRequiredTilesBelow() {
		return requiredTilesBelow;
	}

	public void setRequiredTilesBelow(int requiredTilesBelow) {
		this.requiredTilesBelow = requiredTilesBelow;
	}

	public int getMaxEmployees() {
		return maxEmployees;
	}

	public void setMaxEmployees(int maxEmployees) {
		this.maxEmployees = maxEmployees;
	}

	public int getMaxPatrons() {
		return maxPatrons;
	}

	public void setMaxPatrons(int maxPatrons) {
		this.maxPatrons = maxPatrons;
	}

	public int getMaxResidents() {
		return maxResidents;
	}

	public void setMaxResidents(int maxResidents) {
		this.maxResidents = maxResidents;
	}

	public float getNoiseFactor() {
		return noiseFactor;
	}

	public void setNoiseFactor(float noiseFactor) {
		this.noiseFactor = noiseFactor;
	}

	public float getNoiseTolerance() {
		return noiseTolerance;
	}

	public void setNoiseTolerance(float noiseTolerance) {
		this.noiseTolerance = noiseTolerance;
	}

	public float getIncomeAmount() {
		return incomeAmount;
	}

	public void setIncomeAmount(float incomeAmount) {
		this.incomeAmount = incomeAmount;
	}

	public float getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(float saleAmount) {
		this.saleAmount = saleAmount;
	}

	public void setPurchaseCost(float purchaseCost) {
		this.purchaseCost = purchaseCost;
	}
	
}
