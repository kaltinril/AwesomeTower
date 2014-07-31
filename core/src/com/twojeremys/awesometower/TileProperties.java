package com.twojeremys.awesometower;

import java.util.HashMap;

import com.twojeremys.awesometower.tileengine.TileProperty;

public class TileProperties {
	
	private static TileProperties tileProperty = null;
	private static HashMap<Integer, TileProperty> propertyHash;
	
	protected TileProperties() {
		//Do Nothing
	}
	
	public synchronized static  TileProperties getInstance() {
		if (tileProperty == null) {
			tileProperty = new TileProperties();
			propertyHash = new HashMap<Integer, TileProperty>();
		}
		
		return tileProperty;
	}
	
	//If a property with this ID already exists then it will be overwritten
	public void addProperty(TileProperty tp) {
		propertyHash.put(tp.getID(), tp);
	}
	
	public TileProperty getPropertyById(Integer id) {
		return propertyHash.get(String.valueOf(id));
	}
	
	public TileProperty getPropertyById(String id) {
		return propertyHash.get(id);
	}
	
	public void addProperties(HashMap<Integer, TileProperty> hashish) {
		propertyHash.putAll(hashish);
	}
	
	public HashMap<Integer, TileProperty> getAllProperties() {
		return propertyHash;
	}
	
	public boolean containsProperty(Integer id) {
		return propertyHash.containsKey(String.valueOf(id));
	}
	
	public boolean containsProperty(String id) {
		return propertyHash.containsKey(id);
	}
	
	public boolean containsProperty(TileProperty tp) {
		return propertyHash.containsKey(tp.getID());
	}

}
