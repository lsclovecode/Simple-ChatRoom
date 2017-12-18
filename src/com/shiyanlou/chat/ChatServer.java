package com.shiyanlou.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

/**
 * chatroom
 * @author Sicong Liu
 *
 */
@ServerEndpoint("/websocket")
public class ChatServer {
	
	
	private static final Set<Session> clients = new HashSet<Session>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
	private Session deleteSession;
	
	@OnOpen
	public void open(Session session) {
		
		clients.add(session);
		deleteSession = session;
	}
	
	/**
	 * 
	 * receive message from client side and send the messages to all connected conversations
	 * @param message from client side
	 * @param session conversion at client 
	 */
	@OnMessage
	public void getMessage(String message, Session session) {
		// parse the message from client side into JSON object
		JSONObject jsonObject = JSONObject.fromObject(message);
		// add send date in message
		jsonObject.put("date", DATE_FORMAT.format(new Date()));
		// send message to all connected conversation
		for (Session openSession : clients) {
			// add this sign as this message in cuurent conversation 
			jsonObject.put("isSelf", openSession.equals(session));
			// send messages with json
			openSession.getAsyncRemote().sendText(jsonObject.toString());
		}
	}

	@OnClose
	public void close() {
		// add opeartions for closing
		clients.remove(deleteSession);
	}

	@OnError
	public void error(Throwable t) {
		
	}
}