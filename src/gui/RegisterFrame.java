package gui;

import controller.ChefController;
import exception.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class RegisterFrame extends JFrame {

    // elementi grafici
    private static final long serialVersionUID = 1L;
	private final JPanel contentPane;
    private JPanel registerPanel;
    private JPanel sideBarPanel;
    private JPanel formCard;
    private JLabel welcomeTitle;
    private JButton backButton;
    private JLabel nomeLabel;
    private JTextField nomeField;
    private JLabel cognomeLabel;
    private JTextField cognomeField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel sidebarTitle;
    private JLabel sidebarSubtitle;
    private JPanel sidebarAccentLine;
	
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
		setSize(850,500);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		buildMainPanel();
		buildSideBar();
	}

	private void buildMainPanel() {
		registerPanel = new JPanel();
		registerPanel.setBackground(new Color(245, 247, 252));
		registerPanel.setLayout(null);
		registerPanel.setBounds(0, 0, 550, 500);
		contentPane.add(registerPanel);

		int panelWidth = 550;
		int cardWidth = 360;
		int cardHeight = 340;
		int cardX = (panelWidth - cardWidth) / 2;

		int titleHeight = 32;
		int titleY = 24;
		int buttonWidth = 150;
		int buttonHeight = 34;
		int buttonGap = 20;
		int cardY = 65;
		int buttonY = 55 + cardHeight + buttonGap;

		welcomeTitle = new JLabel("Registrati a UninaFoodLab", SwingConstants.CENTER);
		welcomeTitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 26));
		welcomeTitle.setForeground(new Color(45, 68, 118));
		welcomeTitle.setBounds(0, titleY, panelWidth, titleHeight);
		registerPanel.add(welcomeTitle);

		formCard = new JPanel();
		formCard.setBackground(Color.WHITE);
		formCard.setBorder(new LineBorder(new Color(220, 225, 235), 1, true));
		formCard.setBounds(cardX, cardY, cardWidth, cardHeight);
		formCard.setLayout(null);
		registerPanel.add(formCard);

		nomeLabel = new JLabel("Nome");
		nomeLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		nomeLabel.setBounds(24, 24, 300, 18);
		formCard.add(nomeLabel);

		nomeField = new JTextField();
		nomeField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		nomeField.setBounds(24, 44, 312, 32);
		formCard.add(nomeField);

		cognomeLabel = new JLabel("Cognome");
		cognomeLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		cognomeLabel.setBounds(24, 88, 300, 18);
		formCard.add(cognomeLabel);

		cognomeField = new JTextField();
		cognomeField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		cognomeField.setBounds(24, 108, 312, 32);
		formCard.add(cognomeField);

		emailLabel = new JLabel("Email");
		emailLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		emailLabel.setBounds(24, 152, 300, 18);
		formCard.add(emailLabel);

		emailField = new JTextField();
		emailField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		emailField.setBounds(24, 172, 312, 32);
		formCard.add(emailField);

		passwordLabel = new JLabel("Password");
		passwordLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		passwordLabel.setBounds(24, 216, 300, 18);
		formCard.add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
		passwordField.setBounds(24, 236, 312, 32);
		formCard.add(passwordField);

		registerButton = new JButton("Registrati");
		registerButton.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15));
		registerButton.setBackground(SystemColor.textHighlight);
		registerButton.setForeground(Color.WHITE);
		registerButton.setBounds(110, 288, 140, 40);
		registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                executeRegistration();
            }
        });
		formCard.add(registerButton);

		backButton = new JButton("‹ Torna al login");
		backButton.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
		backButton.setForeground(SystemColor.textHighlight);
		backButton.setBackground(new Color(232, 239, 251));
		backButton.setBorder(new LineBorder(new Color(210, 224, 248), 1, true));
		backButton.setFocusPainted(false);
		backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		backButton.setBounds(24, buttonY, buttonWidth, buttonHeight);
        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                goBackToLogin();
            }
        });
		registerPanel.add(backButton);
	}

	private void buildSideBar() {
		sideBarPanel = new JPanel();
		sideBarPanel.setBackground(SystemColor.textHighlight);
		sideBarPanel.setBounds(550, 0, 300, 500);
		sideBarPanel.setLayout(null);
		contentPane.add(sideBarPanel);

		sidebarTitle = new JLabel("UninaFoodLab", SwingConstants.CENTER);
		sidebarTitle.setForeground(Color.WHITE);
		sidebarTitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 28));
		sidebarTitle.setBounds(20, 100, 260, 36);
		sideBarPanel.add(sidebarTitle);

		sidebarSubtitle = new JLabel("Cucina · Impara · Cresci", SwingConstants.CENTER);
		sidebarSubtitle.setForeground(new Color(230, 242, 255));
		sidebarSubtitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
		sidebarSubtitle.setBounds(20, 140, 260, 24);
		sideBarPanel.add(sidebarSubtitle);

		sidebarAccentLine = new JPanel();
		sidebarAccentLine.setBackground(new Color(255, 255, 255, 140));
		sidebarAccentLine.setBounds(70, 180, 160, 3);
		sideBarPanel.add(sidebarAccentLine);

		String[] bulletPoints = {
			"• Pianifica le sessioni",
			"• Gestisci le ricette",
			"• Coordina i corsi"
		};
		for (int i = 0; i < bulletPoints.length; i++) {
			JLabel bulletLabel = new JLabel(bulletPoints[i], SwingConstants.CENTER);
			bulletLabel.setForeground(new Color(230, 242, 255));
			bulletLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 14));
			bulletLabel.setBounds(20, 210 + (i * 30), 260, 20);
			sideBarPanel.add(bulletLabel);
		}
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
