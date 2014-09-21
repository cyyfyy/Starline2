package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	boolean gameRunning = true;
	boolean gameStarted = false;
	/** The frame in which we'll display our canvas */
	protected JFrame frame;

	/** The menu bar for options */
	private JMenuBar menuBar;
	//private JMenu menu;
	private JButton menuItem;


	JFileChooser fc;
	private JButton openButton;
	//private JButton saveButton;


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
	Program p1;
	Program p2;
	Program p3;

	/**
	 * Create a new window to render using Java 2D. Note this will
	 * *not* actually cause the window to be shown.
	 */
	public Java2DGameWindow() {
		frame = new JFrame();

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
				if (p1 != null || p2!= null || p3 != null){
					gameStarted = true;
				}
			}

		});

		//add a file chooser
		fc = new JFileChooser();

		openButton = new JButton("Load a Robot...");
		openButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				//Custom button text
				Object[] options = {"Load Robot One",
						"Load Robot Two",
				"Load Robot Three"};
				int n = JOptionPane.showOptionDialog(frame,
						"Which robot do you want to load?",
						"Load Robot...",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]);

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
			}
		});

		//			File f1 = window.fc.getSelectedFile();
		//			String path = f1.getPath();
		//			path = path.replace("\\", File.separator);
		//			try {
		//				addRobot(new Robot("R1",new Program(Program.createProgram(path)), challenger, gun));
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//
		//			//This is where a real application would open the file.
		//			System.out.println("Opening file.");
		//		} else {
		//			System.out.println("Open command cancelled by user.");
		//		}
		//
		//		int returnVal2 = window.fc.showOpenDialog(Game.this);
		//
		//		if (returnVal2 == JFileChooser.APPROVE_OPTION) {
		//			File f2 = window.fc.getSelectedFile();
		//			String path = f2.getPath();
		//			path = path.replace("\\", File.separator);
		//			try {
		//				addRobot(new Robot("R2",new Program(Program.createProgram(path)), tom, gun));
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//
		//			//This is where a real application would open the file.
		//			System.out.println("Opening file.");
		//		} else {
		//			System.out.println("Open command cancelled by user.");
		//		}
		//
		//	}
		menuBar.add(openButton);

		//		//Create the save button.  We use the image from the JLF
		//		//Graphics Repository (but we extracted it from the jar).
		//		saveButton = new JButton("Save a File...",
		//				createImageIcon("images/Save16.gif"));
		//		saveButton.addActionListener(this);
		//
		//		//For layout purposes, put the buttons in a separate panel
		//		JPanel buttonPanel = new JPanel(); //use FlowLayout
		//		buttonPanel.add(openButton);
		//		buttonPanel.add(saveButton);
		//
		//		//Add the buttons and the log to this panel.
		//		add(buttonPanel, BorderLayout.PAGE_START);
		//		add(logScrollPane, BorderLayout.CENTER);

		////Handle save button action.
		//} else if (e.getSource() == saveButton) {
		//int returnVal = fc.showSaveDialog(FileChooserDemo.this);
		//if (returnVal == JFileChooser.APPROVE_OPTION) {
		//File file = fc.getSelectedFile();
		////This is where a real application would save the file.
		//log.append("Saving: " + file.getName() + "." + newline);
		//} else {
		//log.append("Save command cancelled by user." + newline);
		//}
		//log.setCaretPosition(log.getDocument().getLength());
		//}
		//}
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

	/**
	 * Run the main game loop. This method keeps rendering the scene
	 * and requesting that the callback update its screen.
	 */
	private void preGameLoop() {
		while (gameRunning) {
			if(gameStarted)
			{
				break;
			}
			// Get hold of a graphics context for the accelerated 

			// surface and blank it out

			g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,1000,1000);

			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over

			g.dispose();
			strategy.show();

		}
		gameLoop();
	}
	private void gameLoop() {
		while (gameStarted) {
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

