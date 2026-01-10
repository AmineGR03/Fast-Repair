package metier;

import java.util.List;

import dao.Client;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;

public interface IGestionClient {
	public void ajouter(Client p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Client p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Client rechercher(int id) throws DatabaseException;
	public List<Client> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Client> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException;
	public List<Client> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException;
	public List<Client> filtrerParAdresse(String adresse) throws InvalidParameterException, DatabaseException;
	public List<Client> filtrerParTelephone(int telephone) throws InvalidParameterException, DatabaseException;

}

