package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;

public class CallbackHook extends Hook {
	private String className;
	private String interfaceName;
	private String methodName;
	private String methodSignature;
	private int position;
	private String callbackMethod;

	public CallbackHook(Injector injector, Element element) {
		super(injector, element);
	}

	public String getClassName() {
		return className;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public int getPosition() {
		return position;
	}

	public String getCallbackMethod() {
		return callbackMethod;
	}

	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		interfaceName = element.getAttributeValue("interface");
		methodName = element.getAttributeValue("method");
		methodSignature = element.getAttributeValue("signature");
		position = Integer.valueOf(element.getAttributeValue("position"));
		callbackMethod = element.getAttributeValue("callback");
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	protected void inject(ClassGen classGen) {
		Method originalMethod = null;
		MethodGen methodForCallback = null;
		for(Method method : classGen.getMethods()) {
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature())) {
				originalMethod = method;
				methodForCallback = new MethodGen(method, className, classGen
						.getConstantPool());
			}
		}
		InstructionList instructionList = methodForCallback
				.getInstructionList();
		InstructionFactory factory = new InstructionFactory(classGen);
		Instruction invoke = factory.createInvoke(interfaceName,
				callbackMethod, Type.VOID, new Type[0], Constants.INVOKESTATIC);
		InstructionHandle handleForInsertion = instructionList.findHandle(
				position).getNext();
		instructionList.insert(handleForInsertion, invoke);
		instructionList.setPositions();
		methodForCallback.setInstructionList(instructionList);
		methodForCallback.setMaxLocals();
		methodForCallback.setMaxStack();
		classGen.replaceMethod(originalMethod, methodForCallback.getMethod());
	}

}
