package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

/***
 * This object will hold the status and reason for various actions.
 * 
 * @author klitos
 */

public class ActionStatus {
	private int status;
	private String reason;
	
	public ActionStatus(int s, String r) {
		this.status = s;
		this.reason = r;
	}
	
	public void setStatus(int s) {
		this.status = s;
	}
	
	public void setReason(String r) {
		this.reason = r;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public String getReason() {
		return this.reason;
	}
}//end class
