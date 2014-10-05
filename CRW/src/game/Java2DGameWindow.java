package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Uses Java 2D rendering to produce the scene.
 */
@SuppressWarnings("serial")
public class Java2DGameWindow extends Canvas {
	/** The strategy that allows us to use accelerate page flipping */
	protected BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the window is showing */
	boolean gameRunning = true;
	/** True if the game is currently "playing", i.e. the game loop is looping */
	boolean gameStarted = false;
	/** The frame in which we'll display our canvas */
	protected JFrame frame;
	
	protected String error = "";
	protected String winner = "";

	/** The menu bar for options */
	private JMenuBar menuBar;
	private JButton menuItem;
	private JButton stopButton;



	JFileChooser fc;
	private JButton openButton;

	/** The width of the display */
	private int width;
	/** The height of the display */
	private int height;
	/** The callback which should be notified of events caused by this window */
	private GameWindowCallback callback;
	/** The current accelerated graphics context */
	private Graphics2D g;

	File f1;
	File f2;
	File f3;
	File f4;
	Program p1;
	Program p2;
	Program p3;
	Program p4;

	int arenaSize = 300;

	/**
	 * Create a new window to render using Java 2D. Note this will
	 * *not* actually cause the window to be shown.
	 */
	public Java2DGameWindow() {
		frame = new JFrame();
		addMenuBar();


	}

	/**
	 * Set the title that should be displayed on the window
	 * 
	 * @param title The title to display on the window 
	 */
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	private void addMenuBar()
	{
		menuBar = new JMenuBar();

		menuItem = new JButton("Start");
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Start the game");

		menuBar.add(menuItem);


		frame.setJMenuBar(menuBar);

		menuItem.setActionCommand("start");
		menuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (p1 != null || p2!= null || p3 != null || p4 !=null){
					gameStarted = true;
				}
			}

		});

		stopButton = new JButton("Reset");
		stopButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (callback != null)
				{
					callback.reset();
				}
			}
		});

		menuBar.add(stopButton);

		//add a file chooser
		fc = new JFileChooser();

		openButton = new JButton("Load a Robot...");
		openButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Custom button text
				Object[] options = {"Load Robot One",
						"Load Robot Two",
						"Load Robot Three", "Load Robot Four"};
				int n = JOptionPane.showOptionDialog(frame,
						"Which robot do you want to load?",
						"Load Robot...",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]);

				if (n == 0)
				{

					//Handle open button action.
					if (e.getSource() == openButton) {
						int returnVal = fc.showOpenDialog(Java2DGameWindow.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							f1 = fc.getSelectedFile();
							try {
								p1 = new Program(Program.createProgram(f1.getAbsolutePath()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							//This is where a real application would open the file.
							System.out.println("Opening: " + f1.getName() + ".");
						} else {
							System.out.println("Open command cancelled by user.");
						}

					}
				}
				if (n == 1)
				{

					//Handle open button action.
					if (e.getSource() == openButton) {
						int returnVal = fc.showOpenDialog(Java2DGameWindow.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							f2 = fc.getSelectedFile();
							try {
								p2 = new Program(Program.createProgram(f2.getAbsolutePath()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							//This is where a real application would open the file.
							System.out.println("Opening: " + f2.getName() + ".");
						} else {
							System.out.println("Open command cancelled by user.");
						}

					}
				}
				if (n == 2)
				{

					//Handle open button action.
					if (e.getSource() == openButton) {
						int returnVal = fc.showOpenDialog(Java2DGameWindow.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							f3 = fc.getSelectedFile();
							try {
								p3 = new Program(Program.createProgram(f3.getAbsolutePath()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							//This is where a real application would open the file.
							System.out.println("Opening: " + f3.getName() + ".");
						} else {
							System.out.println("Open command cancelled by user.");
						}

					}
				}
				if (n == 3)
				{

					//Handle open button action.
					if (e.getSource() == openButton) {
						int returnVal = fc.showOpenDialog(Java2DGameWindow.this);

						if (returnVal == JFileChooser.APPROVE_OPTION) {
							f4 = fc.getSelectedFile();
							try {
								p4 = new Program(Program.createProgram(f4.getAbsolutePath()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							//This is where a real application would open the file.
							System.out.println("Opening: " + f4.getName() + ".");
						} else {
							System.out.println("Open command cancelled by user.");
						}

					}
				}
			}
		});

		menuBar.add(openButton);
	}

	/**
	 * Set the resolution of the game window. Note, this will only
	 * have effect before the window has been made visible
	 * 
	 * @param x The width of the game display
	 * @param y The height of the game display
	 */
	public void setResolution(int x, int y) {
		width = x;
		height = y;
	}

	//public JMenuBar

	/**
	 * Start the rendering process. This method will not return. 
	 */
	public void startRendering() {
		// get hold the content of the frame and set up the resolution of the game

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(arenaSize+250,arenaSize)); //250 for scoreboard
		panel.setLayout(null);


		//	Keyboard.init(this);

		// setup our canvas size and put it into the content of the frame

		setBounds(0,0,width+250,height); //250 for scoreboard
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're

		// going to do that our self in accelerated mode

		setIgnoreRepaint(true);

		// finally make the window visible 

		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		// add a listener to respond to the user closing the window. If they

		// do we'd like to exit the game
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (callback != null) {
					callback.windowClosed();
				} else {
					System.exit(0);
				}
			}
		});

		// request the focus so key events come to us

		requestFocus();

		// create the buffering strategy which will allow AWT

		// to manage our accelerated graphics

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// if we have a callback registered then notify 

		// it that initialization is taking place

		if (callback != null) {
			callback.initialize();
		}

		// start the game loop

		preGameLoop();
	}

	/**
	 * Register the callback that should be notified of game
	 * window events.
	 * 
	 * @param callback The callback to be notified of display events 
	 */
	public void setGameWindowCallback(GameWindowCallback callback) {
		this.callback = callback;
	}

	//	/**
	//	 * Check if a particular key is pressed
	//	 * 
	//	 * @param keyCode The code associated with the key to check
	//	 * @return True if the specified key is pressed
	//	 */
	//	public boolean isKeyPressed(int keyCode) {
	//		return Keyboard.isPressed(keyCode);
	//	}

	/**
	 * Retrieve the current accelerated graphics context. Note this
	 * method has been made package scope since only the other 
	 * members of the "java2D" package need to access it.
	 * 
	 * @return The current accelerated graphics context for this window
	 */
	Graphics2D getDrawGraphics() {
		return g;
	}

	protected void drawScore(Graphics2D g, Stack<Robot> robots)
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 15));
		for(int i = 0; i < robots.size(); i ++)
		{
			if(i == 0 || i == 1){
				Robot rob = robots.get(i);
				String name = rob.name.substring(0,rob.name.indexOf('.'));
				g.drawString(name.toUpperCase(), 10 + getWidth()/4 * i, 22);
				g.drawString("Energy:" + rob.energy, 10 + getWidth()/4 * i, 40);
				g.drawString("Active:" + rob.alive, 10 + getWidth()/4 * i, 55);
				g.drawString("Hull:" + rob.hull, 10 + getWidth()/4 * i, 70);
				g.drawString("Shield:" + rob.shield, 10 + getWidth()/4 * i, 85);
			}
			else if(i == 2 || i == 3)
			{
				Robot rob = robots.get(i);
				String name = rob.name.substring(0,rob.name.indexOf('.'));
				g.drawString(name.toUpperCase(), 10 + getWidth()/4 * ((i-1)/2), 112);
				g.drawString("Energy:" + rob.energy, 10 + getWidth()/4 * ((i-1)/2), 130);
				g.drawString("Active:" + rob.alive, 10 + getWidth()/4 * ((i-1)/2), 145);
				g.drawString("Hull:" + rob.hull, 10 + getWidth()/4 * ((i-1)/2), 160);
				g.drawString("Shield:" + rob.shield, 10 + getWidth()/4 * ((i-1)/2), 175);
			}
		}
		g.drawString("Winner:" + winner, 10 + getWidth()/4 * 0, 200);
		g.drawString("Error:" + error, 10 + getWidth()/4 * 0, 225);




	}

	/**
	 * Run the main game loop. This method keeps rendering the scene
	 * and requesting that the callback update its screen.
	 */
	void preGameLoop() {
		while (gameRunning) {
			if(gameStarted)
			{
				break;
			}
			// Get hold of a graphics context for the accelerated 

			// surface and blank it out

			g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,arenaSize+250,arenaSize);


			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over

			g.dispose();
			strategy.show();

		}
		gameLoop();
	}
	private void gameLoop() {
		if (callback != null) {
			callback.load();
		}
		while (gameStarted) {
			// Get hold of a graphics context for the accelerated 

			// surface and blank it out

			g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,arenaSize+250,arenaSize);

			if (callback != null) {
				callback.frameRendering();
			}

			// finally, we've completed drawing so clear up the graphics

			// and flip the buffer over

			g.dispose();
			strategy.show();
		}
		preGameLoop();
	}
}

