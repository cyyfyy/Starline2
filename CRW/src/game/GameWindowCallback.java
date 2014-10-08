package game;

public interface GameWindowCallback
{

	public void windowClosed();

	public void initialize();

	public void frameRendering();
	
	public void load();
	
	public void pause();

	public void reset();

}
