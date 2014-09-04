package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class Program {

	int MAX_LENGTH = 400;

	String[] instructions;
	Hashtable<String,Integer> registers = new Hashtable<String,Integer>();
	//int[] lineNumbers;
	public int address = 0;
	public int lineNumber = 0;
	String currentInstruction;

	public int numberOfInstructions = 0;


	public Program(File code) throws FileNotFoundException
	{
		instructions = new String[MAX_LENGTH];
		registers = new Hashtable<String,Integer>();
		//lineNumbers = new int[MAX_LENGTH];
		Scanner io = new Scanner(code);
		while (io.hasNext())
		{
			currentInstruction = io.next();
			pushInstructions(currentInstruction);
			if (currentInstruction.startsWith("@@"))
			{
				pushRegister(currentInstruction);
			}
		}
		io.close();
	}

//	public int[] getLineNumbers() {
//		return lineNumbers;
//	}

	public void pushInstructions(String i)
	{
		instructions[address] = i;
		numberOfInstructions++;
		address++;
	}

	public void pushRegister(String ci)
	{
		String register = ci.substring(2);
		registers.put(register, address-1);
		lineNumber++;
	}

}
