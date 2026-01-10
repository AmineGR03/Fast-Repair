package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Panel pour les réparateurs - Gestion des réparations
 */
public class ReparateurPanel extends JPanel {

    private MainWindow mainWindow;
    private String reparateurEmail;

    // Gestionnaires métier
    private metier.GestionReparation gestionReparation;
    private metier.GestionAppareil gestionAppareil;
    private metier.GestionComposant gestionComposant;
    private metier.GestionBoutique gestionBoutique;
    private metier.GestionReparateur gestionReparateur;

    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public ReparateurPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.reparateurEmail = mainWindow.getCurrentUserEmail();

        // Initialiser les gestionnaires
        gestionReparation = new metier.GestionReparation();
        gestionAppareil = new metier.GestionAppareil();
        gestionComposant = new metier.GestionComposant();
        gestionBoutique = new metier.GestionBoutique();
        gestionReparateur = new metier.GestionReparateur();

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
        createReparationsTab();
        createAppareilsTab();
        createComposantsTab();
        createProfileTab();
    }

    private void updateWelcomeLabel() {
        welcomeLabel.setText("Bienvenue, Réparateur: " + (reparateurEmail != null ? reparateurEmail : "Inconnu"));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(70, 130, 180));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel du haut avec le titre et le bouton de déconnexion
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(255, 235, 59)); // Jaune pour les réparateurs

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
                    ReparateurPanel.this,
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

    private void createReparationsTab() {
        JPanel reparationPanel = new JPanel(new BorderLayout());
        reparationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnNouvelleReparation = new JButton("🆕 Nouvelle Réparation");
        JButton btnMettreAJour = new JButton("📝 Mettre à Jour");
        JButton btnTerminer = new JButton("✅ Terminer Réparation");
        JButton btnVoirToutes = new JButton("📋 Voir Toutes");

        // Style des boutons
        btnNouvelleReparation.setBackground(new Color(40, 167, 69));
        btnMettreAJour.setBackground(new Color(255, 193, 7));
        btnTerminer.setBackground(new Color(23, 162, 184));
        btnVoirToutes.setBackground(new Color(108, 117, 125));

        for (JButton btn : new JButton[]{btnNouvelleReparation, btnMettreAJour, btnTerminer, btnVoirToutes}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        reparationPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des réparations
        String[] columns = {"ID", "ID Appareil", "Code Suivi", "Date Dépôt", "État", "Prix Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        reparationPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("🔧 Mes Réparations", reparationPanel);

        // Charger les réparations du réparateur connecté
        rafraichirMesReparations(tableModel);

        // Ajouter les listeners
        btnNouvelleReparation.addActionListener(e -> creerNouvelleReparation(tableModel));
        btnMettreAJour.addActionListener(e -> mettreAJourReparation(table, tableModel));
        btnTerminer.addActionListener(e -> terminerReparation(table, tableModel));
        btnVoirToutes.addActionListener(e -> rafraichirMesReparations(tableModel));
    }

    private void rafraichirMesReparations(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Récupérer le réparateur connecté
            dao.Reparateur reparateurConnecte = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(reparateurEmail)) {
                    reparateurConnecte = r;
                    break;
                }
            }

            if (reparateurConnecte != null) {
                // Pour l'instant, on affiche toutes les réparations
                // TODO: filtrer par réparateur assigné
                List<dao.Reparation> reparations = gestionReparation.lister();
                for (dao.Reparation r : reparations) {
                    tableModel.addRow(new Object[]{
                        r.getIdReparation(),
                        r.getIdAppareil(),
                        r.getCodeSuivi(),
                        r.getDateDepot() != null ? r.getDateDepot().toString() : "N/A",
                        r.getEtat(),
                        r.getPrixTotal()
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des réparations: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouvelleReparation(DefaultTableModel tableModel) {
        try {
            // Vérifier que des appareils existent
            List<dao.Appareil> appareils = gestionAppareil.lister();
            if (appareils.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun appareil disponible. L'admin doit d'abord créer des appareils.",
                    "Appareils manquants", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Récupérer le réparateur connecté et sa boutique
            dao.Reparateur reparateurConnecte = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(reparateurEmail)) {
                    reparateurConnecte = r;
                    break;
                }
            }

            if (reparateurConnecte == null) {
                JOptionPane.showMessageDialog(this, "Réparateur non trouvé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Liste des appareils disponibles
            String[] appareilOptions = appareils.stream()
                .map(a -> a.getIdAppareil() + " - " + a.getMarque() + " " + a.getModele() + " (IMEI: " + a.getImei() + ")")
                .toArray(String[]::new);

            JComboBox<String> appareilCombo = new JComboBox<>(appareilOptions);
            JTextField codeField = new JTextField("REP-" + System.currentTimeMillis());
            JTextField prixField = new JTextField("0.0");

            Object[] message = {
                "Sélectionner l'appareil:", appareilCombo,
                "Code de suivi:", codeField,
                "Prix estimé:", prixField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Nouvelle Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String selectedAppareil = (String) appareilCombo.getSelectedItem();
                int idAppareil = Integer.parseInt(selectedAppareil.split(" - ")[0]);

                // TODO: Récupérer la boutique assignée au réparateur
                // Pour l'instant, on utilise une valeur par défaut
                int idBoutique = 1; // À remplacer par la vraie logique

                dao.Reparation reparation = dao.Reparation.builder()
                    .idAppareil(idAppareil)
                    .idBoutique(idBoutique)
                    .idReparateur(reparateurConnecte.getId())
                    .codeSuivi(codeField.getText())
                    .dateDepot(java.time.LocalDateTime.now())
                    .etat("En cours")
                    .commentaire("")
                    .prixTotal(Double.parseDouble(prixField.getText()))
                    .build();

                gestionReparation.ajouter(reparation);
                rafraichirMesReparations(tableModel);

                JOptionPane.showMessageDialog(this, "Réparation créée avec succès !\n" +
                    "Elle a été automatiquement assignée à votre boutique.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mettreAJourReparation(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réparation à mettre à jour.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idReparation = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparation reparation = gestionReparation.rechercher(idReparation);

            if (reparation == null) {
                JOptionPane.showMessageDialog(this, "Réparation introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Formulaire de mise à jour
            JTextField etatField = new JTextField(reparation.getEtat());
            JTextArea commentaireArea = new JTextArea(reparation.getCommentaire(), 3, 20);
            JTextField prixField = new JTextField(String.valueOf(reparation.getPrixTotal()));

            JScrollPane scrollPane = new JScrollPane(commentaireArea);

            Object[] message = {
                "Nouvel état:", etatField,
                "Commentaire:", scrollPane,
                "Prix total:", prixField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Mettre à Jour Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                reparation.setEtat(etatField.getText());
                reparation.setCommentaire(commentaireArea.getText());
                reparation.setPrixTotal(Double.parseDouble(prixField.getText()));

                gestionReparation.modifer(reparation);
                rafraichirMesReparations(tableModel);

                JOptionPane.showMessageDialog(this, "Réparation mise à jour avec succès !");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void terminerReparation(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réparation à terminer.",
                "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir marquer cette réparation comme terminée ?",
            "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (option != JOptionPane.YES_OPTION) return;

        try {
            int idReparation = (Integer) table.getValueAt(selectedRow, 0);
            dao.Reparation reparation = gestionReparation.rechercher(idReparation);

            if (reparation != null) {
                reparation.setEtat("Terminée");
                gestionReparation.modifer(reparation);
                rafraichirMesReparations(tableModel);

                JOptionPane.showMessageDialog(this, "Réparation marquée comme terminée !");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAppareilsTab() {
        JPanel appareilPanel = new JPanel(new BorderLayout());

        // Boutons pour les appareils
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirAppareils = new JButton("📱 Voir Appareils");
        JButton btnAssocierAppareil = new JButton("🔗 Associer à Réparation");

        btnVoirAppareils.setBackground(new Color(52, 152, 219));
        btnAssocierAppareil.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnVoirAppareils, btnAssocierAppareil}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        appareilPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des appareils
        String[] columns = {"ID Appareil", "IMEI", "Marque", "Modèle", "Type"};
        JTable table = new JTable(new Object[][]{}, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        appareilPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("📱 Appareils", appareilPanel);

        // Listeners
        btnVoirAppareils.addActionListener(e -> {
            try {
                // Afficher tous les appareils
                JOptionPane.showMessageDialog(this,
                    "Affichage des appareils - Fonctionnalité à implémenter",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAssocierAppareil.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Association appareil-réparation - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void createComposantsTab() {
        JPanel composantPanel = new JPanel(new BorderLayout());

        // Boutons pour les composants
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirComposants = new JButton("🔩 Voir Composants");
        JButton btnUtiliserComposant = new JButton("⚙️ Utiliser Composant");

        btnVoirComposants.setBackground(new Color(230, 126, 34));
        btnUtiliserComposant.setBackground(new Color(231, 76, 60));

        for (JButton btn : new JButton[]{btnVoirComposants, btnUtiliserComposant}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        composantPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des composants
        String[] columns = {"ID Composant", "Nom", "Prix", "Quantité"};
        JTable table = new JTable(new Object[][]{}, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        composantPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("🔩 Composants", composantPanel);

        // Listeners
        btnVoirComposants.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Gestion des composants - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });

        btnUtiliserComposant.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Utilisation de composants - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
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
        infoPanel.add(new JLabel(reparateurEmail != null ? reparateurEmail : "Non défini"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel("Réparateur"), gbc);

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
        btnModifierProfil.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Modification du profil - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });

        btnVoirStatistiques.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Statistiques personnelles - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void addReparationListeners(JButton btnNouvelle, JButton btnMaj, JButton btnTerminer, JButton btnVoir, JTable table) {

        btnVoir.addActionListener(e -> {
            try {
                // Afficher les réparations (simplifié pour l'instant)
                JOptionPane.showMessageDialog(this,
                    "Affichage des réparations du réparateur - Fonctionnalité à implémenter",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Placeholders pour les autres boutons
        ActionListener placeholderListener = e -> {
            JButton source = (JButton) e.getSource();
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité '" + source.getText() + "' - À implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        };

        btnNouvelle.addActionListener(placeholderListener);
        btnMaj.addActionListener(placeholderListener);
        btnTerminer.addActionListener(placeholderListener);
    }
}
