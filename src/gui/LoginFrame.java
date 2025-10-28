package gui;

import controller.ChefController;
import exception.InvalidCredentialsException;
import exception.OperationException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.ImageIcon;
import java.net.URL;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // elementi grafici
    private final JPanel contentPane; //rimane costante fino al termine del programma
    private JPanel sideBarPanel;
    private JLabel brandTitle;
    private JLabel brandSubtitle;
    private JPanel accentLine;
    private JPanel loginPanel;
    private JPanel formCard;
    private JLabel welcomeLabel;
    private JLabel welcomeSubtitle;
    private JLabel lblAccess;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel registerHintLabel;
    private JButton loginButton;

    // controller
    private ChefController chefController;

    // frame indirizzabili
    private HomeFrame homeFrame;
    private RegisterFrame registerFrame;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);

                HomeFrame frame2 = new HomeFrame(frame);
                RegisterFrame frame3 = new RegisterFrame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public LoginFrame() {
        super("UninaFoodLab");
        
        URL iconURL = getClass().getResource("/icon/UF_icon.png");
		if (iconURL != null) {
		    setIconImage(new ImageIcon(iconURL).getImage());
		}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(850, 500);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        buildSideBar(); // void per costruire la barra laterale "UninaFoodLab"
        buildLoginPanel(); // void per costruire il pannello del login / reindirizzamento alla registrazione
    }

    private void buildSideBar() {
        sideBarPanel = new JPanel();
        sideBarPanel.setBackground(SystemColor.textHighlight);
        sideBarPanel.setLayout(null);
        sideBarPanel.setBounds(0, 0, 300, 500);
        contentPane.add(sideBarPanel);

        brandTitle = new JLabel("UninaFoodLab", SwingConstants.CENTER);
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        brandTitle.setBounds(20, 90, 260, 36);
        sideBarPanel.add(brandTitle);

        brandSubtitle = new JLabel("Cucina · Impara · Condividi", SwingConstants.CENTER);
        brandSubtitle.setForeground(new Color(230, 242, 255));
        brandSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        brandSubtitle.setBounds(20, 130, 260, 24);
        sideBarPanel.add(brandSubtitle);

        accentLine = new JPanel();
        accentLine.setBackground(new Color(255, 255, 255, 140));
        accentLine.setBounds(70, 170, 160, 3);
        sideBarPanel.add(accentLine);

        String[] sidebarPoints = {
            "• Pianifica le sessioni",
            "• Gestisci le ricette",
            "• Coordina i corsi"
        };
        for (int i = 0; i < sidebarPoints.length; i++) {
            JLabel pointLabel = new JLabel(sidebarPoints[i]);
            pointLabel.setForeground(new Color(230, 242, 255));
            pointLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            pointLabel.setHorizontalAlignment(SwingConstants.CENTER);
            pointLabel.setBounds(20, 200 + (i * 28), 260, 20);
            sideBarPanel.add(pointLabel);
        }

    }

    private void buildLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setBackground(new Color(245, 247, 252));
        loginPanel.setLayout(null);
        loginPanel.setBounds(300, 0, 550, 500);
        contentPane.add(loginPanel);

        int panelWidth = 550;
        int cardWidth = 360;
        int cardHeight = 330;
        int cardX = (panelWidth - cardWidth) / 2;
        int cardY = 60;

        formCard = new JPanel();
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new LineBorder(new Color(220, 225, 235), 1, true));
        formCard.setLayout(null);
        formCard.setBounds(cardX, cardY, cardWidth, cardHeight);
        loginPanel.add(formCard);

        welcomeLabel = new JLabel("Bentornato!", SwingConstants.LEFT);
        welcomeLabel.setForeground(new Color(51, 63, 81));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setBounds(24, 18, 240, 28);
        formCard.add(welcomeLabel);

        welcomeSubtitle = new JLabel("Inserisci le tue credenziali per continuare", SwingConstants.LEFT);
        welcomeSubtitle.setForeground(new Color(105, 115, 134));
        welcomeSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeSubtitle.setBounds(24, 44, 300, 18);
        formCard.add(welcomeSubtitle);

        lblAccess = new JLabel("Effettua il login", SwingConstants.LEFT);
        lblAccess.setForeground(new Color(51, 63, 81));
        lblAccess.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblAccess.setBounds(24, 72, 200, 20);
        formCard.add(lblAccess);

        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(24, 100, 300, 20);
        formCard.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailField.setBounds(24, 120, 312, 32);
        formCard.add(emailField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordLabel.setBounds(24, 164, 300, 20);
        formCard.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBounds(24, 184, 312, 32);
        formCard.add(passwordField);

        loginButton = new JButton("Log in");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginButton.setBackground(SystemColor.textHighlight);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(110, 236, 140, 40);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                executeLogin();
            }
        });
        formCard.add(loginButton);

        registerHintLabel = new JLabel("Non hai un account? Registrati per iniziare.", SwingConstants.CENTER);
        registerHintLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        registerHintLabel.setForeground(new Color(72, 107, 196));
        registerHintLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerHintLabel.setBounds(24, 295, 312, 20);
        registerHintLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                goToRegistrationFrame();
            }
        });
        formCard.add(registerHintLabel);
    }
    
    // =========================================================================
 	// FUNZIONI AUSILIARIE - HANDLER 
 	// =========================================================================

    private void executeLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        try {
            chefController = new ChefController();
            chefController.loginChef(email, password);
            JOptionPane.showMessageDialog(LoginFrame.this, "Login effettuato con successo!", "Success", JOptionPane.INFORMATION_MESSAGE);
            LoginFrame.this.setVisible(false);
            homeFrame = new HomeFrame(this);
            homeFrame.setVisible(true);
        } catch (InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            emailField.setText("");
            passwordField.setText("");
        } catch (OperationException ex) {
            JOptionPane.showMessageDialog(LoginFrame.this, ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
            emailField.setText("");
            passwordField.setText("");
        }
    }

    private void goToRegistrationFrame() {
        registerFrame = new RegisterFrame();
        LoginFrame.this.setVisible(false);
        registerFrame.setVisible(true);
    }
}
