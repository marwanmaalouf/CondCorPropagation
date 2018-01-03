package com.issta.test;


import java.util.HashSet;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;



public class TestMethodVisitor extends MethodVisitor {

	protected final String methodIdentifier;
	protected boolean isInstrumentable;
	protected static final HashSet<String> reservedNames;
	
	static{
		reservedNames = new HashSet<String>();
		reservedNames.add("<init>");
		reservedNames.add("<clinit>");
		reservedNames.add("setUp");
		reservedNames.add("tearDown");
	}
	
	public TestMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String className, String[] exceptions) {
		super(api, mv);
		methodIdentifier = className + "." + name + desc;
		isInstrumentable = !reservedNames.contains(name);
		
	}

	@Override
	public void visitCode() {
		super.visitCode();
		
		if(isInstrumentable){
		// Call Profiler.beforeTest(String testIdentifier)
		super.visitLdcInsn(methodIdentifier);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "beforeTest", 
				Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false);
		}
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
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitLabel(Label label) {
		super.visitLabel(label);
	}


	@Override 
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf){    
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}


	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs){
		super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label){
		super.visitJumpInsn(opcode, label);		
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index){
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc){   		
		super.visitFieldInsn(opcode, owner, name, desc);
		
	}

	@Override
	public void visitInsn(int opcode) {
		switch(opcode){
		case Opcodes.RETURN:
			if(isInstrumentable){
				// Call Profiler.afterTest()
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/issta/Profiler", "afterTest", 
								Type.getMethodDescriptor(Type.VOID_TYPE), false);
				}
			break;	
		}
		super.visitInsn(opcode);
		
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		super.visitFrame(type, nLocal, local, nStack, stack);
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		super.visitIincInsn(var, increment);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		super.visitLdcInsn(cst);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		super.visitIntInsn(opcode, operand);
	}

	@Override 
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return super.visitAnnotation(desc, visible);
	}
	
	@Override
	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
	}

	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
	}
	
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
	}

	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
			int[] index, String desc, boolean visible) {
		return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack, maxLocals);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return super.visitParameterAnnotation(parameter, desc, visible);
	}

	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return super.visitAnnotationDefault();
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		super.visitLineNumber(line, start);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		super.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		super.visitTryCatchBlock(start, end, handler, type);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		super.visitTypeInsn(opcode, type);
	}
}
