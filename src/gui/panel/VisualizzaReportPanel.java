package gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controller.Controller;
import dto.StatisticheRicetteDTO; // Assumo tu abbia questo DTO dalla conversazione precedente
import exception.OperationException;
import session.SessionChef; // Assumo tu abbia questo per l'ID Chef

public class VisualizzaReportPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // --- Stile da VisualizzaTuttiChefPanel ---
    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color BG_COLOR = new Color(245, 247, 252); // Sfondo chiaro
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final int PADDING = 20;

    // --- Font ---
    private static final Font FONT_TITOLO_PAGINA = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_TITOLO_CARD = new Font("SansSerif", Font.BOLD, 19);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_VALUE = new Font("SansSerif", Font.BOLD, 16);

    // --- Controller ---
    private Controller controller;

    // --- Componenti UI ---
    private JComboBox<String> meseComboBox;
    private JComboBox<Integer> annoComboBox;
    private JButton cercaButton;

    // --- Componenti per i dati (sinistra) ---
    private JLabel corsiValueLabel;
    private JLabel onlineValueLabel;
    private JLabel praticaValueLabel;
    private AbstractBarChartPanel corsiChartPanel;

    // --- Componenti per i dati (destra) ---
    private JLabel maxValueLabel;
    private JLabel minValueLabel;
    private JLabel avgValueLabel;
    private AbstractBarChartPanel ricetteChartPanel;

    public VisualizzaReportPanel() {
        controller = new Controller();

        setLayout(new BorderLayout(0, 15));
        setOpaque(false); 
        setBackground(BG_COLOR); 
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        // Intestazione con titolo e filtri
        add(buildHeaderPanel(), BorderLayout.NORTH);
        
        // Oannello centrale a 2 colonne
        add(buildContentPanel(), BorderLayout.CENTER);

        // Inizializza i valori a 0
        aggiornaPannelliDati(0, 0, 0, new StatisticheRicetteDTO(0, 0, 0.0));
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

       
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);
        JLabel pageTitle = new JLabel("Report Mensile");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_DARK);
        titleContainer.add(pageTitle);

        
        JPanel filterContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterContainer.setOpaque(false);

        filterContainer.add(new JLabel("Mese:"));
        meseComboBox = new JComboBox<>(new String[]{
            "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
            "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
        });
        meseComboBox.setPreferredSize(new Dimension(150, 32));
        meseComboBox.setBackground(Color.WHITE);
        meseComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        filterContainer.add(meseComboBox);

        filterContainer.add(Box.createHorizontalStrut(15));

        filterContainer.add(new JLabel("Anno:"));
        annoComboBox = new JComboBox<>();
        int annoCorrente = LocalDate.now().getYear();
        for (int i = annoCorrente; i >= 2025; i--) {
            annoComboBox.addItem(i);
        }
        annoComboBox.setPreferredSize(new Dimension(150, 32));
        annoComboBox.setBackground(Color.WHITE);
        annoComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        filterContainer.add(annoComboBox);

        filterContainer.add(Box.createHorizontalStrut(15));

        cercaButton = new JButton("Cerca Statistiche");
        cercaButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cercaButton.setBackground(PRIMARY_BLUE);
        cercaButton.setForeground(Color.WHITE);
        cercaButton.setOpaque(true);
        cercaButton.setBorder(new EmptyBorder(6, 12, 6, 12));
        cercaButton.addActionListener(e -> onCercaPremuto()); // Collega l'evento
        filterContainer.add(cercaButton);

        headerPanel.add(titleContainer);
        headerPanel.add(filterContainer);
        return headerPanel;
    }

   
    private JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, PADDING, PADDING));
        contentPanel.setOpaque(false);

        // Colonna Sinistra
        JPanel leftColumn = new JPanel(new BorderLayout(0, PADDING));
        leftColumn.setOpaque(false);
        leftColumn.add(buildReportCorsiPanel(), BorderLayout.NORTH);
        leftColumn.add(buildCorsiBarChartPanel(), BorderLayout.CENTER);

        // Colonna Destra
        JPanel rightColumn = new JPanel(new BorderLayout(0, PADDING));
        rightColumn.setOpaque(false);
        rightColumn.add(buildReportStatistichePanel(), BorderLayout.NORTH);
        rightColumn.add(buildRicetteBarChartPanel(), BorderLayout.CENTER);

        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);

        return contentPanel;
    }

    // --- PANNELLI COLONNA SINISTRA ---

    private JPanel buildReportCorsiPanel() {
        JPanel card = createBaseCardPanel("Report Corsi e Sessioni");

        corsiValueLabel = new JLabel("0");
        onlineValueLabel = new JLabel("0");
        praticaValueLabel = new JLabel("0");

        card.add(createDataRow("Numero Totale Corsi:", corsiValueLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(createDataRow("Totale Sessioni Online:", onlineValueLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(createDataRow("Totale Sessioni Pratiche:", praticaValueLabel));

        return card;
    }

    private JPanel buildCorsiBarChartPanel() {
        JPanel card = createBaseCardPanel("Riepilogo Grafico Sessioni");
        card.setLayout(new BorderLayout());
        card.remove(0); // Rimuove il titolo (BoxLayout) per rimetterlo (BorderLayout)
        card.add(createCardTitle("Riepilogo Grafico Sessioni"), BorderLayout.NORTH);
        

        String[] labels = {"Corsi", "Online", "Pratica"};
        Color[] colors = {new Color(30, 144, 255), new Color(255, 165, 0), new Color(50, 205, 50)};
        corsiChartPanel = new AbstractBarChartPanel(labels, colors);
        
        corsiChartPanel.setOpaque(false);
        card.add(corsiChartPanel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 300)); // Dà altezza al grafico
        return card;
    }

    // --- PANNELLI COLONNA DESTRA ---

    private JPanel buildReportStatistichePanel() {
        JPanel card = createBaseCardPanel("Statistiche Ricette");

        maxValueLabel = new JLabel("0");
        minValueLabel = new JLabel("0");
        avgValueLabel = new JLabel("0.0");

        card.add(createDataRow("Numero Massimo Ricette:", maxValueLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(createDataRow("Numero Minimo Ricette:", minValueLabel));
        card.add(Box.createVerticalStrut(8));
        card.add(createDataRow("Media Ricette Realizzate:", avgValueLabel));

        return card;
    }

    private JPanel buildRicetteBarChartPanel() {
        JPanel card = createBaseCardPanel("Riepilogo Grafico Ricette");
        card.setLayout(new BorderLayout());
        card.remove(0);
        card.add(createCardTitle("Riepilogo Grafico Ricette"), BorderLayout.NORTH);

        String[] labels = {"Max", "Min", "Med"};
        Color[] colors = {new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 206, 86)};
        ricetteChartPanel = new AbstractBarChartPanel(labels, colors);
        
        ricetteChartPanel.setOpaque(false);
        card.add(ricetteChartPanel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 300));
        return card;
        
         
    }

    private void onCercaPremuto() {
        // 1. Raccogli input dall'UI
        int meseSelezionato = meseComboBox.getSelectedIndex() + 1; // +1 perché 0-based
        int annoSelezionato = (Integer) annoComboBox.getSelectedItem();

        try {
            int corsi = controller.getNumeroCorsiTenutiNelMese(meseSelezionato, annoSelezionato);
            int online = controller.getNumeroSessioniOnlineTenuteNelMese(meseSelezionato, annoSelezionato);
            int pratica = controller.getNumeroSessioniInPresenzaTenuteNelMese(meseSelezionato, annoSelezionato);
    
            
            int idChefLoggato = SessionChef.getChefId(); 
            StatisticheRicetteDTO stats = controller.getStatisticheRicettePerReport(idChefLoggato, meseSelezionato, annoSelezionato);
    
            
            aggiornaPannelliDati(corsi, online, pratica, stats);

        } catch (OperationException e) {
            JOptionPane.showMessageDialog(this, 
                "Errore nel caricamento del report: " + e.getMessage(), 
                "Errore", 
                JOptionPane.ERROR_MESSAGE);
            aggiornaPannelliDati(0, 0, 0, new StatisticheRicetteDTO(0, 0, 0.0));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Errore imprevisto: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            aggiornaPannelliDati(0, 0, 0, new StatisticheRicetteDTO(0, 0, 0.0));
        }
    }

    private void aggiornaPannelliDati(int corsi, int online, int pratica, StatisticheRicetteDTO stats) {
        // Aggiorna Pannello Sinistro (Testo)
        corsiValueLabel.setText(String.valueOf(corsi));
        onlineValueLabel.setText(String.valueOf(online));
        praticaValueLabel.setText(String.valueOf(pratica));

        // Aggiorna Pannello Sinistro (Grafico)
        corsiChartPanel.updateData(corsi, online, pratica);

        // Aggiorna Pannello Destro (Testo)
        maxValueLabel.setText(String.valueOf(stats.getMax()));
        minValueLabel.setText(String.valueOf(stats.getMin()));
        avgValueLabel.setText(String.format("%.2f", stats.getAvg()));

        // Aggiorna Pannello Destro (Grafico)
        ricetteChartPanel.updateData(stats.getMax(), stats.getMin(), stats.getAvg());

        // Forza il ridisegno di tutto
        this.revalidate();
        this.repaint();
    }

    
    private JPanel createBaseCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(PADDING - 5, PADDING, PADDING, PADDING))); // 15, 20, 20, 20
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createCardTitle(title));
        panel.add(Box.createVerticalStrut(16));
        return panel;
    }

    private JLabel createCardTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITOLO_CARD);
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0)); // Spazio sotto
        return titleLabel;
    }

    private JPanel createDataRow(String labelText, JLabel valueLabel) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        rowPanel.add(label, BorderLayout.WEST);

        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(TEXT_DARK);
        rowPanel.add(valueLabel, BorderLayout.EAST);

        return rowPanel;
    }

    //GRAFICO A BARRE
    class AbstractBarChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String[] labels;
        private final Color[] colors;
        private double[] values; 

        private final int BAR_WIDTH = 50;
        private final int BAR_GAP = 40;

        public AbstractBarChartPanel(String[] labels, Color[] colors) {
            this.labels = labels;
            this.colors = colors;
            this.values = new double[labels.length]; 
            
            int prefWidth = (BAR_WIDTH * labels.length) + (BAR_GAP * (labels.length + 1));
            setPreferredSize(new Dimension(prefWidth, 250));
        }

        
        public void updateData(double... newValues) {
            if (newValues.length == values.length) {
                this.values = newValues;
                repaint(); // Ridisegna il componente con i nuovi dati
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double maxValue = 0;
            for (double value : values) {
                if (value > maxValue) maxValue = value;
            }

            if (maxValue == 0) maxValue = 1; // Evita divisione per zero

            int panelHeight = getHeight() - 40; // Spazio per etichette
            int panelWidth = getWidth();

            int totalBarSpace = (BAR_WIDTH * values.length) + (BAR_GAP * (values.length - 1));
            int startX = (panelWidth - totalBarSpace) / 2;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / maxValue) * panelHeight);
                int y = panelHeight - barHeight + 20; // +20 per padding superiore
                int x = startX + i * (BAR_WIDTH + BAR_GAP);

                // Disegna la barra
                g2.setColor(colors[i]);
                g2.fillRect(x, y, BAR_WIDTH, barHeight);

                // Disegna etichetta (valore) sopra la barra
                g2.setColor(TEXT_DARK);
                g2.setFont(FONT_VALUE);
                
                // Formatta il valore (int o double)
                String valueStr;
                if (values[i] % 1 == 0) { // Se è un intero
                    valueStr = String.valueOf((int)values[i]);
                } else {
                    valueStr = String.format("%.2f", values[i]);
                }
                
                int strWidth = g2.getFontMetrics().stringWidth(valueStr);
                g2.drawString(valueStr, x + (BAR_WIDTH - strWidth) / 2, y - 5);

                // Disegna etichetta (nome) sotto la barra
                g2.setFont(FONT_LABEL);
                strWidth = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (BAR_WIDTH - strWidth) / 2, panelHeight + 35);
            }
        }
    }
}