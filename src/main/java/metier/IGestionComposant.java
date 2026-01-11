package metier;

import java.util.List;

import dao.Composant;


import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionComposant {
	public void ajouter(Composant p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Composant p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Composant rechercher(int id) throws DatabaseException;
	public List<Composant> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Composant> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException;
	public List<Composant> filtrerParPrix(double prix) throws InvalidParameterException, DatabaseException;
	public List<Composant> filtrerParQuantite(int quantite) throws InvalidParameterException, DatabaseException;

}







