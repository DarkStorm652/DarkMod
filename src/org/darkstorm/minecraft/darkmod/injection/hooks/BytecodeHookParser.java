package org.darkstorm.minecraft.darkmod.injection.hooks;

import java.lang.reflect.Constructor;
import java.util.*;

import org.darkstorm.tools.strings.StringTools;
import org.jdom.Element;

import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;

public class BytecodeHookParser {
	private BytecodeHook hook;
	private ClassGen classGen;
	private MethodGen method;
	private InstructionList instructions;
	private Stack<Integer> constIndexStack;
	private int startingIndex;
	private int insertedCount = 0;

	public BytecodeHookParser(BytecodeHook hook, ClassGen classGen,
			MethodGen method, int startingIndex) {
		this.hook = hook;
		this.classGen = classGen;
		this.method = method;
		instructions = this.method.getInstructionList();
		constIndexStack = new Stack<Integer>();
		this.startingIndex = startingIndex;
	}

	public void parseNext(Element instructionElement) {
		// <instruction type="addconst" const="String" arguments="hello" />
		// <instruction type="GETFIELD" arguments="const" />
		// <instruction type="IFEQ" arguments="pos1" />
		String type = instructionElement.getAttributeValue("type");
		if(type.equals("addconst")) {
			ConstantPoolGen constantPool = classGen.getConstantPool();
			String className = instructionElement.getAttributeValue("const");
			if(className.equals("String")) {
				String value = instructionElement
						.getAttributeValue("arguments");
				constIndexStack.push(constantPool.addString(value));
			} else if(className.equals("Integer")) {
				int value = Integer.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addInteger(value));
			} else if(className.equals("Float")) {
				float value = Float.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addFloat(value));
			} else if(className.equals("Double")) {
				double value = Double.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addDouble(value));
			} else if(className.equals("Long")) {
				long value = Long.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addLong(value));
			} else if(className.equals("Fieldref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] fieldInfo = arguments.split(":");
				constIndexStack.push(constantPool.addFieldref(fieldInfo[0],
						fieldInfo[1], fieldInfo[2]));
			} else if(className.equals("Methodref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] methodInfo = arguments.split(":");
				constIndexStack.push(constantPool.addMethodref(methodInfo[0],
						methodInfo[1], methodInfo[2]));
			} else if(className.equals("InterfaceMethodref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] methodInfo = arguments.split(":");
				constIndexStack.push(constantPool.addInterfaceMethodref(
						methodInfo[0], methodInfo[1], methodInfo[2]));
			}
		} else {
			try {
				Class<?> instructionClass = Class
						.forName("com.sun.org.apache.bcel.internal.generic."
								+ type);
				String argumentsTogether = instructionElement
						.getAttributeValue("arguments");
				String[] arguments = argumentsTogether.split(":");
				Vector<Class<?>> argumentClasses = new Vector<Class<?>>();
				Vector<Object> argumentObjects = new Vector<Object>();
				for(String argument : arguments) {
					if(StringTools.isInteger(argument)) {
						argumentClasses.add(Integer.TYPE);
						argumentObjects.add(Integer.parseInt(argument));
					} else if(argument.startsWith("pos")) {
						argumentClasses.add(InstructionHandle.class);
						int pos = Integer.parseInt(argument.substring(3));
						InstructionHandle handle = instructions
								.getInstructionHandles()[pos + insertedCount];
						argumentObjects.add(handle);
					} else if(argument.equals("const")) {
						argumentClasses.add(Integer.TYPE);
						argumentObjects.add(constIndexStack.pop());
					}
				}
				Constructor<?> constructor = instructionClass
						.getConstructor(argumentClasses.toArray(new Class[0]));
				Instruction instruction = (Instruction) constructor
						.newInstance(argumentObjects.toArray(new Object[0]));
				if(instruction instanceof BranchInstruction)
					instructions.insert(
							instructions.getInstructionHandles()[startingIndex
									+ insertedCount],
							(BranchInstruction) instruction);
				else
					instructions.insert(
							instructions.getInstructionHandles()[startingIndex
									+ insertedCount], instruction);
				insertedCount++;
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private BranchHandle createForcedBranchHandle(BranchInstruction instruction) {
		try {
			Class<? extends BranchHandle> branchHandleClass = BranchHandle.class;
			Constructor<? extends BranchHandle> constructor = branchHandleClass
					.getConstructor(BranchInstruction.class);
			return constructor.newInstance(instruction);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public void finish() {
		instructions.setPositions();
		method.setInstructionList(instructions);
		method.setMaxLocals();
		method.setMaxStack();
		String methodName = method.getName();
		String methodSignature = method.getSignature();
		Method oldMethod = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				oldMethod = method;
		classGen.replaceMethod(oldMethod, method.getMethod());
		classGen.setConstantPool(method.getConstantPool());
	}

	public BytecodeHook getBytecodeHook() {
		return hook;
	}

	public ClassGen getClassGen() {
		return classGen;
	}
}
