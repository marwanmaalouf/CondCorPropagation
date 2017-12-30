package com.issta;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

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

// TODO: new Throwable print stack trace, get where the oracle is and count from there
// TODO: use the unit tests case of defects4j and use the oracles
// TODO: When done, remove the static and the printlog, this should be done by calling the before/after test function in the 
//	@After and @Before annotations in Junit

public class Profiler{
	private static HashMap<String, StrongOracleInfo> strongOracleLogInfoMap;
	private static HashMap<String, Integer> strongOracleCount;

	// Run identifier
	private static String runIdentifier;

	static{
		beforeTest("testRun");
	}

	public static void logInstruction(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logInstruction(ins);
		}
	}

	public static void logConditional(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logConsitionals(ins);
		}
	}

	public static void logModulo(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logModulo(ins);
		}
	}

	public static void logMultiply(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logMultiply(ins);
		}
	}

	public static void logDivide(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logDivide(ins);
		}
	}

	public static void logInvoke(int ins){
		for(String temp : strongOracleLogInfoMap.keySet()){
			strongOracleLogInfoMap.get(temp).logInvoke(ins);
		}
	}

	public static void saveAndReset(String identifier){	
		StrongOracleInfo strongOracle = StrongOracleInfo.createStrongOracleInfo(identifier);
		strongOracleLogInfoMap.put(identifier, strongOracle);
				
		if(strongOracleCount.containsKey(identifier)){
			strongOracleCount.put(identifier, strongOracleCount.get(identifier) + 1);
		}else{
			strongOracleCount.put(identifier, 1);
		}
	}

	public static void beforeTest(String testIdentifier){
		strongOracleLogInfoMap = new HashMap<>();
		strongOracleCount = new HashMap<>();
		runIdentifier = testIdentifier;
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
			fileWriter = new FileWriter(
					//Main._DIRECTORY_OUTPUT + 
					runIdentifier + "_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()) + ".csv");

			//Write the CSV file header
			fileWriter.append(COLUMNS);

			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);

			//Write a new student object list to the CSV file
			StrongOracleInfo temp = null;
			for (String key : strongOracleLogInfoMap.keySet()) {
				temp = strongOracleLogInfoMap.get(key);
				fileWriter.append(key);
				fileWriter.append(COMMA_DELIMITER);
				
				fileWriter.append(((Integer)temp.instructionSet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecStatements).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.conditionalSet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecConditionals).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.moduloSet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecModulo).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.multiplySet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecMultiply).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.divideSet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecDiv).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.invokeSet.size()).toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(((Integer)temp.numExecInvoke).toString());
				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(((Integer)temp.callStackSize).toString());
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