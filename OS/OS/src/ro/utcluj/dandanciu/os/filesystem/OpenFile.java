package ro.utcluj.dandanciu.os.filesystem;

import ro.utcluj.dandanciu.os.userprogs.syscall.XUserThread;


public interface OpenFile extends Comparable{

	/**
	 * Get the file system to which this file belongs.
	 *
	 * @return	the file system to which this file belongs.
	 */
	FileSystem getFileSystem();

	/**
	 * Get the name of this open file.
	 *
	 * @return	the name of this open file.
	 */
	String getName();

	/**
	 * Read this file starting at the specified position and return the number
	 * of bytes successfully read. If no bytes were read because of a fatal
	 * error, returns -1
	 *
	 * @param	pos	the offset in the file at which to start reading.
	 * @param	buf	the buffer to store the bytes in.
	 * @param	offset	the offset in the buffer to start storing bytes.
	 * @param	length	the number of bytes to read.
	 * @return	the actual number of bytes successfully read, or -1 on failure.
	 */
	int read(XUserThread current, int pos, byte[] buf, int offset, int length);

	/**
	 * Write this file starting at the specified position and return the number
	 * of bytes successfully written. If no bytes were written because of a
	 * fatal error, returns -1.
	 *
	 * @param	pos	the offset in the file at which to start writing.
	 * @param	buf	the buffer to get the bytes from.
	 * @param	offset	the offset in the buffer to start getting.
	 * @param	length	the number of bytes to write.
	 * @return	the actual number of bytes successfully written, or -1 on
	 *		failure.
	 */
	int write(XUserThread current, int pos, byte[] buf, int offset, int length);

	/**
	 * Get the length of this file.
	 *
	 * @return	the length of this file, or -1 if this file has no length.
	 */
	int length();

	/**
	 * Close this file and release any associated system resources.
	 */
	void close();

	/**
	 * Set the value of the current file pointer.
	 */
	void seek(int pos);

	/**
	 * Get the value of the current file pointer, or -1 if this file has no
	 * pointer.
	 */
	int tell();

	/**
	 * Read this file starting at the current file pointer and return the
	 * number of bytes successfully read. Advances the file pointer by this
	 * amount. If no bytes could be* read because of a fatal error, returns -1.
	 *
	 * @param	buf	the buffer to store the bytes in.
	 * @param	offset	the offset in the buffer to start storing bytes.
	 * @param	length	the number of bytes to read.
	 * @return	the actual number of bytes successfully read, or -1 on failure.
	 */
	int read(XUserThread current, byte[] buf, int offset, int length);

	/**
	 * Write this file starting at the current file pointer and return the
	 * number of bytes successfully written. Advances the file pointer by this
	 * amount. If no bytes could be written because of a fatal error, returns
	 * -1.
	 *
	 * @param	buf	the buffer to get the bytes from.
	 * @param	offset	the offset in the buffer to start getting.
	 * @param	length	the number of bytes to write.
	 * @return	the actual number of bytes successfully written, or -1 on
	 *		failure.
	 */
	int write(XUserThread current, byte[] buf, int offset, int length);

}