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
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import controller.Controller;
import session.SessionChef;
import exception.AlreadyExistsException;
import exception.OperationException;
import gui.HomeFrame;

public class CreaNuovoCorsoPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255); 
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    
    private static final int HORIZONTAL_PADDING = 20;
    
    private JLabel pageTitle;
    private JPanel titleContainer;
    
    private JPanel contentPanel;
    private JPanel formCard;
    private JPanel formPanel;
    private JTextField nomeField;
    private JTextField categoriaField;
    private JDateChooser dataInizioChooser;
    private JComboBox<String> frequenzaComboBox;
    private JTextField numeroSessioniField;
    private JButton inviaButton;
    
    private Controller controller;

    public CreaNuovoCorsoPanel() {
        controller = new Controller();
        
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(20, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));
        
        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
    }

    private JPanel buildTitlePanel() {
        titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        titleContainer.setOpaque(false);
        
        pageTitle = new JLabel("Crea Nuovo Corso");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_DARK);
        
        titleContainer.add(pageTitle);

        return titleContainer;
    }
    
    private JPanel buildContentPanel() {
        contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setOpaque(false);

        formCard = new JPanel();
        formCard.setOpaque(true);
        formCard.setBackground(Color.WHITE);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        
        Border lineBorder = BorderFactory.createLineBorder(BORDER_GRAY, 1);
        Border paddingBorder = new EmptyBorder(20, 24, 20, 24);
        formCard.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        formPanel = buildFormPanel();
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = buildButtonPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        formCard.add(formPanel);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(buttonPanel);
        
        formCard.setMaximumSize(new Dimension(formCard.getPreferredSize().width, formCard.getPreferredSize().height));
        
        contentPanel.add(formCard);
        return contentPanel;
    }
    
    private JPanel buildFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        Dimension fieldSize = new Dimension(300, 32);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(createFormLabel("Nome Corso:"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(2, 5, 5, 5);
        nomeField = new JTextField();
        nomeField.setPreferredSize(fieldSize);
        nomeField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(nomeField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(createFormLabel("Categoria:"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(2, 5, 5, 5);
        categoriaField = new JTextField();
        categoriaField.setPreferredSize(fieldSize);
        categoriaField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(categoriaField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(createFormLabel("Data Inizio:"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(2, 5, 5, 5);
        dataInizioChooser = new JDateChooser();
        dataInizioChooser.setPreferredSize(fieldSize);
        dataInizioChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(dataInizioChooser, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(createFormLabel("Frequenza:"), gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(2, 5, 5, 5);
        String[] frequenze = {"Settimanale", "Bisettimanale", "Trisettimanale"};
        frequenzaComboBox = new JComboBox<>(frequenze);
        frequenzaComboBox.setPreferredSize(fieldSize);
        frequenzaComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        frequenzaComboBox.setBackground(Color.WHITE);
        formPanel.add(frequenzaComboBox, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 5, 0, 5);
        formPanel.add(createFormLabel("Numero Sessioni:"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(2, 5, 5, 5);
        numeroSessioniField = new JTextField();
        numeroSessioniField.setPreferredSize(fieldSize);
        numeroSessioniField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(numeroSessioniField, gbc);

        return formPanel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }
    
    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setOpaque(false);
        
        inviaButton = new JButton("Crea Corso");
        inviaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        inviaButton.setBackground(PRIMARY_BLUE);
        inviaButton.setForeground(Color.WHITE);
        inviaButton.setOpaque(true);
        inviaButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        inviaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        inviaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		insertNewCourse();
        	}
        });
        
        buttonPanel.add(inviaButton);
        return buttonPanel;
    }
    
    //====================================================
    // FUNZIONI AUSILIARIE - HANDLER 
    //====================================================
    
    private void insertNewCourse() {
    	String nomeCorso = nomeField.getText();
    	String categoria = categoriaField.getText();
    	Date dataInizioUtil = dataInizioChooser.getDate();
    	String frequenza = (String) frequenzaComboBox.getSelectedItem();
    	String numSessioni = numeroSessioniField.getText();
    	try {
    		controller.inserimentoCorso(nomeCorso, categoria, dataInizioUtil, numSessioni, frequenza);
    		JOptionPane.showMessageDialog(this, "Corso inserito con successo!", "Success", JOptionPane.INFORMATION_MESSAGE);
    		HomeFrame home = (HomeFrame) SwingUtilities.getWindowAncestor(this);
            if (home != null) {
                home.showMainPanel(new GestioneCorsiPanel());
            }
    	}
    	catch(AlreadyExistsException ex) {
    		JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
    	} catch(OperationException ex) {
    		JOptionPane.showMessageDialog(this, ex.getMessage(), "Operation Error", JOptionPane.ERROR_MESSAGE);
    		
    		try {
    			if(Integer.parseInt(numSessioni) <= 0) {
        			numeroSessioniField.setText("");
        		}
    		}catch(NumberFormatException nfe) {
    			numeroSessioniField.setText("");
    		}
    		
    		if(!controller.isOnlyLettersAndSpaces(nomeCorso)) {
    			nomeField.setText("");
    		}
    		if(!controller.isOnlyLettersAndSpaces(categoria)) {
    			categoriaField.setText("");
    		}
    	}
    }
    
}