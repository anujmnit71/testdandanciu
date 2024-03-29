package ro.utcluj.dandanciu.nachos.filesys;

import ro.utcluj.dandanciu.nachos.machine.Disk;
import ro.utcluj.dandanciu.nachos.threads.Lock;
import ro.utcluj.dandanciu.nachos.threads.Semaphore;

// SynchDisk.java
//	Class for synchronous access of the disk.  The physical disk 
//	is an asynchronous device (disk requests return immediately, and
//	an interrupt happens later on).  This is a layer on top of
//	the disk providing a synchronous interface (requests wait until
//	the request completes).
//
//	Use a semaphore to synchronize the interrupt handlers with the
//	pending requests.  And, because the physical disk can only
//	handle one operation at a time, use a lock to enforce mutual
//	exclusion.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

// The following class defines a "synchronous" disk abstraction.
// As with other I/O devices, the raw physical disk is an asynchronous device --
// requests to read or write portions of the disk return immediately,
// and an interrupt occurs later to signal that the operation completed.
// (Also, the physical characteristics of the disk device assume that
// only one operation can be requested at a time).
//
// This class provides the abstraction that for any individual thread
// making a request, it waits around until the operation finishes before
// returning.


public class SynchDisk {
  
  private Disk disk;	  		// Raw disk device
  private Semaphore semaphore; 		// To synchronize requesting thread 
				// with the interrupt handler
  private Lock lock;	  		// Only one read/write request
                                // can be sent to the disk at a time
  private SynchDiskIntHandler handler;  // internal handler

  //----------------------------------------------------------------------
  // SynchDisk
  // 	Initialize the synchronous interface to the physical disk, in turn
  //	initializing the physical disk.
  //
  //	"name" -- UNIX file name to be used as storage for the disk data
  //	   (usually, "DISK")
  //----------------------------------------------------------------------
  
  public SynchDisk(String name) {
    
    semaphore = new Semaphore("synch disk", 0);
    lock = new Lock("synch disk lock");
    handler = new SynchDiskIntHandler(this);
    disk = new Disk(name, handler);


  }

  //----------------------------------------------------------------------
  // readSector
  // 	Read the contents of a disk sector into a buffer.  Return only
  //	after the data has been read.
  //
  //	"sectorNumber" -- the disk sector to read
  //	"data" -- the buffer to hold the contents of the disk sector
  //----------------------------------------------------------------------

  public void readSector(int sectorNumber, byte[] data, int index) {
    lock.acquire();			// only one disk I/O at a time
    disk.readRequest(sectorNumber, data, index);
    semaphore.P();			// wait for interrupt
    lock.release();
  }

  //----------------------------------------------------------------------
  // writeSector
  // 	Write the contents of a buffer into a disk sector.  Return only
  //	after the data has been written.
  //
  //	"sectorNumber" -- the disk sector to be written
  //	"data" -- the new contents of the disk sector
  //----------------------------------------------------------------------

  public void writeSector(int sectorNumber, byte[] data, int index) {
    lock.acquire();			// only one disk I/O at a time
    disk.writeRequest(sectorNumber, data, index);
    semaphore.P();			// wait for interrupt
    lock.release();
  }

  //----------------------------------------------------------------------
  // requestDone
  // 	Disk interrupt handler.  Wake up any thread waiting for the disk
  //	request to finish.
  //----------------------------------------------------------------------

  public void requestDone() { 
    semaphore.V();
  }

}


// SynchDisk interrupt handler class
//
class SynchDiskIntHandler implements Runnable {
  private SynchDisk disk;

  public SynchDiskIntHandler(SynchDisk dsk) {
    disk = dsk;
  }
  
  public void run() {
    disk.requestDone();
  }
}


