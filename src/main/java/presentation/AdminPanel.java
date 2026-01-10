package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel d'administration avec toutes les fonctionnalités CRUD
 */
public class AdminPanel extends JPanel {

    private MainWindow mainWindow;

    // Gestionnaires métier
    private metier.GestionClient gestionClient;
    private metier.GestionAppareil gestionAppareil;
    private metier.GestionReparation gestionReparation;
    private metier.GestionReparateur gestionReparateur;
    private metier.GestionProprietaire gestionProprietaire;
    private metier.GestionBoutique gestionBoutique;
    private metier.GestionCaisse gestionCaisse;
    private metier.GestionComposant gestionComposant;
    private metier.GestionEmprunt gestionEmprunt;
    private metier.GestionRecu gestionRecu;

    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton logoutButton;

    public AdminPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        // Initialiser tous les gestionnaires
        initializeGestionnaires();

        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeGestionnaires() {
        gestionClient = new metier.GestionClient();
        gestionAppareil = new metier.GestionAppareil();
        gestionReparation = new metier.GestionReparation();
        gestionReparateur = new metier.GestionReparateur();
        gestionProprietaire = new metier.GestionProprietaire();
        gestionBoutique = new metier.GestionBoutique();
        gestionCaisse = new metier.GestionCaisse();
        gestionComposant = new metier.GestionComposant();
        gestionEmprunt = new metier.GestionEmprunt();
        gestionRecu = new metier.GestionRecu();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        logoutButton = new JButton("🚪 Déconnexion");

        // Style du bouton
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Créer les onglets pour chaque entité
        createClientTab();
        createAppareilTab();
        createReparationTab();
        createReparateurTab();
        createProprietaireTab();
        createBoutiqueTab();
        createCaisseTab();
        createComposantTab();
        createEmpruntTab();
        createRecuTab();
        createDashboardTab();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel du haut avec le titre et le bouton de déconnexion
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Interface Administrateur - Fast-Repair", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupListeners() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    AdminPanel.this,
                    "Êtes-vous sûr de vouloir vous déconnecter ?",
                    "Confirmation de déconnexion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    mainWindow.logout();
                }
            }
        });
    }

    private void createClientTab() {
        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.add(createCrudPanel("Client", new String[]{"ID", "Nom", "Prénom", "Adresse", "Téléphone"}), BorderLayout.CENTER);
        tabbedPane.addTab("👥 Clients", clientPanel);
    }

    private void createAppareilTab() {
        JPanel appareilPanel = new JPanel(new BorderLayout());
        appareilPanel.add(createCrudPanel("Appareil", new String[]{"ID Appareil", "IMEI", "Marque", "Modèle", "Type"}), BorderLayout.CENTER);
        tabbedPane.addTab("📱 Appareils", appareilPanel);
    }

    private void createReparationTab() {
        JPanel reparationPanel = new JPanel(new BorderLayout());
        reparationPanel.add(createCrudPanel("Reparation", new String[]{"ID Appareil", "Code Suivi", "Date Dépôt", "État", "Commentaire", "Prix Total"}), BorderLayout.CENTER);
        tabbedPane.addTab("🔧 Réparations", reparationPanel);
    }

    private void createReparateurTab() {
        JPanel reparateurPanel = new JPanel(new BorderLayout());
        reparateurPanel.add(createCrudPanel("Reparateur", new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe", "% Gain"}), BorderLayout.CENTER);
        tabbedPane.addTab("👷 Réparateurs", reparateurPanel);
    }

    private void createProprietaireTab() {
        JPanel proprietairePanel = new JPanel(new BorderLayout());
        proprietairePanel.add(createCrudPanel("Proprietaire", new String[]{"ID", "Nom", "Prénom", "Email", "Mot de passe"}), BorderLayout.CENTER);
        tabbedPane.addTab("🏢 Propriétaires", proprietairePanel);
    }

    private void createBoutiqueTab() {
        JPanel boutiquePanel = new JPanel(new BorderLayout());
        boutiquePanel.add(createCrudPanel("Boutique", new String[]{"ID Boutique", "Nom", "Adresse", "Téléphone", "Numéro P"}), BorderLayout.CENTER);
        tabbedPane.addTab("🏪 Boutiques", boutiquePanel);
    }

    private void createCaisseTab() {
        JPanel caissePanel = new JPanel(new BorderLayout());
        caissePanel.add(createCrudPanel("Caisse", new String[]{"ID Caisse", "Solde Actuel", "Dernier Mouvement"}), BorderLayout.CENTER);
        tabbedPane.addTab("💰 Caisses", caissePanel);
    }

    private void createComposantTab() {
        JPanel composantPanel = new JPanel(new BorderLayout());
        composantPanel.add(createCrudPanel("Composant", new String[]{"ID Composant", "Nom", "Prix", "Quantité"}), BorderLayout.CENTER);
        tabbedPane.addTab("🔩 Composants", composantPanel);
    }

    private void createEmpruntTab() {
        JPanel empruntPanel = new JPanel(new BorderLayout());
        empruntPanel.add(createCrudPanel("Emprunt", new String[]{"ID Emprunt", "Date", "Montant", "Type", "Commentaire"}), BorderLayout.CENTER);
        tabbedPane.addTab("💸 Emprunts", empruntPanel);
    }

    private void createRecuTab() {
        JPanel recuPanel = new JPanel(new BorderLayout());
        recuPanel.add(createCrudPanel("Recu", new String[]{"ID Reçu", "Date", "Montant"}), BorderLayout.CENTER);
        tabbedPane.addTab("🧾 Reçus", recuPanel);
    }

    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(createDashboardContent(), BorderLayout.CENTER);
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
    }

    private JPanel createCrudPanel(String entityName, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnAjouter = new JButton("➕ Ajouter");
        JButton btnModifier = new JButton("✏️ Modifier");
        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        JButton btnRechercher = new JButton("🔍 Rechercher");
        JButton btnLister = new JButton("📋 Lister");

        // Style des boutons
        btnAjouter.setBackground(new Color(40, 167, 69));
        btnModifier.setBackground(new Color(255, 193, 7));
        btnSupprimer.setBackground(new Color(220, 53, 69));
        btnRechercher.setBackground(new Color(23, 162, 184));
        btnLister.setBackground(new Color(108, 117, 125));

        for (JButton btn : new JButton[]{btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnLister}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 35));
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table pour afficher les données
        String[] columnNames = columns;
        Object[][] data = {}; // Données vides initialement
        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Ajouter les listeners aux boutons
        addCrudListeners(btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnLister, entityName, table);

        return panel;
    }

    private JPanel createDashboardContent() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Statistiques générales
        try {
            int nbClients = gestionClient.lister().size();
            int nbAppareils = gestionAppareil.lister().size();
            int nbReparations = gestionReparation.lister().size();
            int nbReparateurs = gestionReparateur.lister().size();
            double soldeTotal = calculerSoldeTotal();

            panel.add(createStatCard("👥 Clients", String.valueOf(nbClients), new Color(52, 152, 219)));
            panel.add(createStatCard("📱 Appareils", String.valueOf(nbAppareils), new Color(155, 89, 182)));
            panel.add(createStatCard("🔧 Réparations", String.valueOf(nbReparations), new Color(46, 204, 113)));
            panel.add(createStatCard("👷 Réparateurs", String.valueOf(nbReparateurs), new Color(230, 126, 34)));
            panel.add(createStatCard("💰 Solde Total", String.format("%.2f €", soldeTotal), new Color(231, 76, 60)));
            panel.add(createStatCard("📊 État Système", "Opérationnel", new Color(44, 62, 80)));

        } catch (Exception e) {
            panel.add(createStatCard("❌ Erreur", "Connexion BD", Color.RED));
        }

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private double calculerSoldeTotal() {
        try {
            return gestionCaisse.lister().stream()
                    .mapToDouble(dao.Caisse::getSoldeActuel)
                    .sum();
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void addCrudListeners(JButton btnAjouter, JButton btnModifier, JButton btnSupprimer,
                                 JButton btnRechercher, JButton btnLister, String entityName, JTable table) {

        btnLister.addActionListener(e -> {
            try {
                refreshTableData(table, entityName);
                JOptionPane.showMessageDialog(this, "Liste mise à jour avec succès!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du listage: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Les autres boutons peuvent être implémentés selon les besoins
        // Pour l'instant, on affiche juste un message
        ActionListener placeholderListener = e -> {
            JButton source = (JButton) e.getSource();
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité '" + source.getText() + "' pour " + entityName + " - À implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        };

        btnAjouter.addActionListener(placeholderListener);
        btnModifier.addActionListener(placeholderListener);
        btnSupprimer.addActionListener(placeholderListener);
        btnRechercher.addActionListener(placeholderListener);
    }

    private void refreshTableData(JTable table, String entityName) throws Exception {
        // Cette méthode peut être implémentée pour rafraîchir les données de la table
        // Pour l'instant, on ne fait rien de spécial
        JOptionPane.showMessageDialog(this,
            "Données de " + entityName + " listées (fonctionnalité de base)",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
