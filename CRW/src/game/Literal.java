package game;

public class Literal {

	public static boolean isLiteral(String instruction) {
		try{
			Integer value = Integer.parseInt(instruction);
			return value instanceof Integer;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}

}
