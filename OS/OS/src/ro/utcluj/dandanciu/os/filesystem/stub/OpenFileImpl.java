// PART OF THE MACHINE SIMULATION. DO NOT CHANGE.

package ro.utcluj.dandanciu.os.filesystem.stub;

import ro.utcluj.dandanciu.os.filesystem.FileSystem;
import ro.utcluj.dandanciu.os.filesystem.OpenFile;
import ro.utcluj.dandanciu.os.userprogs.syscall.XUserThread;

/**
 * A file that supports reading, writing, and seeking.
 */
public class OpenFileImpl implements ro.utcluj.dandanciu.os.filesystem.OpenFile {
    /**
     * Allocate a new <tt>OpenFile</tt> object with the specified name on the
     * specified file system.
     *
     * @param	fileSystem	the file system to which this file belongs.
     * @param	name		the name of the file, on that file system.
     */
    public OpenFileImpl(FileSystem fileSystem, String name) {
	this.fileSystem = fileSystem;
	this.name = name;
    }

    /**
     * Allocate a new unnamed <tt>OpenFile</tt> that is not associated with any
     * file system.
     */
    public OpenFileImpl() {
	this(null, "unnamed");
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#getFileSystem()
	 */
    public FileSystem getFileSystem() {
	return fileSystem;
    }
    
    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#getName()
	 */
    public String getName() {
	return name;
    }
    
    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#read(int, byte[], int, int)
	 */    
    public int read(XUserThread current, int pos, byte[] buf, int offset, int length) {
	return -1;
    }
    
    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#write(int, byte[], int, int)
	 */    
    public int write(XUserThread current, int pos, byte[] buf, int offset, int length) {
	return -1;
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#length()
	 */
    public int length() {
	return -1;
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#close()
	 */
    public void close() {
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#seek(int)
	 */
    public void seek(int pos) {
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#tell()
	 */
    public int tell() {
	return -1;
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#read(byte[], int, int)
	 */    
    public int read(XUserThread current, byte[] buf, int offset, int length) {
	return -1;
    }

    /* (non-Javadoc)
	 * @see ro.utcluj.dandanciu.os.filesystem.stub.OpenFileX#write(byte[], int, int)
	 */    
    public int write(XUserThread current, byte[] buf, int offset, int length) {
	return -1;
    }

    private FileSystem fileSystem;
    private String name;
	public int compareTo(Object arg0) {
		return name.compareTo(((OpenFile)arg0).getName());
	}
}
