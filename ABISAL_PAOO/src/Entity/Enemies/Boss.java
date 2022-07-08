package Entity.Enemies;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Boss extends Enemy {

	private BufferedImage[] sprites;


	public Boss(TileMap tm){
		super(tm);

		moveSpeed=0.3;
		maxSpeed=0.4;
		fallSpeed=0.2;
		maxFallSpeed=10.0;

		width = 36;
		height = 72;
		cwidth = 22;
		cheight = 60;

		health=maxHealth=50;
		damage=2;

		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/boss.png"));
			sprites = new BufferedImage[14];
			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < sprites.length; j++) {
					if (i < 1) {
						sprites[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height);
					}
				}
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}


		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);

		left = true;
		facingRight = true;


	}

	private void getNextPosition() {

		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		if(falling) {
			dy += fallSpeed;
		}



	}

	public void update() {

		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check flinching
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 400) {
				flinching = false;
			}
		}

		// daca loveste peretele, merge in directia opusa

		if(right && dx == 0) {
			right = false;
			left = true;
			facingRight = false;
		}
		else if(left && dx == 0) {
			right = true;
			left = false;
			facingRight = true;
		}


		// update animation
		animation.update();

	}

	public void draw(Graphics2D g) {
		setMapPosition();
		super.draw(g);

	}

}