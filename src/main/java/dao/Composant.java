package dao;
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

public class Composant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idComposant;
	private String nom;
	private double prix;
	private int quantite;
	

}


