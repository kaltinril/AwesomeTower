package com.twojeremys.awesometower.tileengine;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.twojeremys.awesometower.Constants;

public class TileMap {
	
	private static final String TAG = TileMap.class.getSimpleName();

	private Tile[][] tiles;
	
	private Array<Tile> placedTiles;
	
	//Holds a map of all the tile properties we use; loads from file in GameScreen.java
	private HashMap<Integer, TileProperty> tileProperties;
	
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
		this.tileProperties = new HashMap<Integer, TileProperty>();
		
		this.placedTiles= new Array<Tile>(false, 0);
		
		//TODO DEBUG Remove when done testing screen fills
		if (Constants.DEBUG) {
			
			//make the corners red
			this.tiles[0][0] = new Tile(1);
			this.tiles[maxX-1][maxY-1] = new Tile(1);
			this.tiles[maxX-1][0] = new Tile(1);
			this.tiles[0][maxY-1] = new Tile(1);
		}
		
		populatePlacedArray();
	}
	
	private void calculateMaxTiles(){
		if (tiles != null){
			this.maxX = tiles.length;
			this.maxY = tiles[0].length;
			Gdx.app.debug(TAG, "maxX: " + this.maxX + " maxY: " + this.maxY);
		}
	}
	
	public Array<Tile> getPlacedTiles(){
		return placedTiles;
	}
	
	//This returns the room clicked on, regardless of which PART of that room is clicked on
	// Meaning if a tile is a "Child" return the parent instead.
	// If the tile is empty, return null
	public Tile getTile(int tileX, int tileY){
		if (pointsWithinBounds(tileX, tileY)){
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
		} else {
			return null;
		}
	}

	public boolean setTile(int x, int y, int tile){
		
		//Make sure there are no collisions before we place the tile
		//if there is then do nothing
		if (!hasCollision(x, y, tile) && canPlace(x, y, tile)) {
			
			//Get tile properties so we know size of the tile we're working with
			TileProperty tp = getTilePropertiesById(tile);
			
			Tile parentTile = new Tile(tile);
			
			//Set the child tiles to reference the parent for collision detection
			for (int spanX=0; spanX < tp.getTileSpanX(); spanX++)
				for (int spanY=0; spanY < tp.getTileSpanY(); spanY++)
					this.tiles[x+spanX][y+spanY] = new Tile(tile, parentTile);
			
			//Set the Initial tile spot to the correct value
			this.tiles[x][y] = parentTile;
			placedTiles.add(parentTile);
			
			updateNoiseLevel(x, y, tp, true);
			return true;
		}
		else{
			//TODO TASK we should pop toast to inform the user of collision
			Gdx.app.debug(TAG, "Outside or Collision " + x + " " + y);
			return false;
		}
	}
	
	public boolean removeTile(int x, int y){
		if (pointsWithinBounds(x, y)){
			Tile tile = tiles[x][y];
			
			if (tile != null){
				TileProperty tp = getTilePropertiesById(tile.getID());
				
				//Remove noise this tile generates
				updateNoiseLevel(x, y, tp, false);
				
				//remove references to the tile
				for (int spanX=0; spanX < tp.getTileSpanX(); spanX++)
					for (int spanY=0; spanY < tp.getTileSpanY(); spanY++)
						this.tiles[x+spanX][y+spanY] = null; //TODO ENHANCE change to "buy this space" tile/room
			}
		}
		
		return false;
	}
	

	private void updateNoiseLevel(int x, int y, TileProperty tp, boolean addNoise){

		int maxAffectable = (2 * (tp.getTileSpanX()+2)) +  (2 * tp.getTileSpanY()); 
		ArrayMap<Tile, Vector2> affectedTiles = new ArrayMap<Tile, Vector2>(false, maxAffectable);
		
		float halfNoise = tp.getNoiseFactor()/2;
		float quarterNoise = tp.getNoiseFactor()/4;
		
		//Check tiles directly above this tile
		//Check tiles directly below this tile
		for (int offset = 0; offset<tp.getTileSpanX(); offset++){
			updateNoiseOnTile(x+offset, y-1, halfNoise, addNoise, affectedTiles);
			updateNoiseOnTile(x+offset, y+tp.getTileSpanY(), halfNoise, addNoise, affectedTiles);
		}

		//Check tiles directly to the left of this tile
		//Check tiles directly to the right of this tile
		for (int offset = 0; offset<tp.getTileSpanY(); offset++){
			updateNoiseOnTile(x-1, y+offset, halfNoise, addNoise, affectedTiles);
			updateNoiseOnTile(x+tp.getTileSpanX(), y+offset, halfNoise, addNoise, affectedTiles);
		}
		
		//Check 4 corners
		updateNoiseOnTile(x-1, y-1, quarterNoise, addNoise, affectedTiles); //bottom left corner
		updateNoiseOnTile(x-1, y+tp.getTileSpanY(), quarterNoise, addNoise, affectedTiles); //Top Left Corner
		updateNoiseOnTile(x+tp.getTileSpanX(), y-1, quarterNoise, addNoise, affectedTiles); //Bottom Right Corner
		updateNoiseOnTile(x+tp.getTileSpanX(), y+tp.getTileSpanY(), quarterNoise, addNoise, affectedTiles); //Top Right Corner
		
		//Add up all the noise from tiles that affect this tile.
		for(Entry<Tile, Vector2> tempTile:affectedTiles){

			//The tile is directly above/below/left/right of the tile
			if ((tempTile.value.x >= x) && (tempTile.value.x < x+tp.getTileSpanX()) 
					|| (tempTile.value.y >= y && tempTile.value.y < y+tp.getTileSpanY())){
				//Take half the noise level into account
				updateNoiseOnTile(x, y, this.getTilePropertiesById(tempTile.key.getID()).getNoiseFactor()/2, addNoise);
			} else {
				//Take 1/4 of the noise level into account
				updateNoiseOnTile(x, y, this.getTilePropertiesById(tempTile.key.getID()).getNoiseFactor()/4, addNoise);
			}
		}
	}
	
	
	//Add the noise without null exception errors
	private void updateNoiseOnTile(int x, int y, float noiseAmount, boolean addNoise, ArrayMap<Tile, Vector2> affectedTiles){
		Tile tempTile = this.getTile(x, y);
		if (tempTile != null && !affectedTiles.containsKey(tempTile)){
			affectedTiles.put(tempTile, new Vector2(x,y));
			if (addNoise){
				tempTile.getTileStats().addNoise(noiseAmount);
			} else {
				tempTile.getTileStats().removeNoise(noiseAmount);
			}
			Gdx.app.debug("Noise","Added to x=" + x + " y=" + y + " amount=" + noiseAmount);
		}
	}
	
	//Add the noise without null exception errors
	private void updateNoiseOnTile(int x, int y, float noiseAmount, boolean addNoise){
		Tile tempTile = this.getTile(x, y);
		if (tempTile != null){
			if (addNoise){
				tempTile.getTileStats().addNoise(noiseAmount);
			} else {
				tempTile.getTileStats().removeNoise(noiseAmount);
			}
			Gdx.app.debug("Noise","Added to x=" + x + " y=" + y + " amount=" + noiseAmount);
		}
	}
	
	
	//Add noise to each tile surrounding the tile placed
	//This will propagate outwards in a square (Not circular) pattern
	//addNoise = TRUE means you've just placed tile
	//addNoise - FALSE means you've just sold a tile
//	private void updateNoiseLevel(int x, int y, TileProperties tp, boolean addNoise){
//		
//		Gdx.app.debug("Noise","Placed tile at: x=" + x + " y=" + y + " StartAmount=" + tp.getNoiseFactor()/2);
//		
//		//Half the noise each "layer" of tiles we move out (HowFarAway)
//		for(float noiseLevel=tp.getNoiseFactor()/2, howFarAway = 0;noiseLevel>1;noiseLevel /= 2){
//			howFarAway++;
//
//			//Top row and Bottom Row
//			for (int xtemp = x-(int)howFarAway;xtemp<=x+(int)howFarAway + (tp.getTileSpanX() - 1);xtemp++){
//				Gdx.app.debug("Noise","X loop");
//				updateNoiseOnTile(xtemp, y+(int)howFarAway + (tp.getTileSpanY() - 1), noiseLevel, addNoise);
//				updateNoiseOnTile(xtemp, y-(int)howFarAway, noiseLevel, addNoise);
//			}
//			
//			//Left Side and Right side (Minus top and bottom of sides)
//			for (int ytemp = y-(int)howFarAway+1;ytemp<=y+(int)howFarAway-1 + (tp.getTileSpanY() - 1);ytemp++){
//				Gdx.app.debug("Noise","Y loop");
//				updateNoiseOnTile(x-(int)howFarAway, ytemp, noiseLevel, addNoise);
//				updateNoiseOnTile(x+(int)howFarAway + (tp.getTileSpanX() - 1), ytemp, noiseLevel, addNoise);
//			}
//		}
//	}

	
	//Add the noise without null exception errors
//	private void updateNoiseOnTile(int x, int y, float noiseAmount, boolean addNoise){
//		Tile TempTile = this.getTile(x, y);
//		if (TempTile != null){
//			if (addNoise){
//				TempTile.getTileStats().addNoise(noiseAmount);
//			} else {
//				TempTile.getTileStats().removeNoise(noiseAmount);
//			}
//			Gdx.app.debug("Noise","Added to x=" + x + " y=" + y + " amount=" + noiseAmount);
//		}
//	}
	
	//Called after game load, or if setTiles is called
	private void populatePlacedArray(){
		placedTiles.clear();
		
		for(int x=0;x<maxX;x++){
			for (int y=0;y<maxY;y++){
				if (tiles[x][y] != null && !tiles[x][y].hasParent()){
					placedTiles.add(tiles[x][y]);
				}
			}
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
		populatePlacedArray();
	}
	
	public HashMap<Integer, TileProperty> getTileProperties() {
		return tileProperties;
	}
	
	public TileProperty getTileProperty(int tileID){
		return getTilePropertiesById(tileID);
	}

	public void setTileProperties(HashMap<Integer, TileProperty> tileProperties) {
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
		TileProperty tp = getTilePropertiesById(tile);
				
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
	public boolean hasCollision(int tileX, int tileY, int tile, TileProperty tp) {
		
		if (pointsOutsideBounds(tileX, tileY)){
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

	private boolean pointsWithinBounds(int tileX, int tileY) {
		//If the point is outside the bounds of the map return true
		if (tileX < 0 || tileX >= maxX || tileY < 0 || tileY >= maxY) {
			return false;
		}else{
			return true;
		}
	}
	
	private boolean pointsOutsideBounds(int tileX, int tileY){
		return !pointsWithinBounds(tileX, tileY);
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
		TileProperty tp = getTilePropertiesById(tile);
				
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
						TileProperty tp = getTilePropertiesById(tiles[x][y].getID());
						
						TileStats ts = tiles[x][y].getTileStats();
						
						//Draw this tile to the designated width and height based on tilespan and tilewidth/height
						batch.draw(atlas.findRegion(tp.getAtlasName()), 
							x*tileWidth, y*tileHeight, //Position
							0, 0, //Origin Offset
							tp.getTileSpanX() * tileWidth, tp.getTileSpanY() * tileHeight, //Width and Height to stretch to
							//1-(ts.getCurrentNoiseLevel() / tp.getNoiseTolerance()), 1-(ts.getCurrentNoiseLevel() / tp.getNoiseTolerance()),
							1, 1,   //Scale, Could use this for "floating" affect
							0		//rotation
						);
					}
				}
			}
		}
	}
	
	private TileProperty getTilePropertiesById(int tile) {
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
