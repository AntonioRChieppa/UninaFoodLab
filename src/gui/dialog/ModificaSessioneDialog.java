package gui.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
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

import com.toedter.calendar.JDateChooser;

import controller.Controller;
import dto.CorsoDTO;
import dto.SessioneDTO;
import dto.SessioneInPresenzaDTO;
import dto.SessioneOnlineDTO;
import exception.AlreadyExistsException;
import exception.NotFoundException;
import exception.OperationException;
import exception.UnauthorizedOperationException;
import session.SessionChef;

public class ModificaSessioneDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color LIGHT_GRAY_BACKGROUND = new Color(248, 249, 252);

    private JPanel formCard;
    private JPanel formPanel;
    private JComboBox<String> tipoSessioneComboBox;
    private JComboBox<CorsoDTO> corsoComboBox;
    private JTextField argomentoField;
    private JDateChooser dataSessioneChooser;
    private JComboBox<Integer> oraComboBox;
    private JComboBox<Integer> minutiComboBox;
    private JPanel dynamicPanelContainer;
    private CardLayout cardLayout;
    private JPanel panelInPresenza;
    private JPanel panelOnline;
    private JTextField sedeField;
    private JTextField edificioField;
    private JTextField aulaField;
    private JTextField linkConferenzaField;
    private JButton salvaButton;
    private JButton annullaButton;

    private Controller controller;
    private SessioneDTO sessioneOriginale;

    private boolean saved = false;

    private static final String PRESENZA_CARD = "In Presenza";
    private static final String ONLINE_CARD = "Online";

    public ModificaSessioneDialog(JFrame owner, SessioneDTO sessioneDaModificare, Controller controller) {
        super(owner, "Modifica Sessione", true);
        this.sessioneOriginale = sessioneDaModificare;
        this.controller = controller;

        setLayout(new BorderLayout());
        setResizable(false);
        setBackground(LIGHT_GRAY_BACKGROUND);

        add(buildContentPanel(), BorderLayout.CENTER);

        prefillForm();

        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPanel.setOpaque(true);
        contentPanel.setBackground(LIGHT_GRAY_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        formCard = new JPanel();
        formCard.setOpaque(true);
        formCard.setBackground(Color.WHITE);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        Border lineBorder = BorderFactory.createLineBorder(BORDER_GRAY, 1);
        Border paddingBorder = new EmptyBorder(15, 24, 15, 24);
        formCard.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        formPanel = buildFormPanel();
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = buildButtonPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formCard.add(formPanel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(buttonPanel);

        contentPanel.add(formCard);
        return contentPanel;
    }

    private JPanel buildFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        Dimension fieldSize = new Dimension(300, 30);
        Dimension halfFieldSize = new Dimension(145, 30);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 0, 5);

        gbc.gridy = 0; formPanel.add(createFormLabel("Tipo Sessione:"), gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 5, 3, 5);
        tipoSessioneComboBox = new JComboBox<>(new String[]{PRESENZA_CARD, ONLINE_CARD});
        tipoSessioneComboBox.setPreferredSize(fieldSize);
        tipoSessioneComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tipoSessioneComboBox.setBackground(Color.LIGHT_GRAY);
        tipoSessioneComboBox.setEnabled(false);
        formPanel.add(tipoSessioneComboBox, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(4, 5, 0, 5); formPanel.add(createFormLabel("Corso:"), gbc);
        gbc.gridy = 3; gbc.insets = new Insets(0, 5, 3, 5);
        corsoComboBox = new JComboBox<>();
        corsoComboBox.setPreferredSize(fieldSize);
        corsoComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        corsoComboBox.setBackground(Color.WHITE);
        loadCorsiComboBox();
        formPanel.add(corsoComboBox, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(4, 5, 0, 5); formPanel.add(createFormLabel("Argomento:"), gbc);
        gbc.gridy = 5; gbc.insets = new Insets(0, 5, 3, 5);
        argomentoField = new JTextField();
        argomentoField.setPreferredSize(fieldSize);
        argomentoField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(argomentoField, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(4, 5, 0, 5); formPanel.add(createFormLabel("Data Sessione:"), gbc);
        gbc.gridy = 7; gbc.insets = new Insets(0, 5, 3, 5);
        dataSessioneChooser = new JDateChooser();
        dataSessioneChooser.setPreferredSize(fieldSize);
        dataSessioneChooser.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formPanel.add(dataSessioneChooser, gbc);

        gbc.gridy = 8; gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 0, 5); formPanel.add(createFormLabel("Ora Inizio:"), gbc);

        gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 5, 3, 5);
        JPanel timePanel = new JPanel(new GridBagLayout());
        timePanel.setOpaque(false);
        GridBagConstraints gbcTime = new GridBagConstraints();
        gbcTime.insets = new Insets(0, 0, 0, 5);
        gbcTime.fill = GridBagConstraints.NONE;
        gbcTime.anchor = GridBagConstraints.WEST;

        oraComboBox = new JComboBox<>();
        for (int i = 0; i < 24; i++) oraComboBox.addItem(i);
        oraComboBox.setPreferredSize(halfFieldSize);
        oraComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        oraComboBox.setBackground(Color.WHITE);
        gbcTime.gridx = 0; timePanel.add(oraComboBox, gbcTime);

        minutiComboBox = new JComboBox<>();
        for (int i = 0; i < 60; i++) minutiComboBox.addItem(i);
        minutiComboBox.setPreferredSize(halfFieldSize);
        minutiComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        minutiComboBox.setBackground(Color.WHITE);
        gbcTime.gridx = 1; gbcTime.insets = new Insets(0, 0, 0, 0);
        timePanel.add(minutiComboBox, gbcTime);

        gbcTime.gridx = 2; gbcTime.weightx = 1.0; timePanel.add(Box.createHorizontalGlue(), gbcTime);

        formPanel.add(timePanel, gbc);


        gbc.gridy = 10; gbc.gridwidth = 2; gbc.insets = new Insets(4, 0, 0, 0); gbc.fill = GridBagConstraints.HORIZONTAL;

        cardLayout = new CardLayout();
        dynamicPanelContainer = new JPanel(cardLayout);
        dynamicPanelContainer.setOpaque(false);

        buildPanelInPresenza();
        buildPanelOnline();

        int hPresenza = panelInPresenza.getPreferredSize().height;
        int hOnline = panelOnline.getPreferredSize().height;
        int maxHeight = Math.max(hPresenza, hOnline);

        dynamicPanelContainer.setPreferredSize(new Dimension(fieldSize.width, maxHeight));

        dynamicPanelContainer.add(panelInPresenza, PRESENZA_CARD);
        dynamicPanelContainer.add(panelOnline, ONLINE_CARD);

        formPanel.add(dynamicPanelContainer, gbc);

        return formPanel;
    }

    private void buildPanelInPresenza() {
        panelInPresenza = new JPanel(new GridBagLayout());
        panelInPresenza.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        Dimension fullWidthFieldSize = new Dimension(300, 30);
        Dimension halfWidthFieldSize = new Dimension(145, 30);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(2, 5, 0, 5); panelInPresenza.add(createFormLabel("Sede:"), gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 5, 2, 5);
        sedeField = new JTextField(); sedeField.setPreferredSize(fullWidthFieldSize); sedeField.setFont(new Font("SansSerif", Font.PLAIN, 13)); panelInPresenza.add(sedeField, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.insets = new Insets(4, 5, 0, 5); panelInPresenza.add(createFormLabel("Edificio:"), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(4, 5, 0, 5); panelInPresenza.add(createFormLabel("Aula:"), gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        gbc.insets = new Insets(0, 5, 2, 5);
        edificioField = new JTextField(); edificioField.setPreferredSize(halfWidthFieldSize); edificioField.setFont(new Font("SansSerif", Font.PLAIN, 13)); panelInPresenza.add(edificioField, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 2, 5);
        aulaField = new JTextField(); aulaField.setPreferredSize(halfWidthFieldSize); aulaField.setFont(new Font("SansSerif", Font.PLAIN, 13)); panelInPresenza.add(aulaField, gbc);
    }

    private void buildPanelOnline() {
        panelOnline = new JPanel(new GridBagLayout());
        panelOnline.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        Dimension fieldSize = new Dimension(300, 30);
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0; gbc.weighty = 0;
        gbc.insets = new Insets(2, 5, 0, 5); panelOnline.add(createFormLabel("Link Conferenza:"), gbc);
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 2, 5);
        linkConferenzaField = new JTextField(); linkConferenzaField.setPreferredSize(fieldSize); linkConferenzaField.setFont(new Font("SansSerif", Font.PLAIN, 13)); panelOnline.add(linkConferenzaField, gbc);

        gbc.gridy = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelOnline.add(Box.createVerticalGlue(), gbc);
    }


    private void loadCorsiComboBox() {
        try {
            List<CorsoDTO> corsi = controller.visualizzaCorsiPerChef();

            Vector<CorsoDTO> model = new Vector<>(corsi);
            corsoComboBox.setModel(new DefaultComboBoxModel<>(model));

            corsoComboBox.setRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = 1L;
                @Override
                public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof CorsoDTO) {
                        setText(((CorsoDTO) value).getNomeCorso());
                    } else if (value == null && index == -1) {
                        setText("");
                    }
                    return this;
                }
            });

        } catch(NotFoundException ex) {
            corsoComboBox.setModel(new DefaultComboBoxModel<>());
        } catch (OperationException ex) {
            JOptionPane.showMessageDialog(this, "Errore caricamento corsi: " + ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
            corsoComboBox.setModel(new DefaultComboBoxModel<>());
        }
    }


    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
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
        annullaButton.addActionListener(e -> dispose());

        buttonPanel.add(salvaButton);
        buttonPanel.add(annullaButton);
        return buttonPanel;
    }

    //=====================================================
    // FUNZIONI AUSILIARIE
    //=====================================================
    
    private void prefillForm() {
        if (sessioneOriginale == null) return;

        argomentoField.setText(sessioneOriginale.getArgomento());
        if (sessioneOriginale.getDataSessione() != null) {
            Date date = Date.from(sessioneOriginale.getDataSessione().atStartOfDay(ZoneId.systemDefault()).toInstant());
            dataSessioneChooser.setDate(date);
        }
        if (sessioneOriginale.getOraInizio() != null) {
            oraComboBox.setSelectedItem(sessioneOriginale.getOraInizio().getHour());
            minutiComboBox.setSelectedItem(sessioneOriginale.getOraInizio().getMinute());
        }

        CorsoDTO corsoCorrente = sessioneOriginale.getCorsoSessione();
        if (corsoCorrente != null) {
             for (int i = 0; i < corsoComboBox.getItemCount(); i++) {
                 CorsoDTO item = corsoComboBox.getItemAt(i);
                 if (item instanceof CorsoDTO && ((CorsoDTO)item).getId() == corsoCorrente.getId()) {
                    corsoComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        if ("presenza".equalsIgnoreCase(sessioneOriginale.getTipoSessione())) {
            tipoSessioneComboBox.setSelectedItem(PRESENZA_CARD);
            cardLayout.show(dynamicPanelContainer, PRESENZA_CARD);
            try {
                SessioneInPresenzaDTO sessioneIP = controller.getSessioneInPresenzaById(sessioneOriginale.getIdSessione());
                if (sessioneIP != null) {
                    sedeField.setText(sessioneIP.getSede());
                    edificioField.setText(sessioneIP.getEdificio());
                    aulaField.setText(sessioneIP.getAula());
                }
            } catch (OperationException ex) {
                 JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        } else if ("online".equalsIgnoreCase(sessioneOriginale.getTipoSessione())) {
            tipoSessioneComboBox.setSelectedItem(ONLINE_CARD);
            cardLayout.show(dynamicPanelContainer, ONLINE_CARD);
             try {
                SessioneOnlineDTO sessioneOn = controller.getSessioneOnlineById(sessioneOriginale.getIdSessione());
                if (sessioneOn != null) {
                    linkConferenzaField.setText(sessioneOn.getLinkConferenza());
                }
            } catch (OperationException ex) {
                 JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvaModifiche() {
        try {
            int idSessione = sessioneOriginale.getIdSessione();

            CorsoDTO corsoSelezionato = (CorsoDTO) corsoComboBox.getSelectedItem();
            String argomento = argomentoField.getText();
            Date dataSessioneUtil = dataSessioneChooser.getDate();
            Integer ora = (Integer) oraComboBox.getSelectedItem();
            Integer minuti = (Integer) minutiComboBox.getSelectedItem();
            LocalTime oraInizio = LocalTime.of(ora, minuti);

            if ("presenza".equalsIgnoreCase(sessioneOriginale.getTipoSessione())) {
                String sede = sedeField.getText();
                String edificio = edificioField.getText();
                String aula = aulaField.getText();
                controller.aggiornaSessioneInPresenza(idSessione, argomento, oraInizio, dataSessioneUtil, corsoSelezionato.getId(), sede, edificio, aula);

            } else if ("online".equalsIgnoreCase(sessioneOriginale.getTipoSessione())) {
                String link = linkConferenzaField.getText();
                controller.aggiornaSessioneOnline(idSessione, argomento, oraInizio, dataSessioneUtil, corsoSelezionato.getId(), link);
            }

            this.saved = true;
            dispose();

        } catch (UnauthorizedOperationException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (NotFoundException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } catch(OperationException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return this.saved;
    }
}