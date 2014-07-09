package com.twojeremys.awesometower.tileengine;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.twojeremys.awesometower.Constants;

public class TileMap {

	private int[][] tiles;
	
	//Holds a map of all the tile properties we use; loads from file in GameScreen.java
	private HashMap<Integer, TileProperties> tileProperties;
	
	//The texture atlas, duh!
	private TextureAtlas atlas;
	
	// Width and height of our tile map (entire screen)
	private int maxX;
	private int maxY;
	
	// Width and height of the individual tiles
	private int tileWidth = 20;
	private int tileHeight = 20;

	public TileMap(int inMaxX, int inMaxY, TextureAtlas atlas) {
		this.maxX = inMaxX;
		this.maxY = inMaxY;
		this.tiles = new int[maxX][maxY];
		this.atlas = atlas;
		
		//set the hash to new object (will be overridden later)
		this.tileProperties = new HashMap<Integer, TileProperties>();
		
		//Clear tiles
		for (int x = 0; x < maxX - 1; x++){
			for (int y = 0; y < maxY - 1; y++){
				this.tiles[x][y] = 0;
			}
		}
		
		//TODO: Remove when done testing screen fills
		if (Constants.DEBUG) {
			for (int xy = 0; xy < maxX - 1; xy++)
				this.tiles[xy][xy] = 2;
			
			//make the corners red
			this.tiles[0][0] = 1;
			this.tiles[maxX-1][maxY-1] = 1;
			this.tiles[maxX-1][0] = 1;
			this.tiles[0][maxY-1] = 1;
		}
	}
	
	public void setTile(int tileX, int tileY, int tile){
		
		//Make sure the tile placement is on the screen, otherwise ignore it.
		if (tileX >= 0 && tileX <= (maxX-1) &&	tileY >= 0 && tileY <= (maxY-1))
			this.tiles[tileX][tileY] = tile;
		else
			System.out.println("Outside " + tileX + " " + tileY);
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

	
	
	/*
	 * need a way to mark tile as "used"
	 - Could use negative value of the same ID
	 - Convert int[][] into Tile[][], where tile contains properties unique to each placeable object (How many square it takes up, etc)
	 - Create TileProperties class and TileProperties[][], that indicates at each position, if it has a parent tile (X,y) and information about that tile.
	 */

	public void drawMap(SpriteBatch batch) {
		//TODO: Use Camera.unproject with (0,0), (0,screenheight), (screenwidth,0), and (screenwidth,screenheight)
		// This way we only draw tiles that will be partially or fully visible within the camera
		for (int x = 0; x < maxX ; x++){
			for (int y = 0; y < maxY; y++) {
				if (tiles[x][y] != 0){
					//For some reason using an Integer or int does not work.  have to cast it to string to find the match
					// It makes no sense to me given that the hashmap key is set to be an Integer.
					if (tileProperties.containsKey(String.valueOf(tiles[x][y]))) {
						batch.draw(atlas.findRegion(tileProperties.get(String.valueOf(tiles[x][y])).getName()), x*tileWidth, y*tileHeight);
					}
				}
			}
		}
	}

}
