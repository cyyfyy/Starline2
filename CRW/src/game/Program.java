package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Program {

	int MAX_LENGTH = 400;

	String[] instructions;
	int[] lineNumbers;
	//	private String errors;
	public int address = 0;
	public int lineNumber = 0;
	String currentLine;

	public int numberOfInstructions = 0;


	public Program(File code) throws FileNotFoundException
	{
		//		errors = "";
		instructions = new String[MAX_LENGTH];
		lineNumbers = new int[MAX_LENGTH];
		Scanner io = new Scanner(code);
		while (io.hasNext())
		{
			currentLine = io.next();
			pushInstructions(currentLine);
		}

		// this.label_to_address = {};
		// this.address_to_label = {};
		io.close();
	}

	public int[] getLineNumbers() {
		return lineNumbers;
	}

	public void pushInstructions(String i)
	{
		instructions[address] = i;
		numberOfInstructions++;
		address++;
	}

	public void pushLN(int i)
	{
		lineNumbers[lineNumber] = i;
		lineNumber++;
	}

}
