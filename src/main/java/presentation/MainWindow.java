package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application Fast-Repair
 * Gère la navigation entre les différentes interfaces utilisateur
 */
public class MainWindow extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel navigationPanel;
    private JPanel contentPanel;

    // Panels pour chaque interface
    private AuthentificationPanel authPanel;
    private AdminPanel adminPanel;
    private ReparateurPanel reparateurPanel;
    private ProprietairePanel proprietairePanel;
    private SuiviReparationPanel suiviPanel;

    // État de l'utilisateur connecté
    private String currentUserRole = null;
    private String currentUserEmail = null;

    public MainWindow() {
        initializeComponents();
        setupLayout();
        setupNavigation();
        setupListeners();

        setTitle("Fast-Repair - Système de Gestion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Afficher le panel d'authentification par défaut
        showPanel("AUTH");
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        navigationPanel = new JPanel();
        contentPanel = new JPanel(cardLayout);

        // Initialiser les panels
        authPanel = new AuthentificationPanel(this);
        adminPanel = new AdminPanel(this);
        reparateurPanel = new ReparateurPanel(this);
        proprietairePanel = new ProprietairePanel(this);
        suiviPanel = new SuiviReparationPanel(this);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel de navigation en haut
        setupNavigationPanel();

        // Panel principal avec CardLayout
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(navigationPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Ajouter les panels au CardLayout
        contentPanel.add(authPanel, "AUTH");
        contentPanel.add(adminPanel, "ADMIN");
        contentPanel.add(reparateurPanel, "REPARATEUR");
        contentPanel.add(proprietairePanel, "PROPRIETAIRE");
        contentPanel.add(suiviPanel, "SUIVI");

        add(mainPanel);
    }

    private void setupNavigationPanel() {
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        navigationPanel.setBackground(new Color(70, 130, 180));

        // Boutons de navigation
        JButton btnAccueil = createNavButton("🏠 Accueil");
        JButton btnSuivi = createNavButton("🔍 Suivre Réparation");
        JButton btnAuth = createNavButton("🔐 Connexion");
        JButton btnDeconnexion = createNavButton("🚪 Déconnexion");

        navigationPanel.add(btnAccueil);
        navigationPanel.add(btnSuivi);
        navigationPanel.add(Box.createHorizontalGlue()); // Espacement
        navigationPanel.add(btnAuth);
        navigationPanel.add(btnDeconnexion);

        // Masquer certains boutons selon l'état de connexion
        updateNavigationVisibility();
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupListeners() {
        // Les listeners sont gérés dans les méthodes spécifiques
    }

    private void setupNavigation() {
        // Les actions de navigation sont définies dans les listeners
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        updateNavigationVisibility();

        // Mettre à jour le titre selon le panel affiché
        switch (panelName) {
            case "AUTH":
                setTitle("Fast-Repair - Authentification");
                break;
            case "ADMIN":
                setTitle("Fast-Repair - Interface Administrateur");
                break;
            case "REPARATEUR":
                setTitle("Fast-Repair - Interface Réparateur");
                break;
            case "PROPRIETAIRE":
                setTitle("Fast-Repair - Interface Propriétaire");
                break;
            case "SUIVI":
                setTitle("Fast-Repair - Suivi de Réparation");
                break;
            default:
                setTitle("Fast-Repair - Système de Gestion");
        }
    }

    private void updateNavigationVisibility() {
        // Cette méthode sera appelée depuis les panels enfants
    }

    // Getters pour les panels
    public AuthentificationPanel getAuthPanel() { return authPanel; }
    public AdminPanel getAdminPanel() { return adminPanel; }
    public ReparateurPanel getReparateurPanel() { return reparateurPanel; }
    public ProprietairePanel getProprietairePanel() { return proprietairePanel; }
    public SuiviReparationPanel getSuiviPanel() { return suiviPanel; }

    // Gestion de l'état de connexion
    public void setCurrentUser(String role, String email) {
        this.currentUserRole = role;
        this.currentUserEmail = email;

        // Afficher le panel approprié selon le rôle
        switch (role.toUpperCase()) {
            case "ADMIN":
                showPanel("ADMIN");
                break;
            case "REPARATEUR":
                showPanel("REPARATEUR");
                break;
            case "PROPRIETAIRE":
                showPanel("PROPRIETAIRE");
                break;
            default:
                showPanel("AUTH");
        }
    }

    public void logout() {
        this.currentUserRole = null;
        this.currentUserEmail = null;
        showPanel("AUTH");
    }

    public String getCurrentUserRole() { return currentUserRole; }
    public String getCurrentUserEmail() { return currentUserEmail; }

    // Méthode principale pour lancer l'application
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de Fast-Repair...");

        // Lancer l'interface graphique dans l'EDT
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("📱 Initialisation de l'interface graphique...");
                new MainWindow().setVisible(true);
                System.out.println("✅ Interface graphique lancée avec succès!");
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du démarrage de l'interface: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage de l'application:\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
