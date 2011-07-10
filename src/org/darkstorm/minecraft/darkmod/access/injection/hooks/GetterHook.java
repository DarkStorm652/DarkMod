package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.*;

public class GetterHook extends Hook {
	private String className, interfaceName, fieldName, fieldSignature,
			returnType, getterName;
	private boolean isStatic;

	public GetterHook(Injector injector, Element element) {
		super(injector, element);
	}

	public String getClassName() {
		return className;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldSignature() {
		return fieldSignature;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getGetterName() {
		return getterName;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		interfaceName = element.getAttributeValue("interface");
		fieldName = element.getAttributeValue("field");
		fieldSignature = element.getAttributeValue("signature");
		isStatic = Boolean.valueOf(element.getAttributeValue("static"));
		returnType = element.getAttributeValue("return");
		getterName = element.getAttributeValue("getter");
	}

	@Override
	protected void inject(ClassGen classGen) {
		ConstantPoolGen cp = classGen.getConstantPool();
		InstructionList iList = new InstructionList();
		Type returnType = getType(this.returnType);
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC, returnType,
				Type.NO_ARGS, new String[] {}, getterName, classGen
						.getClassName(), iList, cp);
		InstructionFactory iFact = new InstructionFactory(classGen, cp);
		Instruction pushThis = new ALOAD(0);
		Instruction get;
		if(isStatic)
			get = iFact.createFieldAccess(className, fieldName, Type
					.getType(fieldSignature), Constants.GETSTATIC);
		else
			get = iFact.createFieldAccess(className, fieldName, Type
					.getType(fieldSignature), Constants.GETFIELD);
		Instruction returner = InstructionFactory.createReturn(returnType);
		iList.append(pushThis);
		iList.append(get);
		if(!fieldSignature.equals(returnType.getSignature()))
			iList.append(iFact.createCast(returnType, Type
					.getType(fieldSignature)));
		iList.append(returner);
		method.setMaxStack();
		method.setMaxLocals();
		classGen.addMethod(method.getMethod());
	}

	private Type getType(String className) {
		if(className.endsWith("]"))
			return getArrayType(className);
		else if(className.equals("boolean"))
			return Type.BOOLEAN;
		else if(className.equals("byte"))
			return Type.BYTE;
		else if(className.equals("short"))
			return Type.SHORT;
		else if(className.equals("int"))
			return Type.INT;
		else if(className.equals("long"))
			return Type.LONG;
		else if(className.equals("float"))
			return Type.FLOAT;
		else if(className.equals("double"))
			return Type.DOUBLE;
		else if(className.equals("char"))
			return Type.CHAR;
		return new ObjectType(className);
	}

	private ArrayType getArrayType(String className) {
		String baseClassName = "";
		int dimensions = 0;
		for(char character : className.toCharArray())
			if(character == '[')
				dimensions++;
			else if(character != ']')
				baseClassName += character;
		Type baseClassType = getType(baseClassName);
		return new ArrayType(baseClassType, dimensions);
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}
}
