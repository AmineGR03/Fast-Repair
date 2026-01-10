package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Reparateur;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionReparateur implements IGestionReparateur {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionReparateur() {
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
    public void ajouter(Reparateur reparateur) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
            if (reparateur == null) {
                throw new InvalidParameterException("Le réparateur ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Reparateur existingReparateur = em.find(Reparateur.class, reparateur.getId());
            if (existingReparateur != null) {
                throw new DuplicateEntityException("Un rÃ©parateur avec l'ID " + reparateur.getId() + " existe dÃ©jÃ ");
            }

            em.persist(reparateur);
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
            throw new DatabaseException("Erreur lors de l'ajout du rÃ©parateur", e);
        }
    }

    @Override
    public void modifer(Reparateur reparateur) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
            if (reparateur == null) {
                throw new InvalidParameterException("Le réparateur ne peut pas être null");
            }
        } catch (InvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Reparateur existingReparateur = em.find(Reparateur.class, reparateur.getId());
            if (existingReparateur == null) {
                throw new EntityNotFoundException("RÃ©parateur avec l'ID " + reparateur.getId() + " non trouvÃ©");
            }

            existingReparateur.setNom(reparateur.getNom());
            existingReparateur.setPrenom(reparateur.getPrenom());
            existingReparateur.setEmail(reparateur.getEmail());
            existingReparateur.setMdp(reparateur.getMdp());
            existingReparateur.setPourcentageGain(reparateur.getPourcentageGain());

            em.merge(existingReparateur);
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
            throw new DatabaseException("Erreur lors de la modification du rÃ©parateur", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Reparateur reparateur = em.find(Reparateur.class, id);
            if (reparateur == null) {
                throw new EntityNotFoundException("RÃ©parateur avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(reparateur);
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
            throw new DatabaseException("Erreur lors de la suppression du rÃ©parateur", e);
        }
    }

    @Override
    public Reparateur rechercher(int id) throws DatabaseException {
        try {
            return em.find(Reparateur.class, id);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du rÃ©parateur", e);
        }
    }

    @Override
    public List<Reparateur> lister() throws DatabaseException {
        try {
            TypedQuery<Reparateur> query = em.createQuery("SELECT r FROM Reparateur r", Reparateur.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du listage des rÃ©parateurs", e);
        }
    }

    @Override
    public List<Reparateur> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new InvalidParameterException("Le nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE LOWER(r.nom) LIKE LOWER(:nom)", Reparateur.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parateurs par nom", e);
        }
    }

    @Override
    public List<Reparateur> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new InvalidParameterException("Le prÃ©nom ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE LOWER(r.prenom) LIKE LOWER(:prenom)", Reparateur.class);
            query.setParameter("prenom", "%" + prenom + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parateurs par prÃ©nom", e);
        }
    }

    @Override
    public List<Reparateur> filtrerParEmail(String email) throws InvalidParameterException, DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidParameterException("L'email ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE LOWER(r.email) LIKE LOWER(:email)", Reparateur.class);
            query.setParameter("email", "%" + email + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parateurs par email", e);
        }
    }

    @Override
    public List<Reparateur> filtrerParMdp(String mdp) throws InvalidParameterException, DatabaseException {
        if (mdp == null || mdp.trim().isEmpty()) {
            throw new InvalidParameterException("Le mot de passe ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.mdp = :mdp", Reparateur.class);
            query.setParameter("mdp", mdp);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parateurs par mot de passe", e);
        }
    }

    @Override
    public List<Reparateur> filtrerParPourcentageGain(double pourcentageGain) throws InvalidParameterException, DatabaseException {
        if (pourcentageGain < 0) {
            throw new InvalidParameterException("Le pourcentage de gain ne peut pas Ãªtre nÃ©gatif");
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE r.pourcentageGain = :pourcentageGain", Reparateur.class);
            query.setParameter("pourcentageGain", pourcentageGain);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des rÃ©parateurs par pourcentage de gain", e);
        }
    }

    @Override
    public Reparateur rechercherParEmail(String email) throws DatabaseException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            TypedQuery<Reparateur> query = em.createQuery(
                "SELECT r FROM Reparateur r WHERE LOWER(r.email) = LOWER(:email)", Reparateur.class);
            query.setParameter("email", email.trim());
            java.util.List<Reparateur> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la recherche du rÃ©parateur par email", e);
        }
    }
}





