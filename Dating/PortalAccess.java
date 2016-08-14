import java.util.List;
import com.jaunt.MultipleFound;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public interface PortalAccess {
	
	public void login();
	public void loadThreadArray();
	public boolean isLoggedIn() throws NotFound;
	/**
 	 * performs a click on an anchor element.
	 * 
	 * @param name attribut of the HTML tag
	 * @param value attribut value 
	 * @throws NotFound 
	 * @throws ResponseException Tries to reconnect, after some seconds
	 * @throws InterruptedException
	 */
	public void anchorByAttribut(String name, String value)throws NotFound, ResponseException, InterruptedException;
	/**
	 * clears search-filter
	 * 
	 * @throws NotFound
	 * @throws MultipleFound
	 * @throws ResponseException
	 * @throws InterruptedException
	 */
	public void clearFilter() throws NotFound, MultipleFound, ResponseException, InterruptedException;

// Getter
	public UserAgent getUseragent();
	public String getHost();
	public String getLogin();
	public String getSearch();
	public String getUser();
	public String getPass();
	public String getUserName();
	public String getPassName();
	public String getSession();
	public String getFilter();
	public String getMisc();
	public String getFormId();
	public String getAutologin();
	public int getSleep();
	
	// public void checkGallery();
	public String getProfil();
	/**
	 * Investigates search requests with max results under the limit of 1000. 
	 * First filter is age, second filter mariage status. Pushes the method sendMessages.
	 * Disadvatage: if second filter active, only "flirt-results" will show up.
	 * 
	 * @param i upper age
	 * @param j lower age
	 * @param k maritial status
	 * @param b did the last search revealed over 1000 results?
	 * @throws NotFound
	 * @throws MultipleFound
	 * @throws ResponseException
	 * @throws InterruptedException
	 */
	public void setFilterAge(int lower, int upper, boolean hitMax) throws NotFound, MultipleFound, ResponseException, InterruptedException;
	/**
	 * Investigates search requests with max results under the limit of 1000. 
	 * Only filter is age. Pushes the method sendMessages.
	 * Disadvatage: dont reaches results over 1000
	 * 
	 * @param i upper age
	 * @param j lower age
	 * @param b did the last search revealed over 1000 results?
	 * @throws NotFound
	 * @throws MultipleFound
	 * @throws ResponseException
	 * @throws InterruptedException
	 */
	public void setFilterMarital(int lower, int upper, int maritalId, boolean hitMax) throws NotFound, MultipleFound, ResponseException, InterruptedException;
	/**
	 * Sends a circular to all found users. It scrolls automatically through the results.
	 * 
	 * @throws InterruptedException
	 * @throws ResponseException
	 * @throws NotFound
	 */
	public void setFilterLocation(int lower, int upper, int zipCodeId, boolean hitMax) throws NotFound, MultipleFound, ResponseException, InterruptedException;
	public void sendCircular() throws InterruptedException, ResponseException, NotFound;
	//public boolean isAccessible() throws InterruptedException;
	public List<String> readCSVTime();
	public void printThreadState();
	public void runCrawling();
	public void waitForThreads();
	public void visit(UserAgent userAgent, String url, int milliSecond);
	public void visit(String url, int milliSecond);
	void anchorByChild(String child) throws NotFound, InterruptedException, ResponseException;
}
	
	
