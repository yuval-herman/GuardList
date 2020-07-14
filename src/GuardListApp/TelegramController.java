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
	public static int lastUserId=0;
	public static JSONObject ret;

	public static JSONObject httpRequstMethod(String method, String... args) throws IOException{
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

	public static JSONObject sendMessage(int chat_id, String text, String... args) throws IOException {
		String[] args2 = new String[args.length + 2];
		System.arraycopy(args, 0, args2, 0, args.length);
		args2[args2.length-2] = "chat_id="+chat_id;
		args2[args2.length-1] = "text="+URLEncoder.encode(text, StandardCharsets.UTF_8);
		return httpRequstMethod("sendMessage", args2);
	}

	public static Dna calcTempSchedule(JSONObject msgObj) {
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		String msgText=msgObj.getString("text");
		return scheduleGenerator.calculateBestSchedule(scheduleGenerator.ScheduleFromString(msgText));
	}

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		while (true) {
			ret = getUpdates(-1);

			lastUserId = ((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
					.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id");

			String msgText = (String) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
					.get(ret.getJSONArray("result").length()-1)).get("message")).get("text");

			sendMessage(lastUserId,
					"got -> "+msgText);
			System.out.println(ret);

			switch (msgText.toLowerCase()) {
			case "test":
				try {
					makeSchedule();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"wrong fomatting, try again");
				}
				break;

			default:
				sendMessage(lastUserId,
						"testList - calculate best sceduale for a given list in the following format:\n"
								+ "name, priority, station number:time.\n"
								+ "example: nadav,0.213,0:1.\n"
								+ "in the last line add the number of people to save in each station like so:\n"
								+ "2:5:1(indicating 2 for the first station five for the second and so on).");
				break;
			}
		}
	}

	private static String getMsg() {
		return ((JSONObject) ((JSONObject) ret.getJSONArray("result")
				.get(ret.getJSONArray("result").length()-1)).
				get("message")).getString("text");
	}

	private static void makeSchedule() throws IOException {
		sendMessage(lastUserId,
				"now write the configuration message.",
				"reply_markup="+
						URLEncoder.encode("{\"keyboard\":[[{\"text\":\"name\"},"
								+ "{\"text\":\"priority\"},"
								+ "{\"text\":\"preference\"}]]}", StandardCharsets.UTF_8));

		getUpdates(-1);
		System.out.println(ret);
		String name = null;
		String priority = null;
		String preference = null;
		switch (getMsg()) {
		case "name":
			System.out.println(getMsg());
			sendMessage(lastUserId,
					"write the name",
					"reply_markup="+
							URLEncoder.encode("{\"keyboard\":[["+(name!=null?"{\"text\":\"name\"},":null)
									+ (priority!=null?"{\"text\":\"priority\"},":null)
									+ (preference!=null?"{\"text\":\"preference\"}]]}":null), StandardCharsets.UTF_8));
			getUpdates(-1);
			name = getMsg();
			break;
		case "priority":
			sendMessage(lastUserId,
					"write the priority",
					"reply_markup="+
							URLEncoder.encode("{\"keyboard\":[["+(name!=null?"{\"text\":\"name\"},":null)
									+ (priority!=null?"{\"text\":\"priority\"},":null)
									+ (preference!=null?"{\"text\":\"preference\"}]]}":null), StandardCharsets.UTF_8));
			getUpdates(-1);
			priority = getMsg();
			break;
		case "preference":
			sendMessage(lastUserId,
					"write the preference",
					"reply_markup="+
							URLEncoder.encode("{\"keyboard\":[["+(name!=null?"{\"text\":\"name\"},":null)
									+ (priority!=null?"{\"text\":\"priority\"},":null)
									+ (preference!=null?"{\"text\":\"preference\"}]]}":null), StandardCharsets.UTF_8));
			getUpdates(-1);
			preference = getMsg();
			break;

		default:
			break;
		}
		sendMessage(lastUserId, "I got " + name+","+priority+","+preference,
				"reply_markup={\"remove_keyboard\":true}");
		sendMessage(lastUserId,
				calcTempSchedule((JSONObject) ((JSONObject) ret.getJSONArray("result")
						.get(ret.getJSONArray("result").length()-1)).
						get("message")).toString());
	}
}