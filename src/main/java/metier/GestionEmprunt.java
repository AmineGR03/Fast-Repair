package metier;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Emprunt;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionEmprunt implements IGestionEmprunt {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionEmprunt() {
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
    public void ajouter(Emprunt emprunt) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (emprunt == null) {
                throw new InvalidParameterException("L'emprunt ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Emprunt existingEmprunt = em.find(Emprunt.class, emprunt.getIdEmprunt());
            if (existingEmprunt != null) {
                throw new DuplicateEntityException("Un emprunt avec l'ID " + emprunt.getIdEmprunt() + " existe dÃ©jÃ ");
            }

            em.persist(emprunt);
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
            throw new DatabaseException("Erreur lors de l'ajout de l'emprunt", e);
        }
    }

    @Override
    public void modifer(Emprunt emprunt) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (emprunt == null) {
                throw new InvalidParameterException("L'emprunt ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Emprunt existingEmprunt = em.find(Emprunt.class, emprunt.getIdEmprunt());
            if (existingEmprunt == null) {
                throw new EntityNotFoundException("Emprunt avec l'ID " + emprunt.getIdEmprunt() + " non trouvÃ©");
            }

            existingEmprunt.setDate(emprunt.getDate());
            existingEmprunt.setMontant(emprunt.getMontant());
            existingEmprunt.setType(emprunt.getType());
            existingEmprunt.setCommentaire(emprunt.getCommentaire());

            em.merge(existingEmprunt);
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
            throw new DatabaseException("Erreur lors de la modification de l'emprunt", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Emprunt emprunt = em.find(Emprunt.class, id);
            if (emprunt == null) {
                throw new EntityNotFoundException("Emprunt avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(emprunt);
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
            throw new DatabaseException("Erreur lors de la suppression de l'emprunt", e);
        }
    }

    @Override
    public Emprunt rechercher(int id) throws DatabaseException {
        try {
            return em.find(Emprunt.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche de l'emprunt", e);
        }
    }

    @Override
    public List<Emprunt> lister() throws DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery("SELECT e FROM Emprunt e", Emprunt.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des emprunts", e);
        }
    }

    @Override
    public List<Emprunt> filtrerParDate(LocalDateTime date) throws InvalidParameterException, DatabaseException {
        if (date == null) {
            throw new InvalidParameterException("La date ne peut pas Ãªtre null");
        }

        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.date = :date", Emprunt.class);
            query.setParameter("date", date);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des emprunts par date", e);
        }
    }

    @Override
    public List<Emprunt> filtrerParMontant(double montant) throws InvalidParameterException, DatabaseException {
        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.montant = :montant", Emprunt.class);
            query.setParameter("montant", montant);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des emprunts par montant", e);
        }
    }

    @Override
    public List<Emprunt> filtrerParType(String type) throws InvalidParameterException, DatabaseException {
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidParameterException("Le type ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE LOWER(e.type) LIKE LOWER(:type)", Emprunt.class);
            query.setParameter("type", "%" + type + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des emprunts par type", e);
        }
    }

    @Override
    public List<Emprunt> filtrerParCommentaire(String commentaire) throws InvalidParameterException, DatabaseException {
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new InvalidParameterException("Le commentaire ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Emprunt> query = em.createQuery(
                "SELECT e FROM Emprunt e WHERE LOWER(e.commentaire) LIKE LOWER(:commentaire)", Emprunt.class);
            query.setParameter("commentaire", "%" + commentaire + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des emprunts par commentaire", e);
        }
    }
}




