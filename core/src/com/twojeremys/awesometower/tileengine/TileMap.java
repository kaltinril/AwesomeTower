package com.twojeremys.awesometower.tileengine;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.twojeremys.awesometower.Constants;

public class TileMap {

	private int[][] tiles;
	
	private HashMap<Integer, String> tileMap = new HashMap<Integer, String>();
	
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
		
		//Generate the tileMap so that we can correlate the grid index key to the texture region
		for (AtlasRegion reg : atlas.getRegions()) {
			tileMap.put(reg.index, reg.name);
		}
		
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
			
			this.tiles[0][0] = 1;
			this.tiles[maxX-1][maxY-1] = 1;
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
	
	public void drawMap(SpriteBatch batch) {
		
		//TODO: Use Camera.unproject with (0,0), (0,screenheight), (screenwidth,0), and (screenwidth,screenheight)
		// This way we only draw tiles that will be partially or fully visible within the camera
		for (int x = 0; x < maxX ; x++){
			for (int y = 0; y < maxY; y++) {
				if (tiles[x][y] != 0){
					//System.out.println("tile contents: " + tiles[x][y]);
					batch.draw(atlas.findRegion(tileMap.get(tiles[x][y])), x*tileWidth, y*tileHeight);
				}
			}
		}
	}

}
