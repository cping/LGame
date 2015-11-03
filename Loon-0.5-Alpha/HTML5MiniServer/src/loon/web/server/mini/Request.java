package loon.web.server.mini;

import java.util.ArrayList;
import java.util.HashMap;

public class Request {

	private String header;
	private String page;
	private HashMap<String, String> get;

	private ArrayList<String> message;
	private HashMap<String, String> post;

	public Request(ArrayList<String> message, String post) throws Exception {
		this.message = message;
		this.post = phrase(post);
		this.header = message.get(0);
		this.page = header.split(" ")[1];
		if (page.contains("?")) {
			this.get = phrase(page.split("\\?")[1]);
			this.page = page.split("\\?")[0];

		}
	}

	private HashMap<String, String> phrase(String args) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String string : args.split("&")) {
			if (string.split("=").length >= 2) {
				map.put(string.split("=")[0], string.split("=")[1]);
			}
		}
		return map;
	}

	public HashMap<String, String> getPost() {
		return post;
	}

	public ArrayList<String> getMessage() {
		return message;
	}

	public String getHeader() {
		return header;
	}

	public String getPage() {
		return page;
	}

	public HashMap<String, String> getGet() {
		return get;
	}
}
