package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import dna.Dna;
import dna.Schedule;

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

	public static void idIncrement(JSONObject ret) {
		JSONObject obj = (JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1);
		lastupdateId=obj.getInt("update_id");
	}

	public static JSONObject getUpdates(int timeOut) throws IOException {
		do {
			JSONObject ret = httpRequstMethod(
					"getUpdates", "timeout="+(timeOut<0?120:timeOut),
					"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
			if (ret.getJSONArray("result").length()!=0) {
				idIncrement(ret);
				return ret;
			}
		} while (timeOut<0);
		return null;

	}

	public static JSONObject sendMessage(int chat_id, String text) throws IOException {
		return httpRequstMethod(
				"sendMessage", "chat_id="+chat_id,
				"text="+URLEncoder.encode(text, StandardCharsets.UTF_8));
	}
	
	public static Dna calcTempSchedule(JSONObject msgObj) {
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		String msgText=msgObj.getString("text");
		return scheduleGenerator.calculateBestSchedule(scheduleGenerator.ScheduleFromString(msgText));
	}

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		JSONObject ret = null;
		while (true) {
			ret = getUpdates(-1);

			String msgText = null;
			JSONObject message=null;
			for (int i = 0; i < ret.getJSONArray("result").length(); i++) {
				JSONObject obj = (JSONObject) ret.getJSONArray("result").get(i);
				 message = (JSONObject) obj.get("message");
				msgText=message.getString("text");
			}
			
			sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
					.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
					"got -> "+msgText);
			System.out.println(ret);
			
			try {
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
						.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						calcTempSchedule(message).toString());
			} catch (Exception e) {
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
						.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						"wrong fomatting, try again");
			}
		}
	}
}