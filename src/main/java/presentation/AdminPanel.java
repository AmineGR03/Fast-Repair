package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.*;

/**
 * Panel d'administration avec toutes les fonctionnalités CRUD
 */
public class AdminPanel extends JPanel {

    private MainWindow mainWindow;

    // Gestionnaires métier
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


    private void createAppareilTab() {
        JPanel appareilPanel = new JPanel(new BorderLayout());
        appareilPanel.add(createCrudPanel("Appareil", new String[]{"ID Appareil", "IMEI", "Marque", "Modèle", "Type"}), BorderLayout.CENTER);
        tabbedPane.addTab("📱 Appareils", appareilPanel);
    }

    private void createReparationTab() {
        JPanel reparationPanel = new JPanel(new BorderLayout());
        reparationPanel.add(createCrudPanel("Reparation", new String[]{"ID", "ID Appareil", "ID Boutique", "ID Réparateur", "Code Suivi", "Date Dépôt", "État", "Prix Total"}), BorderLayout.CENTER);
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
        boutiquePanel.add(createCrudPanel("Boutique", new String[]{"ID Boutique", "Nom", "Adresse", "Téléphone", "Numéro P", "ID Propriétaire"}), BorderLayout.CENTER);
        tabbedPane.addTab("🏪 Boutiques", boutiquePanel);
    }

    private void createCaisseTab() {
        JPanel caissePanel = new JPanel(new BorderLayout());
        caissePanel.add(createCrudPanel("Caisse", new String[]{"ID Caisse", "ID Boutique", "Solde Actuel", "Dernier Mouvement"}), BorderLayout.CENTER);
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
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
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
            int nbAppareils = gestionAppareil.lister().size();
            int nbReparations = gestionReparation.lister().size();
            int nbReparateurs = gestionReparateur.lister().size();
            int nbProprietaires = gestionProprietaire.lister().size();
            int nbBoutiques = gestionBoutique.lister().size();
            double soldeTotal = calculerSoldeTotal();

            panel.add(createStatCard("📱 Appareils", String.valueOf(nbAppareils), new Color(155, 89, 182)));
            panel.add(createStatCard("🔧 Réparations", String.valueOf(nbReparations), new Color(46, 204, 113)));
            panel.add(createStatCard("👷 Réparateurs", String.valueOf(nbReparateurs), new Color(230, 126, 34)));
            panel.add(createStatCard("🏢 Propriétaires", String.valueOf(nbProprietaires), new Color(52, 152, 219)));
            panel.add(createStatCard("🏪 Boutiques", String.valueOf(nbBoutiques), new Color(155, 89, 182)));
            panel.add(createStatCard("💰 Solde Total", String.format("%.2f €", soldeTotal), new Color(231, 76, 60)));

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
                int rowCount = table.getRowCount();
                JOptionPane.showMessageDialog(this, 
                    "Liste mise à jour avec succès!\nNombre d'éléments: " + rowCount,
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                String errorMsg = "Erreur lors du listage: " + ex.getMessage();
                System.err.println(errorMsg);
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, errorMsg,
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAjouter.addActionListener(e -> handleAjouter(entityName, table));
        btnModifier.addActionListener(e -> handleModifier(entityName, table));
        btnSupprimer.addActionListener(e -> handleSupprimer(entityName, table));
        btnRechercher.addActionListener(e -> handleRechercher(entityName, table));
    }

    private void refreshTableData(JTable table, String entityName) throws Exception {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        switch (entityName) {
            case "Appareil":
                loadAppareils(model);
                break;
            case "Reparation":
                loadReparations(model);
                break;
            case "Reparateur":
                loadReparateurs(model);
                break;
            case "Proprietaire":
                loadProprietaires(model);
                break;
            case "Boutique":
                loadBoutiques(model);
                break;
            case "Caisse":
                loadCaisses(model);
                break;
            case "Composant":
                loadComposants(model);
                break;
            case "Emprunt":
                loadEmprunts(model);
                break;
            case "Recu":
                loadRecus(model);
                break;
        }
    }

    // === GESTION DES APPAREILS ===
    private void loadAppareils(DefaultTableModel model) throws Exception {
        try {
            List<Appareil> appareils = gestionAppareil.lister();
            if (appareils == null) {
                System.err.println("Liste d'appareils est null");
                return;
            }
            System.out.println("Nombre d'appareils à charger: " + appareils.size());
            for (Appareil a : appareils) {
                if (a == null) {
                    System.err.println("Appareil null détecté");
                    continue;
                }
                Object[] rowData = {
                    a.getIdAppareil(), 
                    a.getImei() != null ? a.getImei() : "",
                    a.getMarque() != null ? a.getMarque() : "",
                    a.getModele() != null ? a.getModele() : "",
                    a.getTypeAppareil() != null ? a.getTypeAppareil() : ""
                };
                model.addRow(rowData);
            }
        } catch (Exception e) {
            System.err.println("Erreur dans loadAppareils: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void handleAjouter(String entityName, JTable table) {
        switch (entityName) {
            case "Appareil":
                ajouterAppareil(table);
                break;
            case "Reparation":
                ajouterReparation(table);
                break;
            case "Reparateur":
                ajouterReparateur(table);
                break;
            case "Proprietaire":
                ajouterProprietaire(table);
                break;
            case "Boutique":
                ajouterBoutique(table);
                break;
            case "Caisse":
                ajouterCaisse(table);
                break;
            case "Composant":
                ajouterComposant(table);
                break;
            case "Emprunt":
                ajouterEmprunt(table);
                break;
            case "Recu":
                ajouterRecu(table);
                break;
        }
    }

    private void handleModifier(String entityName, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à modifier",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (entityName) {
            case "Appareil":
                modifierAppareil(table, selectedRow);
                break;
            case "Reparation":
                modifierReparation(table, selectedRow);
                break;
            case "Reparateur":
                modifierReparateur(table, selectedRow);
                break;
            case "Proprietaire":
                modifierProprietaire(table, selectedRow);
                break;
            case "Boutique":
                modifierBoutique(table, selectedRow);
                break;
            case "Caisse":
                modifierCaisse(table, selectedRow);
                break;
            case "Composant":
                modifierComposant(table, selectedRow);
                break;
            case "Emprunt":
                modifierEmprunt(table, selectedRow);
                break;
            case "Recu":
                modifierRecu(table, selectedRow);
                break;
        }
    }

    private void handleSupprimer(String entityName, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à supprimer",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cet élément ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (option != JOptionPane.YES_OPTION) return;

        try {
            switch (entityName) {
                case "Appareil":
                    gestionAppareil.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Reparation":
                    gestionReparation.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Reparateur":
                    gestionReparateur.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Proprietaire":
                    gestionProprietaire.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Boutique":
                    gestionBoutique.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Caisse":
                    gestionCaisse.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Composant":
                    gestionComposant.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Emprunt":
                    gestionEmprunt.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
                case "Recu":
                    gestionRecu.supprimer((Integer) table.getValueAt(selectedRow, 0));
                    break;
            }
            refreshTableData(table, entityName);
            JOptionPane.showMessageDialog(this, "Élément supprimé avec succès!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRechercher(String entityName, JTable table) {
        String idStr = JOptionPane.showInputDialog(this, "Entrez l'ID à rechercher:",
            "Recherche", JOptionPane.QUESTION_MESSAGE);
        
        if (idStr == null || idStr.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(idStr);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            switch (entityName) {
                case "Appareil":
                    Appareil app = gestionAppareil.rechercher(id);
                    if (app != null) {
                        model.setRowCount(0);
                        model.addRow(new Object[]{
                            app.getIdAppareil(), 
                            app.getImei() != null ? app.getImei() : "",
                            app.getMarque() != null ? app.getMarque() : "",
                            app.getModele() != null ? app.getModele() : "",
                            app.getTypeAppareil() != null ? app.getTypeAppareil() : ""
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Appareil non trouvé avec l'ID: " + id,
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                case "Reparation":
                    Reparation rep = gestionReparation.rechercher(id);
                    if (rep != null) {
                        model.setRowCount(0);
                        loadReparationInTable(model, rep);
                    } else {
                        JOptionPane.showMessageDialog(this, "Réparation non trouvée");
                    }
                    break;
                case "Reparateur":
                    Reparateur repa = gestionReparateur.rechercher(id);
                    if (repa != null) {
                        model.addRow(new Object[]{
                            repa.getId(), repa.getNom(), repa.getPrenom(),
                            repa.getEmail(), repa.getMdp(), repa.getPourcentageGain()
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Réparateur non trouvé");
                    }
                    break;
                case "Proprietaire":
                    Proprietaire prop = gestionProprietaire.rechercher(id);
                    if (prop != null) {
                        model.addRow(new Object[]{
                            prop.getId(), prop.getNom(), prop.getPrenom(),
                            prop.getEmail(), prop.getMdp()
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Propriétaire non trouvé");
                    }
                    break;
                case "Boutique":
                    Boutique bout = gestionBoutique.rechercher(id);
                    if (bout != null) {
                        model.addRow(new Object[]{
                            bout.getIdBoutique(), bout.getNom(), bout.getAdresse(),
                            bout.getNumTel(), bout.getNumP()
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Boutique non trouvée");
                    }
                    break;
                case "Caisse":
                    Caisse caisse = gestionCaisse.rechercher(id);
                    if (caisse != null) {
                        model.setRowCount(0);
                        loadCaisseInTable(model, caisse);
                    } else {
                        JOptionPane.showMessageDialog(this, "Caisse non trouvée");
                    }
                    break;
                case "Composant":
                    Composant comp = gestionComposant.rechercher(id);
                    if (comp != null) {
                        model.addRow(new Object[]{
                            comp.getIdComposant(), comp.getNom(), comp.getPrix(), comp.getQuantite()
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, "Composant non trouvé");
                    }
                    break;
                case "Emprunt":
                    Emprunt emp = gestionEmprunt.rechercher(id);
                    if (emp != null) {
                        loadEmpruntInTable(model, emp);
                    } else {
                        JOptionPane.showMessageDialog(this, "Emprunt non trouvé");
                    }
                    break;
                case "Recu":
                    Recu recu = gestionRecu.rechercher(id);
                    if (recu != null) {
                        loadRecuInTable(model, recu);
                    } else {
                        JOptionPane.showMessageDialog(this, "Reçu non trouvé");
                    }
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === MÉTHODES D'AJOUT ===
    private void ajouterAppareil(JTable table) {
        JTextField imeiField = new JTextField();
        JTextField marqueField = new JTextField();
        JTextField modeleField = new JTextField();
        JTextField typeField = new JTextField();

        Object[] message = {
            "IMEI:", imeiField,
            "Marque:", marqueField,
            "Modèle:", modeleField,
            "Type:", typeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Appareil",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Appareil appareil = Appareil.builder()
                    .imei(imeiField.getText())
                    .marque(marqueField.getText())
                    .modele(modeleField.getText())
                    .typeAppareil(typeField.getText())
                    .build();
                gestionAppareil.ajouter(appareil);
                refreshTableData(table, "Appareil");
                JOptionPane.showMessageDialog(this, "Appareil ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterReparation(JTable table) {
        // Récupérer la liste des appareils, boutiques et réparateurs
        try {
            List<Appareil> appareils = gestionAppareil.lister();
            List<Boutique> boutiques = gestionBoutique.lister();
            List<Reparateur> reparateurs = gestionReparateur.lister();

            String[] imeiList = appareils.stream()
                .map(a -> a.getIdAppareil() + " - " + a.getImei())
                .toArray(String[]::new);
            String[] boutiqueList = boutiques.stream()
                .map(b -> b.getIdBoutique() + " - " + b.getNom())
                .toArray(String[]::new);
            String[] reparateurList = reparateurs.stream()
                .map(r -> r.getId() + " - " + r.getNom() + " " + r.getPrenom())
                .toArray(String[]::new);

            if (imeiList.length == 0 || boutiqueList.length == 0 || reparateurList.length == 0) {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord créer des appareils, boutiques et réparateurs.");
                return;
            }

            JComboBox<String> appareilCombo = new JComboBox<>(imeiList);
            JComboBox<String> boutiqueCombo = new JComboBox<>(boutiqueList);
            JComboBox<String> reparateurCombo = new JComboBox<>(reparateurList);
            JTextField codeField = new JTextField();
            JTextField etatField = new JTextField("En cours");
            JTextField prixField = new JTextField("0.0");

            Object[] message = {
                "ID Appareil:", appareilCombo,
                "ID Boutique:", boutiqueCombo,
                "ID Réparateur:", reparateurCombo,
                "Code Suivi:", codeField,
                "État:", etatField,
                "Prix Total:", prixField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int idAppareil = Integer.parseInt(((String) appareilCombo.getSelectedItem()).split(" - ")[0]);
                int idBoutique = Integer.parseInt(((String) boutiqueCombo.getSelectedItem()).split(" - ")[0]);
                int idReparateur = Integer.parseInt(((String) reparateurCombo.getSelectedItem()).split(" - ")[0]);

                Reparation reparation = Reparation.builder()
                    .idAppareil(idAppareil)
                    .idBoutique(idBoutique)
                    .idReparateur(idReparateur)
                    .codeSuivi(codeField.getText())
                    .dateDepot(LocalDateTime.now())
                    .etat(etatField.getText())
                    .commentaire("")
                    .prixTotal(Double.parseDouble(prixField.getText()))
                    .build();
                gestionReparation.ajouter(reparation);
                refreshTableData(table, "Reparation");
                JOptionPane.showMessageDialog(this, "Réparation ajoutée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void ajouterReparateur(JTable table) {
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField mdpField = new JPasswordField();
        JTextField pourcentageField = new JTextField("10.0");

        Object[] message = {
            "Nom:", nomField,
            "Prénom:", prenomField,
            "Email:", emailField,
            "Mot de passe:", mdpField,
            "Pourcentage Gain:", pourcentageField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Réparateur",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Reparateur reparateur = Reparateur.builder()
                    .nom(nomField.getText())
                    .prenom(prenomField.getText())
                    .email(emailField.getText())
                    .mdp(new String(mdpField.getPassword()))
                    .pourcentageGain(Double.parseDouble(pourcentageField.getText()))
                    .build();
                gestionReparateur.ajouter(reparateur);
                refreshTableData(table, "Reparateur");
                JOptionPane.showMessageDialog(this, "Réparateur ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterProprietaire(JTable table) {
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField mdpField = new JPasswordField();

        Object[] message = {
            "Nom:", nomField,
            "Prénom:", prenomField,
            "Email:", emailField,
            "Mot de passe:", mdpField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Propriétaire",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Proprietaire proprietaire = Proprietaire.builder()
                    .nom(nomField.getText())
                    .prenom(prenomField.getText())
                    .email(emailField.getText())
                    .mdp(new String(mdpField.getPassword()))
                    .build();
                gestionProprietaire.ajouter(proprietaire);
                refreshTableData(table, "Proprietaire");
                JOptionPane.showMessageDialog(this, "Propriétaire ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterBoutique(JTable table) {
        try {
            List<Proprietaire> proprietaires = gestionProprietaire.lister();
            if (proprietaires.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord créer un propriétaire.");
                return;
            }
            String[] proprietaireList = proprietaires.stream()
                .map(p -> p.getId() + " - " + p.getNom() + " " + p.getPrenom())
                .toArray(String[]::new);

            JComboBox<String> proprietaireCombo = new JComboBox<>(proprietaireList);
            JTextField nomField = new JTextField();
            JTextField adresseField = new JTextField();
            JTextField telField = new JTextField();
            JTextField numPField = new JTextField();

            Object[] message = {
                "ID Propriétaire:", proprietaireCombo,
                "Nom:", nomField,
                "Adresse:", adresseField,
                "Téléphone:", telField,
                "Numéro P:", numPField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Boutique",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int idProprietaire = Integer.parseInt(((String) proprietaireCombo.getSelectedItem()).split(" - ")[0]);
                Boutique boutique = Boutique.builder()
                    .nom(nomField.getText())
                    .adresse(adresseField.getText())
                    .numTel(Integer.parseInt(telField.getText()))
                    .numP(Integer.parseInt(numPField.getText()))
                    .build();
                // Note: On ne peut pas directement set idProprietaire car c'est une relation
                // Il faut utiliser setProprietaire avec l'objet Proprietaire
                Proprietaire prop = gestionProprietaire.rechercher(idProprietaire);
                boutique.setProprietaire(prop);
                gestionBoutique.ajouter(boutique);
                refreshTableData(table, "Boutique");
                JOptionPane.showMessageDialog(this, "Boutique ajoutée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void ajouterCaisse(JTable table) {
        try {
            List<Boutique> boutiques = gestionBoutique.lister();
            if (boutiques.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord créer une boutique.");
                return;
            }
            String[] boutiqueList = boutiques.stream()
                .map(b -> b.getIdBoutique() + " - " + b.getNom())
                .toArray(String[]::new);

            JComboBox<String> boutiqueCombo = new JComboBox<>(boutiqueList);
            JTextField soldeField = new JTextField("0.0");

            Object[] message = {
                "ID Boutique:", boutiqueCombo,
                "Solde Actuel:", soldeField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Caisse",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int idBoutique = Integer.parseInt(((String) boutiqueCombo.getSelectedItem()).split(" - ")[0]);
                Boutique boutique = gestionBoutique.rechercher(idBoutique);
                
                Caisse caisse = Caisse.builder()
                    .soldeActuel(Double.parseDouble(soldeField.getText()))
                    .dernierMouvement(LocalDateTime.now())
                    .boutique(boutique)
                    .build();
                gestionCaisse.ajouter(caisse);
                refreshTableData(table, "Caisse");
                JOptionPane.showMessageDialog(this, "Caisse ajoutée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void ajouterComposant(JTable table) {
        JTextField nomField = new JTextField();
        JTextField prixField = new JTextField("0.0");
        JTextField quantiteField = new JTextField("0");

        Object[] message = {
            "Nom:", nomField,
            "Prix:", prixField,
            "Quantité:", quantiteField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Composant",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Composant composant = Composant.builder()
                    .nom(nomField.getText())
                    .prix(Double.parseDouble(prixField.getText()))
                    .quantite(Integer.parseInt(quantiteField.getText()))
                    .build();
                gestionComposant.ajouter(composant);
                refreshTableData(table, "Composant");
                JOptionPane.showMessageDialog(this, "Composant ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterEmprunt(JTable table) {
        JTextField montantField = new JTextField("0.0");
        JTextField typeField = new JTextField();
        JTextArea commentaireArea = new JTextArea(3, 20);

        Object[] message = {
            "Montant:", montantField,
            "Type:", typeField,
            "Commentaire:", new JScrollPane(commentaireArea)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Emprunt",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Emprunt emprunt = Emprunt.builder()
                    .date(LocalDateTime.now())
                    .montant(Double.parseDouble(montantField.getText()))
                    .type(typeField.getText())
                    .commentaire(commentaireArea.getText())
                    .build();
                gestionEmprunt.ajouter(emprunt);
                refreshTableData(table, "Emprunt");
                JOptionPane.showMessageDialog(this, "Emprunt ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ajouterRecu(JTable table) {
        JTextField montantField = new JTextField("0.0");

        Object[] message = {
            "Montant:", montantField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Ajouter Reçu",
            JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Recu recu = Recu.builder()
                    .date(LocalDateTime.now())
                    .montant(Double.parseDouble(montantField.getText()))
                    .build();
                gestionRecu.ajouter(recu);
                refreshTableData(table, "Recu");
                JOptionPane.showMessageDialog(this, "Reçu ajouté avec succès!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // === MÉTHODES DE MODIFICATION ===
    private void modifierAppareil(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Appareil appareil = gestionAppareil.rechercher(id);
            if (appareil == null) {
                JOptionPane.showMessageDialog(this, "Appareil non trouvé");
                return;
            }

            JTextField imeiField = new JTextField(appareil.getImei());
            JTextField marqueField = new JTextField(appareil.getMarque());
            JTextField modeleField = new JTextField(appareil.getModele());
            JTextField typeField = new JTextField(appareil.getTypeAppareil());

            Object[] message = {
                "IMEI:", imeiField,
                "Marque:", marqueField,
                "Modèle:", modeleField,
                "Type:", typeField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Appareil",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                appareil.setImei(imeiField.getText());
                appareil.setMarque(marqueField.getText());
                appareil.setModele(modeleField.getText());
                appareil.setTypeAppareil(typeField.getText());
                gestionAppareil.modifer(appareil);
                refreshTableData(table, "Appareil");
                JOptionPane.showMessageDialog(this, "Appareil modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierReparation(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Reparation reparation = gestionReparation.rechercher(id);
            if (reparation == null) {
                JOptionPane.showMessageDialog(this, "Réparation non trouvée");
                return;
            }

            List<Boutique> boutiques = gestionBoutique.lister();
            List<Reparateur> reparateurs = gestionReparateur.lister();
            String[] boutiqueList = boutiques.stream()
                .map(b -> b.getIdBoutique() + " - " + b.getNom())
                .toArray(String[]::new);
            String[] reparateurList = reparateurs.stream()
                .map(r -> r.getId() + " - " + r.getNom() + " " + r.getPrenom())
                .toArray(String[]::new);

            JComboBox<String> boutiqueCombo = new JComboBox<>(boutiqueList);
            JComboBox<String> reparateurCombo = new JComboBox<>(reparateurList);
            // Sélectionner les valeurs actuelles
            for (int i = 0; i < boutiqueList.length; i++) {
                if (boutiqueList[i].startsWith(String.valueOf(reparation.getIdBoutique()))) {
                    boutiqueCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < reparateurList.length; i++) {
                if (reparateurList[i].startsWith(String.valueOf(reparation.getIdReparateur()))) {
                    reparateurCombo.setSelectedIndex(i);
                    break;
                }
            }

            JTextField codeField = new JTextField(reparation.getCodeSuivi());
            JTextField etatField = new JTextField(reparation.getEtat());
            JTextField prixField = new JTextField(String.valueOf(reparation.getPrixTotal()));

            Object[] message = {
                "ID Boutique:", boutiqueCombo,
                "ID Réparateur:", reparateurCombo,
                "Code Suivi:", codeField,
                "État:", etatField,
                "Prix Total:", prixField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int idBoutique = Integer.parseInt(((String) boutiqueCombo.getSelectedItem()).split(" - ")[0]);
                int idReparateur = Integer.parseInt(((String) reparateurCombo.getSelectedItem()).split(" - ")[0]);
                
                reparation.setIdBoutique(idBoutique);
                reparation.setIdReparateur(idReparateur);
                reparation.setCodeSuivi(codeField.getText());
                reparation.setEtat(etatField.getText());
                reparation.setPrixTotal(Double.parseDouble(prixField.getText()));
                gestionReparation.modifer(reparation);
                refreshTableData(table, "Reparation");
                JOptionPane.showMessageDialog(this, "Réparation modifiée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void modifierReparateur(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Reparateur reparateur = gestionReparateur.rechercher(id);
            if (reparateur == null) {
                JOptionPane.showMessageDialog(this, "Réparateur non trouvé");
                return;
            }

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
                "Mot de passe:", mdpField,
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
                refreshTableData(table, "Reparateur");
                JOptionPane.showMessageDialog(this, "Réparateur modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierProprietaire(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Proprietaire proprietaire = gestionProprietaire.rechercher(id);
            if (proprietaire == null) {
                JOptionPane.showMessageDialog(this, "Propriétaire non trouvé");
                return;
            }

            JTextField nomField = new JTextField(proprietaire.getNom());
            JTextField prenomField = new JTextField(proprietaire.getPrenom());
            JTextField emailField = new JTextField(proprietaire.getEmail());
            JPasswordField mdpField = new JPasswordField();
            mdpField.setText(proprietaire.getMdp());

            Object[] message = {
                "Nom:", nomField,
                "Prénom:", prenomField,
                "Email:", emailField,
                "Mot de passe:", mdpField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Propriétaire",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                proprietaire.setNom(nomField.getText());
                proprietaire.setPrenom(prenomField.getText());
                proprietaire.setEmail(emailField.getText());
                proprietaire.setMdp(new String(mdpField.getPassword()));
                gestionProprietaire.modifer(proprietaire);
                refreshTableData(table, "Proprietaire");
                JOptionPane.showMessageDialog(this, "Propriétaire modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierBoutique(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Boutique boutique = gestionBoutique.rechercher(id);
            if (boutique == null) {
                JOptionPane.showMessageDialog(this, "Boutique non trouvée");
                return;
            }

            List<Proprietaire> proprietaires = gestionProprietaire.lister();
            String[] proprietaireList = proprietaires.stream()
                .map(p -> p.getId() + " - " + p.getNom() + " " + p.getPrenom())
                .toArray(String[]::new);
            JComboBox<String> proprietaireCombo = new JComboBox<>(proprietaireList);
            if (boutique.getProprietaire() != null) {
                for (int i = 0; i < proprietaireList.length; i++) {
                    if (proprietaireList[i].startsWith(String.valueOf(boutique.getProprietaire().getId()))) {
                        proprietaireCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            JTextField nomField = new JTextField(boutique.getNom());
            JTextField adresseField = new JTextField(boutique.getAdresse());
            JTextField telField = new JTextField(String.valueOf(boutique.getNumTel()));
            JTextField numPField = new JTextField(String.valueOf(boutique.getNumP()));

            Object[] message = {
                "ID Propriétaire:", proprietaireCombo,
                "Nom:", nomField,
                "Adresse:", adresseField,
                "Téléphone:", telField,
                "Numéro P:", numPField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Boutique",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int idProprietaire = Integer.parseInt(((String) proprietaireCombo.getSelectedItem()).split(" - ")[0]);
                Proprietaire prop = gestionProprietaire.rechercher(idProprietaire);
                boutique.setProprietaire(prop);
                boutique.setNom(nomField.getText());
                boutique.setAdresse(adresseField.getText());
                boutique.setNumTel(Integer.parseInt(telField.getText()));
                boutique.setNumP(Integer.parseInt(numPField.getText()));
                gestionBoutique.modifer(boutique);
                refreshTableData(table, "Boutique");
                JOptionPane.showMessageDialog(this, "Boutique modifiée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void modifierCaisse(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Caisse caisse = gestionCaisse.rechercher(id);
            if (caisse == null) {
                JOptionPane.showMessageDialog(this, "Caisse non trouvée");
                return;
            }

            JTextField soldeField = new JTextField(String.valueOf(caisse.getSoldeActuel()));

            Object[] message = {
                "Solde Actuel:", soldeField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Caisse",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                caisse.setSoldeActuel(Double.parseDouble(soldeField.getText()));
                caisse.setDernierMouvement(LocalDateTime.now());
                gestionCaisse.modifer(caisse);
                refreshTableData(table, "Caisse");
                JOptionPane.showMessageDialog(this, "Caisse modifiée avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierComposant(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Composant composant = gestionComposant.rechercher(id);
            if (composant == null) {
                JOptionPane.showMessageDialog(this, "Composant non trouvé");
                return;
            }

            JTextField nomField = new JTextField(composant.getNom());
            JTextField prixField = new JTextField(String.valueOf(composant.getPrix()));
            JTextField quantiteField = new JTextField(String.valueOf(composant.getQuantite()));

            Object[] message = {
                "Nom:", nomField,
                "Prix:", prixField,
                "Quantité:", quantiteField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Composant",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                composant.setNom(nomField.getText());
                composant.setPrix(Double.parseDouble(prixField.getText()));
                composant.setQuantite(Integer.parseInt(quantiteField.getText()));
                gestionComposant.modifer(composant);
                refreshTableData(table, "Composant");
                JOptionPane.showMessageDialog(this, "Composant modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierEmprunt(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Emprunt emprunt = gestionEmprunt.rechercher(id);
            if (emprunt == null) {
                JOptionPane.showMessageDialog(this, "Emprunt non trouvé");
                return;
            }

            JTextField montantField = new JTextField(String.valueOf(emprunt.getMontant()));
            JTextField typeField = new JTextField(emprunt.getType());
            JTextArea commentaireArea = new JTextArea(emprunt.getCommentaire(), 3, 20);

            Object[] message = {
                "Montant:", montantField,
                "Type:", typeField,
                "Commentaire:", new JScrollPane(commentaireArea)
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Emprunt",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                emprunt.setMontant(Double.parseDouble(montantField.getText()));
                emprunt.setType(typeField.getText());
                emprunt.setCommentaire(commentaireArea.getText());
                gestionEmprunt.modifer(emprunt);
                refreshTableData(table, "Emprunt");
                JOptionPane.showMessageDialog(this, "Emprunt modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierRecu(JTable table, int row) {
        try {
            int id = (Integer) table.getValueAt(row, 0);
            Recu recu = gestionRecu.rechercher(id);
            if (recu == null) {
                JOptionPane.showMessageDialog(this, "Reçu non trouvé");
                return;
            }

            JTextField montantField = new JTextField(String.valueOf(recu.getMontant()));

            Object[] message = {
                "Montant:", montantField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Modifier Reçu",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                recu.setMontant(Double.parseDouble(montantField.getText()));
                gestionRecu.modifer(recu);
                refreshTableData(table, "Recu");
                JOptionPane.showMessageDialog(this, "Reçu modifié avec succès!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === MÉTHODES DE CHARGEMENT DES DONNÉES DANS LES TABLES ===
    private void loadReparations(DefaultTableModel model) throws Exception {
        List<Reparation> reparations = gestionReparation.lister();
        for (Reparation r : reparations) {
            loadReparationInTable(model, r);
        }
    }

    private void loadReparationInTable(DefaultTableModel model, Reparation r) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        model.addRow(new Object[]{
            r.getIdReparation(),
            r.getIdAppareil(),
            r.getIdBoutique(),
            r.getIdReparateur(),
            r.getCodeSuivi(),
            r.getDateDepot() != null ? r.getDateDepot().format(formatter) : "N/A",
            r.getEtat(),
            r.getPrixTotal()
        });
    }

    private void loadReparateurs(DefaultTableModel model) throws Exception {
        List<Reparateur> reparateurs = gestionReparateur.lister();
        for (Reparateur r : reparateurs) {
            model.addRow(new Object[]{
                r.getId(), r.getNom(), r.getPrenom(),
                r.getEmail(), r.getMdp(), r.getPourcentageGain()
            });
        }
    }

    private void loadProprietaires(DefaultTableModel model) throws Exception {
        List<Proprietaire> proprietaires = gestionProprietaire.lister();
        for (Proprietaire p : proprietaires) {
            model.addRow(new Object[]{
                p.getId(), p.getNom(), p.getPrenom(),
                p.getEmail(), p.getMdp()
            });
        }
    }

    private void loadBoutiques(DefaultTableModel model) throws Exception {
        List<Boutique> boutiques = gestionBoutique.lister();
        for (Boutique b : boutiques) {
            int idProprietaire = (b.getProprietaire() != null) ? b.getProprietaire().getId() : 0;
            model.addRow(new Object[]{
                b.getIdBoutique(), b.getNom(), b.getAdresse(),
                b.getNumTel(), b.getNumP(), idProprietaire
            });
        }
    }

    private void loadCaisses(DefaultTableModel model) throws Exception {
        List<Caisse> caisses = gestionCaisse.lister();
        for (Caisse c : caisses) {
            loadCaisseInTable(model, c);
        }
    }

    private void loadCaisseInTable(DefaultTableModel model, Caisse c) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        int idBoutique = (c.getBoutique() != null) ? c.getBoutique().getIdBoutique() : 0;
        model.addRow(new Object[]{
            c.getIdCaisse(),
            idBoutique,
            c.getSoldeActuel(),
            c.getDernierMouvement() != null ? c.getDernierMouvement().format(formatter) : "N/A"
        });
    }

    private void loadComposants(DefaultTableModel model) throws Exception {
        List<Composant> composants = gestionComposant.lister();
        for (Composant c : composants) {
            model.addRow(new Object[]{
                c.getIdComposant(), c.getNom(), c.getPrix(), c.getQuantite()
            });
        }
    }

    private void loadEmprunts(DefaultTableModel model) throws Exception {
        List<Emprunt> emprunts = gestionEmprunt.lister();
        for (Emprunt e : emprunts) {
            loadEmpruntInTable(model, e);
        }
    }

    private void loadEmpruntInTable(DefaultTableModel model, Emprunt e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        model.addRow(new Object[]{
            e.getIdEmprunt(),
            e.getDate() != null ? e.getDate().format(formatter) : "N/A",
            e.getMontant(),
            e.getType(),
            e.getCommentaire() != null && e.getCommentaire().length() > 30 
                ? e.getCommentaire().substring(0, 30) + "..." : e.getCommentaire()
        });
    }

    private void loadRecus(DefaultTableModel model) throws Exception {
        List<Recu> recus = gestionRecu.lister();
        for (Recu r : recus) {
            loadRecuInTable(model, r);
        }
    }

    private void loadRecuInTable(DefaultTableModel model, Recu r) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        model.addRow(new Object[]{
            r.getIdRecu(),
            r.getDate() != null ? r.getDate().format(formatter) : "N/A",
            r.getMontant()
        });
    }
}
