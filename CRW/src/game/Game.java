package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Stack;
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
	protected static Java2DGameWindow window;

	private Arena arena;

	/** The normal title of the window */
	private String windowTitle = "Starline2_0_2";

	//set up arena
	static int arenaHeight = 300;
	static int arenaWidth = 300;

	//initialize entity stacks
	static Stack<Robot> robots = new Stack<Robot>();
	Stack<Projectile> projectiles = new Stack<Projectile>();

	//TODO: speed controls
	int time = 0; 
	boolean paused = false;

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
			"ION",
			"TEAMMATES",
			"TOP",
			"WALL",
			"X",
			"Y"
	};

	ImageIcon ooe = null;
	ImageIcon shield = null;
	ImageIcon challenger = createImageIcon("challenger.png");
	ImageIcon gun = createImageIcon("gun.png");
	
	static ImageIcon ref = null;
	static ImageIcon explosive = null;
	static ImageIcon det = null;
	static ImageIcon hellbore = null;
	static ImageIcon ion = null;

	

	public Robot robot1;
	public Robot robot2;
	public Robot robot3;
	public Robot robot4;



	static Hashtable<Integer,String> varHash = new Hashtable<Integer,String>();


	/**
	 * Construct our game and set it running.
	 */
	public Game()
	{
		window = ResourceFactory.get().getGameWindow();
		window.setResolution(window.arenaSize,window.arenaSize);
		window.setGameWindowCallback(this);
		window.setTitle(windowTitle);
	}

	public void startGame()
	{
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
		//TODO: change robot graphics to odd pixel values

		ooe = createImageIcon("ooe.png");
		shield = createImageIcon("shield.png");
		ref = createImageIcon("bullet.png");
		explosive = createImageIcon("ex_bullet.png");
		det = createImageIcon("det.png");
		hellbore = createImageIcon("hellbore.png");
		ion = createImageIcon("ion.png");
	}

	public static boolean isVariable(String instruction) 
	{
		return varHash.contains(instruction);
	}

	public static boolean isComment(String instruction)
	{
		if (instruction != null && instruction.length() > 2)
		{
			if (instruction.startsWith("//"))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isRegister(String instruction)
	{
		if (instruction != null && instruction.length() > 2)
		{
			if (instruction.startsWith("##"))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isHandler(String instruction) 
	{
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
		case "EXPLOSIVE": return new ExplosiveBullet(energy, explosive);
		case "HELLBORE": return new Hellbore(energy, hellbore);
		case "MINE": return new Mine(energy, ref);
		case "NUKE": return new Missile(energy, ref);
		case "ION": return new Ion(energy, ion);
		case "MISSILE": return new TacNuke(energy, ref);
		default: return null;
		}
	}

	void loop()
	{
		if(!paused)
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
					//draw robots
					window.getDrawGraphics().drawImage(robot.image.getImage(), robot.x, robot.y, null);

					//draw aim reticle
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
						window.getDrawGraphics().drawImage(ooe.getImage(), robot.x-1, robot.y-1, null);
					}

				}
			}


			//update projectiles
			for(Projectile projectile: projectiles)
			{
				if (projectile.isActive())
				{
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
						if(projectile instanceof ExplosiveBullet && projectile.speedX == 0)
						{
							window.getDrawGraphics().drawImage(det.getImage(), projectile.x - 25, projectile.y - 25, null);
						}
						else
						{
							window.getDrawGraphics().drawImage(projectile.image.getImage(), projectile.x, projectile.y, null);
						}
					}
				}
			}

			for (Robot a: robots) 
			{
				for (Robot b: robots)
				{
					if (a == b) continue;
					if (a.isTouching(b))
					{
						a.colliding = b.colliding = true;
						a.vx = b.vx = 0;
						a.vy = b.vy = 0;
					}
				}
				for (Projectile projectile: projectiles)
				{
					if(!projectile.isActive() && !(projectile instanceof ExplosiveBullet))
					{
						continue;
					}
					if (a.isTouching(projectile))
					{
						projectile.onContact();
						if (projectile.isHarmful()) 
						{
							projectile.active = false;
							if (projectile.isEmp)
							{
								a.energy = 0;
							} 
							else if (projectile.isStasis)
							{
								a.stasis += (projectile.energy / 4);
							} 
							else 
							{
								a.takeDamage(projectile.energy);
							}
						}
					}
				}
				if (a.colliding)
				{
					a.wasColliding = true;
					a.colliding = false;
				}
			}
			
			//remove projectiles and robots
			Object[] pro = projectiles.toArray();
			for(Object projectile: pro)
			{
				if(projectile instanceof Projectile)
				{
					if (!((Projectile) projectile).isActive())
					{
						projectiles.remove(projectile);
					}
				}
			}
			Object[] r = robots.toArray();
			for(Object rob: r)
			{
				if(rob instanceof Robot)
				{
					if (!((Robot) rob).alive)
					{
						robots.remove(rob);
					}
				}
			}
		}
	}



	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path)
	{
		URL imgURL = Game.class.getResource(path);
		if (imgURL != null)
		{
			return new ImageIcon(imgURL);
		}
		else
		{
			window.error = "Couldn't find file: " + path;
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	/**
	 * Notification that a frame is being rendered. Responsible for
	 * running game logic and rendering the scene.
	 */
	public void frameRendering() {		
		
		window.getDrawGraphics().translate(arenaWidth, 0);
		window.drawScore(window.getDrawGraphics(), robots);
		window.getDrawGraphics().translate(-arenaWidth, 0);
		window.getDrawGraphics().drawLine(arenaWidth+5, 0, arenaWidth+5, arenaHeight);

		if (robots.size() > 1)
		{
			loop();
		}
		else if (robots.size() == 1)
		{
			window.winner = robots.peek().name.substring(0, robots.peek().name.indexOf('.')).toUpperCase();
			window.getDrawGraphics().setColor(Color.RED);
			window.getDrawGraphics().drawString("Winner:" + window.winner, arenaWidth + 10, 200);
			window.getDrawGraphics().dispose();
			window.strategy.show();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reset();
		}
		else
		{
			window.winner = "TIED!";
			window.getDrawGraphics().setColor(Color.RED);
			window.getDrawGraphics().drawString("Winner:" + window.winner, arenaWidth + 10, 200);
			window.getDrawGraphics().dispose();
			window.strategy.show();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			reset();
		}
	}

	/**
	 * Notifcation that the game window has been closed
	 */
	public void windowClosed() {
		System.exit(0);
	}

	@Override
	public void load() {
		if(window.p1 != null){
			addRobot(new Robot(window.f1.getName(),window.p1,challenger,gun));
			//window.p1 = null;
		}
		if(window.p2 != null){
			addRobot(new Robot(window.f2.getName(),window.p2,challenger,gun));
			//window.p2 = null;
		}
		if(window.p3 != null){
			addRobot(new Robot(window.f3.getName(),window.p3,challenger,gun));
			//window.p3 = null;
		}
		if(window.p4 != null){
			addRobot(new Robot(window.f4.getName(),window.p4,challenger,gun));
			//window.p4 = null;
		}	
	}
	
	@Override
	public void pause() {
		paused = !paused;
	}

	@Override
	public void reset() {
		window.gameStarted = false;
		paused = true;
		window.winner = "";
		window.error = "";
		robots.clear();
		projectiles.clear();
		time = 0; 
		paused = false;
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
