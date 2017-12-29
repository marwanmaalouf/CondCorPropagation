package com.issta;

public class LogInfo{
	final Integer numStatements;
	final Integer numExecStatements;
	final Integer numConditionals;
	final Integer numExecConditionals;
	final Integer numModulo; // (irem/lrem/frem/drem)
	final Integer numExecModulo;
	final Integer numMultiply;
	final Integer numExecMultiply;
	final Integer numDivide;
	final Integer numExecDivide;
	final Integer numInvoke;
	final Integer numExecInvoke;
	final Integer classStackSize;
	
	private LogInfo(int numStatements,
			int numExecStatements,
			int numConditionals,
			int numExecConditionals,
			int numModulo, // (irem/lrem/frem/drem)
			int numExecModulo,
			int numMultiply,
			int numExecMultiply,
			int numDivide,
			int numExecDivide,
			int numInvoke,
			int numExecInvoke,
			int classStackSize
			) {

		
		this.numStatements = numStatements;
		this.numExecStatements = numExecStatements;
		this.numConditionals = numConditionals;
		this.numExecConditionals = numExecConditionals;
		this.numModulo = numModulo;
		this.numExecModulo = numExecModulo;
		this.numMultiply = numMultiply;
		this.numExecMultiply = numExecMultiply;
		this.numDivide = numDivide;
		this.numExecDivide = numExecDivide;
		this.numInvoke = numInvoke;
		this.numExecInvoke = numExecInvoke;
		this.classStackSize = classStackSize;
	}
	
	public static LogInfo createLogInfo(
			int numStatements,
			int numExecStatements,
			int numConditionals,
			int numExecConditionals,
			int numModulo, // (irem/lrem/frem/drem)
			int numExecModulo,
			int numMultiply,
			int numExecMultiply,
			int numDivide,
			int numExecDivide,
			int numInvoke,
			int numExecInvoke,
			int classStackSize
			) {
		LogInfo logInfo = new LogInfo(numStatements, numExecStatements, numConditionals, numExecConditionals, numModulo, 
				numExecModulo, numMultiply, numExecMultiply, numDivide, numExecDivide, numInvoke, numExecInvoke, classStackSize);
		return logInfo;
	}
	
}