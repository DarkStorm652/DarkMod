package org.darkstorm.minecraft.darkmod.access.injection.misc;

import java.awt.AWTPermission;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class CustomClassLoader extends URLClassLoader {
	private String minecraftJarName;
	private HashMap<String, byte[]> classes;
	private Vector<String> entryNames;
	private ProtectionDomain domain;

	public CustomClassLoader(String minecraftJarName,
			HashMap<String, byte[]> classes, Vector<String> entryNames,
			URL[] urls) {
		super(urls, ClassLoader.getSystemClassLoader());
		this.minecraftJarName = minecraftJarName;
		this.classes = classes;
		this.entryNames = entryNames;
		try {
			URL location = new File(minecraftJarName).toURI().toURL();
			CodeSource codeSource = new CodeSource(location,
					(CodeSigner[]) null);
			domain = new ProtectionDomain(codeSource,
					getPermissions(codeSource));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if(!classes.containsKey(name))
			return super.findClass(name);
		byte[] classBytes = classes.get(name);
		return defineClass(name, classBytes, 0, classBytes.length, domain);
	}

	@Override
	public URL findResource(String name) {
		if(!entryNames.contains(name))
			super.findResource(name);
		try {
			return new URL("jar:file:" + minecraftJarName + "!/" + name);
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unused")
	private Permissions getPermissions() {
		Permissions permissions = new Permissions();
		permissions.add(new AWTPermission("accessEventQueue"));
		permissions.add(new PropertyPermission("user.home", "read"));
		permissions.add(new PropertyPermission("java.vendor", "read"));
		permissions.add(new PropertyPermission("java.version", "read"));
		permissions.add(new PropertyPermission("os.name", "read"));
		permissions.add(new PropertyPermission("os.arch", "read"));
		permissions.add(new PropertyPermission("os.version", "read"));
		permissions.add(new SocketPermission("*", "connect,resolve"));
		String homeDir = System.getProperty("user.home");
		if(homeDir != null)
			homeDir += "/";
		else
			homeDir = "~/";
		String minecraftDirectory = homeDir + ".minecraft";
		File file = new File(minecraftDirectory);
		permissions.add(new FilePermission(minecraftDirectory, "read"));
		if(!file.exists())
			return permissions;
		minecraftDirectory = file.getPath();
		permissions.add(new FilePermission(minecraftDirectory + File.separator
				+ "-", "read"));
		permissions.add(new FilePermission(minecraftDirectory + File.separator
				+ "-", "write"));
		permissions.setReadOnly();
		return permissions;
	}

	@Override
	public Enumeration<URL> findResources(final String name) throws IOException {
		final Enumeration<URL> otherURLs = super.findResources(name);
		return new Enumeration<URL>() {
			private URL element = findResource(name);

			public boolean hasMoreElements() {
				return element != null || otherURLs.hasMoreElements();
			}

			public URL nextElement() {
				if(element != null) {
					URL element = this.element;
					this.element = null;
					return element;
				}
				return otherURLs.nextElement();
			}
		};
	}
}
