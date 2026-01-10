package metier;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Reparation;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionReparation implements IGestionReparation {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionReparation() {
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
    public void ajouter(Reparation reparation) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (reparation == null) {
                throw new InvalidParameterException("La réparation ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // VÃ©rifier si une rÃ©paration avec le mÃªme ID existe dÃ©jÃ 
            Reparation existingReparation = em.find(Reparation.class, reparation.getIdAppareil());
            if (existingReparation != null) {
                throw new DuplicateEntityException("Une rÃ©paration avec l'ID appareil " + reparation.getIdAppareil() + " existe dÃ©jÃ ");
            }

            em.persist(reparation);
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
            throw new DatabaseException("Erreur lors de l'ajout de la rÃ©paration", e);
        }
    }

    @Override
    public void modifer(Reparation reparation) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (reparation == null) {
                throw new InvalidParameterException("La réparation ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Reparation existingReparation = em.find(Reparation.class, reparation.getIdAppareil());
            if (existingReparation == null) {
                throw new EntityNotFoundException("RÃ©paration avec l'ID appareil " + reparation.getIdAppareil() + " non trouvÃ©e");
            }

            // Mettre Ã  jour les attributs
            existingReparation.setCodeSuivi(reparation.getCodeSuivi());
            existingReparation.setDateDepot(reparation.getDateDepot());
            existingReparation.setEtat(reparation.getEtat());
            existingReparation.setCommentaire(reparation.getCommentaire());
            existingReparation.setPrixTotal(reparation.getPrixTotal());

            em.merge(existingReparation);
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
            throw new DatabaseException("Erreur lors de la modification de la rÃ©paration", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Reparation reparation = em.find(Reparation.class, id);
            if (reparation == null) {
                throw new EntityNotFoundException("RÃ©paration avec l'ID appareil " + id + " non trouvÃ©e");
            }

            em.remove(reparation);
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
            throw new DatabaseException("Erreur lors de la suppression de la rÃ©paration", e);
        }
    }

    @Override
    public Reparation rechercher(int id) throws DatabaseException {
        try {
            return em.find(Reparation.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche de la rÃ©paration", e);
        }
    }

    @Override
    public List<Reparation> lister() throws DatabaseException {
        try {
            TypedQuery<Reparation> query = em.createQuery("SELECT r FROM Reparation r", Reparation.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des rÃ©parations", e);
        }
    }

    @Override
    public List<Reparation> filtrerParCodeSuivi(String codeSuivi) throws InvalidParameterException, DatabaseException {
        if (codeSuivi == null || codeSuivi.trim().isEmpty()) {
            throw new InvalidParameterException("Le code de suivi ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE LOWER(r.codeSuivi) LIKE LOWER(:codeSuivi)", Reparation.class);
            query.setParameter("codeSuivi", "%" + codeSuivi + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parations par code de suivi", e);
        }
    }

    @Override
    public List<Reparation> filtrerParDateDepot(LocalDateTime dateDepot) throws InvalidParameterException, DatabaseException {
        if (dateDepot == null) {
            throw new InvalidParameterException("La date de dÃ©pÃ´t ne peut pas Ãªtre null");
        }

        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.dateDepot = :dateDepot", Reparation.class);
            query.setParameter("dateDepot", dateDepot);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parations par date de dÃ©pÃ´t", e);
        }
    }

    @Override
    public List<Reparation> filtrerParEtat(String etat) throws InvalidParameterException, DatabaseException {
        if (etat == null || etat.trim().isEmpty()) {
            throw new InvalidParameterException("L'Ã©tat ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE LOWER(r.etat) LIKE LOWER(:etat)", Reparation.class);
            query.setParameter("etat", "%" + etat + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parations par Ã©tat", e);
        }
    }

    @Override
    public List<Reparation> filtrerParCommentaire(String commentaire) throws InvalidParameterException, DatabaseException {
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new InvalidParameterException("Le commentaire ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE LOWER(r.commentaire) LIKE LOWER(:commentaire)", Reparation.class);
            query.setParameter("commentaire", "%" + commentaire + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parations par commentaire", e);
        }
    }

    @Override
    public List<Reparation> filtrerParPrixTotal(double prixTotal) throws InvalidParameterException, DatabaseException {
        if (prixTotal < 0) {
            throw new InvalidParameterException("Le prix total ne peut pas Ãªtre nÃ©gatif");
        }

        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.prixTotal = :prixTotal", Reparation.class);
            query.setParameter("prixTotal", prixTotal);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parations par prix total", e);
        }
    }
}





