package game;

import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import javax.imageio.ImageIO;

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

	//	/** The message to display which waiting for a key press */
	//	private Java2DSprite message;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	//private boolean logicRequiredThisLoop = false;

	/** The time at which the last rendering looped started from the point of view of the game logic */
	//	private long lastLoopTime = System.nanoTime();
	/** The window that is being used to render the game */
	private Java2DGameWindow window;

	/** The time since the last record of fps */
	//	private long lastFpsTime = 0;
	/** The recorded fps */
	//	private int fps;

	private Arena arena;


	//private Java2DSprite robot1;

	/** The normal title of the window */
	private String windowTitle = "ClarkU-RW v 0.1";

	//set up arena and scoreboard
	int arenaHeight = 1000;
	int arenaWidth = 1000;

	//initialize entity stacks
	Stack<Robot> robots = new Stack<Robot>();
	Stack<Projectile> projectiles = new Stack<Projectile>();

	//speed controls
	int time = 0; 

	String[] vars = {	
			"AIM",
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

	static Hashtable<Integer,String> varHash = new Hashtable<Integer,String>();


	/**
	 * Construct our game and set it running.
	 * 
	 * @param renderingType The type of rendering to use (should be one of the contansts from ResourceFactory)
	 */
	public Game() {
		// create a window based on a chosen rendering method

		window = ResourceFactory.get().getGameWindow();

		window.setResolution(1000,1000);
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


		// clear out any existing entities and initialize a new set

		//entities.clear();
	}

	/**
	 * Intialise the common elements for the game
	 */
	public void initialize() {
		//gotYou = ResourceFactory.get().getSprite("sprites/gotyou.gif");
		//pressAnyKey = ResourceFactory.get().getSprite("sprites/pressanykey.gif");
		//youWin = ResourceFactory.get().getSprite("sprites/youwin.gif");

		//message = pressAnyKey;

		// setup the initial game state

		//startGame();

		int i = 0;
		for(String var:vars)
		{
			varHash.put(i, var);
			i++;
		}
	}

	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 * @throws FileNotFoundException 
	 */
	private void initEntities() throws FileNotFoundException {
		// create the robots
		BufferedImage testy = null;
		try {
			testy = ImageIO.read(new File("resources/testy.png"));
		} catch (IOException e) {
		}

		BufferedImage challenger = null;
		try {
			challenger = ImageIO.read(new File("resources/challenger.png"));
		} catch (IOException e) {
		}
		Program challengerProgram = new Program(new File("robots/challenger.txt"));
		Program newguyProgram = new Program(new File("robots/newguy.txt"));
		Program bounceProgram = new Program(new File("robots/bounce.txt"));

		//this.addRobot(new Robot("Robot Two", testyProgram, testy));
//		this.addRobot(new Robot("Robot One", challengerProgram, challenger));
//		//this.addRobot(new Robot("Testy2", testyProgram, testy));
		this.addRobot(new Robot("Challenger2", bounceProgram, challenger));
		this.addRobot(new Robot("Challenger2", bounceProgram, challenger));
		this.addRobot(new Robot("Challenger2", bounceProgram, challenger));
		this.addRobot(new Robot("Challenger2", bounceProgram, challenger));

		this.addRobot(new Robot("Challenger2", challengerProgram, challenger));
		this.addRobot(new Robot("Challenger2", challengerProgram, challenger));
		this.addRobot(new Robot("Challenger2", challengerProgram, challenger));
		this.addRobot(new Robot("Challenger2", challengerProgram, challenger));
//
//		//this.addRobot(new Robot("Testy3", testyProgram, testy));
//		this.addRobot(new Robot("Challenger3", newguyProgram, challenger));
		//this.addRobot(new Robot("Testy4", testyProgram, testy));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
//		this.addRobot(new Robot("Challenger4", newguyProgram, challenger));
	}

	public static boolean isVariable(String instruction) {

		return varHash.contains(instruction);
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

	//public enum PType {RUBBER,NORMAL,EXPLOSIVE,HELLBORE,MINE,NUKE,ION,MISSILE};

	protected static Projectile createProjectile(String type, int energy)
	{
		BufferedImage ref = null;
		try {
			ref = ImageIO.read(new File("resources/bullet.png"));
		} catch (IOException e) {
		}
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
				window.getDrawGraphics().drawImage(robot.image, robot.x, robot.y, null);
				
				double aimRadians = robot.aim * (Math.PI + Math.PI) / 360;

				int px = (int) (robot.x + Math.sin(aimRadians) * (robot.radius+7));
				int py = (int) (robot.y - Math.cos(aimRadians) * (robot.radius+7));
				window.getDrawGraphics().drawImage(robot.image, px, py, null);

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
					window.getDrawGraphics().drawImage(projectile.image, projectile.x, projectile.y, null);
				}
			}
		}

		//boolean colliding = false;
		for (Robot a: robots) {
			for (Robot b: robots) {
				if (a == b) continue;
				if (a.isTouching(b)) {
					a.colliding = b.colliding = true;
					a.vx = b.vx = 0;
					a.vy = b.vy = 0;
					// a.x = a.old_x;
					// a.y = a.old_y;
					// b.x = b.old_x;
					// b.y = b.old_y;
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
					} else {
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
		//		//update score
		//			if (robots.size() > 0 && time < 100000)
		//		{
		//				System.out.println(time);
		//				loop(); //continue
		//			}
		//			else
		//			{
		//		//round is over
		//			}
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






	/**
	 * Notification that a frame is being rendered. Responsible for
	 * running game logic and rendering the scene.
	 */
	public void frameRendering() {		
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
