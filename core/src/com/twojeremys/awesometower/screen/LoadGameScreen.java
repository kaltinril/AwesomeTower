package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.twojeremys.awesometower.gamefile.GameState;
import com.twojeremys.awesometower.listener.LoadGameInputListener;

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
		
		for(FileHandle file: this.files) {
			//Must not be a directory, and must end with .twr (tower)
			if (!file.isDirectory()){// && file.name().endsWith(".twr")){
			   Label label = new Label(file.name(), labelStyle);
			   label.addListener(new LoadGameInputListener(file.name(), scrollPane));
			   
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
