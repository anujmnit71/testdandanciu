// PART OF THE MACHINE SIMULATION. DO NOT CHANGE.

package ro.utcluj.dandanciu.os.filesystem.stub;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.os.filesystem.FileSystem;
import ro.utcluj.dandanciu.os.filesystem.OpenFile;
import ro.utcluj.dandanciu.os.userprogs.syscall.XUserThread;

/**
 * This class implements a file system that redirects all requests to the host
 * operating system's file system.
 */
public class StubFileSystem implements FileSystem {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(StubFileSystem.class);

	/**
	 * Allocate a new stub file system.
	 * 
	 * @param directory
	 *            the root directory of the stub file system.
	 */
	public StubFileSystem(File directory) {
		this.directory = directory;
	}

	public OpenFile open(XUserThread current, String name, boolean truncate) {
		if (!checkName(name))
			return null;

		delay(current);

		try {
			return new StubOpenFile(name, truncate);
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

	public boolean remove(XUserThread current, String name) {

		if (!checkName(name))
			return false;

		delay(current);
		
		return new File(directory, name).delete();
	}

	private void delay(XUserThread current) {
		current.sleep(FileSystemStubUtils.DiskDelay);
	}

	private class StubOpenFile extends OpenFileWithPosition {
		StubOpenFile(final String name, final boolean truncate)
				throws IOException {
			super(StubFileSystem.this, name);

			final File f = new File(directory + name);

			if (openCount == maxOpenFiles)
				throw new IOException();

			getRandomAccessFile(f, truncate);

			if (file == null)
				throw new IOException();

			open = true;
			openCount++;
		}

		private void getRandomAccessFile(File f, boolean truncate) {
			try {
				if (!truncate && !f.exists())
					return;

				file = new RandomAccessFile(f, "rw");

				if (truncate)
					file.setLength(0);
			} catch (IOException e) {
			}
		}

		public int read(XUserThread current, int pos, byte[] buf, int offset, int length) {
			if (!open)
				return -1;

			try {
				delay(current);

				file.seek(pos);
				return Math.max(0, file.read(buf, offset, length));
			} catch (IOException e) {
				return -1;
			}
		}

		public int write(XUserThread current, int pos, byte[] buf, int offset, int length) {
			if (!open)
				return -1;

			try {
				delay(current);

				file.seek(pos);
				file.write(buf, offset, length);
				return length;
			} catch (IOException e) {
				return -1;
			}
		}

		public int length() {
			try {
				return (int) file.length();
			} catch (IOException e) {
				return -1;
			}
		}

		public void close() {
			if (open) {
				open = false;
				openCount--;
			}

			try {
				file.close();
			} catch (IOException e) {
			}
		}

		private RandomAccessFile file = null;

		private boolean open = false;
	}

	private int openCount = 0;

	private static final int maxOpenFiles = 16;

	private File directory;

	private static boolean checkName(String name) {
		char[] chars = name.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] < 0 || chars[i] >= allowedFileNameCharacters.length)
				return false;
			if (!allowedFileNameCharacters[(int) chars[i]])
				return false;
		}
		return true;
	}

	private static boolean[] allowedFileNameCharacters = new boolean[0x80];

	private static void allow(char c) {
		allowedFileNameCharacters[c] = true;
	}

	private static void reject(char first, char last) {
		for (char c = first; c <= last; c++)
			allowedFileNameCharacters[c] = false;
	}

	private static void allow(char first, char last) {
		for (char c = first; c <= last; c++)
			allowedFileNameCharacters[c] = true;
	}

	static {
		reject((char) 0x00, (char) 0x7F);

		allow('A', 'Z');
		allow('a', 'z');
		allow('0', '9');

		allow('-');
		allow('_');
		allow('.');
		allow(',');
		allow('/');
	}
}
