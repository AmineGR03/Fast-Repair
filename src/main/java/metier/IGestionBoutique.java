package metier;

import java.util.List;

import dao.Boutique;



import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionBoutique {
	public void ajouter(Boutique p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Boutique p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Boutique rechercher(int id) throws DatabaseException;
	public List<Boutique> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Boutique> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException;
	public List<Boutique> filtrerParAdresse(String adresse) throws InvalidParameterException, DatabaseException;
	public List<Boutique> filtrerParNumTel(int numTel) throws InvalidParameterException, DatabaseException;
	public List<Boutique> filtrerParNumP(int numP) throws InvalidParameterException, DatabaseException;

}




