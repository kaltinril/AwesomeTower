package com.twojeremys.awesometower.screen.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

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
	private Table categoryTable;
	private Container<Actor> categoryNameContainer;
	private Container<Actor> selectionContainer;
	
	private VerticalGroup categoryNameGroup;
	
	
	public GameScreenPuchaseMenu(Skin skin, String menuStyleName){
		this.skin = skin;
		this.menuStyleName = menuStyleName;
	}
	
	private void Initialize(){
	    categoryNameContainer = new Container<Actor>();
	    selectionContainer = new Container<Actor>();
	    categoryNameGroup = new VerticalGroup();
	    
	    //Add the categoryName group to the container
	    categoryNameContainer.setActor(categoryNameGroup);
	    
	    //Build the categoryName menu
	    buildCategoryMenu(categoryNameGroup);
	    
	    //selection table scroll pane
	    selectionScroll = new ScrollPane(selectionTable, skin, "default");
	    selectionScroll.setScrollingDisabled(true, false);
	    
	    buildSelectionMenu(atlas);
	    

		//Setup the scroll bar for vertical only, and fade it after 1 second over 1 second.
		selectionScroll.setScrollBarPositions(false, true);
		selectionScroll.setScrollbarsOnTop(true);
		selectionScroll.setupFadeScrollBars(1.0f, 1.0f);
		selectionScroll.setFadeScrollBars(true);
		
		//Add the scrollpane to the container
		selectionContainer.setActor(selectionScroll);
		selectionContainer.right(); //tells it to draw right to left
		
		//selectionContainer.size(maxRowSize, Gdx.graphics.getHeight());
		
		//Container draws right to left from x as long as you call the size method.  setSize fails
		selectionContainer.setX(Gdx.graphics.getWidth());
		selectionContainer.setY(Gdx.graphics.getHeight()/2);
		
		//Configure the container size to match its child size
		categoryNameContainer.size(categoryNameGroup.getMinWidth(), categoryNameGroup.getMinHeight());
		categoryNameContainer.right();//tells it to draw right to left
		
		//Position container
		categoryNameContainer.setX(Gdx.graphics.getWidth() - selectionContainer.getMinWidth());
		categoryNameContainer.setY(Gdx.graphics.getHeight()/2);
		
	}
	
	private void buildCategoryMenu(VerticalGroup group) {
		
	}
	
	private void buildSelectionMenu(TextureAtlas atlas) {
		
	}
	
	public Table getScrollContentTable(){
		return selectionTable;
	}
	
	public Table getCategoryContentTable(){
		return categoryTable;
	}
	
	
	
	
}
