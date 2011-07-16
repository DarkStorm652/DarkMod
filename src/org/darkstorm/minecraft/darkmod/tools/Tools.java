package org.darkstorm.minecraft.darkmod.tools;

import java.awt.Image;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;

public class Tools {
	public static enum OperatingSystem {
		LINUX, SOLARIS, WINDOWS, MAC, UNKNOWN;
	}

	private static long version;

	static {
		String versionString = readVersion();
		try {
			version = Long.valueOf(versionString);
		} catch(Exception exception) {
			version = -1;
		}
	}

	private static String readVersion() {
		File workingDir = getMinecraftDirectory();
		File versionFile = new File(workingDir, "bin/version");
		try {
			DataInputStream inputStream = new DataInputStream(
					new FileInputStream(versionFile));
			String version = inputStream.readUTF();
			inputStream.close();
			return version;
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	private Tools() {
	}

	public static Image getIcon(String name) {
		try {
			return ImageIO.read(Tools.class.getResourceAsStream("/resources/"
					+ name + ".png"));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(250);
		} catch(Exception exception) {}
	}

	public static boolean isRunningFromJar() {
		URL jarLocation = Tools.class.getProtectionDomain().getCodeSource()
				.getLocation();
		String jarLocationString = jarLocation != null ? jarLocation.toString()
				: null;
		return jarLocationString != null && jarLocationString.endsWith(".jar");
	}

	public static File getCurrentJar() {
		return new File(Tools.class.getProtectionDomain().getCodeSource()
				.getLocation().getFile());
	}

	public static long getMinecraftBuild() {
		return version;
	}

	public static File getCurrentDirectory() {
		if(isRunningFromJar())
			return getCurrentJar().getParentFile();
		else
			return new File(".");
	}

	public static File getMinecraftDirectory() {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch(getPlatform()) {
		case LINUX:
		case SOLARIS:
			workingDirectory = new File(userHome, ".minecraft/");
			break;
		case WINDOWS:
			String applicationData = System.getenv("APPDATA");
			if(applicationData != null)
				workingDirectory = new File(applicationData, ".minecraft/");
			else
				workingDirectory = new File(userHome, ".minecraft/");
			break;
		case MAC:
			workingDirectory = new File(userHome,
					"Library/Application Support/minecraft");
			break;
		default:
			workingDirectory = new File(userHome, ".minecraft/");
		}
		if((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
			throw new RuntimeException(
					"The working directory could not be created: "
							+ workingDirectory);
		return workingDirectory;
	}

	public static OperatingSystem getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win"))
			return OperatingSystem.WINDOWS;
		if(osName.contains("mac"))
			return OperatingSystem.MAC;
		if(osName.contains("solaris"))
			return OperatingSystem.SOLARIS;
		if(osName.contains("sunos"))
			return OperatingSystem.SOLARIS;
		if(osName.contains("linux"))
			return OperatingSystem.LINUX;
		if(osName.contains("unix"))
			return OperatingSystem.LINUX;
		return OperatingSystem.UNKNOWN;
	}

	public static String post(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", Integer
					.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection
					.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			StringBuffer response = new StringBuffer();
			String line;
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			String str1 = response.toString();
			return str1;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(connection != null)
				connection.disconnect();
		}
	}
}
