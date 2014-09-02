package game;

import java.awt.Image;

public abstract class Projectile {

	protected int x;
	protected int y;
	protected int radius;

	protected int speedX;
	protected int speedY;

	protected int energy;

	protected boolean isEmp;
	protected boolean isStasis;
	protected int speed;
	public Image image;
	boolean active;


	protected Projectile(Image img)
	{
		this.energy = 0;
		this.isEmp = false;
		this.isStasis = false;
		this.x = 0;
		this.y = 0;
		this.radius = 2;
		this.speed = 12;
		this.speedX = 0; // Set by caller.
		this.speedY = 0; // Set by caller.
		this.image = img;
		this.active = true;

	}

	protected void step()
	{
		 x += speedX;
		 y += speedY;
	}

	void onContact()
	{

	}
	
	boolean isHarmful()
	{
		return true;
	}

	public boolean isActive() {
		return active;
	}

}
