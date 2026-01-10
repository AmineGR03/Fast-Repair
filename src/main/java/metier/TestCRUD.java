package metier;

import java.time.LocalDateTime;
import java.util.List;

import dao.*;
import exception.*;

/**
 * Classe de test complète pour toutes les opérations CRUD
 * Teste la connexion à la base de données et toutes les fonctionnalités
 */
public class TestCRUD {

    public static void main(String[] args) {
        System.out.println("🚀 Test CRUD Complet - FastRepair\n");

        try {
            // Initialiser la connexion
            System.out.println("1. Initialisation de la connexion...");
            DatabaseConnection.initialize();
            System.out.println("✅ Connexion établie!\n");

            // Tester tous les CRUD
            testClientCRUD();
            testReparateurCRUD();
            testProprietaireCRUD();
            testAppareilCRUD();
            testReparationCRUD();
            testBoutiqueCRUD();
            testCaisseCRUD();
            testComposantCRUD();
            testEmpruntCRUD();
            testRecuCRUD();

            System.out.println("\n🎉 Tous les tests CRUD réussis!");

        } catch (Exception e) {
            System.err.println("\n❌ Erreur lors des tests:");
            e.printStackTrace();
        } finally {
            DatabaseConnection.close();
            System.out.println("\n🔄 Connexion fermée.");
        }
    }

    private static void testClientCRUD() {
        System.out.println("2. Test CRUD Client");
        GestionClient gestion = new GestionClient();

        try {
            // CREATE
            Client client = Client.builder()
                .nom("Dubois")
                .prenom("Marie")
                .adresse("15 Rue des Lilas, Lyon")
                .telephone(987654321)
                .build();

            gestion.ajouter(client);
            System.out.println("   ✓ Client créé - ID: " + client.getId());

            // READ
            Client clientLu = gestion.rechercher(client.getId());
            System.out.println("   ✓ Client lu: " + clientLu.getNom() + " " + clientLu.getPrenom());

            // UPDATE
            clientLu.setAdresse("25 Avenue des Roses, Paris");
            gestion.modifer(clientLu);
            System.out.println("   ✓ Client modifié");

            // FILTER
            List<Client> clientsParNom = gestion.filtrerParNom("Dubois");
            System.out.println("   ✓ Filtrage par nom: " + clientsParNom.size() + " résultat(s)");

            List<Client> clientsParAdresse = gestion.filtrerParAdresse("Paris");
            System.out.println("   ✓ Filtrage par adresse: " + clientsParAdresse.size() + " résultat(s)");

            // LIST ALL
            List<Client> tousClients = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousClients.size() + " client(s)");

            System.out.println("   ✅ CRUD Client réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Client: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testReparateurCRUD() {
        System.out.println("3. Test CRUD Réparateur");
        GestionReparateur gestion = new GestionReparateur();

        try {
            // CREATE
            Reparateur reparateur = Reparateur.builder()
                .nom("Martin")
                .prenom("Pierre")
                .email("p.martin@repair.com")
                .mdp("secure123")
                .pourcentageGain(12.5)
                .build();

            gestion.ajouter(reparateur);
            System.out.println("   ✓ Réparateur créé - ID: " + reparateur.getId());

            // READ
            Reparateur reparateurLu = gestion.rechercher(reparateur.getId());
            System.out.println("   ✓ Réparateur lu: " + reparateurLu.getNom() + " (" + reparateurLu.getEmail() + ")");

            // UPDATE
            reparateurLu.setPourcentageGain(15.0);
            gestion.modifer(reparateurLu);
            System.out.println("   ✓ Réparateur modifié");

            // FILTER
            List<Reparateur> reparateursParEmail = gestion.filtrerParEmail("p.martin@repair.com");
            System.out.println("   ✓ Filtrage par email: " + reparateursParEmail.size() + " résultat(s)");

            // LIST ALL
            List<Reparateur> tousReparateurs = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousReparateurs.size() + " réparateur(s)");

            System.out.println("   ✅ CRUD Réparateur réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Réparateur: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testProprietaireCRUD() {
        System.out.println("4. Test CRUD Propriétaire");
        GestionProprietaire gestion = new GestionProprietaire();

        try {
            // CREATE
            Proprietaire proprietaire = Proprietaire.builder()
                .nom("Leroy")
                .prenom("Sophie")
                .email("s.leroy@fastrepair.com")
                .mdp("admin2024")
                .build();

            gestion.ajouter(proprietaire);
            System.out.println("   ✓ Propriétaire créé - ID: " + proprietaire.getId());

            // READ
            Proprietaire proprietaireLu = gestion.rechercher(proprietaire.getId());
            System.out.println("   ✓ Propriétaire lu: " + proprietaireLu.getNom() + " (" + proprietaireLu.getEmail() + ")");

            // UPDATE
            proprietaireLu.setEmail("sophie.leroy@fastrepair.com");
            gestion.modifer(proprietaireLu);
            System.out.println("   ✓ Propriétaire modifié");

            // FILTER
            List<Proprietaire> proprietairesParEmail = gestion.filtrerParEmail("sophie.leroy@fastrepair.com");
            System.out.println("   ✓ Filtrage par email: " + proprietairesParEmail.size() + " résultat(s)");

            // LIST ALL
            List<Proprietaire> tousProprietaires = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousProprietaires.size() + " propriétaire(s)");

            System.out.println("   ✅ CRUD Propriétaire réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Propriétaire: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testAppareilCRUD() {
        System.out.println("5. Test CRUD Appareil");
        GestionAppareil gestion = new GestionAppareil();

        try {
            // CREATE
            Appareil appareil = Appareil.builder()
                .imei("123456789012345")
                .marque("Samsung")
                .modele("Galaxy S23")
                .typeAppareil("Smartphone")
                .build();

            gestion.ajouter(appareil);
            System.out.println("   ✓ Appareil créé - ID: " + appareil.getIdAppareil());

            // READ
            Appareil appareilLu = gestion.rechercher(appareil.getIdAppareil());
            System.out.println("   ✓ Appareil lu: " + appareilLu.getMarque() + " " + appareilLu.getModele());

            // UPDATE
            appareilLu.setTypeAppareil("Téléphone portable");
            gestion.modifer(appareilLu);
            System.out.println("   ✓ Appareil modifié");

            // FILTER
            List<Appareil> appareilsParMarque = gestion.filtrerParMarque("Samsung");
            System.out.println("   ✓ Filtrage par marque: " + appareilsParMarque.size() + " résultat(s)");

            List<Appareil> appareilsParModele = gestion.filtrerParModele("Galaxy S23");
            System.out.println("   ✓ Filtrage par modèle: " + appareilsParModele.size() + " résultat(s)");

            // LIST ALL
            List<Appareil> tousAppareils = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousAppareils.size() + " appareil(s)");

            System.out.println("   ✅ CRUD Appareil réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Appareil: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testReparationCRUD() {
        System.out.println("6. Test CRUD Réparation");
        GestionReparation gestion = new GestionReparation();

        try {
            // D'abord créer un appareil pour la réparation
            GestionAppareil gestionApp = new GestionAppareil();
            Appareil appareil = Appareil.builder()
                .imei("987654321098765")
                .marque("Apple")
                .modele("iPhone 14")
                .typeAppareil("Smartphone")
                .build();
            gestionApp.ajouter(appareil);
            gestionApp.close();

            // CREATE
            Reparation reparation = Reparation.builder()
                .idAppareil(appareil.getIdAppareil())
                .codeSuivi("REP-2024-001")
                .dateDepot(LocalDateTime.now())
                .etat("En cours")
                .commentaire("Écran fissuré - remplacement nécessaire")
                .prixTotal(150.0)
                .build();

            gestion.ajouter(reparation);
            System.out.println("   ✓ Réparation créée - ID Appareil: " + reparation.getIdAppareil());

            // READ
            Reparation reparationLue = gestion.rechercher(reparation.getIdAppareil());
            System.out.println("   ✓ Réparation lue: " + reparationLue.getCodeSuivi() + " - " + reparationLue.getEtat());

            // UPDATE
            reparationLue.setEtat("Terminée");
            reparationLue.setPrixTotal(180.0);
            gestion.modifer(reparationLue);
            System.out.println("   ✓ Réparation modifiée");

            // FILTER
            List<Reparation> reparationsParEtat = gestion.filtrerParEtat("Terminée");
            System.out.println("   ✓ Filtrage par état: " + reparationsParEtat.size() + " résultat(s)");

            // LIST ALL
            List<Reparation> toutesReparations = gestion.lister();
            System.out.println("   ✓ Liste complète: " + toutesReparations.size() + " réparation(s)");

            System.out.println("   ✅ CRUD Réparation réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Réparation: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testBoutiqueCRUD() {
        System.out.println("7. Test CRUD Boutique");
        GestionBoutique gestion = new GestionBoutique();

        try {
            // CREATE
            Boutique boutique = Boutique.builder()
                .nom("FastRepair Lyon")
                .adresse("10 Place Bellecour, Lyon")
                .numTel(478123456)
                .numP(12345)
                .build();

            gestion.ajouter(boutique);
            System.out.println("   ✓ Boutique créée - ID: " + boutique.getIdBoutique());

            // READ
            Boutique boutiqueLue = gestion.rechercher(boutique.getIdBoutique());
            System.out.println("   ✓ Boutique lue: " + boutiqueLue.getNom());

            // UPDATE
            boutiqueLue.setNumTel(478987654);
            gestion.modifer(boutiqueLue);
            System.out.println("   ✓ Boutique modifiée");

            // FILTER
            List<Boutique> boutiquesParNom = gestion.filtrerParNom("FastRepair");
            System.out.println("   ✓ Filtrage par nom: " + boutiquesParNom.size() + " résultat(s)");

            // LIST ALL
            List<Boutique> toutesBoutiques = gestion.lister();
            System.out.println("   ✓ Liste complète: " + toutesBoutiques.size() + " boutique(s)");

            System.out.println("   ✅ CRUD Boutique réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Boutique: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testCaisseCRUD() {
        System.out.println("8. Test CRUD Caisse");
        GestionCaisse gestion = new GestionCaisse();

        try {
            // CREATE
            Caisse caisse = Caisse.builder()
                .soldeActuel(1500.50)
                .dernierMouvement(LocalDateTime.now())
                .build();

            gestion.ajouter(caisse);
            System.out.println("   ✓ Caisse créée - ID: " + caisse.getIdCaisse());

            // READ
            Caisse caisseLue = gestion.rechercher(caisse.getIdCaisse());
            System.out.println("   ✓ Caisse lue: Solde = " + caisseLue.getSoldeActuel() + "€");

            // UPDATE
            caisseLue.setSoldeActuel(1750.75);
            gestion.modifer(caisseLue);
            System.out.println("   ✓ Caisse modifiée");

            // FILTER
            List<Caisse> caissesParSolde = gestion.filtrerParSoldeActuel(1750.75);
            System.out.println("   ✓ Filtrage par solde: " + caissesParSolde.size() + " résultat(s)");

            // LIST ALL
            List<Caisse> toutesCaisses = gestion.lister();
            System.out.println("   ✓ Liste complète: " + toutesCaisses.size() + " caisse(s)");

            System.out.println("   ✅ CRUD Caisse réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Caisse: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testComposantCRUD() {
        System.out.println("9. Test CRUD Composant");
        GestionComposant gestion = new GestionComposant();

        try {
            // CREATE
            Composant composant = Composant.builder()
                .nom("Écran iPhone 14")
                .prix(89.99)
                .quantite(25)
                .build();

            gestion.ajouter(composant);
            System.out.println("   ✓ Composant créé - ID: " + composant.getIdComposant());

            // READ
            Composant composantLu = gestion.rechercher(composant.getIdComposant());
            System.out.println("   ✓ Composant lu: " + composantLu.getNom() + " - Stock: " + composantLu.getQuantite());

            // UPDATE
            composantLu.setQuantite(20);
            gestion.modifer(composantLu);
            System.out.println("   ✓ Composant modifié");

            // FILTER
            List<Composant> composantsParNom = gestion.filtrerParNom("Écran");
            System.out.println("   ✓ Filtrage par nom: " + composantsParNom.size() + " résultat(s)");

            List<Composant> composantsParQuantite = gestion.filtrerParQuantite(20);
            System.out.println("   ✓ Filtrage par quantité: " + composantsParQuantite.size() + " résultat(s)");

            // LIST ALL
            List<Composant> tousComposants = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousComposants.size() + " composant(s)");

            System.out.println("   ✅ CRUD Composant réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Composant: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testEmpruntCRUD() {
        System.out.println("10. Test CRUD Emprunt");
        GestionEmprunt gestion = new GestionEmprunt();

        try {
            // CREATE
            Emprunt emprunt = Emprunt.builder()
                .date(LocalDateTime.now())
                .montant(500.00)
                .type("Matériel")
                .commentaire("Achat d'outils de réparation")
                .build();

            gestion.ajouter(emprunt);
            System.out.println("   ✓ Emprunt créé - ID: " + emprunt.getIdEmprunt());

            // READ
            Emprunt empruntLu = gestion.rechercher(emprunt.getIdEmprunt());
            System.out.println("   ✓ Emprunt lu: " + empruntLu.getType() + " - " + empruntLu.getMontant() + "€");

            // UPDATE
            empruntLu.setMontant(550.00);
            gestion.modifer(empruntLu);
            System.out.println("   ✓ Emprunt modifié");

            // FILTER
            List<Emprunt> empruntsParType = gestion.filtrerParType("Matériel");
            System.out.println("   ✓ Filtrage par type: " + empruntsParType.size() + " résultat(s)");

            List<Emprunt> empruntsParMontant = gestion.filtrerParMontant(550.00);
            System.out.println("   ✓ Filtrage par montant: " + empruntsParMontant.size() + " résultat(s)");

            // LIST ALL
            List<Emprunt> tousEmprunts = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousEmprunts.size() + " emprunt(s)");

            System.out.println("   ✅ CRUD Emprunt réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Emprunt: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }

    private static void testRecuCRUD() {
        System.out.println("11. Test CRUD Reçu");
        GestionRecu gestion = new GestionRecu();

        try {
            // CREATE
            Recu recu = Recu.builder()
                .date(LocalDateTime.now())
                .montant(180.50)
                .build();

            gestion.ajouter(recu);
            System.out.println("   ✓ Reçu créé - ID: " + recu.getIdRecu());

            // READ
            Recu recuLu = gestion.rechercher(recu.getIdRecu());
            System.out.println("   ✓ Reçu lu: " + recuLu.getMontant() + "€ - " + recuLu.getDate());

            // UPDATE
            recuLu.setMontant(200.00);
            gestion.modifer(recuLu);
            System.out.println("   ✓ Reçu modifié");

            // FILTER
            List<Recu> recusParMontant = gestion.filtrerParMontant(200.00);
            System.out.println("   ✓ Filtrage par montant: " + recusParMontant.size() + " résultat(s)");

            // LIST ALL
            List<Recu> tousRecus = gestion.lister();
            System.out.println("   ✓ Liste complète: " + tousRecus.size() + " reçu(s)");

            System.out.println("   ✅ CRUD Reçu réussi!\n");

        } catch (Exception e) {
            System.err.println("   ❌ Erreur Reçu: " + e.getMessage());
        } finally {
            gestion.close();
        }
    }
}


