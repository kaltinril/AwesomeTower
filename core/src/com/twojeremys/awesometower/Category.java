package com.twojeremys.awesometower;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public enum Category {
	
	Commercial, Residential, Utility, Transport, Lobby;
	
	private Table table;
	private float tableWidth = 0f;
	
	public void setTable(Table i) {
		this.table = i;
	}
	
	public Table getTable() {
		return table;
	}

	public float getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(float tableWidth) {
		this.tableWidth = tableWidth;
	}
}
