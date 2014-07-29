package com.twojeremys.awesometower.tileengine;

import com.badlogic.gdx.utils.Array;
import com.twojeremys.awesometower.Person;

public class TileStats {
	
	//"Just visiting" when a residential (shouldn't really be needed for residential)
	//Patrons when a business
	private Array<Person> visitors;
	
	//Residents when a condo/apartment
	//Employees when a business
	private Array<Person> occupants;
	
	//TODO TASK add more stuffs to track
	
	private float currentNoiseLevel;
	
	// This is a value that is slowly increased or decreased based on complicated factors of the alignment of the planets in far away galaxys.
	private float desirability;
	
	public TileStats() {
		occupants = new Array<Person>(false, 0);
		visitors = new Array<Person>(false, 0);
	}

	public int getOccupantsCount() {
		return occupants.size;
	}

	public int getVisitorsCount() {
		return visitors.size;
	}
	
	public void addVisitor(Person visitorAddition){
		this.visitors.add(visitorAddition);
	}
	
	public void addOccupant(Person occupantAddition){
		this.occupants.add(occupantAddition);
	}
	
	public boolean removeVisitor(Person p){
		return this.visitors.removeValue(p, true);
	}
	
	public boolean removeOccupant(Person p){
		return this.occupants.removeValue(p, true);
	}

	public float getCurrentNoiseLevel() {
		return currentNoiseLevel;
	}
	
	public void addNoise(float noiseAmount){
		this.currentNoiseLevel += noiseAmount;
	}
	
	public void removeNoise(float noiseAmount){
		this.currentNoiseLevel -= noiseAmount;
	}

	
	public float getDesirability() {
		return desirability;
	}


	
	
}
