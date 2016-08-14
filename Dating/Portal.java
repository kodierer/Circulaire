import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jaunt.Cookie;
import com.jaunt.Element;
import com.jaunt.JauntException;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import com.jaunt.component.Form;
import com.jaunt.component.Hyperlink;

public abstract class Portal implements PortalAccess {
	
	private List<String> locList;
	final static Lock lock = new ReentrantLock();
	final static Condition filterReady = lock.newCondition();
	final static Condition pagesReady = lock.newCondition();
	
	public void visit(UserAgent userAgent, String url, int milliSecond) {
		lock.lock();
		try {
			Thread.currentThread();
			Thread.sleep(milliSecond);
		} catch (InterruptedException e) {
			System.err.println("IE");
		}finally{
			lock.unlock();
		}
		try {
			userAgent.visit(url);
		} catch (ResponseException e2) {
			//System.err.print("\nLadefehler: "  + url);
			System.err.print("X");
			visit(userAgent, url, milliSecond);
		}
	}
	
	public Portal(){
		this.locList = readCSVTime();
	}
	
	public List<String> getLocationList() {
		return locList;
	}
	
	public void anchor(Hyperlink anchor){
		try {
			Thread.sleep(getSleep());
			anchor.follow();
		} catch (ResponseException e) {
			System.err.println("reconnect");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				System.err.println("IE");
			}
			anchor(anchor);
		} catch (InterruptedException e) {
			System.err.println("IE");
		}
	}
	
	@SuppressWarnings("unused")
	protected void printHTML(){
		System.out.println(this.getUseragent().doc.innerHTML());
	}
	
	@Override
	public List<String> readCSVTime() {
        try {
            java.io.BufferedReader fileReader=                      //ein Reader um die Datei Zeilenweise auszulesen
                    new java.io.BufferedReader(
                        new java.io.FileReader(
                            new java.io.File("DE-PLZ.csv")
                        )
                    );
           
            String zeile="";
            Set<String> locationSet = new HashSet<String>();
            while(null!=(zeile=fileReader.readLine())){     
                String[] split=zeile.split(",");               
                locationSet.add(split[1]);
            }
            List<String> locList = new ArrayList<>(locationSet);
            fileReader.close();
            return locList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }    
    }
	
	public void runCrawling(){
		waitForThreads();	
	}
}
