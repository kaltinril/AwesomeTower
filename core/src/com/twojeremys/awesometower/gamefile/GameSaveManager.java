package com.twojeremys.awesometower.gamefile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class GameSaveManager {

	public static boolean saveState(GameState gameSave, String saveFileName){
		
		try{
			Json json = new Json();	
			String saveData = json.toJson(gameSave);
			
			//TODO ENHANCEMENT ENCRYPT data before saving to file
			
			//Use temporary base64 encoding to obfuscate save data
			saveData = Base64Coder.encodeString(saveData);

			//Windows:	This will default to the User Directory
			//Android:	This will default to ............
	        FileHandle outHandle = Gdx.files.external(saveFileName + ".twr"); 
	        outHandle.writeString(saveData, false);
		} catch (Throwable e) {
			//TODO ENHANCEMENT make this some sort of real warning message or pop-up
			System.out.println("ERROR: Couldn't Save to External Storage [awesomeTowerSave.txt]: " + e.getMessage() + "\n");
			return false;
		}
		
		return true;
	}
	
	public static GameState loadState(String saveFileName)
	{
		GameState gameSave;
		Json json = new Json();	
		
		try{
	        FileHandle handle = Gdx.files.external(saveFileName);	//Load file from external location
	        String saveData = handle.readString();
	        
			//TODO ENHANCEMENT DECRYPT data before saving to file
			
			//Use temporary base64 decoding to de-obfuscate save data
	        saveData = Base64Coder.decodeString(saveData);
	        
	        //Translate JSON string into a GameSave object
	        gameSave = json.fromJson(GameState.class, saveData);
		} catch (Throwable e) {
			//TODO ENHANCEMENT make this some sort of real warning message or pop-up
			System.out.println("ERROR: Couldn't Load from External Storage [awesomeTowerSave.txt]: " + e.getMessage() + "\n");
			return null;
		}
		
		return gameSave;
	}
}
