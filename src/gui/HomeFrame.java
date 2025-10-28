package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import session.SessionChef;
import gui.panel.CreaNuovaSessionePanel;
import gui.panel.CreaNuovoCorsoPanel;
import gui.panel.GestioneCorsiPanel;
import gui.panel.GestioneSessioniPanel;
import gui.panel.ProfilePanel;
import gui.panel.VisualizzaReportPanel;
import gui.panel.VisualizzaRicetteChefPanel;
import gui.panel.VisualizzaTuttiChefPanel;
import gui.panel.VisualizzaTuttiCorsiPanel;


public class HomeFrame extends JFrame {

	private static final long serialVersionUID = 1L;
    private static final Color BUTTON_BASE_COLOR = new Color(30, 144, 255);

	private final JPanel contentPane;
	private JPanel sideBarPanel;
    private JPanel headerPanel;
    private SimpleBrandLogo brandLogo;
    private JLabel brandName;
    private JPanel menuContainer;
    private JButton dashboardButton;
    private JButton profileButton;
	private JPanel dashboardPanel;
	private JPanel dashboardContentPanel;
	private JPanel bannerPanel;
	private JLabel greetingLabel;
    private JPanel infoCard;
    private JPanel infoCardBody;
    private JLabel cardTitle;
    private JLabel cardSubtitle;
    private JLabel cardHint;
    private JPanel actionsPanel;
	private JPanel viewCoursesButtonPanel; 
	private JPanel viewChefsButtonPanel; 
	private JPanel viewMonthlyReportButtonPanel;

	private final List<JPanel> sideBarContentPanels = new ArrayList<>();

	@SuppressWarnings("unused")
	private final LoginFrame previous;

	public HomeFrame(LoginFrame previous) {
		super("UninaFoodLab");
		this.previous = previous;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(1000, 600);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		buildSideBar();
		buildDashboard();
		buildBanner();
		buildMainActions();

        showMainPanel(buildDashboardContentPanel());
	}

	private void buildSideBar() {
		sideBarContentPanels.clear();
		sideBarPanel = new JPanel(new BorderLayout());
		sideBarPanel.setBackground(new Color(27, 52, 84));
		sideBarPanel.setBounds(0, 0, 200, 600);
		contentPane.add(sideBarPanel);

		headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(20, 24, 12, 24));

		brandLogo = new SimpleBrandLogo(Color.WHITE, new Color(17, 36, 62));
		brandLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
		brandLogo.setPreferredSize(new Dimension(48, 48));
		brandLogo.setMaximumSize(new Dimension(48, 48));
		headerPanel.add(brandLogo);

		headerPanel.add(Box.createVerticalStrut(8));

		brandName = new JLabel("UninaFoodLab", SwingConstants.LEFT);
		brandName.setFont(new Font("SansSerif", Font.BOLD, 16));
		brandName.setForeground(Color.WHITE);
		brandName.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerPanel.add(brandName);

		sideBarPanel.add(headerPanel, BorderLayout.NORTH);

		menuContainer = new JPanel();
		menuContainer.setOpaque(false);
		menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
		menuContainer.setBorder(new EmptyBorder(0, 16, 24, 16));

		dashboardButton = createNavButton("Dashboard");
		dashboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				e.getSource();
				collapseAllSections();
				showMainPanel(buildDashboardContentPanel());
			}
		});
		menuContainer.add(dashboardButton);
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Corsi", "Crea nuovo corso", "Gestisci i tuoi corsi");
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Sessioni", "Crea nuova sessione", "Gestisci le tue sessioni");
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Ricette", "Visualizza ricette");
		menuContainer.add(Box.createVerticalStrut(12));

		profileButton = createNavButton("Profilo");
		profileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				e.getSource();
				collapseAllSections();
				showMainPanel(new ProfilePanel(HomeFrame.this));
			}
		});
		menuContainer.add(profileButton);
		menuContainer.add(Box.createVerticalGlue());

		sideBarPanel.add(menuContainer, BorderLayout.CENTER);
	}

	private void buildBanner() {
		bannerPanel = new JPanel(new BorderLayout());
		bannerPanel.setBackground(Color.WHITE);
		bannerPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
		bannerPanel.setBounds(200, 0, 800, 48);
		contentPane.add(bannerPanel);

		greetingLabel = new JLabel("Bentornato "+SessionChef.getNameChef()+" !", SwingConstants.LEFT);
		greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		greetingLabel.setForeground(new Color(51, 63, 81));
		bannerPanel.add(greetingLabel, BorderLayout.CENTER);
	}


	private void buildDashboard() {
		dashboardPanel = new JPanel(new BorderLayout());
		dashboardPanel.setBackground(new Color(248, 249, 252));
		dashboardPanel.setBounds(200, 48, 800, 552);
		contentPane.add(dashboardPanel);

		dashboardContentPanel = new JPanel(new BorderLayout());
        dashboardContentPanel.setOpaque(false);
        dashboardPanel.add(dashboardContentPanel, BorderLayout.CENTER);
	}


    private JPanel buildDashboardContentPanel() {
        JPanel verticalWrapper = new JPanel();
        verticalWrapper.setOpaque(false);
        verticalWrapper.setLayout(new BoxLayout(verticalWrapper, BoxLayout.Y_AXIS));
        verticalWrapper.setBorder(new EmptyBorder(20, 32, 24, 32));

        infoCard = new JPanel();
        infoCard.setOpaque(false);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoCardBody = new JPanel();
        infoCardBody.setBackground(Color.WHITE);
        infoCardBody.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 245)),
            new EmptyBorder(20, 24, 20, 24)));
        infoCardBody.setLayout(new BoxLayout(infoCardBody, BoxLayout.Y_AXIS));

        cardTitle = new JLabel("Scopri tutte le funzioni!");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        cardTitle.setForeground(new Color(51, 63, 81));
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCardBody.add(cardTitle);

        infoCardBody.add(Box.createVerticalStrut(6));

        cardSubtitle = new JLabel("Gestisci corsi, ricette e sessioni in un unico spazio organizzato.");
        cardSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardSubtitle.setForeground(new Color(90, 103, 123));
        cardSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCardBody.add(cardSubtitle);

        infoCardBody.add(Box.createVerticalStrut(10));

        cardHint = new JLabel("Sfrutta il menù laterale per iniziare →");
        cardHint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cardHint.setForeground(new Color(122, 133, 152));
        cardHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCardBody.add(cardHint);

        infoCardBody.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoCardBody);
        int infoCardHeight = infoCardBody.getPreferredSize().height + 16;

        infoCard.setMaximumSize(new Dimension(580, infoCardHeight));
        infoCard.setPreferredSize(new Dimension(580, infoCardHeight));

        verticalWrapper.add(infoCard);
        verticalWrapper.add(Box.createVerticalStrut(24));

        buildMainActionsDashboard(verticalWrapper);

        return verticalWrapper;
    }


	private void buildMainActions() {

        viewCoursesButtonPanel = createActionPanelButton(
            "Visualizza tutti i corsi",
            "Sfoglia il catalogo completo dei corsi offerti.",
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMainPanel(new VisualizzaTuttiCorsiPanel());
                }
            });

        viewChefsButtonPanel = createActionPanelButton(
            "Visualizza tutti gli chef",
            "Scopri i profili degli chef che collaborano con UninaFoodLab.",
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMainPanel(new VisualizzaTuttiChefPanel());
                }
            });

        viewMonthlyReportButtonPanel = createActionPanelButton(
            "Visualizza report mensile",
            "Analizza le statistiche e le performance dei tuoi corsi.",
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showMainPanel(new VisualizzaReportPanel());
                }
            });
	}

	private void buildMainActionsDashboard(JPanel targetPanel) {
		actionsPanel = new JPanel(new GridLayout(3, 1, 0, 16)); // 3 Righe, 1 Colonna, 16 vgap
		actionsPanel.setOpaque(false);
		actionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionsPanel.setMaximumSize(new Dimension(580, 300)); // Aumentata altezza
		actionsPanel.setPreferredSize(new Dimension(580, 300));

		actionsPanel.add(viewCoursesButtonPanel);
		actionsPanel.add(viewChefsButtonPanel);
		actionsPanel.add(viewMonthlyReportButtonPanel);

		targetPanel.add(actionsPanel);
	}

	// =========================================================================
	// FUNZIONI AUSILIARIE - INTERFACCIA
	// =========================================================================

    public void showMainPanel(JPanel newPanel) {
        dashboardContentPanel.removeAll();
        dashboardContentPanel.add(newPanel, BorderLayout.CENTER);
        dashboardContentPanel.revalidate();
        dashboardContentPanel.repaint();
    }

	public void setGreetingText(String text) {
		if (greetingLabel != null) {
			greetingLabel.setText(text);
		}
	}

	private JButton createNavButton(String text) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFont(new Font("SansSerif", Font.BOLD, 15));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(27, 52, 84));
		button.setOpaque(false);
		button.setBorder(new EmptyBorder(10, 12, 10, 8));
		return button;
	}

	private void addExpandableSection(JPanel container, String title, String... entries) {
		container.add(createSection(title, entries));
	}

	private JPanel createSection(String title, String... entries) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setOpaque(false);
		wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

		JButton headerButton = createSectionButton(title, false);
		wrapper.add(headerButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(6, 18, 0, 0));

		for (int i = 0; i < entries.length; i++) {
			JButton itemButton = createSubItemButton(entries[i]);
			contentPanel.add(itemButton);
			if (i < entries.length - 1) {
				contentPanel.add(Box.createVerticalStrut(6));
			}

			if(title.equals("Corsi")) {
				if(i==0) {
					itemButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							collapseAllSections();
							showMainPanel(new CreaNuovoCorsoPanel());
						}
					});
				}
				else if(i==1) {
					itemButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							collapseAllSections();
							showMainPanel(new GestioneCorsiPanel());
						}
					});
				}
			}
			else if(title.equals("Sessioni")) {
				if(i==0) {
					itemButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							collapseAllSections();
							showMainPanel(new CreaNuovaSessionePanel());
						}
					});
				}
				else if(i==1) {
					itemButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							collapseAllSections();
							showMainPanel(new GestioneSessioniPanel());
						}
					});
				}
			}
			else if(title.equals("Ricette")) {
				if(i==0) {
					itemButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							collapseAllSections();
							showMainPanel(new VisualizzaRicetteChefPanel());
						}
					});
				}
			}
		}

		contentPanel.setVisible(false);
		wrapper.add(contentPanel);

		sideBarContentPanels.add(contentPanel);

		headerButton.addActionListener(event -> {
			event.getSource();
			toggleSection(contentPanel, headerButton, title);
		});

		return wrapper;
	}

	private JButton createSectionButton(String title, boolean expanded) {
		String text = buildSectionTitle(title, expanded);
		JButton button = new JButton(text);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFont(new Font("SansSerif", Font.BOLD, 15));
		button.setForeground(Color.WHITE);
		button.setBackground(new Color(27, 52, 84));
		button.setOpaque(false);
		button.setBorder(new EmptyBorder(10, 12, 10, 8));
		return button;
	}

	private JButton createSubItemButton(String text) {
		JButton button = new JButton(text);
		button.setAlignmentX(Component.LEFT_ALIGNMENT);
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setFont(new Font("SansSerif", Font.PLAIN, 12));
		button.setForeground(new Color(224, 233, 247));
		button.setBackground(new Color(27, 52, 84));
		button.setOpaque(false);
		button.setBorder(new EmptyBorder(6, 12, 6, 8));
		return button;
	}

	private void toggleSection(JPanel contentPanel, JButton headerButton, String title) {
		boolean shouldExpand = !contentPanel.isVisible();
		collapseAllSections();
		setSectionExpanded(contentPanel, headerButton, title, shouldExpand);
	}

	private void collapseAllSections() {
		for (JPanel contentPanel : sideBarContentPanels) {
			if (contentPanel.isVisible()) {
				JPanel wrapper = (JPanel) contentPanel.getParent();
				JButton headerButton = (JButton) wrapper.getComponent(0);
				String text = headerButton.getText();
				String title = text.substring(3);
				setSectionExpanded(contentPanel, headerButton, title, false);
			}
		}
	}

	private void setSectionExpanded(JPanel contentPanel, JButton headerButton, String title, boolean expanded) {
		contentPanel.setVisible(expanded);
		headerButton.setText(buildSectionTitle(title, expanded));
	}

	private String buildSectionTitle(String title, boolean expanded) {
		String arrow = expanded ? "\u25BC" : "\u25B6";
		return arrow + "  " + title;
	}

	// Sostituito createActionButton con createActionPanelButton
    private JPanel createActionPanelButton(String title, String description, ActionListener actionListener) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BUTTON_BASE_COLOR);
        panel.setOpaque(true);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        Color shadowColor = BUTTON_BASE_COLOR.darker();
        Color underlineColor = new Color(255, 255, 255, 120);

        JPanel textWrapper = new JPanel();
        textWrapper.setOpaque(false);
        textWrapper.setLayout(new BoxLayout(textWrapper, BoxLayout.Y_AXIS));
        
        Border lineAndPadding = BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 20, 16, 20), // Aumentato padding verticale
            BorderFactory.createMatteBorder(0, 0, 1, 0, underlineColor)
        );
        textWrapper.setBorder(lineAndPadding);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Leggermente più piccolo
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textWrapper.add(titleLabel);

        textWrapper.add(Box.createVerticalStrut(4));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(new Color(230, 242, 255)); // Bianco leggermente trasparente
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textWrapper.add(descLabel);

        panel.add(textWrapper, BorderLayout.CENTER);

        // Applica bordo esterno per ombra
		panel.setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, shadowColor));

        // Installa effetto hover su tutto il pannello
		installPanelHoverEffect(panel, BUTTON_BASE_COLOR);
        
        // Aggiunge un MouseListener per gestire il click sull'intero pannello
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Crea un ActionEvent fittizio per riutilizzare l'action listener
                actionListener.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, null));
            }
        });

		return panel;
	}

	// Modificato installButtonHoverEffect in installPanelHoverEffect
	private void installPanelHoverEffect(JPanel panel, Color baseColor) {
		Color hoverColor = baseColor.darker();
		Color pressColor = hoverColor.darker();
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				panel.setBackground(hoverColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				panel.setBackground(baseColor);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				panel.setBackground(pressColor);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (panel.getBounds().contains(e.getPoint())) {
					panel.setBackground(hoverColor);
				} else {
					panel.setBackground(baseColor);
				}
			}
		});
	}
}