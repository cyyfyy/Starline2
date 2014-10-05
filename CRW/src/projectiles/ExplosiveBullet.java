package projectiles;

import game.Projectile;

import javax.swing.ImageIcon;

public class ExplosiveBullet extends Projectile {
	boolean detonated;
	int timeSinceDetonation;
	public ExplosiveBullet(int e, ImageIcon ref) {
		super(ref);
		this.speed = 10;
		this.energy = e*2;
		detonated = false;
		timeSinceDetonation = 0;
	}

	//override
	protected boolean isHarmful()
	{
		if(timeSinceDetonation > 4 || !detonated)
		{
			return true;
		}
		return false;
	}

	//override
	protected void onContact()
	{
		if(!detonated)
		{
			detonated = true;
			timeSinceDetonation++;
			this.radius = 10;
		}
	}

	//override
	protected void step()
	{
		if(detonated)
		{
			this.speedX = 0;
			this.speedY = 0;
			timeSinceDetonation++;
			radius = 10 * timeSinceDetonation;//explosion radius
		}
		else
		{
			this.x += this.speedX;
			this.y += this.speedY;
		}

	}

}
