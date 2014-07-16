package com.twojeremys.awesometower;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public enum Category {
	
	Commercial, Residential, Utility, Transport, Lobby;
	
	private Table table;
	
	public void setTable(Table i) {
		this.table = i;
	}
	
	public Table getTable() {
		return table;
	}
}
