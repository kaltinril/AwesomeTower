package com.twojeremys.awesometower.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.twojeremys.awesometower.AwesomeTower;

public class DesktopLauncher {

	private static final boolean rebuildAtlas = false;
	private static final boolean drawDebugOutline = false;

	public static void main(String[] arg) {

		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.combineSubdirectories = true;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/gamescreen", "../android/assets", "gamescreen");
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new AwesomeTower(), config);
	}
}
