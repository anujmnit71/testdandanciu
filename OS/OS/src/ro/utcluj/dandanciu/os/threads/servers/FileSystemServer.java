package ro.utcluj.dandanciu.os.threads.servers;

import java.util.ArrayList;

import ro.utcluj.dandanciu.os.filesystem.FileSystem;
import ro.utcluj.dandanciu.os.filesystem.OpenFile;
import ro.utcluj.dandanciu.os.threads.FileManagementInfo;
import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;
import ro.utcluj.dandanciu.os.threads.util.InfoType;
import ro.utcluj.dandanciu.os.userprogs.syscall.XUserThread;
import ro.utcluj.dandanciu.os.utils.IdTable;
import ro.utcluj.dandanciu.os.utils.OsConfigOptions;
import ro.utcluj.dandanciu.os.utils.Utils;

public class FileSystemServer {

	private static FileSystem fileSystem = null;

	private static final class OpenFileTableEntry {
		public OpenFile openFile;

		public FileManagementInfo info;

		public boolean markedForDelete;
	}

	private static IdTable<OpenFileTableEntry> openFileTable = 
			new IdTable<OpenFileTableEntry>(OsConfigOptions.OpenFileTableInitialSize, OsConfigOptions.OpenFileTableSizeIncrement);

	public static void setFileSystem(FileSystem fs) {
		fileSystem = fs;
	}

	public static int create(XUserThread thread, String name) {
		OpenFileTableEntry entry = new OpenFileTableEntry();
		entry.openFile = fileSystem.open(thread, fixFileName(name,
				getFileManagementInfo(thread)), true);
		entry.info = getFileManagementInfo(thread);
		entry.info.getOpenFileList().add(entry.openFile);
		return openFileTable.put(entry);
	}

	public static int close(XUserThread thread, int fileDescriptor) {

		OpenFileTableEntry entry = openFileTable.get(fileDescriptor);
		openFileTable.clear(fileDescriptor);
		entry.openFile.close();
		if (entry.markedForDelete) {
			delete(thread, entry.openFile.getName());
		}
		return 0;
	}

	public static int open(XUserThread thread, String name) {
		OpenFileTableEntry entry = new OpenFileTableEntry();
		entry.openFile = fileSystem.open(thread, fixFileName(name,
				getFileManagementInfo(thread)), false);
		entry.info = getFileManagementInfo(thread);
		entry.info.getOpenFileList().add(entry.openFile);
		return openFileTable.put(entry);
	}

	/**
	 * 
	 * @param thread
	 * @param name
	 * @return 0 on success, or -1 if an error occurred.
	 */
	public static int delete(XUserThread thread, String name) {
		String fullName = fixFileName(name, getFileManagementInfo(thread));
		for (OpenFileTableEntry entry : openFileTable) {
			if (entry != null && entry.openFile.getName().equals(fullName)) {
				entry.markedForDelete = true;
				return 0;
			}
		}
		return fileSystem.remove(thread, fullName) ? 0 : -1;
	}

	public static int read(XUserThread thread, int fileDescriptor, byte[] buffer) {
		OpenFileTableEntry entry = openFileTable.get(fileDescriptor);
		return entry.openFile.read(thread, buffer, entry.openFile.tell(),
				buffer.length);
	}

	public static int write(XUserThread thread, int fileDescriptor,
			byte[] buffer, int count) {
		OpenFileTableEntry entry = openFileTable.get(fileDescriptor);
		return entry.openFile.write(thread, buffer, entry.openFile.tell(),
				buffer.length);
	}

	private static String fixFileName(String name, FileManagementInfo info) {
		if (name.startsWith(OsConfigOptions.filePathSeparator)) {
			// it is not a relative path
			return name;
		}
		return info.getWorkingDirectory() + name;
	}

	private static FileManagementInfo getFileManagementInfo(XUserThread thread) {
		return (FileManagementInfo) SystemTask.getInstance()
				.kernellCall(
						KernelCallType.SYS_GETINFO,
						new Object[] { InfoType.FILE_MANAGEMENT,
								thread.getThreadId() });
	}
}
