package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import controller.Controller;
import dto.CorsoDTO;
import exception.OperationException;
import exception.NotFoundException;
import exception.UnauthorizedOperationException;

public class GestioneCorsiPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255); 
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color STRIPE_COLOR = new Color(245, 247, 252);
    private static final Color DELETE_RED = new Color(220, 53, 69); 
    
    private JLabel pageTitle;
    private JPanel titleContainer;
    private JPanel contentPanel;
    private JTable coursesTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;
    private JButton modificaButton;
    private JButton eliminaButton;
    
    private Controller controller;
    private List<CorsoDTO> elencoCorsiAttuale;
    
    private static final int HORIZONTAL_PADDING = 20; 
    private static final int TABLE_HEIGHT = 400; 

    public GestioneCorsiPanel() {
        controller = new Controller();
        elencoCorsiAttuale = new ArrayList<>();
        
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
        
        pageTitle = new JLabel("Gestisci i Tuoi Corsi");
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
        
        contentPanel.add(Box.createVerticalStrut(10));
        
        JPanel buttonPanel = buildActionButtons();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(buttonPanel);
        
        return contentPanel;
    }

    private JScrollPane buildTableScrollPane() {
        String[] columnNames = {"Nome", "Categoria", "Data Inizio", "Frequenza", "N. Sessioni"};
        
        tableModel = new DefaultTableModel(null, columnNames) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
         };
        
        coursesTable = new JTable(tableModel);
        coursesTable.setFillsViewportHeight(true);
        coursesTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        coursesTable.setRowHeight(30);
        coursesTable.setGridColor(BORDER_GRAY);
        coursesTable.setBackground(Color.WHITE);
        coursesTable.setShowVerticalLines(false);
        coursesTable.setIntercellSpacing(new Dimension(0, 0));
        
        coursesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        coursesTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        coursesTable.getTableHeader().setBackground(TEXT_DARK);
        coursesTable.getTableHeader().setForeground(Color.WHITE);
        coursesTable.getTableHeader().setReorderingAllowed(false);
        coursesTable.getTableHeader().setPreferredSize(new Dimension(coursesTable.getTableHeader().getWidth(), 35));
        
        coursesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean isRowSelected = coursesTable.getSelectedRow() != -1;
                modificaButton.setEnabled(isRowSelected);
                eliminaButton.setEnabled(isRowSelected);
            }
        });

        tableScrollPane = new JScrollPane(coursesTable);
        tableScrollPane.setPreferredSize(new Dimension(0, TABLE_HEIGHT)); 
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, TABLE_HEIGHT));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        
        return tableScrollPane;
    }
    
    private JPanel buildActionButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); 
        buttonPanel.setOpaque(false);

        modificaButton = new JButton("Modifica Corso");
        modificaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        modificaButton.setBackground(PRIMARY_BLUE); 
        modificaButton.setForeground(Color.WHITE);
        modificaButton.setOpaque(true);
        modificaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        modificaButton.setEnabled(false);
        modificaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeModifica();
        	}
        });
        
        eliminaButton = new JButton("Elimina Corso");
        eliminaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        eliminaButton.setBackground(DELETE_RED); 
        eliminaButton.setForeground(Color.WHITE);
        eliminaButton.setOpaque(true);
        eliminaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        eliminaButton.setEnabled(false);
        eliminaButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		executeElimina();
        	}
        });

        buttonPanel.add(modificaButton);
        buttonPanel.add(eliminaButton);
        
        return buttonPanel;
    }
    
    //=====================================================
    // FUNZIONI AUSILIARIE 
    //=====================================================
    
    private void loadInitialData() {
        try {
            elencoCorsiAttuale = controller.visualizzaCorsiPerChef();
            updateTable(elencoCorsiAttuale);
        } catch(NotFoundException ex) {
        	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        } catch (OperationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void executeModifica() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        CorsoDTO corsoSelezionato = elencoCorsiAttuale.get(selectedRow);
        
        JFrame home = (JFrame) SwingUtilities.getWindowAncestor(this);
        ModificaCorsoDialog dialog = new ModificaCorsoDialog(home, corsoSelezionato, controller);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadInitialData(); 
        }
    }
    
    public void executeElimina() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) return; 
        
        String nomeCorso = (String) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(
            this, 
            "Sei sicuro di voler eliminare il corso '" + nomeCorso + "'?\nQuesta azione è irreversibile.",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                controller.eliminaCorso(nomeCorso);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Corso eliminato con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch(NotFoundException ex) {
            	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch(UnauthorizedOperationException ex) {
            	JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (OperationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
            } 
        }
    }
    
    public void updateTable(List<CorsoDTO> elencoCorsi) {
        tableModel.setRowCount(0);
        
        for(CorsoDTO corso: elencoCorsi) {
            String[] rowData = {
                corso.getNomeCorso(),
                corso.getCategoria(),
                localDateToString(corso.getDataInizio()),
                corso.getFrequenzaSessioni(),
                String.valueOf(corso.getNumeroSessioni())
            };
            tableModel.addRow(rowData);
        }
    }
    
    public String localDateToString(LocalDate date) {
       if (date == null) return "Non Definita";
       
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       return date.format(formatter);
    }
}