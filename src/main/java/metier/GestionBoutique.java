package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Boutique;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionBoutique implements IGestionBoutique {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionBoutique() {
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
    public void ajouter(Boutique boutique) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (boutique == null) {
                throw new InvalidParameterException("La boutique ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Vérifier si une boutique avec le même ID existe déjà
            Boutique existingBoutique = em.find(Boutique.class, boutique.getIdBoutique());
            if (existingBoutique != null) {
                throw new DuplicateEntityException("Une boutique avec l'ID " + boutique.getIdBoutique() + " existe dÃ©jÃ ");
            }

            // Persister la boutique d'abord
            em.persist(boutique);

            // Créer automatiquement une caisse pour cette boutique
            dao.Caisse caisse = dao.Caisse.builder()
                .soldeActuel(0.0)
                .dernierMouvement(java.time.LocalDateTime.now())
                .boutique(boutique)
                .build();

            em.persist(caisse);

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
            throw new DatabaseException("Erreur lors de l'ajout de la boutique", e);
        }
    }

    @Override
    public void modifer(Boutique boutique) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (boutique == null) {
                throw new InvalidParameterException("La boutique ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Boutique existingBoutique = em.find(Boutique.class, boutique.getIdBoutique());
            if (existingBoutique == null) {
                throw new EntityNotFoundException("Boutique avec l'ID " + boutique.getIdBoutique() + " non trouvÃ©e");
            }

            // Mettre Ã  jour les attributs
            existingBoutique.setNom(boutique.getNom());
            existingBoutique.setAdresse(boutique.getAdresse());
            existingBoutique.setNumTel(boutique.getNumTel());
            existingBoutique.setNumP(boutique.getNumP());

            em.merge(existingBoutique);
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
            throw new DatabaseException("Erreur lors de la modification de la boutique", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Boutique boutique = em.find(Boutique.class, id);
            if (boutique == null) {
                throw new EntityNotFoundException("Boutique avec l'ID " + id + " non trouvÃ©e");
            }

            em.remove(boutique);
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
            throw new DatabaseException("Erreur lors de la suppression de la boutique", e);
        }
    }

    @Override
    public Boutique rechercher(int id) throws DatabaseException {
        try {
            return em.find(Boutique.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche de la boutique", e);
        }
    }

    @Override
    public List<Boutique> lister() throws DatabaseException {
        try {
            TypedQuery<Boutique> query = em.createQuery("SELECT b FROM Boutique b", Boutique.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des boutiques", e);
        }
    }

    @Override
    public List<Boutique> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new InvalidParameterException("Le nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE LOWER(b.nom) LIKE LOWER(:nom)", Boutique.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des boutiques par nom", e);
        }
    }

    @Override
    public List<Boutique> filtrerParAdresse(String adresse) throws InvalidParameterException, DatabaseException {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new InvalidParameterException("L'adresse ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE LOWER(b.adresse) LIKE LOWER(:adresse)", Boutique.class);
            query.setParameter("adresse", "%" + adresse + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des boutiques par adresse", e);
        }
    }

    @Override
    public List<Boutique> filtrerParNumTel(int numTel) throws InvalidParameterException, DatabaseException {
        if (numTel <= 0) {
            throw new InvalidParameterException("Le numÃ©ro de tÃ©lÃ©phone doit Ãªtre positif");
        }

        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.numTel = :numTel", Boutique.class);
            query.setParameter("numTel", numTel);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des boutiques par numÃ©ro de tÃ©lÃ©phone", e);
        }
    }

    @Override
    public List<Boutique> filtrerParNumP(int numP) throws InvalidParameterException, DatabaseException {
        if (numP <= 0) {
            throw new InvalidParameterException("Le numÃ©ro P doit Ãªtre positif");
        }

        try {
            TypedQuery<Boutique> query = em.createQuery(
                "SELECT b FROM Boutique b WHERE b.numP = :numP", Boutique.class);
            query.setParameter("numP", numP);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des boutiques par numÃ©ro P", e);
        }
    }
}




