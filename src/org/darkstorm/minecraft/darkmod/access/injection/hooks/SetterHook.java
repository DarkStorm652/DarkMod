package org.darkstorm.minecraft.darkmod.access.injection.hooks;

import org.darkstorm.minecraft.darkmod.access.injection.Injector;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.generic.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;

public class SetterHook extends Hook {
	private String className, interfaceName, fieldName, fieldSignature,
			argumentType, setterName;
	private boolean isStatic;

	public SetterHook(Injector injector, Element element) {
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

	public boolean isStatic() {
		return isStatic;
	}

	public String getReturnType() {
		return argumentType;
	}

	public String getSetterName() {
		return setterName;
	}

	@Override
	protected void readElement(Element element) {
		className = element.getAttributeValue("class");
		fieldName = element.getAttributeValue("field");
		fieldSignature = element.getAttributeValue("signature");
		isStatic = Boolean.valueOf(element.getAttributeValue("static"));
		argumentType = element.getAttributeValue("argument");
		setterName = element.getAttributeValue("setter");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	protected boolean isInjectable(ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	protected void inject(ClassGen classGen) {
		InstructionFactory factory = new InstructionFactory(classGen);
		InstructionList instructionList = new InstructionList();
		instructionList.append(new ALOAD(0));
		Type fieldType = Type.getType(fieldSignature);
		instructionList.append(InstructionFactory.createLoad(fieldType, 1));
		Type returnType = null;
		try {
			returnType = Type.getType(Class.forName(argumentType));
		} catch(ClassNotFoundException exception) {
			returnType = getType(argumentType);
		}
		String returnTypeSignature = returnType.getSignature();
		String fieldTypeSignature = fieldType.getSignature();
		if(!returnTypeSignature.equals(fieldTypeSignature))
			instructionList.append(factory.createCast(returnType, fieldType));
		instructionList.append(factory.createPutField(classGen.getClassName(),
				fieldName, fieldType));
		instructionList.append(new RETURN());
		MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
				new Type[] { returnType }, new String[] { fieldName },
				setterName, className, instructionList, classGen
						.getConstantPool());
		methodGen.setMaxLocals();
		methodGen.setMaxStack();
		classGen.addMethod(methodGen.getMethod());
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
		return new ArrayType(baseClassName, dimensions);
	}
}
