package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Logo minimale: un cerchio pieno con le iniziali "UF" centrato.
 */
public class SimpleBrandLogo extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Color circleColor;
    private final Color textColor;

    public SimpleBrandLogo(Color circleColor, Color textColor) {
        this.circleColor = circleColor;
        this.textColor = textColor;
        setOpaque(false);
        setPreferredSize(new Dimension(120, 120));
        setMinimumSize(new Dimension(80, 80));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = Math.min(getWidth(), getHeight());
        int diameter = size - 6;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(circleColor);
        g2d.fillOval(x, y, diameter, diameter);

        g2d.setColor(textColor);
        Font font = getFont().deriveFont(Font.BOLD, diameter * 0.45f);
        g2d.setFont(font);
        String initials = "UF";
        int textWidth = g2d.getFontMetrics().stringWidth(initials);
        int textHeight = g2d.getFontMetrics().getAscent();
        int textX = getWidth() / 2 - textWidth / 2;
        int textY = getHeight() / 2 + textHeight / 3;
        g2d.drawString(initials, textX, textY);
        g2d.dispose();
    }
}
