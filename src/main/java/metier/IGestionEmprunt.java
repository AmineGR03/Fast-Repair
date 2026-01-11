package metier;

import java.time.LocalDateTime;
import java.util.List;

import dao.Emprunt;

import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionEmprunt {
	public void ajouter(Emprunt p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Emprunt p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Emprunt rechercher(int id) throws DatabaseException;
	public List<Emprunt> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Emprunt> filtrerParDate(LocalDateTime date) throws InvalidParameterException, DatabaseException;
	public List<Emprunt> filtrerParMontant(double montant) throws InvalidParameterException, DatabaseException;
	public List<Emprunt> filtrerParType(String type) throws InvalidParameterException, DatabaseException;
	public List<Emprunt> filtrerParCommentaire(String commentaire) throws InvalidParameterException, DatabaseException;

	// Méthodes spéciales pour la gestion des fonds de caisse par les réparateurs
	public void ajouterFondsCaisse(int idCaisse, int idReparateur, double montant, String commentaire)
			throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void retirerFondsCaisse(int idCaisse, int idReparateur, double montant, String commentaire)
			throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public List<Emprunt> listerEmpruntsParCaisse(int idCaisse) throws DatabaseException;
	public List<Emprunt> listerEmpruntsParReparateur(int idReparateur) throws DatabaseException;

}







