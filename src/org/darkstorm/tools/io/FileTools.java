package org.darkstorm.tools.io;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import org.darkstorm.tools.strings.StringTools;

public class FileTools {
	public static final String DEFAULT_DIR = "%home%";
	public static final String CURRENT_DIR = "./";

	private FileTools() {
	}

	public static File getDirectoryFormatted(String directory) {
		String formattedDirectory = directory;
		formattedDirectory = replaceDefaultDirs(directory);
		return new File(formattedDirectory).getAbsoluteFile();
	}

	private static String replaceDefaultDirs(String directory) {
		String formattedDirectory = directory;
		String directoryLowerCase = directory.toLowerCase();
		String defaultDirectory = getDefaultDirectory();
		while(directoryLowerCase.contains(DEFAULT_DIR)) {
			int defaultDirIndex = directoryLowerCase.indexOf(DEFAULT_DIR);
			String toReplace = formattedDirectory.substring(defaultDirIndex,
					DEFAULT_DIR.length());
			formattedDirectory = StringTools.replaceFirst(formattedDirectory,
					toReplace, defaultDirectory);
			directoryLowerCase = StringTools.replaceFirst(directoryLowerCase,
					DEFAULT_DIR, defaultDirectory);
		}
		return formattedDirectory;
	}

	public static String getDefaultDirectory() {
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		File defaultDirectory = fileSystemView.getDefaultDirectory();
		return defaultDirectory.getAbsolutePath();
	}
}
