package dao;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder

public class Boutique {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idBoutique;
	private String nom;
	private String adresse;
	private int numTel;
	private int numP;
	
	@ManyToOne
	@JoinColumn(name = "idProprietaire")
	private Proprietaire proprietaire;
	
	@OneToOne(mappedBy = "boutique")
	private Caisse caisse;

}


