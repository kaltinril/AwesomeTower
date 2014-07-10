package com.twojeremys.awesometower.tileengine;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.twojeremys.awesometower.Constants;

//TODO TASK Save game state etc
public class TileMap {

	private Tile[][] tiles;
	
	//Holds a map of all the tile properties we use; loads from file in GameScreen.java
	private HashMap<Integer, TileProperties> tileProperties;
	
	//The texture atlas, duh!
	private TextureAtlas atlas;
	
	// Width and height of our tile map (entire screen)
	private int maxX;
	private int maxY;
	
	// Width and height of the individual tiles
	private int tileWidth = Constants.TILE_WIDTH;
	private int tileHeight = Constants.TILE_HEIGHT;

	public TileMap(int inMaxX, int inMaxY, TextureAtlas atlas) {
		this.maxX = inMaxX;
		this.maxY = inMaxY;
		this.tiles = new Tile[maxX][maxY];
		this.atlas = atlas;
		
		//set the hash to new object (will be overridden later)
		this.tileProperties = new HashMap<Integer, TileProperties>();
		
		//TODO DEBUG Remove when done testing screen fills
		if (Constants.DEBUG) {
			
			//make the corners red
			this.tiles[0][0] = new Tile(1);
			this.tiles[maxX-1][maxY-1] = new Tile(1);
			this.tiles[maxX-1][0] = new Tile(1);
			this.tiles[0][maxY-1] = new Tile(1);
		}
	}
	
	//This returns the room clicked on, regardless of which PART of that room is clicked on
	// Meaning if a tile is a "Child" return the parent instead.
	// If the tile is empty, return null
	public Tile getTile(int tileX, int tileY){
		Tile possibleChildTile = tiles[tileX][tileY];
		
		if (possibleChildTile != null) {
			
			if (possibleChildTile.hasParent()){
				return possibleChildTile.getParentTile();
			} else {
				return possibleChildTile; //This means that it IS the parent
			}
			
		} else {
			return null;
		}
	}

	public void setTile(int tileX, int tileY, int tile){
		
		//Make sure there are no collisions before we place the tile
		//if there is then do nothing 
		//TODO TASK we should pop toast to inform the user of collision
		if (!hasCollision(tileX, tileY, tile)) {
			
			//Get tile properties so we know size of the tile we're working with
			TileProperties tp = tileProperties.get(String.valueOf(tile));
			
			Tile parentTile = new Tile(tile);
			
			//Set the child tiles to reference the parent for collision detection
			for (int spanX=0; spanX < tp.getTileSpanX(); spanX++)
				for (int spanY=0; spanY < tp.getTileSpanY(); spanY++)
					this.tiles[tileX+spanX][tileY+spanY] = new Tile(tile, parentTile);
			
			//Set the Initial tile spot to the correct value
			this.tiles[tileX][tileY] = parentTile;
			
		}
		else{
			System.out.println("Outside or Collision " + tileX + " " + tileY);
		}
	}
	
	/*
	 * Get and Set methods for variables
	 */
	public int getTileWidth(){
		return this.tileWidth;
	}
	
	public int getTileHeight(){
		return this.tileHeight;
	}
	
	public int getMapWidth(){
		return this.maxX;
	}
	
	public int getMapHeight(){
		return this.maxY;
	}
	
	public int getMapPixelWidth(){
		return this.maxX*this.tileWidth;
	}

	public int getMapPixelHeight(){
		return this.maxY*this.tileHeight;
	}
	
	
	public HashMap<Integer, TileProperties> getTileProperties() {
		return tileProperties;
	}

	public void setTileProperties(HashMap<Integer, TileProperties> tileProperties) {
		this.tileProperties = tileProperties;
	}
	
	public boolean hasCollision(int tileX, int tileY, int tile) {
		
		//If the point is outside the bounds of the map return true
		if (tileX < 0 || tileX >= maxX || tileY < 0 || tileY >= maxY) {
			return true;
		}
		
		//Get tile properties so we know size of the tile we're working with
		TileProperties tp = tileProperties.get(String.valueOf(tile));
		
		//if the tile will end up outside the bounds of the map return true
		if (tileX + tp.getTileSpanX() > maxX || tileY + tp.getTileSpanY() > maxY) {
			return true;
		}
		
		//Check for other items in the cell
		for (int spanX = 0; spanX < tp.getTileSpanX(); spanX++){
			for (int spanY = 0; spanY < tp.getTileSpanY(); spanY++){
				if (tiles[tileX+spanX][tileY+spanY] != null){
					return true;
				}
			}
		}
		
		return false;
	}

	public void drawMap(SpriteBatch batch) {
		//TODO ENHANCEMENT Use Camera.unproject with (0,0), (0,screenheight), (screenwidth,0), and (screenwidth,screenheight)
		// This way we only draw tiles that will be partially or fully visible within the camera
		for (int x = 0; x < maxX ; x++){
			for (int y = 0; y < maxY; y++) {
				if (tiles[x][y] != null && !tiles[x][y].hasParent()){
					//For some reason using an Integer or int does not work.  have to cast it to string to find the match
					// It makes no sense to me given that the hashmap key is set to be an Integer.
					if (tileProperties.containsKey(String.valueOf(tiles[x][y].getID()))) {
						
						//Get a reference to the tile property to pull out more information from it
						TileProperties tp = tileProperties.get(String.valueOf(tiles[x][y].getID()));
						
						//Draw this tile to the designated width and height based on tilespan and tilewidth/height
						batch.draw(atlas.findRegion(tp.getName()), 
							x*tileWidth, y*tileHeight, //Position
							0, 0, //Origin Offset
							tp.getTileSpanX() * tileWidth, tp.getTileSpanY() * tileHeight, //Width and Height to stretch to
							1, 1,   //Scale, Could use this for "floating" affect
							0		//rotation
						);
					}
				}
			}
		}
	}

}
