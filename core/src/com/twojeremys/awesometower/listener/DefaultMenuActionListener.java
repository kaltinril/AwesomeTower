package com.twojeremys.awesometower.listener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.screen.IntroScreen;

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
    		((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen(((Game) Gdx.app.getApplicationListener())));
    	}
    	else if (whichButton.equalsIgnoreCase("loadgame")){
    		// TODO TASK put the load game code screen/method call here
    		System.out.println("TODO: Load game code");
    	}
    		
    }
}