package com.twojeremys.awesometower.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.twojeremys.awesometower.AwesomeTower;
import com.twojeremys.awesometower.Constants;

public class MainMenuScreen extends BaseScreen  {
	
	private static final String TAG = MainMenuScreen.class.getSimpleName();

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
		table = new Table(skin);
		
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
				//FIXME this is creating a new dialog each time which is using up memory
				//  should change to show/hide concept so we can reuse the objects.
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
		//loadGameButton.addListener(new DefaultMenuActionListener("loadgame"));
		loadGameButton.addListener(new InputListener(){
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {		        
		        return true;
		    }

		    //This only fires when the button is first let up
			@Override
		    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				//Do a TSA precheck and see if there are any files
				FileHandle[] sysFiles = Gdx.files.local(Constants.SAVE_FOLDER).list();
				
				Gdx.app.debug(TAG, "local avail?: " + Gdx.files.isLocalStorageAvailable());
				Gdx.app.debug(TAG, "exter avail?: " + Gdx.files.isExternalStorageAvailable());
				Gdx.app.debug(TAG, "int path: >" + Gdx.files.getLocalStoragePath() + "<");
				Gdx.app.debug(TAG, "ext path: >" + Gdx.files.getExternalStoragePath() + "<");
				Gdx.app.debug(TAG, "files: " + sysFiles.length);
				
				int foundFiles = 0;
				
				for(FileHandle file: sysFiles) {
					//Must not be a directory
					if (!file.isDirectory()){
						foundFiles++;
					}
				}
	    		
	    		Gdx.app.debug(TAG, "found files: " + foundFiles);
	    		
	    		//If we found files then load the screen
	    		if (foundFiles > 0) {
	    			dispose();
	    			game.setScreen(new LoadGameScreen(game, sysFiles));
	    		} else {
	    			Dialog dialog = new Dialog("No games found!", skin, "default");
	    			
	    			dialog.button("Aww...", true);
	    			dialog.show(stage);
	    		}
			}
		});
		
		// Setup format to be the same as start button
		
		// Add the load game button to the table
		table.add(loadGameButton);
		
		// Add another row to the table
		table.row();
		
		// Create another button using the same style, passing in the word "extiGame" to the listener for handling later
        exitGameButton = new TextButton("Exit Game", skin, "default");
        exitGameButton.addListener(new InputListener(){
		    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {		        
		        return true;
		    }

		    //This only fires when the button is first let up
			@Override
		    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.debug(TAG, "exit button pressed. bye now.");
				Gdx.app.exit();
			}
		});;
        
        // Add some padding all the way around the button this time
        exitGameButton.pad(10);
        
        // Add the extGameButton to the table so it is active/displayed
        table.add(exitGameButton);

        //Set the table off the screen, to use actions to add "tween" flare
        table.setPosition(Gdx.graphics.getWidth()/2, 0);
        
//        MoveToAction bounceAction = new MoveToAction();
//        bounceAction.setInterpolation(Interpolation.swing);
//        bounceAction.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
//        bounceAction.setDuration(0.5f);
//        table.addAction(bounceAction);
        
        //Make the menu move up from off the screen with flare and "tween" by adding Interpolation.swing
        table.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, 0.5f, Interpolation.swing));
        
		// add the table onto the stage
		stage.addActor(table);
	}

	
	//This is a popup that will ask for a name
	private void createNewGamePopup(){
		
		//Create a dialog box
		Dialog dialog = new Dialog("New Tower", skin, "default"){
			@Override
		    protected void result (Object object) {
		    	if ((Boolean)object == true){
		    		if (Constants.DEBUG){
		    			System.out.println("Save Game Name: " + textField.getText());
		    		}
		    		
		    		//Verify text is not empty, add "Tween" red highlight
		    		if (textField.getText().equals("")){
		    			textField.addAction(
		    					Actions.sequence(
		    							Actions.color(Color.RED, 0.2f),
		    							Actions.color(Color.WHITE, 0.2f))
		    					);
		    			cancel();	//Cancel the closing of the window
		    		}else {
		    			dispose();
			    		game.setScreen(new IntroScreen(game, textField.getText()));
			    		Gdx.input.setOnscreenKeyboardVisible(false);
		    		}
		    	} else {
		    		Gdx.input.setOnscreenKeyboardVisible(false);
		    	}
		    }
		};

		//Create a text box (TextField) using the style created above
		textField = new TextField("", skin, "default");
		
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
		dialog.setMovable(false);
		dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false).key(Keys.BACK, false); //FIXME the back button on android is exiting the game not the dialog
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

	//Allow buttons on the stage to be correctly adjusted and positioned
	public void resize (int width, int height) {
	    // See below for what true means.
	    stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void hide () {
		super.hide();
	}
	
	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose main menu");
		batch.dispose();
		title.getTexture().dispose();
		
		//Dispose of all the MENU items
		skin.dispose();
		stage.dispose();
	}

}
