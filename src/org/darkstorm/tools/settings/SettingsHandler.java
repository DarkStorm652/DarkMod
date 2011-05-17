package org.darkstorm.tools.settings;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.darkstorm.tools.exceptions.NullArgumentException;
import org.darkstorm.tools.io.FileTools;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

/**
 * @created Jun 30, 2010 at 5:48:44 PM
 * @author DarkStorm
 */
public class SettingsHandler {
	private SettingVector settings;
	private SettingVector defaultSettings;
	private XMLOutputter prettyXMLOutputter;
	private File settingsFile;

	private volatile boolean saveSettingsOnExit = true;

	public SettingsHandler(String directory, String fileName) {
		this(directory, fileName, new HashMap<String, String>());
	}

	public SettingsHandler(String directory, String fileName,
			SettingVector defaultSettings) {
		if(fileName == null)
			throw new NullArgumentException(0, "String");
		if(defaultSettings == null)
			throw new NullArgumentException(1, "SettingVector");

		this.defaultSettings = defaultSettings;
		instantiateVariables(directory, fileName);
		init();
	}

	public SettingsHandler(String directory, String fileName,
			Map<String, String> defaultSettings) {
		if(fileName == null)
			throw new IllegalArgumentException("param 0 (type String) is null");
		if(defaultSettings == null)
			throw new IllegalArgumentException(
					"param 1 (type HashMap<String, String>) is null");
		instantiateVariables(directory, fileName);
		this.defaultSettings = produceSettingsFromMap(defaultSettings);
		init();
	}

	private SettingVector produceSettingsFromMap(Map<String, String> settingsMap) {
		SettingVector settings = new SettingVector();
		for(Entry<String, String> entry : settingsMap.entrySet()) {
			String entryKey = entry.getKey();
			String entryValue = entry.getValue();
			if(entryKey == null || entryValue == null)
				continue;
			Setting setting = new Setting(entry.getKey(), entry.getValue());
			settings.add(setting);
		}
		return settings;
	}

	private void instantiateVariables(String directory, String fileName) {
		settings = new SettingVector();
		locateSettingsFileDir(directory, fileName);
		prettyXMLOutputter = new XMLOutputter(Format.getPrettyFormat());
	}

	private void locateSettingsFileDir(String directory, String fileName) {
		File settingsFileDir = FileTools.getDirectoryFormatted(directory);
		if(!settingsFileDir.exists())
			settingsFileDir.mkdirs();
		settingsFile = new File(settingsFileDir.getPath() + "/" + fileName
				+ ".xml");
	}

	private void init() {
		createShutdownHook();
		reloadSettings();
		addMissingDefaultSettings();
	}

	private void createShutdownHook() {
		Runtime runtime = Runtime.getRuntime();
		Runnable shutdownRunnable = new Runnable() {
			@Override
			public void run() {
				if(saveSettingsOnExit)
					saveSettings();
			}
		};
		Thread shutdownHook = new Thread(shutdownRunnable);
		runtime.addShutdownHook(shutdownHook);
	}

	public final void reloadSettings() {
		synchronized(settingsFile) {
			settings.clear();
			checkFile();
			readAndParseDocuments();
			System.gc();
		}
	}

	private void checkFile() {
		if(!settingsFile.exists()) {
			settings.addAll(defaultSettings);
			saveSettings();
		}
	}

	private void readAndParseDocuments() {
		try {
			List<Element> settingElementList = readDocument();
			parseElementsToSettings(settingElementList);
			addMissingDefaultSettings();
		} catch(JDOMException e) {
			System.err.println("The settings file is corrupt or invalid.");
		} catch(IOException exception) {
			System.err.println("Unable to connect/read error");
		}
	}

	@SuppressWarnings("unchecked")
	private List<Element> readDocument() throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(settingsFile);
		Element rootElement = doc.getRootElement();
		return rootElement.getChildren("setting");
	}

	private void parseElementsToSettings(List<Element> settingElementList) {
		for(Element settingElement : settingElementList) {
			Setting setting = createSettingForElement(settingElement);
			settings.add(setting);
		}
	}

	private Setting createSettingForElement(Element element) {
		String key = element.getAttributeValue("key");
		String value = element.getAttributeValue("value");
		if(value == null)
			value = "";
		SettingVector subSettings = readSubSettings(element);
		Setting setting = new Setting(key, value, subSettings);
		return setting;
	}

	private SettingVector readSubSettings(Element settingElement) {
		SettingVector subSettings = new SettingVector();
		Element subSettingContainer = settingElement.getChild("subsettings");
		if(subSettingContainer != null) {
			readSubSettingElements(subSettingContainer, subSettings);
		}
		return subSettings;
	}

	@SuppressWarnings("unchecked")
	private void readSubSettingElements(Element subSettingContainer,
			SettingVector subSettings) {
		List<Element> subSettingElements = subSettingContainer
				.getChildren("setting");
		for(Element subSettingElement : subSettingElements) {
			Setting subSetting = createSettingForElement(subSettingElement);
			subSettings.add(subSetting);
		}
	}

	private void addMissingDefaultSettings() {
		for(Setting defaultSetting : defaultSettings) {
			String defaultSettingKey = defaultSetting.getKey();
			boolean containsDefaultSetting = false;
			for(Setting setting : settings) {
				String settingKey = setting.getKey();
				if(settingKey.equals(defaultSettingKey))
					containsDefaultSetting = true;
			}
			if(!containsDefaultSetting)
				settings.add(defaultSetting);
		}
	}

	public final void saveSettings() {
		synchronized(settingsFile) {
			try {
				Document doc = new Document(new Element("settings"));
				Element rootElement = doc.getRootElement();
				for(Setting setting : settings) {
					if(setting != null && setting.getKey() != null) {
						Element settingElement = createElementForSetting(setting);
						handleSubSettings(setting, settingElement);
						rootElement.addContent(settingElement);
					}
				}
				FileWriter fileWriterToSettingsFile = new FileWriter(
						settingsFile);
				prettyXMLOutputter.output(doc, fileWriterToSettingsFile);
				fileWriterToSettingsFile.close();
			} catch(Exception e) {
				System.err.println("Error while saving settings to "
						+ settingsFile.getPath());
			}
			System.gc();
		}
	}

	private void handleSubSettings(Setting setting, Element settingElement) {
		SettingVector subSettings = setting.getSubSettings();
		if(subSettings.size() > 0) {
			Element subSettingsElement = new Element("subsettings");
			createSubSettingElements(subSettings, subSettingsElement);
			settingElement.addContent(subSettingsElement);
		}
	}

	private void createSubSettingElements(SettingVector subSettings,
			Element subSettingsElement) {
		for(Setting subSetting : subSettings) {
			if(subSetting != null && subSetting.getKey() != null) {
				Element subSettingElement = createElementForSetting(subSetting);
				subSettingsElement.addContent(subSettingElement);
			}
		}
	}

	private Element createElementForSetting(Setting setting) {
		Element settingElement = new Element("setting");
		settingElement.setAttribute("key", setting.getKey());
		String settingValue = setting.getValue();
		if(settingValue != null && !settingValue.equals(""))
			settingElement.setAttribute("value", setting.getValue());
		return settingElement;
	}

	public SettingVector getSettings() {
		return settings;
	}

	public void setSaveSettingsOnExit(boolean saveSettingsOnExit) {
		this.saveSettingsOnExit = saveSettingsOnExit;
	}

	public boolean isSaveSettingsOnExit() {
		return saveSettingsOnExit;
	}

}