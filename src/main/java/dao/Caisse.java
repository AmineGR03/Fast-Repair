package dao;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class Caisse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idCaisse;
	private double soldeActuel;
	private LocalDateTime dernierMouvement;
	
	@OneToOne
	@JoinColumn(name = "idBoutique")
	private Boutique boutique;
}
