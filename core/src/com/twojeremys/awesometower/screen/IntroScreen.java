package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.twojeremys.awesometower.gamefile.GameState;

public class IntroScreen extends BaseScreen {
	
	private static final String TAG = IntroScreen.class.getSimpleName();

	private TextureRegion intro;
	private SpriteBatch batch;
	private float time = 0;
	
	private GameState gameSave;
	private String saveName;


	public IntroScreen (Game game) {
		super(game);
	}

	public IntroScreen(Game game, GameState gameSave, String saveName){
		this(game);
		
		//Override with real gameSave data
		this.gameSave = gameSave;
		this.saveName = saveName;
	}
	
	public IntroScreen(Game game, String saveName){
		this(game);
		
		this.saveName = saveName;
	}
	
	@Override
	public void show () {
		intro = new TextureRegion(new Texture(Gdx.files.internal("data/intro.png")), 0, 0, 480, 320);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(intro, 0, 0);
		batch.end();

		time += delta;
		if (time > 1) {
			if (Gdx.input.isKeyPressed(Keys.ANY_KEY) || Gdx.input.justTouched()) {
				dispose();
				game.setScreen(new GameScreen(game, gameSave, saveName));
			}
		}
	}

	@Override
	public void hide () {
		super.hide();
	}
	
	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose intro screen");
		batch.dispose();
		intro.getTexture().dispose();
	}

}
