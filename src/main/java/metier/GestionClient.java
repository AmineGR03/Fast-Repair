package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Client;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionClient implements IGestionClient {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionClient() {
        this.emf = Persistence.createEntityManagerFactory("FastRepairPU");
        this.em = emf.createEntityManager();
    }

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    public void ajouter(Client client) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (client == null) {
                throw new InvalidParameterException("Le client ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Vérifier si un client avec le même ID existe déjà
            Client existingClient = em.find(Client.class, client.getId());
            if (existingClient != null) {
                throw new DuplicateEntityException("Un client avec l'ID " + client.getId() + " existe déjà");
            }

            em.persist(client);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de l'ajout du client", e);
        }
    }

    @Override
    public void modifer(Client client) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (client == null) {
                throw new InvalidParameterException("Le client ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Client existingClient = em.find(Client.class, client.getId());
            if (existingClient == null) {
                throw new EntityNotFoundException("Client avec l'ID " + client.getId() + " non trouvé");
            }

            // Mettre à jour les attributs
            existingClient.setNom(client.getNom());
            existingClient.setPrenom(client.getPrenom());
            existingClient.setAdresse(client.getAdresse());
            existingClient.setTelephone(client.getTelephone());

            em.merge(existingClient);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de la modification du client", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Client client = em.find(Client.class, id);
            if (client == null) {
                throw new EntityNotFoundException("Client avec l'ID " + id + " non trouvé");
            }

            em.remove(client);
            tx.commit();

        } catch (EntityNotFoundException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de la suppression du client", e);
        }
    }

    @Override
    public Client rechercher(int id) throws DatabaseException {
        try {
            return em.find(Client.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du client", e);
        }
    }

    @Override
    public List<Client> lister() throws DatabaseException {
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c", Client.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des clients", e);
        }
    }

    @Override
    public List<Client> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new InvalidParameterException("Le nom ne peut pas être null ou vide");
        }

        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.nom) LIKE LOWER(:nom)", Client.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des clients par nom", e);
        }
    }

    @Override
    public List<Client> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new InvalidParameterException("Le prénom ne peut pas être null ou vide");
        }

        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.prenom) LIKE LOWER(:prenom)", Client.class);
            query.setParameter("prenom", "%" + prenom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des clients par prénom", e);
        }
    }

    @Override
    public List<Client> filtrerParAdresse(String adresse) throws InvalidParameterException, DatabaseException {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new InvalidParameterException("L'adresse ne peut pas être null ou vide");
        }

        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE LOWER(c.adresse) LIKE LOWER(:adresse)", Client.class);
            query.setParameter("adresse", "%" + adresse + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des clients par adresse", e);
        }
    }

    @Override
    public List<Client> filtrerParTelephone(int telephone) throws InvalidParameterException, DatabaseException {
        if (telephone <= 0) {
            throw new InvalidParameterException("Le numéro de téléphone doit être positif");
        }

        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.telephone = :telephone", Client.class);
            query.setParameter("telephone", telephone);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des clients par téléphone", e);
        }
    }
}


