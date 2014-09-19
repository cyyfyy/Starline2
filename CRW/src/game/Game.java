package game;

import java.awt.Canvas;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import projectiles.ExplosiveBullet;
import projectiles.Hellbore;
import projectiles.Ion;
import projectiles.Mine;
import projectiles.Missile;
import projectiles.NormalBullet;
import projectiles.RubberBullet;
import projectiles.TacNuke;

/**
 * The main hook of the game.
 */
@SuppressWarnings("serial")
public class Game extends Canvas implements GameWindowCallback {

	/** The window that is being used to render the game */
	private Java2DGameWindow window;

	private Arena arena;

	/** The normal title of the window */
	private String windowTitle = "Starline2_0_1";

	//set up arena
	//TODO: Scoreboard
	int arenaHeight = 300;
	int arenaWidth = 300;

	//initialize entity stacks
	Stack<Robot> robots = new Stack<Robot>();
	Stack<Projectile> projectiles = new Stack<Projectile>();

	//TODO: speed controls
	int time = 0; 

	String[] vars = {	
			"AIM",
			"ADDAIM",
			"BULLET",
			"BOTTOM",
			"BOT",
			"CHANNEL",
			"CHRONON",
			"COLLISION",
			"DAMAGE",
			"DOPPLER",
			"DRONE",
			"ENERGY",
			"FIRE",
			"FRIEND",
			"HISTORY",
			"HELLBORE",
			"ICON0",
			"ICON1",
			"ICON2",
			"ICON3",
			"ICON4",
			"ICON5",
			"ICON6",
			"ICON7",
			"ICON8",
			"ICON9",
			"ID",
			"KILLS",
			"LASER",
			"LEFT",
			"LOOK",
			"MINE",
			"MISSILE",
			"MOVEX",
			"MOVEY",
			"NUKE",
			"PROBE",
			"RADAR",
			"RANDOM",
			"RANGE",
			"RIGHT",
			"ROBOTS",
			"SCAN",
			"SHIELD",
			"SIGNAL",
			"SND0",
			"SND1",
			"SND2",
			"SND3",
			"SND4",
			"SND5",
			"SND6",
			"SND7",
			"SND8",
			"SND9",
			"SPEEDX",
			"SPEEDY",
			"STUNNER",
			"TEAMMATES",
			"TOP",
			"WALL",
			"X",
			"Y"
	};

	ImageIcon ooe = null;
	ImageIcon shield = null;
	static ImageIcon ref = null;

	//Robot robot1;
	//Robot robot2;


	static Hashtable<Integer,String> varHash = new Hashtable<Integer,String>();


	/**
	 * Construct our game and set it running.
	 */
	public Game() {
		window = ResourceFactory.get().getGameWindow();

		window.setResolution(300,300);
		window.setGameWindowCallback(this);
		window.setTitle(windowTitle);
	}

	public void startGame() {
		arena = new Arena(this, arenaHeight, arenaWidth);
		try {
			initEntities();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		window.startRendering();
	}

	/**
	 * Initialize the variable hash
	 */
	public void initialize() {

		int i = 0;
		for(String var:vars)
		{
			varHash.put(i, var);
			i++;
		}

	}

	/**
	 * Initialize the starting state of the entities. Each
	 * entity will be added to the overall list of entities in the game.
	 * @throws FileNotFoundException 
	 */
	private void initEntities() throws FileNotFoundException {
		// create the robots
		//TODO: change robot graphics to odd pixel values

		ImageIcon tom = createImageIcon("tom.png");
		ImageIcon challenger = createImageIcon("doge.png");
		ImageIcon gun = createImageIcon("gun.png");
		ooe = createImageIcon("ooe.png");
		shield = createImageIcon("shield.png");
		ref = createImageIcon("bullet.png");

		int returnVal = window.fc.showOpenDialog(Game.this);
		
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));
//		addRobot(new Robot("R1",new Program(Program.createProgram("src/game/temp.txt")), challenger, gun));



		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f1 = window.fc.getSelectedFile();
			String path = f1.getPath();
			path = path.replace("\\", File.separator);
			try {
				addRobot(new Robot("R1",new Program(Program.createProgram(path)), challenger, gun));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//This is where a real application would open the file.
			System.out.println("Opening file.");
		} else {
			System.out.println("Open command cancelled by user.");
		}

		int returnVal2 = window.fc.showOpenDialog(Game.this);

		if (returnVal2 == JFileChooser.APPROVE_OPTION) {
			File f2 = window.fc.getSelectedFile();
			String path = f2.getPath();
			path = path.replace("\\", File.separator);
			try {
				addRobot(new Robot("R2",new Program(Program.createProgram(path)), tom, gun));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//This is where a real application would open the file.
			System.out.println("Opening file.");
		} else {
			System.out.println("Open command cancelled by user.");
		}

	}

	public static boolean isVariable(String instruction) {
		return varHash.contains(instruction);
	}

	public static boolean isComment(String instruction) {
		if (instruction != null && instruction.length() > 2)
		{
			if (instruction.startsWith("//"))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isRegister(String instruction) {
		if (instruction != null && instruction.length() > 2)
		{
			if (instruction.startsWith("##"))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isHandler(String instruction) {
		if (instruction != null && instruction.length() > 2)
		{
			if (instruction.startsWith("@@"))
			{
				return true;
			}
		}
		return false;
	}


	protected void addRobot(Robot robot)
	{
		robot.arena = arena;
		robots.push(robot);
	}

	protected void addProjectile(Projectile p)
	{
		projectiles.push(p);
	}

	protected static Projectile createProjectile(String type, int energy)
	{
		switch(type)
		{
		case "RUBBER": return new RubberBullet(energy, ref);
		case "NORMAL": return new NormalBullet(energy, ref);
		case "EXPLOSIVE": return new ExplosiveBullet(energy, ref);
		case "HELLBORE": return new Hellbore(energy, ref);
		case "MINE": return new Mine(energy, ref);
		case "NUKE": return new Missile(energy, ref);
		case "ION": return new Ion(energy, ref);
		case "MISSILE": return new TacNuke(energy, ref);
		default: return null;
		}
	}

	void loop()
	{
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		time++;
		if (time % 50 == 0)
		{
			System.out.println(time/50);
			for(Robot robotScore: robots)
			{
				System.out.println(robotScore.name+ ": "+robotScore.hull);
			}
		}

		//update robots
		for(Robot robot: robots)
		{
			int x = robot.x;
			int y = robot.y;
			int r = robot.radius + 2;
			robot.touchingWall = (x < r || y < r || x > (arenaWidth - r) || y > (arenaHeight - r));


			robot.step();
			if(robot.alive)
			{
				//draw robots
				window.getDrawGraphics().drawImage(robot.image.getImage(), robot.x, robot.y, null);

				//draw aim reticule
				//TODO: get a graphic for this
				double aimRadians = robot.aim * (Math.PI + Math.PI) / 360;

				int px = (int) (robot.x+2 +(robot.radius/2));
				int py = (int) (robot.y+2 + (robot.radius/2));
				AffineTransform rotate = new AffineTransform();
				rotate.rotate(aimRadians+Math.PI);
				window.getDrawGraphics().translate(px, py);
				window.getDrawGraphics().drawImage(robot.gunImage.getImage(), rotate, null);
				window.getDrawGraphics().translate(-px, -py);

				if (robot.shield > 0)
				{
					window.getDrawGraphics().drawImage(shield.getImage(), robot.x-1, robot.y-1, null);
				}
				if (robot.energy <= 0)//draw over shield if ooe
				{
					robot.vx = 0;
					robot.vy = 0;
					window.getDrawGraphics().drawImage(ooe.getImage(), robot.x-1, robot.y-1, null);
				}

			}
		}


		//update projectiles
		for(Projectile projectile: projectiles)
		{
			if (projectile.isActive()){
				projectile.step();
				int x = projectile.x;
				int y = projectile.y;
				int r = projectile.radius;
				if (x < -r || y < -r || x > (arenaWidth + r) || y > (arenaHeight + r))
				{
					projectile.active = false;
				}
				else
				{
					//draw projectiles
					window.getDrawGraphics().drawImage(projectile.image.getImage(), projectile.x, projectile.y, null);
				}
			}
		}

		for (Robot a: robots) {
			for (Robot b: robots) {
				if (a == b) continue;
				if (a.isTouching(b)) {
					a.colliding = b.colliding = true;
					a.vx = b.vx = 0;
					a.vy = b.vy = 0;
				}
			}
			for (Projectile projectile: projectiles) {
				if(!projectile.isActive())
				{
					continue;
				}
				if (a.isTouching(projectile)) {
					projectile.onContact();
					//if (projectile.is_harmful()) {
					projectile.active = false;
					if (projectile.isEmp) {
						a.energy = 0;
					} else if (projectile.isStasis) {
						a.stasis += (int)(projectile.energy / 4);
					} else {//TODO: take damage
						a.hull -= projectile.energy;
						//	System.out.println("hull after hit: " + a.hull);
					}
					//}
				}
			}
			if (a.colliding)
			{
				a.wasColliding = true;
				a.colliding = false;
			}
		}
		//TODO: update scoreboard

		//remove projectiles and robots
		Object[] pro = projectiles.toArray();
		for(Object projectile: pro)
		{
			if(projectile instanceof Projectile){
				if (!((Projectile) projectile).isActive())
				{
					projectiles.remove(projectile);
				}
			}
		}
		Object[] r = robots.toArray();
		for(Object rob: r)
		{
			if(rob instanceof Robot){
				if (!((Robot) rob).alive)
				{
					robots.remove(rob);
				}
			}
		}


	}



	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		URL imgURL = Game.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	/**
	 * Notification that a frame is being rendered. Responsible for
	 * running game logic and rendering the scene.
	 */
	public void frameRendering() {		
		//TODO: implement game ending screen which shows the winner and maybe some stats
		//if (robots.size() > 1)
		//	{
		loop();
		//	}
		//	else
		//	{
		//		System.out.println("Winner is: " + robots.peek().name);
		//		System.exit(0);
		//	}
	}

	/**
	 * Notifcation that the game window has been closed
	 */
	public void windowClosed() {
		System.exit(0);
	}

	/**
	 * This begins the game.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g = new Game();
		g.startGame();
	}
}
