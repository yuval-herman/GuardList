package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.json.JSONObject;

public class TelegramUpdateListener implements Runnable{
	private TelegramData data;

	public TelegramUpdateListener(TelegramData data) {
		// TODO Auto-generated constructor stub
		this.data = data;
	}

	/**
	 * gets all unread messages from the server via long pulling
	 * @param timeOut time to wait from new messages to arrive, minus values to wait indefinitely
	 * @return return json formatted answer from server, null if timed out
	 * @throws IOException
	 */
	public void getUpdates(int timeOut) throws IOException {
		do {
			JSONObject ret = httpsRequstMethod(
					"getUpdates", "timeout="+(timeOut<0?(60*60):timeOut),
					"offset=" + (data.getLastupdateId()!=0 ? String.valueOf(data.getLastupdateId()+1) : "0"));
			if (ret.getJSONArray("result").length()!=0) {
				data.ret = ret;
				idIncrement();
				synchronized (data) {//TODO uncertain this is actually effective
					data.unread.add(ret);
				}
				return;
			}
		}
		while(timeOut<0);
		return;

	}

	/**
	 * sends https request to telegram server
	 * @param method telegram api method string
	 * @param args arguments to telegram api method
	 * @return return json formatted object from telegram server
	 * @throws IOException
	 */
	public JSONObject httpsRequstMethod(String method, String... args) throws IOException{
		String requetString = data.getRequestUrl()+method;
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
	public void idIncrement() {
		JSONObject obj = (JSONObject) data.ret.getJSONArray("result").get(data.ret.getJSONArray("result").length()-1);
		data.setLastupdateId(obj.getInt("update_id"));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("run");
			getUpdates(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
