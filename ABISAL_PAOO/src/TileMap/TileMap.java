package TileMap;
import java.awt.*;
import java.awt.image.*;

import java.io.*;
import javax.imageio.ImageIO;

import Main.GamePanel;

public class TileMap
{
	//dimensiuni
	private double x;
	private double y;
	
	// limite
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;

	//scroll la camera la player
	private double scroll;
	
	// map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	
	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;

	//draw
	//ca sa nu desenez de fiecare data mapa, o sa desenez doar dalele ce sunt pe ecran
	private int rowOffset;//de pe ce linie sa incepeapa sa deseneze
	private int colOffset;//de pe ce coloana sa incepeapa sa deseneze
	private int RowsDraw;//cate linii sa deseneze
	private int ColsDraw;//cate coloane sa deseneze
	
	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		RowsDraw = GamePanel.HEIGHT / tileSize + 2;// +2 ca avem nr fix si sa nu se vada taiat urat
		ColsDraw = GamePanel.WIDTH / tileSize + 2;
		scroll = 0.07;
	}
	//salveaza tilele in memorie
	public void loadTiles(String s) {
		
		try {

			tileset = ImageIO.read(
				getClass().getResourceAsStream(s)
			);
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile[2][numTilesAcross];
			
			BufferedImage subimage;
			for(int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(
							col * tileSize,
							0,
							tileSize,
							tileSize
						);
				tiles[0][col] = new Tile(subimage, Tile.BLOCKED);

				subimage = tileset.getSubimage(
							col * tileSize,
							tileSize,
							tileSize,
							tileSize
						);
				tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}
			subimage = tileset.getSubimage(0,0,tileSize,tileSize);
			tiles[0][0]= new Tile(subimage,Tile.NORMAL);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	//salveaza fisierele de la mapa in memorie
	public void loadMap(String s) {
		
		try {
			
			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;
			
			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
			
			String delims = "\\s+";//spatiu trasparent

			for(int row = 0; row < numRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delims);

				for(int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int getTileSize() { return tileSize; }
	public double getx() { return x; }
	public double gety() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public int getType(int row, int col) {
		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}
	
	public void setScroll(double d) { scroll = d; }
	
	public void setPosition(double x, double y) {

		this.x += (x - this.x) * scroll;//diferenta dintre pozitia curenta si scroll ne muta camera spre player
		this.y += (y - this.y) * scroll;

		
		fixBounds();
		
		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
		
	}
	
	private void fixBounds() {
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
	}
	
	public void draw(Graphics2D g) {
		
		for(int row = rowOffset; row < rowOffset + RowsDraw; row++) {
			if(row >= numRows) break;
			
			for(int col = colOffset; col < colOffset + ColsDraw; col++) {
				if(col >= numCols) break;
				
				if(map[row][col] == 0) continue;
				
				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;
				
				g.drawImage(tiles[r][c].getImage(), (int)x + col * tileSize, (int)y + row * tileSize, null);
				
			}
			
		}
		
	}
	
}



















