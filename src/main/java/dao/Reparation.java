package dao;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder

public class Reparation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idReparation;
	
	private int idAppareil;
	private int idBoutique;
	private int idReparateur;
	
	private String codeSuivi;
	private LocalDateTime dateDepot;
	private String etat;
	private String commentaire;
	private double prixTotal;
	

}
