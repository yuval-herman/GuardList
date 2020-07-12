package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;

public class TelegramController {
	//https://api.telegram.org/bot<token>/METHOD_NAME
	private static String token = "1379983604:AAFf_X5fPCy5krKdnuP4VdwR0uZZNRLhyOM";
	private static String requestUrl = "https://api.telegram.org/bot"+token+"/";
	private static int lastupdateId=0;

	public static JSONObject httpRequstMethod(String method, String... args) throws IOException{
		String requetString = requestUrl+method;
		if (args.length>0) {
			requetString+="?"+args[0];
			for (int i = 1; i < args.length; i++) requetString+="&"+args[i];
		}

		InputStream is = new URL(requetString).openStream();
		String data = "";
		try {
			while(is.available()>0) {
				data+=(char)is.read();
			}
		} finally {
			is.close();
		}
		return new JSONObject(data);
	}

	public static JSONObject getUpdates() throws IOException {
		return httpRequstMethod(
				"getUpdates", "timeout=60",
				"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
	}
	
	public static void idIncrement(JSONObject ret) {
		JSONObject obj = (JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1);
		lastupdateId=obj.getInt("update_id");
	}

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		JSONObject ret = null;
		while (true) {
			ret = getUpdates();

			if (ret.getJSONArray("result").length()==0) {
				continue;
			}
			
			idIncrement(ret);
			String msgText = null;
			for (int i = 0; i < ret.getJSONArray("result").length(); i++) {
				JSONObject obj = (JSONObject) ret.getJSONArray("result").get(i);
				JSONObject message = (JSONObject) obj.get("message");
				msgText=message.getString("text");
			}
			System.out.println(msgText);
		}
	}
}