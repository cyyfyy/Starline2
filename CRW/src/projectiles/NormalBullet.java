package projectiles;

import game.Projectile;

import javax.swing.ImageIcon;

public class NormalBullet extends Projectile {

	public NormalBullet(int e, ImageIcon ref) {
		super(ref);
		this.speed = 12;
		this.energy = e;
	}

}
