package org.darkstorm.minecraft.darkmod.injection.hooks;

import org.darkstorm.minecraft.darkmod.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

public abstract class Hook {
	protected Injector injector;
	protected boolean injected;

	public Hook(Injector injector, Element element) {
		this.injector = injector;
		readElement(element);
	}

	protected abstract void readElement(Element element);

	public void attemptInjection(ClassGen classGen) {
		if(isInjectable(classGen)) {
			inject(classGen);
			injected = true;
		}
	}

	protected abstract boolean isInjectable(ClassGen classGen);

	protected abstract void inject(ClassGen classGen);

	public boolean isInjected() {
		return injected;
	}
}
