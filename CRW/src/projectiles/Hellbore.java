package projectiles;

import game.Projectile;

import javax.swing.ImageIcon;

public class Hellbore extends Projectile {

	public Hellbore(int e, ImageIcon ref) {
		super(ref);
		this.speed = 14;
		this.energy = e/4;
		this.isStasis = true;
	}

}
