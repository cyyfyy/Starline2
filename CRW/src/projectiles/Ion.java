package projectiles;

import game.Projectile;

import javax.swing.ImageIcon;

public class Ion extends Projectile {

	public Ion(int e, ImageIcon ref) {
		super(ref);
		this.isEmp = true;
		int s = 0;
		if (e >= 80)
		{
			s = 24;
		}
		if (e < 80)
		{
			s = 22;
		}
		if (e < 70)
		{
			s = 20;
		}
		if (e < 60)
		{
			s = 18;
		}
		if (e < 50)
		{
			s = 16;
		}
		if (e < 40)
		{
			s = 14;
		}
		if (e < 30)
		{
			s = 12;
		}
		if (e < 20)
		{
			s = 10;
		}
		if (e < 10)
		{
			s = 8;
		}
		this.speed = s;
		this.energy = 1;
	}

}
