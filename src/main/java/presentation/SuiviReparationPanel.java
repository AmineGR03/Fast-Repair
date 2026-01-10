package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import exception.DatabaseException;

/**
 * Panel de suivi de réparation pour les clients (sans authentification)
 */
public class SuiviReparationPanel extends JPanel {

    private MainWindow mainWindow;
    private JTextField codeField;
    private JButton searchButton;
    private JTextArea resultArea;

    private metier.GestionReparation gestionReparation;

    public SuiviReparationPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.gestionReparation = new metier.GestionReparation();

        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        codeField = new JTextField(20);
        searchButton = new JButton("🔍 Rechercher");
        resultArea = new JTextArea(15, 50);

        // Style
        searchButton.setBackground(new Color(34, 139, 34));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createLoweredBevelBorder());
        resultArea.setBackground(new Color(248, 249, 250));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Panel du haut avec le titre et la recherche
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Suivi de Réparation"));
        topPanel.setBackground(Color.WHITE);

        // Titre
        JLabel titleLabel = new JLabel("Suivre l'état de votre réparation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        searchPanel.add(new JLabel("Code de suivi:"));
        searchPanel.add(codeField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Panel des résultats
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Informations de la réparation"));
        resultPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton retour
        JButton backButton = new JButton("← Retour à l'accueil");
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainWindow.showPanel("AUTH"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(backButton);
        resultPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(resultPanel, BorderLayout.CENTER);

        // Message d'accueil
        resultArea.setText(
            "Bienvenue dans le système de suivi de réparation Fast-Repair!\n\n" +
            "Pour suivre l'état de votre réparation, saisissez le code de suivi\n" +
            "qui vous a été fourni lors du dépôt de votre appareil.\n\n" +
            "Le code de suivi est généralement composé de lettres et chiffres\n" +
            "et vous permet de consulter :\n" +
            "• L'état actuel de la réparation\n" +
            "• Les commentaires du réparateur\n" +
            "• Le prix total estimé\n" +
            "• La date de dépôt\n\n" +
            "Exemples de codes : REP001, ABC123, etc."
        );
    }

    private void setupListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherReparation();
            }
        });

        codeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechercherReparation();
            }
        });
    }

    private void rechercherReparation() {
        String codeSuivi = codeField.getText().trim();

        if (codeSuivi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir un code de suivi.",
                "Code manquant",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Rechercher la réparation par code de suivi
            java.util.List<dao.Reparation> reparations = gestionReparation.filtrerParCodeSuivi(codeSuivi);

            if (reparations.isEmpty()) {
                resultArea.setText(
                    "❌ Aucune réparation trouvée pour le code: " + codeSuivi + "\n\n" +
                    "Vérifiez que le code saisi est correct.\n" +
                    "Le code de suivi est sensible à la casse.\n\n" +
                    "Si le problème persiste, contactez le service client."
                );
                return;
            }

            // Afficher les informations de la réparation
            dao.Reparation reparation = reparations.get(0);
            afficherDetailsReparation(reparation);

        } catch (DatabaseException ex) {
            resultArea.setText(
                "❌ Erreur lors de la recherche:\n" +
                ex.getMessage() + "\n\n" +
                "Veuillez réessayer plus tard."
            );
        } catch (Exception ex) {
            resultArea.setText(
                "❌ Erreur inattendue:\n" +
                ex.getMessage() + "\n\n" +
                "Veuillez contacter le support technique."
            );
        }
    }

    private void afficherDetailsReparation(dao.Reparation reparation) {
        StringBuilder sb = new StringBuilder();
        sb.append("✅ RÉPARATION TROUVÉE\n");
        sb.append("=====================================\n\n");

        sb.append("📋 INFORMATIONS GÉNÉRALES\n");
        sb.append("Code de suivi: ").append(reparation.getCodeSuivi()).append("\n");
        sb.append("ID Appareil: ").append(reparation.getIdAppareil()).append("\n");
        sb.append("Date de dépôt: ").append(reparation.getDateDepot() != null ?
            reparation.getDateDepot().toString() : "Non spécifiée").append("\n\n");

        sb.append("🔧 ÉTAT DE LA RÉPARATION\n");
        sb.append("Statut: ").append(getStatusDescription(reparation.getEtat())).append("\n\n");

        sb.append("💬 COMMENTAIRES\n");
        sb.append(reparation.getCommentaire() != null && !reparation.getCommentaire().trim().isEmpty() ?
            reparation.getCommentaire() : "Aucun commentaire pour le moment.").append("\n\n");

        sb.append("💰 INFORMATIONS FINANCIÈRES\n");
        sb.append("Prix total estimé: ");
        if (reparation.getPrixTotal() != null && reparation.getPrixTotal() > 0) {
            sb.append(String.format("%.2f €", reparation.getPrixTotal()));
        } else {
            sb.append("À déterminer");
        }
        sb.append("\n\n");

        sb.append("📞 PROCHAINES ÉTAPES\n");
        sb.append(getNextStepsMessage(reparation.getEtat()));

        resultArea.setText(sb.toString());
    }

    private String getStatusDescription(String etat) {
        if (etat == null) return "Statut inconnu";

        switch (etat.toUpperCase()) {
            case "DEPOT": return "📦 Appareil déposé - En attente de diagnostic";
            case "DIAGNOSTIC": return "🔍 Diagnostic en cours";
            case "REPARATION": return "🔧 Réparation en cours";
            case "TEST": return "✅ Tests et vérifications";
            case "TERMINE": return "🎉 Réparation terminée - Prêt pour récupération";
            case "ANNULÉ": return "❌ Réparation annulée";
            default: return "📝 " + etat;
        }
    }

    private String getNextStepsMessage(String etat) {
        if (etat == null) return "Contactez-nous pour plus d'informations.";

        switch (etat.toUpperCase()) {
            case "DEPOT":
                return "Votre appareil a été enregistré. Un diagnostic sera effectué prochainement.\n" +
                       "Vous serez informé par email de l'évolution.";
            case "DIAGNOSTIC":
                return "Le diagnostic est en cours. Nous vous contacterons dès que possible\n" +
                       "avec un devis détaillé.";
            case "REPARATION":
                return "Les réparations sont en cours. La durée dépend de la complexité\n" +
                       "des travaux nécessaires.";
            case "TEST":
                return "Votre appareil est en phase de test. Si tout est conforme,\n" +
                       "il sera disponible pour récupération.";
            case "TERMINE":
                return "🎉 Votre réparation est terminée ! Vous pouvez venir récupérer\n" +
                       "votre appareil avec le paiement du solde restant.";
            case "ANNULÉ":
                return "La réparation a été annulée. Contactez-nous pour plus de détails.";
            default:
                return "Pour plus d'informations, contactez notre service client.";
        }
    }
}
