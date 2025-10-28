package gui.dialog;

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
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import com.toedter.calendar.JDateChooser;

import controller.Controller;
import dto.CorsoDTO;
import exception.NotFoundException;
import exception.OperationException;
import exception.UnauthorizedOperationException;

public class ModificaCorsoDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255); 
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    
    private JPanel formCard;
    private JPanel formPanel;
    private JTextField nomeField;
    private JTextField categoriaField;
    private JDateChooser dataInizioChooser;
    private JComboBox<String> frequenzaComboBox;
    private JTextField numeroSessioniField;
    private JButton salvaButton;
    private JButton annullaButton;
    
    private Controller controller;
    private CorsoDTO corso; 
    
    private boolean saved = false;

    public ModificaCorsoDialog(JFrame owner, CorsoDTO corso, Controller controller) {
        super(owner, "Modifica Corso", true); 
        
        this.corso = corso;
        this.controller = controller;
        
        URL iconURL = getClass().getResource("/icon/UF_icon.png");
		if (iconURL != null) {
		    setIconImage(new ImageIcon(iconURL).getImage());
		}
        
        setLayout(new BorderLayout());
        setResizable(false);
        
        add(buildContentPanel(), BorderLayout.CENTER);
        
        prefillForm();
        
        pack();
        setLocationRelativeTo(owner);
    }
    
    private JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setOpaque(true);
        contentPanel.setBackground(new Color(248, 249, 252));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

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
        formCard.add(Box.createVerticalStrut(20));
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
        nomeField.setEditable(false); 
        nomeField.setBackground(Color.LIGHT_GRAY); 
        formPanel.add(nomeField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 5, 0, 5);
        formPanel.add(createFormLabel("Categoria:"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(2, 5, 5, 5);
        categoriaField = new JTextField();
        categoriaField.setPreferredSize(fieldSize);
        categoriaField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(categoriaField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(10, 5, 0, 5);
        formPanel.add(createFormLabel("Data Inizio:"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(2, 5, 5, 5);
        dataInizioChooser = new JDateChooser();
        dataInizioChooser.setPreferredSize(fieldSize);
        dataInizioChooser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(dataInizioChooser, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(10, 5, 0, 5);
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
        gbc.insets = new Insets(10, 5, 0, 5);
        formPanel.add(createFormLabel("Numero Sessioni:"), gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(2, 5, 5, 5);
        numeroSessioniField = new JTextField();
        numeroSessioniField.setPreferredSize(fieldSize);
        numeroSessioniField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(numeroSessioniField, gbc);

        return formPanel;
    }
    
    private void prefillForm() {
        nomeField.setText(corso.getNomeCorso());
        categoriaField.setText(corso.getCategoria());
        
        Date date = Date.from(corso.getDataInizio().atStartOfDay(ZoneId.systemDefault()).toInstant());
        dataInizioChooser.setDate(date);
        
        frequenzaComboBox.setSelectedItem(corso.getFrequenzaSessioni());
        numeroSessioniField.setText(String.valueOf(corso.getNumeroSessioni()));
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }
    
    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        salvaButton = new JButton("Salva Modifiche");
        salvaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        salvaButton.setBackground(PRIMARY_BLUE);
        salvaButton.setForeground(Color.WHITE);
        salvaButton.setOpaque(true);
        salvaButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        salvaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        salvaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		salvaModifiche();
        	}
        });
        
        annullaButton = new JButton("Annulla");
        annullaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        annullaButton.setBackground(Color.GRAY);
        annullaButton.setForeground(Color.WHITE);
        annullaButton.setOpaque(true);
        annullaButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        annullaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        annullaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
        	}
        });

        buttonPanel.add(salvaButton);
        buttonPanel.add(annullaButton);
        return buttonPanel;
    }
    
    //==========================================================
    // FUNZIONI AUSILIARIE
    //==========================================================
    
    public void salvaModifiche() {
        try {
            String nomeCorso = nomeField.getText();
            String categoria = categoriaField.getText();
            Date dataInizioUtil = dataInizioChooser.getDate();
            String frequenza = (String) frequenzaComboBox.getSelectedItem();
            String numSessioniStr = numeroSessioniField.getText();

            controller.aggiornaCorso(nomeCorso, categoria, dataInizioUtil, numSessioniStr, frequenza);
            
            this.saved = true;
            dispose(); 

        } catch(UnauthorizedOperationException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } catch(NotFoundException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } catch(OperationException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    public boolean isSaved() {
        return this.saved;
    }
}