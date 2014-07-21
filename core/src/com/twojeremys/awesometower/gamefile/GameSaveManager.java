package com.twojeremys.awesometower.gamefile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.twojeremys.awesometower.Constants;

public class GameSaveManager {

	public static boolean saveState(GameState gameSave, String saveFileName){
		
		try{
			Json json = new Json();	
			String saveData = json.toJson(gameSave);
			
			//TODO ENHANCEMENT ENCRYPT data before saving to file
			
			//Use temporary base64 encoding to obfuscate save data
			saveData = Base64Coder.encodeString(saveData);

			//Windows:	This will default to the Working Directory
			//Android:	This will default to ............
	        FileHandle outHandle= Gdx.files.local(Constants.SAVE_FOLDER + saveFileName);
	        
	        //sometimes we don't have external storage...
//	        if (Gdx.files.isExternalStorageAvailable()) {
//	        	outHandle = Gdx.files.external(saveFileName);
//	        } else {
//	        	outHandle = Gdx.files.internal(saveFileName);
//	        }
	        
	        System.out.println(outHandle.path());
	        
	        outHandle.writeString(saveData, false);
		} catch (Throwable e) {
			//TODO ENHANCEMENT make this some sort of real warning message or pop-up
			System.out.println("ERROR: Couldn't Save to storage [" + saveFileName + "]: " + e.getLocalizedMessage() + "\n");
			return false;
		}
		
		return true;
	}
	
	public static GameState loadState(String saveFileName) {		
		try{
	        FileHandle handle = Gdx.files.local(Constants.SAVE_FOLDER + saveFileName);
	        
	        return  loadState(handle);
		} catch (Throwable e) {
			//TODO ENHANCEMENT make this some sort of real warning message or pop-up
			System.out.println("ERROR: Couldn't Save to storage [" + saveFileName + "]: " + e.getLocalizedMessage() + "\n");
			return null;
		}
	}
	
	public static GameState loadState(FileHandle handle) {
		GameState gameSave;
		Json json = new Json();	
		
		try{
	        //Gdx.files.external(saveFileName);	//Load file from external location
	        String saveData = handle.readString();
	        
			//TODO ENHANCEMENT DECRYPT data before loading from file
			
			//Use temporary base64 decoding to de-obfuscate save data
	        saveData = Base64Coder.decodeString(saveData);
	        
	        //Translate JSON string into a GameSave object
	        gameSave = json.fromJson(GameState.class, saveData);
	        
		} catch (Throwable e) {
			//TODO ENHANCEMENT make this some sort of real warning message or pop-up
			System.out.println("ERROR: Couldn't Save to storage [" + handle.name() + "]: " + e.getLocalizedMessage() + "\n");
			return null;
		}
		
		return gameSave;
	}
}
