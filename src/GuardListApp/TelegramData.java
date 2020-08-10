package GuardListApp;

import java.util.HashMap;
import java.util.Stack;

import org.json.JSONObject;

import GuardListApp.TelegramController.Pair;
import dna.Profile;

public class TelegramData {
	private String token = "1379983604:AAFf_X5fPCy5krKdnuP4VdwR0uZZNRLhyOM";
	private String requestUrl = "https://api.telegram.org/bot"+token+"/";
	private int lastupdateId=0;
	public int lastUserId=0;
	public JSONObject ret; //last retrieved message from the server
	public Profile[] savedProfiles=null;
	private int[] savedRange;
	private HashMap<Integer, Pair> profilesMap = new HashMap<Integer, TelegramController.Pair>();
	public Stack<JSONObject> unread = new Stack<JSONObject>();

	
	public int getLastupdateId() {
		return lastupdateId;
	}
	public void setLastupdateId(int lastupdateId) {
		this.lastupdateId = lastupdateId;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

}
