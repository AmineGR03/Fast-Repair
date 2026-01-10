package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Composant;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionComposant implements IGestionComposant {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionComposant() {
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
    public void ajouter(Composant composant) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (composant == null) {
                throw new InvalidParameterException("Le composant ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Composant existingComposant = em.find(Composant.class, composant.getIdComposant());
            if (existingComposant != null) {
                throw new DuplicateEntityException("Un composant avec l'ID " + composant.getIdComposant() + " existe dÃ©jÃ ");
            }

            em.persist(composant);
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
            throw new DatabaseException("Erreur lors de l'ajout du composant", e);
        }
    }

    @Override
    public void modifer(Composant composant) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (composant == null) {
                throw new InvalidParameterException("Le composant ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Composant existingComposant = em.find(Composant.class, composant.getIdComposant());
            if (existingComposant == null) {
                throw new EntityNotFoundException("Composant avec l'ID " + composant.getIdComposant() + " non trouvÃ©");
            }

            existingComposant.setNom(composant.getNom());
            existingComposant.setPrix(composant.getPrix());
            existingComposant.setQuantite(composant.getQuantite());

            em.merge(existingComposant);
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
            throw new DatabaseException("Erreur lors de la modification du composant", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Composant composant = em.find(Composant.class, id);
            if (composant == null) {
                throw new EntityNotFoundException("Composant avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(composant);
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
            throw new DatabaseException("Erreur lors de la suppression du composant", e);
        }
    }

    @Override
    public Composant rechercher(int id) throws DatabaseException {
        try {
            return em.find(Composant.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du composant", e);
        }
    }

    @Override
    public List<Composant> lister() throws DatabaseException {
        try {
            TypedQuery<Composant> query = em.createQuery("SELECT c FROM Composant c", Composant.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des composants", e);
        }
    }

    @Override
    public List<Composant> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new InvalidParameterException("Le nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Composant> query = em.createQuery(
                "SELECT c FROM Composant c WHERE LOWER(c.nom) LIKE LOWER(:nom)", Composant.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des composants par nom", e);
        }
    }

    @Override
    public List<Composant> filtrerParPrix(double prix) throws InvalidParameterException, DatabaseException {
        if (prix < 0) {
            throw new InvalidParameterException("Le prix ne peut pas Ãªtre nÃ©gatif");
        }

        try {
            TypedQuery<Composant> query = em.createQuery(
                "SELECT c FROM Composant c WHERE c.prix = :prix", Composant.class);
            query.setParameter("prix", prix);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des composants par prix", e);
        }
    }

    @Override
    public List<Composant> filtrerParQuantite(int quantite) throws InvalidParameterException, DatabaseException {
        if (quantite < 0) {
            throw new InvalidParameterException("La quantitÃ© ne peut pas Ãªtre nÃ©gative");
        }

        try {
            TypedQuery<Composant> query = em.createQuery(
                "SELECT c FROM Composant c WHERE c.quantite = :quantite", Composant.class);
            query.setParameter("quantite", quantite);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des composants par quantitÃ©", e);
        }
    }
}




