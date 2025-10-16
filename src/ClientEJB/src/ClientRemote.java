import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import metier.BanqueRemote;
import metier.entities.Compte;

public class ClientRemote {

	public static void main(String[] args) {
		try {	
			Context ctx=new InitialContext();
			BanqueRemote proxy=(BanqueRemote) ctx.lookup("ejb:BanqueEAR/BanqueEJB/BF!metier.BanqueRemote");
			System.out.println("apple methods");
			proxy.addCompte(new Compte());
			proxy.addCompte(new Compte());
			proxy.verser(1L , 20000);
			proxy.retirer(1L, 500);
			proxy.virement(1L, 2L, 300);
			
			List<Compte> cptes=proxy.listComptes();
			
			for (Compte c:cptes) {
				System.out.println("code Compte = "+c.getCode()+"   Solde = "+c.getSolde());
			}
		
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
