package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.Creaturi;
import Entity.Enemies.Boss;
import Entity.Enemies.Creaturi2;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Level2State extends GameState {

	private TileMap tileMap;
	private Background bg;

	private Player player;
	private ArrayList<Enemy> enemies;
	private ArrayList<Ghost> ghosts;
	private BufferedImage Text;
	private Title title;
	private Door door;

	private boolean eventStart;
	private boolean eventFinish;
	private boolean eventDead;
	private boolean blockInput = false;
	private ArrayList<Rectangle> tb;
	private int eventCount = 0;

	private Life life;
	private AudioPlayer Music;
	private boolean finish;

	public Level2State(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}
	
	public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/TileMap.png");
		tileMap.loadMap("/Maps/l2.map");
		tileMap.setPosition(0, 0);
		tileMap.setScroll(1);
		
		bg = new Background("/Backgrounds/bun.png", 0.1);

		//player
		player = new Player(tileMap);
		player.setPosition(100, 100);
		player.setHealth(PlayerSave.getHealth());
		player.setLives(PlayerSave.getLives());

		//inamici
		populateEnemies();

		//viata
		life = new Life(player);

		//inamicii dupa ce mor
		ghosts = new ArrayList<Ghost>();

		//music
		Music= new AudioPlayer("/Music/msc.mp3");
		//Music.play();


		// title and subtitle
		try {

			Text = ImageIO.read(getClass().getResourceAsStream("/LIFE/level2.png"));
			title = new Title(Text.getSubimage(0, 0, 90, 50));
			title.sety(60);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		//door
		door = new Door(tileMap);
		door.setPosition(3220,280);

		//start
		eventStart = true;
		tb = new ArrayList<Rectangle>();
		eventStart();

	}
	//inamici
	private void populateEnemies(){
		enemies = new ArrayList<Enemy>();

		Creaturi c;
		Creaturi2 c2;

		Point[] points = new Point[] {
				new Point(120, 100),
				new Point(900, 31),
				new Point(2009, 67),
				new Point(2050, 69),
				new Point(2600, 87)

		};
		for(int i = 0; i < points.length; i++) {
			c = new Creaturi(tileMap);
			c.setPosition(points[i].x, points[i].y);
			enemies.add(c);
			c2 = new Creaturi2(tileMap);
			c2.setPosition(points[i].x +50 , points[i].y);
			enemies.add(c2);


		}
		Boss b;
		b = new Boss(tileMap);
		b.setPosition(2900,96);
		enemies.add(b);

	}
	
	public void update() {
		
		// update player
		player.update();

		//verificam daca sfarsitul lvl poate incepe
		if(door.intersects(player)) {
			eventFinish = blockInput = true;
			finish = true;
			System.out.println("finish");
		}
		tileMap.setPosition(
			GamePanel.WIDTH / 2 - player.getx(),
			GamePanel.HEIGHT / 2 - player.gety()
		);

		bg.setPosition(tileMap.getx(),tileMap.gety());

		//aici moare
		if(player.getHealth() == 0 || player.gety() > tileMap.getHeight()) {
			eventDead = blockInput = true;
		}

		if(eventStart) eventStart();
		if(eventDead) eventDead();
		if(eventFinish) eventFinish();

		if(title != null) {
			title.update();
			if(title.shouldRemove()) title = null;
		}

		//up atac inamici
		player.checkAttack(enemies);

		//up toti inamicii
		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.update();
			if(e.isDead()) {
				enemies.remove(i);
				i--;
				ghosts.add(new Ghost(e.getx(), e.gety()));
			}
		}
		//up stafii
		for(int i = 0; i < ghosts.size(); i++) {
			ghosts.get(i).update();
			if(ghosts.get(i).shouldRemove()) {
				ghosts.remove(i);
				i--;
			}
		}
		//up door
		door.update();
	}
	
	public void draw(Graphics2D g) {
		
		//desenez background
		bg.draw(g);
		
		//desenez tilemap
		tileMap.draw(g);
		
		//desenez player
		player.draw(g);

		//desenez inamicii
		for(int i=0;i<enemies.size();i++){
			enemies.get(i).draw(g);
		}
		// desenez stafiile
		for(int i = 0; i < ghosts.size(); i++) {
			ghosts.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
			ghosts.get(i).draw(g);
		}

		//vieti
		life.draw(g);

		//titlu
		if(title != null) title.draw(g);

		//tranzitie
		g.setColor(Color.BLACK);
		for(int i = 0; i < tb.size(); i++) {
			g.fill(tb.get(i));
		}

		//door
		door.draw(g);
		
	}


	private void reset() {
		player.reset();
		player.setPosition(300, 161);
		populateEnemies();
		blockInput = true;
		eventCount = 0;
		eventStart = true;
		eventStart();
		title = new Title(Text.getSubimage(0, 0, 178, 20));
		title.sety(60);

	}

	private void eventStart() {
		eventCount++;
		if(eventCount == 1) {
			tb.clear();
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
			tb.add(new Rectangle(0, GamePanel.HEIGHT / 2, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
		}
		if(eventCount > 1 && eventCount < 60) {
			tb.get(0).height -= 4;
			tb.get(1).width -= 6;
			tb.get(2).y += 4;
			tb.get(3).x += 6;
		}
		if(eventCount == 30) title.begin();
		if(eventCount == 60) {
			eventStart = blockInput = false;
			eventCount = 0;
			tb.clear();
		}
	}

	private void eventDead() {
		System.out.println("mort");
		eventCount++;
		if(eventCount == 1) {
			player.setDead();
			player.stop();
		}
		if(eventCount == 60) {
			tb.clear();
			tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 60) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;
		}
		if(eventCount >= 120) {
			if(player.getLives() == 1) {
				gsm.setState(GameStateManager.GAMEOVER);
			}
			else {
				eventDead = blockInput = false;
				eventCount = 0;
				player.loseLife();
				reset();
			}
		}
	}
	private void eventFinish() {
		eventCount++;
		if(eventCount == 1) {
			player.setNextdoor(true);
			player.stop();

		}
		else if(eventCount == 120) {
			tb.clear();
			tb.add(new Rectangle(GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 120) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;

		}
		if(eventCount == 180 || finish) {
			PlayerSave.setHealth(player.getHealth());
			PlayerSave.setLives(player.getLives());
			//PlayerSave.setTime(player.getScore());

			gsm.setState(GameStateManager.YOUWIN);
		}

	}


	public void keyPressed(int k) {
		if(k == KeyEvent.VK_A) player.setLeft(true);
		if(k == KeyEvent.VK_D) player.setRight(true);
		if(k == KeyEvent.VK_UP) player.setUp(true);

		if(k == KeyEvent.VK_S) player.setDown(true);
		if(k == KeyEvent.VK_SPACE) player.setJumping(true);

		if(k == KeyEvent.VK_R) player.setUltima();
		if(k == KeyEvent.VK_F) player.setFiring();
	}
	
	public void keyReleased(int k) {
		if(k == KeyEvent.VK_A) player.setLeft(false);
		if(k == KeyEvent.VK_D) player.setRight(false);
		if(k == KeyEvent.VK_UP) player.setUp(false);

		if(k == KeyEvent.VK_S) player.setDown(false);
		if(k == KeyEvent.VK_SPACE) player.setJumping(false);


	}
	
}












