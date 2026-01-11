package metier;

import java.time.LocalDateTime;
import java.util.List;

import dao.Reparation;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public interface IGestionReparation {
	public void ajouter(Reparation p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Reparation p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Reparation rechercher(int id) throws DatabaseException;
	public List<Reparation> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Reparation> filtrerParCodeSuivi(String codeSuivi) throws InvalidParameterException, DatabaseException;
	public List<Reparation> filtrerParDateDepot(LocalDateTime dateDepot) throws InvalidParameterException, DatabaseException;
	public List<Reparation> filtrerParEtat(String etat) throws InvalidParameterException, DatabaseException;
	public List<Reparation> filtrerParCommentaire(String commentaire) throws InvalidParameterException, DatabaseException;
	public List<Reparation> filtrerParPrixTotal(double prixTotal) throws InvalidParameterException, DatabaseException;

}


