package org.darkstorm.minecraft.darkmod.injection.misc;

import java.util.Vector;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

public class ClassVector extends Vector<ClassGen> {
	private static final long serialVersionUID = 8663919436007878267L;

	public ClassGen getByName(String name) {
		if(name == null)
			return null;
		for(Object o : elementData) {
			if(o != null) {
				ClassGen cg = (ClassGen) o;
				if(cg.getClassName().equals(name))
					return cg;
			}
		}
		return null;
	}

	public boolean containsByName(String name) {
		if(name == null)
			return false;
		for(Object o : elementData) {
			if(o != null) {
				ClassGen cg = (ClassGen) o;
				if(cg.getClassName().equals(name))
					return true;
			}
		}
		return false;
	}

	public ClassGen getByInterface(String interfaceName) {
		if(interfaceName == null)
			return null;
		for(ClassGen classGen : this)
			for(String classInterfaceName : classGen.getInterfaceNames())
				if(interfaceName.equals(classInterfaceName))
					return classGen;
		return null;
	}

}
