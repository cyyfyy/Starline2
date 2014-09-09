package game;

import java.util.Stack;

public class Arena {

	int width;
	int height;
	Stack<Robot> robots;
	Stack<Projectile> projectiles;
	Game game;

	public Arena(Game g, int h, int w)
	{
		game = g;
		robots = g.robots;
		projectiles = g.projectiles;
		width = w;
		height = h;
	}

	protected static int fix360(double d)
	{
		d %= 360;
		return (int) ((d < 0) ? 360 + d : d);
	}

	protected static int degToRad(int degrees) {
		return (int) (degrees * (Math.PI / 180));
	}

	protected static int rad2deg(double d) {
		return (int)(d * (180 / Math.PI));
	}

	protected String pad(String string, int length) {
		while (string.length() < length)
			string = string + ' ';
		return string;
	}
	
	void shoot(Robot robot, String type, int energy) {
		double aimRadians = robot.aim * (Math.PI + Math.PI) / 360;
		int radius = robot.radius + 7;

		Projectile p = Game.createProjectile(type, energy);
		p.x = (int) (robot.x + Math.sin(aimRadians) * radius);
		p.y = (int) (robot.y - Math.cos(aimRadians) * radius);
		p.energy = energy;
		p.speedX = (int) (Math.sin(aimRadians) * p.speed);
		p.speedY = (int) (-Math.cos(aimRadians) * p.speed);

		game.projectiles.push(p);
	}
/**
 * Returns 0 in no robot is in sights
 * 
 * @param observer
 * @param direction
 * @return range to nearest robot in sights
 */

	protected Robot findNearestRobot(Robot observer, int direction) {
		// A port of RoboWar 4.5.2's Engine/Projectile.c distance()
		int dist = 0;
		Robot target = null;
		double m, n, t;  // x=mt + x', y=nt + y'
		int a, b, c, d;  // Coordinates of robots.

		/*
		       Range algorithm:  see what robots intersect line of sight.

		        t = distance along line of sight closest to target being checked
		        m = cos (angle)
		        n = sin (angle)

		        1) Compute t, distance along aim vector
		        2) Use crude check to see if target is in general area
		 */

		m = Math.cos(degToRad(fix360(direction + 270)));
		n = -(Math.sin(degToRad(fix360(direction - 270))));
		a = observer.x;
		b = observer.y;

		for (Robot rob: robots) {
			if (rob.alive && rob != observer) {
				c = rob.x;
				d = rob.y;
				t = m*(c-a) + n*(d-b);
				// First a crude Manhattan Metric check
				if (t > 0 && Math.abs((m+n)*t+a+b-c-d) < 20) {
				//	double radiusSquared = rob.radius * rob.radius;
				//	double test = (m*t+a-c)*(m*t+a-c) + (n*t+b-d)*(n*t+b-d);
				//	if (test < (radiusSquared-8)) {
						// in sights
						if (dist == 0 || t < dist) {
							dist = (int)t;
							target = rob;
				//		}
					}
				}
			}
		}
		return target;
	}


	Projectile findNearestProjectile(Robot observer, int direction) {
		// A port of RoboWar 4.5.2's Engine/Projectile.c radar()
		int theta, range, closeDistance = Integer.MAX_VALUE;
		Projectile closest = null;
		int x = observer.x;
		int y = observer.y;
		int scan = fix360(direction);

		for (Projectile pro: projectiles) {
			theta = (int)((450 - rad2deg(Math.atan2(y-pro.y, pro.x-x))) % 360);

			if ((Math.abs(theta - scan) < 20) || (Math.abs(theta - scan) > 340)) {
				range = (y - pro.y) * (int)(y - pro.y) +
						(x - pro.x) * (int)(x - pro.x);
				if (range < closeDistance) {
					closeDistance = range;
					closest = pro;
				}
			}
		}

//		if (closeDistance == Integer.MAX_VALUE)
//		{
//			closeDistance = 0;
//		}
//		else
//		{
//			closeDistance = (int)(Math.sqrt(closeDistance));
//		}
		return closest;
	}


	public int doRange(Robot robot) {
		int direction = fix360(robot.aim + robot.scan);
		return (int)robot.distanceTo(findNearestRobot(robot, direction));
	}

	public double doRadar(Robot robot)  {
		int direction = fix360(robot.aim + robot.look);
		return robot.distanceTo(findNearestProjectile(robot, direction));
	}

	public double doDoppler(Robot robot){
		int direction = fix360(robot.aim + robot.look);
		Robot nearest = findNearestRobot(robot, direction);
		if (nearest == null)
		{
			return 0;
		}

		// The following is stolen from Robowar 4.5.2's Projectiles.c.
		double dist = 0;
		Robot target;
		double doppler = 0;
		double tmp;
		double m = Math.sin((robot.aim + robot.look + 270) % 360);
		double n = -Math.sin((robot.aim + robot.look) % 360);

		for (Robot enemy: robots) {
			if (enemy == robot || !enemy.alive) continue;
			int a = robot.x;
			int b = robot.y;
			int c = enemy.x;
			int d = enemy.y;
			double t = (m*c + n*d - m*a -n*b); /* /(m*m+n*n) deleted because it seems to equal 1 */
			if (t > 0 &&
					(m*t+a-c)*(m*t+a-c)+
					(n*t+b-d)*(n*t+b-d)<
					(robot.radius * robot.radius - 9)) /* in sights */
				if (dist == 0 || t < dist) {
					dist = t;
					target = enemy;
					if (target.energy <= 0 || target.stasis > 0  ||
							target.colliding || target.touchingWall)
					{
						doppler = 0;
					}
					else {
						a = (a-c); /* a = rx */ //x difference
						b = (b-d); /* b = ry */ //y difference
						c = (a*target.vx + b*target.vy); /* c = r¥v */ //xdiff*xspeed + ydiff*yspeed
						t = (target.vx*target.vx+ target.vy*target.vy) - (c*c) / (a*a+b*b);
						tmp = Math.sqrt(t);
						if (tmp-(int)(tmp) > 0.5) tmp+=1.0;
						doppler = (a*target.vy-b*target.vx) > 0 ?
								-tmp : tmp;
					}
				}
		}

		return dist == 0 ? 0 : doppler;
	}
	public double jakeDoppler(Robot robot)
	{
		
		return 0;
		
	}

	public int activeRobots() {
		int count = 0;
		for (Robot rob: robots) {
			if (rob.alive) count++;
		}
		return count;
	}

}
