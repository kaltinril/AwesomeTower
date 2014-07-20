package com.twojeremys.awesometower;

public class Constants {

	//toggles debug information
	public static final boolean DEBUG = true;
	public static final float DEBUG_DISPLAY_INTERVAL = 3f;
	
	//Used to calculate debug display information
	public static final float ONE_MEGABYTE = 1048576.0f;
	
	//Used in the TileMap.class
	public static final int TILE_WIDTH = 20;
	public static final int TILE_HEIGHT = 20;
	
	//Default Max Tile Map Size
	public static final int DEFAULT_MAX_TILE_MAP_SIZE_X = 30;
	public static final int DEFAULT_MAX_TILE_MAP_SIZE_Y = 30;
	
	//The spacing between cells in the game screen side menu
	public static final float CELL_SPACE = 3f; //all sides
	
	//The padding around the table in the game screen side menu
	public static final float TABLE_PAD = 1.5f;
	
	//dollar sign for cost display
	public static final String USD_SIGN = "$";
	
	//auto-save interval in seconds
	public static final float SAVE_INTERVAL = 15f;
	
	//save directory
	public static final String SAVE_FOLDER = "saves/";
	
	public static final float ZOOM_MAX = 5f;
	public static final float ZOOM_MIN = 1f;
	public static final float ZOOM_DEFAULT = 2f;	
}
