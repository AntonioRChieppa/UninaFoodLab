package gui;

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
import java.time.LocalTime;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;

import controller.Controller;
import dto.CorsoDTO;
import exception.OperationException;
import exception.AlreadyExistsException;
import exception.NotFoundException;

public class CreaNuovaSessionePanel extends JPanel {

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

    private Controller controller;

    private static final String PRESENZA_CARD = "In Presenza";
    private static final String ONLINE_CARD = "Online";


    public CreaNuovaSessionePanel() {
        controller = new Controller();

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(10, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);
    }

    private JPanel buildTitlePanel() {
        titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        pageTitle = new JLabel("Crea Nuova Sessione");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setForeground(TEXT_DARK);

        titleContainer.add(pageTitle);

        return titleContainer;
    }

    private JPanel buildContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        cardWrapper.setOpaque(false);

        formCard = new JPanel();
        formCard.setOpaque(true);
        formCard.setBackground(Color.WHITE);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        Border lineBorder = BorderFactory.createLineBorder(BORDER_GRAY, 1);
        Border paddingBorder = new EmptyBorder(15, 24, 15, 24);
        formCard.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        formPanel = buildFormPanel();
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        formCard.add(formPanel);

        cardWrapper.add(formCard);
        contentPanel.add(cardWrapper, BorderLayout.NORTH);

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
        tipoSessioneComboBox.setBackground(Color.WHITE);
        tipoSessioneComboBox.addActionListener(e -> switchDynamicPanel());
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
        cardLayout.show(dynamicPanelContainer, PRESENZA_CARD);

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

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 0, 5);
        panelInPresenza.add(createActionButton(), gbc);
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

        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 0, 5);
        panelOnline.add(createActionButton(), gbc);

        gbc.gridy = 3; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panelOnline.add(Box.createVerticalGlue(), gbc);
    }


    private void switchDynamicPanel() {
        String selectedType = (String) tipoSessioneComboBox.getSelectedItem();
        cardLayout.show(dynamicPanelContainer, selectedType);

        dynamicPanelContainer.revalidate();
        dynamicPanelContainer.repaint();
        formCard.revalidate();
        formCard.repaint();
    }

    // METODO PER CARICARE TUTTI I CORSI DELLO CHEF ALL'INTERNO DELLA COMBO-BOX
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
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore Caricamento Corsi", JOptionPane.ERROR_MESSAGE);
            corsoComboBox.setModel(new DefaultComboBoxModel<>());
        }
    }


    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JButton createActionButton() {
        JButton button = new JButton("Crea Sessione");
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertNewSession();
            }
        });
        return button;
    }


    //==========================================================
    // FUNZIONI AUSILIARIE
    //==========================================================

    private void insertNewSession() {
    	String tipoSessione = (String) tipoSessioneComboBox.getSelectedItem();
        CorsoDTO corsoSelezionato = (CorsoDTO) corsoComboBox.getSelectedItem();
        String argomento = argomentoField.getText();
        Date dataSessioneUtil = dataSessioneChooser.getDate();
        Integer ora = (Integer) oraComboBox.getSelectedItem();
        Integer minuti = (Integer) minutiComboBox.getSelectedItem();
        try {

            LocalTime oraInizio = LocalTime.of(ora, minuti);

            if (PRESENZA_CARD.equals(tipoSessione)) {
                String sede = sedeField.getText();
                String edificio = edificioField.getText();
                String aula = aulaField.getText();

                controller.inserimentoSessioneIP(argomento, oraInizio, dataSessioneUtil, corsoSelezionato.getId(), sede, edificio, aula);

            } else if (ONLINE_CARD.equals(tipoSessione)) {
                String link = linkConferenzaField.getText();

                controller.inserimentoSessioneOn(argomento, oraInizio, dataSessioneUtil, corsoSelezionato.getId(), link);
            }

            JOptionPane.showMessageDialog(this, "Sessione creata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

            HomeFrame home = (HomeFrame) SwingUtilities.getWindowAncestor(this);
            if (home != null) {
                home.showMainPanel(new GestioneSessioniPanel());
            }

        } catch(AlreadyExistsException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        } catch(OperationException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        	ex.printStackTrace();;
        }
    }
}