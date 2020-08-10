package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;

public class TelegramUpdateListener implements Runnable{
	private static String token = "1379983604:AAFf_X5fPCy5krKdnuP4VdwR0uZZNRLhyOM";
	private static String requestUrl = "https://api.telegram.org/bot"+token+"/";
	private static int lastupdateId=0;
	public static JSONObject ret; //last retrieved message from the server


	/**
	 * gets all unread messages from the server via long pulling
	 * @param timeOut time to wait from new messages to arrive, minus values to wait indefinitely
	 * @return return json formatted answer from server, null if timed out
	 * @throws IOException
	 */
	public static void getUpdates(int timeOut) throws IOException {
		JSONObject ret = httpsRequstMethod(
				"getUpdates", "timeout="+(timeOut<0?(60*60):timeOut),
				"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
		if (ret.getJSONArray("result").length()!=0) {
			TelegramController.ret = ret;
			idIncrement();
			return;
		}
		return;

	}

	/**
	 * sends https request to telegram server
	 * @param method telegram api method string
	 * @param args arguments to telegram api method
	 * @return return json formatted object from telegram server
	 * @throws IOException
	 */
	public static JSONObject httpsRequstMethod(String method, String... args) throws IOException{
		String requetString = requestUrl+method;
		if (args.length>0) {
			requetString+="?"+args[0];
			for (int i = 1; i < args.length; i++) requetString+="&"+args[i];
		}
		System.out.println(requetString);
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

	/**
	 * increments the last id of the received message
	 * @param ret the last received message from the server
	 */
	public static void idIncrement() {
		JSONObject obj = (JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1);
		lastupdateId=obj.getInt("update_id");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			getUpdates(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
