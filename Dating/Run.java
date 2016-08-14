
public class Run {

	public static void main(String[] args) throws Exception {
		//PortalAccess portal = new OkCupid();
		PortalAccess portal = new Jappy();	
		//portal.readCSVTime();
		portal.getUseragent().settings.autoRedirect=true;
		portal.login();
		portal.visit(portal.getSearch(), 500);
		portal.clearFilter();	
		portal.loadThreadArray();
		portal.setFilterLocation(24,62,5500,false);
	}
}
