package gui;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import controller.ChefController;
import exception.*;
import java.io.File;
import javax.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;



public class LoginFrame extends JFrame {
	
	// elementi grafici
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel sideBarPanel;
	private JLabel lblNewLabel;
	private JPanel loginPanel;
	private JLabel lblAccess;
	private JLabel emailLabel;
	private JTextField emailField;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton registerButton;
	
	// chef controller
	private ChefController chefController;
	
	// next screen - Home or Registration
	private HomeFrame homeFrame;
	private RegisterFrame registerFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);

					HomeFrame frame2 = new HomeFrame(frame);
					RegisterFrame frame3 = new RegisterFrame();
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
        sideBarPanel.setBounds(0, 0, 300, 450);
        contentPane.add(sideBarPanel);
        sideBarPanel.setLayout(null);
        
        // "UninaFoodLab" label
        lblNewLabel = new JLabel("UninaFoodLab", JLabel.CENTER);
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNewLabel.setBounds(47, 276, 206, 43); // (300-206)/2 = 47, 100+150+20=270
        sideBarPanel.add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setIcon(new ImageIcon(LoginFrame.class.getResource("/icons/logo_UninaFoodLab.png")));
        lblNewLabel_1.setBounds(47, 93, 206, 172);
        sideBarPanel.add(lblNewLabel_1);

        loginPanel = new JPanel();
        loginPanel.setBounds(298, 0, 600, 450);
        contentPane.add(loginPanel);
        loginPanel.setLayout(null);

        // "Welcome Back!" label
        lblAccess = new JLabel("Effettua l'accesso!", JLabel.CENTER);
        lblAccess.setForeground(SystemColor.textHighlight);
        lblAccess.setFont(new Font("SansSerif", Font.BOLD, 24));
        int welcomeWidth = 300;
        int welcomeHeight = 40;
        lblAccess.setBounds(130, 52, welcomeWidth, welcomeHeight);
        loginPanel.add(lblAccess);
        
        // Email label
        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailLabel.setBounds(130, 118, 300, 25);
        loginPanel.add(emailLabel);

        // Email text field
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailField.setBounds(130, 153, 300, 30);
        loginPanel.add(emailField);

        // Password label
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordLabel.setBounds(130, 193, 300, 25);
        loginPanel.add(passwordLabel);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBounds(130, 228, 300, 30);
        loginPanel.add(passwordField);

        // Log in button
        loginButton = new JButton("Log in");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBackground(SystemColor.textHighlight);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(130, 279, 140, 40);
        loginButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeLogin();
        	}
        });
        loginPanel.add(loginButton);

        // Register now button
        registerButton = new JButton("Registrati ora");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.setBackground(new Color(64, 224, 208)); // Turquoise
        registerButton.setForeground(Color.WHITE);
        registerButton.setBounds(290, 279, 140, 40);
        registerButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		goToRegistrationFrame();
        	}
        });
        loginPanel.add(registerButton);
        
        setVisible(true);

	}
	
	// FUNZIONI AUSILIARIE
	public void executeLogin() {
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());
		try {
			chefController = new ChefController();
			chefController.loginChef(email, password);
			JOptionPane.showMessageDialog(LoginFrame.this, "Login effettuato con successo!", "Success", JOptionPane.INFORMATION_MESSAGE);
			LoginFrame.this.setVisible(false);
			homeFrame = new HomeFrame(this);
			homeFrame.setVisible(true);
		} catch(InvalidCredentialsException ex) {
			 JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
			 emailField.setText("");
	         passwordField.setText("");
		} catch(OperationException ex) {
			JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
			emailField.setText("");
	        passwordField.setText("");
		}
	}
	
	public void goToRegistrationFrame() {
		registerFrame = new RegisterFrame();
		LoginFrame.this.setVisible(false);
		registerFrame.setVisible(true);
	}
	

public ImageIcon loadImage(String resourcePath, int width, int height) {
    try {
        BufferedImage originalImage = ImageIO.read(new File(resourcePath));
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return new ImageIcon(resizedImage);
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}
}
