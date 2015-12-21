package loon.web.server.mini;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

	public static String indexPage = "index";

	private static Server self;
	private ServerSocket socket;
	private Thread run;

	private Config config;
	private boolean running = false;

	public Server(String config) throws Exception {
		try {
			this.config = new Config(config);
			Server.self = this;
		} catch (Exception e) {
			System.err.println("the config is error");
			throw e;
		}
	}
	
	public void open() throws IOException{
		Page.loadPages();
		socket = new ServerSocket(this.config.getPort());
		System.out.println("Started Server on port: " + this.config.getPort());
		run = new Thread(this, "WebServer");
		run.start();
		running = true;
	}

	public void run() {
		while (running) {
			try {
				Socket clientSocket = socket.accept();
				Thread client = new Thread(new WebClient(clientSocket),
						"WebClient");
				client.start();
			} catch (Exception e) {
				if (!running)
					return;
				e.printStackTrace();
			}
		}
	}

	public void process(Socket clientSocket, Request request)
			throws IOException {

		Response response = new Response(clientSocket);

		for (Page page : Page.pages) {
			if (page.getName().equals(request.getPage())) {
				page.called(request, response);
				return;
			}
		}

		if (request.getPage().equals("/")) {
			try {
				File file = new File(config.getWebDir() + '/' + indexPage
						+ ".class");
				if (file.exists()) {
					Page page = Page.load(file);
					if (page == null) {
						throw new FileNotFoundException();
					}
					page.called(request, response);
					return;
				}
				response.setContentType("text/html");
				response.sendFile(new File(config.getWebDir() + '/' + indexPage
						+ ".html"));
				return;
			} catch (FileNotFoundException e) {
				response.setResponse("HTTP/1.1 404 UNFOUND");
				response.sendFile(new File("404.html"));
				return;
			} catch (IOException e) {
				response.setResponse("HTTP/1.1 404 UNFOUND");
				response.sendFile(new File("404.html"));
				return;
			}
		}

		try {
			File file = new File(config.getWebDir() + request.getPage()
					+ ".class");
			if (file.exists()) {
				Page page = Page.load(file);
				if (page == null) {
					throw new FileNotFoundException();
				}
				page.called(request, response);
			} else {
				response.sendFile(new File(config.getWebDir() + '/'
						+ request.getPage()));
			}
			return;
		} catch (FileNotFoundException e) {
			response.setResponse("HTTP/1.1 404 UNFOUND");
			response.sendFile(new File("404.html"));
			return;
		} catch (IOException e) {
			response.setResponse("HTTP/1.1 404 UNFOUND");
			response.sendFile(new File("404.html"));
			return;
		}
	}

	public void stop() throws Exception {
		running = false;
		socket.close();
		run.join();
	}

	public static Server getServer() {
		return self;
	}

	public Config getConfig() {
		return config;
	}

}
