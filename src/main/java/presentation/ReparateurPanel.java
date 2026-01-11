package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel pour les réparateurs - Gestion des réparations
 */
public class ReparateurPanel extends JPanel {

    private MainWindow mainWindow;

    // Gestionnaires métier
    private metier.GestionReparation gestionReparation;
    private metier.GestionAppareil gestionAppareil;
    private metier.GestionComposant gestionComposant;
    private metier.GestionBoutique gestionBoutique;
    private metier.GestionReparateur gestionReparateur;
    private metier.GestionEmprunt gestionEmprunt;
    private metier.GestionCaisse gestionCaisse;

    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public ReparateurPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        // Initialiser les gestionnaires
        gestionReparation = new metier.GestionReparation();
        gestionAppareil = new metier.GestionAppareil();
        gestionComposant = new metier.GestionComposant();
        gestionBoutique = new metier.GestionBoutique();
        gestionReparateur = new metier.GestionReparateur();
        gestionEmprunt = new metier.GestionEmprunt();
        gestionCaisse = new metier.GestionCaisse();

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
        createCaisseTab();
        createCRUDTab();
        createProfileTab();
    }

    private void updateWelcomeLabel() {
        String email = mainWindow.getCurrentUserEmail();
        welcomeLabel.setText("Bienvenue, Réparateur: " + (email != null ? email : "Inconnu"));
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

    /**
     * Méthode appelée quand l'utilisateur se connecte pour rafraîchir les données
     */
    public void onUserLoggedIn() {
        // Rafraîchir les données qui dépendent de l'utilisateur connecté
        // Pour l'instant, les données se rafraîchissent automatiquement quand les onglets sont sélectionnés
    }

    private void createReparationsTab() {
        JPanel reparationPanel = new JPanel(new BorderLayout());
        reparationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnNouvelleReparation = new JButton("🆕 Nouvelle Réparation");
        JButton btnCreerAppareil = new JButton("📱 Créer Appareil");
        JButton btnMettreAJour = new JButton("📝 Mettre à Jour");
        JButton btnTerminer = new JButton("✅ Terminer Réparation");
        JButton btnVoirToutes = new JButton("📋 Voir Toutes");

        // Style des boutons
        btnNouvelleReparation.setBackground(new Color(40, 167, 69));
        btnCreerAppareil.setBackground(new Color(52, 152, 219));
        btnMettreAJour.setBackground(new Color(255, 193, 7));
        btnTerminer.setBackground(new Color(23, 162, 184));
        btnVoirToutes.setBackground(new Color(108, 117, 125));

        for (JButton btn : new JButton[]{btnNouvelleReparation, btnCreerAppareil, btnMettreAJour, btnTerminer, btnVoirToutes}) {
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
        btnCreerAppareil.addActionListener(e -> creerNouvelAppareil(null));
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
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateurConnecte = r;
                    break;
                }
            }

            if (reparateurConnecte != null) {
                // Filtrer les réparations par réparateur connecté
                List<dao.Reparation> reparations = gestionReparation.lister();
                for (dao.Reparation r : reparations) {
                    if (r.getIdReparateur() == reparateurConnecte.getId()) {
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
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
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

            // Récupérer les boutiques disponibles pour ce réparateur
            List<dao.Boutique> boutiques = gestionBoutique.lister();
            if (boutiques.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune boutique disponible. Le propriétaire doit d'abord créer des boutiques.",
                    "Boutiques manquantes", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Utiliser automatiquement la boutique assignée au réparateur
            dao.Boutique boutiqueAssignee = reparateurConnecte.getBoutique();
            if (boutiqueAssignee == null) {
                JOptionPane.showMessageDialog(this, "Vous n'êtes pas assigné à une boutique. Contactez votre propriétaire.",
                    "Boutique non assignée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<String> appareilCombo = new JComboBox<>(appareilOptions);
            JTextField codeField = new JTextField("REP-" + System.currentTimeMillis());
            JTextField prixField = new JTextField("0.0");

            Object[] message = {
                "Sélectionner l'appareil:", appareilCombo,
                "Code de suivi:", codeField,
                "Prix estimé:", prixField,
                "Boutique assignée: " + boutiqueAssignee.getNom() + " (" + boutiqueAssignee.getAdresse() + ")"
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Nouvelle Réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String selectedAppareil = (String) appareilCombo.getSelectedItem();

                int idAppareil = Integer.parseInt(selectedAppareil.split(" - ")[0]);
                int idBoutique = boutiqueAssignee.getIdBoutique();

                // Récupérer le nom de la boutique pour le message
                String nomBoutique = boutiqueAssignee.getNom();

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
                    "Elle a été assignée à la boutique '" + nomBoutique + "'.");
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
                // Marquer la réparation comme terminée
                reparation.setEtat("Terminée");
                gestionReparation.modifer(reparation);

                // Ajouter automatiquement le montant à la caisse de la boutique
                try {
                    ajouterMontantALaCaisse(reparation);
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

                rafraichirMesReparations(tableModel);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ajoute automatiquement le montant d'une réparation terminée à la caisse de la boutique
     */
    private void ajouterMontantALaCaisse(dao.Reparation reparation) throws Exception {
        if (reparation == null) {
            throw new IllegalArgumentException("Réparation invalide (null)");
        }

        if (reparation.getPrixTotal() <= 0) {
            System.out.println("INFO: Réparation ID " + reparation.getIdReparation() +
                             " terminée mais montant nul ou négatif (" + reparation.getPrixTotal() +
                             "€) - Aucun ajout à la caisse");
            return; // Ne pas lever d'exception pour les réparations gratuites
        }

        System.out.println("INFO: Ajout automatique de " + reparation.getPrixTotal() +
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

        System.out.println("✅ PAIEMENT AUTO - Réparation ID " + reparation.getIdReparation() +
                          " | Boutique: " + boutique.getNom() +
                          " | Montant: " + String.format("%.2f€", montantAjoute) +
                          " | Solde: " + String.format("%.2f€", ancienSolde) +
                          " → " + String.format("%.2f€", nouveauSolde));
    }

    private void createAppareilsTab() {
        JPanel appareilPanel = new JPanel(new BorderLayout());

        // Boutons pour les appareils
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirAppareils = new JButton("📱 Voir Appareils");
        JButton btnCreerAppareil = new JButton("➕ Créer Appareil");
        JButton btnAssocierAppareil = new JButton("🔗 Associer à Réparation");

        btnVoirAppareils.setBackground(new Color(52, 152, 219));
        btnCreerAppareil.setBackground(new Color(40, 167, 69));
        btnAssocierAppareil.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnVoirAppareils, btnCreerAppareil, btnAssocierAppareil}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        appareilPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des appareils
        String[] columns = {"ID Appareil", "IMEI", "Marque", "Modèle", "Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        appareilPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("📱 Appareils", appareilPanel);

        // Listeners
        btnVoirAppareils.addActionListener(e -> voirAppareils(table));
        btnCreerAppareil.addActionListener(e -> creerNouvelAppareil(table));
        btnAssocierAppareil.addActionListener(e -> associerAppareilAReparation());
    }

    private void voirAppareils(JTable table) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Clear existing data

            List<dao.Appareil> appareils = gestionAppareil.lister();

            for (dao.Appareil appareil : appareils) {
                model.addRow(new Object[]{
                    appareil.getIdAppareil(),
                    appareil.getImei() != null ? appareil.getImei() : "",
                    appareil.getMarque() != null ? appareil.getMarque() : "",
                    appareil.getModele() != null ? appareil.getModele() : "",
                    appareil.getTypeAppareil() != null ? appareil.getTypeAppareil() : ""
                });
            }

            if (appareils.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun appareil trouvé dans la base de données.\n\n" +
                    "Vous pouvez créer un nouvel appareil en utilisant le bouton '➕ Créer Appareil'.",
                    "Aucun appareil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Liste des appareils mise à jour avec succès!\nNombre d'appareils: " + appareils.size(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des appareils: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouvelAppareil(JTable table) {
        try {
            // Formulaire de création d'appareil
            JTextField imeiField = new JTextField();
            JTextField marqueField = new JTextField();
            JTextField modeleField = new JTextField();
            JTextField typeField = new JTextField();

            Object[] message = {
                "IMEI (optionnel):", imeiField,
                "Marque:", marqueField,
                "Modèle:", modeleField,
                "Type d'appareil:", typeField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Créer un Nouvel Appareil",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                // Validation des champs obligatoires
                if (marqueField.getText().trim().isEmpty() || modeleField.getText().trim().isEmpty() ||
                    typeField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Les champs Marque, Modèle et Type sont obligatoires.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Créer l'appareil
                dao.Appareil nouvelAppareil = dao.Appareil.builder()
                    .imei(imeiField.getText().trim().isEmpty() ? null : imeiField.getText().trim())
                    .marque(marqueField.getText().trim())
                    .modele(modeleField.getText().trim())
                    .typeAppareil(typeField.getText().trim())
                    .build();

                gestionAppareil.ajouter(nouvelAppareil);

                JOptionPane.showMessageDialog(this,
                    "Appareil créé avec succès !\n" +
                    "Marque: " + nouvelAppareil.getMarque() + "\n" +
                    "Modèle: " + nouvelAppareil.getModele() + "\n" +
                    "Type: " + nouvelAppareil.getTypeAppareil(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Rafraîchir la table si elle est fournie
                if (table != null) {
                    voirAppareils(table);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de l'appareil: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerNouveauComposant(JTable table) {
        try {
            // Formulaire de création de composant
            JTextField nomField = new JTextField();
            JTextField prixField = new JTextField();
            JTextField quantiteField = new JTextField();

            Object[] message = {
                "Nom du composant:", nomField,
                "Prix (€):", prixField,
                "Quantité en stock:", quantiteField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Créer un Nouveau Composant",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                // Validation des champs obligatoires
                if (nomField.getText().trim().isEmpty() || prixField.getText().trim().isEmpty() ||
                    quantiteField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.",
                        "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double prix = Double.parseDouble(prixField.getText().trim());
                    int quantite = Integer.parseInt(quantiteField.getText().trim());

                    if (prix < 0) {
                        JOptionPane.showMessageDialog(this, "Le prix ne peut pas être négatif.",
                            "Prix invalide", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (quantite < 0) {
                        JOptionPane.showMessageDialog(this, "La quantité ne peut pas être négative.",
                            "Quantité invalide", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Créer le composant
                    dao.Composant nouveauComposant = dao.Composant.builder()
                        .nom(nomField.getText().trim())
                        .prix(prix)
                        .quantite(quantite)
                        .build();

                    gestionComposant.ajouter(nouveauComposant);

                    JOptionPane.showMessageDialog(this,
                        "Composant créé avec succès !\n" +
                        "Nom: " + nouveauComposant.getNom() + "\n" +
                        "Prix: " + String.format("%.2f €", nouveauComposant.getPrix()) + "\n" +
                        "Quantité: " + nouveauComposant.getQuantite(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Rafraîchir la table si elle est fournie
                    if (table != null) {
                        voirComposants(table);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Le prix doit être un nombre décimal et la quantité un nombre entier.",
                        "Format invalide", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création du composant: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void associerAppareilAReparation() {
        try {
            // Récupérer les réparations en cours
            List<dao.Reparation> reparations = gestionReparation.lister();
            reparations = reparations.stream()
                .filter(r -> !"Terminée".equals(r.getEtat()) && !"Annulée".equals(r.getEtat()))
                .collect(Collectors.toList());

            if (reparations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucune réparation en cours trouvée.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Récupérer les appareils disponibles
            List<dao.Appareil> appareils = gestionAppareil.lister();

            // Créer les listes pour les combo boxes
            String[] reparationOptions = reparations.stream()
                .map(r -> "Réparation #" + r.getIdReparation() + " (Appareil ID: " + r.getIdAppareil() + ")")
                .toArray(String[]::new);

            String[] appareilOptions = appareils.stream()
                .map(a -> a.getIdAppareil() + " - " + a.getMarque() + " " + a.getModele() + " (IMEI: " + a.getImei() + ")")
                .toArray(String[]::new);

            JComboBox<String> reparationCombo = new JComboBox<>(reparationOptions);
            JComboBox<String> appareilCombo = new JComboBox<>(appareilOptions);

            Object[] message = {
                "Sélectionner la réparation:", reparationCombo,
                "Sélectionner le nouvel appareil:", appareilCombo
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                "Associer un nouvel appareil à une réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int selectedReparationIndex = reparationCombo.getSelectedIndex();
                int selectedAppareilIndex = appareilCombo.getSelectedIndex();

                dao.Reparation reparation = reparations.get(selectedReparationIndex);
                int newAppareilId = appareils.get(selectedAppareilIndex).getIdAppareil();

                // Mettre à jour l'appareil de la réparation
                reparation.setIdAppareil(newAppareilId);
                gestionReparation.modifer(reparation);

                JOptionPane.showMessageDialog(this,
                    "Appareil associé à la réparation avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'association: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createComposantsTab() {
        JPanel composantPanel = new JPanel(new BorderLayout());

        // Boutons pour les composants
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirComposants = new JButton("🔩 Voir Composants");
        JButton btnCreerComposant = new JButton("➕ Créer Composant");
        JButton btnUtiliserComposant = new JButton("⚙️ Assigner à Réparation");

        btnVoirComposants.setBackground(new Color(230, 126, 34));
        btnCreerComposant.setBackground(new Color(40, 167, 69));
        btnUtiliserComposant.setBackground(new Color(231, 76, 60));

        for (JButton btn : new JButton[]{btnVoirComposants, btnCreerComposant, btnUtiliserComposant}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        composantPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des composants
        String[] columns = {"ID Composant", "Nom", "Prix", "Quantité"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        composantPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("🔩 Composants", composantPanel);

        // Listeners
        btnVoirComposants.addActionListener(e -> voirComposants(table));
        btnCreerComposant.addActionListener(e -> creerNouveauComposant(table));
        btnUtiliserComposant.addActionListener(e -> assignerComposantAReparation());
    }

    private void voirComposants(JTable table) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Clear existing data

            List<dao.Composant> composants = gestionComposant.lister();
            for (dao.Composant composant : composants) {
                model.addRow(new Object[]{
                    composant.getIdComposant(),
                    composant.getNom(),
                    composant.getPrix(),
                    composant.getQuantite()
                });
            }

            if (composants.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun composant trouvé dans la base de données.\n\n" +
                    "Vous pouvez créer un nouveau composant en utilisant le bouton '➕ Créer Composant'.",
                    "Aucun composant", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Liste des composants mise à jour avec succès!\nNombre de composants: " + composants.size(),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des composants: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignerComposantAReparation() {
        try {
            // Récupérer les composants disponibles
            List<dao.Composant> composants = gestionComposant.lister();
            composants = composants.stream()
                .filter(c -> c.getQuantite() > 0)
                .collect(Collectors.toList());

            if (composants.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun composant disponible en stock.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Récupérer les réparations en cours
            List<dao.Reparation> reparations = gestionReparation.lister();
            reparations = reparations.stream()
                .filter(r -> !"Terminée".equals(r.getEtat()) && !"Annulée".equals(r.getEtat()))
                .collect(Collectors.toList());

            if (reparations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucune réparation en cours trouvée.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Créer les listes pour les combo boxes
            String[] composantOptions = composants.stream()
                .map(c -> c.getIdComposant() + " - " + c.getNom() + " (Stock: " + c.getQuantite() + ")")
                .toArray(String[]::new);

            String[] reparationOptions = reparations.stream()
                .map(r -> "Réparation #" + r.getIdReparation() + " (Appareil ID: " + r.getIdAppareil() + ")")
                .toArray(String[]::new);

            JComboBox<String> composantCombo = new JComboBox<>(composantOptions);
            JComboBox<String> reparationCombo = new JComboBox<>(reparationOptions);
            JTextField quantiteField = new JTextField("1");

            Object[] message = {
                "Sélectionner le composant:", composantCombo,
                "Sélectionner la réparation:", reparationCombo,
                "Quantité à utiliser:", quantiteField
            };

            int option = JOptionPane.showConfirmDialog(this, message,
                "Utiliser un composant dans une réparation",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                int selectedComposantIndex = composantCombo.getSelectedIndex();
                int selectedReparationIndex = reparationCombo.getSelectedIndex();

                dao.Composant composant = composants.get(selectedComposantIndex);
                dao.Reparation reparation = reparations.get(selectedReparationIndex);

                int quantiteUtilisee = Integer.parseInt(quantiteField.getText());

                // Vérifier la quantité disponible
                if (quantiteUtilisee > composant.getQuantite()) {
                    JOptionPane.showMessageDialog(this,
                        "Quantité insuffisante en stock. Stock disponible: " + composant.getQuantite(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mettre à jour le stock du composant
                composant.setQuantite(composant.getQuantite() - quantiteUtilisee);
                gestionComposant.modifer(composant);

                // Ajouter le coût du composant au prix total de la réparation
                double coutComposant = composant.getPrix() * quantiteUtilisee;
                reparation.setPrixTotal(reparation.getPrixTotal() + coutComposant);
                gestionReparation.modifer(reparation);

                JOptionPane.showMessageDialog(this,
                    String.format("Composant utilisé avec succès!\n" +
                        "Quantité utilisée: %d\n" +
                        "Coût ajouté: %.2f €\n" +
                        "Nouveau prix total de la réparation: %.2f €",
                        quantiteUtilisee, coutComposant, reparation.getPrixTotal()),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir une quantité valide.",
                "Erreur de format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'utilisation du composant: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCRUDTab() {
        JPanel crudPanel = new JPanel(new BorderLayout());
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Onglets pour les différentes entités accessibles au réparateur
        JTabbedPane crudTabbedPane = new JTabbedPane();

        // Onglet Réparations (CRUD complet)
        crudTabbedPane.addTab("🔧 Réparations", createReparateurCrudPanel("Reparation"));

        // Onglet Appareils (Lecture seule)
        crudTabbedPane.addTab("📱 Appareils", createReadOnlyPanel("Appareil"));

        // Onglet Composants (Lecture seule)
        crudTabbedPane.addTab("🔩 Composants", createReadOnlyPanel("Composant"));

        // Note: La gestion des caisses se fait via l'onglet dédié "💰 Caisse"

        crudPanel.add(crudTabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("⚙️ Gestion", crudPanel);
    }

    private JPanel createReparateurCrudPanel(String entityName) {
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
        String[] columns = {"ID", "ID Appareil", "ID Boutique", "Code Suivi", "Date Dépôt", "État", "Prix Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Charger les données initiales
        try {
            loadReparateurReparations(tableModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Ajouter les listeners aux boutons
        addReparateurCrudListeners(btnAjouter, btnModifier, btnSupprimer, btnRechercher, btnLister, table, tableModel);

        return panel;
    }

    private JPanel createReadOnlyPanel(String entityName) {
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
            case "Appareil":
                columns = new String[]{"ID Appareil", "IMEI", "Marque", "Modèle", "Type"};
                break;
            case "Composant":
                columns = new String[]{"ID Composant", "Nom", "Prix", "Quantité"};
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
            loadReadOnlyData(tableModel, entityName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Listener pour rafraîchir
        btnRafraichir.addActionListener(e -> {
            try {
                tableModel.setRowCount(0);
                loadReadOnlyData(tableModel, entityName);
                JOptionPane.showMessageDialog(this, "Données mises à jour!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du rafraîchissement: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void loadReparateurReparations(DefaultTableModel model) throws Exception {
        // Récupérer le réparateur connecté
        dao.Reparateur reparateurConnecte = null;
        List<dao.Reparateur> reparateurs = gestionReparateur.lister();
        for (dao.Reparateur r : reparateurs) {
            if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                reparateurConnecte = r;
                break;
            }
        }

        if (reparateurConnecte != null) {
            List<dao.Reparation> reparations = gestionReparation.lister();
            for (dao.Reparation r : reparations) {
                if (r.getIdReparateur() == reparateurConnecte.getId()) {
                    model.addRow(new Object[]{
                        r.getIdReparation(),
                        r.getIdAppareil(),
                        r.getIdBoutique(),
                        r.getCodeSuivi(),
                        r.getDateDepot() != null ? r.getDateDepot().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                        r.getEtat(),
                        r.getPrixTotal()
                    });
                }
            }
        }
    }

    private void loadReadOnlyData(DefaultTableModel model, String entityName) throws Exception {
        switch (entityName) {
            case "Appareil":
                List<dao.Appareil> appareils = gestionAppareil.lister();
                for (dao.Appareil a : appareils) {
                    model.addRow(new Object[]{
                        a.getIdAppareil(),
                        a.getImei() != null ? a.getImei() : "",
                        a.getMarque() != null ? a.getMarque() : "",
                        a.getModele() != null ? a.getModele() : "",
                        a.getTypeAppareil() != null ? a.getTypeAppareil() : ""
                    });
                }
                break;
            case "Composant":
                List<dao.Composant> composants = gestionComposant.lister();
                for (dao.Composant c : composants) {
                    model.addRow(new Object[]{
                        c.getIdComposant(),
                        c.getNom(),
                        c.getPrix(),
                        c.getQuantite()
                    });
                }
                break;
        }
    }

    private void addReparateurCrudListeners(JButton btnAjouter, JButton btnModifier, JButton btnSupprimer,
                                          JButton btnRechercher, JButton btnLister, JTable table, DefaultTableModel tableModel) {

        btnLister.addActionListener(e -> {
            try {
                tableModel.setRowCount(0);
                loadReparateurReparations(tableModel);
                JOptionPane.showMessageDialog(this, "Liste mise à jour avec succès!",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du listage: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAjouter.addActionListener(e -> ajouterReparationReparateur(tableModel));
        btnModifier.addActionListener(e -> modifierReparationReparateur(table, tableModel));
        btnSupprimer.addActionListener(e -> supprimerReparationReparateur(table, tableModel));
        btnRechercher.addActionListener(e -> rechercherReparationReparateur(tableModel));
    }

    private void ajouterReparationReparateur(DefaultTableModel tableModel) {
        // Utiliser la méthode existante
        creerNouvelleReparation(tableModel);
    }

    private void modifierReparationReparateur(JTable table, DefaultTableModel tableModel) {
        // Utiliser la méthode existante
        mettreAJourReparation(table, tableModel);
    }

    private void supprimerReparationReparateur(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une réparation à supprimer",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cette réparation ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (option != JOptionPane.YES_OPTION) return;

        try {
            int idReparation = (Integer) table.getValueAt(selectedRow, 0);
            gestionReparation.supprimer(idReparation);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Réparation supprimée avec succès!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechercherReparationReparateur(DefaultTableModel tableModel) {
        String codeStr = JOptionPane.showInputDialog(this, "Entrez le code de suivi à rechercher:",
            "Recherche", JOptionPane.QUESTION_MESSAGE);

        if (codeStr == null || codeStr.trim().isEmpty()) return;

        try {
            tableModel.setRowCount(0);
            List<dao.Reparation> reparations = gestionReparation.filtrerParCodeSuivi(codeStr);

            // Récupérer le réparateur connecté pour filtrer
            dao.Reparateur reparateurConnecte = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateurConnecte = r;
                    break;
                }
            }

            if (reparateurConnecte != null) {
                for (dao.Reparation r : reparations) {
                    if (r.getIdReparateur() == reparateurConnecte.getId()) {
                        tableModel.addRow(new Object[]{
                            r.getIdReparation(),
                            r.getIdAppareil(),
                            r.getIdBoutique(),
                            r.getCodeSuivi(),
                            r.getDateDepot() != null ? r.getDateDepot().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                            r.getEtat(),
                            r.getPrixTotal()
                        });
                    }
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Aucune réparation trouvée avec ce code de suivi.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
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
        JButton btnVoirBoutique = new JButton("🏪 Ma Boutique");

        btnModifierProfil.setBackground(new Color(255, 193, 7));
        btnVoirStatistiques.setBackground(new Color(52, 152, 219));
        btnVoirBoutique.setBackground(new Color(46, 204, 113));

        for (JButton btn : new JButton[]{btnModifierProfil, btnVoirStatistiques, btnVoirBoutique}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            actionPanel.add(btn);
        }

        profilePanel.add(actionPanel, BorderLayout.CENTER);

        tabbedPane.addTab("👤 Profil", profilePanel);

        // Listeners
        btnModifierProfil.addActionListener(e -> modifierProfilReparateur());

        btnVoirStatistiques.addActionListener(e -> voirStatistiquesReparateur());

        btnVoirBoutique.addActionListener(e -> voirMaBoutique());
    }

    private void modifierProfilReparateur() {
        try {
            // Récupérer le réparateur connecté
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null) {
                JOptionPane.showMessageDialog(this, "Réparateur non trouvé.",
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

                // Mettre à jour le réparateur
                reparateur.setNom(nomField.getText().trim());
                reparateur.setPrenom(prenomField.getText().trim());
                reparateur.setEmail(emailField.getText().trim());

                String newPassword = new String(mdpField.getPassword());
                if (!newPassword.isEmpty()) {
                    reparateur.setMdp(newPassword);
                }

                try {
                    reparateur.setPourcentageGain(Double.parseDouble(pourcentageField.getText().trim()));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Le pourcentage doit être un nombre valide.",
                        "Format invalide", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                gestionReparateur.modifer(reparateur);

                // Mettre à jour l'email dans la session si changé
                mainWindow.setCurrentUser(mainWindow.getCurrentUserRole(), reparateur.getEmail());
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

    private void voirStatistiquesReparateur() {
        try {
            // Récupérer le réparateur connecté
            final dao.Reparateur reparateurConnecte;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            dao.Reparateur tempReparateur = null;
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    tempReparateur = r;
                    break;
                }
            }
            reparateurConnecte = tempReparateur;

            if (reparateurConnecte == null) {
                JOptionPane.showMessageDialog(this, "Réparateur non trouvé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final int reparateurId = reparateurConnecte.getId();

            // Calculer les statistiques du réparateur
            List<dao.Reparation> toutesReparations = gestionReparation.lister();
            List<dao.Reparation> mesReparations = toutesReparations.stream()
                .filter(r -> r.getIdReparateur() == reparateurId)
                .collect(java.util.stream.Collectors.toList());

            int nbReparationsTotal = mesReparations.size();
            int nbReparationsTerminees = (int) mesReparations.stream()
                .filter(r -> "Terminée".equals(r.getEtat()))
                .count();
            int nbReparationsEnCours = (int) mesReparations.stream()
                .filter(r -> !"Terminée".equals(r.getEtat()) && !"Annulée".equals(r.getEtat()))
                .count();

            double revenuTotal = mesReparations.stream()
                .mapToDouble(dao.Reparation::getPrixTotal)
                .sum();

            double gainPersonnel = revenuTotal * (reparateurConnecte.getPourcentageGain() / 100.0);

            // Créer le message des statistiques
            StringBuilder stats = new StringBuilder();
            stats.append("=== STATISTIQUES PERSONNELLES ===\n\n");
            stats.append("🔧 Réparations totales: ").append(nbReparationsTotal).append("\n");
            stats.append("✅ Réparations terminées: ").append(nbReparationsTerminees).append("\n");
            stats.append("🔄 Réparations en cours: ").append(nbReparationsEnCours).append("\n");
            stats.append(String.format("💰 Revenus générés: %.2f €\n", revenuTotal));
            stats.append(String.format("💵 Gain personnel (%.1f%%): %.2f €\n",
                reparateurConnecte.getPourcentageGain(), gainPersonnel));

            if (nbReparationsTotal > 0) {
                double tauxCompletion = (double) nbReparationsTerminees / nbReparationsTotal * 100.0;
                stats.append(String.format("📊 Taux de completion: %.1f%%\n", tauxCompletion));
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

    private void voirMaBoutique() {
        try {
            // Récupérer le réparateur connecté
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null) {
                JOptionPane.showMessageDialog(this, "Réparateur non trouvé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dao.Boutique boutique = reparateur.getBoutique();
            if (boutique == null) {
                JOptionPane.showMessageDialog(this, "Vous n'êtes pas assigné à une boutique.\n" +
                    "Contactez votre propriétaire pour être assigné à une boutique.",
                    "Boutique non assignée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Créer le message d'information de la boutique
            StringBuilder infoBoutique = new StringBuilder();
            infoBoutique.append("🏪 INFORMATIONS DE MA BOUTIQUE\n\n");

            infoBoutique.append("📍 Nom: ").append(boutique.getNom()).append("\n");
            infoBoutique.append("📍 Adresse: ").append(boutique.getAdresse()).append("\n");
            infoBoutique.append("📞 Téléphone: ").append(boutique.getNumTel()).append("\n");
            infoBoutique.append("🏷️ Numéro propriétaire: ").append(boutique.getNumP()).append("\n\n");

            // Informations du propriétaire
            dao.Proprietaire proprietaire = boutique.getProprietaire();
            if (proprietaire != null) {
                infoBoutique.append("👤 PROPRIÉTAIRE\n\n");
                infoBoutique.append("📝 Nom: ").append(proprietaire.getNom()).append(" ").append(proprietaire.getPrenom()).append("\n");
                infoBoutique.append("📧 Email: ").append(proprietaire.getEmail()).append("\n\n");
            }

            // Informations de la caisse
            dao.Caisse caisse = boutique.getCaisse();
            if (caisse != null) {
                infoBoutique.append("💰 CAISSE\n\n");
                infoBoutique.append("💵 Solde actuel: ").append(String.format("%.2f €", caisse.getSoldeActuel())).append("\n");
                if (caisse.getDernierMouvement() != null) {
                    infoBoutique.append("📅 Dernier mouvement: ").append(caisse.getDernierMouvement().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                }
            } else {
                infoBoutique.append("💰 CAISSE\n\n");
                infoBoutique.append("⚠️ Aucune caisse associée à cette boutique.\n");
            }

            // Créer une boîte de dialogue avec scroll pour les longues informations
            JTextArea textArea = new JTextArea(infoBoutique.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBackground(new Color(248, 249, 250));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane,
                "Informations de ma Boutique",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération des informations de la boutique: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCaisseTab() {
        JPanel caissePanel = new JPanel(new BorderLayout());
        caissePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirSolde = new JButton("💰 Voir Solde");
        JButton btnAjouterFonds = new JButton("➕ Ajouter Fonds");
        JButton btnRetirerFonds = new JButton("➖ Retirer Fonds");
        JButton btnHistorique = new JButton("📋 Historique");

        // Style des boutons
        btnVoirSolde.setBackground(new Color(46, 204, 113));
        btnAjouterFonds.setBackground(new Color(52, 152, 219));
        btnRetirerFonds.setBackground(new Color(230, 126, 34));
        btnHistorique.setBackground(new Color(155, 89, 182));

        for (JButton btn : new JButton[]{btnVoirSolde, btnAjouterFonds, btnRetirerFonds, btnHistorique}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(140, 35));
            buttonPanel.add(btn);
        }

        caissePanel.add(buttonPanel, BorderLayout.NORTH);

        // Table pour afficher l'historique des mouvements
        String[] columns = {"Date", "Type", "Montant", "Commentaire", "Solde après"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        caissePanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("💰 Caisse", caissePanel);

        // Charger l'historique initial
        rafraichirHistoriqueCaisse(tableModel);

        // Ajouter les listeners
        btnVoirSolde.addActionListener(e -> voirSoldeCaisse());
        btnAjouterFonds.addActionListener(e -> ajouterFondsCaisse(tableModel));
        btnRetirerFonds.addActionListener(e -> retirerFondsCaisse(tableModel));
        btnHistorique.addActionListener(e -> rafraichirHistoriqueCaisse(tableModel));
    }

    private void voirSoldeCaisse() {
        try {
            // Récupérer le réparateur connecté et sa boutique
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null || reparateur.getBoutique() == null) {
                JOptionPane.showMessageDialog(this, "Vous n'êtes pas assigné à une boutique.",
                    "Boutique non trouvée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Récupérer la caisse en cherchant dans toutes les caisses de la boutique
            System.out.println("DEBUG: Recherche caisse pour boutique ID: " + reparateur.getBoutique().getIdBoutique());
            List<dao.Caisse> toutesCaisses = gestionCaisse.lister();
            System.out.println("DEBUG: Nombre total de caisses: " + toutesCaisses.size());

            dao.Caisse caisse = null;
            for (dao.Caisse c : toutesCaisses) {
                System.out.println("DEBUG: Caisse ID: " + c.getIdCaisse() +
                                 ", Boutique ID: " + (c.getBoutique() != null ? c.getBoutique().getIdBoutique() : "null") +
                                 ", Solde: " + c.getSoldeActuel());
                if (c.getBoutique() != null && c.getBoutique().getIdBoutique() == reparateur.getBoutique().getIdBoutique()) {
                    caisse = c;
                    System.out.println("DEBUG: Caisse trouvée ! Solde: " + caisse.getSoldeActuel());
                    break;
                }
            }

            if (caisse == null) {
                System.out.println("DEBUG: Aucune caisse trouvée pour la boutique !");
                JOptionPane.showMessageDialog(this, "Aucune caisse trouvée pour votre boutique.",
                    "Caisse introuvable", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Forcer le rafraîchissement des données depuis la base
            try {
                gestionCaisse.rechercher(caisse.getIdCaisse());
                List<dao.Caisse> caissesFraiches = gestionCaisse.lister();
                dao.Caisse caisseFraiche = null;
                for (dao.Caisse c : caissesFraiches) {
                    if (c.getIdCaisse() == caisse.getIdCaisse()) {
                        caisseFraiche = c;
                        break;
                    }
                }
                if (caisseFraiche != null) {
                    caisse = caisseFraiche;
                    System.out.println("DEBUG: Caisse rafraîchie - Solde actuel: " + caisse.getSoldeActuel() + "€");
                }
            } catch (Exception refreshError) {
                System.out.println("DEBUG: Impossible de rafraîchir la caisse: " + refreshError.getMessage());
            }

            String message = String.format(
                "État de la caisse de %s:\n\n" +
                "Solde actuel: %.2f €\n" +
                "Dernier mouvement: %s",
                reparateur.getBoutique().getNom(),
                caisse.getSoldeActuel(),
                caisse.getDernierMouvement() != null ? caisse.getDernierMouvement().toString() : "Aucun mouvement"
            );

            System.out.println("DEBUG: Affichage solde caisse - " + message.replace("\n", " | "));
            JOptionPane.showMessageDialog(this, message, "Solde de la Caisse",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du solde: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterFondsCaisse(DefaultTableModel tableModel) {
        try {
            // Récupérer le réparateur connecté et sa boutique
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null || reparateur.getBoutique() == null) {
                JOptionPane.showMessageDialog(this, "Vous n'êtes pas assigné à une boutique.",
                    "Boutique non trouvée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.Caisse caisse = reparateur.getBoutique().getCaisse();
            if (caisse == null) {
                JOptionPane.showMessageDialog(this, "Aucune caisse trouvée pour votre boutique.",
                    "Caisse introuvable", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField montantField = new JTextField();
            JTextField commentaireField = new JTextField();

            Object[] message = {
                "Montant à ajouter (€):", montantField,
                "Commentaire:", commentaireField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Ajouter des Fonds",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    double montant = Double.parseDouble(montantField.getText().trim());
                    String commentaire = commentaireField.getText().trim();

                    if (montant <= 0) {
                        JOptionPane.showMessageDialog(this, "Le montant doit être positif.",
                            "Montant invalide", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    System.out.println("REPARATEUR_PANEL - Appel ajouterFondsCaisse: Caisse " + caisse.getIdCaisse() +
                                     " | Montant: " + montant + "€ | Solde avant: " + caisse.getSoldeActuel() + "€");
                    gestionEmprunt.ajouterFondsCaisse(caisse.getIdCaisse(), reparateur.getId(), montant, commentaire);

                    JOptionPane.showMessageDialog(this, String.format("Fonds ajoutés avec succès !\n%.2f € ont été ajoutés à la caisse.", montant),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                    rafraichirHistoriqueCaisse(tableModel);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Le montant doit être un nombre valide.",
                        "Format invalide", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de fonds: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retirerFondsCaisse(DefaultTableModel tableModel) {
        try {
            // Récupérer le réparateur connecté et sa boutique
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null || reparateur.getBoutique() == null) {
                JOptionPane.showMessageDialog(this, "Vous n'êtes pas assigné à une boutique.",
                    "Boutique non trouvée", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.Caisse caisse = reparateur.getBoutique().getCaisse();
            if (caisse == null) {
                JOptionPane.showMessageDialog(this, "Aucune caisse trouvée pour votre boutique.",
                    "Caisse introuvable", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField montantField = new JTextField();
            JTextField commentaireField = new JTextField();

            Object[] message = {
                "Montant à retirer (€):", montantField,
                "Commentaire:", commentaireField,
                "Solde actuel: " + String.format("%.2f €", caisse.getSoldeActuel())
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Retirer des Fonds",
                JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    double montant = Double.parseDouble(montantField.getText().trim());
                    String commentaire = commentaireField.getText().trim();

                    if (montant <= 0) {
                        JOptionPane.showMessageDialog(this, "Le montant doit être positif.",
                            "Montant invalide", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    System.out.println("REPARATEUR_PANEL - Appel retirerFondsCaisse: Caisse " + caisse.getIdCaisse() +
                                     " | Montant: " + montant + "€ | Solde avant: " + caisse.getSoldeActuel() + "€");
                    gestionEmprunt.retirerFondsCaisse(caisse.getIdCaisse(), reparateur.getId(), montant, commentaire);

                    JOptionPane.showMessageDialog(this, String.format("Fonds retirés avec succès !\n%.2f € ont été retirés de la caisse.", montant),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                    rafraichirHistoriqueCaisse(tableModel);

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Le montant doit être un nombre valide.",
                        "Format invalide", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du retrait de fonds: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirHistoriqueCaisse(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);

            // Récupérer le réparateur connecté et sa boutique
            dao.Reparateur reparateur = null;
            List<dao.Reparateur> reparateurs = gestionReparateur.lister();
            for (dao.Reparateur r : reparateurs) {
                if (r.getEmail().equals(mainWindow.getCurrentUserEmail())) {
                    reparateur = r;
                    break;
                }
            }

            if (reparateur == null || reparateur.getBoutique() == null) {
                return;
            }

            dao.Caisse caisse = reparateur.getBoutique().getCaisse();
            if (caisse == null) {
                return;
            }

            // Récupérer l'historique des emprunts pour cette caisse
            List<dao.Emprunt> emprunts = gestionEmprunt.listerEmpruntsParCaisse(caisse.getIdCaisse());

            // Calculer le solde progressif (en partant du solde actuel et en remontant)
            double soldeCourant = caisse.getSoldeActuel();

            // Trier les emprunts par date décroissante (plus récent en premier)
            emprunts.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));

            for (dao.Emprunt emprunt : emprunts) {
                double montant = emprunt.getMontant();
                String typeOperation = emprunt.getType();
                double soldeApres;

                // Calculer le solde après cette opération
                if ("Ajout de fonds".equals(typeOperation) || "Dépôt".equals(typeOperation)) {
                    soldeApres = soldeCourant + montant;
                } else if ("Retrait de fonds".equals(typeOperation) || "Prêt".equals(typeOperation)) {
                    soldeApres = soldeCourant - montant;
                } else {
                    soldeApres = soldeCourant; // Pour les autres types
                }

                tableModel.addRow(new Object[]{
                    emprunt.getDate() != null ? emprunt.getDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                    typeOperation,
                    String.format("%.2f €", montant),
                    emprunt.getCommentaire() != null ? emprunt.getCommentaire() : "",
                    String.format("%.2f €", soldeApres)
                });

                // Mettre à jour le solde pour la prochaine itération
                soldeCourant = soldeApres - montant; // Annuler l'effet de cette opération
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'historique: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

}


