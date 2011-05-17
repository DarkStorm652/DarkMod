package org.darkstorm.tools.io;

import java.io.*;
import java.util.Vector;

public class ReadableOutputStream extends OutputStream {
	private Vector<Integer> buffer = new Vector<Integer>();
	private Object bufferLock = new Object();
	private InputStream inputToOutputStream = new InputStream() {

		@Override
		public int read() throws IOException {
			return ReadableOutputStream.this.read();
		}
	};

	@Override
	public void write(int b) {
		synchronized(bufferLock) {
			buffer.add(b);
		}
	}

	public int read() {
		Integer firstElement;
		while(true) {
			synchronized(bufferLock) {
				if(!buffer.isEmpty()) {
					firstElement = buffer.remove(0);
					break;
				}
			}
			try {
				Thread.sleep(250);
			} catch(InterruptedException exception) {
			}
		}
		return firstElement;
	}

	public InputStream getInputToOutputStream() {
		return inputToOutputStream;
	}
}
