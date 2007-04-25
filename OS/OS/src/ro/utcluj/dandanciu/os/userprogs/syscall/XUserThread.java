/**
 * 
 */
package ro.utcluj.dandanciu.os.userprogs.syscall;

import org.apache.log4j.Logger;

import ro.utcluj.dandanciu.os.threads.KernelCallType;
import ro.utcluj.dandanciu.os.threads.XThreadAbstract;
import ro.utcluj.dandanciu.os.threads.servers.FileSystemServer;
import ro.utcluj.dandanciu.os.threads.tasks.SystemTask;

/**
 * @author Dan Danciu
 * 
 */
public abstract class XUserThread extends XThreadAbstract implements Syscall {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(XUserThread.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#accept(int)
	 */
	public int accept(int port) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#close(int)
	 */
	public int close(int fileDescriptor) {
		try {
			return FileSystemServer.close(this, fileDescriptor);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#connect(int, int)
	 */
	public int connect(int host, int port) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#creat(java.lang.String)
	 */
	public int create(String name) {
		try {
			return FileSystemServer.create(this, name);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#exec(java.lang.String,
	 *      int, char[])
	 */
	public int exec(String file, int argc, String argv) {
		
		return (Integer) SystemTask.getInstance().kernellCall(
				KernelCallType.SYS_EXEC, new Object[] { this, file, argc, argv });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#exit(int)
	 */
	public void exit(int status) {
		SystemTask.getInstance().kernellCall(KernelCallType.SYS_EXIT,
				new Object[] { status });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#halt()
	 */
	/**
	 * Halts the system.
	 * Makes the ABORT kernel call.
	 */
	public void halt() {
		SystemTask.getInstance().kernellCall(KernelCallType.SYS_ABORT,
				new Object[] {});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#join(int,
	 *      java.lang.Integer)
	 */
	public int join(int processID, Integer status) {
		return (Integer) SystemTask.getInstance().kernellCall(KernelCallType.SYS_JOIN, 
			new Object[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#mmap(int, char[])
	 */
	public int mmap(int fileDescriptor, char[] address) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#open(java.lang.String)
	 */
	public int open(String name) {
		try {
			return FileSystemServer.open(this, name);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#read(int, byte[],
	 *      int)
	 */
	public int read(int fileDescriptor, byte[] buffer, int count) {
		try {
			return FileSystemServer.read(this, fileDescriptor, buffer);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#unlink(java.lang.String)
	 */
	public int unlink(String name) {
		try {
			return FileSystemServer.delete(this, name);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ro.utcluj.dandanciu.os.userprogs.syscall.Syscall#write(int, byte[],
	 *      int)
	 */
	public int write(int fileDescriptor, byte[] buffer, int count) {		
		try {
			return FileSystemServer.write(this, fileDescriptor, buffer, count);
		}catch (Exception e) {
			logger.error(e);
		}
		return -1;	
	}

}
