package projectiles;

import java.awt.Image;

import game.Projectile;

public class NormalBullet extends Projectile {

	public NormalBullet(int e, Image image) {
		super(image);
		this.speed = 12;
		this.energy = e;
	}

}
