package com.twojeremys.awesometower.tileengine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twojeremys.awesometower.Constants;

public class TileMap {

	private int[][] tiles;
	
	//Create a JSON file mapping of ID -> Texture Filename
	private String[] tileMapping;
	
	private AssetManager assets;
	
	// Width and height of our tile map (entire screen)
	private int maxX;
	private int maxY;
	
	private int tileWidth;
	private int tileHeight;

	public TileMap(int inMaxX, int inMaxY, AssetManager inAssets) {
		tileWidth = 20;
		tileHeight = 20;
		
		maxX = inMaxX;
		maxY = inMaxY;
		tiles = new int[maxX][maxY];
		assets = inAssets;
		
		createTileMapping();
		
		//Clear tiles
		for (int x = 0; x < maxX - 1; x++){
			for (int y = 0; y < maxY - 1; y++){
				tiles[x][y] = -1;
			}
		}
		
		//TODO: Remove when done testing screen fills
		if (Constants.DEBUG) {
			for (int xy = 0; xy < maxX - 1; xy++)
				tiles[xy][xy] = 1;
		}
		
		//Load the tiles images via the Asset Manager passed in
		for(int i=0; i< tileMapping.length; i++){
			assets.load(tileMapping[i], Texture.class);
		}
	}
	
	public void setTile(int tileX, int tileY, int tileID){
		
		//Make sure the tile placement is on the screen, otherwise ignore it.
		if (tileX >= 0 && tileX <= (maxX-1) &&	tileY >= 0 && tileY <= (maxY-1))
			tiles[tileX][tileY] = tileID;
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
	
	private void createTileMapping(){
		tileMapping = new String[2];
		
		tileMapping[0] = "tiles/redTile.png";
		tileMapping[1] = "tiles/yellowTile.png";
	}

	public void drawMap(SpriteBatch batch) {
		
		for (int x = 0; x < maxX - 1; x++){
			for (int y = 0; y < maxY - 1; y++) {
				if (tiles[x][y] != -1)
					batch.draw(assets.get(tileMapping[tiles[x][y]], Texture.class), x*tileWidth, y*tileHeight);
			}
		}
	}

}
