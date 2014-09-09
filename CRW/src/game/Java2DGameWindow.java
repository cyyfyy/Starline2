package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * Uses Java 2D rendering to produce the scene.
 */
@SuppressWarnings("serial")
public class Java2DGameWindow extends Canvas implements ActionListener {
	/** The strategy that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;

	/** The frame in which we'll display our canvas */
	protected JFrame frame;

	/** The menu bar for options */
	private JMenuBar menuBar;
	private JMenu menu;
	protected JMenuItem menuItem;

	/** The width of the display */
	private int width;
	/** The height of the display */
	private int height;
	/** The callback which should be notified of events caused by this window */
	private GameWindowCallback callback;
	/** The current accelerated graphics context */
	private Graphics2D g;

	/**
	 * Create a new window to render using Java 2D. Note this will
	 * *not* actually cause the window to be shown.
	 */
	public Java2DGameWindow() {
		frame = new JFrame();
	}

	/**
	 * Set the title that should be displayed on the window
	 * 
	 * @param title The title to display on the window 
	 */
	public void setTitle(String title) {
		frame.setTitle(title);
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

		menuBar = new JMenuBar();
		//Build the first menu.
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"Options menu");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItem = new JMenuItem("Start",
				KeyEvent.VK_T);
		//menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Start the game");
		menu.add(menuItem);

		frame.setJMenuBar(menuBar);
		
		menuItem.setActionCommand("start");
		menuItem.addActionListener(this);

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(1000,1000));
		panel.setLayout(null);


		//	Keyboard.init(this);

		// setup our canvas size and put it into the content of the frame

		setBounds(0,0,width,height);
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

		//gameLoop();
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
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getActionCommand().equals("start"))
		{
			gameLoop();
		}
	}

	/**
	 * Run the main game loop. This method keeps rendering the scene
	 * and requesting that the callback update its screen.
	 */
	private void gameLoop() {
		while (gameRunning) {
			// Get hold of a graphics context for the accelerated 

			// surface and blank it out

			g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,1000,1000);

			if (callback != null) {
				callback.frameRendering();
			}

			// finally, we've completed drawing so clear up the graphics

			// and flip the buffer over

			g.dispose();
			strategy.show();
		}
	}
}

