package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.twojeremys.awesometower.AwesomeTower;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.listener.DefaultMenuActionListener;

public class MainMenuScreen extends BaseScreen  {

	TextureRegion title;
	SpriteBatch batch;
	
	//Menu attempt using scene2d ui items
	Table table;
	Stage stage;
	TextButton newGameButton;
	TextButton exitGameButton;
	TextButton loadGameButton;
	Skin skin;
	
	Label label;
	LabelStyle labelStyle;
	
	BitmapFont font;
	
	TextureAtlas atlas;

	public MainMenuScreen() {
		super(new AwesomeTower());
	}
	
	public MainMenuScreen (Game game) {
		super(game);
	}

	@Override
	public void show () {
		title = new TextureRegion(new Texture(Gdx.files.internal("data/pixietowers.png")), 0, 0, 480, 320);
		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 480, 320);
		
		/*
		 *   Lets create a menu to display choices to the user
		 */
		stage = new Stage();
		
		// Allow the stage to take and process the input, like click or touch. 
		Gdx.input.setInputProcessor(stage);
		
		// Load the json .atlas file that defines where each image is inside the combined texture file (button.png)
		// Store that altas in the skin to use in the button
		atlas = new TextureAtlas(Gdx.files.internal("ui/button/button.atlas"));
		skin = new Skin(atlas);
		
		// TODO ENHANCEMENT Replace with either gdx-freetype font, or a non-default BitmapFont.
		// Create an instance of the default Aerial size 15 Bitmap Font to use with the buttons.
		font = new BitmapFont();
		
		
		// Create the table and add the "skin" to it
		// Unsure why a skin for buttons is needed for the table, if we have to explicitly define up/down later?
		table = new Table(skin);
		table.setFillParent(true);
		
		/* *******************************************
		 *  Create the label and add it to the table
		 **********************************************/
		
		// Create the label Style and set information about it
		labelStyle = new LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = Color.YELLOW;	// import com.badlogic.gdx.graphics.Color;
		
		// Create the label and add the style to it
		label = new Label("Select an option", labelStyle);
		
		// Add the label to the table so it can be displayed
		table.add(label);
		
		// Add another row
		table.row();
		
		/* *******************************************
		 *  Create the buttons and add them to the table
		 ***********************************************/
		
		// This is to define information for the TextButton, what it should look like, etc
		TextButton.TextButtonStyle genericTextButtonStyle = new TextButton.TextButtonStyle();	
		
		// Indicate which icons to use from the atlas, based on the named defined areas inside the altas file
		genericTextButtonStyle.up = skin.getDrawable("button.up");				// Section of the atlas file with information
		genericTextButtonStyle.down = skin.getDrawable("button.down");			// Section of the atlas file with information
		
		// Adjust the text position on the button during "pressed" (down) mode
		genericTextButtonStyle.pressedOffsetX = 1;
		genericTextButtonStyle.pressedOffsetY = -1;
		
		// Set the font to use in this style
		genericTextButtonStyle.font = font;
	
		// Create the new game button, and pass in the word "newGame" to a listener which is called on the DOWN/UP events
		newGameButton = new TextButton("New game", genericTextButtonStyle);
        newGameButton.addListener(new DefaultMenuActionListener("newGame"));

		
		// add the start-game button to the table
        //  Using the returned [CELL] object, change that cell's size, make it uniform, and add space below it.
        //  - I think this is harder because we have to guess the width, vs just using the padding.
        //  - However, this could be beneficial for creating static/standard width buttons regardless of content (text)
        //  - I'm oldschool I suppose and would rather create an instance of Cell, and call size, uniform, and spaceBottom separately.
		table.add(newGameButton).size( 150f, 40f ).uniform().spaceBottom( 10 );
		
		// Create a new row so the buttons are not side-by-side
		table.row();
		
		// Create the Load Game Button, pass in "loadgame" to the listener
		loadGameButton = new TextButton("Load Game", genericTextButtonStyle);
		loadGameButton.addListener(new DefaultMenuActionListener("loadgame"));
		
		// Setup format to be the same as start button
		
		// Add the load game button to the table
		table.add(loadGameButton);
		
		// Add another row to the table
		table.row();
		
		// Create another button using the same style, passing in the word "extiGame" to the listener for handling later
        exitGameButton = new TextButton("Exit Game", genericTextButtonStyle);
        exitGameButton.addListener(new DefaultMenuActionListener("exitGame"));
        
        // Add some padding all the way around the button this time
        exitGameButton.pad(10);
        
        // Add the extGameButton to the table so it is active/displayed
        table.add(exitGameButton);
        
		// add the table onto the stage
		stage.addActor(table);
	}

	@Override
	public void render (float delta) {
		
		// Allow the stage to execute all its code
		stage.act();
		
		//Clear the screen and draw the background
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(title, 0, 0);
		batch.end();

		//Draw the stage (Menu)
		stage.draw();
	}

	@Override
	public void hide () {
		this.dispose();
	}
	
	@Override
	public void dispose() {
		Gdx.app.debug("twojeremys", "dispose main menu");
		batch.dispose();
		title.getTexture().dispose();
		
		//Dispose of all the MENU items
		atlas.dispose();
		skin.dispose();
		stage.dispose();
		font.dispose();	
	}

}
