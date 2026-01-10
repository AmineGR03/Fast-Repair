package metier;

import java.util.List;


import dao.Reparateur;

import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionReparateur {
	public void ajouter(Reparateur p) throws DuplicateEntityException, DatabaseException, InvalidParameterException, Exception;
	public void modifer(Reparateur p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Reparateur rechercher(int id) throws DatabaseException;
	public List<Reparateur> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Reparateur> filtrerParNom(String nom) throws InvalidParameterException, DatabaseException;
	public List<Reparateur> filtrerParPrenom(String prenom) throws InvalidParameterException, DatabaseException;
	public List<Reparateur> filtrerParEmail(String email) throws InvalidParameterException, DatabaseException;
	public List<Reparateur> filtrerParMdp(String mdp) throws InvalidParameterException, DatabaseException;
	public List<Reparateur> filtrerParPourcentageGain(double pourcentageGain) throws InvalidParameterException, DatabaseException;

	// Authentification
	public Reparateur rechercherParEmail(String email) throws DatabaseException;

}



