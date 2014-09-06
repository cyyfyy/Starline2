package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Program {

	int MAX_LENGTH = 400;

	String[] instructions;
	Hashtable<String,Integer> registers = new Hashtable<String,Integer>();
	//int[] lineNumbers;
	public int address = 0;
	public int lineNumber = 0;
	String currentLine;
	String currentInstruction;


	public int numberOfInstructions = 0;


	public Program(ArrayList<String> code) throws FileNotFoundException
	{
		instructions = new String[MAX_LENGTH];
		registers = new Hashtable<String,Integer>();
		//lineNumbers = new int[MAX_LENGTH];
		//Scanner io = new Scanner(code);
		Iterator<String> it = code.iterator();
		while (it.hasNext())
		{
			currentLine = it.next();
			ArrayList<String> split = split(currentLine);
			if(split == null)
			{
				continue;
			}
			Iterator<String> it2 = split.iterator();
			while(it2.hasNext())
			{
				currentInstruction = it2.next().trim();
				if(currentInstruction != " ")
				{
					System.out.println(currentInstruction);
					pushInstructions(currentInstruction);
					if (currentInstruction.startsWith("@@"))
					{
						pushRegister(currentInstruction);
					}
				}
				else
				{
					continue;
				}
			}
		}
	}

	//	public int[] getLineNumbers() {
	//		return lineNumbers;
	//	}

	private ArrayList<String> split(String currentLine) {
		ArrayList<String> result = new ArrayList<String>();
		String first,last = null;
		int i = currentLine.indexOf(" ");
		if (i != -1)
		{
			first = currentLine.substring(0,i).trim();
			result.add(first);
			last = currentLine.substring(i).trim();
		}
		else if (i == 0)
		{
			System.out.println("NULL");
			return null;
		}
		else
		{
			if (currentLine.length() <2 )
			{
				
			}
			else
			{
				result.add(currentLine.trim());
			}
		}
		if(last != null)
		{
			result.addAll(split(last));
		}

		return result;
	}

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

	public static ArrayList<String> createProgram(String path)
	{
		InputStream inStream= Program.class.getResourceAsStream(path);
		ArrayList<String> inList = new ArrayList<String>();
		if (inStream != null) {
			BufferedReader in= new BufferedReader(new InputStreamReader(inStream));
			String nextLine;
			try {   // Whole try-catch is a bit of a waste, but needed to satisfy readLine 
				while( (nextLine= in.readLine()) != null) {
					inList.add(nextLine);
				}
			} catch (IOException ex) {
				System.out.println("something bad happened!");
				System.exit(0);
			}
			return inList;
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
