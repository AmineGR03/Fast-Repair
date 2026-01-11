package metier;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Caisse;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionCaisse implements IGestionCaisse {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionCaisse() {
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
    public void ajouter(Caisse caisse) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (caisse == null) {
                throw new InvalidParameterException("La caisse ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Caisse existingCaisse = em.find(Caisse.class, caisse.getIdCaisse());
            if (existingCaisse != null) {
                throw new DuplicateEntityException("Une caisse avec l'ID " + caisse.getIdCaisse() + " existe dÃ©jÃ ");
            }

            em.persist(caisse);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de l'ajout de la caisse", e);
        }
    }

    @Override
    public void modifer(Caisse caisse) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (caisse == null) {
                throw new InvalidParameterException("La caisse ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Caisse existingCaisse = em.find(Caisse.class, caisse.getIdCaisse());
            if (existingCaisse == null) {
                throw new EntityNotFoundException("Caisse avec l'ID " + caisse.getIdCaisse() + " non trouvÃ©e");
            }

            existingCaisse.setSoldeActuel(caisse.getSoldeActuel());
            existingCaisse.setDernierMouvement(caisse.getDernierMouvement());

            em.merge(existingCaisse);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de la modification de la caisse", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Caisse caisse = em.find(Caisse.class, id);
            if (caisse == null) {
                throw new EntityNotFoundException("Caisse avec l'ID " + id + " non trouvÃ©e");
            }

            em.remove(caisse);
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
            throw new DatabaseException("Erreur lors de la suppression de la caisse", e);
        }
    }

    @Override
    public Caisse rechercher(int id) throws DatabaseException {
        try {
            return em.find(Caisse.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche de la caisse", e);
        }
    }

    @Override
    public List<Caisse> lister() throws DatabaseException {
        try {
            TypedQuery<Caisse> query = em.createQuery("SELECT c FROM Caisse c", Caisse.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des caisses", e);
        }
    }

    @Override
    public List<Caisse> filtrerParSoldeActuel(double soldeActuel) throws InvalidParameterException, DatabaseException {
        try {
            TypedQuery<Caisse> query = em.createQuery(
                "SELECT c FROM Caisse c WHERE c.soldeActuel = :soldeActuel", Caisse.class);
            query.setParameter("soldeActuel", soldeActuel);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des caisses par solde actuel", e);
        }
    }

    @Override
    public List<Caisse> filtrerParDernierMouvement(LocalDateTime dernierMouvement) throws InvalidParameterException, DatabaseException {
        if (dernierMouvement == null) {
            throw new InvalidParameterException("Le dernier mouvement ne peut pas Ãªtre null");
        }

        try {
            TypedQuery<Caisse> query = em.createQuery(
                "SELECT c FROM Caisse c WHERE c.dernierMouvement = :dernierMouvement", Caisse.class);
            query.setParameter("dernierMouvement", dernierMouvement);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des caisses par dernier mouvement", e);
        }
    }

    @Override
    public boolean caisseExistePourBoutique(int idBoutique) throws DatabaseException {
        if (idBoutique <= 0) {
            return false;
        }

        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Caisse c WHERE c.boutique.idBoutique = :idBoutique", Long.class);
            query.setParameter("idBoutique", idBoutique);
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la vérification de l'existence d'une caisse pour la boutique " + idBoutique, e);
        }
    }
}





