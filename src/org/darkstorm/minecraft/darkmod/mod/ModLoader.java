package org.darkstorm.minecraft.darkmod.mod;

import java.io.File;
import java.net.URL;

import org.darkstorm.minecraft.darkmod.*;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.access.injection.misc.CustomClassLoader;
import org.darkstorm.tools.strings.StringTools;

public class ModLoader {
	private ModHandler modHandler;

	ModLoader(ModHandler modHandler) {
		this.modHandler = modHandler;
	}

	public void reloadMods() {
		try {
			File modsDir = new File("mods");
			if(!modsDir.exists())
				modsDir.mkdir();
			URL modsDirURL = modsDir.toURI().toURL();
			DarkMod darkMod = DarkMod.getInstance();
			AccessHandler accessHandler = darkMod.getAccessHandler();
			CustomClassLoader classLoader = (CustomClassLoader) accessHandler
					.getClassLoader();
			classLoader.addURL(modsDirURL);
			modHandler.clearMods();
			loadDirectory(classLoader, modsDir);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private void loadDirectory(ClassLoader classLoader, File directory) {
		for(File file : directory.listFiles()) {
			try {
				if(file.isDirectory()) {
					loadDirectory(classLoader, file);
					continue;
				}
				String fileName = file.getName();
				if(!fileName.endsWith(".class") || fileName.contains("$"))
					continue;
				String path = file.getAbsolutePath();
				path = StringTools.splitFirst(path, "mods" + File.separator)[1]
						.replace(File.separatorChar, '.').replace(".class", "");
				Class<?> modClass = classLoader.loadClass(path);
				if(!Mod.class.isAssignableFrom(modClass))
					continue;
				Mod mod = (Mod) modClass.newInstance();
				modHandler.addMod(mod);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public ModHandler getModHandler() {
		return modHandler;
	}
}
