package org.darkstorm.minecraft.darkmod.injection.hooks;

import org.darkstorm.minecraft.darkmod.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

public class InterfaceHook extends Hook {
	private String className;
	private String interfaceName;

	public InterfaceHook(Injector injector, Element element) {
		super(injector, element);
	}

	public String getClassName() {
		return className;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	protected void inject(ClassGen classGen) {
		classGen.addInterface(interfaceName);
	}
}
