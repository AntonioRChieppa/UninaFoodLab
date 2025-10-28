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
import dto.StatisticheRicetteDTO;
import exception.OperationException;
import session.SessionChef;

public class VisualizzaReportPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color TEXT_DARK = new Color(51, 63, 81);
    private static final Color BORDER_GRAY = new Color(220, 225, 235);
    private static final Color BG_COLOR = new Color(245, 247, 252);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_BLUE = new Color(30, 144, 255);
    private static final int PADDING = 20;

    private static final Font FONT_TITOLO_PAGINA = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_TITOLO_CARD = new Font("SansSerif", Font.BOLD, 19);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_VALUE = new Font("SansSerif", Font.BOLD, 16);

    private Controller controller;

    private JComboBox<String> meseComboBox;
    private JComboBox<Integer> annoComboBox;
    private JButton cercaButton;

    private JLabel corsiValueLabel;
    private JLabel onlineValueLabel;
    private JLabel praticaValueLabel;
    private AbstractBarChartPanel corsiChartPanel;

    private JLabel maxValueLabel;
    private JLabel minValueLabel;
    private JLabel avgValueLabel;
    private AbstractBarChartPanel ricetteChartPanel;

    public VisualizzaReportPanel() {
        controller = new Controller();

        setLayout(new BorderLayout(0, 15));
        setOpaque(true);
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        add(buildHeaderPanel(), BorderLayout.NORTH);
        
        add(buildContentPanel(), BorderLayout.CENTER);

        aggiornaPannelliDati(0, 0, 0, new StatisticheRicetteDTO(0, 0, 0.0));
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        
        JPanel titleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleContainer.setOpaque(false);
        JLabel pageTitle = new JLabel("Report Mensile");
        pageTitle.setFont(FONT_TITOLO_PAGINA);
        pageTitle.setForeground(TEXT_DARK);
        titleContainer.add(pageTitle);

        
        JPanel filterContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterContainer.setOpaque(false);
        filterContainer.setBorder(new EmptyBorder(5, 0, 0, 0));

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
        cercaButton.addActionListener(e -> onCercaPremuto());
        filterContainer.add(cercaButton);

        headerPanel.add(titleContainer);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(filterContainer);
        return headerPanel;
    }

    
    private JPanel buildContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, PADDING, PADDING));
        contentPanel.setOpaque(false);

        JPanel leftColumn = new JPanel(new BorderLayout(0, PADDING));
        leftColumn.setOpaque(false);
        leftColumn.add(buildReportCorsiPanel(), BorderLayout.NORTH);
        leftColumn.add(buildCorsiBarChartPanel(), BorderLayout.CENTER);

        JPanel rightColumn = new JPanel(new BorderLayout(0, PADDING));
        rightColumn.setOpaque(false);
        rightColumn.add(buildReportStatistichePanel(), BorderLayout.NORTH);
        rightColumn.add(buildRicetteBarChartPanel(), BorderLayout.CENTER);

        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);
        
        JPanel bottomMarginPanel = new JPanel();
        bottomMarginPanel.setOpaque(false);
        bottomMarginPanel.setPreferredSize(new Dimension(0, PADDING));
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);
        wrapperPanel.add(bottomMarginPanel, BorderLayout.SOUTH);

        return wrapperPanel;
    }

    private JPanel buildReportCorsiPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(PADDING - 5, PADDING, PADDING, PADDING)));
        // Usa BorderLayout
        card.setLayout(new BorderLayout(0, 16));

        corsiValueLabel = new JLabel("0");
        onlineValueLabel = new JLabel("0");
        praticaValueLabel = new JLabel("0");
        
        // Aggiunge il titolo in NORTH (allineato a sinistra di default da BorderLayout)
        card.add(createCardTitle("Report Corsi e Sessioni"), BorderLayout.NORTH);

        // Pannello per i dati, usa BoxLayout per le righe
        JPanel dataPanel = new JPanel();
        dataPanel.setOpaque(false);
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.add(createDataRow("Numero Totale Corsi:", corsiValueLabel));
        dataPanel.add(Box.createVerticalStrut(8));
        dataPanel.add(createDataRow("Totale Sessioni Online:", onlineValueLabel));
        dataPanel.add(Box.createVerticalStrut(8));
        dataPanel.add(createDataRow("Totale Sessioni Pratiche:", praticaValueLabel));
        
        card.add(dataPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildCorsiBarChartPanel() {
        // Questa card usa già BorderLayout e createCardTitle in NORTH
        JPanel card = createBaseCardPanel(); // Usa il metodo vecchio
        card.setLayout(new BorderLayout());
        card.add(createCardTitle("Riepilogo Grafico Sessioni"), BorderLayout.NORTH);
        

        String[] labels = {"Corsi", "Online", "Pratica"};
        Color[] colors = {new Color(30, 144, 255), new Color(255, 165, 0), new Color(50, 205, 50)};
        corsiChartPanel = new AbstractBarChartPanel(labels, colors);
        
        corsiChartPanel.setOpaque(false);
        card.add(corsiChartPanel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 300));
        return card;
    }

    private JPanel buildReportStatistichePanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(PADDING - 5, PADDING, PADDING, PADDING)));
        // Usa BorderLayout
        card.setLayout(new BorderLayout(0, 16));

        maxValueLabel = new JLabel("0");
        minValueLabel = new JLabel("0");
        avgValueLabel = new JLabel("0.0");
        
        // Aggiunge il titolo in NORTH
        card.add(createCardTitle("Statistiche Ricette"), BorderLayout.NORTH);

        // Pannello per i dati
        JPanel dataPanel = new JPanel();
        dataPanel.setOpaque(false);
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.add(createDataRow("Numero Massimo Ricette:", maxValueLabel));
        dataPanel.add(Box.createVerticalStrut(8));
        dataPanel.add(createDataRow("Numero Minimo Ricette:", minValueLabel));
        dataPanel.add(Box.createVerticalStrut(8));
        dataPanel.add(createDataRow("Media Ricette Realizzate:", avgValueLabel));
        
        card.add(dataPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildRicetteBarChartPanel() {
        // Questa card usa già BorderLayout e createCardTitle in NORTH
        JPanel card = createBaseCardPanel();
        card.setLayout(new BorderLayout());
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
        int meseSelezionato = meseComboBox.getSelectedIndex() + 1;
        int annoSelezionato = (Integer) annoComboBox.getSelectedItem();

        try {
            int corsi = controller.getNumeroCorsiTenutiNelMese(meseSelezionato, annoSelezionato);
            int online = controller.getNumeroSessioniOnlineTenuteNelMese(meseSelezionato, annoSelezionato);
            int pratica = controller.getNumeroSessioniInPresenzaTenuteNelMese(meseSelezionato, annoSelezionato);
            
            StatisticheRicetteDTO stats = controller.getStatisticheRicettePerReport(meseSelezionato, annoSelezionato);
            
            
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
        corsiValueLabel.setText(String.valueOf(corsi));
        onlineValueLabel.setText(String.valueOf(online));
        praticaValueLabel.setText(String.valueOf(pratica));

        corsiChartPanel.updateData(corsi, online, pratica);

        maxValueLabel.setText(String.valueOf(stats.getMax()));
        minValueLabel.setText(String.valueOf(stats.getMin()));
        avgValueLabel.setText(String.format("%.2f", stats.getAvg()));

        ricetteChartPanel.updateData(stats.getMax(), stats.getMin(), stats.getAvg());

        this.revalidate();
        this.repaint();
    }

    
    private JPanel createBaseCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY),
            new EmptyBorder(PADDING - 5, PADDING, PADDING, PADDING)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        return panel;
    }

    private JLabel createCardTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITOLO_CARD);
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
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
                repaint();
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

            if (maxValue == 0) maxValue = 1;

            int panelHeight = getHeight() - 40;
            int panelWidth = getWidth();

            int totalBarSpace = (BAR_WIDTH * values.length) + (BAR_GAP * (values.length - 1));
            int startX = (panelWidth - totalBarSpace) / 2;

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / maxValue) * panelHeight);
                int y = panelHeight - barHeight + 20;
                int x = startX + i * (BAR_WIDTH + BAR_GAP);

                g2.setColor(colors[i]);
                g2.fillRect(x, y, BAR_WIDTH, barHeight);

                g2.setColor(TEXT_DARK);
                g2.setFont(FONT_VALUE);
                
                String valueStr;
                if (values[i] % 1 == 0) {
                    valueStr = String.valueOf((int)values[i]);
                } else {
                    valueStr = String.format("%.2f", values[i]);
                }
                
                int strWidth = g2.getFontMetrics().stringWidth(valueStr);
                g2.drawString(valueStr, x + (BAR_WIDTH - strWidth) / 2, y - 5);

                g2.setFont(FONT_LABEL);
                strWidth = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (BAR_WIDTH - strWidth) / 2, panelHeight + 35);
            }
        }
    }
}