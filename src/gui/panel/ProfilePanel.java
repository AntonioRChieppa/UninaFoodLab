package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import controller.ChefController;
import gui.HomeFrame;
import gui.LoginFrame;
import session.SessionChef;
import exception.OperationException;
import exception.NotFoundException;

public class ProfilePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color BG_COLOR = new Color(245, 247, 252);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final Color DELETE_RED = new Color(220, 53, 69);
    private static final Color READ_ONLY_BG = new Color(235, 235, 235);

    private static final int HORIZONTAL_PADDING = 20;

    private ChefController controller;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JToggleButton showPasswordButton;
    private JButton logoutButton;
    private JButton deleteAccountButton;

    private HomeFrame ownerFrame;

    public ProfilePanel(HomeFrame owner) {
        this.controller = new ChefController();
        this.ownerFrame = owner;

        setLayout(new BorderLayout(0, 15));
        setOpaque(true);
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(10, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        loadChefData();
    }

    private JPanel buildTitlePanel() {
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);
        JLabel pageTitle = new JLabel("Il Tuo Profilo");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setForeground(TEXT_DARK);
        titleContainer.add(pageTitle);
        return titleContainer;
    }

    private JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cardWrapper.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setOpaque(true);
        formCard.setBackground(CARD_BACKGROUND);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        Border lineBorder = BorderFactory.createLineBorder(BORDER_GRAY, 1);
        Border paddingBorder = new EmptyBorder(20, 24, 20, 24);
        formCard.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        formCard.add(buildFormPanel());
        formCard.add(Box.createVerticalStrut(25));
        formCard.add(buildActionButtons());

        cardWrapper.add(formCard);
        contentPanel.add(cardWrapper, BorderLayout.NORTH);

        return contentPanel;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        Dimension fieldSize = new Dimension(220, 32);
        Dimension buttonSize = new Dimension(80, 32);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 5, 4, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createFormLabel("Nome:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        nomeField = createDisplayField();
        nomeField.setPreferredSize(new Dimension(305, 32));
        formPanel.add(nomeField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
        formPanel.add(createFormLabel("Cognome:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        cognomeField = createDisplayField();
        cognomeField.setPreferredSize(new Dimension(305, 32));
        formPanel.add(cognomeField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
        formPanel.add(createFormLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        emailField = createDisplayField();
        emailField.setPreferredSize(new Dimension(305, 32));
        formPanel.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 1;
        formPanel.add(createFormLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 1;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(fieldSize);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setEditable(false);
        passwordField.setBackground(READ_ONLY_BG);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        showPasswordButton = new JToggleButton("Mostra");
        showPasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        showPasswordButton.setPreferredSize(buttonSize);
        showPasswordButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		togglePasswordVisibility();
        	}
        });
        formPanel.add(showPasswordButton, gbc);

        return formPanel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JTextField createDisplayField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setEditable(false);
        field.setBackground(READ_ONLY_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPanel buildActionButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutButton.setBackground(PRIMARY_BLUE);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        logoutButton.addActionListener(new ActionListener () {
        	public void actionPerformed(ActionEvent e) {
        		executeLogout();
        	}
        });
        		
        buttonPanel.add(logoutButton);
        
        deleteAccountButton = new JButton("Elimina Account");
        deleteAccountButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteAccountButton.setBackground(DELETE_RED);
        deleteAccountButton.setForeground(Color.WHITE);
        deleteAccountButton.setOpaque(true);
        deleteAccountButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        deleteAccountButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeEliminaAccount();
        	}
        	
        });
        buttonPanel.add(deleteAccountButton);
        
        return buttonPanel;
    }
    
    //==========================================
    // FUNZIONI AUSILIARIE
    //==========================================

    private void loadChefData() {
       nomeField.setText(SessionChef.getNameChef());
       cognomeField.setText(SessionChef.getChefSurname());
       emailField.setText(SessionChef.getChefMail());
            
       String password = SessionChef.getChefPassword();
       passwordField.setText(password);
       passwordField.setEchoChar('•');
            
    }

    private void togglePasswordVisibility() {
        if (showPasswordButton.isSelected()) {
            passwordField.setEchoChar((char) 0);
            showPasswordButton.setText("Nascondi");
        } else {
            passwordField.setEchoChar('•');
            showPasswordButton.setText("Mostra");
        }
    }

    private void executeLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Sei sicuro di voler effettuare il logout?",
            "Conferma Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            controller.logoutChef();
            ownerFrame.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void executeEliminaAccount() {
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Sei sicuro di voler eliminare il tuo account?\nTUTTI i corsi e le sessioni creati verranno persi.\nQuesta azione è irreversibile.",
            "Conferma Eliminazione Account",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaChef();
                
                JOptionPane.showMessageDialog(this, "Account eliminato con successo.", "Eliminazione completata", JOptionPane.INFORMATION_MESSAGE);
                
                controller.logoutChef();
                ownerFrame.dispose();
                new LoginFrame().setVisible(true);
                
            }catch(NotFoundException ex) {
            	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (OperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}