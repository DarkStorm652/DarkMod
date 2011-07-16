package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.darkstorm.minecraft.darkmod.access.injection.misc.ClassVector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;

public class MethodHook extends Hook {
	private String className, interfaceName, methodName, methodSignature,
			newMethodName, newMethodSignature;

	public MethodHook(Injector injector, Element element) {
		super(injector, element);
	}

	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		methodName = element.getAttributeValue("method");
		methodSignature = element.getAttributeValue("signature");
		newMethodName = element.getAttributeValue("new_method");
		newMethodSignature = element.getAttributeValue("new_signature");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	protected void inject(ClassGen classGen) {
		Method methodToCopy = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				methodToCopy = method;
		if(methodToCopy == null)
			return;
		ClassGen interfaceClass = classGen;
		if(methodToCopy.isStatic()) {
			ClassVector classes = injector.getClasses();
			interfaceClass = classes.getByInterface(interfaceName);
		}

		MethodGen newMethod = new MethodGen(Constants.ACC_PUBLIC, Type
				.getReturnType(newMethodSignature), Type
				.getArgumentTypes(newMethodSignature), null, newMethodName,
				interfaceClass.getClassName(), new InstructionList(),
				interfaceClass.getConstantPool());
		InstructionList instructionList = generateCallerMethodBody(
				interfaceClass, methodToCopy, newMethod);
		newMethod.setMaxLocals();
		newMethod.setInstructionList(instructionList);
		newMethod.setMaxLocals();
		newMethod.setMaxStack();
		interfaceClass.addMethod(newMethod.getMethod());
	}

	private InstructionList generateCallerMethodBody(ClassGen classGen,
			Method method, MethodGen newMethod) {
		InstructionList instructionList = new InstructionList();
		InstructionFactory factory = new InstructionFactory(classGen);
		instructionList.append(new ALOAD(0));
		int index = 1;
		for(int i = 0; i < method.getArgumentTypes().length; i++) {
			Type argumentType = newMethod.getArgumentTypes()[i];
			instructionList.append(InstructionFactory.createLoad(argumentType,
					index));
			if(!argumentType.getSignature().equals(
					method.getArgumentTypes()[i].getSignature()))
				instructionList.append(factory.createCast(argumentType, method
						.getArgumentTypes()[i]));
			int indexIncrement = (argumentType == Type.LONG || argumentType == Type.DOUBLE) ? 2
					: 1;
			index += indexIncrement;
		}

		short invokeType = Constants.INVOKEVIRTUAL;
		String methodName = method.getName();
		if(methodName.equals("<init>") || methodName.equals("<clinit>"))
			invokeType = Constants.INVOKESPECIAL;
		else if(method.isStatic())
			invokeType = Constants.INVOKESTATIC;
		instructionList.append(factory.createInvoke(className,
				method.getName(), method.getReturnType(), method
						.getArgumentTypes(), invokeType));
		instructionList.append(InstructionFactory.createReturn(method
				.getReturnType()));
		classGen.setConstantPool(factory.getConstantPool());
		return instructionList;
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

	public String getNewMethodName() {
		return newMethodName;
	}

	public String getNewMethodSignature() {
		return newMethodSignature;
	}
}
