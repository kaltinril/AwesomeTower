package com.twojeremys.awesometower;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.twojeremys.awesometower.tileengine.Tile;

public class Person {

	private Sprite sprite;
	
	private Tile residence;
	private Tile visiting;
	
	//Motivations (Reasons to visit locations)
	private int hunger;	//0 = RED WARRIOR NEEDS FOOD BADLY
	private int bordem;	//0 = watching honey boo boo
	private int energy; //0 = ZZZZZZZzzzzz
	
	//What brings population into the tower?
	// - Availability for housing (Residents)
	// - Employment 
	// - 
	
	public Sprite getSprite() {
		return sprite;
	}
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	public Tile getResidence() {
		return residence;
	}
	public void setResidence(Tile residence) {
		this.residence = residence;
	}
	public Tile getVisiting() {
		return visiting;
	}
	public void setVisiting(Tile visiting) {
		this.visiting = visiting;
	}
	public int getHunger() {
		return hunger;
	}
	public void setHunger(int hunger) {
		this.hunger = hunger;
	}
	public int getBordem() {
		return bordem;
	}
	public void setBordem(int bordem) {
		this.bordem = bordem;
	}
	public int getEnergy() {
		return energy;
	}
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
}
