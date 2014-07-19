package com.twojeremys.awesometower;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.twojeremys.awesometower.screen.MainMenuScreen;

public class AwesomeTower extends Game {
	
	@Override
	public void create () {
		if (Constants.DEBUG) Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(new MainMenuScreen(this)); 
	}
}
