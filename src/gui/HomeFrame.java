package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
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
import javax.swing.border.EmptyBorder;

public class HomeFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPane;
	private JPanel sideBarPanel;
	private JPanel dashboardPanel;
	private JPanel dashboardContentPanel;
	private JPanel bannerPanel;
	private JLabel greetingLabel;
	private JButton viewCoursesButton;
	private JButton viewChefsButton;
	private JButton viewRecipesButton;
	private JButton viewMonthlyReportButton;

	private final List<SideBarSection> sideBarSections = new ArrayList<>();

	@SuppressWarnings("unused")
	private final LoginFrame previous;

	public HomeFrame(LoginFrame previous) {
		super("UninaFoodLab");
		this.previous = previous;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(850, 500);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		buildSideBar();
		buildBanner();
		buildDashboard();
		buildMainActions();
	}

	private void buildSideBar() {
		sideBarSections.clear();
		sideBarPanel = new JPanel(new BorderLayout());
		sideBarPanel.setBackground(new Color(27, 52, 84));
		sideBarPanel.setBounds(0, 0, 200, 500);
		contentPane.add(sideBarPanel);

		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(20, 24, 12, 18));

		SimpleBrandLogo brandLogo = new SimpleBrandLogo(Color.WHITE, new Color(17, 36, 62));
		brandLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
		brandLogo.setPreferredSize(new Dimension(48, 48));
		brandLogo.setMaximumSize(new Dimension(48, 48));
		headerPanel.add(brandLogo);

		headerPanel.add(Box.createVerticalStrut(8));

		JLabel brandName = new JLabel("UninaFoodLab", SwingConstants.LEFT);
		brandName.setFont(new Font("SansSerif", Font.BOLD, 16));
		brandName.setForeground(Color.WHITE);
		brandName.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerPanel.add(brandName);

		sideBarPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel menuContainer = new JPanel();
		menuContainer.setOpaque(false);
		menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
		menuContainer.setBorder(new EmptyBorder(0, 16, 24, 12));

		JButton dashboardButton = createNavButton("Dashboard");
		dashboardButton.addActionListener(event -> {
			event.getSource();
			collapseAllSections();
		});
		menuContainer.add(dashboardButton);
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Corsi", "Inserisci nuovo corso", "Gestisci i tuoi corsi");
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Sessioni", "Inserisci sessione", "Gestisci sessioni");
		menuContainer.add(Box.createVerticalStrut(12));

		addExpandableSection(menuContainer, "Ricette", "Inserisci nuova ricetta", "Gestisci ricette");
		menuContainer.add(Box.createVerticalStrut(12));

		JButton profileButton = createNavButton("Profilo");
		profileButton.addActionListener(event -> {
			event.getSource();
			collapseAllSections();
		});
		menuContainer.add(profileButton);
		menuContainer.add(Box.createVerticalGlue());

		sideBarPanel.add(menuContainer, BorderLayout.CENTER);
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

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setOpaque(true);
				button.setBackground(new Color(255, 255, 255, 35));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setOpaque(false);
				button.setBackground(new Color(27, 52, 84));
			}
		});

		return button;
	}

	private void addExpandableSection(JPanel container, String title, String... entries) {
		SideBarSection section = createSection(title, entries);
		container.add(section.wrapperPanel);
		sideBarSections.add(section);
	}

	private SideBarSection createSection(String title, String... entries) {
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
		}

		contentPanel.setVisible(false);
		wrapper.add(contentPanel);

		SideBarSection section = new SideBarSection(title, headerButton, contentPanel, wrapper);

		headerButton.addActionListener(event -> {
			event.getSource();
			toggleSection(section);
		});

		return section;
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

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setOpaque(true);
				button.setBackground(new Color(255, 255, 255, 35));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setOpaque(false);
				button.setBackground(new Color(27, 52, 84));
			}
		});

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

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setOpaque(true);
				button.setBackground(new Color(255, 255, 255, 25));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setOpaque(false);
				button.setBackground(new Color(27, 52, 84));
			}
		});

		return button;
	}

	private void toggleSection(SideBarSection target) {
		boolean shouldExpand = !target.contentPanel.isVisible();
		collapseAllSections();
		setSectionExpanded(target, shouldExpand);
	}

	private void collapseAllSections() {
		for (SideBarSection section : sideBarSections) {
			setSectionExpanded(section, false);
		}
	}

	private void setSectionExpanded(SideBarSection section, boolean expanded) {
		section.contentPanel.setVisible(expanded);
		section.headerButton.setText(buildSectionTitle(section.title, expanded));
	}

	private String buildSectionTitle(String title, boolean expanded) {
		String arrow = expanded ? "\u25BC" : "\u25B6";
		return arrow + "  " + title;
	}

	private static class SideBarSection {
		private final String title;
		private final JButton headerButton;
		private final JPanel contentPanel;
		private final JPanel wrapperPanel;

		private SideBarSection(String title, JButton headerButton, JPanel contentPanel, JPanel wrapperPanel) {
			this.title = title;
			this.headerButton = headerButton;
			this.contentPanel = contentPanel;
			this.wrapperPanel = wrapperPanel;
		}
	}

	private void buildBanner() {
		bannerPanel = new JPanel(new BorderLayout());
		bannerPanel.setBackground(Color.WHITE);
		bannerPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
		bannerPanel.setBounds(200, 0, 650, 48);
		contentPane.add(bannerPanel);

		greetingLabel = new JLabel("Benvenuto", SwingConstants.LEFT);
		greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
		greetingLabel.setForeground(new Color(51, 63, 81));
		bannerPanel.add(greetingLabel, BorderLayout.WEST);
	}

	public void setGreetingText(String text) {
		if (greetingLabel != null) {
			greetingLabel.setText(text);
		}
	}

	private void buildDashboard() {
		dashboardPanel = new JPanel(new BorderLayout());
		dashboardPanel.setBackground(new Color(248, 249, 252));
		dashboardPanel.setBounds(200, 48, 650, 452);
		contentPane.add(dashboardPanel);

		dashboardContentPanel = new JPanel();
		dashboardContentPanel.setOpaque(false);
		dashboardContentPanel.setLayout(new BoxLayout(dashboardContentPanel, BoxLayout.Y_AXIS));
		dashboardContentPanel.setBorder(new EmptyBorder(20, 32, 24, 32));
		dashboardPanel.add(dashboardContentPanel, BorderLayout.CENTER);

		JPanel infoCard = new JPanel();
		infoCard.setOpaque(false);
		infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
		infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel infoCardBody = new JPanel();
		infoCardBody.setBackground(Color.WHITE);
		infoCardBody.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 235, 245)),
			new EmptyBorder(20, 24, 20, 24)));
		infoCardBody.setLayout(new BoxLayout(infoCardBody, BoxLayout.Y_AXIS));

		JLabel cardTitle = new JLabel("Scopri tutte le funzioni!");
		cardTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
		cardTitle.setForeground(new Color(51, 63, 81));
		cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoCardBody.add(cardTitle);

		infoCardBody.add(Box.createVerticalStrut(6));

		JLabel cardSubtitle = new JLabel("Gestisci corsi, ricette e sessioni in un unico spazio organizzato.");
		cardSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
		cardSubtitle.setForeground(new Color(90, 103, 123));
		cardSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoCardBody.add(cardSubtitle);

		infoCardBody.add(Box.createVerticalStrut(10));

		JLabel cardHint = new JLabel("Sfrutta il menù laterale per iniziare →");
		cardHint.setFont(new Font("SansSerif", Font.PLAIN, 13));
		cardHint.setForeground(new Color(122, 133, 152));
		cardHint.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoCardBody.add(cardHint);

		infoCardBody.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoCard.add(infoCardBody);
		int infoCardHeight = infoCardBody.getPreferredSize().height + 16;
		infoCard.setMaximumSize(new Dimension(520, infoCardHeight));
		infoCard.setPreferredSize(new Dimension(520, infoCardHeight));

		dashboardContentPanel.add(infoCard);
		dashboardContentPanel.add(Box.createVerticalStrut(24));
	}

	private void buildMainActions() {
		JPanel actionsPanel = new JPanel();
		actionsPanel.setOpaque(false);
		actionsPanel.setLayout(new GridLayout(2, 2, 16, 16));
		actionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionsPanel.setMaximumSize(new Dimension(520, 192));
		actionsPanel.setPreferredSize(new Dimension(520, 192));

		viewCoursesButton = createActionButton("Visualizza tutti i corsi", "Scopri l'offerta formativa aggiornata");
		viewChefsButton = createActionButton("Visualizza tutti gli chef", "Consulta il team e le loro specialità");
		viewRecipesButton = createActionButton("Visualizza tutte le ricette", "Trova idee da proporre ai tuoi corsi");
		viewMonthlyReportButton = createActionButton("Visualizza report mensile", "Monitora risultati e iscrizioni");

		actionsPanel.add(viewCoursesButton);
		actionsPanel.add(viewChefsButton);
		actionsPanel.add(viewRecipesButton);
		actionsPanel.add(viewMonthlyReportButton);

		dashboardContentPanel.add(actionsPanel);
	}

	private JButton createActionButton(String title, String subtitle) {
		JButton button = new JButton(buildActionButtonText(title, subtitle));
		button.setFocusPainted(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setVerticalAlignment(SwingConstants.TOP);
		button.setFont(new Font("SansSerif", Font.PLAIN, 13));
		button.setForeground(Color.WHITE);
		button.setBackground(SystemColor.textHighlight);
		button.setOpaque(true);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(1, 1, 3, 1, SystemColor.textHighlight.darker()),
			new EmptyBorder(16, 20, 18, 20)));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		installButtonHoverEffect(button, SystemColor.textHighlight);
		return button;
	}

	private String buildActionButtonText(String title, String subtitle) {
		return "<html><div style=\"text-align:left;\">"
			+ "<div style=\"font-size:14px;font-weight:700;margin-bottom:6px;letter-spacing:0.2px;\">" + title + "</div>"
			+ "<div style=\"border-bottom:1px solid #ffffff;margin-bottom:8px;opacity:0.45;\"></div>"
			+ "<div style=\"font-size:11px;color:#f0f4ff;line-height:1.45;\">" + subtitle + "</div>"
			+ "</div></html>";
	}

	private void installButtonHoverEffect(JButton button, Color baseColor) {
		Color hoverColor = baseColor.darker();
		Color pressColor = hoverColor.darker();
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hoverColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(baseColor);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				button.setBackground(pressColor);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (button.getBounds().contains(e.getPoint())) {
					button.setBackground(hoverColor);
				} else {
					button.setBackground(baseColor);
				}
			}
		});
	}
}
