package game;

import java.awt.Canvas;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

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
	int arenaHeight = 1000;
	int arenaWidth = 1000;

	//initialize entity stacks
	Stack<Robot> robots = new Stack<Robot>();
	Stack<Projectile> projectiles = new Stack<Projectile>();

	//TODO: speed controls
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
	
	BufferedImage ooe = null;
	BufferedImage shield = null;


	static Hashtable<Integer,String> varHash = new Hashtable<Integer,String>();


	/**
	 * Construct our game and set it running.
	 */
	public Game() {
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
		
		BufferedImage tom = null;
		try {
			tom = ImageIO.read(new File("src/game/tom.png"));
		} catch (IOException e) {
		}

		BufferedImage challenger = null;
		try {
			challenger = ImageIO.read(new File("src/game/doge.png"));
		} catch (IOException e) {
		}
		
		BufferedImage avery = null;
		try {
			avery = ImageIO.read(new File("src/game/avery.jpg"));
		} catch (IOException e) {
		}
		
		BufferedImage mikey = null;
		try {
			mikey = ImageIO.read(new File("src/game/mikey.jpg"));
		} catch (IOException e) {
		}
		
		BufferedImage kai = null;
		try {
			kai = ImageIO.read(new File("src/game/kai.jpg"));
		} catch (IOException e) {
		}

		BufferedImage gun = null;
		try {
			gun = ImageIO.read(new File("src/game/gun.png"));
		} catch (IOException e) {
		}
		
		try {
			ooe = ImageIO.read(new File("src/game/ooe.png"));
		} catch (IOException e) {
		}
		
		try {
			shield = ImageIO.read(new File("src/game/shield.png"));
		} catch (IOException e) {
		}

		//import the programs
//		Program challengerProgram = new Program(new File("bounceRegisters.txt"));
		//Program newguyProgram = new Program(new File("newguy.txt"));
		Program bounceProgram = new Program(Program.createProgram("bounceRegisters.txt"));
		Program followerProgram = new Program(Program.createProgram("gunturret.txt"));

		//add robots to the game
		addRobot(new Robot("KAI", bounceProgram, kai,gun));
		addRobot(new Robot("ZACH", bounceProgram, challenger,gun));
		addRobot(new Robot("CYRUS", followerProgram, tom,gun));
		addRobot(new Robot("MIKEY", bounceProgram, mikey,gun));
		addRobot(new Robot("CYRUS", followerProgram, tom,gun));
		addRobot(new Robot("CYRUS", followerProgram, tom,gun));
		addRobot(new Robot("CYRUS", followerProgram, tom,gun));
//		addRobot(new Robot("MIKEY", bounceProgram, mikey,gun));
//		addRobot(new Robot("MIKEY", bounceProgram, mikey,gun));
//		addRobot(new Robot("MIKEY", bounceProgram, mikey,gun));
//		addRobot(new Robot("ZACH", bounceProgram, challenger,gun));
//		addRobot(new Robot("ZACH", bounceProgram, challenger,gun));
//		addRobot(new Robot("ZACH", bounceProgram, challenger,gun));
//		addRobot(new Robot("KAI", bounceProgram, kai,gun));
//		addRobot(new Robot("KAI", bounceProgram, kai,gun));
//		addRobot(new Robot("KAI", bounceProgram, kai,gun));
//		addRobot(new Robot("AVERY", bounceProgram, avery,gun));
//		addRobot(new Robot("AVERY", bounceProgram, avery,gun));
//		addRobot(new Robot("AVERY", bounceProgram, avery,gun));
//		addRobot(new Robot("AVERY", bounceProgram, avery,gun));

//		addRobot(new Robot("Challenger1", challengerProgram, challenger,gun));
		//addRobot(new Robot("Challenger2", challengerProgram, challenger,gun));
		//addRobot(new Robot("Challenger3", challengerProgram, challenger,gun));
		//addRobot(new Robot("Challenger4", challengerProgram, challenger,gun));

		//addRobot(new Robot("Follower1", followerProgram, testy,gun));
		//addRobot(new Robot("Follower2", followerProgram, testy,gun));
		//addRobot(new Robot("Follower3", followerProgram, testy,gun));
		//addRobot(new Robot("Follower4", followerProgram, testy,gun));
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
		BufferedImage ref = null;
		try {
			ref = ImageIO.read(new File("src/game/bullet.png"));
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
				window.getDrawGraphics().drawImage(robot.image, robot.x, robot.y, null);

				//draw aim reticule
				//TODO: get a graphic for this
				double aimRadians = robot.aim * (Math.PI + Math.PI) / 360;

				int px = (int) (robot.x+2 +(robot.radius/2));
				int py = (int) (robot.y+2 + (robot.radius/2));
				AffineTransform rotate = new AffineTransform();
				rotate.rotate(aimRadians+Math.PI);
				window.getDrawGraphics().translate(px, py);
				window.getDrawGraphics().drawImage(robot.gunImage, rotate, null);
				window.getDrawGraphics().translate(-px, -py);
				
				if (robot.shield > 0)
				{
					window.getDrawGraphics().drawImage(shield, robot.x-1, robot.y-1, null);
				}
				if (robot.energy <= 0)//draw over shield if ooe
				{
					window.getDrawGraphics().drawImage(ooe, robot.x-1, robot.y-1, null);
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
					window.getDrawGraphics().drawImage(projectile.image, projectile.x, projectile.y, null);
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
        java.net.URL imgURL = Game.class.getResource(path);
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
