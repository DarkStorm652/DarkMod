package org.darkstorm.tools.io;

import java.awt.Desktop;
import java.io.*;
import java.net.*;

import javax.swing.JProgressBar;

public class IOTools {
	private IOTools() {
	}

	public static void download(URL url, File file, JProgressBar progressBar)
			throws IOException {
		URLConnection uc = url.openConnection();
		int len = uc.getContentLength();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		progressBar.setMinimum(0);
		progressBar.setMaximum(len);
		try {
			byte[] data = new byte[len];
			int offset = 0;
			while(offset < len) {
				int read = is.read(data, offset, data.length - offset);
				if(read < 0) {
					break;
				}
				offset += read;
				progressBar.setValue(offset);
			}
			if(offset < len)
				throw new IOException(String.format(
						"Read %d bytes; expected %d", offset, len));
			File f = new File(file.getAbsolutePath());
			if(!f.exists()) {
				File temp = new File(f.getParent());
				if(!temp.exists()) {
					temp.mkdirs();
				}
				f.createNewFile();
			}
			if(f.isDirectory())
				throw new IllegalArgumentException("file cannot be a dir");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.flush();
			fos.close();
		} finally {
			is.close();
		}
	}

	public static void download(URL url, File file) throws IOException {
		URLConnection uc = url.openConnection();
		int len = uc.getContentLength();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		try {
			byte[] data = new byte[len];
			int offset = 0;
			while(offset < len) {
				int read = is.read(data, offset, data.length - offset);
				if(read < 0) {
					break;
				}
				offset += read;
			}
			if(offset < len)
				throw new IOException(String.format(
						"Read %d bytes; expected %d", offset, len));
			File f = new File(file.getAbsolutePath());
			if(!f.exists()) {
				File temp = new File(f.getParent());
				if(!temp.exists()) {
					temp.mkdirs();
				}
				f.createNewFile();
			}
			if(f.isDirectory())
				throw new IllegalArgumentException("file cannot be a dir");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.flush();
			fos.close();
		} finally {
			is.close();
		}
	}

	public static byte[] download(URL url) throws IOException {
		URLConnection uc = url.openConnection();
		int len = uc.getContentLength();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		try {
			byte[] data = new byte[len];
			int offset = 0;
			int updateCounter = 0;
			while(offset < len) {
				int read = is.read(data, offset, data.length - offset);
				if(read < 0) {
					break;
				}
				updateCounter += read;
				offset += read;
				if(updateCounter > 32) {
					updateCounter = 0;
				}
			}
			if(offset < len)
				throw new IOException(String.format(
						"Read %d bytes; expected %d", offset, len));
			return data;
		} finally {
			is.close();
		}
	}

	public static byte[] readAll(InputStream in) throws IOException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while((bytesRead = in.read(buffer)) != -1)
				byteArrayOut.write(buffer, 0, bytesRead);
			byteArrayOut.close();
			return byteArrayOut.toByteArray();
		} finally {
			in.close();
		}
	}

	public static void openDefaultBrowser(String url)
			throws MalformedURLException, IOException, URISyntaxException {
		Desktop.getDesktop().browse(new URL(url).toURI());
	}

	public static String readPage(String s) {
		try {
			URL url = new URL(s);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String html = "";
			String line;
			while((line = br.readLine()) != null) {
				html += line;
			}
			return html;
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveToFile(byte[] bytes, File file) throws IOException {
		FileWriter fileWriter = new FileWriter(file);
		for(int c : bytes)
			fileWriter.write(c);
		fileWriter.flush();
		fileWriter.close();
	}

	public static boolean testConnection(String urlstr) {
		try {
			URL url = new URL(urlstr);
			url.openStream();
			return true;
		} catch(Exception e) {

		}
		return false;
	}

	/**
	 * Upload a file to a FTP server. A FTP URL is generated with the following
	 * syntax: ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 *            , FTP server address (optional port ':portNumber').
	 * @param user
	 *            , Optional user name to login.
	 * @param password
	 *            , Optional password for user.
	 * @param fileName
	 *            , Destination file name on FTP server (with optional preceding
	 *            relative path, e.g. "myDir/myFile.txt").
	 * @param source
	 *            , Source file to upload.
	 * @throws MalformedURLException
	 *             , IOException on error.
	 */
	public void upload(String ftpServer, String user, String password,
			String fileName, File source) throws MalformedURLException,
			IOException {
		if(ftpServer != null && fileName != null && source != null) {
			StringBuffer sb = new StringBuffer("ftp://");
			// check for authentication else assume its anonymous access.
			if(user != null && password != null) {
				sb.append(user);
				sb.append(':');
				sb.append(password);
				sb.append('@');
			}
			sb.append(ftpServer);
			sb.append('/');
			sb.append(fileName);
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append(";type=i");

			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				URL url = new URL(sb.toString());
				URLConnection urlc = url.openConnection();

				bos = new BufferedOutputStream(urlc.getOutputStream());
				bis = new BufferedInputStream(new FileInputStream(source));

				int i;
				// read byte by byte until end of stream
				while((i = bis.read()) != -1) {
					bos.write(i);
				}
			} finally {
				if(bis != null) {
					try {
						bis.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
				if(bos != null) {
					try {
						bos.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("Input not available.");
		}
	}

	/**
	 * Download a file from a FTP server. A FTP URL is generated with the
	 * following syntax: ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 *            , FTP server address (optional port ':portNumber').
	 * @param user
	 *            , Optional user name to login.
	 * @param password
	 *            , Optional password for user.
	 * @param fileName
	 *            , Name of file to download (with optional preceeding relative
	 *            path, e.g. one/two/three.txt).
	 * @param destination
	 *            , Destination file to save.
	 * @throws MalformedURLException
	 *             , IOException on error.
	 */
	public void download(String ftpServer, String user, String password,
			String fileName, File destination) throws MalformedURLException,
			IOException {
		if(ftpServer != null && fileName != null && destination != null) {
			StringBuffer sb = new StringBuffer("ftp://");
			// check for authentication else assume its anonymous access.
			if(user != null && password != null) {
				sb.append(user);
				sb.append(':');
				sb.append(password);
				sb.append('@');
			}
			sb.append(ftpServer);
			sb.append('/');
			sb.append(fileName);
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append(";type=i");
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				URL url = new URL(sb.toString());
				URLConnection urlc = url.openConnection();

				bis = new BufferedInputStream(urlc.getInputStream());
				bos = new BufferedOutputStream(new FileOutputStream(
						destination.getName()));

				int i;
				while((i = bis.read()) != -1) {
					bos.write(i);
				}
			} finally {
				if(bis != null) {
					try {
						bis.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
				if(bos != null) {
					try {
						bos.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("Input not available");
		}
	}
}
