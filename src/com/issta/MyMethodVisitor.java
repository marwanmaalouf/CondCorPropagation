package com.issta;


import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MyMethodVisitor extends MethodVisitor {

	protected final String methodName;
	protected final String methodSignature;
	protected final String methodIdenifier;
	protected static int count;
	protected int line;
	
	private final static String StrongOraclePattern;
	private final static Pattern pattern; 

	static{	         
		StrongOraclePattern = ".*strong\\soracle.*";
		pattern = Pattern.compile(StrongOraclePattern, Pattern.CASE_INSENSITIVE);
	}
	
	public MyMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String className, String[] exceptions) {
		super(api, mv);
		methodName = name;
		methodSignature = methodName + desc;
		methodIdenifier = className + "." + methodSignature;
		line = -1;
	}

	@Override
	public void visitCode() {
		super.visitCode();
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
	}

	@Override
	public void visitParameter(String name, int access) {
		super.visitParameter(name, access);

	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		logInstruction();
		super.visitVarInsn(opcode, var);
		count++;
	}

	// Label instruction not counted as an instruction 
	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
	}


	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    
		logInstruction();
		logInvoke();
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		count++;
	}


	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		logInstruction();
		logInvoke();
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		count++;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		logInstruction();
		logConditional();
		super.visitJumpInsn(opcode, label);
		count++;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc){   
		logInstruction();
		super.visitFieldInsn(opcode, owner, name, desc);
		count++;
	}

	@Override
	public void visitInsn(int opcode) {
		logInstruction();
		
		// TODO: remove the code below and use @After to call the after method

		if(methodName.equals("main")){
			switch(opcode) {
			case Opcodes.IRETURN:
			case Opcodes.FRETURN:
			case Opcodes.ARETURN:
			case Opcodes.LRETURN:
			case Opcodes.DRETURN:
			case Opcodes.RETURN:			
				super.visitMethodInsn(
						Opcodes.INVOKESTATIC, "com/issta/Profiler", "afterTest", 
						Type.getMethodDescriptor(Type.VOID_TYPE), false);
				break;
			}
		}

		switch(opcode){
		case Opcodes.IMUL: 
		case Opcodes.LMUL: 
		case Opcodes.FMUL: 
		case Opcodes.DMUL:
			logMultiply();
			break;	
		case Opcodes.IDIV: 
		case Opcodes.LDIV: 
		case Opcodes.FDIV: 
		case Opcodes.DDIV: 
			logDivide();
			break;
		case Opcodes.IREM: 
		case Opcodes.LREM: 
		case Opcodes.FREM: 
		case Opcodes.DREM:
			logModulo();
			break;
		}
		super.visitInsn(opcode);
		count++;
	}

	// Frame not counted as an instruction 
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		//logInstruction();
		super.visitFrame(type, nLocal, local, nStack, stack);
		//count++;
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		logInstruction();
		super.visitIincInsn(var, increment);
		count++;
	}

	@Override
	public void visitLdcInsn(Object cst) {
		if(cst instanceof String){
			String temp = (String) cst;
			if(pattern.matcher(temp).matches()){
				logNewOracle(temp);
			}
		}
		logInstruction();
		super.visitLdcInsn(cst);
		count++;
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		logInstruction();
		super.visitIntInsn(opcode, operand);
		count++;
	}

	// Line instruction not counted as an instruction
	@Override
	public void visitLineNumber(int line, Label start) {
		//logInstruction();
		this.line = line;
		super.visitLineNumber(line, start);
		//count++;
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		logInstruction();
		super.visitLookupSwitchInsn(dflt, keys, labels);
		count++;
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		logInstruction();	
		super.visitMultiANewArrayInsn(desc, dims);
		count++;
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		logInstruction();
		super.visitTableSwitchInsn(min, max, dflt, labels);
		count++;
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		logInstruction();
		super.visitTypeInsn(opcode, type);
		count++;
	}


	// Support functions:

	private void logInstruction(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logInstruction", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logConditional(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logConditional", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logModulo(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logModulo", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logMultiply(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logMultiply", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logDivide(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logDivide", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logInvoke(){
		super.visitLdcInsn(count);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logInvoke", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}

	private void logNewOracle(String oracleIdentifier){
		super.visitLdcInsn(oracleIdentifier);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "saveAndReset", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false);
	}
}
