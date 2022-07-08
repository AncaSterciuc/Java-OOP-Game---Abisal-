package GameState;

import java.util.ArrayList;
import Main.GamePanel;

public class GameStateManager {

	private GameState[] gameStates;
	private int currentState;

	public static final int NUMGAMESTATES = 7;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int LEVEL2STATE = 2;
	public static final int GAMEOVER = 4;
	public static final int SETTINGS = 5;
	public static final int YOUWIN = 6;


	public GameStateManager() {

		gameStates = new GameState[NUMGAMESTATES];

		currentState = MENUSTATE;
		loadState(currentState);

	}

	private void loadState(int state) {
		if(state == MENUSTATE)
			gameStates[state] = new MenuState(this);
		if(state == SETTINGS) {
			gameStates[state] = new Settings(this);
			currentState = SETTINGS;
		}
		if(state == LEVEL1STATE)
			gameStates[state] = new Level1State(this);
		else if(state == LEVEL2STATE)
			gameStates[state] = new Level2State(this);

		else if(state == GAMEOVER)
			gameStates[state] = new GameOver(this);
		else if(state == YOUWIN)
			gameStates[state] = new YouWin(this);
	}

	private void unloadState(int state) {
		gameStates[state] = null;
	}

	public void setState(int state) {
		unloadState(currentState);
		currentState = state;
		loadState(currentState);

	}

	public void update() {
		try {
			gameStates[currentState].update();
		} catch(Exception e) {}
	}

	public void draw(java.awt.Graphics2D g) {
		try {
			gameStates[currentState].draw(g);
		} catch(Exception e) {}
	}

	public void keyPressed(int k) {

		try{
			gameStates[currentState].keyPressed(k);
		}
		catch(Exception e){

		}
	}

	public void keyReleased(int k) {
		try {
			gameStates[currentState].keyReleased(k);
		}
		catch(Exception e){

		}

	}

}









