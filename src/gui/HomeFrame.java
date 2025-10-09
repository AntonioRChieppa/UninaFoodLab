package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
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


	    // "application" label
	    appLabel = new JLabel("Application");
	    appLabel.setLabelFor(appLabel);
	    appLabel.setIcon(new ImageIcon(HomeFrame.class.getResource("/icons/app_icon.png")));
	    appLabel.setBounds(10, 10, 180, 30);
	    appLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
	    appLabel.setForeground(Color.WHITE);
	    sideBarPanel.add(appLabel);

	    JPanel dashboardMenuContent = new JPanel();
	    dashboardMenuContent.setOpaque(false);
	    dashboardMenuContent.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));

	    JPanel dashboardMenu = new JPanel();
	    dashboardMenu.setBounds(10, 60, 180, 30);
	    dashboardMenu.setOpaque(false);
	    dashboardMenu.setLayout(new BorderLayout());
	    dashboardMenu.add(dashboardMenuContent, BorderLayout.EAST);

	    sideBarPanel.add(dashboardMenu);
	    

	    JLabel dashboardLabel = new JLabel("Dashboard");
	    dashboardMenu.add(dashboardLabel, BorderLayout.WEST);
	    dashboardLabel.setIcon(new ImageIcon(HomeFrame.class.getResource("/icons/dashboard_logo.png")));
	    dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
	    dashboardLabel.setForeground(Color.WHITE);

	    // Corso menu (with submenu)
	    JPanel corsoMenu = new JPanel();
	    corsoMenu.setBounds(10, 100, 180, 30);
	    corsoMenu.setOpaque(false);
	    corsoMenu.setLayout(new BorderLayout());
	    JLabel corsoLabel = new JLabel("Corso");
	    corsoLabel.setIcon(new ImageIcon(HomeFrame.class.getResource("/icons/corso_logo.png")));
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
	    
	    // fa visualizzare il sottomenu
	    corsoMenu.addMouseListener(new MouseAdapter() {
	        public void mouseEntered(MouseEvent e) {
	            corsoSubMenu.setVisible(true);
	        }
	        public void mouseExited(MouseEvent e) {
	            // Hide submenu only if mouse is not over submenu
	            java.awt.Point p = e.getPoint();
	            java.awt.Rectangle subMenuBounds = corsoSubMenu.getBounds();
	            if (!subMenuBounds.contains(e.getXOnScreen() - sideBarPanel.getLocationOnScreen().x,
	                                       e.getYOnScreen() - sideBarPanel.getLocationOnScreen().y)) {
	                corsoSubMenu.setVisible(false);
	            }
	        }
	    });
	    // toglie la visualizzazione del sottomenu
	    corsoSubMenu.addMouseListener(new MouseAdapter() {
	        public void mouseExited(MouseEvent e) {
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
