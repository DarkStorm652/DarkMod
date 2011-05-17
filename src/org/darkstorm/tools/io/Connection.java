package org.darkstorm.tools.io;

import java.io.*;
import java.net.*;

public class Connection {
	private String host;
	private int port;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	public Connection(Socket socket) {
		if(!socket.isConnected())
			throw new IllegalArgumentException("Socket must be open");
		try {
			InetAddress address = socket.getInetAddress();
			host = address.getHostAddress();
			port = socket.getPort();
			this.socket = socket;
			createReader();
			createWriter();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
	}

	public Connection(String host, int port) {
		this.host = host;
		this.port = port;
		socket = new Socket();
	}

	public boolean connect() {
		try {
			if(socket.isConnected())
				return false;
			socket.connect(new InetSocketAddress(host, port));
			createReader();
			createWriter();
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	private void createReader() throws IOException {
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputReader = new InputStreamReader(inputStream);
		reader = new BufferedReader(inputReader);
	}

	private void createWriter() throws IOException {
		OutputStream outputStream = socket.getOutputStream();
		OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
		writer = new BufferedWriter(outputWriter);
	}

	public boolean disconnect() {
		if(socket.isConnected()) {
			try {
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
			reader = null;
			writer = null;
		}
		return true;
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

}
