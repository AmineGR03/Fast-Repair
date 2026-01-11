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

public class Appareil {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idAppareil;
	private String imei;
	private String marque;
	private String modele;
	private String typeAppareil;
	

}


