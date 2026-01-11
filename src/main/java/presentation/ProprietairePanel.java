package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Panel pour les propriétaires - Statistiques et gestion financière
 */
public class ProprietairePanel extends JPanel {

    private MainWindow mainWindow;

    // Gestionnaires métier
    private metier.GestionCaisse gestionCaisse;
    private metier.GestionRecu gestionRecu;
    private metier.GestionEmprunt gestionEmprunt;
    private metier.GestionBoutique gestionBoutique;
    private metier.GestionReparateur gestionReparateur;
    private metier.GestionAppareil gestionAppareil;
    private metier.GestionReparation gestionReparation;
    private metier.GestionProprietaire gestionProprietaire;

    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public ProprietairePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        // Initialiser les gestionnaires
        gestionCaisse = new metier.GestionCaisse();
        gestionRecu = new metier.GestionRecu();
        gestionEmprunt = new metier.GestionEmprunt();
        gestionBoutique = new metier.GestionBoutique();
        gestionReparateur = new metier.GestionReparateur();
        gestionAppareil = new metier.GestionAppareil();
        gestionReparation = new metier.GestionReparation();
        gestionProprietaire = new metier.GestionProprietaire();

        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        logoutButton = new JButton("🚪 Déconnexion");
        welcomeLabel = new JLabel();

        // Style du bouton
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));

        // Mettre à jour le label de bienvenue
        updateWelcomeLabel();

        // Créer les onglets
        createDashboardTab();
        createFinancesTab();
        createBoutiquesTab();
        createReparateursTab();
        createReparationsTab();
        createCRUDTab();
        createRapportsTab();
        createProfileTab();
    }

    private void updateWelcomeLabel() {
        String email = mainWindow.getCurrentUserEmail();
        welcomeLabel.setText("Bienvenue, Propriétaire: " + (email != null ? email : "Inconnu"));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(70, 130, 180));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel du haut avec le titre et le bouton de déconnexion
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(155, 89, 182)); // Violet pour les propriétaires

        topPanel.add(welcomeLabel, BorderLayout.CENTER);

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
                    ProprietairePanel.this,
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

    /**
     * Méthode appelée quand l'utilisateur se connecte pour rafraîchir les données
     */
    public void onUserLoggedIn() {
        // Mettre à jour le label de bienvenue
        updateWelcomeLabel();

        // Recréer tous les onglets pour s'assurer qu'ils sont dans le bon état
        tabbedPane.removeAll();
        createDashboardTab();
        createFinancesTab();
        createBoutiquesTab();
        createReparateursTab();
        createCRUDTab();
        createRapportsTab();
        createProfileTab();

        // Sélectionner le dashboard par défaut
        tabbedPane.setSelectedIndex(0);
    }

    /**
     * Méthode utilitaire pour récupérer le propriétaire connecté
     */
    private dao.Proprietaire getProprietaireConnecte() {
        String email = mainWindow.getCurrentUserEmail();
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            return gestionProprietaire.rechercherParEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Tableau de Bord - Vue d'ensemble", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(155, 89, 182));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal avec scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel des statistiques générales
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques Financières"));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("💰 Statistiques Financières"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        try {
            // Calculer les statistiques financières
            double soldeTotal = calculerSoldeTotal();
            int nbRecus = gestionRecu.lister().size();
            int nbEmprunts = gestionEmprunt.lister().size();
            double totalRecus = gestionRecu.lister().stream().mapToDouble(dao.Recu::getMontant).sum();
            double totalEmprunts = gestionEmprunt.lister().stream().mapToDouble(dao.Emprunt::getMontant).sum();

            // Ajouter les cartes de statistiques financières
            statsPanel.add(createStatCard("💰 Solde Total", String.format("%.2f €", soldeTotal), new Color(46, 204, 113)));
            statsPanel.add(createStatCard("📈 Recettes", String.format("%.2f €", totalRecus), new Color(46, 204, 113)));
            statsPanel.add(createStatCard("📉 Dépenses", String.format("%.2f €", totalEmprunts), new Color(231, 76, 60)));
            statsPanel.add(createStatCard("📊 Bénéfice", String.format("%.2f €", totalRecus - totalEmprunts), new Color(44, 62, 80)));
            statsPanel.add(createStatCard("🧾 Reçus", String.valueOf(nbRecus), new Color(155, 89, 182)));
            statsPanel.add(createStatCard("💸 Emprunts", String.valueOf(nbEmprunts), new Color(230, 126, 34)));
            statsPanel.add(createStatCard("📊 Ratio", String.format("%.1f%%", totalRecus > 0 ? (totalEmprunts/totalRecus)*100 : 0), new Color(52, 152, 219)));
            statsPanel.add(createStatCard("⚡ État", "Opérationnel", new Color(46, 204, 113)));

        } catch (Exception e) {
            statsPanel.add(createStatCard("❌ Erreur", "BD inaccessible", Color.RED));
        }

        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Panel central avec boutiques et réparateurs
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        // Panel des boutiques
        JPanel boutiquesOverviewPanel = new JPanel(new BorderLayout());
        boutiquesOverviewPanel.setBorder(BorderFactory.createTitledBorder("🏪 Aperçu des Boutiques"));
        boutiquesOverviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("🏪 Mes Boutiques"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        try {
            dao.Proprietaire proprietaire = getProprietaireConnecte();
            if (proprietaire != null) {
                List<dao.Boutique> boutiques = gestionBoutique.lister().stream()
                    .filter(b -> b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId())
                    .collect(Collectors.toList());

                String[] columns = {"Nom", "Adresse", "Réparateurs", "État"};
                DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) { return false; }
                };

                JTable boutiquesTable = new JTable(tableModel);
                boutiquesTable.setRowHeight(25);
                boutiquesTable.getTableHeader().setReorderingAllowed(false);

                for (dao.Boutique b : boutiques) {
                    // Compter les réparateurs assignés (approximation)
                    long nbReparateurs = gestionReparateur.lister().stream()
                        .filter(r -> r.getBoutique() != null && r.getBoutique().getIdBoutique() == b.getIdBoutique())
                        .count();

                    String etat = nbReparateurs > 0 ? "Active" : "Sans personnel";
                    Color etatColor = nbReparateurs > 0 ? new Color(46, 204, 113) : new Color(231, 76, 60);

                    tableModel.addRow(new Object[]{
                        b.getNom(),
                        b.getAdresse().length() > 20 ? b.getAdresse().substring(0, 20) + "..." : b.getAdresse(),
                        nbReparateurs + " réparateur(s)",
                        etat
                    });
                }

                JScrollPane tableScroll = new JScrollPane(boutiquesTable);
                tableScroll.setPreferredSize(new Dimension(300, 150));
                boutiquesOverviewPanel.add(tableScroll, BorderLayout.CENTER);

                // Boutons d'action rapide
                JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JButton btnVoirBoutiques = new JButton("Voir détails");
                JButton btnNouvelleBoutique = new JButton("➕ Nouvelle");

                btnVoirBoutiques.setBackground(new Color(52, 152, 219));
                btnNouvelleBoutique.setBackground(new Color(40, 167, 69));

                for (JButton btn : new JButton[]{btnVoirBoutiques, btnNouvelleBoutique}) {
                    btn.setForeground(Color.WHITE);
                    btn.setFocusPainted(false);
                    btn.setPreferredSize(new Dimension(100, 25));
                    quickActionsPanel.add(btn);
                }

                boutiquesOverviewPanel.add(quickActionsPanel, BorderLayout.SOUTH);

                // Listeners pour navigation rapide
                btnVoirBoutiques.addActionListener(e -> tabbedPane.setSelectedIndex(1)); // Onglet Boutiques
                btnNouvelleBoutique.addActionListener(e -> {
                    tabbedPane.setSelectedIndex(1); // Onglet Boutiques
                    // Sélectionner automatiquement le sous-onglet "Nouvelle Boutique"
                    JTabbedPane boutiquesTab = (JTabbedPane) ((JPanel) tabbedPane.getComponentAt(1)).getComponent(0);
                    boutiquesTab.setSelectedIndex(1);
                });

            }
        } catch (Exception e) {
            boutiquesOverviewPanel.add(new JLabel("Erreur lors du chargement des boutiques"));
        }

        // Panel des réparateurs
        JPanel reparateursOverviewPanel = new JPanel(new BorderLayout());
        reparateursOverviewPanel.setBorder(BorderFactory.createTitledBorder("👷 Aperçu des Réparateurs"));
        reparateursOverviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("👷 Mes Réparateurs"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        try {
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();

            String[] columns = {"Nom", "Prénom", "Boutique", "État"};
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            JTable reparateursTable = new JTable(tableModel);
            reparateursTable.setRowHeight(25);
            reparateursTable.getTableHeader().setReorderingAllowed(false);

            for (dao.Reparateur r : reparateurs) {
                String boutiqueName = r.getBoutique() != null ? r.getBoutique().getNom() : "Non assigné";
                String etat = r.getBoutique() != null ? "Assigné" : "Libre";
                Color etatColor = r.getBoutique() != null ? new Color(46, 204, 113) : new Color(255, 193, 7);

                tableModel.addRow(new Object[]{
                    r.getNom(),
                    r.getPrenom(),
                    boutiqueName.length() > 15 ? boutiqueName.substring(0, 15) + "..." : boutiqueName,
                    etat
                });
            }

            JScrollPane tableScroll = new JScrollPane(reparateursTable);
            tableScroll.setPreferredSize(new Dimension(300, 150));
            reparateursOverviewPanel.add(tableScroll, BorderLayout.CENTER);

            // Boutons d'action rapide
            JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnVoirReparateurs = new JButton("Voir détails");
            JButton btnNouveauReparateur = new JButton("➕ Nouveau");

            btnVoirReparateurs.setBackground(new Color(52, 152, 219));
            btnNouveauReparateur.setBackground(new Color(40, 167, 69));

            for (JButton btn : new JButton[]{btnVoirReparateurs, btnNouveauReparateur}) {
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(100, 25));
                quickActionsPanel.add(btn);
            }

            reparateursOverviewPanel.add(quickActionsPanel, BorderLayout.SOUTH);

            // Listeners pour navigation rapide
            btnVoirReparateurs.addActionListener(e -> tabbedPane.setSelectedIndex(2)); // Onglet Réparateurs
            btnNouveauReparateur.addActionListener(e -> {
                tabbedPane.setSelectedIndex(2); // Onglet Réparateurs
                // Sélectionner automatiquement le sous-onglet "Nouveau Réparateur"
                JTabbedPane reparateursTab = (JTabbedPane) ((JPanel) tabbedPane.getComponentAt(2)).getComponent(0);
                reparateursTab.setSelectedIndex(1);
            });

        } catch (Exception e) {
            reparateursOverviewPanel.add(new JLabel("Erreur lors du chargement des réparateurs"));
        }

        centerPanel.add(boutiquesOverviewPanel);
        centerPanel.add(reparateursOverviewPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel des dernières activités et mouvements de caisse
        JPanel activitiesPanel = new JPanel(new BorderLayout());
        activitiesPanel.setBorder(BorderFactory.createTitledBorder("📋 Dernières Activités & Paiements"));
        activitiesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("📋 Dernières Activités & Paiements"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        activitiesPanel.setPreferredSize(new Dimension(0, 250));

        try {
            JTextArea activitiesArea = new JTextArea();
            activitiesArea.setEditable(false);
            activitiesArea.setFont(new Font("Consolas", Font.PLAIN, 11));
            activitiesArea.setBackground(new Color(248, 249, 250));

            StringBuilder activities = new StringBuilder();
            activities.append("=== DERNIÈRES ACTIVITÉS ===\n\n");

            // Dernières réparations
            List<dao.Reparation> dernieresReparations = gestionReparation.lister().stream()
                .filter(r -> r.getDateDepot() != null) // Filtrer les réparations avec date
                .sorted((r1, r2) -> r2.getDateDepot().compareTo(r1.getDateDepot()))
                .limit(5)
                .collect(Collectors.toList());

            if (!dernieresReparations.isEmpty()) {
                activities.append("🔧 DERNIÈRES RÉPARATIONS:\n");
                for (dao.Reparation r : dernieresReparations) {
                    String commentaire = r.getCommentaire() != null ? r.getCommentaire() : "Sans description";
                    String descriptionCourte = commentaire.length() > 30 ? commentaire.substring(0, 30) + "..." : commentaire;
                    activities.append(String.format("• %s - %.2f€ (%s)\n",
                        descriptionCourte,
                        r.getPrixTotal(),
                        r.getDateDepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ));
                }
                activities.append("\n");
            }

            // Derniers mouvements de caisse
            activities.append("💰 DERNIERS MOUVEMENTS:\n");
            List<dao.Recu> derniersRecus = gestionRecu.lister().stream()
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .limit(3)
                .collect(Collectors.toList());

            for (dao.Recu r : derniersRecus) {
                activities.append(String.format("• Reçu: +%.2f€ (%s)\n",
                    r.getMontant(),
                    r.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ));
            }

            List<dao.Emprunt> derniersEmprunts = gestionEmprunt.lister().stream()
                .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                .limit(2)
                .collect(Collectors.toList());

            for (dao.Emprunt e : derniersEmprunts) {
                activities.append(String.format("• Emprunt: -%.2f€ (%s)\n",
                    e.getMontant(),
                    e.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ));
            }

            activitiesArea.setText(activities.toString());
            JScrollPane activitiesScroll = new JScrollPane(activitiesArea);
            activitiesPanel.add(activitiesScroll, BorderLayout.CENTER);

        } catch (Exception e) {
            activitiesPanel.add(new JLabel("Erreur lors du chargement des activités"));
        }

        mainPanel.add(activitiesPanel, BorderLayout.SOUTH);

        dashboardPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton de rafraîchissement
        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(150, 40));

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(refreshButton);
        dashboardPanel.add(bottomPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("📊 Dashboard", dashboardPanel);

        refreshButton.addActionListener(e -> {
            // Recréer le dashboard pour actualiser les données
            tabbedPane.remove(0);
            createDashboardTab();
            JOptionPane.showMessageDialog(this, "Données actualisées!");
        });
    }

    private void createFinancesTab() {
        JPanel financesPanel = new JPanel(new BorderLayout());

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirCaisses = new JButton("💰 Gérer Caisses");
        JButton btnVoirRecus = new JButton("🧾 Consulter Reçus");
        JButton btnVoirEmprunts = new JButton("💸 Gérer Emprunts");
        JButton btnRapportFinancier = new JButton("📊 Rapport Financier");

        btnVoirCaisses.setBackground(new Color(46, 204, 113));
        btnVoirRecus.setBackground(new Color(52, 152, 219));
        btnVoirEmprunts.setBackground(new Color(230, 126, 34));
        btnRapportFinancier.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnVoirCaisses, btnVoirRecus, btnVoirEmprunts, btnRapportFinancier}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        financesPanel.add(buttonPanel, BorderLayout.NORTH);

        // Zone d'affichage des informations financières
        JTextArea infoArea = new JTextArea(20, 60);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        infoArea.setText("Informations financières détaillées s'afficheront ici.");
        JScrollPane scrollPane = new JScrollPane(infoArea);
        financesPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("💰 Finances", financesPanel);

        // Listeners
        addFinanceListeners(btnVoirCaisses, btnVoirRecus, btnVoirEmprunts, btnRapportFinancier, infoArea);
    }

    private void createBoutiquesTab() {
        JPanel boutiquesPanel = new JPanel(new BorderLayout());
        boutiquesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel principal avec onglets pour différentes fonctionnalités
        JTabbedPane boutiqueTabbedPane = new JTabbedPane();

        // Onglet 1: Gestion des Boutiques
        boutiqueTabbedPane.addTab("🏪 Mes Boutiques", createMesBoutiquesPanel());

        // Onglet 2: Nouvelle Boutique
        boutiqueTabbedPane.addTab("➕ Nouvelle Boutique", createNouvelleBoutiquePanel());

        // Onglet 3: Assigner Réparateurs
        boutiqueTabbedPane.addTab("👷 Assigner Réparateurs", createAssignerReparateursPanel());

        boutiquesPanel.add(boutiqueTabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("🏪 Boutiques", boutiquesPanel);
    }

    private JPanel createMesBoutiquesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnRafraichir = new JButton("🔄 Rafraîchir");
        JButton btnModifier = new JButton("✏️ Modifier");
        JButton btnDetails = new JButton("📊 Détails");

        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnModifier.setBackground(new Color(255, 193, 7));
        btnDetails.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnRafraichir, btnModifier, btnDetails}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 35));
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table des boutiques
        String[] columns = {"ID Boutique", "Nom", "Adresse", "Téléphone", "Numéro P", "Réparateurs"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les boutiques du propriétaire
        rafraichirMesBoutiques(tableModel);

        // Listeners
        btnRafraichir.addActionListener(e -> rafraichirMesBoutiques(tableModel));

        btnModifier.addActionListener(e -> modifierMaBoutique(table, tableModel));

        btnDetails.addActionListener(e -> voirDetailsBoutique(table));

        return panel;
    }

    private JPanel createNouvelleBoutiquePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Créer une Nouvelle Boutique", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Champs du formulaire
        JTextField nomField = new JTextField(20);
        JTextField adresseField = new JTextField(20);
        JTextField telField = new JTextField(20);
        JTextField numPField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom de la boutique:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Adresse:"), gbc);
        gbc.gridx = 1;
        formPanel.add(adresseField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(telField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Numéro Postal:"), gbc);
        gbc.gridx = 1;
        formPanel.add(numPField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Bouton créer
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCreer = new JButton("🏪 Créer Boutique");
        btnCreer.setBackground(new Color(40, 167, 69));
        btnCreer.setForeground(Color.WHITE);
        btnCreer.setFocusPainted(false);
        btnCreer.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(btnCreer);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Listener
        btnCreer.addActionListener(e -> creerNouvelleBoutique(nomField, adresseField, telField, numPField));

        return panel;
    }

    private JPanel createAssignerReparateursPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Assigner des Réparateurs à mes Boutiques", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // 1. Sélection Boutique-Réparateur
        JPanel assignPanel = new JPanel(new BorderLayout());
        assignPanel.setBorder(BorderFactory.createTitledBorder("Assigner un Réparateur"));

        JPanel controlsPanel = new JPanel(new FlowLayout());

        JComboBox<String> boutiqueCombo = new JComboBox<>();
        JComboBox<String> reparateurCombo = new JComboBox<>();
        JButton btnAssigner = new JButton("👷 Assigner");

        controlsPanel.add(new JLabel("Ma Boutique:"));
        controlsPanel.add(boutiqueCombo);
        controlsPanel.add(new JLabel("Réparateur:"));
        controlsPanel.add(reparateurCombo);
        controlsPanel.add(btnAssigner);

        assignPanel.add(controlsPanel, BorderLayout.CENTER);

        // 2. Liste des assignations
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Réparateurs assignés"));

        String[] columns = {"ID Réparateur", "Nom", "Prénom", "Email", "Boutique"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRafraichir = new JButton("🔄 Rafraîchir");
        JButton btnDissocier = new JButton("🔗 Dissocier");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnRafraichir);
        buttonPanel.add(btnDissocier);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(assignPanel);
        mainPanel.add(listPanel);
        panel.add(mainPanel, BorderLayout.CENTER);

        // Les données seront chargées quand l'utilisateur sera connecté

        // Listeners
        btnAssigner.addActionListener(e -> assignerReparateurABoutique(reparateurCombo, boutiqueCombo, tableModel));
        btnRafraichir.addActionListener(e -> rafraichirAssignationsReparateurs(tableModel));
        btnDissocier.addActionListener(e -> dissocierReparateur(table, tableModel));

        return panel;
    }

    private void rafraichirMesBoutiques(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire != null) {
                List<dao.Boutique> boutiques = gestionBoutique.lister();
                for (dao.Boutique b : boutiques) {
                    if (b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId()) {
                        // Compter les réparateurs assignés (approximation)
                        int nbReparateurs = 0; // TODO: implémenter le comptage
                        tableModel.addRow(new Object[]{
                            b.getIdBoutique(),
                            b.getNom(),
                            b.getAdresse(),
                            b.getNumTel(),
                            b.getNumP(),
                            nbReparateurs + " réparateur(s)"
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des boutiques: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouvelleBoutique(JTextField nomField, JTextField adresseField,
                                      JTextField telField, JTextField numPField) {
        // Validation des champs
        if (nomField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty() ||
            telField.getText().trim().isEmpty() || numPField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire == null) {
                JOptionPane.showMessageDialog(this,
                    "Propriétaire non trouvé. Veuillez vous reconnecter.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer la boutique
            dao.Boutique boutique = dao.Boutique.builder()
                .nom(nomField.getText().trim())
                .adresse(adresseField.getText().trim())
                .numTel(Integer.parseInt(telField.getText().trim()))
                .numP(Integer.parseInt(numPField.getText().trim()))
                .proprietaire(proprietaire)
                .build();

            gestionBoutique.ajouter(boutique);

            // Vérifier que la boutique a été créée avec le bon propriétaire
            dao.Boutique boutiqueVerifiee = gestionBoutique.rechercher(boutique.getIdBoutique());
            if (boutiqueVerifiee != null && boutiqueVerifiee.getProprietaire() != null) {
                System.out.println("Débogage - Boutique créée avec propriétaire ID: " + boutiqueVerifiee.getProprietaire().getId());
            } else {
                System.out.println("Débogage - ERREUR: Boutique créée sans propriétaire ou non trouvée");
            }

            // Vérifier si une caisse existe déjà pour cette boutique et en créer une si nécessaire
            try {
                if (!gestionCaisse.caisseExistePourBoutique(boutique.getIdBoutique())) {
                    // Créer automatiquement une caisse pour cette boutique
                    dao.Caisse caisse = dao.Caisse.builder()
                        .soldeActuel(0.0)
                        .dernierMouvement(java.time.LocalDateTime.now())
                        .boutique(boutique)
                        .build();

                    gestionCaisse.ajouter(caisse);
                    System.out.println("Débogage - Caisse créée pour la boutique " + boutique.getNom());
                } else {
                    System.out.println("Débogage - Une caisse existe déjà pour la boutique " + boutique.getNom());
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification/création de la caisse: " + e.getMessage());
                // Essayer quand même de créer la caisse en cas d'erreur de vérification
                try {
                    dao.Caisse caisse = dao.Caisse.builder()
                        .soldeActuel(0.0)
                        .dernierMouvement(java.time.LocalDateTime.now())
                        .boutique(boutique)
                        .build();
                    gestionCaisse.ajouter(caisse);
                    System.out.println("Débogage - Caisse créée (fallback) pour la boutique " + boutique.getNom());
                } catch (Exception e2) {
                    System.err.println("Erreur lors de la création de la caisse (fallback): " + e2.getMessage());
                }
            }

            JOptionPane.showMessageDialog(this,
                "Boutique créée avec succès !\nUne caisse a été automatiquement créée avec un solde initial de 0€.",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Vider les champs
            nomField.setText("");
            adresseField.setText("");
            telField.setText("");
            numPField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le téléphone et le numéro postal doivent être des nombres.",
                "Format invalide", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierMaBoutique(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une boutique à modifier.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idBoutique = (Integer) table.getValueAt(selectedRow, 0);
            dao.Boutique boutique = gestionBoutique.rechercher(idBoutique);

            if (boutique == null) {
                JOptionPane.showMessageDialog(this, "Boutique introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ouvrir dialog de modification
            JTextField nomField = new JTextField(boutique.getNom());
            JTextField adresseField = new JTextField(boutique.getAdresse());
            JTextField telField = new JTextField(String.valueOf(boutique.getNumTel()));
            JTextField numPField = new JTextField(String.valueOf(boutique.getNumP()));

            Object[] message = {
                "Nom:", nomField,
                "Adresse:", adresseField,
                "Téléphone:", telField,
                "Numéro Postal:", numPField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Boutique",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                boutique.setNom(nomField.getText());
                boutique.setAdresse(adresseField.getText());
                boutique.setNumTel(Integer.parseInt(telField.getText()));
                boutique.setNumP(Integer.parseInt(numPField.getText()));

                gestionBoutique.modifer(boutique);
                rafraichirMesBoutiques(tableModel);

                JOptionPane.showMessageDialog(this, "Boutique modifiée avec succès !");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voirDetailsBoutique(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une boutique.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idBoutique = (Integer) table.getValueAt(selectedRow, 0);
            dao.Boutique boutique = gestionBoutique.rechercher(idBoutique);

            if (boutique != null) {
                String details = String.format(
                    "Détails de la Boutique:\n\n" +
                    "ID: %d\n" +
                    "Nom: %s\n" +
                    "Adresse: %s\n" +
                    "Téléphone: %d\n" +
                    "Numéro Postal: %d",
                    boutique.getIdBoutique(),
                    boutique.getNom(),
                    boutique.getAdresse(),
                    boutique.getNumTel(),
                    boutique.getNumP()
                );

                JOptionPane.showMessageDialog(this, details, "Détails Boutique",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des détails: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerMesBoutiques(JComboBox<String> combo) {
        try {
            combo.removeAllItems();
            combo.addItem("-- Sélectionner ma Boutique --");

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire != null) {
                List<dao.Boutique> boutiques = gestionBoutique.lister();
                for (dao.Boutique b : boutiques) {
                    if (b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId()) {
                        combo.addItem(b.getIdBoutique() + " - " + b.getNom());
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des boutiques: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerReparateursDisponibles(JComboBox<String> combo) {
        try {
            combo.removeAllItems();
            combo.addItem("-- Sélectionner Réparateur --");

            // Charger tous les réparateurs (le propriétaire peut assigner n'importe quel réparateur)
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                String status = r.getBoutique() != null ? " (assigné)" : " (libre)";
                combo.addItem(r.getId() + " - " + r.getNom() + " " + r.getPrenom() + status);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réparateurs: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void dissocierReparateur(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur à dissocier.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            "Fonctionnalité de dissociation - À implémenter",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createReparateursTab() {
        JPanel reparateursPanel = new JPanel(new BorderLayout());
        reparateursPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel principal avec onglets pour différentes fonctionnalités
        JTabbedPane reparateurTabbedPane = new JTabbedPane();

        // Onglet 1: Gestion des Réparateurs
        reparateurTabbedPane.addTab("👷 Mes Réparateurs", createMesReparateursPanel());

        // Onglet 2: Nouveau Réparateur
        reparateurTabbedPane.addTab("➕ Nouveau Réparateur", createNouveauReparateurPanel());

        // Onglet 3: Assigner à Boutique
        reparateurTabbedPane.addTab("🏪 Assigner Réparateur", createAssignerReparateurPanel());

        reparateursPanel.add(reparateurTabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("👷 Réparateurs", reparateursPanel);
    }

    private JPanel createMesReparateursPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnRafraichir = new JButton("🔄 Rafraîchir");
        JButton btnModifier = new JButton("✏️ Modifier");
        JButton btnSupprimer = new JButton("🗑️ Supprimer");
        JButton btnDetails = new JButton("📊 Détails");

        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnModifier.setBackground(new Color(255, 193, 7));
        btnSupprimer.setBackground(new Color(220, 53, 69));
        btnDetails.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnRafraichir, btnModifier, btnSupprimer, btnDetails}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(120, 35));
            buttonPanel.add(btn);
        }

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table des réparateurs
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Pourcentage Gain", "Boutique Assignée"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les réparateurs du propriétaire
        rafraichirMesReparateurs(tableModel);

        // Listeners
        btnRafraichir.addActionListener(e -> rafraichirMesReparateurs(tableModel));
        btnModifier.addActionListener(e -> modifierMonReparateur(table, tableModel));
        btnSupprimer.addActionListener(e -> supprimerMonReparateur(table, tableModel));
        btnDetails.addActionListener(e -> voirDetailsReparateur(table));

        return panel;
    }

    private JPanel createNouveauReparateurPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Créer un Nouveau Réparateur", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Champs du formulaire
        JTextField nomField = new JTextField(20);
        JTextField prenomField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField mdpField = new JPasswordField(20);
        JTextField pourcentageField = new JTextField("10.0");

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        formPanel.add(mdpField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Pourcentage Gain (%):"), gbc);
        gbc.gridx = 1;
        formPanel.add(pourcentageField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Bouton créer
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnCreer = new JButton("👷 Créer Réparateur");
        btnCreer.setBackground(new Color(40, 167, 69));
        btnCreer.setForeground(Color.WHITE);
        btnCreer.setFocusPainted(false);
        btnCreer.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(btnCreer);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Listener
        btnCreer.addActionListener(e -> creerNouveauReparateur(nomField, prenomField, emailField, mdpField, pourcentageField));

        return panel;
    }

    private JPanel createAssignerReparateurPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Assigner un Réparateur à une Boutique", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // 1. Sélection Réparateur-Boutique
        JPanel assignPanel = new JPanel(new BorderLayout());
        assignPanel.setBorder(BorderFactory.createTitledBorder("Assigner un Réparateur"));

        JPanel controlsPanel = new JPanel(new FlowLayout());

        JComboBox<String> reparateurCombo = new JComboBox<>();
        JComboBox<String> boutiqueCombo = new JComboBox<>();
        JButton btnAssigner = new JButton("👷 Assigner à Boutique");

        JButton btnRafraichirCombos = new JButton("🔄");
        btnRafraichirCombos.setToolTipText("Rafraîchir les listes");

        controlsPanel.add(new JLabel("Réparateur:"));
        controlsPanel.add(reparateurCombo);
        controlsPanel.add(new JLabel("Ma Boutique:"));
        controlsPanel.add(boutiqueCombo);
        controlsPanel.add(btnRafraichirCombos);
        controlsPanel.add(btnAssigner);

        // Listener pour rafraîchir les combos
        btnRafraichirCombos.addActionListener(e -> {
            chargerMesReparateurs(reparateurCombo);
            chargerMesBoutiquesAssigner(boutiqueCombo);
        });

        assignPanel.add(controlsPanel, BorderLayout.CENTER);

        // 2. Liste des assignations actuelles
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Réparateurs assignés"));

        String[] columns = {"ID Réparateur", "Nom Réparateur", "Boutique Assignée", "Nom Boutique"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnRafraichir = new JButton("🔄 Rafraîchir");
        JButton btnDessassigner = new JButton("🔗 Dessassigner");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnRafraichir);
        buttonPanel.add(btnDessassigner);
        listPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(assignPanel);
        mainPanel.add(listPanel);
        panel.add(mainPanel, BorderLayout.CENTER);

        // Les données seront chargées quand l'utilisateur sera connecté
        // Pour l'instant, juste initialiser les combos vides

        // Listeners
        btnAssigner.addActionListener(e -> assignerReparateurABoutique(reparateurCombo, boutiqueCombo, tableModel));
        btnRafraichir.addActionListener(e -> rafraichirAssignationsReparateurs(tableModel));
        btnDessassigner.addActionListener(e -> dessassignerReparateur(table, tableModel));

        return panel;
    }

    private void rafraichirMesReparateurs(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Afficher tous les réparateurs avec leur assignation
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                String boutiqueAssignee = "Non assigné";
                if (r.getBoutique() != null) {
                    boutiqueAssignee = r.getBoutique().getNom();
                }
                tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getNom(),
                    r.getPrenom(),
                    r.getEmail(),
                    r.getPourcentageGain(),
                    boutiqueAssignee
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réparateurs: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouveauReparateur(JTextField nomField, JTextField prenomField, JTextField emailField,
                                       JPasswordField mdpField, JTextField pourcentageField) {
        // Validation des champs
        if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() || new String(mdpField.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double pourcentageGain = Double.parseDouble(pourcentageField.getText().trim());
            if (pourcentageGain < 0 || pourcentageGain > 100) {
                JOptionPane.showMessageDialog(this, "Le pourcentage doit être entre 0 et 100.",
                    "Valeur invalide", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Créer le réparateur
            dao.Reparateur reparateur = dao.Reparateur.builder()
                .nom(nomField.getText().trim())
                .prenom(prenomField.getText().trim())
                .email(emailField.getText().trim())
                .mdp(new String(mdpField.getPassword()))
                .pourcentageGain(pourcentageGain)
                .build();

            gestionReparateur.ajouter(reparateur);

            JOptionPane.showMessageDialog(this, "Réparateur créé avec succès !");

            // Vider les champs
            nomField.setText("");
            prenomField.setText("");
            emailField.setText("");
            mdpField.setText("");
            pourcentageField.setText("10.0");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le pourcentage doit être un nombre valide.",
                "Format invalide", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierMonReparateur(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur à modifier.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparateur = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparateur reparateur = gestionReparateur.rechercher(idReparateur);

            if (reparateur == null) {
                JOptionPane.showMessageDialog(this, "Réparateur introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Formulaire de modification
            JTextField nomField = new JTextField(reparateur.getNom());
            JTextField prenomField = new JTextField(reparateur.getPrenom());
            JTextField emailField = new JTextField(reparateur.getEmail());
            JPasswordField mdpField = new JPasswordField();
            mdpField.setText(reparateur.getMdp());
            JTextField pourcentageField = new JTextField(String.valueOf(reparateur.getPourcentageGain()));

            Object[] message = {
                "Nom:", nomField,
                "Prénom:", prenomField,
                "Email:", emailField,
                "Nouveau mot de passe:", mdpField,
                "Pourcentage Gain:", pourcentageField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Réparateur",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                reparateur.setNom(nomField.getText());
                reparateur.setPrenom(prenomField.getText());
                reparateur.setEmail(emailField.getText());
                reparateur.setMdp(new String(mdpField.getPassword()));
                reparateur.setPourcentageGain(Double.parseDouble(pourcentageField.getText()));

                gestionReparateur.modifer(reparateur);
                rafraichirMesReparateurs(tableModel);

                JOptionPane.showMessageDialog(this, "Réparateur modifié avec succès !");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerMonReparateur(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur à supprimer.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer ce réparateur ?\nCette action est irréversible.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (option != JOptionPane.YES_OPTION) return;

        try {
            int idReparateur = (Integer) table.getValueAt(selectedRow, 0);
            gestionReparateur.supprimer(idReparateur);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Réparateur supprimé avec succès!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voirDetailsReparateur(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparateur = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparateur reparateur = gestionReparateur.rechercher(idReparateur);

            if (reparateur != null) {
                String details = String.format(
                    "Détails du Réparateur:\n\n" +
                    "ID: %d\n" +
                    "Nom: %s\n" +
                    "Prénom: %s\n" +
                    "Email: %s\n" +
                    "Pourcentage Gain: %.1f%%\n" +
                    "Boutique Assignée: %s",
                    reparateur.getId(),
                    reparateur.getNom(),
                    reparateur.getPrenom(),
                    reparateur.getEmail(),
                    reparateur.getPourcentageGain(),
                    "Non assigné" // TODO: récupérer la vraie assignation
                );

                JOptionPane.showMessageDialog(this, details, "Détails Réparateur",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des détails: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerMesReparateurs(JComboBox<String> combo) {
        try {
            combo.removeAllItems();
            combo.addItem("-- Sélectionner Réparateur --");

            // Charger tous les réparateurs (le propriétaire peut assigner n'importe quel réparateur)
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                String status = r.getBoutique() != null ? " (assigné)" : " (libre)";
                combo.addItem(r.getId() + " - " + r.getNom() + " " + r.getPrenom() + status);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réparateurs: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerMesBoutiquesAssigner(JComboBox<String> combo) {
        try {
            combo.removeAllItems();
            combo.addItem("-- Sélectionner Boutique --");

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire == null) {
                // Ne pas afficher d'erreur pendant l'initialisation - l'utilisateur n'est pas encore connecté
                return;
            }

            List<dao.Boutique> boutiques = gestionBoutique.lister();
            int count = 0;

            for (dao.Boutique b : boutiques) {
                if (b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId()) {
                    combo.addItem(b.getIdBoutique() + " - " + b.getNom());
                    count++;
                }
            }

            if (count == 0) {
                combo.addItem("Aucune boutique disponible");
            }

        } catch (Exception e) {
            // En cas d'erreur lors de l'initialisation, ne pas afficher de popup
            combo.addItem("Erreur de chargement");
        }
    }

    private void assignerReparateurABoutique(JComboBox<String> reparateurCombo, JComboBox<String> boutiqueCombo,
                                           DefaultTableModel tableModel) {
        if (reparateurCombo.getSelectedIndex() == 0 || boutiqueCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur et une boutique.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Récupérer les IDs sélectionnés
            String selectedReparateur = (String) reparateurCombo.getSelectedItem();
            String selectedBoutique = (String) boutiqueCombo.getSelectedItem();

            int idReparateur = Integer.parseInt(selectedReparateur.split(" - ")[0]);
            int idBoutique = Integer.parseInt(selectedBoutique.split(" - ")[0]);

            // Récupérer le réparateur et la boutique
            dao.Reparateur reparateur = gestionReparateur.rechercher(idReparateur);
            dao.Boutique boutique = gestionBoutique.rechercher(idBoutique);

            if (reparateur == null || boutique == null) {
                JOptionPane.showMessageDialog(this, "Réparateur ou boutique introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier que la boutique appartient bien au propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire == null || boutique.getProprietaire() == null ||
                boutique.getProprietaire().getId() != proprietaire.getId()) {
                JOptionPane.showMessageDialog(this, "Vous ne pouvez assigner des réparateurs qu'à vos propres boutiques.",
                    "Permission refusée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Assigner la boutique au réparateur
            reparateur.setBoutique(boutique);
            gestionReparateur.modifer(reparateur);

            JOptionPane.showMessageDialog(this,
                "Réparateur assigné avec succès !\n" +
                reparateur.getNom() + " " + reparateur.getPrenom() + " travaille maintenant dans " + boutique.getNom(),
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            rafraichirAssignationsReparateurs(tableModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'assignation: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirAssignationsReparateurs(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Récupérer le propriétaire connecté pour filtrer les réparateurs de ses boutiques
            dao.Proprietaire proprietaire = null;
            List<dao.Proprietaire> proprietaires = gestionProprietaire.lister();
            for (dao.Proprietaire p : proprietaires) {
                if (p.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    proprietaire = p;
                    break;
                }
            }

            if (proprietaire != null) {
                // Afficher tous les réparateurs avec leur assignation
                List<dao.Reparateur> reparateurs = gestionReparateur.lister();
                for (dao.Reparateur r : reparateurs) {
                    String nomBoutique = "Non assigné";
                    if (r.getBoutique() != null) {
                        nomBoutique = r.getBoutique().getNom();
                    }
                    tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getNom() + " " + r.getPrenom(),
                        r.getBoutique() != null ? r.getBoutique().getIdBoutique() : "N/A",
                        nomBoutique
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dessassignerReparateur(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un réparateur à dessassigner.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparateur = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparateur reparateur = gestionReparateur.rechercher(idReparateur);

            if (reparateur == null) {
                JOptionPane.showMessageDialog(this, "Réparateur introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int option = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir dessassigner " + reparateur.getNom() + " " + reparateur.getPrenom() + " ?\n" +
                "Il ne pourra plus créer de réparations tant qu'il ne sera pas réassigné à une boutique.",
                "Confirmation de dessassignation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                reparateur.setBoutique(null);
                gestionReparateur.modifer(reparateur);

                JOptionPane.showMessageDialog(this, "Réparateur dessassigné avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

                rafraichirAssignationsReparateurs(tableModel);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la dessassignation: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCRUDTab() {
        JPanel crudPanel = new JPanel(new BorderLayout());
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Onglets pour les différentes entités accessibles au propriétaire
        JTabbedPane crudTabbedPane = new JTabbedPane();

        // Onglet Boutiques (CRUD complet)
        crudTabbedPane.addTab("🏪 Boutiques", createProprietaireCrudPanel("Boutique"));

        // Onglets en lecture seule
        crudTabbedPane.addTab("👷 Réparateurs", createReadOnlyPanelProprietaire("Reparateur"));
        crudTabbedPane.addTab("💰 Caisses", createReadOnlyPanelProprietaire("Caisse"));
        crudTabbedPane.addTab("🧾 Reçus", createReadOnlyPanelProprietaire("Recu"));
        crudTabbedPane.addTab("💸 Emprunts", createReadOnlyPanelProprietaire("Emprunt"));

        crudPanel.add(crudTabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("⚙️ Gestion", crudPanel);
    }

    private JPanel createProprietaireCrudPanel(String entityName) {
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
        String[] columns = {"ID Boutique", "Nom", "Adresse", "Téléphone", "Numéro P", "Propriétaire"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les données initiales
        try {
            loadProprietaireBoutiques(tableModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Ajouter les listeners aux boutons
        addProprietaireCrudListeners(btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnLister, table, tableModel);

        return panel;
    }

    private JPanel createReadOnlyPanelProprietaire(String entityName) {
        JPanel panel = new JPanel(new BorderLayout());

        // Bouton de rafraîchissement
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRafraichir = new JButton("🔄 Rafraîchir");
        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setFocusPainted(false);
        btnRafraichir.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(btnRafraichir);
        panel.add(buttonPanel, BorderLayout.NORTH);

        // Table pour afficher les données (lecture seule)
        String[] columns;
        switch (entityName) {
            case "Reparateur":
                columns = new String[]{"ID", "Nom", "Prénom", "Email", "Pourcentage Gain"};
                break;
            case "Caisse":
                columns = new String[]{"ID Caisse", "ID Boutique", "Solde Actuel", "Dernier Mouvement"};
                break;
            case "Recu":
                columns = new String[]{"ID Reçu", "Date", "Montant"};
                break;
            case "Emprunt":
                columns = new String[]{"ID Emprunt", "Date", "Montant", "Type", "Commentaire"};
                break;
            default:
                columns = new String[]{"Données"};
        }

        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Lecture seule
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les données initiales
        try {
            loadReadOnlyDataProprietaire(tableModel, entityName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Listener pour rafraîchir
        btnRafraichir.addActionListener(e -> {
            try {
                tableModel.setRowCount(0);
                loadReadOnlyDataProprietaire(tableModel, entityName);
                JOptionPane.showMessageDialog(this, "Données mises à jour!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du rafraîchissement: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void loadProprietaireBoutiques(DefaultTableModel model) throws Exception {
        // Récupérer le propriétaire connecté
        dao.Proprietaire proprietaireConnecte = getProprietaireConnecte();

        if (proprietaireConnecte != null) {
            List<dao.Boutique> boutiques = gestionBoutique.lister();
            for (dao.Boutique b : boutiques) {
                if (b.getProprietaire() != null && b.getProprietaire().getId() == proprietaireConnecte.getId()) {
                    model.addRow(new Object[]{
                        b.getIdBoutique(),
                        b.getNom(),
                        b.getAdresse(),
                        b.getNumTel(),
                        b.getNumP(),
                        proprietaireConnecte.getNom() + " " + proprietaireConnecte.getPrenom()
                    });
                }
            }
        }
    }

    private void loadReadOnlyDataProprietaire(DefaultTableModel model, String entityName) throws Exception {
        // Récupérer le propriétaire connecté pour filtrer les données
        final dao.Proprietaire proprietaireConnecte = getProprietaireConnecte();

        if (proprietaireConnecte == null) return;

        final int proprietaireId = proprietaireConnecte.getId();

        switch (entityName) {
            case "Reparateur":
                // Afficher tous les réparateurs (pas de filtrage propriétaire)
                List<dao.Reparateur> reparateurs = gestionReparateur.lister();
                for (dao.Reparateur r : reparateurs) {
                    model.addRow(new Object[]{
                        r.getId(),
                        r.getNom(),
                        r.getPrenom(),
                        r.getEmail(),
                        r.getPourcentageGain()
                    });
                }
                break;
            case "Caisse":
                // Afficher les caisses des boutiques du propriétaire
                List<dao.Caisse> caisses = gestionCaisse.lister();
                List<dao.Boutique> mesBoutiques = gestionBoutique.lister().stream()
                    .filter(b -> b.getProprietaire() != null && b.getProprietaire().getId() == proprietaireId)
                    .collect(Collectors.toList());

                for (dao.Caisse c : caisses) {
                    final int boutiqueId = c.getBoutique().getIdBoutique();
                    boolean isMyBoutique = mesBoutiques.stream()
                        .anyMatch(b -> b.getIdBoutique() == boutiqueId);
                    if (isMyBoutique) {
                        model.addRow(new Object[]{
                            c.getIdCaisse(),
                            c.getBoutique().getIdBoutique(),
                            c.getSoldeActuel(),
                            c.getDernierMouvement() != null ? c.getDernierMouvement().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A"
                        });
                    }
                }
                break;
            case "Recu":
                // Afficher tous les reçus (transactions globales)
                List<dao.Recu> recus = gestionRecu.lister();
                for (dao.Recu r : recus) {
                    model.addRow(new Object[]{
                        r.getIdRecu(),
                        r.getDate() != null ? r.getDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                        r.getMontant()
                    });
                }
                break;
            case "Emprunt":
                // Afficher tous les emprunts (transactions globales)
                List<dao.Emprunt> emprunts = gestionEmprunt.lister();
                for (dao.Emprunt e : emprunts) {
                    model.addRow(new Object[]{
                        e.getIdEmprunt(),
                        e.getDate() != null ? e.getDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                        e.getMontant(),
                        e.getType(),
                        e.getCommentaire() != null && e.getCommentaire().length() > 30
                            ? e.getCommentaire().substring(0, 30) + "..." : e.getCommentaire()
                    });
                }
                break;
        }
    }

    private void addProprietaireCrudListeners(JButton btnAjouter, JButton btnModifier, JButton btnSupprimer,
                                            JButton btnRechercher, JButton btnLister, JTable table, DefaultTableModel tableModel) {

        btnLister.addActionListener(e -> {
            try {
                tableModel.setRowCount(0);
                loadProprietaireBoutiques(tableModel);
                JOptionPane.showMessageDialog(this, "Liste mise à jour avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du listage: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAjouter.addActionListener(e -> ajouterBoutiqueProprietaire(tableModel));
        btnModifier.addActionListener(e -> modifierBoutiqueProprietaire(table, tableModel));
        btnSupprimer.addActionListener(e -> supprimerBoutiqueProprietaire(table, tableModel));
        btnRechercher.addActionListener(e -> rechercherBoutiqueProprietaire(tableModel));
    }

    private void ajouterBoutiqueProprietaire(DefaultTableModel tableModel) {
        // Utiliser la méthode existante creerNouvelleBoutique mais adaptée
        creerNouvelleBoutiqueFromCRUD(tableModel);
    }

    private void modifierBoutiqueProprietaire(JTable table, DefaultTableModel tableModel) {
        modifierMaBoutique(table, tableModel);
    }

    private void supprimerBoutiqueProprietaire(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une boutique à supprimer",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cette boutique ?\n" +
            "Attention: Cette action est irréversible et supprimera également toutes les données associées.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (option != JOptionPane.YES_OPTION) return;

        try {
            int idBoutique = (Integer) table.getValueAt(selectedRow, 0);
            gestionBoutique.supprimer(idBoutique);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Boutique supprimée avec succès!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechercherBoutiqueProprietaire(DefaultTableModel tableModel) {
        String nomStr = JOptionPane.showInputDialog(this, "Entrez le nom de la boutique à rechercher:",
            "Recherche", JOptionPane.QUESTION_MESSAGE);

        if (nomStr == null || nomStr.trim().isEmpty()) return;

        try {
            tableModel.setRowCount(0);

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaireConnecte = null;
            List<dao.Proprietaire> proprietaires = gestionProprietaire.lister();
            for (dao.Proprietaire p : proprietaires) {
                if (p.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    proprietaireConnecte = p;
                    break;
                }
            }

            if (proprietaireConnecte != null) {
                List<dao.Boutique> boutiques = gestionBoutique.lister();
                for (dao.Boutique b : boutiques) {
                    if (b.getProprietaire() != null && b.getProprietaire().getId() == proprietaireConnecte.getId() &&
                        b.getNom().toLowerCase().contains(nomStr.toLowerCase())) {
                        tableModel.addRow(new Object[]{
                            b.getIdBoutique(),
                            b.getNom(),
                            b.getAdresse(),
                            b.getNumTel(),
                            b.getNumP(),
                            proprietaireConnecte.getNom() + " " + proprietaireConnecte.getPrenom()
                        });
                    }
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Aucune boutique trouvée avec ce nom.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouvelleBoutiqueFromCRUD(DefaultTableModel tableModel) {
        JTextField nomField = new JTextField();
        JTextField adresseField = new JTextField();
        JTextField telField = new JTextField();
        JTextField numPField = new JTextField();

        Object[] message = {
            "Nom de la boutique:", nomField,
            "Adresse:", adresseField,
            "Téléphone:", telField,
            "Numéro Postal:", numPField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter une Boutique",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validation des champs
                if (nomField.getText().trim().isEmpty() || adresseField.getText().trim().isEmpty() ||
                    telField.getText().trim().isEmpty() || numPField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Récupérer le propriétaire connecté
                dao.Proprietaire proprietaire = null;
                List<dao.Proprietaire> proprietaires = gestionProprietaire.lister();
                for (dao.Proprietaire p : proprietaires) {
                    if (p.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                        proprietaire = p;
                        break;
                    }
                }

                if (proprietaire == null) {
                    JOptionPane.showMessageDialog(this, "Propriétaire non trouvé.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Créer la boutique
                dao.Boutique boutique = dao.Boutique.builder()
                    .nom(nomField.getText().trim())
                    .adresse(adresseField.getText().trim())
                    .numTel(Integer.parseInt(telField.getText().trim()))
                    .numP(Integer.parseInt(numPField.getText().trim()))
                    .proprietaire(proprietaire)
                    .build();

                gestionBoutique.ajouter(boutique);

                // Créer automatiquement une caisse pour cette boutique
                dao.Caisse caisse = dao.Caisse.builder()
                    .soldeActuel(0.0)
                    .dernierMouvement(java.time.LocalDateTime.now())
                    .boutique(boutique)
                    .build();

                gestionCaisse.ajouter(caisse);

                // Rafraîchir la table
                tableModel.setRowCount(0);
                loadProprietaireBoutiques(tableModel);

                JOptionPane.showMessageDialog(this,
                    "Boutique ajoutée avec succès!\nUne caisse a été automatiquement créée avec un solde initial de 0€.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Le téléphone et le numéro postal doivent être des nombres.",
                    "Format invalide", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createRapportsTab() {
        JPanel rapportsPanel = new JPanel(new BorderLayout());
        rapportsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Rapports et Analyses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(155, 89, 182));
        rapportsPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel des boutons de rapports
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton btnRapportVentes = new JButton("📈 Rapport des Ventes");
        JButton btnRapportBenefices = new JButton("💰 Rapport des Bénéfices");
        JButton btnRapportClients = new JButton("👥 Rapport Clients");
        JButton btnRapportReparations = new JButton("🔧 Rapport Réparations");
        JButton btnRapportStock = new JButton("📦 Rapport Stock");
        JButton btnRapportGeneral = new JButton("📊 Rapport Général");

        JButton[] buttons = {btnRapportVentes, btnRapportBenefices, btnRapportClients,
                           btnRapportReparations, btnRapportStock, btnRapportGeneral};

        Color[] colors = {new Color(46, 204, 113), new Color(52, 152, 219), new Color(155, 89, 182),
                         new Color(230, 126, 34), new Color(231, 76, 60), new Color(44, 62, 80)};

        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setBackground(colors[i]);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(180, 40));
            buttonPanel.add(btn);
        }

        rapportsPanel.add(buttonPanel, BorderLayout.CENTER);

        tabbedPane.addTab("📊 Rapports", rapportsPanel);

        // Ajouter le même listener à tous les boutons
        ActionListener rapportListener = e -> {
            JButton source = (JButton) e.getSource();
            JOptionPane.showMessageDialog(this,
                "Génération du rapport '" + source.getText() + "' - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        };

        for (JButton btn : buttons) {
            btn.addActionListener(rapportListener);
        }
    }

    private void createProfileTab() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Informations du profil
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations du Profil"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(mainWindow.getCurrentUserEmail() != null ? mainWindow.getCurrentUserEmail() : "Non défini"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel("Propriétaire"), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel("Actif"), gbc);

        profilePanel.add(infoPanel, BorderLayout.NORTH);

        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton btnModifierProfil = new JButton("✏️ Modifier Profil");
        JButton btnVoirStatistiques = new JButton("📊 Mes Statistiques");

        btnModifierProfil.setBackground(new Color(255, 193, 7));
        btnVoirStatistiques.setBackground(new Color(52, 152, 219));

        for (JButton btn : new JButton[]{btnModifierProfil, btnVoirStatistiques}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            actionPanel.add(btn);
        }

        profilePanel.add(actionPanel, BorderLayout.CENTER);

        tabbedPane.addTab("👤 Profil", profilePanel);

        // Listeners
        btnModifierProfil.addActionListener(e -> modifierProfil());

        btnVoirStatistiques.addActionListener(e -> voirStatistiques());
    }

    private void modifierProfil() {
        try {
            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();

            if (proprietaire == null) {
                JOptionPane.showMessageDialog(this,
                    "Propriétaire non trouvé. Veuillez vous reconnecter.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Formulaire de modification
            JTextField nomField = new JTextField(proprietaire.getNom());
            JTextField prenomField = new JTextField(proprietaire.getPrenom());
            JTextField emailField = new JTextField(proprietaire.getEmail());
            JPasswordField mdpField = new JPasswordField();
            mdpField.setText(proprietaire.getMdp());

            Object[] message = {
                "Nom:", nomField,
                "Prénom:", prenomField,
                "Email:", emailField,
                "Nouveau mot de passe:", mdpField
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                "Modifier le profil",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                // Validation des champs
                if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Mettre à jour le propriétaire
                proprietaire.setNom(nomField.getText().trim());
                proprietaire.setPrenom(prenomField.getText().trim());
                proprietaire.setEmail(emailField.getText().trim());

                String newPassword = new String(mdpField.getPassword());
                if (!newPassword.isEmpty()) {
                    proprietaire.setMdp(newPassword);
                }

                gestionProprietaire.modifer(proprietaire);

                // Mettre à jour l'email dans la session si changé
                mainWindow.setCurrentUser(mainWindow.getCurrentUserRole(), proprietaire.getEmail());
                updateWelcomeLabel();

                JOptionPane.showMessageDialog(this, "Profil modifié avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la modification du profil: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voirStatistiques() {
        try {
            // Récupérer le propriétaire connecté
            final dao.Proprietaire proprietaireConnecte;
            List<dao.Proprietaire> proprietaires = gestionProprietaire.lister();
            dao.Proprietaire tempProprietaire = null;
            for (dao.Proprietaire p : proprietaires) {
                if (p.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    tempProprietaire = p;
                    break;
                }
            }
            proprietaireConnecte = tempProprietaire;

            if (proprietaireConnecte == null) {
                JOptionPane.showMessageDialog(this, "Propriétaire non trouvé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculer les statistiques du propriétaire
            List<dao.Boutique> boutiques = gestionBoutique.lister();
            List<dao.Boutique> mesBoutiques = boutiques.stream()
                .filter(b -> b.getProprietaire() != null && b.getProprietaire().getId() == proprietaireConnecte.getId())
                .collect(Collectors.toList());

            int nbBoutiques = mesBoutiques.size();
            int nbReparationsTotal = 0;
            double chiffreAffairesTotal = 0.0;

            // Calculer pour chaque boutique
            for (dao.Boutique boutique : mesBoutiques) {
                final int boutiqueId = boutique.getIdBoutique();
                // Compter les réparations de cette boutique
                List<dao.Reparation> reparationsBoutique = gestionReparation.lister().stream()
                    .filter(r -> r.getIdBoutique() == boutiqueId)
                    .collect(Collectors.toList());
                nbReparationsTotal += reparationsBoutique.size();

                // Calculer le CA de cette boutique
                chiffreAffairesTotal += reparationsBoutique.stream()
                    .mapToDouble(dao.Reparation::getPrixTotal)
                    .sum();
            }

            // Statistiques des caisses
            double soldeTotalCaisses = calculerSoldeTotal();

            // Statistiques des reçus et emprunts
            double totalRecus = gestionRecu.lister().stream().mapToDouble(dao.Recu::getMontant).sum();
            double totalEmprunts = gestionEmprunt.lister().stream().mapToDouble(dao.Emprunt::getMontant).sum();

            // Créer le message des statistiques
            StringBuilder stats = new StringBuilder();
            stats.append("=== STATISTIQUES PERSONNELLES ===\n\n");
            stats.append("🏢 Boutiques possédées: ").append(nbBoutiques).append("\n");
            stats.append("🔧 Réparations totales: ").append(nbReparationsTotal).append("\n");
            stats.append(String.format("💰 Chiffre d'affaires: %.2f €\n", chiffreAffairesTotal));
            stats.append(String.format("💵 Solde total des caisses: %.2f €\n", soldeTotalCaisses));
            stats.append(String.format("📈 Recettes totales: %.2f €\n", totalRecus));
            stats.append(String.format("📉 Dépenses totales: %.2f €\n", totalEmprunts));
            stats.append(String.format("📊 Bénéfice net: %.2f €\n", totalRecus - totalEmprunts));

            if (nbBoutiques > 0) {
                stats.append(String.format("\n📈 Moyenne par boutique:\n"));
                stats.append(String.format("• Réparations: %.1f\n", (double) nbReparationsTotal / nbBoutiques));
                stats.append(String.format("• CA: %.2f €\n", chiffreAffairesTotal / nbBoutiques));
            }

            JOptionPane.showMessageDialog(this, stats.toString(),
                "Mes Statistiques",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des statistiques: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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

    private void addFinanceListeners(JButton btnCaisses, JButton btnRecus, JButton btnEmprunts, JButton btnRapport, JTextArea infoArea) {

        btnCaisses.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("=== ÉTAT DES CAISSES ===\n\n");

                java.util.List<dao.Caisse> caisses = gestionCaisse.lister();
                for (dao.Caisse caisse : caisses) {
                    sb.append(String.format("Caisse ID %d: %.2f € (Dernier mouvement: %s)\n",
                        caisse.getIdCaisse(),
                        caisse.getSoldeActuel(),
                        caisse.getDernierMouvement() != null ? caisse.getDernierMouvement().toString() : "N/A"));
                }

                infoArea.setText(sb.toString());

            } catch (Exception ex) {
                infoArea.setText("Erreur lors de la récupération des caisses:\n" + ex.getMessage());
            }
        });

        btnRecus.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("=== REÇUS ===\n\n");

                java.util.List<dao.Recu> recus = gestionRecu.lister();
                double total = 0;
                for (dao.Recu recu : recus) {
                    sb.append(String.format("Reçu ID %d: %.2f € (Date: %s)\n",
                        recu.getIdRecu(),
                        recu.getMontant(),
                        recu.getDate() != null ? recu.getDate().toString() : "N/A"));
                    total += recu.getMontant();
                }
                sb.append(String.format("\nTOTAL: %.2f €", total));

                infoArea.setText(sb.toString());

            } catch (Exception ex) {
                infoArea.setText("Erreur lors de la récupération des reçus:\n" + ex.getMessage());
            }
        });

        btnEmprunts.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("=== EMPRUNTS ===\n\n");

                java.util.List<dao.Emprunt> emprunts = gestionEmprunt.lister();
                double total = 0;
                for (dao.Emprunt emprunt : emprunts) {
                    sb.append(String.format("Emprunt ID %d: %.2f € (Type: %s, Date: %s)\nCommentaire: %s\n\n",
                        emprunt.getIdEmprunt(),
                        emprunt.getMontant(),
                        emprunt.getType(),
                        emprunt.getDate() != null ? emprunt.getDate().toString() : "N/A",
                        emprunt.getCommentaire() != null ? emprunt.getCommentaire() : "N/A"));
                    total += emprunt.getMontant();
                }
                sb.append(String.format("\nTOTAL: %.2f €", total));

                infoArea.setText(sb.toString());

            } catch (Exception ex) {
                infoArea.setText("Erreur lors de la récupération des emprunts:\n" + ex.getMessage());
            }
        });

        btnRapport.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("=== RAPPORT FINANCIER GLOBAL ===\n\n");

                double soldeTotal = calculerSoldeTotal();
                double totalRecus = gestionRecu.lister().stream().mapToDouble(dao.Recu::getMontant).sum();
                double totalEmprunts = gestionEmprunt.lister().stream().mapToDouble(dao.Emprunt::getMontant).sum();

                sb.append(String.format("Solde total des caisses: %.2f €\n", soldeTotal));
                sb.append(String.format("Total des recettes: %.2f €\n", totalRecus));
                sb.append(String.format("Total des emprunts: %.2f €\n", totalEmprunts));
                sb.append(String.format("Bénéfice net: %.2f €\n", totalRecus - totalEmprunts));

                infoArea.setText(sb.toString());

            } catch (Exception ex) {
                infoArea.setText("Erreur lors de la génération du rapport:\n" + ex.getMessage());
            }
        });
    }

    private void createReparationsTab() {
        JPanel reparationPanel = new JPanel(new BorderLayout());
        reparationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirReparations = new JButton("📋 Voir Réparations");
        JButton btnCreerReparation = new JButton("🆕 Créer Réparation");
        JButton btnDetailsReparation = new JButton("📊 Détails");
        JButton btnTerminerReparation = new JButton("✅ Terminer");

        // Style des boutons
        btnVoirReparations.setBackground(new Color(52, 152, 219));
        btnCreerReparation.setBackground(new Color(40, 167, 69));
        btnDetailsReparation.setBackground(new Color(155, 89, 182));
        btnTerminerReparation.setBackground(new Color(23, 162, 184));

        for (JButton btn : new JButton[]{btnVoirReparations, btnCreerReparation, btnDetailsReparation, btnTerminerReparation}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        reparationPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des réparations des boutiques du propriétaire
        String[] columns = {"ID", "Appareil", "Code Suivi", "Date Dépôt", "État", "Prix Total", "Boutique"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        reparationPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("🔧 Réparations", reparationPanel);

        // Charger les réparations au démarrage
        rafraichirReparationsProprietaire(tableModel);

        // Listeners
        btnVoirReparations.addActionListener(e -> rafraichirReparationsProprietaire(tableModel));
        btnCreerReparation.addActionListener(e -> creerNouvelleReparationProprietaire(tableModel));
        btnDetailsReparation.addActionListener(e -> voirDetailsReparationProprietaire(table));
        btnTerminerReparation.addActionListener(e -> terminerReparationProprietaire(table, tableModel));
    }

    private void rafraichirReparationsProprietaire(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();
            if (proprietaire == null) {
                return;
            }

            // Récupérer les IDs des boutiques du propriétaire
            List<dao.Boutique> boutiquesProprietaire = gestionBoutique.lister().stream()
                .filter(b -> b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId())
                .collect(Collectors.toList());

            List<Integer> idsBoutiques = boutiquesProprietaire.stream()
                .map(dao.Boutique::getIdBoutique)
                .collect(Collectors.toList());

            // Récupérer toutes les réparations et filtrer par boutiques du propriétaire
            List<dao.Reparation> reparations = gestionReparation.lister().stream()
                .filter(r -> idsBoutiques.contains(r.getIdBoutique()))
                .collect(Collectors.toList());

            for (dao.Reparation reparation : reparations) {
                // Récupérer le nom de l'appareil
                String nomAppareil = "Inconnu";
                try {
                    dao.Appareil appareil = gestionAppareil.rechercher(reparation.getIdAppareil());
                    if (appareil != null) {
                        nomAppareil = appareil.getMarque() + " " + appareil.getModele();
                    }
                } catch (Exception e) {
                    // Garder "Inconnu" en cas d'erreur
                }

                // Récupérer le nom de la boutique
                String nomBoutique = "Inconnue";
                try {
                    dao.Boutique boutique = boutiquesProprietaire.stream()
                        .filter(b -> b.getIdBoutique() == reparation.getIdBoutique())
                        .findFirst()
                        .orElse(null);
                    if (boutique != null) {
                        nomBoutique = boutique.getNom();
                    }
                } catch (Exception e) {
                    // Garder "Inconnue" en cas d'erreur
                }

                tableModel.addRow(new Object[]{
                    reparation.getIdReparation(),
                    nomAppareil,
                    reparation.getCodeSuivi(),
                    reparation.getDateDepot() != null ?
                        reparation.getDateDepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                    reparation.getEtat(),
                    String.format("%.2f €", reparation.getPrixTotal()),
                    nomBoutique
                });
            }

            if (reparations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucune réparation trouvée pour vos boutiques.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des réparations: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voirDetailsReparationProprietaire(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réparation.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparation = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparation reparation = gestionReparation.rechercher(idReparation);

            if (reparation != null) {
                // Récupérer les détails de l'appareil
                String detailsAppareil = "Informations non disponibles";
                try {
                    dao.Appareil appareil = gestionAppareil.rechercher(reparation.getIdAppareil());
                    if (appareil != null) {
                        detailsAppareil = String.format("%s %s (IMEI: %s)",
                            appareil.getMarque(), appareil.getModele(),
                            appareil.getImei() != null ? appareil.getImei() : "N/A");
                    }
                } catch (Exception e) {
                    // Garder le message par défaut
                }

                // Récupérer les détails de la boutique
                String detailsBoutique = "Informations non disponibles";
                try {
                    dao.Boutique boutique = gestionBoutique.rechercher(reparation.getIdBoutique());
                    if (boutique != null) {
                        detailsBoutique = String.format("%s - %s, %s",
                            boutique.getNom(), boutique.getAdresse(), boutique.getNumP());
                    }
                } catch (Exception e) {
                    // Garder le message par défaut
                }

                String details = String.format(
                    "DÉTAILS DE LA RÉPARATION\n\n" +
                    "ID Réparation: %d\n" +
                    "Code de suivi: %s\n" +
                    "Date de dépôt: %s\n" +
                    "État: %s\n" +
                    "Prix total: %.2f €\n\n" +
                    "APPAREIL:\n%s\n\n" +
                    "BOUTIQUE:\n%s\n\n" +
                    "COMMENTAIRE:\n%s",
                    reparation.getIdReparation(),
                    reparation.getCodeSuivi(),
                    reparation.getDateDepot() != null ?
                        reparation.getDateDepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    reparation.getEtat(),
                    reparation.getPrixTotal(),
                    detailsAppareil,
                    detailsBoutique,
                    reparation.getCommentaire() != null ? reparation.getCommentaire() : "Aucun commentaire"
                );

                JOptionPane.showMessageDialog(this, details, "Détails Réparation",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des détails: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void terminerReparationProprietaire(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réparation à terminer.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparation = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparation reparation = gestionReparation.rechercher(idReparation);

            if (reparation != null) {
                if ("Terminée".equals(reparation.getEtat())) {
                    JOptionPane.showMessageDialog(this, "Cette réparation est déjà terminée.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int option = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir marquer cette réparation comme terminée ?\n\n" +
                    "Montant à créditer: " + String.format("%.2f €", reparation.getPrixTotal()),
                    "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) return;

                // Marquer la réparation comme terminée
                reparation.setEtat("Terminée");
                gestionReparation.modifer(reparation);

                // Ajouter automatiquement le montant à la caisse
                try {
                    ajouterMontantALaCaisseProprietaire(reparation);
                    JOptionPane.showMessageDialog(this,
                        "Réparation marquée comme terminée !\n" +
                        "Le montant de " + String.format("%.2f €", reparation.getPrixTotal()) +
                        " a été ajouté à la caisse de la boutique.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception caisseError) {
                    JOptionPane.showMessageDialog(this,
                        "Réparation terminée, mais erreur lors de la mise à jour de la caisse :\n" +
                        caisseError.getMessage() + "\n\n" +
                        "Le montant devra être ajouté manuellement à la caisse.",
                        "Attention", JOptionPane.WARNING_MESSAGE);
                }

                rafraichirReparationsProprietaire(tableModel);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouvelleReparationProprietaire(DefaultTableModel tableModel) {
        try {
            // Vérifier que des appareils existent
            List<dao.Appareil> appareils = gestionAppareil.lister();
            if (appareils.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun appareil disponible. Vous devez d'abord créer des appareils.",
                    "Appareils manquants", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Récupérer le propriétaire connecté
            dao.Proprietaire proprietaire = getProprietaireConnecte();
            if (proprietaire == null) {
                JOptionPane.showMessageDialog(this, "Propriétaire non trouvé. Veuillez vous reconnecter.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Récupérer les boutiques du propriétaire
            List<dao.Boutique> boutiquesProprietaire = gestionBoutique.lister().stream()
                .filter(b -> b.getProprietaire() != null && b.getProprietaire().getId() == proprietaire.getId())
                .collect(Collectors.toList());

            if (boutiquesProprietaire.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vous n'avez aucune boutique. Créez d'abord une boutique.",
                    "Boutiques manquantes", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Créer les options pour les combo boxes
            String[] appareilOptions = appareils.stream()
                .map(a -> a.getIdAppareil() + " - " + a.getMarque() + " " + a.getModele() + " (IMEI: " + a.getImei() + ")")
                .toArray(String[]::new);

            String[] boutiqueOptions = boutiquesProprietaire.stream()
                .map(b -> b.getIdBoutique() + " - " + b.getNom() + " (" + b.getAdresse() + ")")
                .toArray(String[]::new);

            // Récupérer tous les réparateurs disponibles
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            String[] reparateurOptions = reparateurs.stream()
                .map(r -> r.getId() + " - " + r.getNom() + " " + r.getPrenom() +
                        (r.getBoutique() != null ? " (Boutique: " + r.getBoutique().getNom() + ")" : " (Libre)"))
                .toArray(String[]::new);

            // Créer les composants du formulaire
            JComboBox<String> appareilCombo = new JComboBox<>(appareilOptions);
            JComboBox<String> boutiqueCombo = new JComboBox<>(boutiqueOptions);
            JComboBox<String> reparateurCombo = new JComboBox<>(reparateurOptions);
            JTextField codeField = new JTextField("REP-" + System.currentTimeMillis());
            JTextField prixField = new JTextField("0.0");

            Object[] message = {
                "Sélectionner l'appareil:", appareilCombo,
                "Sélectionner votre boutique:", boutiqueCombo,
                "Assigner un réparateur:", reparateurCombo,
                "Code de suivi:", codeField,
                "Prix estimé:", prixField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Créer une Nouvelle Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                // Récupérer les sélections
                String selectedAppareil = (String) appareilCombo.getSelectedItem();
                String selectedBoutique = (String) boutiqueCombo.getSelectedItem();
                String selectedReparateur = (String) reparateurCombo.getSelectedItem();

                int idAppareil = Integer.parseInt(selectedAppareil.split(" - ")[0]);
                int idBoutique = Integer.parseInt(selectedBoutique.split(" - ")[0]);
                int idReparateur = Integer.parseInt(selectedReparateur.split(" - ")[0]);

                // Validation du prix
                try {
                    double prix = Double.parseDouble(prixField.getText().trim());
                    if (prix < 0) {
                        JOptionPane.showMessageDialog(this, "Le prix ne peut pas être négatif.",
                            "Prix invalide", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Créer la réparation
                    dao.Reparation reparation = dao.Reparation.builder()
                        .idAppareil(idAppareil)
                        .idBoutique(idBoutique)
                        .idReparateur(idReparateur)
                        .codeSuivi(codeField.getText().trim())
                        .dateDepot(java.time.LocalDateTime.now())
                        .etat("En cours")
                        .commentaire("")
                        .prixTotal(prix)
                        .build();

                    gestionReparation.ajouter(reparation);

                    // Rafraîchir la table
                    rafraichirReparationsProprietaire(tableModel);

                    JOptionPane.showMessageDialog(this,
                        "Réparation créée avec succès !\n\n" +
                        "Appareil: " + selectedAppareil.split(" - ")[1] + "\n" +
                        "Boutique: " + selectedBoutique.split(" - ")[1] + "\n" +
                        "Réparateur: " + selectedReparateur.split(" - ")[1] + "\n" +
                        "Prix estimé: " + String.format("%.2f €", prix),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Le prix doit être un nombre décimal valide.",
                        "Format invalide", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de la réparation: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterMontantALaCaisseProprietaire(dao.Reparation reparation) throws Exception {
        if (reparation == null) {
            throw new IllegalArgumentException("Réparation invalide (null)");
        }

        if (reparation.getPrixTotal() <= 0) {
            System.out.println("INFO: Réparation ID " + reparation.getIdReparation() +
                             " terminée mais montant nul ou négatif (" + reparation.getPrixTotal() +
                             "€) - Aucun ajout à la caisse");
            return;
        }

        System.out.println("INFO: Propriétaire - Ajout automatique de " + reparation.getPrixTotal() +
                          "€ à la caisse pour la réparation ID " + reparation.getIdReparation());

        // Récupérer la boutique associée à la réparation
        dao.Boutique boutique = gestionBoutique.rechercher(reparation.getIdBoutique());
        if (boutique == null) {
            throw new Exception("Boutique non trouvée pour la réparation ID: " + reparation.getIdReparation());
        }

        // Trouver la caisse de cette boutique
        List<dao.Caisse> caisses = gestionCaisse.lister();
        dao.Caisse caisseBoutique = null;
        for (dao.Caisse caisse : caisses) {
            if (caisse.getBoutique() != null && caisse.getBoutique().getIdBoutique() == boutique.getIdBoutique()) {
                caisseBoutique = caisse;
                break;
            }
        }

        if (caisseBoutique == null) {
            throw new Exception("Aucune caisse trouvée pour la boutique '" + boutique.getNom() + "'");
        }

        // Calculer le nouveau solde
        double ancienSolde = caisseBoutique.getSoldeActuel();
        double montantAjoute = reparation.getPrixTotal();
        double nouveauSolde = ancienSolde + montantAjoute;

        // Mettre à jour la caisse
        caisseBoutique.setSoldeActuel(nouveauSolde);
        caisseBoutique.setDernierMouvement(java.time.LocalDateTime.now());
        gestionCaisse.modifer(caisseBoutique);

        System.out.println("✅ PAIEMENT PROPRIÉTAIRE - Réparation ID " + reparation.getIdReparation() +
                          " | Boutique: " + boutique.getNom() +
                          " | Montant: " + String.format("%.2f€", montantAjoute) +
                          " | Solde: " + String.format("%.2f€", ancienSolde) +
                          " → " + String.format("%.2f€", nouveauSolde));
    }
}


