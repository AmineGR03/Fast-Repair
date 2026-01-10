package metier;

import java.util.List;

import dao.Appareil;
import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;


public interface IGestionAppareil {
	public void ajouter(Appareil p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Appareil p) throws EntityNotFoundException, DatabaseException, InvalidParameterException;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Appareil rechercher(int id) throws DatabaseException;
	public List<Appareil> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Appareil> filtrerParImei(String imei) throws InvalidParameterException, DatabaseException;
	public List<Appareil> filtrerParMarque(String marque) throws InvalidParameterException, DatabaseException;
	public List<Appareil> filtrerParModele(String modele) throws InvalidParameterException, DatabaseException;
	public List<Appareil> filtrerParTypeAppareil(String typeAppareil) throws InvalidParameterException, DatabaseException;

}

