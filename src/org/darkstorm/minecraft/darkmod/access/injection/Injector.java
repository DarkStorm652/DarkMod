package org.darkstorm.minecraft.darkmod.access.injection;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.*;

import javax.swing.*;

import org.darkstorm.minecraft.darkmod.access.injection.hooks.*;
import org.darkstorm.minecraft.darkmod.access.injection.misc.*;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.minecraft.darkmod.ui.LoginUI;
import org.darkstorm.tools.misc.SysTools;

import com.sun.org.apache.bcel.internal.classfile.*;
import com.sun.org.apache.bcel.internal.generic.*;

public class Injector {
	private LoginUI loginUI;
	private String minecraftJarName;
	private Vector<String> entryNames;
	private ClassVector classes;
	private ClassLoader classLoader;
	private Hook[] hooks;

	private long build;
	private String version;

	private Map<String, String> interfaceToClassNamesMap = new HashMap<String, String>();

	public Injector(LoginUI loginUI) {
		this.loginUI = loginUI;
	}

	public void run() {
		if(loginUI != null) {
			JButton dialogButton1 = loginUI.getDialogButton1();
			JButton dialogButton2 = loginUI.getDialogButton2();
			JProgressBar progressBar = loginUI.getDialogProgressBar();
			dialogButton1.setVisible(false);
			dialogButton2.setVisible(false);
			progressBar.setVisible(true);
			progressBar.setValue(progressBar.getMinimum());
			progressBar.setIndeterminate(false);
		}
		loadJar();
		loadHooks();
		injectHooks();
		dumpJar();
		loadClasses();
	}

	private void loadJar() {
		try {
			JarFile minecraftJar = locateMinecraftJar();
			if(loginUI != null)
				loginUI.setDialogText("Loading jar...");
			minecraftJarName = minecraftJar.getName();
			classes = new ClassVector();
			entryNames = new Vector<String>();
			Vector<JarEntry> listedEntries = new Vector<JarEntry>();
			Enumeration<?> enumeratedEntries = minecraftJar.entries();
			while(enumeratedEntries.hasMoreElements()) {
				JarEntry entry = (JarEntry) enumeratedEntries.nextElement();
				listedEntries.add(entry);
			}
			JProgressBar progressBar = null;
			if(loginUI != null) {
				progressBar = loginUI.getDialogProgressBar();
				progressBar.setMinimum(0);
				progressBar.setMaximum(listedEntries.size());
			}
			int progress = 0;
			for(JarEntry entry : listedEntries) {
				entryNames.add(entry.getName());
				if(entry.getName().endsWith(".class")) {
					ClassParser entryClassParser = new ClassParser(
							minecraftJar.getInputStream(entry), entry.getName());
					JavaClass parsedClass = entryClassParser.parse();
					ClassGen classGen = new ClassGen(parsedClass);
					classes.add(classGen);
				}
				progress++;
				if(loginUI != null)
					progressBar.setValue(progress);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
			SysTools.exit("Unable to load classes from jar", -1);
		}
	}

	private JarFile locateMinecraftJar() {
		if(loginUI != null) {
			loginUI.setDialogText("Locating minecraft directory...");
			loginUI.getDialogProgressBar().setIndeterminate(true);
		}
		File workingDir = Tools.getMinecraftDirectory();
		String path = workingDir.getAbsolutePath() + "/bin/minecraft.jar";
		minecraftJarName = path;
		File file = new File(path);
		if(!file.exists())
			SysTools.exit("Minecraft jar does not exist", -1);
		if(loginUI != null)
			loginUI.getDialogProgressBar().setIndeterminate(false);
		try {
			return new JarFile(file);
		} catch(IOException exception) {
			exception.printStackTrace();
			SysTools.exit("Unable to load minecraft jar", -1);
		}
		return null;
	}

	private void loadHooks() {
		HookLoader hookLoader = new HookLoader(this);
		hooks = hookLoader.loadHooks(loginUI);
		build = hookLoader.getBuild();
	}

	private void injectHooks() {
		JProgressBar progressBar = null;
		if(loginUI != null) {
			loginUI.setDialogText("Injecting hooks...");
			progressBar = loginUI.getDialogProgressBar();
			progressBar.setIndeterminate(false);
			progressBar.setMinimum(0);
			progressBar.setValue(0);
			progressBar.setMaximum(classes.size() * hooks.length);
		}
		int progress = 0;
		int successful = 0;
		for(ClassGen classGen : classes) {
			for(Hook hook : hooks) {
				if(hook instanceof InterfaceHook) {
					if(!hook.isInjected()) {
						try {
							hook.attemptInjection(classGen);
						} catch(Exception exception) {
							exception.printStackTrace();
							printFailedHook(hook);
						}
						if(hook.isInjected()) {
							String interfaceName = ((InterfaceHook) hook)
									.getInterfaceName();
							interfaceToClassNamesMap.put(interfaceName,
									classGen.getClassName());
						}
					}
				}
				progress++;
				if(loginUI != null)
					progressBar.setValue(progress);
			}
		}
		Vector<Hook> hooks = new Vector<Hook>();
		if(loginUI != null) {
			progressBar.setValue(0);
			progressBar.setMaximum(classes.size() * hooks.size());
		}
		progress = 0;
		for(Hook hook : this.hooks)
			if(!(hook instanceof InterfaceHook))
				hooks.add(hook);
		for(ClassGen classGen : classes) {
			for(Hook hook : hooks) {
				try {
					hook.attemptInjection(classGen);
				} catch(Exception exception) {
					exception.printStackTrace();
					printFailedHook(hook);
				}
				progress++;
				if(loginUI != null)
					progressBar.setValue(progress);
			}
		}
		for(Hook hook : this.hooks)
			if(hook.isInjected())
				successful++;
		if(successful < this.hooks.length) {
			System.err.println("Failure to inject all hooks. Hooks injected: "
					+ successful + ", hook count: " + this.hooks.length);
			if(loginUI != null) {
				loginUI.showError("<html><center>Failure injecting hooks.<br/>"
						+ "Your jar may be modified to be<br/>"
						+ "incompatible with DarkMod.</center></html>");
			} else
				System.exit(-1);
		}
	}

	private void printFailedHook(Hook hook) {
		String message = "Failed to inject ";
		if(hook instanceof InterfaceHook) {
			InterfaceHook interfaceHook = (InterfaceHook) hook;
			message += "interface " + interfaceHook.getInterfaceName();
		} else if(hook instanceof GetterHook) {
			GetterHook getterHook = (GetterHook) hook;
			message += "getter " + getterHook.getReturnType() + " "
					+ getterHook.getInterfaceName() + "."
					+ getterHook.getGetterName() + "()";
		} else if(hook instanceof SetterHook) {
			SetterHook setterHook = (SetterHook) hook;
			message += "setter " + setterHook.getInterfaceName() + "."
					+ setterHook.getSetterName() + "("
					+ setterHook.getReturnType() + ")";
		} else if(hook instanceof MethodHook) {
			MethodHook methodHook = (MethodHook) hook;
			message += "method "
					+ Type.getReturnType(methodHook.getNewMethodSignature())
					+ " " + methodHook.getInterfaceName() + "."
					+ methodHook.getNewMethodName() + "(";
			Type[] argumentTypes = Type.getArgumentTypes(methodHook
					.getNewMethodSignature());
			if(argumentTypes.length > 0) {
				message += argumentTypes[0].toString();
				for(int i = 1; i < argumentTypes.length; i++)
					message += ", " + argumentTypes[i].toString();
			}
			message += ")";
		} else if(hook instanceof CallbackHook) {
			CallbackHook callbackHook = (CallbackHook) hook;
			message += "callback to " + callbackHook.getInterfaceName() + "."
					+ callbackHook.getCallbackMethod() + "()";
		} else if(hook instanceof BytecodeHook) {
			BytecodeHook bytecodeHook = (BytecodeHook) hook;
			message += "bytecode to "
					+ Type.getReturnType(bytecodeHook.getMethodSignature())
					+ " " + bytecodeHook.getClassName() + "."
					+ bytecodeHook.getMethodName() + "(";
			Type[] argumentTypes = Type.getArgumentTypes(bytecodeHook
					.getMethodSignature());
			if(argumentTypes.length > 0) {
				message += argumentTypes[0].toString();
				for(int i = 1; i < argumentTypes.length; i++)
					message += ", " + argumentTypes[i].toString();
			}
			message += ")";
		} else
			message += "an unknown hook";
		System.err.println(message);
	}

	/**
	 * For debugging purposes
	 */
	private void dumpJar() {
		try {
			JProgressBar progressBar = null;
			if(loginUI != null) {
				loginUI.setDialogText("Outputting jar...");
				progressBar = loginUI.getDialogProgressBar();
				progressBar.setIndeterminate(false);
				progressBar.setMinimum(0);
				progressBar.setValue(0);
				progressBar.setMaximum(classes.size());
			}
			int progress = 0;
			File file = new File("lib/minecraft_injected.jar");
			FileOutputStream stream = new FileOutputStream(file);
			JarOutputStream out = new JarOutputStream(stream);
			for(ClassGen classGen : classes) {
				JarEntry jarEntry = new JarEntry(classGen.getClassName()
						.replace('.', '/') + ".class");
				out.putNextEntry(jarEntry);
				out.write(classGen.getJavaClass().getBytes());
				progress++;
				if(loginUI != null)
					progressBar.setValue(progress);
			}
			out.close();
			stream.close();
		} catch(Exception e) {}
	}

	private void loadClasses() {
		try {
			JProgressBar progressBar = null;
			if(loginUI != null) {
				loginUI.setDialogText("Loading new jar...");
				progressBar = loginUI.getDialogProgressBar();
				progressBar.setIndeterminate(false);
				progressBar.setMinimum(0);
				progressBar.setValue(0);
				progressBar.setMaximum(classes.size());
			}
			int progress = 0;
			HashMap<String, byte[]> outputClasses = new HashMap<String, byte[]>();
			for(ClassGen classGen : classes) {
				JavaClass javaClass = classGen.getJavaClass();
				outputClasses
						.put(classGen.getClassName(), javaClass.getBytes());
				progress++;
				if(loginUI != null)
					progressBar.setValue(progress);
			}
			URL[] extraJars = locateExtraJars();
			classLoader = new CustomClassLoader(minecraftJarName,
					outputClasses, entryNames, extraJars);
			initClassRepository();
		} catch(Throwable e) {
			e.printStackTrace();
			SysTools.exit("Unable to dump jar", -1);
		}
	}

	private URL[] locateExtraJars() {
		File workingDir = Tools.getMinecraftDirectory();
		String path = workingDir.getAbsolutePath() + "/bin/";
		File dir = new File(path);
		try {
			String[] jarNames = new String[] { "lwjgl.jar", "jinput.jar",
					"lwjgl_util.jar" };
			URL[] urls = new URL[jarNames.length];
			for(int i = 0; i < jarNames.length; i++) {
				String jarName = jarNames[i];
				urls[i] = new File(dir, jarName).toURI().toURL();
			}
			System.setProperty("org.lwjgl.librarypath", path + "/natives");
			System.setProperty("net.java.games.input.librarypath", path
					+ "/natives");
			return urls;
		} catch(Exception exception) {
			exception.printStackTrace();
			SysTools.exit("Could not locate extra jars", -1);
		}
		return null;
	}

	private void initClassRepository() {
		Map<Class<?>, Class<?>> interfaceToClassMap = new HashMap<Class<?>, Class<?>>();
		for(String interfaceName : interfaceToClassNamesMap.keySet()) {
			try {
				Class<?> interfaceClass = Class.forName(interfaceName);
				Class<?> minecraftClass = classLoader
						.loadClass(interfaceToClassNamesMap.get(interfaceName));
				interfaceToClassMap.put(interfaceClass, minecraftClass);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
		Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
		for(ClassGen classGen : this.classes)
			try {
				String className = classGen.getClassName();
				classes.put(className, classLoader.loadClass(className));
			} catch(ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		ClassRepository.init(classes, interfaceToClassMap);
	}

	public ClassVector getClasses() {
		return classes;
	}

	public Hook[] getHooks() {
		return hooks;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public long getBuild() {
		return build;
	}

	public String getVersion() {
		return version;
	}
}
