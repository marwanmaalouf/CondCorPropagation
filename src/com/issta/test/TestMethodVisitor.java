package com.issta.test;


import java.util.HashSet;
import java.util.regex.Pattern;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;



public class TestMethodVisitor extends MethodVisitor {

	static final String CLASS_PROFILER = "com/issta/Profiler";
	static final String CLASS_JUNIT_RULE_TESTNAME = "org/junit/rules/TestName";
	static final String CLASS_JAVA_LANG_STRINGBUILDER = "java/lang/StringBuilder";
	static final String CLASS_JAVA_LANG_STRING = "java/lang/String";
	static final String CLASS_JUNIT_FRAMEWORK_TESTCASE = "junit/framework/TestCase";
	
	static final String ASMCLASS_JUNIT_RULE_TESTNAME = "Lorg/junit/rules/TestName;";
	static final String ASMCLASS_JAVA_LANG_STRINGBUILDER = "Ljava/lang/StringBuilder;";
	static final String ASMCLASS_JAVA_LANG_STRING = "Ljava/lang/String;";




	protected final TestClassVisitor cv;
	protected final String className;
	protected boolean isTest;
	protected boolean isTearDown;
	protected boolean isSetUp;
	protected boolean isConstructor;
	protected boolean seenSuperConstructor;

	
	public TestMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc, String signature, String className, String[] exceptions,
		TestClassVisitor cv) {
		super(api, mv);

		
		this.cv = cv;
		this.className = className;
		
		this.seenSuperConstructor = false;
		this.isConstructor = name.equals("<init>");

		this.isSetUp = name.equals("setUp");// handles Junit 3.x
		if(isSetUp){
			cv.foundSetUp = true;
		}

		this.isTearDown = name.equals("tearDown"); // handles Junit 3.x
		if(isTearDown){
			cv.foundTearDown = true;
		}

		this.isTest = false; // only works for junit 4.X
	}

	@Override
	public void visitCode() {
		super.visitCode();
		

		 if(isTearDown){
		 	super.visitMethodInsn(INVOKESTATIC, CLASS_PROFILER, "afterTest", 
		 			Type.getMethodDescriptor(Type.VOID_TYPE), false);
		 }else if(isSetUp){
		 	super.visitTypeInsn(NEW, CLASS_JAVA_LANG_STRINGBUILDER);
		 	super.visitInsn(DUP);
		 	super.visitMethodInsn(INVOKESPECIAL, CLASS_JAVA_LANG_STRINGBUILDER, "<init>", "()V", false);
		 	super.visitLdcInsn(className.replace('/', '.') + ".");
		 	super.visitMethodInsn(INVOKEVIRTUAL, CLASS_JAVA_LANG_STRINGBUILDER, "append", 
		 			"(" + ASMCLASS_JAVA_LANG_STRING + ")" + ASMCLASS_JAVA_LANG_STRINGBUILDER, false);
		 	super.visitVarInsn(ALOAD, 0);

			if(cv.isJunit3){
		 		super.visitMethodInsn(INVOKEVIRTUAL, CLASS_JUNIT_FRAMEWORK_TESTCASE ,"getName", 
					"()" + ASMCLASS_JAVA_LANG_STRING, false);
		 	}else{
		 	super.visitFieldInsn(GETFIELD, className, cv.testFieldName, ASMCLASS_JUNIT_RULE_TESTNAME);
		 	super.visitMethodInsn(INVOKEVIRTUAL, CLASS_JUNIT_RULE_TESTNAME, "getMethodName", 
		 			"()" + ASMCLASS_JAVA_LANG_STRING, false);
		 	}

		 	super.visitMethodInsn(INVOKEVIRTUAL, CLASS_JAVA_LANG_STRINGBUILDER, "append", 
		 			"(" + ASMCLASS_JAVA_LANG_STRING + ")" + ASMCLASS_JAVA_LANG_STRINGBUILDER, false);
		// 	// TODO: do we need the signature?? send it later to the profiler...
		 	super.visitMethodInsn(INVOKEVIRTUAL, CLASS_JAVA_LANG_STRINGBUILDER, "toString",
		 			"()" + ASMCLASS_JAVA_LANG_STRING, false);
		 	super.visitMethodInsn(INVOKESTATIC, CLASS_PROFILER, "beforeTest", 
		 		"(" + ASMCLASS_JAVA_LANG_STRING + ")V", false);
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
		if(opcode == INVOKESPECIAL && isConstructor && !seenSuperConstructor){
			seenSuperConstructor = true;
			if(!cv.isJunit3){
				super.visitVarInsn(ALOAD, 0);
				super.visitTypeInsn(NEW, CLASS_JUNIT_RULE_TESTNAME);
				super.visitInsn(DUP);
				super.visitMethodInsn(INVOKESPECIAL, CLASS_JUNIT_RULE_TESTNAME, 
					"<init>", "()V", false);
				super.visitFieldInsn(PUTFIELD, className, cv.testFieldName, 
					ASMCLASS_JUNIT_RULE_TESTNAME);
			}
		}
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
		if(desc.equals("Lorg/junit/Test;")){
			isTest = true;
		}
		if(desc.equals("Lorg/junit/After;")){
			isTearDown = true;
			cv.foundTearDown = true;
		}
		if(desc.equals("Lorg/junit/Before;")){
			isSetUp = true;
			cv.foundSetUp = true;
		}
		//System.out.println("Annotation: " + desc);
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
