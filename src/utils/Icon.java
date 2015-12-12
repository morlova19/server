package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Class that loads and stores icon of app.
 */
public class Icon {
    /**
     * Icon of the app.
     */
    private static Image icon = null;
    /**
     * Creates icon of the app.
     * @return icon.
     */
    public static Image getIcon() {
        String path = "images/icon.png";
        if(icon == null) {
            try {
                icon = ImageIO.read(ClassLoader.getSystemResourceAsStream(path));
            } catch (IOException | IllegalArgumentException e) {
                icon = drawIcon();
            }
        }
        return icon;
    }
    /**
     * Draws icon of app if it cannot be read from resource file.
     * @return icon.
     */
    private static Image drawIcon() {
        Image img;
        int w = 35;
        int h = w;
        img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = ((BufferedImage)img).createGraphics();
        gr.setComposite(AlphaComposite.Clear);
        gr.fillRect(0, 0, w, h);
        gr.setComposite(AlphaComposite.SrcOver);
        gr.setColor(Color.BLUE);
        gr.fillOval(0, 0, w, h);
        gr.setFont(new Font("Arial", Font.BOLD, 26));
        gr.setColor(Color.YELLOW);
        gr.drawString("S",10,27);
        return img;
    }
}
