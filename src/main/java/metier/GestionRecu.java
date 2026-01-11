package metier;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Recu;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionRecu implements IGestionRecu {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionRecu() {
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
    public void ajouter(Recu recu) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (recu == null) {
                throw new InvalidParameterException("Le reçu ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Recu existingRecu = em.find(Recu.class, recu.getIdRecu());
            if (existingRecu != null) {
                throw new DuplicateEntityException("Un reÃ§u avec l'ID " + recu.getIdRecu() + " existe dÃ©jÃ ");
            }

            em.persist(recu);
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
            throw new DatabaseException("Erreur lors de l'ajout du reçu", e);
        }
    }

    @Override
    public void modifer(Recu recu) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (recu == null) {
                throw new InvalidParameterException("Le reçu ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Recu existingRecu = em.find(Recu.class, recu.getIdRecu());
            if (existingRecu == null) {
                throw new EntityNotFoundException("ReÃ§u avec l'ID " + recu.getIdRecu() + " non trouvÃ©");
            }

            existingRecu.setDate(recu.getDate());
            existingRecu.setMontant(recu.getMontant());

            em.merge(existingRecu);
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
            throw new DatabaseException("Erreur lors de la modification du reÃ§u", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Recu recu = em.find(Recu.class, id);
            if (recu == null) {
                throw new EntityNotFoundException("ReÃ§u avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(recu);
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
            throw new DatabaseException("Erreur lors de la suppression du reÃ§u", e);
        }
    }

    @Override
    public Recu rechercher(int id) throws DatabaseException {
        try {
            return em.find(Recu.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du reÃ§u", e);
        }
    }

    @Override
    public List<Recu> lister() throws DatabaseException {
        try {
            TypedQuery<Recu> query = em.createQuery("SELECT r FROM Recu r", Recu.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des reÃ§us", e);
        }
    }

    @Override
    public List<Recu> filtrerParDate(LocalDateTime date) throws InvalidParameterException, DatabaseException {
        if (date == null) {
            throw new InvalidParameterException("La date ne peut pas Ãªtre null");
        }

        try {
            TypedQuery<Recu> query = em.createQuery(
                "SELECT r FROM Recu r WHERE r.date = :date", Recu.class);
            query.setParameter("date", date);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des reÃ§us par date", e);
        }
    }

    @Override
    public List<Recu> filtrerParMontant(double montant) throws InvalidParameterException, DatabaseException {
        try {
            TypedQuery<Recu> query = em.createQuery(
                "SELECT r FROM Recu r WHERE r.montant = :montant", Recu.class);
            query.setParameter("montant", montant);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des reÃ§us par montant", e);
        }
    }
}






