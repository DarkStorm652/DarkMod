package org.darkstorm.tools.io;

import java.io.PrintStream;

public class ReadablePrintStream extends PrintStream {
	private ReadableOutputStream readableOutputStream;

	public ReadablePrintStream(ReadableOutputStream readableOutputStream) {
		super(readableOutputStream);
		this.readableOutputStream = readableOutputStream;
	}

	public int read() {
		return readableOutputStream.read();
	}
}
