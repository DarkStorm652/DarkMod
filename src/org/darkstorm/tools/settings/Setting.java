package org.darkstorm.tools.settings;

import org.darkstorm.tools.exceptions.NullArgumentException;

public class Setting {
	private SettingVector subSettings;
	private String key, value;

	public Setting(String key, String value) {
		this(key, value, null);
	}

	public Setting(String key, String value, SettingVector subSettings) {
		if(key == null)
			throw new NullArgumentException(0, "String");
		if(value == null)
			throw new NullArgumentException(1, "String");
		if(subSettings == null)
			this.subSettings = new SettingVector();
		else
			this.subSettings = subSettings;
		this.key = key;
		this.value = value;

	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SettingVector getSubSettings() {
		return subSettings;
	}

	@Override
	public String toString() {
		int subSettingCount = subSettings.size();
		String stringValue = key + " = " + value;
		if(subSettingCount > 0)
			stringValue += " (" + subSettingCount + " subsettings)";
		return stringValue;
	}

}
