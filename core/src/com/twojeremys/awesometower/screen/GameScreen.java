package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.twojeremys.awesometower.TileEngine.TileMap;

public class GameScreen extends BaseScreen {

	//
	private AssetManager assets;

	// Items used for "loading" screen.
	private Texture loadingCircleTexture;
	private Sprite loadingCircleSprite;
	private boolean assetsLoaded;

	// Items used for drawing
	private SpriteBatch batch;

	// SIMULATION = Used to simulate a loading time
	float time = 0;

	// Font
	private BitmapFont loadingFont; // com.badlogic.gdx.graphics.g2d.BitmapFont;

	// Building
	private enum BuildMode {
		manage, build;
	}

	private BuildMode buildMode;

	// Tile engine and map
	TileMap tileMap;

	// Camera, and fixing the origin of the screen
	private OrthographicCamera camera; // com.badlogic.gdx.graphics.OrthographicCamera;
	private Vector3 moveStartPosition;

	public GameScreen(Game game) {
		super(game);
		assetsLoaded = false;
	}

	@Override
	public void show() {

		// Get an instance of the SpriteBatch
		batch = new SpriteBatch();

		// TODO: Doesn't affect the android screen resolution will need to use
		// some sort of depth adjustment to camera view
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);

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
		
		// --Color.RED from the GDX library instead of java
		loadingFont.setColor(Color.RED); // com.badlogic.gdx.graphics.Color;
											
		// Load all items needed for game asynchronously with the AssetManager
		assets = new AssetManager();

		tileMap = new TileMap(30, 30, assets);

		// Camera
		// http://stackoverflow.com/questions/7708379/changing-the-coordinate-system-in-libgdx-java
		// It appears all this really does, is flip the screen upside down.
		// Which screws up text and images as they look upside down.
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.setToOrtho(false, 480, 320);

		// Setup build mode items:
		buildMode = BuildMode.manage;

	}

	@Override
	public void render(float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		time += delta; // Used only to create a delay for loading screen simulation

		//TODO: Need to convert this to an icon to be clicked on
		//Change to BUILD mode
		if (Gdx.input.isKeyPressed(Input.Keys.B) && buildMode == BuildMode.manage){
			buildMode = BuildMode.build;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.M) && buildMode == BuildMode.build){
			buildMode = BuildMode.manage;
		}
		
		if (Gdx.input.isTouched(0)) {

			// Convert screen input to camera position
			Vector3 touchPos = new Vector3();
			touchPos.x = Gdx.input.getX();
			touchPos.y = Gdx.input.getY();
			touchPos.z = 0;
			
			// This will convert the screen coordinates passed in to "camera" coordinate system.
			camera.unproject(touchPos); 

			//Do stuff depending on the Build Mode
			switch (buildMode){
			case build:
				// Convert to TileX and TileY coordinates by dividing by the width/height
				// For a 20x20 tile, this converts 20 to 1, 40 to 2, 60 to 3.
				touchPos.x = touchPos.x / tileMap.getTileWidth();
				touchPos.y = touchPos.y / tileMap.getTileHeight();

				// Set the tile based on this position to tile 0
				tileMap.SetTile((int) touchPos.x, (int) touchPos.y, 0);
				break;
			case manage:
				
				//Allow moving the camera
				
				//Get the change in input position
				//Didn't work
				//Vector3 cameraDelta = new Vector3(Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
				//camera.unproject(cameraDelta);
				
				//Move the camera based on that difference.
				camera.position.x -= Gdx.input.getDeltaX();//cameraDelta.x;
				camera.position.y += Gdx.input.getDeltaY();//cameraDelta.y;
				
				break;
			default:
				break;
			}
		}

		camera.update(); // Make sure the camera is updated, not really needed in this example
		batch.setProjectionMatrix(camera.combined); // Tell the batch processing to use a specific camera

		if (assetsLoaded) {
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				game.setScreen(new MainMenuScreen(game));
			} else {

				// This is where our actual drawing and updating code will go for the game
				batch.begin(); // start - send data to the graphics pipeline for loading/processing
				tileMap.DrawMap(batch);
				batch.end(); // end - Draw all items batched into the pipeline
			}

		} else {
			// Simulate loading by waiting 1 seconds before setting
			// "assetsLoaded".
			if ((time > 1) && (assets.update())) {
				assetsLoaded = true; // Check the status of the assets loading
			}

			// This is a temporary loading section, and will no longer be displayed once the assets are loaded
			loadingCircleSprite.rotate(6); // Rotate the image 6 degrees per frame (1 rotation per second approx)

			batch.begin(); // start - send data to the graphics pipeline for loading/processing
			loadingCircleSprite.draw(batch); // Draw the loading circle sprite
			loadingFont.draw(batch, "Loading...", loadingCircleSprite.getX(),
					loadingCircleSprite.getY()); // Draw the words "Loading" at the given location with the fonts settings.
			batch.end(); // end - Draw all items batched into the pipeline
		}
	}

	@Override
	public void hide() {
		Gdx.app.debug("twojeremys", "dispose game screen");

		// Get rid of the assets loaded
		loadingCircleTexture.dispose();
		assets.dispose();
	}
}
