package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.twojeremys.awesometower.Category;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.Person;
import com.twojeremys.awesometower.gamefile.GameSaveManager;
import com.twojeremys.awesometower.gamefile.GameState;
import com.twojeremys.awesometower.screen.menu.StatusMenu;
import com.twojeremys.awesometower.tileengine.Tile;
import com.twojeremys.awesometower.tileengine.TileMap;
import com.twojeremys.awesometower.tileengine.TileProperties;
import com.twojeremys.awesometower.tileengine.TileStats;

//TODO TASK
//TODO ENHANCEMENT
//TODO DEBUG

public class GameScreen extends BaseScreen implements GestureListener, InputProcessor {
	
	private static final String TAG = GameScreen.class.getSimpleName();

	//
	private AssetManager assets;

	// Items used for "loading" screen.
	private Texture loadingCircleTexture;
	private Sprite loadingCircleSprite;
	private boolean assetsLoaded;
	
	// Items used for "what am I currently building"?
	private Sprite selectedBuildTileSprite;
	private Sprite prePurchaseSprite;

	// Items used for drawing
	private SpriteBatch batch;
	private SpriteBatch overlayBatch;  //Allow items to be drawn to the screen, independent of the camera.

	// Store the time difference
	private float deltaTime = 0;
	private float deltaSaveTime = 0;
	private float deltaGameDay = 0;

	// Font
	private BitmapFont loadingFont; // com.badlogic.gdx.graphics.g2d.BitmapFont;
	
	//Used to hold debug info in bottom left corner of screen
	private final StringBuilder debugInfo;

	//Are we in build mode?
	private boolean buildMode;

	// Tile engine and map
	private TileMap tileMap;
	private TextureAtlas gamescreenTexutureAtlas;
	
	// Stores the tile being used for placement
	private int currentTile = 1;

	//Grid Line
	private ShapeRenderer shapeRenderer;
	
	// Camera, and fixing the origin of the screen
	private OrthographicCamera camera; // com.badlogic.gdx.graphics.OrthographicCamera;
	
	//Get the actual full Pixel height for the combined tile space, minus 1
	private int screenTileMapHeight;
	private int screenTileMapWidth;
	
	//Used to build the side menu
	private Stage stage;
	private ScrollPane selectionScroll;
	private Table selectionTable;
	private Container<Actor> categoryNameContainer;
	private Container<Actor> selectionContainer;
	private VerticalGroup categoryNameGroup;
	private float maxRowSize = 0f;
	
	//Used for the status menu
	private StatusMenu statusMenu;
	
	//Skin to use for all widgets
	private Skin skin;
	
	private boolean drawTableDebug;
	
	//Save, State, and Status information
	private GameState gameState;
	private String saveName;
	
	//Ground and Backdrop
	private Sprite groundSprite;
	
	public GameScreen(Game game, GameState gameState, String saveName){
		this(game);
		
		//Override with real gameState data
		this.gameState = gameState;
		this.saveName = saveName;
	}
	
	public GameScreen(Game game) {
		super(game);
		assetsLoaded = false;
		if (Constants.DEBUG)
			debugInfo = new StringBuilder();
	}

	@Override
	public void show() {
		stage = new Stage();

		// Get an instance of the SpriteBatch
		batch = new SpriteBatch();
		overlayBatch = new SpriteBatch();

		// Camera
		// http://stackoverflow.com/questions/7708379/changing-the-coordinate-system-in-libgdx-java
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		
		// It appears all setting TRUE does, is flip the screen upside down.
		// Which screws up text and images as they look upside down.
		//TODO TASK Set screen resolution here
		camera.setToOrtho(false, 480, 320);
		
		//default camera zoom
		camera.zoom = Constants.ZOOM_DEFAULT;
		
		// Load the "loading screen" texture and sprite for rotation
		loadingCircleTexture = new Texture("data/loadingCircle.png");
		loadingCircleSprite = new Sprite(loadingCircleTexture);
		
		//Setup the skin to use for all UI items
		skin = new Skin(Gdx.files.internal("ui/skin/uiskin.json"));

		// move sprite to center of screen
		loadingCircleSprite.setPosition(
				((Gdx.graphics.getWidth() / 2) - (loadingCircleSprite.getWidth() / 2)),
				((Gdx.graphics.getHeight() / 2) - (loadingCircleSprite.getHeight() / 2))
				);
		
		// Font
		loadingFont = new BitmapFont(); // com.badlogic.gdx.graphics.g2d.BitmapFont; 
		loadingFont.setUseIntegerPositions(false);
		loadingFont.setColor(Color.RED);
											
		// Load all items needed for game asynchronously with the AssetManager
		assets = new AssetManager();
		assets.load("data/ground.png", Texture.class);
		assets.load("gamescreen.atlas", TextureAtlas.class); //load the tiles atlas
		//assets.finishLoading(); //FIXME if this is not set then the asset won't be loaded when we get to the next line (asynchronous is a problem with current design)
        
		//Disable the "Exit app when back pressed"
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void render(float delta) {
		update(delta);
		
		if (assetsLoaded) {
			draw(delta);
		} else {
			drawLoading(delta);
		}
		
		if (Constants.DEBUG){
			drawDebug();
		}
	}

	//Add moving, currency changes, population changes, any game changing
	private void update(float delta){
		//Clear the screen to sky blue!! (as opposed to ocean blue :)
		Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		deltaTime += delta; // Used only to create a delay for loading screen simulation
		deltaSaveTime += delta; //used to keep track of time since last save
		deltaGameDay += delta; //used to keep track of current day
		
		if (assetsLoaded){
			gameState.addToElapsedSeconds(delta);
			
			if (deltaGameDay > Constants.DAY_LENGTH) {
				
				Gdx.app.debug(TAG, "game day updates now taking place!!!");
				
				//TODO make actual updates
				// What are we updating again?
				
				//This may or may not need to happen faster than once a game day?
				calculateEBIDA(tileMap.getPlacedTiles());
				applyIncomeChanges();
				
				//leap day
				deltaGameDay -= Constants.DAY_LENGTH;
			}
			
			statusMenu.updateValues(gameState);
			
			camera.update(); // Make sure the camera is updated, not really needed in this example
			batch.setProjectionMatrix(camera.combined); // Tell the batch processing to use a specific camera
			
			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		}
		
		//auto save if exceed interval setting
		if (deltaSaveTime > Constants.SAVE_INTERVAL) {
			deltaSaveTime = 0f;
			GameSaveManager.saveState(gameState, saveName);
		}
	}
	
	//Add drawing (except debug)
	private void draw(float delta){
		// This is where our actual drawing and updating code will go for the game
		batch.begin(); // start - send data to the graphics pipeline for loading/processing

		//figure out how many [WIDTHS] to the right until we are off the screen
		//figure out how many [WIDTHS] to the left from X=0 we need to start
		//Find number of pixels WIDE from center of camera.
		float edgeToTileMapX = ((camera.viewportWidth/2) * camera.zoom);
		int maxTimesX = (int)Math.ceil((tileMap.getMapPixelWidth() + edgeToTileMapX + edgeToTileMapX) / groundSprite.getWidth());
		int minTimesX = -(int)Math.ceil(edgeToTileMapX / groundSprite.getWidth());

		//figure out how many [HEIGHTS] to move until we are off the screen down
		float edgeToTileMapY = ((camera.viewportHeight/2) * camera.zoom);
		int maxTimesY = -(int)Math.ceil((edgeToTileMapY + (Constants.GROUND_LEVEL * tileMap.getTileHeight())) / groundSprite.getHeight());
		
		//How far to move the ground sprite down by, so it doesn't go above ground level
		int differneceOfHeight = (int)groundSprite.getHeight() - (Constants.GROUND_LEVEL * tileMap.getTileHeight());
		
		//Draw all the required ground tile copies to fill the entire camera zoomed out
		for(int x=minTimesX;x<maxTimesX;x++){
			for (int y=0;y>maxTimesY;y--){
				groundSprite.setPosition(x*groundSprite.getWidth(), ((y*groundSprite.getHeight()) - differneceOfHeight));
				groundSprite.draw(batch);
			}
		}
		
		tileMap.drawMap(batch);
		
		if (buildMode){
	        if (prePurchaseSprite != null){
	        	prePurchaseSprite.draw(batch);
	        }
		}
		batch.end(); // end - Draw all items batched into the pipeline

		//Draw the gridline, can't be inside the batch above
		if (buildMode){
			renderGridOverlay();
		}
		
		//Draw UI elements of menus
        stage.draw();
        
        //All other overlay items draw in here
        overlayBatch.begin();
        if (buildMode){
	        if (selectedBuildTileSprite != null){
	        	
	        	//TODO ENHANCE perhaps create a Table, that has a set location for this along with other stats (gold/etc)
	        	
	        	//Create a small boarder around the image
	        	selectedBuildTileSprite.setColor(Color.BLACK);
	        	selectedBuildTileSprite.setScale(1.1f);
	        	selectedBuildTileSprite.draw(overlayBatch);
	        	
	        	//Draw the image
	        	selectedBuildTileSprite.setColor(Color.WHITE);
	        	selectedBuildTileSprite.setScale(1.0f);
	        	selectedBuildTileSprite.draw(overlayBatch);
	        }
		}
        overlayBatch.end();
	}

	private void drawLoading(float delta){
		// Check the status of the assets loading
		if (assets.update()) {
			assetsLoaded = true;
			loadingCircleTexture.dispose(); //remove the texture to free memory
			afterAssetsLoaded();	//Load and create all objects needed for the game
		} else {

			// This is a temporary loading section, and will no longer be displayed once the assets are loaded
			loadingCircleSprite.rotate(-6); // Rotate the image 6 degrees per frame (1 rotation per second approx)
	
			overlayBatch.begin(); // start - send data to the graphics pipeline for loading/processing
			loadingCircleSprite.draw(overlayBatch); // Draw the loading circle sprite
			loadingFont.draw(overlayBatch, "Loading...", loadingCircleSprite.getX(),
					loadingCircleSprite.getY()); // Draw the words "Loading" at the given location with the fonts settings.
			overlayBatch.end(); // end - Draw all items batched into the pipeline
		}
	}
	
	//Add debug drawing
	private void drawDebug(){
        if (drawTableDebug && assetsLoaded){
        	Table.drawDebug(stage);
        }
	
		if (assetsLoaded) {
			if (deltaTime >= Constants.DEBUG_DISPLAY_INTERVAL) {
				float javaHeapInBytes = Gdx.app.getJavaHeap() / Constants.ONE_MEGABYTE;
				float nativeHeapInBytes = Gdx.app.getNativeHeap() / Constants.ONE_MEGABYTE;
				
				deltaTime = 0f;
				debugInfo.setLength(0);
				debugInfo.append("fps: ");
				debugInfo.append(Gdx.graphics.getFramesPerSecond());
				debugInfo.append("\nmem: (java ");
				debugInfo.append((int) javaHeapInBytes);
				debugInfo.append("Mb, heap ");
				debugInfo.append((int) nativeHeapInBytes);
				debugInfo.append("Mb)");
				debugInfo.append("\nzoom: ");
				debugInfo.append((float) camera.zoom);
				debugInfo.append("\nviewport: (w ");
				debugInfo.append((int) camera.viewportWidth);
				debugInfo.append(" ,h ");
				debugInfo.append((int) camera.viewportHeight);
				debugInfo.append(")");
			}
			
			overlayBatch.begin();
			loadingFont.drawMultiLine(overlayBatch, debugInfo, 4, 70);
			loadingFont.setColor(Color.CYAN);	
			overlayBatch.end();
		}
	}
	
	//Create all objects that require assets loaded
	private void afterAssetsLoaded(){
		//Store this for later use in setting the menu
		gamescreenTexutureAtlas = (TextureAtlas) assets.get("gamescreen.atlas");
		
		//Create an instance of TileMap based on the GameState.
		if (gameState != null){
			Gdx.app.debug(TAG, "Loading save....");
			tileMap = new TileMap(gamescreenTexutureAtlas, camera, gameState.getTiles());
		}
		else {
			tileMap = new TileMap(gamescreenTexutureAtlas, camera);
			gameState = new GameState();
			gameState.setTiles(tileMap.getTiles());
		}
		
		deltaGameDay = gameState.getElapsedSeconds() % Constants.DAY_LENGTH;
		
		//Get the actual full Pixel height for the combined tile space, minus 1
		screenTileMapHeight = tileMap.getMapPixelHeight();
		screenTileMapWidth = tileMap.getMapPixelWidth();

		Gdx.app.debug(TAG, "Width: " + Gdx.graphics.getWidth());
		Gdx.app.debug(TAG, "Height: " + Gdx.graphics.getHeight());
		
		//Ground and skyline
		groundSprite = new Sprite((Texture)assets.get("data/ground.png"));
		groundSprite.setSize(groundSprite.getWidth()/2,groundSprite.getHeight()/2);
		
		//Grid lines
		shapeRenderer = new ShapeRenderer();
		
		//Setup all the Input handling
        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(stage);
        im.addProcessor(gd);
        im.addProcessor(this);
        
        //Set the input processor to the multiplexer
        Gdx.input.setInputProcessor(im);
        
        //Load the data from properties file
        loadProperties();
        
        //Build the side menu
        buildSideMenu(gamescreenTexutureAtlas);
        
        //Build the Status Menu
        buildStatusMenu();
        
        //Get the correct amounts setup
        calculateEBIDA(tileMap.getPlacedTiles());
	}
	
	//Allow buttons on the stage to be correctly adjusted and positioned
	public void resize (int width, int height) {
	    // See below for what true means.
	    stage.getViewport().update(width, height, true);
	}
	
	

//	7:02 PM - frigidplanet: revenue = patrons * income;
//	7:02 PM - frigidplanet: expenses = employees * constant_pay_rate;
//	7:02 PM - frigidplanet: patrons varies based on another formula that isn't written
//	7:03 PM - frigidplanet: exployees could either be static or adjust based on economics.  probably easier to have it be static for now.

	private void calculateEBIDA(Array<Tile> placedTiles){
		
		float totalIncome=0;
		float totalExpense=0; //Doesn't appear to be an expense yet?
		
		Gdx.app.debug(TAG,"Total Placed Tiles: "+ placedTiles.size);
		
		for(Tile tile:placedTiles){
			//TODO REMOVE Hack for 0 patrons right now, need to get patrons/visitors moving into places and visiting places
			if (Constants.DEBUG){
				//tile.setTileStats(new TileStats());
				//tile.getTileStats().addVisitor(new Person());
				//tile.getTileStats().addVisitor(new Person());
				//tile.getTileStats().addVisitor(new Person());
				//tile.getTileStats().addVisitor(new Person());
				tile.getTileStats().addVisitor(new Person());
			}
			
			TileProperties tp = tileMap.getTileProperty(tile.getID());
			TileStats ts = tile.getTileStats();
			
			if (tp.getCategory() == Category.Commercial){
				totalIncome += (tp.getIncomeAmount() * ts.getVisitorsCount()); //Based on the number of customers
			}else if(tp.getCategory() == Category.Residential) {
				totalIncome += tp.getIncomeAmount(); //Set amount of monthly income from property, not based on residence
				
				if (ts.getOccupantsCount() < tp.getMaxResidents()){
					//ADD MORE RESIDENTS !!
					
					//Ideas
					//1. create randomized sub set of people (Visitors) who will check to see if they want to move in or become employees
					//2. create time interval delay before people show up
					//3. create conditional occupancy (if noise + dirt + etc is valid, create persons and assign)
					//4. At placement time of room, just have them move in
					//5. Create % move in value, and based on desirability, a % of the % is used to create Persons.
					
					//Determine Desirability
					// - 
				}
			}
			
			//TODO ENHANCEMENT if we want, we can create an economics class to manage employment etc	
			totalExpense += (tp.getMaxEmployees() * Constants.EMPLOYEE_MINIMUM_WAGE);
		}
		
		Gdx.app.debug(TAG,"Income is now: "+ totalIncome);
		Gdx.app.debug(TAG,"Expense is now: "+ totalExpense);
		
		gameState.setIncome((int)totalIncome);
		gameState.setExpense((int)totalExpense);
	}
	
	//Perhaps this needs to be in gameState.Update(); ??
	private void applyIncomeChanges(){
		gameState.giveGold(gameState.getIncome());
		gameState.takeGold(gameState.getExpense());
	}
	
	private void buildStatusMenu(){
        //Create the Status Menu
        statusMenu = new StatusMenu(gamescreenTexutureAtlas, skin);
        
        //Use a group so that the table will actually draw its background
        VerticalGroup tempGroup = new VerticalGroup();
        Table statusTable = statusMenu.getStatusTable();
        
        //FIXME adjusts the size to match the category container but it's still a bit off. :(
        float col0Size = 0f;
        for (Cell c : statusTable.getCells()) {
        	
        	if (c.getColumn() == 0) {
        		col0Size = c.getMinWidth();
        	} else if (c.getColumn() == 1) {
        		c.width(categoryNameGroup.getMinWidth() - col0Size);
        		break;
        	}
        }
        
        //Add the table to the group and pack it.
        tempGroup.addActor(statusTable);
        tempGroup.pack();
        
        //Draw at the top right
        tempGroup.setX(Gdx.graphics.getWidth() - tempGroup.getWidth());
        tempGroup.setY(Gdx.graphics.getHeight() - statusTable.getMinHeight());
        
        //Add the group to the stage
        stage.addActor(tempGroup);
	}
	
	private void buildSideMenu(TextureAtlas atlas) {
		/**
         * TODO TASK Menu Testing
         * 
         *  stage -> container (categories) -> verticalgroup -> label
         *           container (selections) -> scrollpane -> table -> row -> cell
         */
	    
	    categoryNameContainer = new Container<Actor>();
	    selectionContainer = new Container<Actor>();

	    stage.addActor(categoryNameContainer);
	    stage.addActor(selectionContainer);
	    
	    /**
	     * categoryName Section
	     */
	    
	    //Holds the list of categories as a single vertical grouping
	    categoryNameGroup = new VerticalGroup();
	    
	    //Right justify the stuff in the group
	    categoryNameGroup.right();
	    
	    //Add the categoryName group to the container
	    categoryNameContainer.setActor(categoryNameGroup);
	       
	    //Build the categoryName menu
	    buildCategoryMenu(categoryNameGroup);
		
	    /**
	     * Selection Section
	     */
	    			    
	    //selection table scroll pane
	    selectionScroll = new ScrollPane(selectionTable, skin, "default");
	    selectionScroll.setScrollingDisabled(true, false);
		
		//TODO DEBUG Test loop just to build a bigger menu
		for (int i = 0; i < 15; i++) {
			buildSelectionMenu(atlas);
		}
		
		//Setup the scroll bar for vertical only, and fade it after 1 second over 1 second.
		selectionScroll.setScrollBarPositions(false, true);
		selectionScroll.setScrollbarsOnTop(true);
		selectionScroll.setupFadeScrollBars(.125f, .125f);
		selectionScroll.setFadeScrollBars(true);

		selectionContainer.right(); //tells it to draw right to left
		
		selectionContainer.size(maxRowSize, Gdx.graphics.getHeight());
		
		//Configure the container size to match its child size
		categoryNameContainer.size(categoryNameGroup.getMinWidth(), categoryNameGroup.getMinHeight());
		categoryNameContainer.right();//tells it to draw right to left
		
		//Position container
		categoryNameContainer.setX(Gdx.graphics.getWidth());
		categoryNameContainer.setY(categoryNameContainer.getMinHeight()/2);
		
		//Container draws right to left from x as long as you call the size method.
		selectionContainer.setX(Gdx.graphics.getWidth() - categoryNameContainer.getMinWidth());
		selectionContainer.setY(Gdx.graphics.getHeight()/2);
		
		if (Constants.DEBUG){
			//Gdx.app.debug(TAG, "getWidth" + categoryNameContainer.getWidth());
			//Gdx.app.debug(TAG, "getMinWidth" + categoryNameContainer.getMinWidth());
			//Gdx.app.debug(TAG, "getMaxWidth" + categoryNameContainer.getMaxWidth());
		}
	}
	
	private void buildCategoryMenu(VerticalGroup group) {
		
		//Setup a Table to use the scroll pane background for the menu
		Table backgroundTable = new Table();
		backgroundTable.debug();
	    backgroundTable.background(skin.getDrawable("default-pane"));
		
		//Loop through the category enum and build the buttons and tables
		for (final Category c : Category.values()) {
			
			c.setTable(new Table().pad(Constants.TABLE_PAD).debug());
			
			InputListener categoryListener = new InputListener(){
				@Override
			    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					Gdx.app.debug(TAG, "category touchDown performed {"
			    			+ "event: " + event.toString()
			    			+ ", pointer: " + pointer
			    			+ ", button: " + button
			    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y) + ")"
			    	+ "}");
					return true;
			    }
				@Override
			    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					Gdx.app.debug(TAG, "category touchUp performed {"
			    			+ "event: " + event.toString()
			    			+ ", pointer: " + pointer
			    			+ ", button: " + button
			    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y) + ")"
			    	+ "}");
					
					if (selectionScroll.getChildren().size > 0 && selectionScroll.getChildren().peek() == c.getTable()) {
						
						//System.out.println("Before Shrink POS:  " + selectionScroll.getX() + " - " + selectionScroll.getY());
						
						//shrink and move the scrollpane
						selectionScroll.addAction(
								Actions.sequence(
										Actions.parallel(
													Actions.fadeOut(0.25f)
													//Actions.scaleTo(0, 0, 0.25f)
													//Actions.moveBy(selectionScroll.getWidth(), 0, 0.5f)
													//Actions.moveTo(0, selectionScroll.getY(), 1f)
												)
									    , Actions.run(new Runnable() {
						    public void run () {
						        selectionScroll.removeActor(c.getTable());
						        selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane
						        selectionContainer.removeActor(selectionScroll);
						        //System.out.println("Shrunk POS:  " + selectionScroll.getX() + " - " + selectionScroll.getY());
						    }
						})));
						//selectionScroll.removeActor(c.getTable());
						//selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane
						//selectionContainer.size(0, Gdx.graphics.getHeight());
						//categoryNameContainer.setX(Gdx.graphics.getWidth() - selectionContainer.getMinWidth());
						//categoryNameContainer.setY(Gdx.graphics.getHeight()/2);
					} else {
						
						//Setup the position of the scroll as ontop of the container
						//selectionScroll.setScale(0.0f);
						
						//System.out.println("Before Grow POS params:  " + selectionScroll.getX() + " - " + selectionScroll.getY());
						
						//selectionScroll.setX(0);
						selectionScroll.setWidget(c.getTable());
						selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane
						
						//If the container doesn't have any children then we need to add the scrollpane
						if (!selectionContainer.hasChildren()) {
							selectionContainer.setActor(selectionScroll);
						}
						
						selectionContainer.size(c.getTableWidth(), Gdx.graphics.getHeight());
						//System.out.println("Before Grow POS:  " + selectionScroll.getX() + " - " + selectionScroll.getY());

						//selectionScroll.setPosition(-categoryNameContainer.getMinWidth(), selectionScroll.getY());
						
						//System.out.println(-categoryNameContainer.getMinWidth());
						
						//shrink and move the scrollpane
						selectionScroll.addAction(
								Actions.sequence(
										Actions.parallel(
													Actions.fadeIn(0.25f)
													//Actions.scaleTo(1, 1, 0.25f) 
													//Actions.moveTo(-categoryNameContainer.getMinWidth(), selectionScroll.getY(), 1f)
												)
									    , Actions.run(new Runnable() {
						    public void run () {
						        //selectionScroll.removeActor(c.getTable());
						        //selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane
						        //System.out.println("After Grow POS:  " + selectionScroll.getX() + " - " + selectionScroll.getY());
						    }
						})));
						
						
						//selectionContainer.clearActions();
						//selectionContainer.addAction(Actions.moveTo(Gdx.graphics.getWidth() - selectionContainer.getMinWidth(), Gdx.graphics.getHeight()/2, 0.5f));
						
						//selectionScroll.setWidget(c.getTable());
						//selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane
						//selectionContainer.size(c.getTableWidth(), Gdx.graphics.getHeight());
						
						//categoryNameContainer.setX(Gdx.graphics.getWidth() - selectionContainer.getMinWidth());
						//categoryNameContainer.setY(Gdx.graphics.getHeight()/2);
					}

			    }
		    };
		    
		    //Create a new text button, with the name of the category, adding the above listener.
		    TextButton l = new TextButton(c.toString(), skin, "default");
		    l.addListener(categoryListener);
		    
		    //Add the button to the table, expanded to fill the width, then add a new row
		    backgroundTable.add(l).expand().fill();
		    backgroundTable.row();
		}
	    group.addActor(backgroundTable); //Only need to add the table once
	}

	private void buildSelectionMenu(TextureAtlas atlas) {
		
		for(final TileProperties tileProperty:tileMap.getTileProperties().values()){
			
			//System.out.println("property=" + tileProperty.getDisplayName());
			
			//Get the table for this category
			Table t = tileProperty.getCategory().getTable();
			float size = tileProperty.getCategory().getTableWidth();
			
			Image sep = new Image(new Texture(Gdx.files.internal("data/grey.png")));
			sep.setHeight(0.5f);
			
			//System.out.println("initial size=" + size);
			
			//Using spacing instead of padding
			// see https://github.com/libgdx/libgdx/wiki/Table#padding
			// for specifics
			//Set the column span since we have a row with two items in it below
			t.row().space(Constants.CELL_SPACE).colspan(2);
		    
		    //Create a listener to add to the Label and button
//		    InputListener sideMenuActionListener = new InputListener(){
//				@Override
//			    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//					if (selectionScroll.isPanning()){return false;}
//					else {return true;}
//			    }
//				@Override
//			    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//					//Kaltinril: appears touchUp was still being called anyways, added !scroll.isPanning check
//					if (!selectionScroll.isPanning()){
//						currentTile = tileProperty.getID();
//						toggleBuildMode();
//					}
//			    }
//		    };
			
			ActorGestureListener sideMenuActionListener = new ActorGestureListener(){
					@Override
				    public boolean longPress (Actor actor, float x, float y) {
						Gdx.app.debug(TAG, "longPress performed {"
				    			+ " actor: " + actor.toString()
				    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y) + ")"
				    	+ "}");
						
				    	//TODO i don't think we need this
						
						return false;
				    }
					@Override
				    public void tap (InputEvent event, float x, float y, int pointer, int button) {
						Gdx.app.debug(TAG, "Tap performed {"
				    			+ " event: " + event.getType().toString()
				    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y) + ")"
				    			+ ", pointer: " + pointer //this is the tap count but fires everytime...
				    			+ ", button: " + button
				    	+ "}");
						
						
						//This will allow you to switch between different tiles while in build mode
						// if the same tile is clicked it pulls you out of build mode
						if (!buildMode) {
							currentTile = tileProperty.getID();
							selectedBuildTileSprite = new Sprite(gamescreenTexutureAtlas.findRegion(tileProperty.getAtlasName()));
							selectedBuildTileSprite.setCenter(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() - selectedBuildTileSprite.getHeight());

							prePurchaseSprite = new Sprite(gamescreenTexutureAtlas.findRegion(tileProperty.getAtlasName()));
							prePurchaseSprite.setPosition(-1000, -1000);
							
							toggleBuildMode();
						} else if (currentTile == tileProperty.getID()) {
							toggleBuildMode();
						} else {
							currentTile = tileProperty.getID();
							selectedBuildTileSprite = new Sprite(gamescreenTexutureAtlas.findRegion(tileProperty.getAtlasName()));
							selectedBuildTileSprite.setCenter(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() - selectedBuildTileSprite.getHeight());

							prePurchaseSprite = new Sprite(gamescreenTexutureAtlas.findRegion(tileProperty.getAtlasName()));
							prePurchaseSprite.setPosition(-1000, -1000);
						}
				    }
			    };
	
		    //Create a label to the left of the image and add a listener 
		    //So the label OR button can be clicked on
		    Label l = new Label(tileProperty.getDisplayName(), skin, "default");
		    
		    l.addListener(sideMenuActionListener);
		    
		    //Add the label to the table
		    //expand and fill allow the label to essentially take up the entire cell
		    t.add(l).center().expand().fill();
		    
		    //Check max row size
		    size = (l.getWidth() + Constants.CELL_SPACE*2) > size ? (l.getWidth() + Constants.CELL_SPACE*2) : size;
		    //System.out.println("size=" + size);
		    
		    //Create a new row
		    //Set the column span since we have a row with two items in it below
		    t.row().space(Constants.CELL_SPACE).colspan(2);

			//Create an image button with the image of the tile (room/Purchasable)
		    //Gdx.app.debug(TAG, "TileProperty Name: " + tileProperty.getAtlasName());
			TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(atlas.findRegion(tileProperty.getAtlasName()));
			ImageButton imgButton = new ImageButton(textureRegionDrawable);
			
			//Add the same listener to the button
			imgButton.addListener(sideMenuActionListener);
			
			//Right justify image so that label text does not overlap it
			t.add(imgButton);
			
			size = (imgButton.getWidth() + Constants.CELL_SPACE*2) > size ? (imgButton.getWidth() + Constants.CELL_SPACE*2) : size;
			//System.out.println("size=" + size);
			
			//Create a new row
		    t.row().space(Constants.CELL_SPACE);
		    
		    Label cost = new Label(Constants.USD_SIGN + tileProperty.getPurchaseCost(), skin, "default");
		    
		    cost.addListener(sideMenuActionListener);
		    
		    //Add the label to the table
		    //expand and fill allow the label to essentially take up the entire cell
		    t.add(cost).expand().fill();
		    
		    TextButton info = new TextButton("Info", skin, "default");
		    
		    t.add(info).expand().fill();
		    
		    size = (cost.getWidth() + info.getWidth() + Constants.CELL_SPACE*3) > size ? (cost.getWidth() + info.getWidth() + Constants.CELL_SPACE*3) : size;
		    //System.out.println("size=" + size);
			
			tileProperty.getCategory().setTableWidth(size);
			
			t.row().space(Constants.CELL_SPACE).colspan(2);
						
			t.add(sep).expand().fill();
						
			//System.out.println("final size=" + tileProperty.getCategory().getTableWidth());
			//System.out.println();
		}
	}
	
	private void loadProperties() {
		//For whatever reason can't use Json.fromJson like a static method
        //Had to create an instance
        Json json = new Json();	

        //Create basic structure of TileProperties Json file to see how it will look
        //ArrayList<TileProperties> tp = new ArrayList<TileProperties>();
        
        //TODO DEBUG Remove these tests and examples
        /*
        TileProperties tp = new TileProperties();
        tp.setBlockable(true);
        tp.setID(1);
        tp.setDisplayName("yellowTile");
        tp.setTileSpanX(1);
        tp.setTileSpanY(1);
        tp.setCategory(Category.Commercial);
        
        String outputData = json.toJson(tp);
        
        //first write
        FileHandle outHandle = Gdx.files.external("tileProperties1.txt"); //User Directory
        outHandle.writeString(outputData, false);
         */      
        //Attempt at loading JSON

        //https://github.com/libgdx/libgdx/wiki/File-handling
        //https://github.com/libgdx/libgdx/wiki/Reading-&-writing-JSON
        FileHandle handle = Gdx.files.internal("data/tiles/tileProperties.txt");	//Load file from internal assets
        tileMap.setTileProperties(json.fromJson(tileMap.getTileProperties().getClass(), handle)); //Convert data into its class
   	
    	//for(Entry<Integer, TileProperties> tpr : tileMap.getTileProperties().entrySet())
        //	System.out.println("Key: " + tpr.getKey() + " ID: " + tpr.getValue().getID() + ", Name: " + tpr.getValue().getName());
	}

	//TODO ENHANCEMENT Maybe this should be moved into the tilemap draw, since it uses so many private variables from the tilemap.
	private void renderGridOverlay() {
		//Map the shape render to the camera, so the lines move with it
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		//Setup the Shape Render to user LINE (Not fill) and white color
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1, 1, 1, 1);	
		
		//FIXME Figure out why these 2 FOR loops grow the native heap. (ShapeRenderer issue?)
		//Draw vertical lines
		//Start at the right side of the first tile, and end at the left side of the last tile.
		for(int tileX = tileMap.getTileWidth(); tileX < screenTileMapWidth;tileX=tileX + tileMap.getTileWidth()){
			shapeRenderer.line(tileX, 0, tileX, screenTileMapHeight);
		}
		
		//Draw horizontal lines
		//Start at the bottom side of the first tile, and end at the top side of the last tile.
		for(int tileY = tileMap.getTileHeight(); tileY < screenTileMapHeight;tileY=tileY + tileMap.getTileHeight()){
			shapeRenderer.line(0, tileY, screenTileMapWidth, tileY);
		}
		 
		//FIXME Figure out why this rect call grows the native heap (eventually by its self without the above for loops)
		//draw square around outside
		shapeRenderer.rect(0, 0, screenTileMapWidth, screenTileMapHeight);
		shapeRenderer.end();
	}

	@Override
	public void hide() {
		Gdx.app.debug(TAG, "hide game screen");
		GameSaveManager.saveState(gameState, saveName);
		super.hide();
	}
	
	@Override
	public void pause() {
		Gdx.app.debug(TAG, "pause game screen");
		super.pause();
	}

	@Override
	public void resume() {
		Gdx.app.debug(TAG, "resume game screen");
		super.resume();
	}

	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose game screen");
		GameSaveManager.saveState(gameState, saveName);
		
		// Get rid of the assets loaded
		loadingCircleTexture.dispose();
		batch.dispose();
		overlayBatch.dispose();
		loadingFont.dispose();
		shapeRenderer.dispose();
		stage.dispose();
		assets.dispose();
		skin.dispose();
		
		//Created a dispose method to see if that helps
		tileMap.dispose();	
	}

	/******************************************************************************
	 * Methods used for Input from multiple sources below
	 ******************************************************************************/	
	
	/*
	Placing a Object
	 1. Select object from build menu (some mechanism) [Triggers BUILD mode]
	 2. LongPress event (Show object in Hover mode)
	 3. Touch Dragged event (Move object to location of finger/mouse)
	 4. TouchUp event (Show object in non-hover mode)
	 5. Tap (multiple) [Place the Object into the tilemap], charge MONEY!
	
	Build Mode:
	 - Zoom  methods(zoom/scrolled)
	 - Pan - (Moving the camera)
	 - Disabled [Object/UI selection] (Tap) - only interact with "Object to be placed".
	 - Object Selection (Longpress)
	
	Manage Mode:
	 - Zoom  methods(zoom/scrolled)
	 - Pan - (Moving the camera)
	 - Object/UI Selection (Tap)
	*/
	
	@Override
	//Not used
	public boolean touchDown(float x, float y, int pointer, int button) {
	    return false;
	}

	@Override
    public boolean tap(float x, float y, int count, int button) {
    	Gdx.app.debug(TAG, "Tap performed {"
    			+ "button: " + button
    			+ ", count: " + count
    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y) + ")"
    	+ "}");
    	
    	
		if (buildMode){
			
			// Convert screen input to camera position
			Vector3 touchPos = new Vector3();
			touchPos.x = x;	//only gets input from the first touch
			touchPos.y = y;	//only gets input from the first touch
			touchPos.z = 0;
			
			// This will convert the screen coordinates passed in to "camera" coordinate system.
			camera.unproject(touchPos);
			
			//Convert to TileX and TileY coordinates by dividing by the width/height
			// For a 20x20 tile, this converts 20 to 1, 40 to 2, 60 to 3.
			Vector2 tileTouchPos = new Vector2(touchPos.x / tileMap.getTileWidth(), touchPos.y / tileMap.getTileHeight());


			Gdx.app.debug(TAG, "Tap performed (unproject) {"
	    			+ "xy: (" + Float.toString(tileTouchPos.x) + "," + Float.toString(tileTouchPos.y) + ")"
	    	+ "}");
			
			//Single tab
			if (count == 1){
				
				//Attempt to tint the sprite green or red depending on a tile collision.
				if (!tileMap.canPlace((int) tileTouchPos.x, (int) tileTouchPos.y, this.currentTile)){
					//Red Tinted (Slightly)
					prePurchaseSprite.setColor(1.0f, 0.5f, 0.5f, 1.0f);
				}else {
					//Green Tinted (Slightly)
					prePurchaseSprite.setColor(0.5f, 1.0f, 0.5f, 1.0f);
				}
				
				//Adjust the position so it is blocked in at each tile boundary
				prePurchaseSprite.setPosition((int) tileTouchPos.x*tileMap.getTileWidth(), (int) tileTouchPos.y*tileMap.getTileHeight());
			}
			else if (count > 1){
				if (gameState.getGold() > tileMap.getTileProperty(this.currentTile).getPurchaseCost()){
					if (prePurchaseSprite.getBoundingRectangle().contains(touchPos.x, touchPos.y)){
						// Set the tile based on this position to tile 0
						boolean placedSuccess = tileMap.setTile((int) tileTouchPos.x, (int) tileTouchPos.y, this.currentTile);
						
						if (placedSuccess){
							prePurchaseSprite.setPosition(-1000, -1000);
							gameState.takeGold(tileMap.getTileProperty(this.currentTile).getPurchaseCost());
						}
					}
				}else {
					if (Constants.DEBUG){
						System.out.println("Not enough gold to purchase Room.");
					}
				}
			}
		}
    	
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
    	Gdx.app.debug(TAG, "Long press performed");
        return false;
    }

    @Override
    //Not used
    public boolean fling(float velocityX, float velocityY, int button) {
    	System.out.println("Fling");
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
    	Gdx.app.debug(TAG, "Pan performed {"
    			+ " delta: (" + Float.toString(deltaX) + "," + Float.toString(deltaY) + ")"
    			+ ", xy: (" + Float.toString(x) + "," + Float.toString(y)
    	+ "}");
    	
    	//TODO TASK Check to see if we've clicked on any of the overlay items first.
    	
		//Force the camera to stay within the bounds of the tile map (WARNING: Does not take into account zooming)
		//Have to adjust for the fact that the camera's (0,0) is the center of the screen, not the bottom left.
    	
    	//If no overlay items have been clicked on, move the camera.
		camera.position.x = MathUtils.clamp(camera.position.x - deltaX, camera.viewportWidth/2, tileMap.getMapPixelWidth() - (camera.viewportWidth/2));
		camera.position.y = MathUtils.clamp(camera.position.y + deltaY, camera.viewportHeight/2, tileMap.getMapPixelHeight() - (camera.viewportHeight/2));
   	
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
    	Gdx.app.debug(TAG, "Zoom performed, initial Distance:" + Float.toString(initialDistance) +
                " Distance: " + Float.toString(distance));
    	
    	return scrollZoom(0.0001f, (int)(initialDistance-distance));
    	
    	//Using a really small value otherwise it's too fast on device
//    	camera.zoom = MathUtils.clamp(camera.zoom + 0.0001f*(initialDistance-distance), Constants.ZOOM_MIN, Constants.ZOOM_MAX);
//    	
//    	adjustCameraWhenZooming();
//    	
//      return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
            Vector2 pointer1, Vector2 pointer2) {
    	//Gdx.app.debug(TAG, "Pinch performed");
        return false;
    }

	@Override
	//Not used
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		Gdx.app.debug(TAG, "Key Down: " + Input.Keys.toString(keycode));
		
		//http://www.gamefromscratch.com/post/2013/10/24/LibGDX-Tutorial-5-Handling-Input-Touch-and-gestures.aspx
		//https://github.com/libgdx/libgdx/wiki/Gesture-detection
		
		//Change to BUILD mode
		if (keycode == Input.Keys.B){
			toggleBuildMode();
			return true;
		}
		
		//Used to toggle table debug line drawing
		//using this has a memory leak
		if (keycode == Input.Keys.D){
			drawTableDebug = !drawTableDebug;
			return true;
		}
		
		//TODO TASK Pop up an "are you sure" message
		if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
			dispose();
			game.setScreen(new MainMenuScreen(game));
			return true;
		}
		
		return false;
	}

	@Override
	//Not used
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	//Not used
	//TODO ENHANCEMENT could use this to capture codes to triggering certain events e.g. more money.
	public boolean keyTyped(char character) {	
		return false;
	}

	@Override
	//Not used
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	//Fires after a touchDragged event set ends, perhaps other situations as well
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.debug(TAG, "Touch Up: " + button + " at x:" + screenX + " y:" + screenY);
		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Gdx.app.debug(TAG, "Touch Dragged: at x:" + screenX + " y:" + screenY);
		
		return true;
	}

	@Override
	//Not going to be handling this, ignore.
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		Gdx.app.debug(TAG, "scrolled: " + amount);
		return scrollZoom(0.1f, amount);
	}
	
	private void toggleBuildMode() {
		this.buildMode = !buildMode;
	}
	
	private boolean scrollZoom(float scrollFactor, int scrollAmount) {
		//This is almost too slow may want to increase.
		camera.zoom = MathUtils.clamp(camera.zoom + scrollFactor*scrollAmount, Constants.ZOOM_MIN, Constants.ZOOM_MAX);
		//adjustCameraWhenZooming();
		return true;
	}
	
    private void adjustCameraWhenZooming() {
        float cameraAdjustX, cameraAdjustY;
        
        float leftBound = 0;
        float rightBound = camera.viewportWidth;
        float upperBound = camera.viewportHeight;
        float lowerBound = 0;
        
        Gdx.app.debug(TAG, "camera.position.x: " + camera.position.x + " camera.position.y: " + camera.position.y);
        
        
        if (camera.position.x <= leftBound) {
            cameraAdjustX = camera.position.x - leftBound;
            camera.position.x -= cameraAdjustX;
        }
        else if (camera.position.x >= rightBound) {
            cameraAdjustX = camera.position.x - rightBound;
            camera.position.x -= cameraAdjustX;
        }
        if (camera.position.y >= upperBound) {
           cameraAdjustY = camera.position.y - upperBound;
           camera.position.y -= cameraAdjustY;
        }
        else if (camera.position.y <= lowerBound) {
           cameraAdjustY = camera.position.y - lowerBound;
           camera.position.y -= cameraAdjustY;
        }         
     }

}
