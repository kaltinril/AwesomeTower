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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.twojeremys.awesometower.Category;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.gamefile.GameSaveManager;
import com.twojeremys.awesometower.gamefile.GameState;
import com.twojeremys.awesometower.tileengine.TileMap;
import com.twojeremys.awesometower.tileengine.TileProperties;

//TODO TASK
//TODO ENHANCEMENT
//TODO DEBUG

//TODO TASK build a menu to select items from
public class GameScreen extends BaseScreen implements GestureListener, InputProcessor {
	
	private static final String TAG = GameScreen.class.getSimpleName();

	//
	private AssetManager assets;

	// Items used for "loading" screen.
	private Texture loadingCircleTexture;
	private Sprite loadingCircleSprite;
	private boolean assetsLoaded;

	// Items used for drawing
	private SpriteBatch batch;
	private SpriteBatch overlayBatch;  //Allow items to be drawn to the screen, independent of the camera.

	// Store the time difference
	private float deltaTime = 0;
	private float deltaSaveTime = 0;

	// Font
	private BitmapFont loadingFont; // com.badlogic.gdx.graphics.g2d.BitmapFont;
	
	//Used to hold debug info in bottom left corner of screen
	private final StringBuilder debugInfo;

	//Are we in build mode?
	private boolean buildMode;

	// Tile engine and map
	private TileMap tileMap;
	
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
	//private float maxButtonSize = 0f;
	//private float maxLabelSize = 0f;
	private float maxRowSize = 0f;
	
	private Category selectedCategory;
	
	//Attempt at adding scroll bars
	private Skin skin;
	
	private boolean drawTableDebug;
	
	private GameState gameState;
	private String saveName;
	
	Actor rotatingActor;
	
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
		
		// --Color.RED from the GDX library instead of java
		loadingFont.setColor(Color.RED); // com.badlogic.gdx.graphics.Color;
											
		// Load all items needed for game asynchronously with the AssetManager
		assets = new AssetManager();
		assets.load("tiles.atlas", TextureAtlas.class); //load the tiles atlas
		assets.finishLoading(); //FIXME if this is not set then the asset won't be loaded when we get to the next line (asynchronous is a problem with current design)

		//Store this for later use in setting the menu
		TextureAtlas atlas = (TextureAtlas) assets.get("tiles.atlas");
		
		//Create an instance of TileMap based on the GameState.
		if (gameState != null){
			Gdx.app.debug(TAG, "Loading save....");
			tileMap = new TileMap(atlas, gameState.getTiles());
		}
		else {
			tileMap = new TileMap(atlas);
			gameState = new GameState();
			gameState.setTiles(tileMap.getTiles());
		}
		
		//Get the actual full Pixel height for the combined tile space, minus 1
		screenTileMapHeight = tileMap.getMapPixelHeight();
		screenTileMapWidth = tileMap.getMapPixelWidth();

		Gdx.app.debug(TAG, "Width: " + Gdx.graphics.getWidth());
		Gdx.app.debug(TAG, "Height: " + Gdx.graphics.getHeight());

		// Camera
		// http://stackoverflow.com/questions/7708379/changing-the-coordinate-system-in-libgdx-java
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		
		// It appears all setting TRUE does, is flip the screen upside down.
		// Which screws up text and images as they look upside down.
		//TODO TASK Set screen resolution here
		camera.setToOrtho(false, 480, 320);
		
		//Grid lines
		shapeRenderer = new ShapeRenderer();
		
		//Setup all the Input handling
        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(stage); //TODO: Fix desktop zoom loss
        im.addProcessor(gd);
        im.addProcessor(this);
        
        //Set the input processor to the multiplexer
        Gdx.input.setInputProcessor(im);
        
        //Load the data from properties file
        loadProperties();
        
        //Build the side menu
        buildSideMenu(atlas);
	}

	@Override
	public void render(float delta) {
		//delta = Gdx.graphics.getDeltaTime();
		
		//System.out.println("rendercalls:" + batch.totalRenderCalls);
	
		//Clear the screen black.
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
		deltaTime += delta; // Used only to create a delay for loading screen simulation
		deltaSaveTime += delta; //used to keep track of time since last save
	
		camera.update(); // Make sure the camera is updated, not really needed in this example
		batch.setProjectionMatrix(camera.combined); // Tell the batch processing to use a specific camera
	
		if (assetsLoaded) {
			// This is where our actual drawing and updating code will go for the game
			batch.begin(); // start - send data to the graphics pipeline for loading/processing
			tileMap.drawMap(batch);
			//container.draw(batch, 1);
			batch.end(); // end - Draw all items batched into the pipeline
	
			//Draw the gridline
			if (buildMode){
				renderGridOverlay();
			}
	
			stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	        stage.draw();
	        
	        if (Constants.DEBUG && drawTableDebug){
	        	Table.drawDebug(stage);
	        }
			
		} else {
			// Check the status of the assets loading
			if (assets.update()) {
				assetsLoaded = true;
				loadingCircleTexture.dispose(); //remove the texture to free memory				
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
		
		//auto save if exceed interval setting
		if (deltaSaveTime > Constants.SAVE_INTERVAL) {
			deltaSaveTime = 0f;
			GameSaveManager.saveState(gameState, saveName);
		}
	
		if (Constants.DEBUG) {
	
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
			//loadingFont.setColor(Color.WHITE);
			loadingFont.drawMultiLine(overlayBatch, debugInfo, 4, 70);
			loadingFont.setColor(Color.CYAN);
			//loadingFont.drawMultiLine(overlayBatch, debugInfo, 5, 36);
			//loadingFont.drawMultiLine(overlayBatch, debugInfo, 4, 37);
			//loadingFont.setColor(Color.WHITE);	
			overlayBatch.end();
		}
	}

	//Allow buttons on the stage to be correctly adjusted and positioned
	public void resize (int width, int height) {
	    // See below for what true means.
	    stage.getViewport().update(width, height, true);
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
	    VerticalGroup categoryNameGroup = new VerticalGroup();
	    
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
		
		//Add the scrollpane to the container
		selectionContainer.setActor(selectionScroll);
		selectionContainer.right(); //tells it to draw right to left
		
		selectionContainer.size(maxRowSize, Gdx.graphics.getHeight());
		
		//Configure the container size to match its child size
		categoryNameContainer.size(categoryNameGroup.getMinWidth(), categoryNameGroup.getMinHeight());
		categoryNameContainer.right();//tells it to draw right to left
		
		//Position container
		categoryNameContainer.setX(Gdx.graphics.getWidth());
		categoryNameContainer.setY(Gdx.graphics.getHeight()/2);
		
		//Container draws right to left from x as long as you call the size method.
		selectionContainer.setX(Gdx.graphics.getWidth() - categoryNameContainer.getMinWidth());
		selectionContainer.setY(Gdx.graphics.getHeight()/2);
		
		System.out.println("getWidth" + categoryNameContainer.getWidth());
		System.out.println("getMinWidth" + categoryNameContainer.getMinWidth());
		System.out.println("getMaxWidth" + categoryNameContainer.getMaxWidth());
		
	    //TODO ENHANCEMENT figure out how to Rotate buttons, below code works but for some reason position ends up all messed up.
		/*
		categoryNameContainer.setTransform(true);
	    rotatingActor = categoryNameContainer;
	    
	    System.out.println(rotatingActor.getCenterX() + "  - -  " + rotatingActor.getOriginX());
	    System.out.println(rotatingActor.getCenterY() + "  - -  " + rotatingActor.getOriginY());
	    
	    rotatingActor.setRotation(-90);
	    */
		
		//Print out the category table contents
//		for (final Category c : Category.values()) {
//			
//			for (Cell cc : c.getTable().getCells()) {
//				if ( cc.getActor() instanceof Label) {
//					Label l = (Label) cc.getActor();
//					System.out.println(l.getText());
//				}
//				
//			}
//		}
		
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
					return true;
			    }
				@Override
			    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					
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
						//selectionScroll.set
						
						//System.out.println("Before Grow POS params:  " + selectionScroll.getX() + " - " + selectionScroll.getY());
						
						//selectionScroll.setX(0);
						selectionScroll.setWidget(c.getTable());
						selectionScroll.setScrollPercentY(0);	//Reset to the top of the scroll pane

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
		    InputListener sideMenuActionListener = new InputListener(){
				@Override
			    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					if (selectionScroll.isPanning()){return false;}
					else {return true;}
			    }
				@Override
			    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					//Kaltinril: appears touchUp was still being called anyways, added !scroll.isPanning check
					if (!selectionScroll.isPanning()){
						currentTile = tileProperty.getID();
						toggleBuildMode();
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
    	Gdx.app.debug(TAG, "Tap performed, finger" + Integer.toString(button));
    	
    	
		if (buildMode){
			
			//TODO TASK Need to allow a hover/sprite/temporary "shadow" copy of
			//  the tile, it would be drawn separately from the tileMap.drawMap method.
			//  This would allow tiles to be dragged around, and the screen to be moved
			//  both in build mode without switching back and forth between modes
			
			// Convert screen input to camera position
			Vector3 touchPos = new Vector3();
			touchPos.x = x;	//only gets input from the first touch
			touchPos.y = y;	//only gets input from the first touch
			touchPos.z = 0;
			
			// This will convert the screen coordinates passed in to "camera" coordinate system.
			camera.unproject(touchPos);
			
			//Convert to TileX and TileY coordinates by dividing by the width/height
			// For a 20x20 tile, this converts 20 to 1, 40 to 2, 60 to 3.
			touchPos.x /= tileMap.getTileWidth();
			touchPos.y /= tileMap.getTileHeight();

			// Set the tile based on this position to tile 0
			tileMap.setTile((int) touchPos.x, (int) touchPos.y, this.currentTile);
		}
    	
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
    	Gdx.app.debug(TAG, "Long press performed");
    	
    	//TODO TASK Put code here to make SelectedObject look like it is "Hovering"
    	// By adding a shadow and/or increasing the image size slightly
    	
        return true;
    }

    @Override
    //Not used
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
    	Gdx.app.debug(TAG, "Pan performed, delta:" + Float.toString(deltaX) + "," + Float.toString(deltaY));
    	
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
    	
    	camera.zoom += (0.1f*(initialDistance-distance));
    	
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
            Vector2 pointer1, Vector2 pointer2) {
    	Gdx.app.debug(TAG, "Pinch performed");
        return true;
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
		//TODO TASK Need to convert this to an icon to be clicked on
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

		//TODO TASK Need to convert this to an icon to be clicked on
		//FIXME does not work for some reason...even tried it on the Table object.  works if you set this before initial draw though
		//Toggle menu visibility
		//FIXME converted this into save temporarily
		if (keycode == Input.Keys.S){
			//scroll.setVisible(!scroll.isVisible());
			GameSaveManager.saveState(gameState, saveName);
			return true;
		}
		
		//TODO TASK Pop up an "are you sure" message
		if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
			this.dispose(); //TODO CONFIRM - does this fix the old ones not being released?
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
		
		//TODO TASK Put code here to "place" SelectedObject (remove any shadow effects, etc)
		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Gdx.app.debug(TAG, "Touch Dragged: at x:" + screenX + " y:" + screenY);
		
		//TODO TASK Put code here to move the SelectedObject
		
		return true;
	}

	@Override
	//Not going to be handling this, ignore.
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.zoom += (0.1f*amount);
		return true;
	}
	
	private void toggleBuildMode() {
		this.buildMode = !buildMode;
	}
}
