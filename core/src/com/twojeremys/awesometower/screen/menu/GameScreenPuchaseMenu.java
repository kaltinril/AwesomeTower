package com.twojeremys.awesometower.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.twojeremys.awesometower.Category;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.tileengine.TileProperties;

public class GameScreenPuchaseMenu {
	
	//Passed in during creation
	private Skin skin;
	
	//TODO ENHANCE learn more about skins
	private String menuStyleName;	//what to look-up in the atlas/json for the menu images
	
	private Stage stage;
	private TextureAtlas atlas;
	
	//Created and used in BUILD
	private ScrollPane selectionScroll;
	private Table selectionTable;
	private Container<Actor> categoryNameContainer;
	private Container<Actor> selectionContainer;
	
	private VerticalGroup categoryNameGroup;
	
	
	public GameScreenPuchaseMenu(Skin skin, String menuStyleName){
		this.skin = skin;
		this.menuStyleName = menuStyleName;
	}
	
	public Table getScrollContentTable(){
		return selectionTable;
	}
	
	
}
