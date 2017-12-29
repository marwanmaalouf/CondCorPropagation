package com.issta;

import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;

/*
1)	Once an infection is detected (i.e., when a strong oracle is triggered), 
	we collected the entities listed below until the output is reached:

	a.	numStatements, numExecStatements
	b.	numConditionals, numExecConditionals
	c.	numModulo (irem/lrem/frem/drem), , numExecModulo
	d.	numMultiply, numExecMultiply
	e.	numDivide, numExecDidvide
	f.	numInvoke, numExecInvoke
	g.	classStackSize
 */

// TODO: Capture "Strong Oracle" events -- maybe better handled in the MyMethodVisitor part to reduce added code
// TODO: Check if we need to capture all info till Oracle or starting from oracle
// TODO: what is class stack size (what are we trying to capture here)
// TODO: get a sample of a Junit with the oracles to test on
// TODO: When done, remove the static and the printlog, this should be done by calling the before/after test function in the 
//	@After and @Before annotations in Junit

public class Profiler{
	private static HashMap<String, LogInfo> strongOracleLogInfoMap;
	private static HashMap<String, Integer> strongOracleCount;

	// Statements
	private static HashMap<Integer, Integer> instructionCount;
	private static int numExecStatements;

	// Conditionals
	private static HashSet<Integer> conditionalSet;
	private static int numExecConditionals;

	// Modulo
	private static HashSet<Integer> moduloSet;
	private static int numExecModulo;

	// Multiply
	private static HashSet<Integer> multiplySet;
	private static int numExecMultiply;

	// Divide
	private static HashSet<Integer> divideSet;
	private static int numExecDiv;

	// Invoke
	private static HashSet<Integer> invokeSet;
	private static int numExecInvoke;

	// Stack
	private static int classStackSize;

	// Run identifier
	private static String runIdentifier;
	private static String strongOracleIdentifier;

	static{
		beforeTest("testRun");
	}

	public static void logInstruction(int ins){
		if(instructionCount.containsKey(ins)){
			instructionCount.put(ins, instructionCount.get(ins) + 1);
		}else{
			instructionCount.put(ins, 1);
		}
		numExecStatements++;
	}

	public static void logConditional(int insNum){
		conditionalSet.add(insNum);
		numExecConditionals++;
	}

	public static void logModulo(int insNum){
		moduloSet.add(insNum);
		numExecModulo++;
	}

	public static void logMultiply(int insNum){
		multiplySet.add(insNum);
		numExecMultiply++;
	}

	public static void logDivide(int insNum){
		divideSet.add(insNum);
		numExecDiv++;
	}

	public static void logInvoke(int insNum){
		invokeSet.add(insNum);
		numExecInvoke++;
	}

	public static void logStackDelta(int size){
		classStackSize += size;
	}
	
	public static void printLogBook(){
		classStackSize++;
		saveAndReset("1");
		afterTest();
	}


	public static void saveAndReset(String identifier){
		classStackSize--;// to account for the GETSTATIC System.out
		
		LogInfo info = LogInfo.createLogInfo(
				instructionCount.keySet().size(), numExecStatements, 
				conditionalSet.size(), numExecConditionals, 
				moduloSet.size(), numExecModulo, 
				multiplySet.size(),	numExecMultiply, 
				divideSet.size(), numExecDiv, 
				invokeSet.size(), numExecInvoke, 
				classStackSize);

		strongOracleLogInfoMap.put(strongOracleIdentifier, info);
		initialize();
		classStackSize = 1;
		
		strongOracleIdentifier = identifier;

		if(strongOracleCount.containsKey(strongOracleIdentifier)){
			strongOracleCount.put(strongOracleIdentifier, strongOracleCount.get(strongOracleIdentifier) + 1);
		}else{
			strongOracleCount.put(strongOracleIdentifier, 1);
		}
	}

	private static void initialize(){
		instructionCount = new HashMap<>();
		numExecStatements = 0;

		conditionalSet = new HashSet<>();
		numExecConditionals = 0;

		moduloSet = new HashSet<>();
		numExecModulo = 0;

		multiplySet = new HashSet<>();
		numExecMultiply = 0;

		divideSet = new HashSet<>();
		numExecDiv = 0;

		invokeSet = new HashSet<>();
		numExecInvoke = 0;

		//classStackSize = 0;
	}


	public static void beforeTest(String testIdentifier){
		strongOracleLogInfoMap = new HashMap<>();
		strongOracleCount = new HashMap<>();
		strongOracleIdentifier = "None";
		runIdentifier = testIdentifier;
		initialize();
		classStackSize = 0;
	}

	private final static String COMMA_DELIMITER = ",";
	private final static String NEW_LINE_SEPARATOR= "\n";	
	private final static String COLUMNS = "ORACLE,NUM_STAT,NUM_EXECSTAT,NUM_COND,NUM_EXECCOND,"
			+ "NUM_MODULO,NUM_EXECMODULO,"
			+ "NUM_MULT,NUM_EXECMULT,"
			+ "NUM_DIV,NUM_EXECDIV,"
			+ "NUM_INVOKE,NUM_EXECINVOKE,"
			+ "CLASS_STACK_SIZE";
	public static void afterTest(){
		// print to csv file
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(Main._DIRECTORY_OUTPUT + runIdentifier + ".csv");

			//Write the CSV file header
			fileWriter.append(COLUMNS);

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new student object list to the CSV file
			LogInfo temp = null;
			for (String key : strongOracleLogInfoMap.keySet()) {
				temp = strongOracleLogInfoMap.get(key);
				fileWriter.append(key);
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numStatements.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecStatements.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numConditionals.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecConditionals.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numModulo.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecModulo.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numMultiply.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecMultiply.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numDivide.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecDivide.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.numInvoke.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(temp.numExecInvoke.toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(temp.classStackSize.toString());
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter in '" + runIdentifier +"'");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter in '" + runIdentifier +"'");
				e.printStackTrace();
			}
		}
	}
}