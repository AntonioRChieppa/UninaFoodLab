package gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.BorderLayout;

public class HomeFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel sideBarPanel;
	private JPanel dashboardPanel;
	private JPanel bannerPanel;
	private JLabel titleLabel;
	private ImageIcon logoIcon; 
	private JLabel appLabel;
	
	LoginFrame previous;


	/**
	 * Create the frame.
	 */
	public HomeFrame(LoginFrame previous) {
		super("UninaFoodLab");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
		setSize(850,450);
		setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setForeground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        sideBarPanel = new JPanel();
        sideBarPanel.setBackground(SystemColor.textHighlight);
        sideBarPanel.setBounds(0, 0, 200, 450);
        contentPane.add(sideBarPanel);
        sideBarPanel.setLayout(null);

        // Logo icon
        logoIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/logo_UninaFoodLab.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        setIconImage(logoIcon.getImage());
        ImageIcon logoIcon2 = new ImageIcon(scaledImage);
        sideBarPanel.setLayout(null);
        JLabel logoLabel = new JLabel(logoIcon2);
        logoLabel.setBounds(10, 10, 50, 30);
        sideBarPanel.add(logoLabel);

	    // "application" label
	    appLabel = new JLabel("Application");
	    appLabel.setBounds(60, 10, 85, 30);
	    appLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
	    appLabel.setForeground(Color.WHITE);
	    sideBarPanel.add(appLabel);
	    
	    ImageIcon dashboardIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/dashboard_logo.png"); // set your path
	    Image dashboardImg = dashboardIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
	    ImageIcon dashboardIconScaled = new ImageIcon(dashboardImg);
	    JLabel dashboardIconLabel = new JLabel(dashboardIconScaled);

	    JLabel dashboardLabel = new JLabel("Dashboard");
	    dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
	    dashboardLabel.setForeground(Color.WHITE);

	    JPanel dashboardMenuContent = new JPanel();
	    dashboardMenuContent.setOpaque(false);
	    dashboardMenuContent.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
	    dashboardMenuContent.add(dashboardIconLabel);
	    dashboardMenuContent.add(dashboardLabel);

	    JPanel dashboardMenu = new JPanel();
	    dashboardMenu.setBounds(10, 60, 180, 30);
	    dashboardMenu.setOpaque(false);
	    dashboardMenu.setLayout(new BorderLayout());
	    dashboardMenu.add(dashboardMenuContent, BorderLayout.CENTER);

	    sideBarPanel.add(dashboardMenu);

	    // Corso menu (with submenu)
	    JPanel corsoMenu = new JPanel();
	    corsoMenu.setBounds(10, 100, 180, 30);
	    corsoMenu.setOpaque(false);
	    corsoMenu.setLayout(new BorderLayout());
	    JLabel corsoLabel = new JLabel("Corso");
	    corsoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
	    corsoLabel.setForeground(Color.WHITE);
	    corsoMenu.add(corsoLabel, BorderLayout.CENTER);
	    sideBarPanel.add(corsoMenu);

	    // Submenu panel (hidden by default)
	    JPanel corsoSubMenu = new JPanel();
	    corsoSubMenu.setBounds(30, 130, 160, 60);
	    corsoSubMenu.setOpaque(false);
	    corsoSubMenu.setLayout(new java.awt.GridLayout(2, 1, 0, 5));
	    corsoSubMenu.setVisible(false);

	    JLabel inserisciCorsoLabel = new JLabel("Inserisci corso");
	    inserisciCorsoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
	    inserisciCorsoLabel.setForeground(Color.WHITE);
	    corsoSubMenu.add(inserisciCorsoLabel);

	    JLabel visualizzaCorsiLabel = new JLabel("Visualizza corsi");
	    visualizzaCorsiLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
	    visualizzaCorsiLabel.setForeground(Color.WHITE);
	    corsoSubMenu.add(visualizzaCorsiLabel);

	    sideBarPanel.add(corsoSubMenu);

	    corsoMenu.addMouseListener(new java.awt.event.MouseAdapter() {
	        public void mouseEntered(java.awt.event.MouseEvent evt) {
	            corsoSubMenu.setVisible(true);
	        }
	        public void mouseExited(java.awt.event.MouseEvent evt) {
	            // Hide submenu only if mouse is not over submenu
	            java.awt.Point p = evt.getPoint();
	            java.awt.Rectangle subMenuBounds = corsoSubMenu.getBounds();
	            if (!subMenuBounds.contains(evt.getXOnScreen() - sideBarPanel.getLocationOnScreen().x,
	                                       evt.getYOnScreen() - sideBarPanel.getLocationOnScreen().y)) {
	                corsoSubMenu.setVisible(false);
	            }
	        }
	    });
	    corsoSubMenu.addMouseListener(new java.awt.event.MouseAdapter() {
	        public void mouseExited(java.awt.event.MouseEvent evt) {
	            corsoSubMenu.setVisible(false);
	        }
	    });
        
        dashboardPanel = new JPanel();
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBounds(200, 42, 850, 371);
        contentPane.add(dashboardPanel);
        
        bannerPanel = new JPanel();
        bannerPanel.setBounds(200, 0, 650, 42);
        contentPane.add(bannerPanel);
        
        bannerPanel.setLayout(new java.awt.BorderLayout());
        titleLabel = new JLabel("UninaFoodLab", javax.swing.SwingConstants.CENTER);
        bannerPanel.add(titleLabel, BorderLayout.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(sideBarPanel.getBackground());

	}
}
