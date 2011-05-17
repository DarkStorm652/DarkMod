package org.darkstorm.minecraft.darkmod.tools;

import java.lang.reflect.Constructor;

public class ReflectionUtil {
	private ReflectionUtil() {
	}

	public static Object instantiate(Class<?> targetClass) {
		return instantiate(targetClass, new Class<?>[0]);
	}

	public static Object instantiate(Class<?> targetClass,
			Class<?>[] argClasses, Object... arguments) {
		if(argClasses.length == arguments.length)
			try {
				Constructor<?> constructor = targetClass
						.getConstructor(argClasses);
				return constructor.newInstance(arguments);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		return null;
	}
}
