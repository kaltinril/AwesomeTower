package com.twojeremys.awesometower.listener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.gamefile.GameSaveManager;
import com.twojeremys.awesometower.gamefile.GameState;
import com.twojeremys.awesometower.screen.GameScreen;

public class LoadGameInputListener extends InputListener {
	
	//TODO ENHANCE Turn this into an ENUM maybe?
	private String saveFileName;
	private ScrollPane scrollPane;

	public LoadGameInputListener(String saveFileName, ScrollPane scrollPane) {
		super();
		this.saveFileName = saveFileName;
		this.scrollPane = scrollPane;
	}
	
	//This only fires when the button is first pressed down
	@Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (!scrollPane.isPanning()){
			if (Constants.DEBUG) {
	        	System.out.println("down pressed on [" + saveFileName + "]");
	            return true;
	        }
		}
        
		return false;
    }

    //This only fires when the button is first let up
	@Override
    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
    	
		if (!scrollPane.isPanning()){
			if (Constants.DEBUG) {
				System.out.println("File Selected: " + saveFileName);
			}
			// TODO TASK put the load game code screen/method call here
			GameState gameSave = GameSaveManager.loadState(saveFileName);
	
			//TODO ENHANCE fix Ugly pull in and cast
			((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(((Game) Gdx.app.getApplicationListener()), gameSave, saveFileName));
		}
    }
}