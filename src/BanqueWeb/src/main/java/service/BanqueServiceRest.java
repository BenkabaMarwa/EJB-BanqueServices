package service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import metier.BanqueLocal;
import metier.entities.Compte;

@Stateless
@Path("/")

public class BanqueServiceRest {
	
	@EJB
	BanqueLocal service;

	@POST
	@Path("/copmtes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Compte addCompte(Compte cp) {
		return service.addCompte(cp);
	}
	
	@GET
	@Path("/comptes/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	public Compte getCompte(@PathParam(value="code") Long code) {
		return service.getCompte(code);
	}

	@GET
	@Path("/comptes")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public List<Compte> listComptes() {
		return service.listComptes();
	}

//	@PUT
//	@Path("/comptes/verser")
//	public void verser(@FormParam(value="code") Long code,
//					   @FormParam(value="montant") double mt) {
//		service.verser(code, mt);
//	}
	
	@PUT
	@Path("/comptes/{code}/depot/{montant}")
	public void verser(@PathParam("code") Long code,
	                   @PathParam("montant") double mt) {
	    service.verser(code, mt);
	}

//	@PUT
//	@Path("/comptes/retirer")
//	public void retirer(@FormParam(value="code")Long code,
//						@FormParam(value="montant")double mt) {
//		service.retirer(code, mt);
//	}
	
	@PUT
	@Path("/comptes/{code}/retrait/{montant}")
	public void retirer(@PathParam("code") Long code,
	                    @PathParam("montant") double mt) {
	    service.retirer(code, mt);
	}


//	@PUT
//	@Path("/comptes/virement")
//	public void virement(@FormParam(value="cp1")Long cp1,
//						 @FormParam(value="cp2")Long cp2,
//						 @FormParam(value="montant")double mt) {
//		service.virement(cp1, cp2, mt);
//	}
	
	@PUT
	@Path("/comptes/{cp1}/virement/{cp2}/{montant}")
	public void virement(@PathParam("cp1") Long cp1,
	                     @PathParam("cp2") Long cp2,
	                     @PathParam("montant") double mt) {
	    service.virement(cp1, cp2, mt);
	}


	@DELETE
	@Path("/comptes/{code}")
	public boolean removeCompte(@PathParam(value="code") Long code) {
		return service.removeCompte(code);
	}	
}