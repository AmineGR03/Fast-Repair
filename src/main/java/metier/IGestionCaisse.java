package metier;

import java.time.LocalDateTime;
import java.util.List;

import dao.Caisse;


import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionCaisse {
	public void ajouter(Caisse p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Caisse p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Caisse rechercher(int id) throws DatabaseException;
	public List<Caisse> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Caisse> filtrerParSoldeActuel(double soldeActuel) throws InvalidParameterException, DatabaseException;
	public List<Caisse> filtrerParDernierMouvement(LocalDateTime dernierMouvement) throws InvalidParameterException, DatabaseException;

}





