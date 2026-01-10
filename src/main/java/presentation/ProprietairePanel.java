package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel pour les propriétaires - Statistiques et gestion financière
 */
public class ProprietairePanel extends JPanel {

    private MainWindow mainWindow;
    private String proprietaireEmail;

    // Gestionnaires métier
    private metier.GestionCaisse gestionCaisse;
    private metier.GestionRecu gestionRecu;
    private metier.GestionEmprunt gestionEmprunt;
    private metier.GestionBoutique gestionBoutique;

    // Composants UI
    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public ProprietairePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.proprietaireEmail = mainWindow.getCurrentUserEmail();

        // Initialiser les gestionnaires
        gestionCaisse = new metier.GestionCaisse();
        gestionRecu = new metier.GestionRecu();
        gestionEmprunt = new metier.GestionEmprunt();
        gestionBoutique = new metier.GestionBoutique();

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
        createRapportsTab();
        createProfileTab();
    }

    private void updateWelcomeLabel() {
        welcomeLabel.setText("Bienvenue, Propriétaire: " + (proprietaireEmail != null ? proprietaireEmail : "Inconnu"));
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

    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Tableau de Bord - Vue d'ensemble", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(155, 89, 182));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel des statistiques
        JPanel statsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        try {
            // Calculer les statistiques
            double soldeTotal = calculerSoldeTotal();
            int nbBoutiques = gestionBoutique.lister().size();
            int nbRecus = gestionRecu.lister().size();
            int nbEmprunts = gestionEmprunt.lister().size();
            double totalRecus = gestionRecu.lister().stream().mapToDouble(dao.Recu::getMontant).sum();
            double totalEmprunts = gestionEmprunt.lister().stream().mapToDouble(dao.Emprunt::getMontant).sum();

            // Ajouter les cartes de statistiques
            statsPanel.add(createStatCard("💰 Solde Total", String.format("%.2f €", soldeTotal), new Color(46, 204, 113)));
            statsPanel.add(createStatCard("🏪 Boutiques", String.valueOf(nbBoutiques), new Color(52, 152, 219)));
            statsPanel.add(createStatCard("🧾 Reçus", String.valueOf(nbRecus), new Color(155, 89, 182)));
            statsPanel.add(createStatCard("💸 Emprunts", String.valueOf(nbEmprunts), new Color(230, 126, 34)));
            statsPanel.add(createStatCard("📈 Recettes", String.format("%.2f €", totalRecus), new Color(46, 204, 113)));
            statsPanel.add(createStatCard("📉 Dépenses", String.format("%.2f €", totalEmprunts), new Color(231, 76, 60)));
            statsPanel.add(createStatCard("📊 Bénéfice", String.format("%.2f €", totalRecus - totalEmprunts), new Color(44, 62, 80)));
            statsPanel.add(createStatCard("⚡ État", "Opérationnel", new Color(46, 204, 113)));

        } catch (Exception e) {
            statsPanel.add(createStatCard("❌ Erreur", "BD inaccessible", Color.RED));
        }

        dashboardPanel.add(statsPanel, BorderLayout.CENTER);

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

        // Boutons pour les boutiques
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnVoirBoutiques = new JButton("🏪 Voir Boutiques");
        JButton btnAjouterBoutique = new JButton("➕ Nouvelle Boutique");
        JButton btnModifierBoutique = new JButton("✏️ Modifier Boutique");

        btnVoirBoutiques.setBackground(new Color(52, 152, 219));
        btnAjouterBoutique.setBackground(new Color(40, 167, 69));
        btnModifierBoutique.setBackground(new Color(255, 193, 7));

        for (JButton btn : new JButton[]{btnVoirBoutiques, btnAjouterBoutique, btnModifierBoutique}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(150, 35));
            buttonPanel.add(btn);
        }

        boutiquesPanel.add(buttonPanel, BorderLayout.NORTH);

        // Table des boutiques
        String[] columns = {"ID Boutique", "Nom", "Adresse", "Téléphone", "Numéro P"};
        JTable table = new JTable(new Object[][]{}, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        boutiquesPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("🏪 Boutiques", boutiquesPanel);

        // Listeners
        btnVoirBoutiques.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Affichage des boutiques - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });

        btnAjouterBoutique.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Ajout de nouvelle boutique - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });

        btnModifierBoutique.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Modification de boutique - Fonctionnalité à implémenter",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });
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
        infoPanel.add(new JLabel(proprietaireEmail != null ? proprietaireEmail : "Non défini"), gbc);

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

                var caisses = gestionCaisse.lister();
                for (var caisse : caisses) {
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

                var recus = gestionRecu.lister();
                double total = 0;
                for (var recu : recus) {
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

                var emprunts = gestionEmprunt.lister();
                double total = 0;
                for (var emprunt : emprunts) {
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
}
