package GuardListApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class TelegramApi {

	
	/**
	 * gets the text from the last message received from the server
	 * @param messageNum number of message out of returned array from server
	 * @return text from message object
	 */
//	private static String getMsgText(JSONObject updateObj) {
//		return (String) updateObj.query("/message/text");
//	}
	
	/**
	 * sends https request to telegram server
	 * @param method telegram api method string
	 * @param args arguments to telegram api method
	 * @return return json formatted object from telegram server
	 * @throws IOException
	 */
	public static JSONObject httpsRequstMethod(String requestUrl, String method, String... args) throws IOException{
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
	 * send a message to a chat by id
	 * @param chat_id id to send the message to
	 * @param text text to send in the message body
	 * @param args arguments to pass for the sendMessage telegram method
	 * @return return json formatted answer from server
	 * @throws IOException
	 */
	public static JSONObject sendMessage(String requestUrl, int chat_id, String text, String... args) throws IOException {
		String[] args2 = new String[args.length + 2];
		System.arraycopy(args, 0, args2, 0, args.length);
		args2[args2.length-2] = "chat_id="+chat_id;
		args2[args2.length-1] = "text="+URLEncoder.encode(text, StandardCharsets.UTF_8);
		return httpsRequstMethod(requestUrl, "sendMessage", args2);
	}

}
