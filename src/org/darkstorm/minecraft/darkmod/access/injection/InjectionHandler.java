package org.darkstorm.minecraft.darkmod.access.injection;

import java.awt.*;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.access.injection.hooks.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.minecraft.darkmod.ui.*;
import org.darkstorm.tools.misc.SysTools;

public class InjectionHandler extends AccessHandler {
	private LoginUI loginUI;
	private ClassLoader classLoader;
	private Thread minecraftThread;
	private Minecraft minecraft;
	private Hook[] hooks;

	private long minecraftBuild;
	private String minecraftVersion;

	@Override
	public void load(LoginUI loginUI) {
		this.loginUI = loginUI;
		Injector injector = new Injector(loginUI);
		injector.run();
		classLoader = injector.getClassLoader();
		hooks = injector.getHooks();
		minecraftBuild = injector.getBuild();
		minecraftVersion = injector.getVersion();
	}

	@Override
	public void start() {
		try {
			if(loginUI != null) {
				loginUI.setDialogText("Starting Minecraft...");
				loginUI.getDialogProgressBar().setIndeterminate(true);
			}
			DarkMod darkMod = DarkMod.getInstance();
			DarkModUI darkModUI = darkMod.getUI();
			minecraft = createInstance(darkModUI);
			minecraftThread = new Thread(minecraft, "Minecraft main thread");
			minecraftThread.setPriority(10);
			minecraft.setHomeURL("www.minecraft.net");
			minecraft.setSession(createSession(darkMod));
			darkModUI
					.addWindowListener(createWindowAdapterOverride(minecraftThread));
			minecraftThread.start();
		} catch(Exception exception) {
			exception.printStackTrace();
			SysTools.exit("Could not start minecraft", -1);
		}
	}

	private Minecraft createInstance(DarkModUI darkModUI) {
		try {
			Class<?> minecraftExtensionClass = getClassByInterface(MinecraftExtension.class);
			Constructor<?> constructor = minecraftExtensionClass
					.getConstructor(Component.class, Canvas.class, classLoader
							.loadClass("net.minecraft.client.MinecraftApplet"),
							Integer.TYPE, Integer.TYPE, Boolean.TYPE,
							Frame.class);
			return (Minecraft) constructor.newInstance(new Object[] {
					darkModUI, darkModUI.getCanvas(), null, 854, 480, false,
					darkModUI });
		} catch(Exception exception) {
			exception.printStackTrace();
			SysTools.exit("Unable to instantiate minecraft", -1);
		}
		return null;
	}

	private Session createSession(DarkMod darkMod) {
		Class<?> playerInfoClass = ClassRepository
				.getClassForInterface(org.darkstorm.minecraft.darkmod.hooks.client.Session.class);
		Session session = (Session) ReflectionUtil.instantiate(playerInfoClass,
				new Class<?>[] { String.class, String.class }, darkMod
						.getUsername(), darkMod.getSessionID());
		session.setMultiplayerPassword(darkMod.getPassword());
		return session;
	}

	private WindowListener createWindowAdapterOverride(Thread minecraftThread) {
		try {
			Class<?> windowAdapterOverrideClass = getClassByInterface(WindowAdapterOverride.class);
			Constructor<?> constructor = windowAdapterOverrideClass
					.getConstructor(classLoader
							.loadClass("net.minecraft.client.Minecraft"),
							Thread.class);
			return (WindowListener) constructor.newInstance(minecraft,
					minecraftThread);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	private Class<?> getClassByInterface(Class<?> interfaceClass)
			throws ClassNotFoundException {
		String interfaceName = interfaceClass.getName();
		for(Hook hook : hooks) {
			if(hook instanceof InterfaceHook) {
				InterfaceHook interfaceHook = (InterfaceHook) hook;
				if(interfaceName.equals(interfaceHook.getInterfaceName()))
					return classLoader.loadClass(interfaceHook.getClassName());
			}
		}
		return null;
	}

	@Override
	public Minecraft getMinecraft() {
		return minecraft;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public long getMinecraftBuild() {
		return minecraftBuild;
	}

	@Override
	public String getMinecraftVersion() {
		return minecraftVersion;
	}

}
