package com.twojeremys.awesometower.listener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.gamefile.GameSave;
import com.twojeremys.awesometower.gamefile.GameSaveManager;
import com.twojeremys.awesometower.screen.IntroScreen;
import com.twojeremys.awesometower.screen.LoadGameScreen;

public class DefaultMenuActionListener extends InputListener {
	
	//TODO ENHANCE Turn this into an ENUM maybe?
	private String whichButton;

	public DefaultMenuActionListener(String inWhichButton) {
		super();
		this.whichButton = inWhichButton;
	}
	
	//This only fires when the button is first pressed down
	@Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        
		if (Constants.DEBUG) {
        	System.out.println("down pressed on [" + whichButton + "]");
        }
        
        return true;
    }

    //This only fires when the button is first let up
	@Override
    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
    	
		if (Constants.DEBUG) {
			System.out.println("up pressed on [" + whichButton + "]");
		}
		
    	if (whichButton.equalsIgnoreCase("exitgame")){
    		Gdx.app.exit();
    	}
    	else if (whichButton.equalsIgnoreCase("newgame")){
    		//Ugly pull in and cast
    		//TODO TASK Create a way to allow user to name tower, so it can be used as the filename

    		((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen(((Game) Gdx.app.getApplicationListener())));
    	}
    	else if (whichButton.equalsIgnoreCase("loadgame")){    		
    		((Game) Gdx.app.getApplicationListener()).setScreen(new LoadGameScreen(((Game) Gdx.app.getApplicationListener())));
    	}
    		
    }
}