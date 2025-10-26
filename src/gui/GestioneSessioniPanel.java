package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import controller.Controller;
import dto.SessioneDTO;
import dto.SessioneInPresenzaDTO;
import dto.SessioneOnlineDTO;
import dto.CorsoDTO;
import exception.OperationException;
import exception.NotFoundException;
import exception.UnauthorizedOperationException;
import session.SessionChef;

public class GestioneSessioniPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color STRIPE_COLOR = new Color(245, 247, 252);
    private static final Color DELETE_RED = new Color(220, 53, 69);
    private static final Color ADD_GREEN = new Color(40, 167, 69);
    private static final Color LIGHT_GRAY_BACKGROUND = new Color(240, 240, 240); // Nuovo colore di sfondo per il dialog

    private JLabel pageTitle;
    private JPanel titleContainer;
    private JPanel contentPanel;
    private JPanel infoCardPanel;
    private JTable sessioniTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;
    private JButton aggiungiRicettaButton;
    private JButton modificaButton;
    private JButton eliminaButton;

    private Controller controller;
    private List<SessioneDTO> elencoSessioniAttuale;

    private static final int HORIZONTAL_PADDING = 20;
    private static final int TABLE_HEIGHT = 420;

    public GestioneSessioniPanel() {
        controller = new Controller();
        elencoSessioniAttuale = new ArrayList<>();

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(10, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        loadInitialData();
    }

    private void loadInitialData() {
         try {
             elencoSessioniAttuale = controller.visualizzaTutteSessioniPerChef();
             updateTable(elencoSessioniAttuale);
         } catch(NotFoundException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Informazione", JOptionPane.INFORMATION_MESSAGE);
             elencoSessioniAttuale.clear();
             updateTable(elencoSessioniAttuale);
         } catch (OperationException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
             elencoSessioniAttuale.clear();
             updateTable(elencoSessioniAttuale);
         }
    }

    private JPanel buildTitlePanel() {
        titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        pageTitle = new JLabel("Gestisci le Tue Sessioni");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setForeground(TEXT_DARK);

        titleContainer.add(pageTitle);
        return titleContainer;
    }

    private JPanel buildContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        infoCardPanel = buildInfoCardPanel();
        infoCardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, infoCardPanel.getPreferredSize().height));

        tableScrollPane = buildTableScrollPane();
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = buildActionButtons();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height));

        contentPanel.add(infoCardPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(tableScrollPane);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(HORIZONTAL_PADDING));

        return contentPanel;
    }

     private JPanel buildInfoCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(new Color(230, 242, 255));
        card.setLayout(new FlowLayout(FlowLayout.LEFT));
        Border lineBorder = BorderFactory.createLineBorder(PRIMARY_BLUE.darker());
        Border paddingBorder = new EmptyBorder(8, 12, 8, 12);
        card.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        JLabel infoLabel = new JLabel("ℹ️ Fai doppio click su una riga per visualizzare i dettagli della sessione.");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_DARK);
        card.add(infoLabel);

        return card;
    }


    private JScrollPane buildTableScrollPane() {
        String[] columnNames = {"Argomento", "Ora Inizio", "Data Sessione", "Corso", "Tipo Sessione"};

        tableModel = new DefaultTableModel(null, columnNames) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
         };

        sessioniTable = new JTable(tableModel);
        sessioniTable.setFillsViewportHeight(true);
        sessioniTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sessioniTable.setRowHeight(30);
        sessioniTable.setGridColor(BORDER_GRAY);
        sessioniTable.setBackground(Color.WHITE);
        sessioniTable.setShowVerticalLines(false);
        sessioniTable.setIntercellSpacing(new Dimension(0, 0));
        sessioniTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sessioniTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : STRIPE_COLOR);
                }
                label.setBorder(new EmptyBorder(7, 8, 7, 8));
                return label;
            }
        });

        sessioniTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        sessioniTable.getTableHeader().setBackground(TEXT_DARK);
        sessioniTable.getTableHeader().setForeground(Color.WHITE);
        sessioniTable.getTableHeader().setReorderingAllowed(false);
        sessioniTable.getTableHeader().setPreferredSize(new Dimension(sessioniTable.getTableHeader().getWidth(), 35));

        sessioniTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                 if (!e.getValueIsAdjusting()) {
                    boolean isRowSelected = sessioniTable.getSelectedRow() != -1;
                    aggiungiRicettaButton.setEnabled(isRowSelected);
                    modificaButton.setEnabled(isRowSelected);
                    eliminaButton.setEnabled(isRowSelected);
                }
            }
        });

        sessioniTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSessionDetails();
                }
            }
        });

        tableScrollPane = new JScrollPane(sessioniTable);
        tableScrollPane.setPreferredSize(new Dimension(0, TABLE_HEIGHT));
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_HEIGHT));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        return tableScrollPane;
    }

    private JPanel buildActionButtons() {
        JPanel mainButtonPanel = new JPanel(new BorderLayout(10, 0));
        mainButtonPanel.setOpaque(false);

        aggiungiRicettaButton = new JButton("Aggiungi Ricetta");
        aggiungiRicettaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        aggiungiRicettaButton.setBackground(ADD_GREEN);
        aggiungiRicettaButton.setForeground(Color.WHITE);
        aggiungiRicettaButton.setOpaque(true);
        aggiungiRicettaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        aggiungiRicettaButton.setEnabled(false);
        aggiungiRicettaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeAggiungiRicetta();
        	}
        });
        
        JPanel addRecipeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addRecipeWrapper.setOpaque(false);
        addRecipeWrapper.add(aggiungiRicettaButton);
        mainButtonPanel.add(addRecipeWrapper, BorderLayout.WEST);


        JPanel modifyDeletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        modifyDeletePanel.setOpaque(false);

        modificaButton = new JButton("Modifica Sessione");
        modificaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        modificaButton.setBackground(PRIMARY_BLUE);
        modificaButton.setForeground(Color.WHITE);
        modificaButton.setOpaque(true);
        modificaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        modificaButton.setEnabled(false);
        modificaButton.addActionListener(new ActionListener( ) {
        	public void actionPerformed(ActionEvent e) {
        		executeModifica();
        	}
        });

        eliminaButton = new JButton("Elimina Sessione");
        eliminaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        eliminaButton.setBackground(DELETE_RED);
        eliminaButton.setForeground(Color.WHITE);
        eliminaButton.setOpaque(true);
        eliminaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        eliminaButton.setEnabled(false);
        eliminaButton.addActionListener(new ActionListener( ) {
        	public void actionPerformed(ActionEvent e) {
        		executeElimina();
        	}
        });

        modifyDeletePanel.add(modificaButton);
        modifyDeletePanel.add(eliminaButton);
        mainButtonPanel.add(modifyDeletePanel, BorderLayout.EAST);

        return mainButtonPanel;
    }


    private void updateTable(List<SessioneDTO> elencoSessioni) {
        tableModel.setRowCount(0);

        if (elencoSessioni == null) {
             elencoSessioni = new ArrayList<>();
        }

        elencoSessioni.sort(Comparator.comparing(SessioneDTO::getDataSessione, Comparator.nullsLast(Comparator.naturalOrder()))
                                      .thenComparing(SessioneDTO::getOraInizio, Comparator.nullsLast(Comparator.naturalOrder())));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for(SessioneDTO sessione: elencoSessioni) {
            String nomeCorso = (sessione.getCorsoSessione() != null) ? sessione.getCorsoSessione().getNomeCorso() : "N/D";
            String oraFormattata = (sessione.getOraInizio() != null) ? sessione.getOraInizio().format(timeFormatter) : "N/D";
            String dataFormattata = (sessione.getDataSessione() != null) ? sessione.getDataSessione().format(dateFormatter) : "N/D";
            String tipo = sessione.getTipoSessione();

             if (tipo != null && tipo.length() > 0) {
                 if ("presenza".equalsIgnoreCase(tipo)) {
                    tipo = "In Presenza";
                 } else if ("online".equalsIgnoreCase(tipo)){
                    tipo = "Online";
                 } else {
                    tipo = tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase();
                 }
            } else {
                tipo = "N/D";
            }


            String[] rowData = {
                sessione.getArgomento(),
                oraFormattata,
                dataFormattata,
                nomeCorso,
                tipo
            };
            tableModel.addRow(rowData);
        }
        sessioniTable.clearSelection();
    }

    private void showSessionDetails() {
         int selectedRow = sessioniTable.getSelectedRow();
        if (selectedRow == -1) return;

        SessioneDTO sessioneSelezionata = elencoSessioniAttuale.get(selectedRow);

        JDialog detailsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Dettagli Sessione", true);
        detailsDialog.setLayout(new BorderLayout(10, 10));
        detailsDialog.setSize(450, 400); // Aumentato un po' la dimensione per più spazio
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setResizable(false);
        detailsDialog.setBackground(LIGHT_GRAY_BACKGROUND); // Sfondo leggermente grigio

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        detailsPanel.setBackground(Color.WHITE); // Sfondo bianco per il pannello dei dettagli interni

        // Ogni componente aggiunto al detailsPanel deve essere allineato a sinistra
        detailsPanel.add(createDetailLabel("Argomento:", sessioneSelezionata.getArgomento()));
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(createDetailLabel("Data:", localDateToString(sessioneSelezionata.getDataSessione())));
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(createDetailLabel("Ora:", localTimeToString(sessioneSelezionata.getOraInizio())));
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(createDetailLabel("Corso:", (sessioneSelezionata.getCorsoSessione() != null) ? sessioneSelezionata.getCorsoSessione().getNomeCorso() : "N/D"));
        detailsPanel.add(Box.createVerticalStrut(12));


        if ("presenza".equalsIgnoreCase(sessioneSelezionata.getTipoSessione())) {
             detailsPanel.add(createDetailLabel("Tipo:", "In Presenza"));
             detailsPanel.add(Box.createVerticalStrut(8));
             try {
                SessioneInPresenzaDTO sessioneIP = controller.getSessioneInPresenzaById(sessioneSelezionata.getIdSessione());
                if (sessioneIP != null) {
                    detailsPanel.add(createDetailLabel("Sede:", sessioneIP.getSede()));
                    detailsPanel.add(Box.createVerticalStrut(8));
                    detailsPanel.add(createDetailLabel("Edificio:", sessioneIP.getEdificio()));
                    detailsPanel.add(Box.createVerticalStrut(8));
                    detailsPanel.add(createDetailLabel("Aula:", sessioneIP.getAula()));
                } else {
                     detailsPanel.add(createErrorLabel("Dettagli presenza non trovati."));
                }
             } catch (OperationException e) {
                 detailsPanel.add(createErrorLabel("Errore nel recupero dettagli: " + e.getMessage()));
             }
        } else if ("online".equalsIgnoreCase(sessioneSelezionata.getTipoSessione())) {
            detailsPanel.add(createDetailLabel("Tipo:", "Online"));
            detailsPanel.add(Box.createVerticalStrut(8));
             try {
                SessioneOnlineDTO sessioneOn = controller.getSessioneOnlineById(sessioneSelezionata.getIdSessione());
                if (sessioneOn != null) {
                     // createDetailLabel per il link, e poi una JTextArea per il valore
                     detailsPanel.add(createDetailLabel("Link:", null)); // Label "Link:" senza valore
                     JTextArea linkArea = new JTextArea(sessioneOn.getLinkConferenza());
                     linkArea.setEditable(false);
                     linkArea.setLineWrap(true);
                     linkArea.setWrapStyleWord(true);
                     linkArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
                     linkArea.setBackground(detailsPanel.getBackground());
                     linkArea.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0)); // Padding minimo
                     linkArea.setForeground(TEXT_DARK);
                     linkArea.setAlignmentX(Component.LEFT_ALIGNMENT); // Allinea a sinistra
                     linkArea.setMaximumSize(new Dimension(detailsDialog.getWidth() - 50, linkArea.getPreferredSize().height)); // Limita larghezza
                     detailsPanel.add(linkArea);

                } else {
                    detailsPanel.add(createErrorLabel("Dettagli online non trovati."));
                }
             } catch (OperationException e) {
                  detailsPanel.add(createErrorLabel("Errore nel recupero dettagli: " + e.getMessage()));
             }
        } else {
             detailsPanel.add(createDetailLabel("Tipo:", "Non specificato"));
        }

        // Pannello per il bottone Chiudi
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LIGHT_GRAY_BACKGROUND); // Sfondo leggermente grigio
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Spazio inferiore

        JButton closeButton = new JButton("Chiudi");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeButton.setBackground(PRIMARY_BLUE); // Usa colore primario
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false); // Rimuove il bordo focus
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1), // Bordo leggermente più scuro
            new EmptyBorder(8, 20, 8, 20) // Padding interno
        ));
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);

        detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER); // Aggiunto JScrollPane per i dettagli
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.setVisible(true);
    }

    private JLabel createDetailLabel(String labelText, String valueText) {
        JLabel label = new JLabel();
        String text = "<html><b>" + labelText + "</b> ";
        if (valueText != null) {
            text += valueText;
        }
        text += "</html>";
        label.setText(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Allinea a sinistra
        return label;
    }
    
    // Nuovo metodo per creare etichette di errore rosse
    private JLabel createErrorLabel(String text) {
        JLabel label = new JLabel("<html><font color='red'>" + text + "</font></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT); // Allinea a sinistra
        return label;
    }
    
    //========================================================
    // FUNZIONI AUSILIARIE
    //========================================================

    
    private void executeAggiungiRicetta() {
        int selectedRow = sessioniTable.getSelectedRow();
        if (selectedRow == -1) return;
        SessioneDTO sessioneSelezionata = elencoSessioniAttuale.get(selectedRow);

        JOptionPane.showMessageDialog(this, "Apertura pannello aggiunta ricette per sessione: "
            + sessioneSelezionata.getArgomento() + "\n(Logica da implementare)");
    }

    private void executeModifica() {
        int selectedRow = sessioniTable.getSelectedRow();
        if (selectedRow == -1) return;

        SessioneDTO sessioneSelezionata = elencoSessioniAttuale.get(selectedRow);

        JFrame owner = (JFrame) SwingUtilities.getWindowAncestor(this);
        ModificaSessioneDialog dialog = new ModificaSessioneDialog(owner, sessioneSelezionata, controller);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            loadInitialData(); 
        }

    }

    private void executeElimina() {
        int selectedRow = sessioniTable.getSelectedRow();
        if (selectedRow == -1) return;

        SessioneDTO sessioneSelezionata = elencoSessioniAttuale.get(selectedRow);
        String nomeSessione = sessioneSelezionata.getArgomento() + " del " + localDateToString(sessioneSelezionata.getDataSessione());

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler eliminare la sessione '" + nomeSessione + "'?\nQuesta azione è irreversibile.",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                if ("presenza".equalsIgnoreCase(sessioneSelezionata.getTipoSessione())) {
                     controller.eliminaSessioneIp(sessioneSelezionata.getIdSessione());
                } else if ("online".equalsIgnoreCase(sessioneSelezionata.getTipoSessione())) {
                     controller.eliminaSessioneOn(sessioneSelezionata.getIdSessione());
                } else {
                    throw new OperationException("Tipo sessione non valido per l'eliminazione.");
                }

                elencoSessioniAttuale.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Sessione eliminata con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);

            } catch(NotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch(UnauthorizedOperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (OperationException ex) {
                JOptionPane.showMessageDialog(this,ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String localDateToString(LocalDate date) {
       if (date == null) return "N/D";
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       return date.format(formatter);
    }

    public String localTimeToString(LocalTime time) {
        if (time == null) return "N/D";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }
}