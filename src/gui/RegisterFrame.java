package gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.ChefController;
import exception.*;

public class RegisterFrame extends JFrame {

	// elementi grafici
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel sideBarPanel;
	private ImageIcon logoIcon;
	private JLabel lblNewLabel; 
	private JPanel registerPanel;
	private ImageIcon arrowIcon;
	private JButton backButton;
	private JLabel lblRegistration;
	private JLabel nomeLabel;
	private JTextField nomeField;
	private JLabel cognomeLabel;
	private JTextField cognomeField;
	private JLabel emailLabel;
	private JTextField emailField;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JButton registerButton;
	
	// chef controller
	private ChefController chefController;
	
	// next screen - Login back
	private LoginFrame loginFrame;

	/**
	 * Create the frame.
	 */
	public RegisterFrame() {
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
        sideBarPanel.setBounds(550, 0, 300, 450);
        contentPane.add(sideBarPanel);
        
        logoIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/logo_UninaFoodLab.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon logoIcon2 = new ImageIcon(scaledImage);
        sideBarPanel.setLayout(null);
        JLabel logoLabel = new JLabel(logoIcon2);
        logoLabel.setBounds(47, 100, 206, 178);
        sideBarPanel.add(logoLabel);

        // "UninaFoodLab" label
        lblNewLabel = new JLabel("UninaFoodLab", JLabel.CENTER);
        lblNewLabel.setForeground(Color.WHITE);
        lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNewLabel.setBounds(67, 271, 166, 32); 
        sideBarPanel.add(lblNewLabel);
        
        registerPanel = new JPanel();
        registerPanel.setBounds(-50, 0, 600, 450);
        contentPane.add(registerPanel);
        registerPanel.setLayout(null);
        
        // small arrow to go back to the login
        arrowIcon = new ImageIcon(System.getProperty("user.dir") + "/icons/arrow_back.png");
        Image arrowImg = arrowIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        ImageIcon arrowIconScaled = new ImageIcon(arrowImg);

        // Create the button
        backButton = new JButton(arrowIconScaled);
        backButton.setBounds(70, 15, 32, 32); // Position at top-left
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		goBackToLogin();
        	}
        });
        registerPanel.add(backButton);
        
        lblRegistration = new JLabel("Registrati ora!", JLabel.CENTER);
        lblRegistration.setForeground(SystemColor.textHighlight);
        lblRegistration.setFont(new Font("SansSerif", Font.BOLD, 24));
        int regWidth = 300;
        int regHeight = 40;
        lblRegistration.setBounds(154, 10, regWidth, regHeight);
        registerPanel.add(lblRegistration);
        
        // Nome label
        nomeLabel = new JLabel("Nome");
        nomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nomeLabel.setBounds(154, 50, 300, 25);
        registerPanel.add(nomeLabel);
        
        // Nome text field
        nomeField = new JTextField();
        nomeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nomeField.setBounds(154, 80, 300, 30);
        registerPanel.add(nomeField);
        
        // Cognome label
        cognomeLabel = new JLabel("Cognome");
        cognomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cognomeLabel.setBounds(154, 120, 300, 25);
        registerPanel.add(cognomeLabel);
        
        // Cognome text field
        cognomeField = new JTextField();
        cognomeField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cognomeField.setBounds(154, 155, 300, 30);
        registerPanel.add(cognomeField);
        
        // Email label
        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailLabel.setBounds(154, 195, 300, 25);
        registerPanel.add(emailLabel);

        // Email text field
        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailField.setBounds(154, 230, 300, 30);
        registerPanel.add(emailField);
        
        // Password label
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordLabel.setBounds(154, 270, 300, 25);
        registerPanel.add(passwordLabel);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBounds(154, 305, 300, 30);
        registerPanel.add(passwordField);
        
        // Log in button
        registerButton = new JButton("Registrati");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.setBackground(SystemColor.textHighlight);
        registerButton.setForeground(Color.WHITE);
        registerButton.setBounds(235, 350, 140, 40);
        registerButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeRegistration();
        	}
        });
        registerPanel.add(registerButton);
        
	}
	
	// FUNZIONI AUSILIARIE
	public void executeRegistration() {
		String nome = nomeField.getText();
		String cognome = cognomeField.getText();
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());
		try {
			chefController = new ChefController();
			chefController.registrazioneChef(nome, cognome, email, password);
			JOptionPane.showMessageDialog(RegisterFrame.this, "Registrazione avvenuta con successo!", "Success", JOptionPane.INFORMATION_MESSAGE);
			RegisterFrame.this.setVisible(false);
			loginFrame = new LoginFrame();
			loginFrame.setVisible(true);
		} catch(OperationException ex) {
			JOptionPane.showMessageDialog(RegisterFrame.this, ex.getMessage(), "Register Error", JOptionPane.ERROR_MESSAGE);
			 if (!chefController.isValidNameOrSurname(nome)) {
		         nomeField.setText("");
		     }
		     if (!chefController.isValidNameOrSurname(cognome)) {
		         cognomeField.setText("");
		     }
		     if (!chefController.isValidEmail(email)) {
		         emailField.setText("");
		     }
		     if (!chefController.checkPasswordRequirements(password)) {
		         passwordField.setText("");
		     }
		} catch(AlreadyExistsException ex) {
			JOptionPane.showMessageDialog(RegisterFrame.this, ex.getMessage(), "Register Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void goBackToLogin() {
		dispose();
		new LoginFrame().setVisible(true);
	}
}
