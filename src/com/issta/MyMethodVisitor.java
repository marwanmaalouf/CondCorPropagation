package com.issta;


import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


// TODO: new Throwable print stack trace, get where the oracle is and count from there
// TODO: propagation of oracles to output
// TODO: use the unit tests case of defects4j and use the oracles

public class MyMethodVisitor extends MethodVisitor {

	protected final String methodName;
	protected final String methodSignature;
	protected final String methodIdenifier;
	protected static int count;
	protected int line;
	
	
	private int tempCounter;

	protected final static String StrongOracleIdentifier = "Strong Oracle";


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

		// Stack size handling
		switch(opcode){
		case Opcodes.ILOAD:
		case Opcodes.FLOAD:
		case Opcodes.DLOAD:
		case Opcodes.LLOAD:
		case Opcodes.ALOAD:
			logStackDelta(1);
			break;
		case Opcodes.ISTORE:
		case Opcodes.FSTORE:
		case Opcodes.DSTORE:
		case Opcodes.LSTORE:
		case Opcodes.ASTORE:
			logStackDelta(-1);
			break;
		default: 
		}



		super.visitVarInsn(opcode, var);
		count++;
	}

	// Label instruction not counted as an instruction 
	@Override
	public void visitLabel(Label label) {
		//logInstruction();
		super.visitLabel(label);
		//count++;
	}


	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    
		logInstruction();
		logInvoke();


		// TODO: should we directly add the return to the stack? I would rather not but what if the method accessed is not 
		// instrumented?

		// Stack size handling
		int size = Type.getArgumentsAndReturnSizes(desc);
		int argumentSize = size >>> 2;
		int returnSize =  size & 0x03;
		int delta = returnSize - argumentSize;
		switch(opcode){
		case Opcodes.INVOKESTATIC:
			logStackDelta(delta + 1); // no this
			break;
		default:
			logStackDelta(delta);
		}

		super.visitMethodInsn(opcode, owner, name, desc, itf);
		count++;
	}


	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		logInstruction();
		logInvoke();

		// Stack size handling
		int size = Type.getArgumentsAndReturnSizes(desc);
		int argumentSize = size >>> 2;
		int returnSize =  size & 0x03;
		int delta = returnSize - argumentSize + 1; //no this
		logStackDelta(delta);

		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
		count++;
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		logInstruction();
		logConditional();

		switch(opcode){		
		case Opcodes.IF_ICMPEQ:
		case Opcodes.IF_ICMPNE:
		case Opcodes.IF_ICMPLT:
		case Opcodes.IF_ICMPGE:
		case Opcodes.IF_ICMPGT:
		case Opcodes.IF_ICMPLE:
		case Opcodes.IF_ACMPEQ: 
		case Opcodes.IF_ACMPNE:
			logStackDelta(-2);
			break;
		case Opcodes.IFEQ:
		case Opcodes.IFNE:
		case Opcodes.IFLT:
		case Opcodes.IFGE:
		case Opcodes.IFGT:
		case Opcodes.IFLE:
		case Opcodes.IFNULL:
		case Opcodes.IFNONNULL:
			logStackDelta(-1);
			break;
		case Opcodes.GOTO:
		case Opcodes.JSR: // Hopefully we will never come across one + the return is already added in method call
		default:

		}

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

		// Stack Size handling
		switch(opcode){
		case Opcodes.GETSTATIC:
			logStackDelta(1);
			break;
		case Opcodes.PUTSTATIC:
			logStackDelta(-1);
			break;
		case Opcodes.GETFIELD:
			break;
		case Opcodes.PUTFIELD:
			logStackDelta(-2);
			break;
		}

		super.visitFieldInsn(opcode, owner, name, desc);
		count++;
	}

	@Override
	public void visitInsn(int opcode) {
		logInstruction();
		
		switch(opcode) {
		case Opcodes.IRETURN:
		case Opcodes.FRETURN:
		case Opcodes.ARETURN:
		case Opcodes.LRETURN:
		case Opcodes.DRETURN:
			// the return value is already added to the stack on the method call. Check visitmethodInsn
			logStackDelta(-1);
		case Opcodes.RETURN:
			// TODO: remove the code below and use @After to call the after method
			if(methodName.equals("main")){
			super.visitMethodInsn(
					Opcodes.INVOKESTATIC, "com/issta/Profiler", "printLogBook", 
					Type.getMethodDescriptor(Type.VOID_TYPE), false);
			}
			break;
			
		case Opcodes.NOP:
		case Opcodes.SWAP:
		case Opcodes.INEG: 
		case Opcodes.LNEG: 
		case Opcodes.FNEG: 
		case Opcodes.DNEG: 
		case Opcodes.I2L: 
		case Opcodes.I2F: 
		case Opcodes.I2D: 
		case Opcodes.L2I: 
		case Opcodes.L2F: 
		case Opcodes.L2D: 
		case Opcodes.F2I: 
		case Opcodes.F2L: 
		case Opcodes.F2D: 
		case Opcodes.D2I: 
		case Opcodes.D2L: 
		case Opcodes.D2F: 
		case Opcodes.I2B: 
		case Opcodes.I2C: 
		case Opcodes.I2S:
		case Opcodes.ARRAYLENGTH: 
			break;
			
		case Opcodes.ACONST_NULL:
		case Opcodes.ICONST_M1:
		case Opcodes.ICONST_0:
		case Opcodes.ICONST_1: 
		case Opcodes.ICONST_2: 
	    case Opcodes.ICONST_3: 
		case Opcodes.ICONST_4: 
		case Opcodes.ICONST_5: 
		case Opcodes.LCONST_0: 
		case Opcodes.LCONST_1: 
		case Opcodes.FCONST_0: 
		case Opcodes.FCONST_1: 
		case Opcodes.FCONST_2: 
		case Opcodes.DCONST_0: 
		case Opcodes.DCONST_1: 
		case Opcodes.DUP: 
		case Opcodes.DUP_X1: 
		case Opcodes.DUP_X2: 
		case Opcodes.ATHROW: // TODO: double check if it is +1
			logStackDelta(1);		
			break;
		
			
		case Opcodes.IREM: 
		case Opcodes.LREM: 
		case Opcodes.FREM: 
		case Opcodes.DREM:
			logModulo();
			logStackDelta(-1); // 2 remove 1 add
			break;
			
		case Opcodes.IMUL: 
		case Opcodes.LMUL: 
		case Opcodes.FMUL: 
		case Opcodes.DMUL:
			logMultiply();
			logStackDelta(-1); // 2 remove 1 add
			break;	
		
		case Opcodes.IDIV: 
		case Opcodes.LDIV: 
		case Opcodes.FDIV: 
		case Opcodes.DDIV: 
			logDivide();
		case Opcodes.IALOAD: 
		case Opcodes.LALOAD: 
		case Opcodes.FALOAD: 
		case Opcodes.DALOAD: 
		case Opcodes.AALOAD: 
		case Opcodes.BALOAD: 
		case Opcodes.CALOAD: 
		case Opcodes.SALOAD:
		case Opcodes.POP: 
		case Opcodes.IADD: 
		case Opcodes.LADD: 
		case Opcodes.FADD: 
		case Opcodes.DADD: 
		case Opcodes.ISUB: 
		case Opcodes.LSUB: 
		case Opcodes.FSUB: 
		case Opcodes.DSUB:
		case Opcodes.ISHL: 
		case Opcodes.LSHL: 
		case Opcodes.ISHR: 
		case Opcodes.LSHR: 
		case Opcodes.IUSHR: 
		case Opcodes.LUSHR: 
		case Opcodes.IAND: 
		case Opcodes.LAND: 
		case Opcodes.IOR: 
		case Opcodes.LOR: 
		case Opcodes.IXOR: 
		case Opcodes.LXOR: 
		case Opcodes.LCMP: 
		case Opcodes.FCMPL: 
		case Opcodes.FCMPG: 
		case Opcodes.DCMPL: 
		case Opcodes.DCMPG:
		case Opcodes.MONITORENTER:
		case Opcodes.MONITOREXIT:
			logStackDelta(-1); // 2 remove 1 add
			break;
			
		case Opcodes.IASTORE:
		case Opcodes.LASTORE: 
		case Opcodes.FASTORE: 
		case Opcodes.DASTORE: 
		case Opcodes.AASTORE: 
		case Opcodes.BASTORE: 
		case Opcodes.CASTORE: 
		case Opcodes.SASTORE: 
			logStackDelta(-3);
			break;
			
		case Opcodes.POP2: 
			logStackDelta(-2);
			break;
			
		case Opcodes.DUP2: 
		case Opcodes.DUP2_X1: 
		case Opcodes.DUP2_X2:
			logStackDelta(2);
			break;

		default: // do nothing
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
		
		// Stack size handling
		// no change
		
		super.visitIincInsn(var, increment);
		count++;
	}

	@Override
	public void visitLdcInsn(Object cst) {
		if(cst instanceof String){
			if(((String) cst).contains(StrongOracleIdentifier)){
				logNewOracle((String) cst);
			}
		}
		logInstruction();
		
		// Stack size handling
		logStackDelta(1);
		
		super.visitLdcInsn(cst);
		count++;
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		logInstruction();
		
		switch(opcode){
		case Opcodes.BIPUSH:
		case Opcodes.SIPUSH:
			logStackDelta(1);
			break;
		case Opcodes.NEWARRAY:
			break;
		}
		
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
		
		// Stack size handling
		logStackDelta(-1);
		
		super.visitLookupSwitchInsn(dflt, keys, labels);
		count++;
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		logInstruction();
		
		// Stack size handling
		tempCounter = 0;
		for(int i = 0; i < desc.length(); i++){
			if(desc.charAt(i) == '['){
				tempCounter++;
			}
		}
		logStackDelta(1 - tempCounter);
		
		
		super.visitMultiANewArrayInsn(desc, dims);
		count++;
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		logInstruction();
		
		// Stack size handling
		logStackDelta(-1);
		
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
		
		// Stack size handling
		switch(opcode){
		case Opcodes.NEW:
			logStackDelta(1);
			break;
		case Opcodes.ANEWARRAY:
		case Opcodes.CHECKCAST:
		case Opcodes.INSTANCEOF:
		}
		
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

//	private void logStackEntry(int size){
//		super.visitLdcInsn(size);
//		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logStackEntry", 
//				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
//	}
//
//	private void removeStackEntry(int size){
//		super.visitLdcInsn(size);
//		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "removeStackEntry", 
//				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
//	}
	
	private void logStackDelta(int size){
		super.visitLdcInsn(size);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "logStackDelta", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE), false);
	}
}
