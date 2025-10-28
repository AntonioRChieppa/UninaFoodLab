package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import controller.Controller;
import dto.RicettaDTO;
import dto.IngredienteDTO;
import exception.OperationException;
import exception.NotFoundException;
import session.SessionChef;

public class VisualizzaRicetteChefPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color STRIPE_COLOR = new Color(245, 247, 252);
    
    private static final Color DIFFICOLTA_FACILE = new Color(40, 167, 69);
    private static final Color DIFFICOLTA_MEDIA = new Color(255, 193, 7);
    private static final Color DIFFICOLTA_DIFFICILE = new Color(220, 53, 69);

    private static final Color LIGHT_GRAY_BACKGROUND = new Color(240, 240, 240);


    private JLabel pageTitle;
    private JPanel titleContainer;
    private JPanel contentPanel;
    private JPanel infoCardPanel;
    private JTable ricetteTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;

    private Controller controller;
    private List<RicettaDTO> elencoRicetteAttuale;

    private static final int HORIZONTAL_PADDING = 20;
    private static final int TABLE_HEIGHT = 450;

    public VisualizzaRicetteChefPanel() {
        controller = new Controller();
        elencoRicetteAttuale = new ArrayList<>();

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(10, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        loadInitialData();
    }

    private void loadInitialData() {
         try {
             elencoRicetteAttuale = controller.visualizzaTutteRicettePerChef();
             updateTable(elencoRicetteAttuale);
         } catch (OperationException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
             elencoRicetteAttuale.clear();
             updateTable(elencoRicetteAttuale);
         }
    }

    private JPanel buildTitlePanel() {
        titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        pageTitle = new JLabel("Visualizza le Tue Ricette");
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

        contentPanel.add(infoCardPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(tableScrollPane);
        contentPanel.add(Box.createVerticalStrut(HORIZONTAL_PADDING * 2));

        return contentPanel;
    }

     private JPanel buildInfoCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(new Color(230, 242, 255));
        card.setLayout(new FlowLayout(FlowLayout.LEFT));
        Border lineBorder = BorderFactory.createLineBorder(PRIMARY_BLUE.darker());
        Border paddingBorder = new EmptyBorder(8, 12, 8, 12);
        card.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        JLabel infoLabel = new JLabel("ℹ️ Fai doppio click su una ricetta per visualizzare la lista degli ingredienti.");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_DARK);
        card.add(infoLabel);

        return card;
    }


    private JScrollPane buildTableScrollPane() {
        String[] columnNames = {"Nome Ricetta", "Tempo Preparazione", "Porzioni", "Difficoltà"};

        tableModel = new DefaultTableModel(null, columnNames) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
         };

        ricetteTable = new JTable(tableModel);
        ricetteTable.setFillsViewportHeight(true);
        ricetteTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ricetteTable.setRowHeight(30);
        ricetteTable.setGridColor(BORDER_GRAY);
        ricetteTable.setBackground(Color.WHITE);
        ricetteTable.setShowVerticalLines(false);
        ricetteTable.setIntercellSpacing(new Dimension(0, 0));
        ricetteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ricetteTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        ricetteTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    String difficolta = (String) value;
                    
                    label.setText(difficolta);
                    label.setOpaque(true);
                    label.setForeground(Color.WHITE);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    
                    Border padding = new EmptyBorder(4, 6, 4, 6);
                    label.setBorder(BorderFactory.createCompoundBorder(label.getBorder(), padding));

                    if (difficolta != null) {
                        if (difficolta.equalsIgnoreCase("Facile")) {
                            label.setBackground(DIFFICOLTA_FACILE);
                        } else if (difficolta.equalsIgnoreCase("Medio")) {
                            label.setBackground(DIFFICOLTA_MEDIA);
                             label.setForeground(Color.BLACK);
                        } else if (difficolta.equalsIgnoreCase("Difficile")) {
                            label.setBackground(DIFFICOLTA_DIFFICILE);
                        } else {
                            label.setBackground(Color.GRAY);
                        }
                    }

                    if (isSelected) {
                         label.setBackground(table.getSelectionBackground());
                         label.setForeground(table.getSelectionForeground());
                    }
                }

                return c;
            }
        });


        ricetteTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        ricetteTable.getTableHeader().setBackground(TEXT_DARK);
        ricetteTable.getTableHeader().setForeground(Color.WHITE);
        ricetteTable.getTableHeader().setReorderingAllowed(false);
        ricetteTable.getTableHeader().setPreferredSize(new Dimension(ricetteTable.getTableHeader().getWidth(), 35));

        ricetteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showIngredientiDetails();
                }
            }
        });

        tableScrollPane = new JScrollPane(ricetteTable);
        tableScrollPane.setPreferredSize(new Dimension(0, TABLE_HEIGHT));
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_HEIGHT));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        return tableScrollPane;
    }

    private void updateTable(List<RicettaDTO> elencoRicette) {
        this.elencoRicetteAttuale = elencoRicette;
        tableModel.setRowCount(0);

        if (elencoRicette == null) {
             elencoRicette = new ArrayList<>();
        }

        elencoRicette.sort(Comparator.comparing(RicettaDTO::getNomeRicetta, String.CASE_INSENSITIVE_ORDER));

        for(RicettaDTO ricetta: elencoRicette) {
            String[] rowData = {
                ricetta.getNomeRicetta(),
                ricetta.getTempoPreparazione() + " min",
                String.valueOf(ricetta.getPorzioni()),
                ricetta.getDifficolta()
            };
            tableModel.addRow(rowData);
        }
    }

    private void showIngredientiDetails() {
         int selectedRow = ricetteTable.getSelectedRow();
        if (selectedRow == -1) return;

        RicettaDTO ricettaSelezionata = elencoRicetteAttuale.get(selectedRow);

        JDialog detailsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Ingredienti per: " + ricettaSelezionata.getNomeRicetta(), true);
        detailsDialog.setLayout(new BorderLayout(10, 10));
        detailsDialog.setSize(400, 350);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setResizable(false);
        detailsDialog.setBackground(LIGHT_GRAY_BACKGROUND);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        detailsPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Lista Ingredienti:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        try {
            List<IngredienteDTO> ingredienti = controller.visualizzaIngredientiPerRicetta(ricettaSelezionata.getId());

            if (ingredienti != null && !ingredienti.isEmpty()) {
                for (IngredienteDTO ingrediente : ingredienti) {
                    JLabel ingLabel = new JLabel("• " + ingrediente.getNomeIngrediente());
                    ingLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    ingLabel.setForeground(TEXT_DARK);
                    ingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    detailsPanel.add(ingLabel);
                    detailsPanel.add(Box.createVerticalStrut(5));
                }
            } else {
                JLabel noIngLabel = new JLabel("Nessun ingrediente associato a questa ricetta.");
                noIngLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                noIngLabel.setForeground(TEXT_DARK);
                noIngLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                detailsPanel.add(noIngLabel);
            }
        } catch(NotFoundException ex) {
        	detailsPanel.add(createErrorLabel(ex.getMessage()));
        } catch (OperationException ex) {
             detailsPanel.add(createErrorLabel("Errore nel recupero ingredienti: " + ex.getMessage()));
        } 

        detailsPanel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(LIGHT_GRAY_BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton closeButton = new JButton("Chiudi");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeButton.setBackground(PRIMARY_BLUE);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE.darker(), 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);

        detailsDialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.setVisible(true);
    }
    
    private JLabel createErrorLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
}