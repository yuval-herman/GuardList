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
	public static void idIncrement(JSONObject ret) {
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
					"getUpdates", "timeout="+(timeOut<0?120:timeOut),
					"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
			if (ret.getJSONArray("result").length()!=0) {
				idIncrement(ret);
				TelegramController.ret = ret;
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
		sendMessage(lastUserId,
				"how many people are there?");

		getUpdates(-1);
		
		int numOfPips = Integer.valueOf(getMsg(0));
		sendMessage(lastUserId,
				numOfPips+" people it is then.");
		
		sendMessage(lastUserId,
				"now tell me how many stations have you got.🏠");
		
		getUpdates(-1);
		int[] range = new int[Integer.valueOf(getMsg(0))];
		
		for (int i = 0; i < range.length; i++) {
			sendMessage(lastUserId,
					"how many people are assigned to the "+i+" station?");
			
			getUpdates(-1);
			range[i] = Integer.valueOf(getMsg(0));
		}
		
		sendMessage(lastUserId,
				"okay great!😃 now let's fill in the details.");
		
		String name = null;
		float priority = 0;
		int[] preference = new int[2];
		Profile[] profiles = new Profile[numOfPips];
		
		for (int i = 0; i < profiles.length; i++) {
			sendMessage(lastUserId,
					"give me a name for the "+i+" person.");
			getUpdates(-1);
			name=getMsg(0);
			
			sendMessage(lastUserId,
					"write the "+i+" person priority.");
			getUpdates(-1);
			priority=Float.valueOf(getMsg(0));
			
			sendMessage(lastUserId,
					"write the "+i+" person preferred station.");
			getUpdates(-1);
			preference[0]=Integer.valueOf(getMsg(0));
			
			sendMessage(lastUserId,
					"write the "+i+" person preferred time.");
			getUpdates(-1);
			preference[1]=Integer.valueOf(getMsg(0));
			
			profiles[i] = new Profile(name, priority, preference);
			sendMessage(lastUserId,
					"the "+i+" person looks like this: "+profiles[i].toString()+".");
			if (i+1!=profiles.length) sendMessage(lastUserId,
					"now let's go for the next!😁");
		}
		sendMessage(lastUserId,
				"Done!, give me a second and i will calculate the best schedule🤓");
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(profiles, range));
		sendMessage(lastUserId,
				URLEncoder.encode(bestDna.getGenome().toString(), StandardCharsets.UTF_8));

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
			case "test":
				try {
					makeSchedule();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"wrong fomatting, try again",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			default:
				sendMessage(lastUserId,
						"testList - calculate best sceduale for a given list in the following format:\n"
								+ "name, priority, station number:time.\n"
								+ "example: nadav,0.213,0:1.\n"
								+ "in the last line add the number of people to save in each station like so:\n"
								+ "2:5:1(indicating 2 for the first station five for the second and so on).",
								"reply_markup={\"remove_keyboard\":true}");
				break;
			}
		}
	}
}