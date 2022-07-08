package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

    // player
    private int lives;
    private int health;
    private int maxHealth;
    private int fire;
    private int maxFire;
    private int ultimata;
    private int maxultimata;
    private boolean dead;
    private boolean flinching;
    private boolean knockback;
    private long flinchTimer;
    private boolean nextdoor;
    private int score;

    // fireball
    private boolean firing;
    private int fireCost;
    private int fireBallDamage;
    private ArrayList<FireBall> fireBalls;

    // ultimata, lovitura
    private boolean ultimating;
    private int ultimataDamage;
    private ArrayList<Ultimata> ultimatas;



    //animatii
    private ArrayList<BufferedImage[]> sprites;
    private final int[] numFrames = {1, 5, 3, 3, 7, 10, 10, 10, 3};

    //animatii actiuni

    private static final int IDLE = 0;
    private static final int WALKING = 1;
    private static final int JUMPING = 2;
    private static final int FALLING = 2;
    private static final int DOWN = 3;
    private static final int KNOCKBACK = 4;
    private static final int ATAC = 5;
    private static final int ULTIMATA = 6;
    private static final int DOWN_ATAC = 7;
    private static final int DEAD = 8;

    public Player(TileMap tm) {

        super(tm);

        width = 32;
        height = 64;
        cwidth = 18;
        cheight = 55;

        moveSpeed = 0.3;
        maxSpeed = 1.6;
        stopSpeed = 0.4;
        fallSpeed = 0.15;
        maxFallSpeed = 4.0;
        jumpStart = -4.8;
        stopJumpSpeed = 0.3;

        facingRight = true;

        health = maxHealth = 5;
        lives = 3;
        fire = maxFire = 2500;
        ultimata = maxultimata = 2500;

        fireCost = 200;
        fireBallDamage = 5;
        fireBalls = new ArrayList<FireBall>();

        ultimataDamage = 8;
        ultimatas = new ArrayList<Ultimata>();
        score = 0;

        // load sprites
        try {

            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/Arya.png"));

            sprites = new ArrayList<BufferedImage[]>();
            for (int i = 0; i < 9; i++) {
                BufferedImage[] bi = new BufferedImage[numFrames[i]];
                for (int j = 0; j < numFrames[i]; j++) {

                    if (i < 8) {
                        bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);

                    } else {
                        bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height);
                    }

                }
                sprites.add(bi);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        animation = new Animation();
        currentAction = IDLE;
        animation.setFrames(sprites.get(IDLE));
        animation.setDelay(400);

    }
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getFire() {
        return fire;
    }

    public int getMaxFire() {
        return maxFire;
    }

    public void setFiring() {
        firing = true;
    }

    public void setUltima() {
        ultimating = true;
    }

    public void setHealth(int i) { health = i; }

    public void setLives(int i) { lives = i; }

    public void loseLife() { lives--; }
    public int getLives() { return lives; }

    public void setScoare(int i) { score = i; }
    public int getScore() { return score; }


    public void setNextdoor(boolean b) { nextdoor = b; }

    //dead
    public void setDead() {
        health = 0;
        stop();
    }
    public void stop() {
        left = right = up = down = flinching =
                firing = jumping = ultimating = false;
    }


    public void checkAttack(ArrayList<Enemy> enemies) {

    // loop through enemies
		for(int i = 0; i < enemies.size(); i++) {
        Enemy e = enemies.get(i);

        // fireballs
        for(int j = 0; j < fireBalls.size(); j++) {
            if(fireBalls.get(j).intersects(e)) {
                e.hit(fireBallDamage);
                fireBalls.get(j).setHit();
                break;
            }
        }
        //ultimata
            for(int j = 0; j < ultimatas.size(); j++) {
                if(ultimatas.get(j).intersects(e)) {
                    e.hit(ultimataDamage);
                    ultimatas.get(j).setHit();
                    break;
                }
            }

        // check enemy collision
        if(intersects(e)) {
            hit(e.getDamage());
        }

    }

}
    public void hit(int damage) {
        if(flinching) return;
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0) dead = true;
        flinching = true;
        flinchTimer = System.nanoTime();
    }

    public void reset() {
        health = maxHealth;
        facingRight = true;
        currentAction = -1;
        stop();
    }

    private void getNextPosition() {

        // movement
        if (left) {
            dx -= moveSpeed;
            if (dx < -maxSpeed) {
                dx = -maxSpeed;
            }
        } else if (right) {
            dx += moveSpeed;
            if (dx > maxSpeed) {
                dx = maxSpeed;
            }
        } else {
            if (dx > 0) {
                dx -= stopSpeed;
                if (dx < 0) {
                    dx = 0;
                }
            } else if (dx < 0) {
                dx += stopSpeed;
                if (dx > 0) {
                    dx = 0;
                }
            }
        }

        //nu se poate misca in timp ce ataca
        if ((currentAction == ULTIMATA || currentAction == ATAC) && !(jumping || falling)) {
            dx = 0;
        }

        // sare
        if (jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        // cade
        if (falling) {
            dy += fallSpeed;

            if (dy > 0) jumping = false;
            if (dy < 0 && !jumping) dy += stopJumpSpeed;

            if (dy > maxFallSpeed) dy = maxFallSpeed;

        }

    }

    public void update() {

        // update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp, ytemp);
        if(dx == 0) x = (int)x;

        if (currentAction == ULTIMATA){
            if(animation.hasPlayedOnce()) ultimating = false;
        }
        if(currentAction == ATAC) {
            if(animation.hasPlayedOnce()) firing = false;
        }


        //atac cu foc
        if(fire > maxFire) fire = maxFire;
        if(firing && currentAction != ATAC){
            if( fire >fireCost){
                fire-=fireCost;
                FireBall fb = new FireBall(tileMap,facingRight);
                fb.setPosition(x,y);
                fireBalls.add(fb);
            }
        }

        //atac cu ultimata
        if(ultimata > maxultimata) ultimata = maxultimata;
        if(ultimating && currentAction != ULTIMATA){
            if( ultimata >fireCost){
                ultimata-=fireCost;
                Ultimata u = new Ultimata(tileMap,facingRight);
                u.setPosition(x,y);
                ultimatas.add(u);
            }
        }


        //update
        for( int i=0;i<fireBalls.size();i++){
            fireBalls.get(i).update();
            if(fireBalls.get(i).shouldRemove()){
                fireBalls.remove(i);
                i--;
            }
        }
        for( int i=0;i<ultimatas.size();i++){
            ultimatas.get(i).update();
            if(ultimatas.get(i).shouldRemove()){
                ultimatas.remove(i);
                i--;
            }
        }
        //
        if(flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 1000) {
                flinching = false;
            }
        }

        // set animation
        if(nextdoor) {
            if(currentAction != IDLE) {
                if(animation.hasPlayedOnce()) nextdoor = false;
            }
        }

        else if (ultimating) {
            if (currentAction != ULTIMATA) {
                currentAction = ULTIMATA;
                animation.setFrames(sprites.get(ULTIMATA));
                animation.setDelay(50);
                width = 30;
            }
        } else if (firing) {
            if (currentAction != ATAC) {
                currentAction = ATAC;
                animation.setFrames(sprites.get(ATAC));
                animation.setDelay(80);
                width = 30;
            }

        } else if (dy > 0) {
            if (currentAction != FALLING) {
                currentAction = FALLING;
                animation.setFrames(sprites.get(FALLING));
                animation.setDelay(100);
                width = 30;
            }
        } else if (dy < 0) {
            if (currentAction != JUMPING) {
                currentAction = JUMPING;
                animation.setFrames(sprites.get(JUMPING));
                animation.setDelay(-1);
                width = 30;
            }
        } else if (left || right) {
            if (currentAction != WALKING) {
                currentAction = WALKING;
                animation.setFrames(sprites.get(WALKING));
                animation.setDelay(40);
                width = 30;
            }
        }
        else if (down) {
            if (currentAction != DOWN) {
                currentAction = DOWN;
                animation.setFrames(sprites.get(DOWN));
                animation.setDelay(200);
                width = 30;
            }
        }
        else {
            if (currentAction != IDLE) {
                currentAction = IDLE;
                animation.setFrames(sprites.get(IDLE));
                animation.setDelay(400);
                width = 30;
            }
            else if(health == 0) {
                if(currentAction != DEAD) {
                    currentAction = DEAD;
                    animation.setFrames(sprites.get(DEAD));
                    animation.setDelay(100);
                    width = 30;
                }

            }


        }

        animation.update();

        // set direction
        if (currentAction != ULTIMATA && currentAction != ATAC) {
            if (right) facingRight = true;
            if (left) facingRight = false;
        }

    }

    public void draw(Graphics2D g) {

        setMapPosition();

        for(int i = 0; i<fireBalls.size();i++){
            fireBalls.get(i).draw(g);
        }
        for(int i = 0; i<ultimatas.size();i++){
            ultimatas.get(i).draw(g);
        }

        // draw player
        if (flinching) {
            long elapsed =
                    (System.nanoTime() - flinchTimer) / 1000000;
            if (elapsed / 100 % 2 == 0) {
                return;
            }
        }

        super.draw(g);

    }

}