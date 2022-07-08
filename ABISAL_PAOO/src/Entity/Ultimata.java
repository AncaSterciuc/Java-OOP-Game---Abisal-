package Entity;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Ultimata extends MapObject{

    private boolean hit;
    private boolean remove;
    private BufferedImage[] sprites;

    public Ultimata(TileMap tm, boolean right){
        super(tm);
        facingRight = right;

        moveSpeed=3.8;
        facingRight = right;

        moveSpeed = 3.8;
        if(right) dx = moveSpeed;
        else dx = -moveSpeed;

        width = 30;
        height = 30;

        cwidth = 14;
        cheight = 14;

        try {
           BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/ultimata.png"));
            sprites = new BufferedImage[7];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
            }

            animation = new Animation();
            animation.setFrames(sprites);
            animation.setDelay(70);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void setHit() {
        if(hit) return;
        hit = true;
        animation.setFrames(sprites);
        animation.setDelay(70);
        dx = 0;
    }


    public boolean shouldRemove() { return remove; }

    public void update() {

        checkTileMapCollision();
        setPosition(xtemp, ytemp);

        if(dx ==0 && !hit){
            setHit();
        }

        if(dx == 0 && !hit) {
            setHit();
        }

        animation.update();
        if(hit && animation.hasPlayedOnce()) {
            remove = true;
        }

    }

    public void draw(Graphics2D g) {

        setMapPosition();

        super.draw(g);

    }

}
