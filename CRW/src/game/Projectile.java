package game;

import javax.swing.ImageIcon;

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
	public ImageIcon image;
	protected boolean active;


	protected Projectile(ImageIcon ref)
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
		this.image = ref;
		this.active = true;

	}

	protected void step()
	{
		 x += speedX;
		 y += speedY;
	}

	protected void onContact()
	{

	}
	
	protected boolean isHarmful()
	{
		return true;
	}

	public boolean isActive() {
		return active;
	}
}
