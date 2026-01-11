package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Proprietaire;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionProprietaire implements IGestionProprietaire {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionProprietaire() {
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
    public void ajouter(Proprietaire proprietaire) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (proprietaire == null) {
                throw new InvalidParameterException("Le propriétaire ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Proprietaire existingProprietaire = em.find(Proprietaire.class, proprietaire.getId());
            if (existingProprietaire != null) {
                throw new DuplicateEntityException("Un propriÃ©taire avec l'ID " + proprietaire.getId() + " existe dÃ©jÃ ");
            }

            em.persist(proprietaire);
            tx.commit();

        } catch (DuplicateEntityException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de l'ajout du propriétaire", e);
        }
    }

    @Override
    public void modifer(Proprietaire proprietaire) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (proprietaire == null) {
                throw new InvalidParameterException("Le propriétaire ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Proprietaire existingProprietaire = em.find(Proprietaire.class, proprietaire.getId());
            if (existingProprietaire == null) {
                throw new EntityNotFoundException("PropriÃ©taire avec l'ID " + proprietaire.getId() + " non trouvÃ©");
            }

            existingProprietaire.setNom(proprietaire.getNom());
            existingProprietaire.setPrenom(proprietaire.getPrenom());
            existingProprietaire.setEmail(proprietaire.getEmail());
            existingProprietaire.setMdp(proprietaire.getMdp());

            em.merge(existingProprietaire);
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
            throw new DatabaseException("Erreur lors de la modification du propriétaire", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Proprietaire proprietaire = em.find(Proprietaire.class, id);
            if (proprietaire == null) {
                throw new EntityNotFoundException("PropriÃ©taire avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(proprietaire);
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
            throw new DatabaseException("Erreur lors de la suppression du propriÃ©taire", e);
        }
    }

    @Override
    public Proprietaire rechercher(int id) throws DatabaseException {
        try {
            return em.find(Proprietaire.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du propriÃ©taire", e);
        }
    }

    @Override
    public List<Proprietaire> lister() throws DatabaseException {
        try {
            TypedQuery<Proprietaire> query = em.createQuery("SELECT p FROM Proprietaire p", Proprietaire.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des propriÃ©taires", e);
        }
    }

    @Override
    public List<Proprietaire> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new InvalidParameterException("Le nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE LOWER(p.nom) LIKE LOWER(:nom)", Proprietaire.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des propriÃ©taires par nom", e);
        }
    }

    @Override
    public List<Proprietaire> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new InvalidParameterException("Le prÃ©nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE LOWER(p.prenom) LIKE LOWER(:prenom)", Proprietaire.class);
            query.setParameter("prenom", "%" + prenom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des propriÃ©taires par prÃ©nom", e);
        }
    }

    @Override
    public List<Proprietaire> filtrerParEmail(String email) throws InvalidParameterException, DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidParameterException("L'email ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE LOWER(p.email) LIKE LOWER(:email)", Proprietaire.class);
            query.setParameter("email", "%" + email + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des propriÃ©taires par email", e);
        }
    }

    @Override
    public List<Proprietaire> filtrerParMdp(String mdp) throws InvalidParameterException, DatabaseException {
        if (mdp == null || mdp.trim().isEmpty()) {
            throw new InvalidParameterException("Le mot de passe ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE p.mdp = :mdp", Proprietaire.class);
            query.setParameter("mdp", mdp);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des propriÃ©taires par mot de passe", e);
        }
    }

    @Override
    public Proprietaire rechercherParEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            TypedQuery<Proprietaire> query = em.createQuery(
                "SELECT p FROM Proprietaire p WHERE LOWER(p.email) = LOWER(:email)", Proprietaire.class);
            query.setParameter("email", email.trim());
            java.util.List<Proprietaire> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du propriÃ©taire par email", e);
        }
    }
}






