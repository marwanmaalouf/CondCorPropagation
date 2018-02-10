package com.issta.test;

import org.objectweb.asm.*;
import java.io.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestClassVisitor extends ClassVisitor {

    static final String testFieldName;
    private final static String testPattern;
    private final static Pattern pattern; 

    static{
        testFieldName = "testName";
        testPattern = ".*/.*((Test)|(TestBinary)|(TestPermutations)|(AbstractTest)|(TestBase))";          
        pattern = Pattern.compile(testPattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

	String className;
    String superClassName;
	boolean foundTearDown = false;
    boolean foundSetUp = false;
    boolean isJunit3 = false;
    boolean isInstrumentable = true;
	
    public TestClassVisitor(int api) {
        super(api);
    }

    public TestClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {

        className = name;

        isInstrumentable = pattern.matcher(className).matches();
        if(!isInstrumentable){
            System.out.println("Not instrumentable: " + className);
        }

    // System.out.println("Visiting class: " + name);
    //    System.out.println("Class Major Version: " + version);
    //    System.out.println("Super class: " + superName);

        super.visit(version, access, name, signature, superName, interfaces);
 

        if(isInstrumentable){
            // MM: in Junit 3, tests extends TestCase  
            String superClass = superName;
            superClassName = superName;

            while(superClass!= null && !superClass.equals("java/lang/Object")){
               if(superClass.equals(TestMethodVisitor.CLASS_JUNIT_FRAMEWORK_TESTCASE)){
                   isJunit3 = true;
                   break;
                }else{
                    ClassReader cr=null;
                    try {
                    //  InputStream in = new FileInputStream(
                    //      "C:/Users/User/Desktop/test/" +
                    //                superClass
                    //                + ".class"
                    //            );

                    // MM: for jar files

                    // new bash file: -- hardcode path to ../instrumented/profile
                    // 1. go to target
                    // 2. jar tests to Tests.jar
                    // 3. delete test folder
                    // -- run profler in the current directory, reduces move operations
                    // 4. run profiler on test (add Tests.jar to the path) 
                    // 5. jar classes to Classes.jar
                    // 6. delete classes folder
                    // 7. run profiler on classes in the current directory
                    // 8. unzip instrumented jars
                    // 9. go to root directory of version and run tests command


                    // How to fix issues with framework:
                    // ISSUE 1: junit version
                    // before running the tests command, create a script that goes into every version and makes a copy of junit11 to the 
                    // directory where the junit jar file of the current version is. 
                    // Rename junit11 to the junit jar file name
                    // ISSUE 2: dependancy on CC_Profiler.jar
                    // ...
                    // ...
                    // ...
                        cr = new ClassReader(superClass);
                        superClass = cr.getSuperName();

                    } catch (IOException e) {
                        e.printStackTrace();
                        superClass = null;
                    }
                } 
            }


            // MM: if not Junit 3, assume Junit 4 -> need to declare field
            //             @Rule public TestName;
            if(!isJunit3){
                 FieldVisitor fv = super.visitField(1, testFieldName, "Lorg/junit/rules/TestName;", 
                     null, null);
                 fv.visitAnnotation("Lorg/junit/Rule;", true);
                 fv.visitEnd();
            }  
        }
    }

    /**
     * This is the main reason why we extend the ClassVisitor: replace the MethodVisitor by our MethodVisitor
     */
    @Override
    public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions) {
        // System.out.println("Visiting method: " + name + desc);
        // foundTearDown = foundTearDown || name.equals("tearDown");
        // foundSetUp = foundSetUp || name.equals("setUp");
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if(!isInstrumentable){
            return mv;
        }


        return new TestMethodVisitor(api, mv, access, name, desc, signature, className, exceptions, this);
    }

    /**
     * Invoked only when the class being visited is an inner class
     */
    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        // System.out.println("Outer class: " + owner);
        super.visitOuterClass(owner, name, desc);
    }

    /**
     * Invoked when a class level annotation is encountered
     */
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        //System.out.println("Annotation: " + desc);
        return super.visitAnnotation(desc, visible);
    }

    /**
     * When a class attribute is encountered
     */
    @Override
    public void visitAttribute(Attribute attr) {
        // System.out.println("Class Attribute: " + attr.type);
        super.visitAttribute(attr);
    }

    /**
     * When an inner class is encountered
     */
    @Override
    public void visitInnerClass(String name, String outerName,
                                String innerName, int access) {
        // System.out.println("Inner Class: " + innerName + " defined in " + outerName);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    /**
     * When a field is encountered
     */
    @Override
    public FieldVisitor visitField(int access, String name,
                                   String desc, String signature, Object value) {
        // System.out.println("Field: " + name + " " + desc + " value:" + value);
        return super.visitField(access, name, desc, signature, value);
    }


    @Override
    public void visitEnd() {
        // System.out.println("Class ends here");
        if(!foundSetUp && isInstrumentable){
            //System.out.println("Adding setUp Method");
            
            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "setUp", 
                        "()V", null, null);
            mv.visitAnnotation("Lorg/junit/Before;", true);
            mv.visitCode();

            // create and load <className>.<testName>
            mv.visitTypeInsn(Opcodes.NEW, TestMethodVisitor.CLASS_JAVA_LANG_STRINGBUILDER);
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, TestMethodVisitor.CLASS_JAVA_LANG_STRINGBUILDER,
             "<init>", "()V", false);           
            mv.visitLdcInsn(className.replace('/', '.') + ".");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TestMethodVisitor.CLASS_JAVA_LANG_STRINGBUILDER, 
                "append", "(" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING + ")" 
                + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRINGBUILDER, false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            if(isJunit3){ 
              // if junit 3 call getName()
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
                    TestMethodVisitor.CLASS_JUNIT_FRAMEWORK_TESTCASE ,"getName", 
                    "()" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING, false);
            }else{
              // if junit 4 call testName.getMethodName()
                mv.visitFieldInsn(Opcodes.GETFIELD, className, testFieldName, 
                    TestMethodVisitor.ASMCLASS_JUNIT_RULE_TESTNAME);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
                    TestMethodVisitor.CLASS_JUNIT_RULE_TESTNAME, "getMethodName", 
                        "()" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING, false);
            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
                TestMethodVisitor.CLASS_JAVA_LANG_STRINGBUILDER, "append", 
                    "(" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING + ")" + 
                    TestMethodVisitor.ASMCLASS_JAVA_LANG_STRINGBUILDER, false);
            // TODO: do we need the signature?? send it later to the profiler...
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
                TestMethodVisitor.CLASS_JAVA_LANG_STRINGBUILDER, "toString",
                    "()" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING, false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, TestMethodVisitor.CLASS_PROFILER, "beforeTest", 
                "(" + TestMethodVisitor.ASMCLASS_JAVA_LANG_STRING + ")V", false);

            // adding super.setUp call
            if(superClassName != null
                && !superClassName.equals("java/lang/Object")
                && !superClassName.equals(TestMethodVisitor.CLASS_JUNIT_FRAMEWORK_TESTCASE)
                ){
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassName, "setUp", "()V", false);
            }
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        if(!foundTearDown && isInstrumentable){
        	//System.out.println("Adding tearDown Method");
   			MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "tearDown", 
    					"()V", null, null);
   			mv.visitAnnotation("Lorg/junit/After;", true);
    		mv.visitCode();
    		mv.visitMethodInsn(Opcodes.INVOKESTATIC, TestMethodVisitor.CLASS_PROFILER, "afterTest", 
					"()V", false);
	    	mv.visitInsn(Opcodes.RETURN);
    	    mv.visitMaxs(0, 0);
    	    mv.visitEnd();
        }
        super.visitEnd();
    }

    /**
     * When the optional source is encountered
     */
    @Override
    public void visitSource(String source, String debug) {
        // System.out.println("Source: " + source);
        super.visitSource(source, debug);
    }

}
