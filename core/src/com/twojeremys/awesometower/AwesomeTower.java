package com.twojeremys.awesometower;

import com.badlogic.gdx.Game;
import com.twojeremys.awesometower.screen.MainMenuScreen;

public class AwesomeTower extends Game {
	
	@Override
	public void create () {
		setScreen(new MainMenuScreen(this));
	}
}
