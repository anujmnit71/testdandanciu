package ro.utcluj.dandanciu.os.threads;

import java.util.Set;
import java.util.TreeSet;

import ro.utcluj.dandanciu.os.filesystem.OpenFile;
import ro.utcluj.dandanciu.os.utils.OsConfigOptions;

public class FileManagementInfo {

	/**
	 * Root directory
	 */
	private String rootDirectory = OsConfigOptions.filePathSeparator;
	/**
	 * Working directory
	 */
	private String workingDirectory = OsConfigOptions.filePathSeparator;
	/**
	 * File descriptors
	 */
	private Set<OpenFile> openFileList = new TreeSet<OpenFile>();
	/**
	 * @return the openFileList
	 */
	public Set<OpenFile> getOpenFileList() {
		return openFileList;
	}
	/**
	 * @param openFileList the openFileList to set
	 */
	public void setOpenFileList(Set<OpenFile> openFileList) {
		this.openFileList = openFileList;
	}
	/**
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}
	/**
	 * @param rootDirectory the rootDirectory to set
	 */
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	/**
	 * @return the workingDirectory
	 */
	public String getWorkingDirectory() {
		return workingDirectory;
	}
	/**
	 * @param workingDirectory the workingDirectory to set
	 */
	public void setWorkingDirectory(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}   


}
