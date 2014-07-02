package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends BaseScreen {
	
	//
	private AssetManager assets;
	
	//Items used for "loading" screen.
	private Texture loadingCircleTexture;
	private Sprite loadingCircleSprite;
	private boolean assetsLoaded;
	
	//Items used for drawing
	private SpriteBatch batch;
	
	//SIMULATION = Used to simulate a loading time
	float time = 0;
	
	//Font
	private BitmapFont loadingFont;			//import com.badlogic.gdx.graphics.g2d.BitmapFont;
	
	
	public GameScreen (Game game) {
		super(game);
		assetsLoaded = false;
	}

	@Override
	public void show () {
		
		//Get an instance of the SpriteBatch
		batch = new SpriteBatch();
		
		//Load the "loading screen" texture and sprite for rotation
		loadingCircleTexture = new Texture("data/loadingCircle.png");
		loadingCircleSprite = new Sprite(loadingCircleTexture);
		
		//move sprite center of screen
		//Apparently a sprites "position" is the bottom left of the sprite.
		loadingCircleSprite.setPosition(						
				((Gdx.graphics.getWidth()/2) - (loadingCircleSprite.getWidth()/2)),
				((Gdx.graphics.getHeight()/2) - (loadingCircleSprite.getHeight()/2)));	
		
		//Font
        loadingFont = new BitmapFont();		//import com.badlogic.gdx.graphics.g2d.BitmapFont;
        loadingFont.setColor(Color.RED);		//import com.badlogic.gdx.graphics.Color;  --Color.RED from the GDX library instead of javas
		
		//Load all other items needed for game asynchronously with the AssetManager
		assets = new AssetManager();
		assets.load("data/wrench.png", Texture.class);
	}

	@Override
	public void render (float delta) {
		delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		time += delta;							//Used only to create a delay for loading screen simulation
		
		if (assetsLoaded) {
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				game.setScreen(new MainMenuScreen(game));
			}
			else {
				batch.begin();						//start - send data to the graphics pipeline for loading/processing
				batch.draw(assets.get("data/wrench.png", Texture.class), 300, 100);	//Draw the wrench to show its done
				batch.end();						//end - Draw all items batched into the pipeline
			}
			
		}
		else {
			//Simulate loading by waiting 5 seconds before setting "assetsLoaded".
			if ((time > 5) && (assets.update())) {
				assetsLoaded = true;			//Check the status of the assets loading
			}
			
			//This is a temporary loading section, and will no longer be displayed once the assets are loaded
			loadingCircleSprite.rotate(6);		//Rotate the image 6 degrees per frame (1 rotation per second approx)
			
			batch.begin();						//start - send data to the graphics pipeline for loading/processing
			loadingCircleSprite.draw(batch);	//Draw the loading circle sprite
			loadingFont.draw(batch, "Loading...", 
						loadingCircleSprite.getX(), 
						loadingCircleSprite.getY());	//Draw the words "Loading" at the given location with the fonts settings.
			batch.end();						//end - Draw all items batched into the pipeline
		}
	}

	@Override
	public void hide () {
		Gdx.app.debug("Kaltinril", "dispose game screen");
		
		//Get rid of the assets loaded
		loadingCircleTexture.dispose();
		assets.dispose();
	}
}
