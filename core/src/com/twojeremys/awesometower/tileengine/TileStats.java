package com.twojeremys.awesometower.tileengine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.twojeremys.awesometower.Person;
import com.twojeremys.awesometower.TileProperties;

public class TileStats {
	
	//The id of the tile duh!
	private int id;
	
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
	private float awesomeness = 0;
	
	public TileStats(){
		
	}
	
	public TileStats(int id) {
		this.id = id;
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
		return MathUtils.clamp(1-getNegativeImpact(), 0, 1);
	}
	
	private float getNegativeImpact() {
		
		float nt = TileProperties.getInstance().getPropertyById(id).getNoiseTolerance();
		
		if (nt >= this.currentNoiseLevel) {
			return 0;
		}
		
		float badNoise = this.currentNoiseLevel - nt;
		
		//TODO move this into the properties file
		return (badNoise * .015f);
	}
	
	//TODO think of positive things
	private float getPositiveImpact() {
		return 0;
	}
}
