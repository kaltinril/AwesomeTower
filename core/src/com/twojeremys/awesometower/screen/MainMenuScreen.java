package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.twojeremys.awesometower.AwesomeTower;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.listener.DefaultMenuActionListener;

public class MainMenuScreen extends BaseScreen  {

	private TextureRegion title;
	private SpriteBatch batch;
	
	//Menu attempt using scene2d ui items
	private Table table;
	private Stage stage;
	private TextButton newGameButton;
	private TextButton exitGameButton;
	private TextButton loadGameButton;
	private Skin skin;
	
	private Label label;
	
	private TextField textField;
	
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
		
		skin = new Skin(Gdx.files.internal("ui/skin/uiskin.json"));

		// Create the table and add the "skin" to it
		// Unsure why a skin for buttons is needed for the table, if we have to explicitly define up/down later?
		table = new Table(skin);
		table.setFillParent(true);
		
		/* *******************************************
		 *  Create the label and add it to the table
		 **********************************************/
		
		// Create the label and add the style to it
		label = new Label("Select an option", skin, "default");
		
		// Add the label to the table so it can be displayed
		table.add(label);
		
		// Add another row
		table.row();
		
		/* *******************************************
		 *  Create the buttons and add them to the table
		 ***********************************************/
		
		// Create the new game button, and pass in the word "newGame" to a listener which is called on the DOWN/UP events
		newGameButton = new TextButton("New game", skin, "default");

		newGameButton.addListener(new InputListener(){
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {		        
		        return true;
		    }

		    //This only fires when the button is first let up
			@Override
		    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (Constants.DEBUG) {System.out.println("NewGameButtonUp");}
			createNewGamePopup();
			}
		});
		
		// add the start-game button to the table
        //  Using the returned [CELL] object, change that cell's size, make it uniform, and add space below it.
        //  - I think this is harder because we have to guess the width, vs just using the padding.
        //  - However, this could be beneficial for creating static/standard width buttons regardless of content (text)
        //  - I'm oldschool I suppose and would rather create an instance of Cell, and call size, uniform, and spaceBottom separately.
		table.add(newGameButton).size( 150f, 40f ).uniform().spaceBottom( 10 );
		
		// Create a new row so the buttons are not side-by-side
		table.row();
		
		// Create the Load Game Button, pass in "loadgame" to the listener
		loadGameButton = new TextButton("Load Game", skin, "default");
		loadGameButton.addListener(new DefaultMenuActionListener("loadgame"));
		
		// Setup format to be the same as start button
		
		// Add the load game button to the table
		table.add(loadGameButton);
		
		// Add another row to the table
		table.row();
		
		// Create another button using the same style, passing in the word "extiGame" to the listener for handling later
        exitGameButton = new TextButton("Exit Game", skin, "default");
        exitGameButton.addListener(new DefaultMenuActionListener("exitGame"));
        
        // Add some padding all the way around the button this time
        exitGameButton.pad(10);
        
        // Add the extGameButton to the table so it is active/displayed
        table.add(exitGameButton);
        
		// add the table onto the stage
		stage.addActor(table);
	}

	
	//This is a popup that will ask for a name
	//TODO ENHANCE create a standard skin that all buttons images etc can use
	private void createNewGamePopup(){
		
		//Create a dialog box
		Dialog dialog = new Dialog("New Tower", skin, "default"){
		    protected void result (Object object) {
		    	if ((Boolean)object == true){
		    		if (Constants.DEBUG){
		    			System.out.println("Save Game Name: " + textField.getText());
		    		}
		    		//TODO ENHANCEMENT dispose of the main menu screen in some form or fashion.
		    		((Game) Gdx.app.getApplicationListener()).setScreen(new IntroScreen(((Game) Gdx.app.getApplicationListener()), textField.getText()));
		    	}
		    }
		};

		//Create a text box (TextField) using the style created above
		textField = new TextField("Kaltinril", skin, "default");
		
		//Adjust Dialog padding
		dialog.padTop(20).padBottom(20);
		
		//Add a label to the dialog
		dialog.text("Enter a Tower Name");
		
		//Add a row, then a text box to the Content table
		dialog.getContentTable().row();
		dialog.getContentTable().add(textField).padLeft(5).padRight(5).row();
		
		//Adjust button padding
		dialog.getButtonTable().padTop(10);
		
		//Add the create game button, which will call result with "TRUE"
		dialog.button("Create Game", true);

		//Add the cancel button, which will call result with "FALSE"
		dialog.button("Cancel", false);
		
		//set it all up
		dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		dialog.invalidateHierarchy();
		dialog.invalidate();
		dialog.layout();
		dialog.show(stage);
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
		skin.dispose();
		stage.dispose();
	}

}
