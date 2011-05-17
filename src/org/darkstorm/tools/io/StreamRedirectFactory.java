package org.darkstorm.tools.io;

import java.io.*;

public class StreamRedirectFactory {
	private StreamRedirectFactory() {
	}

	public static Thread createInputToOutputRedirect(
			final InputStream inputStream, final OutputStream outputStream) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					int read;
					while((read = inputStream.read()) != -1) {
						outputStream.write(read);
					}
				} catch(IOException exception) {
					exception.printStackTrace();
				}
			}
		};
		thread.start();
		return thread;
	}

}
