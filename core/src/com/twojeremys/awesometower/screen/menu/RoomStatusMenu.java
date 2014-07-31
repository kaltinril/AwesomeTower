package com.twojeremys.awesometower.screen.menu;

import java.text.MessageFormat;
import java.text.NumberFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.twojeremys.awesometower.Constants;
import com.twojeremys.awesometower.tileengine.Tile;
import com.twojeremys.awesometower.tileengine.TileStats;

public class RoomStatusMenu {

	private Table statusTable;
	
	private Label occupantsLabel;
	private Label visitorsLabel;
	private Label noiseLabel;
	private Label desireabilityLabel;
	
	public RoomStatusMenu(TextureAtlas atlas, Skin skin){
		
		statusTable = new Table().pad(Constants.TABLE_PAD);
		statusTable.background(skin.getDrawable("default-pane"));
		statusTable.right();
		
		//Get an image from the atlas for each stat to display
		Image occupantImage = new Image(atlas.findRegion("ui/employee"));
		Image visitorImage = new Image(atlas.findRegion("ui/customer"));
		Image noiseImage = new Image(atlas.findRegion("ui/noise"));
		Image desirabilityImage = new Image(atlas.findRegion("ui/star"));
		
		occupantsLabel = new Label("0", skin, "default");
		visitorsLabel = new Label("0", skin, "default");
		noiseLabel = new Label("0", skin, "default");
		desireabilityLabel = new Label("0", skin, "default");
		
		occupantsLabel.setAlignment(Align.right);
		visitorsLabel.setAlignment(Align.right);
		noiseLabel.setAlignment(Align.right);
		desireabilityLabel.setAlignment(Align.right);
		
		statusTable.columnDefaults(0).width(32).height(32);
		statusTable.columnDefaults(1).width(60);
		
		//Add each stat to the table
		
		//TODO ENHANCE determine a value that we can use for the width, that is a bit more dynamic than "50" for the labels.

		//Setting this once appears to force the table structure for the other rows.
		
		statusTable.add(occupantImage);
		statusTable.add(occupantsLabel);
		statusTable.row();
		
		statusTable.add(visitorImage);
		statusTable.add(visitorsLabel);
		statusTable.row();
		
		statusTable.add(noiseImage);
		statusTable.add(noiseLabel);
		statusTable.row();
		
		statusTable.add(desirabilityImage);
		statusTable.add(desireabilityLabel);
		
		if (Constants.DEBUG){
			statusTable.debug();
		}
	}

	public Table getStatusTable() {
		return statusTable;
	}
	
	
	public void updateValues(Tile tile){
		TileStats ts = tile.getTileStats();
		this.setOccupants(ts.getOccupantsCount());
		this.setVisitors(ts.getVisitorsCount());
		this.setNoise(ts.getCurrentNoiseLevel());
		this.setDesirability(ts.getDesirability());
		
		Gdx.app.debug("meow", "Noise" + ts.getCurrentNoiseLevel());
	}

	public void setDesirability(float desirability) {
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		String result = percentFormat.format(desirability);
		this.desireabilityLabel.setText(result);
	}

	public void setNoise(float currentNoiseLevel) {
		String text = String.valueOf(currentNoiseLevel);
		this.noiseLabel.setText(text);
	}

	public void setVisitors(int visitorsCount) {
		String text = String.valueOf(visitorsCount);
		this.visitorsLabel.setText(text);
	}

	public void setOccupants(int occupantsCount) {
		String text = String.valueOf(occupantsCount);
		this.occupantsLabel.setText(text);
	}
}
