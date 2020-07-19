package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import dna.Dna;
import dna.Profile;
import dna.Schedule;

public class TelegramController {
	//https://api.telegram.org/bot<token>/METHOD_NAME
	private static String token = "1379983604:AAFf_X5fPCy5krKdnuP4VdwR0uZZNRLhyOM";
	private static String requestUrl = "https://api.telegram.org/bot"+token+"/";
	private static int lastupdateId=0;
	public static int lastUserId=0;
	public static JSONObject ret; //last retrieved message from the server
	public static Profile[] savedProfiles=null;
	private static int[] savedRange;

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

	/**
	 * gets all unread messages from the server via long pulling
	 * @param timeOut time to wait from new messages to arrive, minus values to wait indefinitely
	 * @return return json formatted answer from server, null if timed out
	 * @throws IOException
	 */
	public static void getUpdates(int timeOut) throws IOException {
		do {
			JSONObject ret = httpsRequstMethod(
					"getUpdates", "timeout="+(timeOut<0?(60*60):timeOut),
					"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
			if (ret.getJSONArray("result").length()!=0) {
				TelegramController.ret = ret;
				idIncrement();
				return;
			}
		} while (timeOut<0);
		return;

	}

	/**
	 * send a message to a chat by id
	 * @param chat_id id to send the message to
	 * @param text text to send in the message body
	 * @param args arguments to pass for the sendMessage telegram method
	 * @return return json formatted answer from server
	 * @throws IOException
	 */
	public static JSONObject sendMessage(int chat_id, String text, String... args) throws IOException {
		String[] args2 = new String[args.length + 2];
		System.arraycopy(args, 0, args2, 0, args.length);
		args2[args2.length-2] = "chat_id="+chat_id;
		args2[args2.length-1] = "text="+URLEncoder.encode(text, StandardCharsets.UTF_8);
		return httpsRequstMethod("sendMessage", args2);
	}

	/**
	 * calculates best schedule from a single text message
	 * @param msgObj message object received from server
	 * @return return Dna object of best schedule
	 */
	public static Dna calcTempSchedule(JSONObject msgObj) {
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		String msgText=msgObj.getString("text");
		return scheduleGenerator.calculateBestSchedule(scheduleGenerator.ScheduleFromString(msgText));
	}

	/**
	 * gets the text from the last message received from the server
	 * @param messageNum number of message out of returned array from server
	 * @return text from message object
	 */
	private static String getMsg(int messageNum) {
		return ((JSONObject) ((JSONObject) ret.getJSONArray("result")
				.get(messageNum)).
				get("message")).getString("text");
	}

	/**
	 * interactively makes On-The-Fly schedule with a chat user
	 * @throws IOException
	 */
	private static void makeSchedule() throws IOException {
		saveProfiles();
		sendMessage(lastUserId,
				"זהו!, תן לי כמה רגעים ואני יחשב את הרשימה האופטימלית🤓");
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(savedProfiles, savedRange));
		sendMessage(lastUserId,
				bestDna.getGenome().hebtoString());

	}

	private static void saveProfiles() throws IOException {
		sendMessage(lastUserId,
				"כמה אנשים נמצאים?",
				"reply_markup={\"remove_keyboard\":true}");

		getUpdates(-1);

		int numOfPips = Integer.valueOf(getMsg(0));

		sendMessage(lastUserId,
				"וכמה עמדות יש?🏠");

		getUpdates(-1);
		savedRange = new int[Integer.valueOf(getMsg(0))];

		if (savedRange.length!=1) {
			for (int i = 0; i < savedRange.length; i++) {
				sendMessage(lastUserId,
						"כמה אנשים צריכים לאייש את עמדה מספר "+(i+1)+"?");

				getUpdates(-1);
				savedRange[i] = Integer.valueOf(getMsg(0));
			}
		} else {
			savedRange[0] = numOfPips;
		}

		sendMessage(lastUserId,
				"אוקי מצויין!😃 עכשיו נמלא את הפרטים.");

		String name = null;
		float priority = 0;
		int[] preference = new int[2];
		Profile[] profiles = new Profile[numOfPips];

		for (int i = 0; i < profiles.length; i++) {
			sendMessage(lastUserId,
					"מה השם של הבן אדם ה-"+(i+1)+"?");
			getUpdates(-1);
			name=getMsg(0);

			sendMessage(lastUserId,
					"כמה העדפה יש לאדם ה-"+(i+1)+"?");
			getUpdates(-1);
			priority=Float.valueOf(getMsg(0));

			sendMessage(lastUserId,
					"באיזו עמדה האדם ה-"+(i+1)+" מעדיף לשמור?");
			getUpdates(-1);
			preference[0]=Integer.valueOf(getMsg(0));

			sendMessage(lastUserId,
					"באיזו שעה האדם ה-"+(i+1)+"מעדיף לשמור?");
			getUpdates(-1);
			preference[1]=Integer.valueOf(getMsg(0));

			profiles[i] = new Profile(name, priority, preference);
			sendMessage(lastUserId,
					"הנה הפרופיל לאדם הראשון\n"+profiles[i].toString()+".");
			if (i+1!=profiles.length) sendMessage(lastUserId,
					"עכשיו בוא נמשיך להבא!😁");
		}
		sendMessage(lastUserId,
				"זהו!, הכל שמור אצלי!🔐",
				"reply_markup={\"keyboard\":["
						+ "[{\"text\":\""+URLEncoder.encode("רשימה חד פעמית", StandardCharsets.UTF_8)+"\"}],"
						+ "[{\"text\":\""+URLEncoder.encode("שמירת רשימת שמות", StandardCharsets.UTF_8)+"\"}],"
						+ "[{\"text\":\""+URLEncoder.encode("חישוב רשימת שמות", StandardCharsets.UTF_8)+"\"}]"
						+ "]}");
		savedProfiles=profiles;
	}

	private static void calcSavedProfiles() throws IOException {
		sendMessage(lastUserId,
				"תן לי כמה רגעים ואני יחשב את הרשימה האופטימלית🤓");
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(savedProfiles, savedRange));
		sendMessage(lastUserId,
				bestDna.getGenome().hebtoString());
	}

	public static void main(String[] args) throws IOException {
		System.out.println("begin");
		while (true) {
			getUpdates(-1);

			lastUserId = ((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
					.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id");

			String msgText = getMsg(0);

			sendMessage(lastUserId,
					"got -> "+msgText);
			System.out.println(ret);

			switch (msgText.toLowerCase()) {
			case "רשימה חד פעמית":
				try {
					makeSchedule();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			case "שמירת רשימת שמות":
				try {
					saveProfiles();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			case "חישוב רשימת שמות":
				try {
					calcSavedProfiles();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			default:
				sendMessage(lastUserId,
						"נסה להשתמש במקלדת המותאמת אישית כדי לשלוח פקודה שאני יבין👇",
						"reply_markup={\"keyboard\":["
								+ "[{\"text\":\""+URLEncoder.encode("רשימה חד פעמית", StandardCharsets.UTF_8)+"\"}],"
								+ "[{\"text\":\""+URLEncoder.encode("שמירת רשימת שמות", StandardCharsets.UTF_8)+"\"}],"
								+ "[{\"text\":\""+URLEncoder.encode("חישוב רשימת שמות", StandardCharsets.UTF_8)+"\"}]"
								+ "]}");
				break;
			}
		}
	}
}