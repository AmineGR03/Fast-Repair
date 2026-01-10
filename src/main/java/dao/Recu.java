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
public class Recu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRecu;
	private LocalDateTime date;
	private double montant;
	

}
