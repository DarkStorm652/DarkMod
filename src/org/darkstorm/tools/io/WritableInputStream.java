package org.darkstorm.tools.io;

import java.io.*;
import java.util.Vector;

public class WritableInputStream extends InputStream {
	private Vector<Integer> buffer = new Vector<Integer>();
	private Object bufferLock = new Object();
	private OutputStream outputToInputStream = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			WritableInputStream.this.write(b);
		};
	};

	@Override
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

	public void write(int b) {
		synchronized(bufferLock) {
			buffer.add(b);
		}
	}

	public OutputStream getOutputToInputStream() {
		return outputToInputStream;
	}
}
