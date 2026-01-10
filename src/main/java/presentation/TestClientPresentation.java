package presentation;

import java.util.List;
import java.util.Scanner;

import dao.Client;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
import metier.GestionClient;

public class TestClientPresentation {

    private static GestionClient gestionClient;
    private static Scanner scanner;

    public static void main(String[] args) {
        gestionClient = new GestionClient();
        scanner = new Scanner(System.in);

        try {
            System.out.println("=== TEST DES FONCTIONNALITÉS CRUD CLIENT (connexion via persistence.xml) ===\n");

            // Test d'ajout de clients
            testAjouterClients();

            // Test de listage
            testListerClients();

            // Test de recherche
            testRechercherClient();

            // Test de modification
            testModifierClient();

            // Test de filtrage
            testFiltrage();

            // Test de suppression
            testSupprimerClient();

            System.out.println("\n=== FIN DES TESTS ===");

        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (gestionClient != null) {
                gestionClient.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void testAjouterClients() {
        System.out.println("--- Test d'ajout de clients ---");

        try {
            // Création de clients de test
            Client client1 = Client.builder()
                    .nom("Dupont")
                    .prenom("Jean")
                    .adresse("123 Rue de la Paix, Paris")
                    .telephone(123456789)
                    .build();

            Client client2 = Client.builder()
                    .nom("Martin")
                    .prenom("Marie")
                    .adresse("456 Avenue des Champs, Lyon")
                    .telephone(987654321)
                    .build();

            Client client3 = Client.builder()
                    .nom("Dubois")
                    .prenom("Pierre")
                    .adresse("789 Boulevard Saint-Michel, Marseille")
                    .telephone(555666777)
                    .build();

            // Test d'ajout
            System.out.println("Ajout du client 1...");
            gestionClient.ajouter(client1);
            System.out.println("✓ Client 1 ajouté avec succès");

            System.out.println("Ajout du client 2...");
            gestionClient.ajouter(client2);
            System.out.println("✓ Client 2 ajouté avec succès");

            System.out.println("Ajout du client 3...");
            gestionClient.ajouter(client3);
            System.out.println("✓ Client 3 ajouté avec succès");

            // Test d'ajout avec ID dupliqué (si possible)
            try {
                Client clientDuplicate = Client.builder()
                        .nom("Test")
                        .prenom("Duplicate")
                        .adresse("Test Address")
                        .telephone(111111111)
                        .build();
                // Pour tester la duplication, nous aurions besoin de forcer un ID
                // Mais comme c'est auto-généré, ce test pourrait ne pas fonctionner
                // selon l'implémentation JPA
                System.out.println("Tentative d'ajout avec données valides...");
                gestionClient.ajouter(clientDuplicate);
                System.out.println("✓ Client supplémentaire ajouté");
            } catch (DuplicateEntityException e) {
                System.out.println("✓ Exception attendue pour duplication : " + e.getMessage());
            }

        } catch (DuplicateEntityException e) {
            System.err.println("✗ Erreur de duplication : " + e.getMessage());
        } catch (DatabaseException e) {
            System.err.println("✗ Erreur base de données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Erreur inattendue lors de l'ajout : " + e.getMessage());
        }

        System.out.println();
    }

    private static void testListerClients() {
        System.out.println("--- Test de listage des clients ---");

        try {
            List<Client> clients = gestionClient.lister();

            System.out.println("Nombre de clients trouvés : " + clients.size());
            System.out.println("Liste des clients :");

            for (Client client : clients) {
                System.out.println("  ID: " + client.getId() +
                                 ", Nom: " + client.getNom() +
                                 ", Prénom: " + client.getPrenom() +
                                 ", Adresse: " + client.getAdresse() +
                                 ", Téléphone: " + client.getTelephone());
            }

            System.out.println("✓ Listage réussi");

        } catch (DatabaseException e) {
            System.err.println("✗ Erreur lors du listage : " + e.getMessage());
        }

        System.out.println();
    }

    private static void testRechercherClient() {
        System.out.println("--- Test de recherche de client ---");

        try {
            // Récupérer la liste pour connaître un ID existant
            List<Client> clients = gestionClient.lister();

            if (!clients.isEmpty()) {
                int idToSearch = clients.get(0).getId();

                System.out.println("Recherche du client avec ID : " + idToSearch);
                Client client = gestionClient.rechercher(idToSearch);

                if (client != null) {
                    System.out.println("Client trouvé :");
                    System.out.println("  ID: " + client.getId() +
                                     ", Nom: " + client.getNom() +
                                     ", Prénom: " + client.getPrenom());
                    System.out.println("✓ Recherche réussie");
                } else {
                    System.out.println("✗ Client non trouvé");
                }

                // Test de recherche d'un ID inexistant
                System.out.println("Recherche d'un client avec ID inexistant (999)...");
                Client clientInexistant = gestionClient.rechercher(999);
                if (clientInexistant == null) {
                    System.out.println("✓ Recherche d'ID inexistant retourne null correctement");
                } else {
                    System.out.println("✗ Recherche d'ID inexistant ne retourne pas null");
                }
            } else {
                System.out.println("Aucun client trouvé pour tester la recherche");
            }

        } catch (DatabaseException e) {
            System.err.println("✗ Erreur lors de la recherche : " + e.getMessage());
        }

        System.out.println();
    }

    private static void testModifierClient() {
        System.out.println("--- Test de modification de client ---");

        try {
            List<Client> clients = gestionClient.lister();

            if (!clients.isEmpty()) {
                Client clientToModify = clients.get(0);
                System.out.println("Modification du client ID " + clientToModify.getId());

                // Créer une version modifiée
                Client modifiedClient = Client.builder()
                        .id(clientToModify.getId())
                        .nom(clientToModify.getNom() + " (Modifié)")
                        .prenom(clientToModify.getPrenom())
                        .adresse("Nouvelle adresse : " + clientToModify.getAdresse())
                        .telephone(clientToModify.getTelephone())
                        .build();

                gestionClient.modifer(modifiedClient);
                System.out.println("✓ Client modifié avec succès");

                // Vérifier la modification
                Client verifiedClient = gestionClient.rechercher(clientToModify.getId());
                if (verifiedClient != null && verifiedClient.getNom().contains("(Modifié)")) {
                    System.out.println("✓ Modification vérifiée");
                } else {
                    System.out.println("✗ Modification non vérifiée");
                }
            } else {
                System.out.println("Aucun client trouvé pour tester la modification");
            }

            // Test de modification d'un client inexistant
            try {
                Client inexistantClient = Client.builder()
                        .id(999)
                        .nom("Inexistant")
                        .prenom("Test")
                        .adresse("Test")
                        .telephone(123456789)
                        .build();

                gestionClient.modifer(inexistantClient);
                System.out.println("✗ Modification d'un client inexistant n'a pas levé d'exception");
            } catch (EntityNotFoundException e) {
                System.out.println("✓ Exception attendue pour client inexistant : " + e.getMessage());
            }

        } catch (EntityNotFoundException e) {
            System.err.println("✗ Client non trouvé : " + e.getMessage());
        } catch (DatabaseException e) {
            System.err.println("✗ Erreur base de données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Erreur inattendue lors de la modification : " + e.getMessage());
        }

        System.out.println();
    }

    private static void testFiltrage() {
        System.out.println("--- Test des méthodes de filtrage ---");

        try {
            // Test de filtrage par nom
            System.out.println("Filtrage par nom 'Dupont'...");
            List<Client> clientsParNom = gestionClient.filtrerParNom("Dupont");
            System.out.println("Clients trouvés : " + clientsParNom.size());
            afficherClients(clientsParNom);

            // Test de filtrage par prénom
            System.out.println("Filtrage par prénom 'Marie'...");
            List<Client> clientsParPrenom = gestionClient.filtrerParPrenom("Marie");
            System.out.println("Clients trouvés : " + clientsParPrenom.size());
            afficherClients(clientsParPrenom);

            // Test de filtrage par adresse
            System.out.println("Filtrage par adresse 'Paris'...");
            List<Client> clientsParAdresse = gestionClient.filtrerParAdresse("Paris");
            System.out.println("Clients trouvés : " + clientsParAdresse.size());
            afficherClients(clientsParAdresse);

            // Test de filtrage par téléphone
            List<Client> allClients = gestionClient.lister();
            if (!allClients.isEmpty()) {
                int telephoneToSearch = allClients.get(0).getTelephone();
                System.out.println("Filtrage par téléphone '" + telephoneToSearch + "'...");
                List<Client> clientsParTelephone = gestionClient.filtrerParTelephone(telephoneToSearch);
                System.out.println("Clients trouvés : " + clientsParTelephone.size());
                afficherClients(clientsParTelephone);
            }

            // Test de filtrage avec paramètre invalide
            try {
                System.out.println("Test de filtrage avec nom vide...");
                gestionClient.filtrerParNom("");
                System.out.println("✗ Filtrage avec paramètre invalide n'a pas levé d'exception");
            } catch (InvalidParameterException e) {
                System.out.println("✓ Exception attendue pour paramètre invalide : " + e.getMessage());
            }

            System.out.println("✓ Tests de filtrage terminés");

        } catch (InvalidParameterException e) {
            System.err.println("✗ Paramètre invalide : " + e.getMessage());
        } catch (DatabaseException e) {
            System.err.println("✗ Erreur base de données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Erreur inattendue lors du filtrage : " + e.getMessage());
        }

        System.out.println();
    }

    private static void testSupprimerClient() {
        System.out.println("--- Test de suppression de client ---");

        try {
            List<Client> clients = gestionClient.lister();

            if (!clients.isEmpty()) {
                Client clientToDelete = clients.get(clients.size() - 1); // Supprimer le dernier
                int idToDelete = clientToDelete.getId();

                System.out.println("Suppression du client ID " + idToDelete + " (" + clientToDelete.getNom() + ")");

                gestionClient.supprimer(idToDelete);
                System.out.println("✓ Client supprimé avec succès");

                // Vérifier la suppression
                Client verifiedClient = gestionClient.rechercher(idToDelete);
                if (verifiedClient == null) {
                    System.out.println("✓ Suppression vérifiée");
                } else {
                    System.out.println("✗ Suppression non vérifiée");
                }
            } else {
                System.out.println("Aucun client trouvé pour tester la suppression");
            }

            // Test de suppression d'un client inexistant
            try {
                System.out.println("Tentative de suppression d'un client inexistant (ID 999)...");
                gestionClient.supprimer(999);
                System.out.println("✗ Suppression d'un client inexistant n'a pas levé d'exception");
            } catch (EntityNotFoundException e) {
                System.out.println("✓ Exception attendue pour client inexistant : " + e.getMessage());
            }

        } catch (EntityNotFoundException e) {
            System.err.println("✗ Client non trouvé : " + e.getMessage());
        } catch (DatabaseException e) {
            System.err.println("✗ Erreur base de données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Erreur inattendue lors de la suppression : " + e.getMessage());
        }

        System.out.println();
    }

    private static void afficherClients(List<Client> clients) {
        for (Client client : clients) {
            System.out.println("  - " + client.getNom() + " " + client.getPrenom() +
                             " (" + client.getAdresse() + ", " + client.getTelephone() + ")");
        }
    }
}
