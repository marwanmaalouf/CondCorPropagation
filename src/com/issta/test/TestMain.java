package com.issta.test;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.io.*;

import java.lang.String;

public class TestMain {

	private static final String _JAR = ".jar";
	private static final String _CLASS = ".class";
	public static final String _DIRECTORY_OUTPUT = "instrumented/";
	private static final String _DIRECTORY_JAR = _DIRECTORY_OUTPUT + "jar/";
	private static final String _DIRECTORY_CLASS = _DIRECTORY_OUTPUT + "classes/";
	private static String _directoryPath;

	protected static void instrumentJarFile(String jarFile, String outputFileName){
		//System.out.println("Loading Jar file: " + jarFile);

		JarFile jis;
		try {
			//System.out.println(jarFile);
			jis = new JarFile(jarFile);
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputFileName));
			Enumeration<JarEntry> entries = jis.entries();

			while (entries.hasMoreElements()) {
				JarEntry inputJarEntry = entries.nextElement(); 
				JarEntry newEntry = null;
				byte [] bytes = null;
				String entryName = inputJarEntry.toString();

				//System.out.println("Loading " + entryName);

				if(entryName.endsWith(_CLASS)){
					InputStream classFileInputStream;
					classFileInputStream = jis.getInputStream(inputJarEntry);
					ClassWriter cw = instrumentClassFile(classFileInputStream, entryName);
					classFileInputStream.close();
					bytes = cw.toByteArray();
				}else{
					InputStream inputJarStream = jis.getInputStream(inputJarEntry);
					int len = inputJarStream.available();
					bytes = new byte[len];
					int nRead = 0;
					int nReadTotal = 0;
					int nOffset = 0;
					while (len > 0)
					{
						nRead = inputJarStream.read(bytes, nOffset, len);
						nReadTotal += nRead;
						nOffset += nRead;
						len -= nRead;
					}
				}
				newEntry = new JarEntry(inputJarEntry.getName());
				
				newEntry.setMethod(java.util.zip.ZipOutputStream.DEFLATED);
				newEntry.setSize(bytes.length);

				jos.putNextEntry(newEntry);
				jos.write(bytes);
				jos.flush();
				jos.closeEntry();
			}
			jos.close();
		} catch (IOException e) {
			System.out.println("Failed to instrument " + jarFile);
			e.printStackTrace();
		}
	}

	private static ClassWriter instrumentClassFile(InputStream in, String className) throws IOException {
		//System.out.println("Starting instrumentation of " + className);
		ClassReader classReader = new ClassReader(in);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		TestClassVisitor myClassVisitor = new TestClassVisitor(Opcodes.ASM5, classWriter);
		classReader.accept(myClassVisitor, 0);
		
		return classWriter;
	}

	private static void instrumentClassFile(String classFile, String outputFileName) {
		try {
			//System.out.println("Loading " + classFile);
			InputStream in = new FileInputStream(classFile);
			ClassWriter classWriter = instrumentClassFile(in, classFile);
			

			final DataOutputStream dout = new DataOutputStream(new FileOutputStream(outputFileName));
			dout.write(classWriter.toByteArray());
		} catch (IOException e) {
			System.out.println("Failed to instrument " + classFile);
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String [] args){
		if (args.length == 0) {
			System.out.println("Provide a path to the class file as argument");
			return;
		}
		
		_directoryPath = Paths.get("").toAbsolutePath().toString() + "/";
		
		
		for(int i = 0; i < args.length; i++){
			String filePath = args[i];
			String [] temp = filePath.split("/");
			String outputFilePath; 
			
			
			//System.out.println("File found: " + filePath);
			
			if(filePath.substring(filePath.length() - 4).equals(_JAR)){
				// outputFilePath = _directoryPath + _DIRECTORY_JAR + temp[temp.length - 1];
				outputFilePath = _directoryPath + temp[temp.length - 1].split(_JAR)[0] 
				 + "_instrumented.jar";
				instrumentJarFile(filePath, outputFilePath);
			}else if(filePath.substring(filePath.length() - 6).equals(_CLASS)){
				outputFilePath = temp[temp.length - 1];
				instrumentClassFile(filePath, outputFilePath);
			}else{
				outputFilePath = _directoryPath + _DIRECTORY_OUTPUT + temp[temp.length - 1]; 
			}
			
			//System.out.println("Output written to " + outputFilePath);
			//System.out.println();
			//System.out.println();
		}
	}
	

	public static void save(File jar, final List<ClassNode> nodes) {
        try {
            try(final JarOutputStream output = new JarOutputStream(new FileOutputStream(jar))) {
                for(ClassNode element : nodes) {
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    output.putNextEntry(new JarEntry(element.name.replaceAll("\\.", "/") + ".class"));
                    output.write(writer.toByteArray());
                    output.closeEntry();
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ClassNode> load(File file) {
        try {
            JarFile jar = new JarFile(file);
            List<ClassNode> list = new ArrayList<>();
            Enumeration<JarEntry> enumeration = jar.entries();
            while(enumeration.hasMoreElements()) {
                JarEntry next = enumeration.nextElement();
                if(next.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(next));
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    list.add(node);
                }
            }
            jar.close();
            return list;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
