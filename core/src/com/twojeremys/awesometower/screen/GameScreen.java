package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
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
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.tileengine.TileMap;

public class GameScreen extends BaseScreen implements GestureListener, InputProcessor {

	//
	private AssetManager assets;

	// Items used for "loading" screen.
	private Texture loadingCircleTexture;
	private Sprite loadingCircleSprite;
	private boolean assetsLoaded;

	// Items used for drawing
	private SpriteBatch batch;
	private SpriteBatch overlayBatch;  //Allow items to be drawn to the screen, independant of the camera.

	// SIMULATION = Used to simulate a loading time
	float deltaTime = 0;

	// Font
	private BitmapFont loadingFont; // com.badlogic.gdx.graphics.g2d.BitmapFont;
	
	//TODO add comments :)
	private final StringBuilder debugInfo;

	// Building
	private enum BuildMode {
		manage, build;
	}

	private BuildMode buildMode;

	// Tile engine and map
	TileMap tileMap;

	//Grid Line
	ShapeRenderer shapeRenderer;
	
	// Camera, and fixing the origin of the screen
	private OrthographicCamera camera; // com.badlogic.gdx.graphics.OrthographicCamera;
	//private Vector3 moveStartPosition;

	//Input related
	//Vector3 touchPos;
	
	//Get the actual full Pixel height for the combined tile space, minus 1
	int screenTileMapHeight;
	int screenTileMapWidth;
	
	public GameScreen(Game game) {
		super(game);
		assetsLoaded = false;
		if (Constants.DEBUG)
			debugInfo = new StringBuilder();
	}

	@Override
	public void show() {

		// Get an instance of the SpriteBatch
		batch = new SpriteBatch();
		overlayBatch = new SpriteBatch();

		// Load the "loading screen" texture and sprite for rotation
		loadingCircleTexture = new Texture("data/loadingCircle.png");
		loadingCircleSprite = new Sprite(loadingCircleTexture);

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
		assets.finishLoading(); //TODO if this is not set then the asset won't be loaded when we get to the next line (asynchronous is a problem with current design)

		tileMap = new TileMap(30, 30, (TextureAtlas) assets.get("tiles.atlas"));
		
		//Get the actual full Pixel height for the combined tile space, minus 1
		screenTileMapHeight = tileMap.getMapPixelHeight();
		screenTileMapWidth = tileMap.getMapPixelWidth();

		if (Constants.DEBUG) {
			System.out.println("Width: " + Gdx.graphics.getWidth());
			System.out.println("Height: " + Gdx.graphics.getHeight());
		}

		
		// Camera
		// http://stackoverflow.com/questions/7708379/changing-the-coordinate-system-in-libgdx-java
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		
		// It appears all setting TRUE does, is flip the screen upside down.
		// Which screws up text and images as they look upside down.
		//TODO: Set screen resolution here
		camera.setToOrtho(false, 480, 320);

		// Setup build mode items:
		buildMode = BuildMode.manage;
		
		//Grid lines
		shapeRenderer = new ShapeRenderer();
		
		//Setup all the Input handling
        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);
        
        Gdx.input.setInputProcessor(im);
	}

	@Override
	public void render(float delta) {
		delta = Gdx.graphics.getDeltaTime();

		//Clear the screen black.
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		deltaTime += delta; // Used only to create a delay for loading screen simulation

		camera.update(); // Make sure the camera is updated, not really needed in this example
		batch.setProjectionMatrix(camera.combined); // Tell the batch processing to use a specific camera

		if (assetsLoaded) {
			// This is where our actual drawing and updating code will go for the game
			batch.begin(); // start - send data to the graphics pipeline for loading/processing
			tileMap.drawMap(batch);
			batch.end(); // end - Draw all items batched into the pipeline

			//Draw the gridline
			if (buildMode == BuildMode.build){
				renderGridOverlay();
			}
		} else {
			// Check the status of the assets loading
			if (assets.update()) {
				assetsLoaded = true;
			}

			// This is a temporary loading section, and will no longer be displayed once the assets are loaded
			loadingCircleSprite.rotate(-6); // Rotate the image 6 degrees per frame (1 rotation per second approx)

			overlayBatch.begin(); // start - send data to the graphics pipeline for loading/processing
			loadingCircleSprite.draw(overlayBatch); // Draw the loading circle sprite
			loadingFont.draw(overlayBatch, "Loading...", loadingCircleSprite.getX(),
					loadingCircleSprite.getY()); // Draw the words "Loading" at the given location with the fonts settings.
			overlayBatch.end(); // end - Draw all items batched into the pipeline
		}

		if (Constants.DEBUG) {

			float javaHeapInBytes = Gdx.app.getJavaHeap() / Constants.ONE_MEGABYTE;
			float nativeHeapInBytes = Gdx.app.getNativeHeap() / Constants.ONE_MEGABYTE;

			if (deltaTime >= 3f) {
				deltaTime = 0f;
				debugInfo.setLength(0);
				debugInfo.append("fps: ");
				debugInfo.append(Gdx.graphics.getFramesPerSecond());
				debugInfo.append("\nmem: (java ");
				debugInfo.append((int) javaHeapInBytes);
				debugInfo.append("Mb, heap: ");
				debugInfo.append((int) nativeHeapInBytes);
				debugInfo.append("Mb ");
				debugInfo.append((float) camera.zoom);
				debugInfo.append(" Zoom ");
				debugInfo.append((int) camera.viewportWidth);
				debugInfo.append(" w ");
				debugInfo.append((int) camera.viewportHeight);
				debugInfo.append(" h ");
			}
			
			overlayBatch.begin();
			loadingFont.setColor(Color.WHITE);
			loadingFont.drawMultiLine(overlayBatch, debugInfo, 6, 35);
			loadingFont.setColor(Color.CYAN);
			loadingFont.drawMultiLine(overlayBatch, debugInfo, 5, 36);
			loadingFont.setColor(Color.WHITE);	
			overlayBatch.end();
		}
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

	Manage Mode:
	 - Zoom  methods(zoom/scrolled)
	 - Pan - (Moving the camera)
	 - Object/UI Selection (Tap)
	*/
	
	
	//TODO: Maybe this should be moved into the tilemap draw, since it uses so many private variables from the tilemap.
	private void renderGridOverlay() {
		//Map the shape render to the camera, so the lines move with it
		shapeRenderer.setProjectionMatrix(camera.combined);
		 
		//Setup the Shape Render to user LINE (Not fill) and white color
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(1, 1, 1, 1);	
		
		//TODO: Figure out why these 2 FOR loops grow the native heap. (ShapeRenderer issue?)
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
		 
		//TODO: Figure out why this rect call grows the native heap (eventually by its self without the above for loops)
		//draw square around outside
		shapeRenderer.rect(0, 0, screenTileMapWidth, screenTileMapHeight);
		shapeRenderer.end();
	}

	@Override
	public void hide() {
		Gdx.app.debug("twojeremys", "dispose game screen");

		// Get rid of the assets loaded
		loadingCircleTexture.dispose();
		assets.dispose();
	}

	

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
		if (Constants.DEBUG){
			System.out.println("GD: Touch Down: " + button + " at x:" + x + " y:" + y);
		}
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
    	if (Constants.DEBUG) {
        	System.out.println("Tap performed, finger" + Integer.toString(button));
    	}
    	
    	
		switch (buildMode){
		case build:
			
			//TODO: Need to allow a hover/sprite/temporary "shadow" copy of
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
			touchPos.x = touchPos.x / tileMap.getTileWidth();
			touchPos.y = touchPos.y / tileMap.getTileHeight();

			// Set the tile based on this position to tile 0
			tileMap.setTile((int) touchPos.x, (int) touchPos.y, 2); //TODO remove hard code
			break;
		case manage:


		default:
			break;
		}
    	
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
    	if (Constants.DEBUG) {
    		System.out.println("Long press performed");
    	}
    	
    	//TODO: Put code here to make SelectedObject look like it is "Hovering"
    	// By adding a shadow and/or increasing the image size slightly
    	
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
    	if (Constants.DEBUG) {
    		System.out.println("Fling performed, velocity:" + Float.toString(velocityX) +
                "," + Float.toString(velocityY));
    	}
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
    	if (Constants.DEBUG) {
    		System.out.println("Pan performed, delta:" + Float.toString(deltaX) +
                "," + Float.toString(deltaY));
    	}
    	
    	//TODO: Check to see if we've clicked on any of the overlay items first.
    	
    	
		//TODO: Need to allow moving in Build mode eventually as well
		//Move the camera based on that difference.
		
		//Force the camera to stay within the bounds of the tile map (WARNING: Does not take into account zooming)
		//Have to adjust for the fact that the camera's (0,0) is the center of the screen, not the bottom left.
    	
    	//If no overlay items have been clicked on, move the camera.
		camera.position.x = MathUtils.clamp(camera.position.x - deltaX, camera.viewportWidth/2, tileMap.getMapPixelWidth() - (camera.viewportWidth/2));
		camera.position.y = MathUtils.clamp(camera.position.y + deltaY, camera.viewportHeight/2, tileMap.getMapPixelHeight() - (camera.viewportHeight/2));
   	
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
    	if (Constants.DEBUG) {
    		System.out.println("Zoom performed, initial Distance:" + Float.toString(initialDistance) +
                " Distance: " + Float.toString(distance));
    	}
    	
    	camera.zoom += (0.1f*(initialDistance-distance));
    	
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
            Vector2 pointer1, Vector2 pointer2) {
    	if (Constants.DEBUG) {
    		System.out.println("Pinch performed");
    	}
        return true;
    }

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (Constants.DEBUG){
			System.out.println("Key Down: " + Input.Keys.toString(keycode));
		}
		
		//http://www.gamefromscratch.com/post/2013/10/24/LibGDX-Tutorial-5-Handling-Input-Touch-and-gestures.aspx
		//https://github.com/libgdx/libgdx/wiki/Gesture-detection
		//TODO: Need to convert this to an icon to be clicked on
		//Change to BUILD mode
		if (keycode == Input.Keys.B && buildMode == BuildMode.manage){
			buildMode = BuildMode.build;
			return true;
		}
		
		//TODO: Need to convert this to an icon to be clicked on
		//Change to MANAGE mode
		if (keycode == Input.Keys.M && buildMode == BuildMode.build){
			buildMode = BuildMode.manage;
			return true;
		}
		
		//TODO: Pop up "Are you sure you want to deploy your airbag?"
		if (keycode == Input.Keys.ESCAPE) {
			game.setScreen(new MainMenuScreen(game));
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (Constants.DEBUG){
			System.out.println("Key Up: " + Input.Keys.toString(keycode));
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (Constants.DEBUG){
			System.out.println("Key Typed: " + character);
		}
		
		return false;
	}

	@Override
	//Doesn't appear to ever fire?
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (Constants.DEBUG){
			System.out.println("IP: Touch Down: " + button + " at x:" + screenX + " y:" + screenY);
		}
		return false;
	}

	@Override
	//Fires after a touchDragged event set ends, perhaps other situations as well
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (Constants.DEBUG){
			System.out.println("Touch Up: " + button + " at x:" + screenX + " y:" + screenY);
		}
		
		//TODO: Put code here to "place" SelectedObject (remove any shadow effects, etc)
		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Constants.DEBUG){
			System.out.println("Touch Dragged: at x:" + screenX + " y:" + screenY);
		}
		
		//TODO: Put code here to move the SelectedObject
		
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
}
