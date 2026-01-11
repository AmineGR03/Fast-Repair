package dao;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="role")
public abstract class Personne {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected int id;
	protected String nom;
	protected String prenom;
}




