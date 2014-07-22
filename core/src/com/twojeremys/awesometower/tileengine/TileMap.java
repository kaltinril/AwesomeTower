package com.twojeremys.awesometower.tileengine;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.twojeremys.awesometower.Constants;

public class TileMap {
	
	private static final String TAG = TileMap.class.getSimpleName();

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
	
	// Add the Camera for use in restricting what is drawn
	private Camera cam;

	public TileMap(TextureAtlas atlas, Camera camera) {
		this(atlas, camera, new Tile[Constants.DEFAULT_MAX_TILE_MAP_SIZE_X][Constants.DEFAULT_MAX_TILE_MAP_SIZE_Y]);
	}
	
	public TileMap(TextureAtlas atlas, Camera cam, Tile[][] tiles){
		this.tiles = tiles;
		this.atlas = atlas;
		this.cam = cam;
		
		calculateMaxTiles();
		
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
	private void calculateMaxTiles(){
		if (tiles != null){
			this.maxX = tiles.length;
			this.maxY = tiles[0].length;
			Gdx.app.debug(TAG, "maxX: " + this.maxX + " maxY: " + this.maxY);
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

	public boolean setTile(int x, int y, int tile){
		
		//Make sure there are no collisions before we place the tile
		//if there is then do nothing
		if (!hasCollision(x, y, tile) && canPlace(x, y, tile)) {
			
			//Get tile properties so we know size of the tile we're working with
			TileProperties tp = getTilePropertiesById(tile);
			
			Tile parentTile = new Tile(tile);
			
			//Set the child tiles to reference the parent for collision detection
			for (int spanX=0; spanX < tp.getTileSpanX(); spanX++)
				for (int spanY=0; spanY < tp.getTileSpanY(); spanY++)
					this.tiles[x+spanX][y+spanY] = new Tile(tile, parentTile);
			
			//Set the Initial tile spot to the correct value
			this.tiles[x][y] = parentTile;
			return true;
		}
		else{
			//TODO TASK we should pop toast to inform the user of collision
			Gdx.app.debug(TAG, "Outside or Collision " + x + " " + y);
			return false;
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
	
	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	public HashMap<Integer, TileProperties> getTileProperties() {
		return tileProperties;
	}
	
	public TileProperties getTileProperty(int tileID){
		return getTilePropertiesById(tileID);
	}

	public void setTileProperties(HashMap<Integer, TileProperties> tileProperties) {
		this.tileProperties = tileProperties;
	}
	
	/**
	 * Check to see if the supplied tile and coordinates are colliding with another already placed tile
	 * 
	 * @param tileX
	 * @param tileY
	 * @param tile
	 * @return boolean
	 */
	public boolean hasCollision(int tileX, int tileY, int tile) {
		
		//Get tile properties so we know size of the tile we're working with
		TileProperties tp = getTilePropertiesById(tile);
				
		return hasCollision(tileX, tileY, tile, tp);
	}
	
	/**
	 * Check to see if the supplied tile and coordinates are colliding with another already placed tile
	 * 
	 * @param tileX
	 * @param tileY
	 * @param tile
	 * @param tp
	 * @return
	 */
	public boolean hasCollision(int tileX, int tileY, int tile, TileProperties tp) {
		
		//If the point is outside the bounds of the map return true
		if (tileX < 0 || tileX >= maxX || tileY < 0 || tileY >= maxY) {
			return true;
		}
		
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

	/**
	 * Whether or not the given tile can be placed at the given coordinates.
	 *  uses hasCollision() internally so does not need to be called.
	 * 
	 * @param tileX
	 * @param tileY
	 * @param tile
	 * @return boolean
	 */
	public boolean canPlace(int tileX, int tileY, int tile){
		
		//Get tile properties so we know size of the tile we're working with
		TileProperties tp = getTilePropertiesById(tile);
				
		if (hasCollision(tileX, tileY, tile, tp)) {
			return false;
		}
		
		//Check tiles under placement position, count to see if it meets requirement
		int countOfTiles = 0;
		int checkDirection = -1;
		//If below ground, must place against room above.
		//TODO Figure out the ground level and store as constant to use everywhere
		if (tileY < 10){
			checkDirection = 1;
		}
			
		for (int spanX = 0; spanX < tp.getTileSpanX(); spanX++){
			if (tiles[tileX+spanX][tileY + checkDirection] != null){
				countOfTiles++;
			}
		}
		if (countOfTiles < tp.getRequiredTilesBelow()){
			return false;
		}
		
		//Check if a room is trying to be placed on a floor it is not allowed on
		if (tp.getBlackListFloors() != null && tp.getBlackListFloors().length != 0){
			for (int floor:tp.getBlackListFloors()){
				if (tileY == floor){
					return false;
				}
			}
		}
		
		//Check if a room is allowed to be placed here, null indicates allowed all
		if (tp.getWhiteListFloors() != null && tp.getWhiteListFloors().length != 0){
			for (int floor:tp.getWhiteListFloors()){
				if (tileY == floor){
					return true;
				}
			}
			//No floor found to match the white list
			return false;
		}
		
		return true;
	}
	
	public void drawMap(SpriteBatch batch) {
		
		//TODO NOTE - This section may not be needed at all if the size of the full map does not increase
		// Currently this is only saving drawing maybe 2-3 columns and 2-3 rows
		// This is because of the required buffer since we are not drawing actual TILES but tiles that can SPAN
		/*****
		 *   Below section may not be needed
		 *   - Try to figure out what the first "visible" tile is on the screen, and the last "visible" tile is
		 *   - Only draw the tiles that are going to be up being "visible"
		 */
		
		//Create a buffer so that wide tiles (pink robbin) are still drawn
		// - to see why this is used set the bufferWidth to 0, place a pink robbin room
		// - Zoom all the way in, and pan around
		int bufferWidth = 6;
		int bufferHeight = 2;
		
		//Bottom Left (plus 6 tiles, to accommodate wider and taller tiles
		Vector3 minPos = new Vector3(-(tileWidth*bufferWidth), Gdx.graphics.getHeight() + (tileHeight*bufferHeight), 0);
		
		//Top Right
		Vector3 maxPos = new Vector3(Gdx.graphics.getWidth() + (tileWidth*bufferWidth),-(tileHeight*bufferHeight), 0);
		
		//Adjust to the camera positions
		minPos = cam.unproject(minPos);
		maxPos = cam.unproject(maxPos);

		//Convert to tile positions
		minPos.x = MathUtils.clamp(minPos.x / this.tileWidth, 0, maxX);
		minPos.y = MathUtils.clamp(minPos.y / this.tileHeight, 0, maxY);
		
		maxPos.x = MathUtils.clamp(maxPos.x / this.tileWidth, 0, maxX);
		maxPos.y = MathUtils.clamp(maxPos.y / this.tileHeight, 0, maxY);
		
		/************************
		 *   The above section is an attempt to restrict drawing, may not be that helpful, especially in zoomed out view
		 */
		
		
		for (int x = (int)minPos.x; x < (int)maxPos.x ; x++){
			for (int y = (int)minPos.y; y < (int)maxPos.y; y++) {
				if (tiles[x][y] != null && !tiles[x][y].hasParent()){
					//For some reason using an Integer or int does not work.  have to cast it to string to find the match
					// It makes no sense to me given that the hashmap key is set to be an Integer.
					if (tileProperties.containsKey(String.valueOf(tiles[x][y].getID()))) {
						
						//Get a reference to the tile property to pull out more information from it
						TileProperties tp = getTilePropertiesById(tiles[x][y].getID());
						
						//Draw this tile to the designated width and height based on tilespan and tilewidth/height
						batch.draw(atlas.findRegion(tp.getAtlasName()), 
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
	
	private TileProperties getTilePropertiesById(int tile) {
		return tileProperties.get(String.valueOf(tile));
	}

	//Not sure if this is needed, but added it.
	public void dispose(){
		tiles = null;
		tileProperties = null;
		
		if (atlas!=null) 
			atlas.dispose();
	}
}
