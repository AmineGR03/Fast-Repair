package metier;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import dao.Appareil;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public class GestionAppareil implements IGestionAppareil {

    private EntityManagerFactory emf;
    private EntityManager em;

    public GestionAppareil() {
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
    public void ajouter(Appareil appareil) throws DuplicateEntityException, DatabaseException, InvalidParameterException {
        try {
			if (appareil == null) {
			    throw new InvalidParameterException("L'appareil ne peut pas Ãªtre null");
			}
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            // Vérifier si un appareil avec le même IMEI existe déjà (IMEI doit être unique)
            if (appareil.getImei() != null && !appareil.getImei().trim().isEmpty()) {
                TypedQuery<Appareil> query = em.createQuery(
                    "SELECT a FROM Appareil a WHERE a.imei = :imei", Appareil.class);
                query.setParameter("imei", appareil.getImei());
                List<Appareil> existing = query.getResultList();
                if (!existing.isEmpty()) {
                    throw new DuplicateEntityException("Un appareil avec l'IMEI " + appareil.getImei() + " existe déjà");
                }
            }

            em.persist(appareil);
            // Récupérer l'ID généré après persist
            em.flush();
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
            throw new DatabaseException("Erreur lors de l'ajout de l'appareil", e);
        }
    }

    @Override
    public void modifer(Appareil appareil) throws EntityNotFoundException, DatabaseException, InvalidParameterException {
        try {
			if (appareil == null) {
			    throw new InvalidParameterException("L'appareil ne peut pas Ãªtre null");
			}
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Appareil existingAppareil = em.find(Appareil.class, appareil.getIdAppareil());
            if (existingAppareil == null) {
                throw new EntityNotFoundException("Appareil avec l'ID " + appareil.getIdAppareil() + " non trouvÃ©");
            }

            // Mettre Ã  jour les attributs
            existingAppareil.setImei(appareil.getImei());
            existingAppareil.setMarque(appareil.getMarque());
            existingAppareil.setModele(appareil.getModele());
            existingAppareil.setTypeAppareil(appareil.getTypeAppareil());

            em.merge(existingAppareil);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Erreur lors de la modification de l'appareil", e);
        }
    }

    @Override
    public void supprimer(int id) throws EntityNotFoundException, DatabaseException {
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            Appareil appareil = em.find(Appareil.class, id);
            if (appareil == null) {
                throw new EntityNotFoundException("Appareil avec l'ID " + id + " non trouvÃ©");
            }

            em.remove(appareil);
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
            throw new DatabaseException("Erreur lors de la suppression de l'appareil", e);
        }
    }

    @Override
    public Appareil rechercher(int id) throws DatabaseException {
        try {
            // S'assurer que l'EntityManager est ouvert
            if (!em.isOpen()) {
                this.em = emf.createEntityManager();
            }
            // Rafraîchir le cache
            em.clear();
            Appareil result = em.find(Appareil.class, id);
            if (result != null) {
                System.out.println("Appareil trouvé - ID: " + result.getIdAppareil() + ", IMEI: " + result.getImei());
            } else {
                System.out.println("Aucun appareil trouvé avec l'ID: " + id);
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erreur dans rechercher(): " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Erreur lors de la recherche de l'appareil: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> lister() throws DatabaseException {
        try {
            // S'assurer que l'EntityManager est ouvert
            if (!em.isOpen()) {
                this.em = emf.createEntityManager();
            }
            // Rafraîchir le cache pour avoir les dernières données
            em.clear();
            TypedQuery<Appareil> query = em.createQuery("SELECT a FROM Appareil a", Appareil.class);
            List<Appareil> result = query.getResultList();
            System.out.println("Nombre d'appareils trouvés: " + result.size());
            return result;
        } catch (Exception e) {
            System.err.println("Erreur dans lister(): " + e.getMessage());
            e.printStackTrace();
            throw new DatabaseException("Erreur lors du listage des appareils: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appareil> filtrerParImei(String imei) throws InvalidParameterException, DatabaseException {
        if (imei == null || imei.trim().isEmpty()) {
            throw new InvalidParameterException("L'IMEI ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE LOWER(a.imei) LIKE LOWER(:imei)", Appareil.class);
            query.setParameter("imei", "%" + imei + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des appareils par IMEI", e);
        }
    }

    @Override
    public List<Appareil> filtrerParMarque(String marque) throws InvalidParameterException, DatabaseException {
        if (marque == null || marque.trim().isEmpty()) {
            throw new InvalidParameterException("La marque ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE LOWER(a.marque) LIKE LOWER(:marque)", Appareil.class);
            query.setParameter("marque", "%" + marque + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des appareils par marque", e);
        }
    }

    @Override
    public List<Appareil> filtrerParModele(String modele) throws InvalidParameterException, DatabaseException {
        if (modele == null || modele.trim().isEmpty()) {
            throw new InvalidParameterException("Le modÃ¨le ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE LOWER(a.modele) LIKE LOWER(:modele)", Appareil.class);
            query.setParameter("modele", "%" + modele + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des appareils par modÃ¨le", e);
        }
    }

    @Override
    public List<Appareil> filtrerParTypeAppareil(String typeAppareil) throws InvalidParameterException, DatabaseException {
        if (typeAppareil == null || typeAppareil.trim().isEmpty()) {
            throw new InvalidParameterException("Le type d'appareil ne peut pas Ãªtre null ou vide");
        }

        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE LOWER(a.typeAppareil) LIKE LOWER(:typeAppareil)", Appareil.class);
            query.setParameter("typeAppareil", "%" + typeAppareil + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors du filtrage des appareils par type", e);
        }
    }
}

