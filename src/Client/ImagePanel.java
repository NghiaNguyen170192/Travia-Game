/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Le Chinh Nhan & Nguyen Quoc Trong Nghia
 */
public class ImagePanel extends JPanel {

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ImageIcon imIcon = new ImageIcon("src\\Client\\ClientImage.png");
        g.drawImage(imIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
    }
}
