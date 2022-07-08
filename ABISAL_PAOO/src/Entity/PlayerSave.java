package Entity;

public class PlayerSave {
	
	private static int lives = 3;
	private static int health = 5;
	private static int score = 0;

	public static void init() {
		lives = 3;
		health = 5;
		score = 0;
	}
	
	public static int getLives() { return lives; }
	public static void setLives(int i) { lives = i; }
	
	public static int getHealth() { return health; }
	public static void setHealth(int i) { health = i; }


	public static long getScore() { return score; }
	public static void setScore(int s) { score= s; }
	
}
