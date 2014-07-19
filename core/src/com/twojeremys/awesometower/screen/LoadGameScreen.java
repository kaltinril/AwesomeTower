package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.gamefile.GameSaveManager;
import com.twojeremys.awesometower.gamefile.GameState;

public class LoadGameScreen extends BaseScreen{
	
	private static final String TAG = LoadGameScreen.class.getSimpleName();

	private Stage stage;
	private ScrollPane scrollPane;
	private Table scrollInnerTable;
	private FileHandle[] files;

	private GameState gameSave;
	
	public LoadGameScreen(Game game, FileHandle[] files) {
		super(game);
		this.files = files;
	}

	@Override
	public void show () {
		stage = new Stage();
		
		//Create the inner table containing all the saved games
		scrollInnerTable = new Table();
		
		//Create a label style to use
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = new BitmapFont();
		labelStyle.fontColor = Color.MAROON;
		
		//Add the Inner table to the scroll pane
		scrollPane = new ScrollPane(scrollInnerTable);
		
		for(final FileHandle file: this.files) {
			//Must not be a directory
			if (!file.isDirectory()){
			   Label label = new Label(file.name(), labelStyle);
			   
				label.addListener(new InputListener() {
					@Override
					public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
						if (!scrollPane.isPanning()) {
							if (Constants.DEBUG) {
								System.out.println("down pressed on [" + file + "]");
							}
							return true;
						}
						return false;
					}
					
					@Override
					public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
						if (!scrollPane.isPanning()) {
							if (Constants.DEBUG) {
								System.out.println("File Selected: " + file);
							}
							game.setScreen(new GameScreen(game, GameSaveManager.loadState(file), file.name()));
						}
					}
				});
			   
			   scrollInnerTable.add(label);
			   
			   //TODO ENHANCE figure out a way to show a zoomed out image of the map to easily identify.
			   
			   scrollInnerTable.row();
			}
		}
		
		scrollPane.setScrollingDisabled(true, true);
		
		//TODO ENHANCE Figure out where to place this.
		scrollPane.setX(20);
		scrollPane.setY(20);
		scrollPane.setWidth(Gdx.graphics.getWidth() - 40);
		scrollPane.setHeight(Gdx.graphics.getHeight() - 40);
		
		//Add the scroll pane to the stage
		stage.addActor(scrollPane);
		
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}

	//Allow buttons on the stage to be correctly adjusted and positioned
	public void resize (int width, int height) {
	    // See below for what true means.
	    stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void hide () {
		dispose();
	}
	
	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose intro screen");

		stage.dispose();
		
	}

}
