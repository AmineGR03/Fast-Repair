package metier;

import java.time.LocalDateTime;
import java.util.List;

import dao.Recu;

import exception.DatabaseException;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.InvalidParameterException;
public interface IGestionRecu {
	public void ajouter(Recu p) throws DuplicateEntityException, DatabaseException, InvalidParameterException;
	public void modifer(Recu p) throws EntityNotFoundException, DatabaseException, InvalidParameterException, Exception;
	public void supprimer(int id) throws EntityNotFoundException, DatabaseException;
	public Recu rechercher(int id) throws DatabaseException;
	public List<Recu> lister() throws DatabaseException;

	// Filtrage par attributs
	public List<Recu> filtrerParDate(LocalDateTime date) throws InvalidParameterException, DatabaseException;
	public List<Recu> filtrerParMontant(double montant) throws InvalidParameterException, DatabaseException;

}





