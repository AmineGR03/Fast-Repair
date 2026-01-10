package metier;

import java.time.LocalDateTime;
import dao.*;
import exception.DatabaseException;
import exception.DuplicateEntityException;

/**
 * Classe utilitaire pour initialiser des données de test
 * dans la base de données
 */
public class TestDataInitializer {

    public static void initializeTestData() {
        System.out.println("🔄 Initialisation des données de test...");

        try {
            // Initialiser quelques clients
            initializeClients();

            // Initialiser quelques réparateurs
            initializeReparateurs();

            // Initialiser quelques propriétaires
            initializeProprietaires();

            // Initialiser quelques appareils
            initializeAppareils();

            // Initialiser quelques boutiques
            initializeBoutiques();

            System.out.println("✅ Données de test initialisées avec succès!");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation des données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeClients() throws DatabaseException, DuplicateEntityException {
        GestionClient gestion = new GestionClient();

        try {
            // Client 1
            Client client1 = Client.builder()
                .nom("Dupont")
                .prenom("Jean")
                .adresse("15 Rue de la Paix, Paris")
                .telephone(123456789)
                .build();
            gestion.ajouter(client1);

            // Client 2
            Client client2 = Client.builder()
                .nom("Martin")
                .prenom("Sophie")
                .adresse("25 Avenue des Champs, Lyon")
                .telephone(987654321)
                .build();
            gestion.ajouter(client2);

            // Client 3
            Client client3 = Client.builder()
                .nom("Dubois")
                .prenom("Pierre")
                .adresse("10 Place Bellecour, Lyon")
                .telephone(456789123)
                .build();
            gestion.ajouter(client3);

            System.out.println("   ✓ 3 clients créés");

        } finally {
            gestion.close();
        }
    }

    private static void initializeReparateurs() throws DatabaseException, DuplicateEntityException {
        GestionReparateur gestion = new GestionReparateur();

        try {
            // Réparateur 1
            Reparateur reparateur1 = Reparateur.builder()
                .nom("Leroy")
                .prenom("Marc")
                .email("m.leroy@repair.com")
                .mdp("tech123")
                .pourcentageGain(10.0)
                .build();
            gestion.ajouter(reparateur1);

            // Réparateur 2
            Reparateur reparateur2 = Reparateur.builder()
                .nom("Garcia")
                .prenom("Anna")
                .email("a.garcia@repair.com")
                .mdp("fixit456")
                .pourcentageGain(12.5)
                .build();
            gestion.ajouter(reparateur2);

            System.out.println("   ✓ 2 réparateurs créés");

        } finally {
            gestion.close();
        }
    }

    private static void initializeProprietaires() throws DatabaseException, DuplicateEntityException {
        GestionProprietaire gestion = new GestionProprietaire();

        try {
            // Propriétaire 1
            Proprietaire proprietaire1 = Proprietaire.builder()
                .nom("Robert")
                .prenom("Marie")
                .email("m.robert@fastrepair.com")
                .mdp("admin2024")
                .build();
            gestion.ajouter(proprietaire1);

            System.out.println("   ✓ 1 propriétaire créé");

        } finally {
            gestion.close();
        }
    }

    private static void initializeAppareils() throws DatabaseException, DuplicateEntityException {
        GestionAppareil gestion = new GestionAppareil();

        try {
            // Appareil 1
            Appareil appareil1 = Appareil.builder()
                .imei("111111111111111")
                .marque("Samsung")
                .modele("Galaxy S23")
                .typeAppareil("Smartphone")
                .build();
            gestion.ajouter(appareil1);

            // Appareil 2
            Appareil appareil2 = Appareil.builder()
                .imei("222222222222222")
                .marque("Apple")
                .modele("iPhone 14")
                .typeAppareil("Smartphone")
                .build();
            gestion.ajouter(appareil2);

            // Appareil 3
            Appareil appareil3 = Appareil.builder()
                .imei("333333333333333")
                .marque("Huawei")
                .modele("P40 Pro")
                .typeAppareil("Smartphone")
                .build();
            gestion.ajouter(appareil3);

            System.out.println("   ✓ 3 appareils créés");

        } finally {
            gestion.close();
        }
    }

    private static void initializeBoutiques() throws DatabaseException, DuplicateEntityException {
        GestionBoutique gestion = new GestionBoutique();

        try {
            // Boutique 1
            Boutique boutique1 = Boutique.builder()
                .nom("FastRepair Paris")
                .adresse("123 Avenue des Champs-Élysées, Paris")
                .numTel(145678901)
                .numP(75001)
                .build();
            gestion.ajouter(boutique1);

            // Boutique 2
            Boutique boutique2 = Boutique.builder()
                .nom("FastRepair Lyon")
                .adresse("45 Rue de la République, Lyon")
                .numTel(478123456)
                .numP(69001)
                .build();
            gestion.ajouter(boutique2);

            System.out.println("   ✓ 2 boutiques créées");

        } finally {
            gestion.close();
        }
    }

    /**
     * Méthode principale pour initialiser les données
     */
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Initialisation des données de test FastRepair\n");

            // Initialiser la connexion
            DatabaseConnection.initialize();

            // Initialiser les données
            initializeTestData();

            System.out.println("\n✅ Initialisation terminée avec succès!");

        } catch (Exception e) {
            System.err.println("\n❌ Erreur lors de l'initialisation:");
            e.printStackTrace();
        } finally {
            DatabaseConnection.close();
        }
    }
}


