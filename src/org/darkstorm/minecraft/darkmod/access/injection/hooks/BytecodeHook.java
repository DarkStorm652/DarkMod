package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;

public class BytecodeHook extends Hook {
	private String className;
	private String methodName;
	private String methodSignature;
	private int position;
	private Element[] instructions;

	public BytecodeHook(Injector injector, Element element) {
		super(injector, element);
	}

	public String getClassName() {
		return className;
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

	public Element[] getInstructions() {
		return instructions;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		methodName = element.getAttributeValue("method");
		methodSignature = element.getAttributeValue("signature");
		position = Integer.valueOf(element.getAttributeValue("position"));
		instructions = (Element[]) element.getChildren("instruction").toArray(
				new Element[0]);
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	protected void inject(ClassGen classGen) {
		MethodGen targetMethod = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				targetMethod = new MethodGen(method, className, classGen
						.getConstantPool());
		if(targetMethod == null) {
			Type returnType = Type.getReturnType(methodSignature);
			Type[] argumentTypes = Type.getArgumentTypes(methodSignature);
			InstructionList instructions = new InstructionList();
			ConstantPoolGen constantPool = classGen.getConstantPool();
			targetMethod = new MethodGen(Constants.ACC_PUBLIC, returnType,
					argumentTypes, null, methodName, className, instructions,
					constantPool);
		}
		BytecodeHookParser parser = new BytecodeHookParser(this, classGen,
				targetMethod, position);
		for(Element instruction : instructions)
			parser.parseNext(instruction);
		parser.finish();
	}
}
