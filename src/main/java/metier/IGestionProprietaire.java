package metier;

import java.util.List;

import dao.Proprietaire;

import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionProprietaire {
	public void ajouter(Proprietaire p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Proprietaire p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Proprietaire rechercher(int id) throws DatabaseException;
	public List<Proprietaire> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Proprietaire> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException;
	public List<Proprietaire> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException;
	public List<Proprietaire> filtrerParEmail(String email) throws InvalidParameterException, DatabaseException;
	public List<Proprietaire> filtrerParMdp(String mdp) throws InvalidParameterException, DatabaseException;

	// Authentification
	public Proprietaire rechercherParEmail(String email) throws DatabaseException;

}





