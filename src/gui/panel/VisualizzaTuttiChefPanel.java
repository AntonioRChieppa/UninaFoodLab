package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box; 
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;

import controller.ChefController;
import dto.ChefDTO;
import exception.OperationException;

public class VisualizzaTuttiChefPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color STRIPE_COLOR = new Color(245, 247, 252);

    private JLabel pageTitle;
    private JPanel titleContainer;
    private JPanel contentPanel;
    private JTable chefTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;

    private ChefController controller;

    private static final int HORIZONTAL_PADDING = 20;
    private static final int TABLE_HEIGHT = 450;

    public VisualizzaTuttiChefPanel() {
        controller = new ChefController();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(20, HORIZONTAL_PADDING, HORIZONTAL_PADDING, HORIZONTAL_PADDING));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        loadInitialData();
    }

    private JPanel buildTitlePanel() {
        titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);

        pageTitle = new JLabel("Visualizza Tutti gli Chef");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_DARK);

        titleContainer.add(pageTitle);

        return titleContainer;
    }

    private JPanel buildContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        tableScrollPane = buildTableScrollPane();
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(tableScrollPane);
        contentPanel.add(Box.createVerticalStrut(HORIZONTAL_PADDING * 2)); 

        return contentPanel;
    }

    private JScrollPane buildTableScrollPane() {
        String[] columnNames = {"Nome", "Cognome", "Email"};

        tableModel = new DefaultTableModel(null, columnNames) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
         };

        chefTable = new JTable(tableModel);
        chefTable.setFillsViewportHeight(true);
        chefTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chefTable.setRowHeight(30);
        chefTable.setGridColor(BORDER_GRAY);
        chefTable.setBackground(Color.WHITE);
        chefTable.setShowVerticalLines(false);
        chefTable.setIntercellSpacing(new Dimension(0, 0));

        chefTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        chefTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        chefTable.getTableHeader().setBackground(TEXT_DARK);
        chefTable.getTableHeader().setForeground(Color.WHITE);
        chefTable.getTableHeader().setReorderingAllowed(false);
        chefTable.getTableHeader().setPreferredSize(new Dimension(chefTable.getTableHeader().getWidth(), 35));

        tableScrollPane = new JScrollPane(chefTable);

        tableScrollPane.setPreferredSize(new Dimension(0, TABLE_HEIGHT));
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_HEIGHT));

        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));

        return tableScrollPane;
    }
    
    //================================================================
    // FUNZIONI AUSILIARIE 
    //================================================================

    private void loadInitialData() {
        try {
            List<ChefDTO> elencoChef = controller.visualizzaTuttiChef();
            updateTable(elencoChef);
        } catch (OperationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<ChefDTO> elencoChef) {
        tableModel.setRowCount(0);

        if (elencoChef == null) {
            elencoChef = new ArrayList<>();
        }

        for(ChefDTO chef: elencoChef) {
            String[] rowData = {
                chef.getNome(),
                chef.getCognome(),
                chef.getEmail()
            };
            tableModel.addRow(rowData);
        }
    }
}