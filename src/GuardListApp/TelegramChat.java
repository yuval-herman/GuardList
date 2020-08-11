package GuardListApp;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import dna.Dna;
import dna.Profile;
import dna.Schedule;

public class TelegramChat implements Runnable{
	int userId;
	JSONObject activatorMsg;
	Profile profile;
	TelegramData data;
	boolean isAdmin;
	Profile[] connectedProfiles;
	
	public TelegramChat(int userId, JSONObject activatorMsg, Profile profile, TelegramData data) {
		this.userId = userId;
		this.activatorMsg = activatorMsg;
		this.profile = profile;
		this.data = data;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (getMsg().toLowerCase()) {
		case "רשימה חדשה":
			try {
				makeSchedule();
			} catch (Exception e) {
				TelegramApi.sendMessage(data.getRequestUrl(), userId,
						"קרתה תקלה, נסה שוב🤪.",
						"reply_markup={\"remove_keyboard\":true}");
			}
			break;

		case "חישוב רשימת שמות":
			try {
				if (savedProfiles==null||savedProfiles.length==0) {
					sendMessage(userId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
					break;
				}
				calcSavedProfiles();
			} catch (Exception e) {
				sendMessage(userId,
						"קרתה תקלה, נסה שוב🤪.",
						"reply_markup={\"remove_keyboard\":true}");
			}
			break;

		case "שינוי ידני":
			try {
				if (savedProfiles==null||savedProfiles.length==0) {
					sendMessage(userId,"אין מידע על אנשים במערכת, נסה קודם ליצור רשימת שמות📓");
					break;
				}
				manualEdit();
				sendMessage(userId,
						"שבצ\"ק מעודכן:");
				sendMessage(userId,
						new Schedule(savedProfiles, savedRange).hebtoString());
			} catch (Exception e) {
				sendMessage(userId,
						"קרתה תקלה, נסה שוב🤪.",
						"reply_markup={\"remove_keyboard\":true}");
			}
			break;

		default:
			sendOptions("נסה להשתמש במקלדת המותאמת אישית כדי לשלוח פקודה שאני יבין👇");
			break;
		}
	}

	/**
	 * gets the text from the first message received from the server
	 * @return text from message object
	 */
	private String getMsg() {
		return (String) activatorMsg.query("/message/text");
	}
	
	private static void sendOptions(String text) throws IOException {
		httpsRequstMethod("sendMessage", "chat_id="+lastUserId+"&text="+URLEncoder.encode(text, StandardCharsets.UTF_8)
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
	
	private void saveProfiles() throws IOException {
		sendMessage(userId,
				"כמה אנשים נמצאים?",
				"reply_markup={\"remove_keyboard\":true}");

		getUpdates(-1);

		int numOfPips = Integer.valueOf(getMsg(0));

		sendMessage(userId,
				"וכמה עמדות יש?🏠");

		getUpdates(-1);
		int[] range = new int[Integer.valueOf(getMsg(0))];

		if (range.length!=1) {
			for (int i = 0; i < range.length; i++) {
				sendMessage(userId,
						"כמה אנשים צריכים לאייש את עמדה מספר "+(i+1)+"?");

				getUpdates(-1);
				range[i] = Integer.valueOf(getMsg(0));
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
			getUpdates(-1);
			name=getMsg(0);

			sendMessage(userId,
					"כמה העדפה יש לאדם ה-"+(i+1)+"? (מ-0 עד 10)");
			getUpdates(-1);
			priority=Float.valueOf(getMsg(0))/10;


			if (range==null || range.length==1) {
				preference[0]=0;
				range = new int[] {numOfPips};
			} else {
				sendMessage(userId,
						"באיזו עמדה האדם ה-"+(i+1)+" מעדיף לשמור? (מ-0 עד "+(range.length-1)+")");
				getUpdates(-1);
				preference[0]=Integer.valueOf(getMsg(0));
			}

			sendMessage(userId,
					"באיזו שעה האדם ה-"+(i+1)+"מעדיף לשמור? (מ-0 עד "+(range[preference[0]]-1)+")");
			getUpdates(-1);
			preference[1]=Integer.valueOf(getMsg(0));

			profiles[i] = new Profile(name, priority, preference);
			sendMessage(userId,
					"הנה הפרופיל לאדם ה"+i+"\n"+profiles[i].toString()+".");
			if (i+1!=profiles.length) sendMessage(userId,
					"עכשיו בוא נמשיך להבא!😁");
		}

		sendOptions("זהו!, הכל שמור אצלי!🔐");

		savedProfiles=profiles;
		savedRange=range;
	}

	private void calcSavedProfiles() throws IOException {
		sendMessage(userId,
				"תן לי כמה רגעים ואני יחשב את הרשימה האופטימלית🤓");
		ScheduleGenerator scheduleGenerator = new ScheduleGenerator();
		Dna bestDna = scheduleGenerator.calculateBestSchedule(new Schedule(savedProfiles, savedRange));
		savedProfiles = bestDna.getGenome().getProfiles();
		sendMessage(userId,
				bestDna.getGenome().hebtoString());
	}

	private void manualEdit() throws IOException {
		String strNames = "";
		for (int i = 0; i < savedProfiles.length-1; i++) {
			strNames+="[{\"text\":\""+URLEncoder.encode(savedProfiles[i].getName(), StandardCharsets.UTF_8)+"\"}],";
		}
		strNames+="[{\"text\":\""+URLEncoder.encode(savedProfiles[savedProfiles.length-1].getName(), StandardCharsets.UTF_8)+"\"}]";

		sendMessage(userId,
				"בחר למי לבצע שינוי במקלדת המותאמת האישית!👪",
				"reply_markup={\"keyboard\":["+strNames+"]}");

		getUpdates(-1);

		String name=getMsg(0);

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

		getUpdates(-1);

		switch (getMsg(0).toLowerCase()) {
		case "שם":
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השם החדש בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setName(getMsg(0));

					sendMessage(userId,
							name+" שונה ל"+getMsg(0)+"👨");
					return;
				}
			}
			break;

		case "העדפה":
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את ההעדפה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setPriority(Float.valueOf(getMsg(0)));

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "עמדה מועדפת":
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את העמדה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setPreference(new int[] {Integer.valueOf(getMsg(0)), savedProfiles[i].getPreference()[1]});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "שעה מועדפת":
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השעה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setPreference(new int[] {savedProfiles[i].getPreference()[0], Integer.valueOf(getMsg(0))});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "עמדה סופית":
			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את העמדה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setPost(new int[] {Integer.valueOf(getMsg(0)), savedProfiles[i].getPost()[1]});

					sendMessage(userId,
							"הפעולה הושלמה בהצלחה🤖");
					return;
				}
			}
			break;

		case "שעה סופית":
			//TODO MAKE SURE YOU NOTICE THE USER AND SWAP THE STATION WITH SOMNE ELSE!!!
			for (int i = 0; i < savedProfiles.length; i++) {
				if (savedProfiles[i].getName().equals(name)) {
					sendMessage(userId,
							"עכשיו תשלח את השעה החדשה בשביל "+name,
							"reply_markup={\"remove_keyboard\":true}");
					getUpdates(-1);

					savedProfiles[i].setPost(new int[] {savedProfiles[i].getPost()[0], Integer.valueOf(getMsg(0))});

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
