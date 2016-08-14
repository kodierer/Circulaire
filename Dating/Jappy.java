import com.jaunt.*;
import com.jaunt.component.*;

public class Jappy extends Portal {
	static final UserAgent USERAGENT = new UserAgent();
	// path
	static final String HOST = "http://www.jappy.de";
	private static final String LOGIN_PATH = "";
	static final String SEARCH_PATH = "/search";
	static final String PROFIL_PATH = "/user/";
	private static final String FILTER_PATH = "/search/standard/settings";
	private static final String FILTER_FORM_PATH = "/search/standard/settings/alter";
	private static final String MISC_PATH = "/misc";
	static final String CONVERSATION_PATH = "/mailbox/conversation/";
	static final String CONVERS_FORM_TAG = "<form id=\"textform\">";
	// login data
	private static final String USERNAME = "";
	private static final String PASSWORD = "";
	// attribute values
	private static final String USER_TEXTFIELD_NAME = "login[u]";
	private static final String PASS_TEXTFIELD_NAME = "login[p]";
	static final String RESULTPAGE_QUERY_ATTRIBUT = "?&start=";
	private static final String SESSION = "JaumoSession";
	private static final String FORM_ID = "login";
	private static final String AUTOLOGIN = "enableAutologin";
	static final String flirtTextFoto = ", hübsches Foto. Hast du Lust etwas mit mir zu schreiben?";
	static final String flirtText = ", interessntes Profil, nur leider keine Fotos. Hast du Lust etwas mit mir zu schreiben?";
	private static final int SLEEP = 300;
	private static final int UPPER = 62;
	private static final int LOWER = 24;
	private static final int LIMIT = 1000;
	private static final String RADIUS = "100";

	static int result;
	static int threadsWaiting = 0;
	private final static Thread PAGES[] = new Thread[50];
	
	public Jappy(){
		USERAGENT.settings.autoRedirect=true;
	}

	// getter

	@Override
	public UserAgent getUseragent() {
		return USERAGENT;
	}

	@Override
	public String getHost() {
		return HOST;
	}

	@Override
	public String getLogin() {
		return LOGIN_PATH;
	}

	@Override
	public String getSearch() {
		return SEARCH_PATH;
	}

	@Override
	public String getUser() {
		return USERNAME;
	}

	@Override
	public String getPass() {
		return PASSWORD;
	}

	@Override
	public String getUserName() {
		return USER_TEXTFIELD_NAME;
	}

	@Override
	public String getPassName() {
		return PASS_TEXTFIELD_NAME;
	}

	@Override
	public String getSession() {
		return SESSION;
	}

	@Override
	public String getFilter() {
		return FILTER_PATH;
	}

	@Override
	public String getMisc() {
		return MISC_PATH;
	}

	@Override
	public String getFormId() {
		return FORM_ID;
	}

	@Override
	public String getAutologin() {

		return AUTOLOGIN;
	}

	@Override
	public int getSleep() {
		return Jappy.SLEEP;
	}

	@Override
	public String getProfil() {
		return PROFIL_PATH;
	}

	public static String getFilterForm() {
		return FILTER_FORM_PATH;
	}

	public static String getConversation() {
		return CONVERSATION_PATH;
	}

	public static String getConversForm() {
		return CONVERS_FORM_TAG;
	}

	public static String getFlirttextfoto() {
		return flirtTextFoto;
	}

	public static String getFlirttext() {
		return flirtText;
	}

	@Override
	public void clearFilter() throws NotFound, MultipleFound, ResponseException, InterruptedException {
		this.anchorByAttribut("href", this.getHost() + this.getFilter());
		Form filterForm = USERAGENT.doc.getForm("<form action=\"" + HOST + FILTER_FORM_PATH + "\"");
		filterForm.setTextField("ageStart", "14");
		filterForm.setTextField("ageEnd", "90");
		filterForm.setSelect("gender", 1);
		filterForm.setSelect("isOnline", 0);
		filterForm.setSelect("recentRegistration", 0);
		filterForm.setCheckBox("useLocation", false);
		filterForm.setCheckBox("useName", false);
		filterForm.setSelect("figure", 0);
		filterForm.setSelect("hairColor", 0);
		filterForm.setSelect("hairLength", 0);
		filterForm.setSelect("eyeColor", 0);
		filterForm.setSelect("smoker", 0);
		filterForm.setSelect("maritalStatus", 0);
		filterForm.setSelect("hasImage", 0);
		filterForm.setTextField("heightMinimum", "0");
		filterForm.setTextField("heightMaximum", "250");
		Thread.sleep(SLEEP);
		filterForm.submit(0);
		System.out.print("\nFilter zurückgesetzt");
		// System.out.println(getResult());
	}

	/**
	 * @return number of searchresults
	 */
	protected int getResult() {
		String entrys = null;
		try {
			entrys = USERAGENT.doc.findFirst("<b>").getText();
			if (!entrys.replaceAll("\\d", "").trim().isEmpty()) {
				return Integer.parseInt(entrys);
			} else
				return 20;
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			return 20;
		}
	}

	@Override
	public void setFilterMarital(int lower, int upper, int maritalId, boolean hitMax)
			throws NotFound, MultipleFound, ResponseException, InterruptedException {
		submitSearch(lower, upper, maritalId, -1);
		System.out.print("\r Marital:  " + result + " - " + lower + " - " + upper + " - " + maritalId + " - " + hitMax);
		if (upper > 90) {
			sendCircular();
			setFilterMarital(14, 14, 0, false);
		}
		if (maritalId > 10) {
			sendCircular();
			setFilterMarital(lower + 1, upper + 1, 0, false);
		}
		if (maritalId > 0) {
			sendCircular();
			setFilterMarital(lower, upper, maritalId + 1, false);
		}
		if (result >= 1000) {
			if (upper <= lower)
				setFilterMarital(lower, upper, 1, false);
			else
				setFilterMarital(lower, upper - 1, 0, true);
		} else if (hitMax) {
			sendCircular();
			setFilterMarital(upper + 1, upper + 1, 0, false);
		} else
			setFilterMarital(lower, upper + 1, 0, false);
	}

	@Override
	public void setFilterLocation(int lower, int upper, int locationId, boolean hitMax)
			throws NotFound, MultipleFound, ResponseException, InterruptedException {
		submitSearch(lower, upper, 0, locationId);
		System.out.print(".");
		if (upper > UPPER) {
			execSearch();
			setFilterLocation(LOWER, UPPER, locationId + 500, false);
		}
		if (result >= LIMIT)
			if (upper > lower)
				if (hitMax)
					setFilterLocation(LOWER, LOWER, locationId + 1, false);
				else
					setFilterLocation(lower, upper - 1, locationId, true);
			else {
				System.err.print(".");
				setFilterLocation(LOWER, UPPER, locationId + 1, false);
			}
		else if (hitMax) {
			execSearch();
			setFilterLocation(upper + 1, upper + 1, locationId, false);
		} else
			setFilterLocation(lower, upper + 1, locationId, false);
	}

	public void execSearch() throws NotFound, ResponseException {
		Elements info = null;
		String filter = "";
		info = USERAGENT.doc.findFirst("<div id=us").findFirst("<div id=usSelection").findEach("<span class=entry>");
		for (Element e : info) {
			filter += " " + e.innerText().trim();
		}
		System.out.print("\nFilterkriterien: " + filter + "\n");
		sendCircular();
	}

	@Override
	public void setFilterAge(int lower, int upper, boolean hitMax)
			throws NotFound, MultipleFound, ResponseException, InterruptedException {
		if (upper > 90) {
			sendCircular();
			setFilterAge(14, 14, false);
		}
		submitSearch(lower, upper, 0, -1);
		System.out.print("\r" + result + " - " + lower + " - " + upper + " - " + hitMax);
		if (result >= 1000) {
			if (upper <= lower) {
				sendCircular();
				setFilterAge(lower + 1, upper + 1, false);
			} else
				setFilterAge(lower, upper - 1, true);
		} else if (hitMax) {
			sendCircular();
			setFilterAge(upper + 1, upper + 1, false);
		} else
			setFilterAge(lower, upper + 1, false);
	}

	/**
	 * Visits search page, fillout form and submit
	 * 
	 * @param lower
	 *            upper age limit
	 * @param upper
	 *            lower age limit
	 * @param maritalStatus
	 *            maritalstatus id
	 * @throws NotFound
	 * @throws ResponseException
	 * @throws InterruptedException
	 */
	private void submitSearch(int lower, int upper, int maritalStatus, int locationId)
			throws ResponseException, InterruptedException {
		visit(FILTER_PATH, 500);
		Form filterForm;
		try {
			filterForm = USERAGENT.doc.getForm("<form action=" + HOST + FILTER_FORM_PATH + ">");
			filterForm.setTextField("ageStart", Integer.toString(lower));
			filterForm.setTextField("ageEnd", Integer.toString(upper));
			filterForm.setSelect("gender", 1);
			filterForm.setSelect("isOnline", 0);
			filterForm.setSelect("recentRegistration", 0);
			filterForm.setSelect("maritalStatus", Integer.toString(maritalStatus));
			boolean location = false;
			if (locationId > 0)
				location = true;
			filterForm.setCheckBox("useLocation", location);
			if (location) {
				// filterForm.setTextField("locationName",
				// getLocationList().get(locationId));
				filterForm.setHidden("locationId", locationId + "");
				filterForm.setTextField("distance", RADIUS);
			}
			filterForm.setCheckBox("useName", false);
			Thread.sleep(SLEEP);
			filterForm.submit(0);
		} catch (NotFound e) {
			System.err.println("Formular nicht gefunden");
		}
		result = getResult();
	}

	@Override
	public void sendCircular() throws ResponseException, NotFound {
		lock.lock();
		try {
			filterReady.signalAll();
			// System.out.println("\nSignal:\"Filter Bereit\" gesendet");
		} finally {
			lock.unlock();
			waitForThreads();
		}
	}

	@SuppressWarnings("unused")
	public void sendCircularMT() throws InterruptedException, ResponseException, NotFound {

		System.currentTimeMillis();
		searchResult: for (int page = 2; page < result / 20 && page < 50; page++) {
			System.out.println();
			System.currentTimeMillis();
		}
	}

	protected static UserAgent getUserAgentCopy() {
		return USERAGENT.copy();
	}

	public void loadThreadArray() {
		for (int page = 0; page < 50; page++) {
			PAGES[page] = new JappyThread(page + 1);
			PAGES[page].start();
		}
		waitForThreads();
	}

	@Override
	public void waitForThreads() {
		while (threadsWaiting < 50) {
			try {
				Thread.currentThread();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.err.println("Sleep");
			}
		}
		// System.out.print("\nAlle Threads warten");
		threadsWaiting = 0;
	}

	@Override
	public void printThreadState() {
		for (Thread thr : PAGES)
			System.out.println(thr.getName() + " - " + thr.getState());
	}

	@Override
	public void visit(String path, int milliSecond) {
		visit(USERAGENT, path, milliSecond);
	}

	@Override
	public void visit(UserAgent userAgent, String path, int milliSecond) {
		super.visit(userAgent, HOST + path, milliSecond);
	}

	@Override
	public void login() {
		try {
			visit(LOGIN_PATH, 500);
			Form form = USERAGENT.doc.getForm("<form id=" + FORM_ID + ">");
			form.setTextField(USER_TEXTFIELD_NAME, USERNAME);
			form.setPassword(PASS_TEXTFIELD_NAME, PASSWORD);
			form.setCheckBox(AUTOLOGIN, true);
			Thread.sleep(SLEEP);
			form.submit();
			System.out.print("\nHost: " + USERAGENT.getLocation());
			if (isLoggedIn())
				System.out.print("\nEingeloggt");
		} catch (JauntException | InterruptedException e) {
			System.err.println(e);
		}
	}

	@Override
	public boolean isLoggedIn() throws NotFound {
		String message = USERAGENT.doc.findFirst("<img title=" + USERNAME + ">").getAt("alt");
		return message.contains(USERNAME.toString());
	}
	
	@Override
	public void anchorByChild(String child){
		Element searchAnchor = null;
		try {
			searchAnchor = USERAGENT.doc.findFirst(child);
		} catch (NotFound e) {
			System.err.println("Search Anchor NotFound");
		}
		Hyperlink anchor = null;
		try {
			anchor = new Hyperlink(searchAnchor, USERAGENT);
		} catch (NotFound e) {
			System.err.println("Search Anchor NotFound");
		}
		anchor(anchor);
	}
	
	@Override
	public void anchorByAttribut(String attribName, String attribValue) throws NotFound, InterruptedException, ResponseException{
		Element searchAnchor = USERAGENT.doc.findFirst("<a "+attribName+"="+attribValue+">");
		Hyperlink anchor = new Hyperlink(searchAnchor, USERAGENT);
		anchor(anchor);
	}
}