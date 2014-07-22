package com.twojeremys.awesometower.screen.menu;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.twojeremys.awesometower.Constants;

public class StatusMenu {

	private Table statusTable;
	
	private Label clockLabel;
	private Label coinLabel;
	private Label expenseLabel;
	private Label incomeLabel;
	private Label populationLabel;
	
	public StatusMenu(TextureAtlas atlas, Skin skin){
		
		statusTable = new Table().pad(Constants.TABLE_PAD);
		statusTable.background(skin.getDrawable("default-pane"));
		statusTable.right();
		
		//Get an image from the atlas for each stat to display
		Image clockImage = new Image(atlas.findRegion("ui/clock"));
		Image coinImage = new Image(atlas.findRegion("ui/coin"));
		Image expenseImage = new Image(atlas.findRegion("ui/expense"));
		Image incomeImage = new Image(atlas.findRegion("ui/income"));
		Image populationImage = new Image(atlas.findRegion("ui/population"));

		//TODO ENHANCEMENT make this data not static
		//Load up the details for each item to be displayed
		clockLabel = new Label("0", skin, "default");
		coinLabel = new Label("0", skin, "default");
		expenseLabel = new Label("0", skin, "default");
		incomeLabel = new Label("0", skin, "default");
		populationLabel = new Label("0", skin, "default");
		
		clockLabel.setAlignment(Align.right);
		coinLabel.setAlignment(Align.right);
		expenseLabel.setAlignment(Align.right);
		incomeLabel.setAlignment(Align.right);
		populationLabel.setAlignment(Align.right);
		
		statusTable.columnDefaults(0).width(32).height(32);
		statusTable.columnDefaults(1).width(60);
		
		//Add each stat to the table
		
		//TODO ENHANCE determine a value that we can use for the width, that is a bit more dynamic than "50" for the labels.

		//Setting this once appears to force the table structure for the other rows.
		statusTable.add(clockImage);
		statusTable.add(clockLabel);
		statusTable.row();
		
		statusTable.add(coinImage);
		statusTable.add(coinLabel);
		statusTable.row();
		
		statusTable.add(expenseImage);
		statusTable.add(expenseLabel);
		statusTable.row();
		
		statusTable.add(incomeImage);
		statusTable.add(incomeLabel);
		statusTable.row();
		
		statusTable.add(populationImage);
		statusTable.add(populationLabel);
		
		if (Constants.DEBUG){
			statusTable.debug();
		}
	}

	public Table getStatusTable() {
		return statusTable;
	}

	public void setStatusTable(Table statusTable) {
		this.statusTable = statusTable;
	}
	
	public void setClock(String clockValue){
		this.clockLabel.setText(clockValue);
	}
	
	public void setCoins(float coinValue){
		this.coinLabel.setText(String.valueOf(coinValue));
	}
	
	public void setExpense(int expenseValue){
		this.expenseLabel.setText(String.valueOf(expenseValue));
	}
	
	public void setIncome(int incomeValue){
		this.incomeLabel.setText(String.valueOf(incomeValue));
	}
	
	public void setPopulation(String populationValue){
		this.populationLabel.setText(populationValue);
	}
	
	
}
