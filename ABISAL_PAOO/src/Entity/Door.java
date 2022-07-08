package Entity;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Door extends MapObject {

	private BufferedImage[] sprites;

	public Door(TileMap tm) {
		super(tm);
		facingRight = true;
		width = height = 40;
		cwidth = 20;
		cheight = 40;

		try {
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream("/Sprites/Items/of.png")
			);
			sprites = new BufferedImage[4];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(
						i * width, 0, width, height
				);
			}
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		animation.update();
	}
	
	public void draw(Graphics2D g) {

		setMapPosition();
		super.draw(g);
	}
	
}
