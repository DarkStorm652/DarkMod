package org.darkstorm.minecraft.darkmod.tools;

import java.util.Map;

import org.darkstorm.minecraft.darkmod.hooks.client.Callback;
import org.darkstorm.tools.exceptions.InstanceAlreadyExistsException;
import org.darkstorm.tools.strings.StringTools;

public class ClassRepository {
	private static Map<String, Class<?>> classes;
	private static Map<Class<?>, Class<?>> interfaceToClassMap;

	private ClassRepository() {
	}

	public static void init(Map<String, Class<?>> classes,
			Map<Class<?>, Class<?>> interfaceToClassMap) {
		if(ClassRepository.classes != null
				|| ClassRepository.interfaceToClassMap != null)
			throw new InstanceAlreadyExistsException(
					"Cannot reinit ClassRepository");
		ClassRepository.classes = classes;
		ClassRepository.interfaceToClassMap = interfaceToClassMap;
	}

	public static Class<?> getClassByName(String name) {
		return classes.get(name);
	}

	public static Class<?> getClassForInterface(Class<?> interfaceClass) {
		return interfaceToClassMap.get(interfaceClass);
	}

	public static Class<?> getClassForInterface(String interfaceSimpleName) {
		try {
			String callbackClassName = Callback.class.getName();
			String interfacePackage = StringTools.splitLast(callbackClassName,
					".")[0];
			return interfaceToClassMap.get(Class.forName(interfacePackage + "."
					+ interfaceSimpleName));
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
}
