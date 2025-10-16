package service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import metier.BanqueLocal;
import metier.entities.Compte;
import java.util.List;
import javax.ejb.EJB;


@WebService
public class BanqueService {
	@EJB 
	private BanqueLocal service ;
	
	
	@WebMethod
	public Compte addCompte(@WebParam(name = "compte")Compte cp) {
		return service.addCompte(cp);
	}
	
	@WebMethod
	public Compte getCompte(@WebParam(name="code")Long code) {
		return service.getCompte(code);
	}
	
	@WebMethod
	public List<Compte> listComptes() {
		return service.listComptes();
	}
	
	@WebMethod
	public void verser(@WebParam(name="code")Long code,
					   @WebParam(name="montant")double mt) {
		service.verser(code, mt);
	}
	
	@WebMethod
	public void retirer(@WebParam(name="code")Long code,
						@WebParam(name="montant")double mt) {
		service.retirer(code, mt);
	}
	
	@WebMethod
	public void virement(@WebParam(name="cp1")Long cp1,
						 @WebParam(name="cp2") Long cp2,
						 @WebParam(name="montant") double mt) {
		service.virement(cp1, cp2, mt);
	}
	
	@WebMethod
	public boolean removeCompte(@WebParam(name="code")Long code) {
		return service.removeCompte(code);
	}
}
