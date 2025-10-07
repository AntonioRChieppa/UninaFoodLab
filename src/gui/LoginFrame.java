package gui;

import java.awt.EventQueue;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Insets;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;



public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setLocationRelativeTo(null);
					frame.setSize(850,450);
					frame.setResizable(false);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
        super("UninaFoodLab");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setForeground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel SideBarPanel = new JPanel();
        SideBarPanel.setBackground(SystemColor.textHighlight);
        SideBarPanel.setBounds(0, 0, 300, 450);
        contentPane.add(SideBarPanel);
        SideBarPanel.setLayout(null);

        // Logo
        ImageIcon logoIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/logo_UninaFoodLab.png");
        Image img = logoIcon.getImage();
        BufferedImage scaledImg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(img, 0, 0, 150, 150, null);
        g2d.dispose();
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImg));
        logoLabel.setBounds((300-150)/2, 100, 150, 150);
        SideBarPanel.add(logoLabel);


        // "UninaFoodLab" label
        JLabel lblNewLabel = new JLabel("UninaFoodLab", JLabel.CENTER);
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        lblNewLabel.setBounds(47, 270, 206, 43); // (300-206)/2 = 47, 100+150+20=270
        SideBarPanel.add(lblNewLabel);

        JPanel Loginpanel = new JPanel();
        Loginpanel.setBounds(298, 0, 600, 450);
        contentPane.add(Loginpanel);
        Loginpanel.setLayout(null);

        // "Welcome Back!" label
        JLabel lblWelcomeBack = new JLabel("Welcome Back!", JLabel.CENTER);
        lblWelcomeBack.setForeground(SystemColor.textHighlight);
        lblWelcomeBack.setFont(new Font("Consolas", Font.BOLD, 24));
        int welcomeWidth = 300;
        int welcomeHeight = 40;
        int welcomeX = (600 - welcomeWidth) / 2;
        int welcomeY = 30;
        lblWelcomeBack.setBounds(welcomeX, welcomeY, welcomeWidth, welcomeHeight);
        Loginpanel.add(lblWelcomeBack);

	}
}
