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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder; 
import java.util.Comparator;

import controller.Controller;
import dto.RicettaDTO;
import dto.SessioneInPresenzaDTO;
import exception.NotFoundException;
import exception.OperationException;

public class AggiungiRicettaDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Colori
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color LIGHT_GRAY_BACKGROUND = new Color(248, 249, 252);

    // elementi grafici
    private JList<RicettaDTO> availableList;
    private JList<RicettaDTO> associatedList;
    private DefaultListModel<RicettaDTO> availableListModel;
    private DefaultListModel<RicettaDTO> associatedListModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton addAllButton;
    private JButton removeAllButton;
    private JButton salvaButton;
    private JButton annullaButton;

    private Controller controller;
    private SessioneInPresenzaDTO sessione; 

    private boolean saved = false;

    public AggiungiRicettaDialog(JFrame owner, SessioneInPresenzaDTO sessione, Controller controller) {
        super(owner, "Associa Ricette alla Sessione", true);
        this.sessione = sessione;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        setBackground(LIGHT_GRAY_BACKGROUND);
        if (getContentPane() instanceof JPanel) {
            ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
            ((JPanel) getContentPane()).setOpaque(false);
        }

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildListsPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadInitialData();

        pack(); 
        setMinimumSize(new Dimension(600, 400)); 
        setLocationRelativeTo(owner);
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatted = (sessione.getDataSessione() != null) ? sessione.getDataSessione().format(dateFormatter) : "N/D";

        JLabel titleLabel = new JLabel("Sessione: " + sessione.getArgomento() + " del " + dataFormatted);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5)); 

        return headerPanel;
    }

    // Logica di creazione della Dual List Box
    private JPanel buildListsPanel() {
        JPanel listsPanel = new JPanel(new GridBagLayout());
        listsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        availableListModel = new DefaultListModel<>();
        availableList = createRicettaList(availableListModel, "Ricette Disponibili");

        associatedListModel = new DefaultListModel<>();
        associatedList = createRicettaList(associatedListModel, "Ricette Associate");

        JPanel buttonColumn = buildTransferButtonsPanel();

        // Lista Disponibili (sinistra)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        listsPanel.add(new JScrollPane(availableList), gbc);

        // Bottoni (centro)
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0; gbc.weighty = 0; 
        gbc.fill = GridBagConstraints.VERTICAL; 
        gbc.insets = new Insets(0, 5, 0, 5);
        listsPanel.add(buttonColumn, gbc);

        // Lista Associate (destra)
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 0);
        listsPanel.add(new JScrollPane(associatedList), gbc);

        return listsPanel;
    }

    private JList<RicettaDTO> createRicettaList(DefaultListModel<RicettaDTO> model, String title) {
        JList<RicettaDTO> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setFont(new Font("SansSerif", Font.PLAIN, 13));
        list.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_GRAY), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 12), TEXT_DARK),
            new EmptyBorder(5, 5, 5, 5)
        ));

        list.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RicettaDTO) {
                    setText(((RicettaDTO) value).getNomeRicetta());
                }
                return this;
            }
        });
        return list;
    }

    private JPanel buildTransferButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(50, 28); 

        addAllButton = new JButton(">>");
        addAllButton.setPreferredSize(buttonSize);
        addAllButton.setMaximumSize(buttonSize);
        addAllButton.setToolTipText("Aggiungi tutte");
        addAllButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		transferAll(availableListModel, associatedListModel);
        	}
        });

        addButton = new JButton(">");
        addButton.setPreferredSize(buttonSize);
        addButton.setMaximumSize(buttonSize);
        addButton.setToolTipText("Aggiungi selezionate");
        addButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		transferSelected(availableList, availableListModel, associatedListModel);
        	}
        });

        removeButton = new JButton("<");
        removeButton.setPreferredSize(buttonSize);
        removeButton.setMaximumSize(buttonSize);
        removeButton.setToolTipText("Rimuovi selezionate");
        removeButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		transferSelected(associatedList, associatedListModel, availableListModel);
        	}
        });
        	
        removeAllButton = new JButton("<<");
        removeAllButton.setPreferredSize(buttonSize);
        removeAllButton.setMaximumSize(buttonSize);
        removeAllButton.setToolTipText("Rimuovi tutte");
        removeAllButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		transferAll(associatedListModel, availableListModel);
        	}
        });

        panel.add(Box.createVerticalGlue()); 
        panel.add(addAllButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(addButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(removeButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(removeAllButton);
        panel.add(Box.createVerticalGlue()); 

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        salvaButton = new JButton("Salva Associazioni");
        salvaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        salvaButton.setBackground(PRIMARY_BLUE);
        salvaButton.setForeground(Color.WHITE);
        salvaButton.setOpaque(true);
        salvaButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        salvaButton.addActionListener(e -> executeSalva());

        annullaButton = new JButton("Annulla");
        annullaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        annullaButton.setBackground(Color.GRAY);
        annullaButton.setForeground(Color.WHITE);
        annullaButton.setOpaque(true);
        annullaButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        annullaButton.addActionListener(e -> dispose());

        buttonPanel.add(salvaButton);
        buttonPanel.add(annullaButton);
        return buttonPanel;
    }

    private void loadInitialData() {
        try {
            List<RicettaDTO> tutteRicette = controller.visualizzaTutteRicette();
            List<RicettaDTO> ricetteAssociate = controller.visualizzaRicetteSessione(sessione.getIdSessione());

            Set<Integer> idAssociate = ricetteAssociate.stream()
                                                      .map(RicettaDTO::getId)
                                                      .collect(Collectors.toSet());

            // Popola i modelli
            availableListModel.clear();
            associatedListModel.clear();

            for (RicettaDTO ricetta : tutteRicette) {
                if (idAssociate.contains(ricetta.getId())) {
                    associatedListModel.addElement(ricetta);
                } else {
                    availableListModel.addElement(ricetta);
                }
            }
        } catch (NotFoundException e) {
             // Se la sessione non ha ricette, la lista associate rimane vuota (ok)
             // Carica comunque le disponibili
             try {
                  List<RicettaDTO> tutteRicette = controller.visualizzaTutteRicette();
                  availableListModel.clear();
                  associatedListModel.clear();
                  for (RicettaDTO ricetta : tutteRicette) {
                       availableListModel.addElement(ricetta);
                  }
             } catch (OperationException ex) {
                   JOptionPane.showMessageDialog(this,ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
             }
        } catch (OperationException e) {
            
            availableListModel.clear();
            associatedListModel.clear();
        }
    }
    
    //=======================================================
    // FUNZIONI AUSILIARIE
    //======================================================

    // trasferisce nell' associatedListModel solo le ricette selezionate
    private void transferSelected(JList<RicettaDTO> sourceList, DefaultListModel<RicettaDTO> sourceModel, DefaultListModel<RicettaDTO> targetModel) {
        List<RicettaDTO> selected = sourceList.getSelectedValuesList();
        if (selected != null && !selected.isEmpty()) {
            for (RicettaDTO item : selected) {
                sourceModel.removeElement(item);
                targetModel.addElement(item);
            }
            sortListModel(targetModel);
        }
    }

    // trasferisce nell' associatedListModel tutte le ricette 
    private void transferAll(DefaultListModel<RicettaDTO> sourceModel, DefaultListModel<RicettaDTO> targetModel) {
        if (sourceModel.isEmpty()) return;
        List<RicettaDTO> itemsToMove = new ArrayList<>();
        for (int i = 0; i < sourceModel.getSize(); i++) {
            itemsToMove.add(sourceModel.getElementAt(i));
        }
        for (RicettaDTO item : itemsToMove) {
            targetModel.addElement(item);
        }
        sourceModel.clear();
        sortListModel(targetModel); // Riordina
    }


    private void sortListModel(DefaultListModel<RicettaDTO> model) {
        List<RicettaDTO> tempList = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            tempList.add(model.getElementAt(i));
        }
        tempList.sort(Comparator.comparing(RicettaDTO::getNomeRicetta, String.CASE_INSENSITIVE_ORDER));
        model.clear();
        for (RicettaDTO ricetta : tempList) {
            model.addElement(ricetta);
        }
    }
    
    private void executeSalva() {
        try {
            // Raccogli gli ID dalla lista delle associate
            List<Integer> idRicetteAssociate = new ArrayList<>();
            for (int i = 0; i < associatedListModel.getSize(); i++) {
                idRicetteAssociate.add(associatedListModel.getElementAt(i).getId());
            }

            // Chiama il controller per salvare
            controller.aggiornaRicettePerSessione(sessione.getIdSessione(), idRicetteAssociate);

            this.saved = true;
            dispose();

        } catch (OperationException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore Generico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return this.saved;
    }
}