package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import exception.DatabaseException;

/**
 * Panel d'authentification pour les différents rôles utilisateur
 */
public class AuthentificationPanel extends JPanel {

    private MainWindow mainWindow;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton guestButton;

    // Gestionnaires métier
    private metier.GestionReparateur gestionReparateur;
    private metier.GestionProprietaire gestionProprietaire;

    public AuthentificationPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.gestionReparateur = new metier.GestionReparateur();
        this.gestionProprietaire = new metier.GestionProprietaire();

        initializeComponents();
        setupLayout();
        setupListeners();
    }

    private void initializeComponents() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[]{"Sélectionnez un rôle", "ADMIN", "REPARATEUR", "PROPRIETAIRE"});
        loginButton = new JButton("Se connecter");
        guestButton = new JButton("Suivre une réparation (sans connexion)");

        // Style
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        guestButton.setBackground(new Color(34, 139, 34));
        guestButton.setForeground(Color.WHITE);
        guestButton.setFocusPainted(false);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Authentification Fast-Repair"));
        setBackground(new Color(248, 249, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Bienvenue sur Fast-Repair");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel, gbc);

        // Sous-titre
        gbc.gridy = 1;
        JLabel subtitleLabel = new JLabel("Système de gestion des réparations");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(Color.GRAY);
        add(subtitleLabel, gbc);

        // Espace
        gbc.gridy = 2;
        add(Box.createVerticalStrut(20), gbc);

        // Rôle
        gbc.gridy = 3; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Rôle:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(roleComboBox, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(emailField, gbc);

        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(guestButton);

        add(buttonPanel, gbc);

        // Informations supplémentaires
        gbc.gridy = 7;
        JTextArea infoArea = new JTextArea(
            "Pour les comptes de démonstration:\n" +
            "• Admin: admin@fastrepair.com / admin123\n" +
            "• Réparateur: reparateur@fastrepair.com / reparateur123\n" +
            "• Propriétaire: proprietaire@fastrepair.com / proprietaire123"
        );
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 11));
        infoArea.setForeground(Color.GRAY);
        infoArea.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(infoArea, gbc);
    }

    private void setupListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.showPanel("SUIVI");
            }
        });

        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFieldsVisibility();
            }
        });
    }

    private void updateFieldsVisibility() {
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if ("ADMIN".equals(selectedRole)) {
            // Pour l'admin, on peut utiliser des credentials par défaut
            emailField.setText("admin@fastrepair.com");
            passwordField.setText("admin123");
            emailField.setEditable(false);
            passwordField.setEditable(false);
        } else {
            emailField.setText("");
            passwordField.setText("");
            emailField.setEditable(true);
            passwordField.setEditable(true);
        }
    }

    private void authenticateUser() {
        String role = (String) roleComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation des champs
        if (role == null || role.equals("Sélectionnez un rôle")) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rôle.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir votre email.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir votre mot de passe.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean authenticated = false;

            switch (role) {
                case "ADMIN":
                    // Pour l'admin, vérification simple (en production, utiliser une vraie authentification)
                    if ("admin@fastrepair.com".equals(email) && "admin123".equals(password)) {
                        authenticated = true;
                    }
                    break;

                case "REPARATEUR":
                    // Vérifier dans la base de données
                    try {
                        dao.Reparateur reparateur = gestionReparateur.rechercherParEmail(email);
                        if (reparateur != null && password.equals(reparateur.getMdp())) {
                            authenticated = true;
                        }
                    } catch (DatabaseException ex) {
                        // Reparateur non trouvé, continuer
                    }
                    break;

                case "PROPRIETAIRE":
                    // Vérifier dans la base de données
                    try {
                        dao.Proprietaire proprietaire = gestionProprietaire.rechercherParEmail(email);
                        if (proprietaire != null && password.equals(proprietaire.getMdp())) {
                            authenticated = true;
                        }
                    } catch (DatabaseException ex) {
                        // Proprietaire non trouvé, continuer
                    }
                    break;
            }

            if (authenticated) {
                mainWindow.setCurrentUser(role, email);
                JOptionPane.showMessageDialog(this,
                    "Connexion réussie en tant que " + role + "!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Email ou mot de passe incorrect.",
                    "Échec de l'authentification",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'authentification:\n" + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
