import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import com.jaunt.component.Form;

public class JappyThread extends Thread{

	private UserAgent UA_COPY;
	private final long timeStart = System.currentTimeMillis();
	private int calledUser = 0;
	private int resultsOnPage = 0;
	private String threadName;
	private String messageText;
	private String profilName;
	private String picStr;
	private int picsCount;
	private String[] basicData;
	private final int page;

	JappyThread(int page){
		this.page = page;
	}

	@Override
	public void run() {
		this.setName("PageThread-" + page);
		this.threadName = this.getName();
		while (true) {
			try {
				//System.out.print("\n" + threadName + " Fertig");
				Jappy.lock.lock();
				Jappy.threadsWaiting++;
				Jappy.filterReady.await();
			} catch (InterruptedException e) {
				System.err.println(threadName + "interruptExc");
			} finally {
				Jappy.lock.unlock();
				if (page <= Jappy.result / 20 )
					resultPage();
			} 
		}
	}

	private void resultPage() {
		UA_COPY = Jappy.getUserAgentCopy();
		visitSearch();
		//System.out.print("\nStarte " + threadName + " ");
		Elements userNames;
		String userFirstName;
		Element node = getNode();
		/* collect and count results */
		userNames = node.findEach("<span class=\"stampOffline\"");
		resultsOnPage = userNames.toList().size();
		calledUser += resultsOnPage;
		//System.out.print("R" + resultsOnPage);
		for (Element userName : userNames) {
			profilName = userName.getText().split(" ")[2];
			visitProf();
			checkGallery();
			userFirstName = getUserFirstName();
			visitCon();
			if (!isNeedy())
				continue;
			if (!userFirstName.isEmpty())
				profilName = userFirstName;
			sendMessage();
		}
	}

	private void sendMessage() {
		Form conversForm;
			/* send message */
			try {
				conversForm = UA_COPY.doc.getForm(Jappy.CONVERS_FORM_TAG);
				conversForm.setTextArea("text", "Hallo " + profilName + messageText);
				conversForm.submit();
				System.out.print("*");
			} catch (Exception e) {
				//System.err.println("Konversationsformular nicht gefunden->reload");
				visitCon();
			}
	}

	private void visitProf() {
		visit(Jappy.PROFIL_PATH + profilName, 250);
	}

	private void visitCon() {
		visit(Jappy.CONVERSATION_PATH + profilName, 100);
	}
	
	private void visitSearch() {
		visit(Jappy.SEARCH_PATH + Jappy.RESULTPAGE_QUERY_ATTRIBUT + page, 450);
	}

	private Element getNode() {
		Element node = null;
		while (node == null) {
			try {
				node = UA_COPY.doc.findFirst("<div id=usResults>");
			} catch (NotFound e3) {
				//System.err.print("\n"+this.threadName + ":Search-Ladefehler->Reload");
				visitSearch();
			}
		}
		return node;
	}
	
	
	private void visit(String url, int milliSecond) {
		Jappy.lock.lock();
		try {
			JappyThread.sleep(milliSecond);
		} catch (InterruptedException e) {
			System.err.println("IE");
		}finally{
			Jappy.lock.unlock();
		}
		try {
			UA_COPY.visit(Jappy.HOST + url);
		} catch (ResponseException e2) {
			//System.err.print("\nLadefehler: "  + url);
			System.err.print("X");
			visit(url, milliSecond);
		}
	}
	

	private void measureTime() {
		if (resultsOnPage > 0) {
			long interim = System.currentTimeMillis();
			System.out.print("-" + (Math.round((double) (interim - timeStart) / calledUser / 1000)) + "Sek/Prof");
		}
	}

	/**
	 * @return number of profil pictures.
	 */
	private int countPics() {
		try {
			picStr = UA_COPY.doc.findFirst("<a class=profileOwnerNumberOfImages>").getText().trim();
			return Integer.parseInt(picStr);
		} catch (NotFound e1) {
			// System.err.println("keinen Bilderlink gefunden!");
			//System.err.print("|");
			return 0;
		}
	}

	/**
	 * Checks, if profil is availible.
	 * 
	 * @return accessibility of profil
	 * @throws InterruptedException
	 */
	private boolean isNeedy() {
		if (!UA_COPY.doc.innerText().contains("Keine Nachrichten")
				|| UA_COPY.doc.innerText().contains("Nutzer nicht kontaktieren")) {
			// System.err.println(profilName + " wurde übersprungen");
			//System.err.print("~");
			return false;
		}
		return true;
	}

	/**
	 * Investigate users first name, if it exist.
	 * 
	 * @return Users first name
	 */
	private String getUserFirstName() {
		try {
			basicData = UA_COPY.doc.findFirst("<div class=basicData>").getText().trim().split(",");
			if (((basicData.length > 2 && !basicData[1].trim().isEmpty())
					|| (basicData.length == 2 && !basicData[1].split(",")[0].trim().contains("aus")))
					&& !basicData[1].trim().contains("Partner:"))
				return basicData[0].replace(",", " ").trim();
			return "";
		} catch (NotFound e) {
			// System.err.println("no user name data found");
			//System.err.print("ud");
			return "";
		}
	}

	/**
	 * checks, if pictures exist in the profil gallery. Adjust the the message
	 * for no picture profiles.
	 */
	private void checkGallery() {
		picsCount = countPics();
		if (UA_COPY.doc.innerText().contains("Galerie (0)") && picsCount == 0)
			messageText = Jappy.flirtText;
		else
			messageText = Jappy.flirtTextFoto;
	}
}
