package GuardListApp;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import org.json.JSONObject;

import dna.Dna;
import dna.Profile;
import dna.Schedule;

public class TelegramChat implements Runnable{
	public Stack<JSONObject> unread;
	int userId;
	JSONObject activatorMsg;
	ProfileData profileData;
	TelegramData data;
	boolean isAdmin;
	volatile boolean isRunning = true;
	Profile[] connectedProfiles;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public TelegramChat(int userId, JSONObject activatorMsg, ProfileData profile, TelegramData data) {
		this.userId = userId;
		this.activatorMsg = activatorMsg;
		this.profileData = profile;
		this.data = data;
		this.unread = new Stack<JSONObject>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (getMsg().toLowerCase()) {
		case "רשימה חדשה":
			try {
				makeSchedule();
			} catch (Exception e) {
				try {
					TelegramApi.sendMessage(data.getRequestUrl(), userId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		case "חישוב רשימת שמות":
			try {
				if (data.savedProfiles==null||data.savedProfiles.length==0) {
					sendMessage(userId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
					break;
				}
				calcSavedProfiles();
			} catch (Exception e) {
				try {
					sendMessage(userId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		case "שינוי ידני":
			try {
				if (data.savedProfiles==null||data.savedProfiles.length==0) {
					sendMessage(userId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
					break;
				}
				manualEdit();
				sendMessage(userId,
						"שבצ\"ק מעודכן:");
				sendMessage(userId,
						new Schedule(data.savedProfiles, data.savedRange).hebtoString());
			} catch (Exception e) {
				try {
					sendMessage(userId,
							"קרתה תקלה, נסה שוב🤪.",
							"reply_markup={\"remove_keyboard\":true}");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			break;

		default:
			try {
				sendOptions("נסה להשתמש במקלדת המותאמת אישית כדי לשלוח פקודה שאני יבין👇");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		exit();
	}

	private void exit() {
		// TODO Auto-generated method stub
		isRunning = false;
	}

	/**
	 * gets the text from the first message received from the server
	 * @return text from message object
	 */
	private String getMsg() {
		if (unread.isEmpty()) {
			return (String) activatorMsg.query("/message/text");
		}
		String msg = (String) unread.firstElement().query("/message/text");
		unread.removeElementAt(0);
		return msg;
	}

	private void sendOptions(String text) throws IOException {
		TelegramApi.httpsRequstMethod(data.getRequestUrl(), "sendMessage", "chat_id="+userId+"&text="+URLEncoder.encode(text, StandardCharsets.UTF_8)
		+"&reply_markup={\"keyboard\":["
		+ "[{\"text\":\""+URLEncoder.encode("רשימה חדשה", StandardCharsets.UTF_8)+"\"}],"
		+ "[{\"text\":\""+URLEncoder.encode("שינוי ידני", StandardCharsets.UTF_8)+"\"}],"
		+ "[{\"text\":\""+URLEncoder.encode("חישוב רשימת שמות", StandardCharsets.UTF_8)+"\"}]"
		+ "]}");
	}

	private void makeSchedule() throws IOException {
		saveProfiles();
		calcSavedProfiles();
	}

	/**
	 * send a message to a chat by id
	 * @param chat_id id to send the message to
	 * @param text text to send in the message body
	 * @param args arguments to pass for the sendMessage telegram method
	 * @return return json formatted answer from server
	 * @throws IOException
	 */
	public JSONObject sendMessage(int chat_id, String text, String... args) throws IOException {
		String[] args2 = new String[args.length + 2];
		System.arraycopy(args, 0, args2, 0, args.length);
		args2[args2.length-2] = "chat_id="+chat_id;
		args2[args2.length-1] = "text="+URLEncoder.encode(text, StandardCharsets.UTF_8);
		return TelegramApi.httpsRequstMethod(data.getRequestUrl(), "sendMessage", args2);
	}

	private void saveProfiles() throws IOException {
		sendMessage(userId,
				"כמה אנשים נמצאים?",
				"reply_markup={\"remove_keyboard\":true}");

		getUpdates();

		int numOfPips = Integer.valueOf(getMsg());

		sendMessage(userId,
				"וכמה עמדות יש?🏠");

		getUpdates();
		int[] range = new int[Integer.valueOf(getMsg())];

		if (range.length!=1) {
			for (int i = 0; i < range.length; i++) {
				sendMessage(userId,
						"כמה אנשים צריכים לאייש את עמדה מספר "+(i+1)+"?");

				getUpdates();
				range[i] = Integer.valueOf(getMsg());
			}
		} else {
			range[0] = numOfPips;
		}

		sendMessage(userId,
				"אוקי מצויין!😃 עכשיו נמלא את הפרטים.");

		String name = null;
		float priority = 0;
		int[] preference = new int[2];
		Profile[] profiles = new Profile[numOfPips];

		for (int i = 0; i < profiles.length; i++) {
			sendMessage(userId,
					"מה השם של הבן אדם ה-"+(i+1)+"?");
			getUpdates();
			name=getMsg();

			sendMessage(userId,
					"כמה העדפה יש לאדם ה-"+(i+1)+"? (מ-0 עד 10)");
			getUpdates();
			priority=Float.valueOf(getMsg())/10;


			if (range==null || range.length==1) {
				preference[0]=0;
				range = new int[] {numOfPips};
			} else {
				sendMessage(userId,
						"באיזו עמדה האדם ה-"+(i+1)+" מעדיף לשמור? (מ-0 עד "+(range.length-1)+")");
				getUpdates();
				preference[0]=Integer.valueOf(getMsg());
			}

			sendMessage(userId,
					"באיזו שעה האדם ה-"+(i+1)+"מעדיף לשמור? (מ-0 עד "+(range[preference[0]]-1)+")");
			getUpdates();
			preference[1]=Integer.valueOf(getMsg());

			profiles[i] = new Profile(name, priority, preference);
			sendMessage(userId,
					"הנה הפרופיל לאדם ה"+i+"\n"+profiles[i].toString()+".");
			if (i+1!=profiles.length) sendMessage(userId,
					"עכשיו בוא נמשיך להבא!😁");
		}

		sendOptions("זהו!, הכל שמור אצלי!🔐");

		data.savedProfiles=profiles;
		data.savedRange=range;
	}

	private void getUpdates() {
		// TODO Auto-generated method stub
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void calcSavedProfiles() throws IOException {
		sendMessage(userId,
				"תן לי כמה רגעים ואני יחשב את הרשימה האופטימלית🤓");
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(data.savedProfiles, data.savedRange));
		data.savedProfiles = bestDna.getGenome().getProfiles();
		sendMessage(userId,
				bestDna.getGenome().hebtoString());
	}

	private void manualEdit() throws IOException {
		String strNames = "";
		for (int i = 0; i < data.savedProfiles.length-1; i++) {
			strNames+="[{\"text\":\""+URLEncoder.encode(data.savedProfiles[i].getName(), StandardCharsets.UTF_8)+"\"}],";
		}
		strNames+="[{\"text\":\""+URLEncoder.encode(data.savedProfiles[data.savedProfiles.length-1].getName(), StandardCharsets.UTF_8)+"\"}]";

		sendMessage(userId,
				"בחר למי לבצע שינוי במקלדת המותאמת האישית!👪",
				"reply_markup={\"keyboard\":["+strNames+"]}");

		getUpdates();

		String name=getMsg();

		sendMessage(userId,
				"מצויין😃,עכשיו תבחר מה אתה רוצה לשנות!📊",
				"reply_markup={\"keyboard\":["+
						"[{\"text\":\""+URLEncoder.encode("שם", StandardCharsets.UTF_8)+"\"}],"+
						"[{\"text\":\""+URLEncoder.encode("העדפה", StandardCharsets.UTF_8)+"\"}],"+
						"[{\"text\":\""+URLEncoder.encode("עמדה מועדפת", StandardCharsets.UTF_8)+"\"}],"+
						"[{\"text\":\""+URLEncoder.encode("שעה מועדפת", StandardCharsets.UTF_8)+"\"}],"+
						"[{\"text\":\""+URLEncoder.encode("עמדה סופית", StandardCharsets.UTF_8)+"\"}],"+
						"[{\"text\":\""+URLEncoder.encode("שעה סופית", StandardCharsets.UTF_8)+"\"}]"+
				"]}");

		getUpdates();

		switch (getMsg().toLowerCase()) {
		case "שם":
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השם החדש בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setName(getMsg());

					sendMessage(userId,
							name+" שונה ל"+getMsg()+"👨");
					return;
				}
			}
			break;

		case "העדפה":
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את ההעדפה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setPriority(Float.valueOf(getMsg()));

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "עמדה מועדפת":
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את העמדה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setPreference(new int[] {Integer.valueOf(getMsg()), data.savedProfiles[i].getPreference()[1]});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "שעה מועדפת":
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השעה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setPreference(new int[] {data.savedProfiles[i].getPreference()[0], Integer.valueOf(getMsg())});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "עמדה סופית":
			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את העמדה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setPost(new int[] {Integer.valueOf(getMsg()), data.savedProfiles[i].getPost()[1]});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "שעה סופית":
			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
			for (int i = 0; i < data.savedProfiles.length; i++) {
				if (data.savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השעה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates();

					data.savedProfiles[i].setPost(new int[] {data.savedProfiles[i].getPost()[0], Integer.valueOf(getMsg())});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		default:
			throw new IOException();
		}
	}

}
