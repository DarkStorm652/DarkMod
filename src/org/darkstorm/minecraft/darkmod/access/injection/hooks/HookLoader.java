package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.JProgressBar;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.darkstorm.minecraft.darkmod.tools.Tools;
import org.darkstorm.minecraft.darkmod.ui.LoginUI;
import org.darkstorm.tools.misc.SysTools;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

public class HookLoader {
	private Injector injector;

	private long build;
	private String version;

	public HookLoader(Injector injector) {
		this.injector = injector;
	}

	public Hook[] loadHooks(LoginUI loginUI) {
		if(loginUI != null) {
			loginUI.setDialogText("Loading hooks...");
			loginUI.getDialogProgressBar().setIndeterminate(true);
		}
		Document document = loadXML();
		if(loginUI != null)
			loginUI.setDialogText("Caching hooks for offline mode...");
		outputXML(document);
		if(loginUI != null)
			loginUI.getDialogProgressBar().setIndeterminate(false);
		return parseHooks(document, loginUI);
	}

	private Document loadXML() {
		try {
			DarkMod darkMod = DarkMod.getInstance();
			if(Tools.isRunningFromJar() && !darkMod.isPlayingOffline())
				return new SAXBuilder().build(new URL(
						"http://darkstorm652.webs.com/darkmod/Hooks ("
								+ Tools.getMinecraftBuild() + ","
								+ DarkMod.getVersion() + ").xml"));
			return new SAXBuilder().build(new File("Hooks.xml"));
		} catch(Exception exception) {
			exception.printStackTrace();
			SysTools.exit("Unable to load xml file!", -1);
		}
		return null;
	}

	private void outputXML(Document document) {
		try {
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			File hooksFile = new File("Hooks.xml");
			File hooksDir = hooksFile.getParentFile();
			if(!hooksDir.exists())
				hooksFile.mkdirs();
			FileOutputStream outputStream = new FileOutputStream(hooksFile);
			outputter.output(document, outputStream);
		} catch(Exception exception) {}
	}

	@SuppressWarnings("unchecked")
	private Hook[] parseHooks(Document document, LoginUI loginUI) {
		List<Hook> hooks = new ArrayList<Hook>();
		Element rootElement = document.getRootElement();
		build = Long.valueOf(rootElement.getAttributeValue("build"));
		version = rootElement.getAttributeValue("version");
		List<Element> hookElements = rootElement.getChildren("hook");
		JProgressBar progressBar = null;
		if(loginUI != null) {
			loginUI.setDialogText("Parsing hook data...");
			progressBar = loginUI.getDialogProgressBar();
			progressBar.setMinimum(0);
			progressBar.setMaximum(hookElements.size());
			progressBar.setValue(0);
		}
		int progress = 0;
		for(Element element : hookElements) {
			String type = element.getAttributeValue("type");
			if(type == null)
				SysTools.exit("Error loading hooks: unspecified type", -1);
			else if(type.equals("interface"))
				hooks.add(new InterfaceHook(injector, element));
			else if(type.equals("getter"))
				hooks.add(new GetterHook(injector, element));
			else if(type.equals("setter"))
				hooks.add(new SetterHook(injector, element));
			else if(type.equals("method"))
				hooks.add(new MethodHook(injector, element));
			else if(type.equals("callback"))
				hooks.add(new CallbackHook(injector, element));
			else if(type.equals("bytecode"))
				hooks.add(new BytecodeHook(injector, element));
			progress++;
			if(loginUI != null)
				progressBar.setValue(progress);
		}
		return hooks.toArray(new Hook[hooks.size()]);
	}

	public Injector getInjector() {
		return injector;
	}

	public long getBuild() {
		return build;
	}

	public String getVersion() {
		return version;
	}
}