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

	public static JSONObject getUpdates() throws IOException {
		JSONObject ret = httpRequstMethod(
				"getUpdates", "timeout=120",
				"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
		if (ret.getJSONArray("result").length()!=0) idIncrement(ret);
		return ret;
	}

	public static JSONObject sendMessage(int chat_id, String text) throws IOException {
		return httpRequstMethod(
				"sendMessage", "chat_id="+chat_id,
				"text="+text);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		JSONObject ret = null;
		while (true) {
			ret = getUpdates();

			if (ret.getJSONArray("result").length()==0) {
				continue;
			}

			String msgText = null;
			for (int i = 0; i < ret.getJSONArray("result").length(); i++) {
				JSONObject obj = (JSONObject) ret.getJSONArray("result").get(i);
				JSONObject message = (JSONObject) obj.get("message");
				msgText=message.getString("text");
			}

			//sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"), "got -> "+((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).getString("text"));
			System.out.println(msgText);

			if (msgText.equals("test")) {
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						URLEncoder.encode("send your profiles in the following format:\nname,priority,preferred location:preferred time.", StandardCharsets.UTF_8));
				ret = getUpdates();
				ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
				String datamsgText = null;
				for (int i = 0; i < ret.getJSONArray("result").length(); i++) {
					JSONObject obj = (JSONObject) ret.getJSONArray("result").get(i);
					JSONObject message = (JSONObject) obj.get("message");
					datamsgText=message.getString("text");
				}
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						URLEncoder.encode("send the number of people for each station like the following example:\n4:6:2\nthe above stands for 3 stations with 4 people manning the 1st station 6 the 2th and 2 the 3rd.", StandardCharsets.UTF_8));
				ret = getUpdates();
				for (int i = 0; i < ret.getJSONArray("result").length(); i++) {
					JSONObject obj = (JSONObject) ret.getJSONArray("result").get(i);
					JSONObject message = (JSONObject) obj.get("message");
					msgText=message.getString("text");
				}
				int[] range = new int[msgText.split(":").length];
				for (int i = 0; i < range.length; i++) {
					range[i] = Integer.valueOf(msgText.split(":")[i]);
				}
				
				Schedule schedule = scheduleGenerator.ScheduleFromString(datamsgText, range);
				
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						URLEncoder.encode(schedule.toString(), StandardCharsets.UTF_8));
				
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						URLEncoder.encode("calculating...", StandardCharsets.UTF_8));
				
				Dna bestDna = scheduleGenerator.calculateBestSchedule(schedule);
				
				sendMessage(((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result").get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id"),
						URLEncoder.encode(bestDna.toString(), StandardCharsets.UTF_8));
			}
		}
	}
}