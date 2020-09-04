package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import dna.Dna;
import dna.Profile;



public class TelegramController {
	//https://api.telegram.org/bot<token>/METHOD_NAME
	private static TelegramData data;
	private static String token = "1379983604:AAFf_X5fPCy5krKdnuP4VdwR0uZZNRLhyOM";
	private static String requestUrl = "https://api.telegram.org/bot"+token+"/";
	private static int lastupdateId=0;
	public static int lastUserId=0;
	public static JSONObject ret; //last retrieved message from the server
	public static Profile[] savedProfiles=null;
	private static HashMap<Integer, ProfileData> profilesMap = new HashMap<Integer, ProfileData>();
	private static ArrayList<Pair<TelegramChat, Thread>> activeChats = new ArrayList<Pair<TelegramChat, Thread>>();

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
	public static void getUpdates(int timeOut) {
		do {
			JSONObject ret = null;
			while (ret==null) {
				try {
					ret = httpsRequstMethod(
							"getUpdates", "timeout="+(timeOut<0?(60*60):timeOut),
							"offset=" + (lastupdateId!=0 ? String.valueOf(lastupdateId+1) : "0"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
					e.printStackTrace();
				}
			}
			if (ret.getJSONArray("result").length()!=0) {
				TelegramController.ret = ret;
				idIncrement();
				lastUserId = ((JSONObject) ((JSONObject) ((JSONObject) ret.getJSONArray("result")
						.get(ret.getJSONArray("result").length()-1)).get("message")).get("from")).getInt("id");
				synchronized (data) {
					data.unread.add(ret);
				}
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
//	private static String getMsg(int messageNum) {
//		return ((JSONObject) ((JSONObject) ret.getJSONArray("result")
//				.get(messageNum)).
//				get("message")).getString("text");
//	}

	/*private static void setUser(int userId) {
		//TODO improve memory efficiency by saving map object to temporary variable
		if (profilesMap.containsKey(userId)) {
			savedProfiles = profilesMap.get(userId).data.getSavedProfiles();
			savedRange = profilesMap.get(userId).data.getSavedRange();
		} else {
			profilesMap.put(lastUserId, new TelegramController().new Pair(savedProfiles, savedRange));
			savedProfiles=null;
			savedRange=null;
		}
	}*/

	/**
	 * interactively makes On-The-Fly schedule with a chat user
	 * @throws IOException
	 */
//	private static void makeSchedule() throws IOException {
//		saveProfiles();
//		calcSavedProfiles();
//	}

//	private static void saveProfiles() throws IOException {
//		sendMessage(lastUserId,
//				"כמה אנשים נמצאים?",
//				"reply_markup={\"remove_keyboard\":true}");
//
//		getUpdates(-1);
//
//		int numOfPips = Integer.valueOf(getMsg(0));
//
//		sendMessage(lastUserId,
//				"וכמה עמדות יש?🏠");
//
//		getUpdates(-1);
//		int[] range = new int[Integer.valueOf(getMsg(0))];
//
//		if (range.length!=1) {
//			for (int i = 0; i < range.length; i++) {
//				sendMessage(lastUserId,
//						"כמה אנשים צריכים לאייש את עמדה מספר "+(i+1)+"?");
//
//				getUpdates(-1);
//				range[i] = Integer.valueOf(getMsg(0));
//			}
//		} else {
//			range[0] = numOfPips;
//		}
//
//		sendMessage(lastUserId,
//				"אוקי מצויין!😃 עכשיו נמלא את הפרטים.");
//
//		String name = null;
//		float priority = 0;
//		int[] preference = new int[2];
//		Profile[] profiles = new Profile[numOfPips];
//
//		for (int i = 0; i < profiles.length; i++) {
//			sendMessage(lastUserId,
//					"מה השם של הבן אדם ה-"+(i+1)+"?");
//			getUpdates(-1);
//			name=getMsg(0);
//
//			sendMessage(lastUserId,
//					"כמה העדפה יש לאדם ה-"+(i+1)+"? (מ-0 עד 10)");
//			getUpdates(-1);
//			priority=Float.valueOf(getMsg(0))/10;
//
//
//			if (range==null || range.length==1) {
//				preference[0]=0;
//				range = new int[] {numOfPips};
//			} else {
//				sendMessage(lastUserId,
//						"באיזו עמדה האדם ה-"+(i+1)+" מעדיף לשמור? (מ-0 עד "+(range.length-1)+")");
//				getUpdates(-1);
//				preference[0]=Integer.valueOf(getMsg(0));
//			}
//
//			sendMessage(lastUserId,
//					"באיזו שעה האדם ה-"+(i+1)+"מעדיף לשמור? (מ-0 עד "+(range[preference[0]]-1)+")");
//			getUpdates(-1);
//			preference[1]=Integer.valueOf(getMsg(0));
//
//			profiles[i] = new Profile(name, priority, preference);
//			sendMessage(lastUserId,
//					"הנה הפרופיל לאדם ה"+i+"\n"+profiles[i].toString()+".");
//			if (i+1!=profiles.length) sendMessage(lastUserId,
//					"עכשיו בוא נמשיך להבא!😁");
//		}
//
//		sendOptions("זהו!, הכל שמור אצלי!🔐");
//
//		savedProfiles=profiles;
//		savedRange=range;
//	}

//	private static void calcSavedProfiles() throws IOException {
//		sendMessage(lastUserId,
//				"תן לי כמה רגעים ואני יחשב את הרשימה האופטימלית🤓");
//		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
//		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(savedProfiles, savedRange));
//		savedProfiles = bestDna.getGenome().getProfiles();
//		sendMessage(lastUserId,
//				bestDna.getGenome().hebtoString());
//	}

//	private static void manualEdit() throws IOException {
//		String strNames = "";
//		for (int i = 0; i < savedProfiles.length-1; i++) {
//			strNames+="[{\"text\":\""+URLEncoder.encode(savedProfiles[i].getName(), StandardCharsets.UTF_8)+"\"}],";
//		}
//		strNames+="[{\"text\":\""+URLEncoder.encode(savedProfiles[savedProfiles.length-1].getName(), StandardCharsets.UTF_8)+"\"}]";
//
//		sendMessage(lastUserId,
//				"בחר למי לבצע שינוי במקלדת המותאמת האישית!👪",
//				"reply_markup={\"keyboard\":["+strNames+"]}");
//
//		getUpdates(-1);
//
//		String name=getMsg(0);
//
//		sendMessage(lastUserId,
//				"מצויין😃,עכשיו תבחר מה אתה רוצה לשנות!📊",
//				"reply_markup={\"keyboard\":["+
//						"[{\"text\":\""+URLEncoder.encode("שם", StandardCharsets.UTF_8)+"\"}],"+
//						"[{\"text\":\""+URLEncoder.encode("העדפה", StandardCharsets.UTF_8)+"\"}],"+
//						"[{\"text\":\""+URLEncoder.encode("עמדה מועדפת", StandardCharsets.UTF_8)+"\"}],"+
//						"[{\"text\":\""+URLEncoder.encode("שעה מועדפת", StandardCharsets.UTF_8)+"\"}],"+
//						"[{\"text\":\""+URLEncoder.encode("עמדה סופית", StandardCharsets.UTF_8)+"\"}],"+
//						"[{\"text\":\""+URLEncoder.encode("שעה סופית", StandardCharsets.UTF_8)+"\"}]"+
//				"]}");
//
//		getUpdates(-1);
//
//		switch (getMsg(0).toLowerCase()) {
//		case "שם":
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את השם החדש בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setName(getMsg(0));
//
//					sendMessage(lastUserId,
//							name+" שונה ל"+getMsg(0)+"👨");
//					return;
//				}
//			}
//			break;
//
//		case "העדפה":
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את ההעדפה החדשה בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setPriority(Float.valueOf(getMsg(0)));
//
//					sendMessage(lastUserId,
//							"הפעולה הושלמה בהצלחה🤖");
//					return;
//				}
//			}
//			break;
//
//		case "עמדה מועדפת":
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את העמדה החדשה בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setPreference(new int[] {Integer.valueOf(getMsg(0)), savedProfiles[i].getPreference()[1]});
//
//					sendMessage(lastUserId,
//							"הפעולה הושלמה בהצלחה🤖");
//					return;
//				}
//			}
//			break;
//
//		case "שעה מועדפת":
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את השעה החדשה בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setPreference(new int[] {savedProfiles[i].getPreference()[0], Integer.valueOf(getMsg(0))});
//
//					sendMessage(lastUserId,
//							"הפעולה הושלמה בהצלחה🤖");
//					return;
//				}
//			}
//			break;
//
//		case "עמדה סופית":
//			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את העמדה החדשה בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setPost(new int[] {Integer.valueOf(getMsg(0)), savedProfiles[i].getPost()[1]});
//
//					sendMessage(lastUserId,
//							"הפעולה הושלמה בהצלחה🤖");
//					return;
//				}
//			}
//			break;
//
//		case "שעה סופית":
//			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
//			for (int i = 0; i < savedProfiles.length; i++) {
//				if (savedProfiles[i].getName().equals(name)) {
//					sendMessage(lastUserId,
//							"עכשיו תשלח את השעה החדשה בשביל "+name,
//							"reply_markup={\"remove_keyboard\":true}");
//					getUpdates(-1);
//
//					savedProfiles[i].setPost(new int[] {savedProfiles[i].getPost()[0], Integer.valueOf(getMsg(0))});
//
//					sendMessage(lastUserId,
//							"הפעולה הושלמה בהצלחה🤖");
//					return;
//				}
//			}
//			break;
//
//		default:
//			throw new IOException();
//		}
//	}

	public static void main(String[] args) throws IOException {
		data = new TelegramData(); //contains data for the bot
		System.out.println("begin");
		while (true) {
			getUpdates(-1);
			for (int i=0; i<activeChats.size(); i++) {
				if (!activeChats.get(i).key.isRunning) {
					try {
						activeChats.get(i).value.join(1000);
						activeChats.remove(i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			System.out.println(ret); //prints the 1st update
			for (Object updateObj : ret.getJSONArray("result")) { //loops througth all update objects
				JSONObject update = (JSONObject) updateObj; //convert from Object to JSONObject
				int userId = (int) update.query("/message/from/id"); //gets the user id

				boolean found = false;
				for (int i = 0; i < activeChats.size(); i++) { //check if an active chat is waiting for a message from that user
					if(activeChats.get(i).key.getUserId() == userId) {
						synchronized (activeChats.get(i).key.unread) { //if so notify the chat that it has the new message
							activeChats.get(i).key.unread.add(update); 
						}
						if(activeChats.get(i).value.getState()==State.WAITING) {
							synchronized (activeChats.get(i).key.lockObject) {
								activeChats.get(i).key.lockObject.notify();
							}
						}
						found=true;
						break;
					}
				}
				if (!found) { //if no active chat is waiting for a message from that user create a new chat
					if (!profilesMap.containsKey(userId)) { //if there is no data about the user
						profilesMap.put(userId, new ProfileData(new Profile[0],
								new Profile((String)update.query("/message/from/first_name")+" "+(String)update.query("/message/from/last_name"),0f,null), 
								null, false));//TODO improve this
					}
					TelegramChat chat = new TelegramChat(userId, update, profilesMap.get(userId), data); //make the new chat
					Thread chatThread = new Thread(chat);
					chatThread.start();
					activeChats.add(new Pair<TelegramChat, Thread>(chat, chatThread));
				}
			}

			/*switch (getMsg(0).toLowerCase()) {
			case "רשימה חדשה":
				try {
					makeSchedule();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			case "חישוב רשימת שמות":
				try {
					if (savedProfiles==null||savedProfiles.length==0) {
						sendMessage(lastUserId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
						break;
					}
					calcSavedProfiles();
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			case "שינוי ידני":
				try {
					if (savedProfiles==null||savedProfiles.length==0) {
						sendMessage(lastUserId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
						break;
					}
					manualEdit();
					sendMessage(lastUserId,
							"שבצ\"ק מעודכן:");
					sendMessage(lastUserId,
							new Schedule(savedProfiles, savedRange).hebtoString());
				} catch (Exception e) {
					sendMessage(lastUserId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				}
				break;

			default:
				sendOptions("נסה להשתמש במקלדת המותאמת אישית כדי לשלוח פקודה שאני יבין👇");
				break;
			}*/
		}
	}

//	private static void sendOptions(String text) throws IOException {
//		httpsRequstMethod("sendMessage", "chat_id="+lastUserId+"&text="+URLEncoder.encode(text, StandardCharsets.UTF_8)
//		+"&reply_markup={\"keyboard\":["
//		+ "[{\"text\":\""+URLEncoder.encode("רשימה חדשה", StandardCharsets.UTF_8)+"\"}],"
//		+ "[{\"text\":\""+URLEncoder.encode("שינוי ידני", StandardCharsets.UTF_8)+"\"}],"
//		+ "[{\"text\":\""+URLEncoder.encode("חישוב רשימת שמות", StandardCharsets.UTF_8)+"\"}]"
//		+ "]}");
//	}

}