package com.issta;

import java.util.HashSet;

public class OracleInfo{
	// Statements
	HashSet<Integer> instructionSet;
	int numExecStatements;

	// Conditionals
	HashSet<Integer> conditionalSet;
	int numExecConditionals;

	// Modulo
	HashSet<Integer> moduloSet;
	int numExecModulo;

	// Multiply
	HashSet<Integer> multiplySet;
	int numExecMultiply;

	// Divide
	HashSet<Integer> divideSet;
	int numExecDiv;

	// Invoke
	HashSet<Integer> invokeSet;
	int numExecInvoke;

	// Stack
	int callStackSize;
	
	// Identifier
	String strongOracleIdentifier;
	
	
	
	private OracleInfo(String identifier){
		this.strongOracleIdentifier = identifier;
		
		this.instructionSet = new HashSet<>();
		this.numExecStatements = 0;

		this.conditionalSet = new HashSet<>();
		this.numExecConditionals = 0;

		this.moduloSet = new HashSet<>();
		this.numExecModulo = 0;

		this.multiplySet = new HashSet<>();
		this.numExecMultiply = 0;

		this.divideSet = new HashSet<>();
		this.numExecDiv = 0;

		this.invokeSet = new HashSet<>();
		this.numExecInvoke = 0;

		this.callStackSize = 0;
	}
	
	public void logInstruction(int ins){
		this.instructionSet.add(ins);
		this.numExecStatements++;
	}
	
	public void logConsitionals(int ins){
		this.conditionalSet.add(ins);
		this.numExecConditionals++;	
	}
	
	public void logModulo(int ins){
		this.moduloSet.add(ins);
		this.numExecModulo++;
	}
	
	public void logMultiply(int ins){
		this.multiplySet.add(ins);
		this.numExecMultiply++;
	}
	
	public void logDivide(int ins){
		this.divideSet.add(ins);
		this.numExecDiv++;
	}
	
	public void logInvoke(int ins){
		this.invokeSet.add(ins);
		this.numExecInvoke++;
	}		
	
	public static OracleInfo createOracleInfo(String idenitifier){
		return new OracleInfo(idenitifier);
	}
}