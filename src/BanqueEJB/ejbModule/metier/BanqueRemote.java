package metier;

import java.util.List;

import javax.ejb.Remote;
import metier.entities.Compte;

@Remote
public interface BanqueRemote {

	public Compte addCompte(Compte cp);
	public Compte getCompte(Long code ); 
	public List<Compte> listComptes();
	public void verser(Long code , double mt);
	public void retirer(Long code , double mt);
	public void virement(Long cp1, Long cp2, double mt);
	public boolean removeCompte (Long code );
}
