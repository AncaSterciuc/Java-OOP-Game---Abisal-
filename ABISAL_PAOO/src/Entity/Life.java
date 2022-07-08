package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Life {

    private Player player;

    private BufferedImage image;
    private BufferedImage life;
    private Font font;

    public Life(Player p) {
        player = p;
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/LIFE/hea.png"));
            life = ImageIO.read(getClass().getResourceAsStream("/LIFE/1.png"));
            life = life.getSubimage(0, 0, 20, 20);
            font = new Font("Ariel", Font.BOLD, 10);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g) {

        g.drawImage(image, 0, 10, null);
        g.setFont(font);

        for(int i = 0; i < player.getLives(); i++) {
            g.drawImage(life, 8 + i * 22, 33, null);
        }
        g.setColor(Color.BLACK);

        g.drawString(player.getHealth() + "/" + player.getMaxHealth(), 30, 32);


    }

}













