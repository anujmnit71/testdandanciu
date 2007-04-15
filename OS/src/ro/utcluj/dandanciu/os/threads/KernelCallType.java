package ro.utcluj.dandanciu.os.threads;

public enum KernelCallType {
	
	  SYS_FORK,			// A process has forked

	  SYS_EXEC, 		//Set stack pointer after EXEC call
	 
	  SYS_EXIT,			//A process has exited
	 
	  SYS_NICE,			//Set scheduling priority
	 
	  SYS_PRIVCTL,		//Set or change privileges
	 
	  SYS_TRACE,		//Carry out an operation of the PTRACE call
	 
	  SYS_KILL,			//Send signal to a process after KILL call
	 
	  SYS_GETKSIG,		//Process manager (PM) is checking for pending signals
	 
	  SYS_ENDKSIG, 		//PM has finished processing signal
	 
	  SYS_SIGSEND,		//Send a signal to a process
	 
	  SYS_SIGRETURN,	//Cleanup after completion of a signal
	 
	  SYS_IRQCTL,		//Enable, disable, or configure interrupt
	 
	  SYS_DEVIO,		//Read from or write to an I/O port
	 
	  SYS_SDEVIO, 		//Read or write string from/to I/O port
	 
	  SYS_VDEVIO, 		//Carry out a vector of I/O requests
	 
	  SYS_INT86,		//Do a real-mode BIOS call
	 
	  SYS_NEWMAP,		//Set up a process memory map
	 
	  SYS_SEGCTL,		//Add segment and get selector (far data access)
	 
	  SYS_MEMSET,		//Write char to memory area
	 
	  SYS_UMAP,		//Convert virtual address to physical address
	 
	  SYS_VIRCOPY,		//Copy using pure virtual addressing
	 
	  SYS_PHYSCOPY,	//Copy using physical addressing
	 
	  SYS_VIRVCOPY,	//Vector of VCOPY requests
	 
	  SYS_PHYSVCOPY,	//Vector of PHYSCOPY requests
	 
	  SYS_TIMES,		//Get uptime and process times
	 
	  SYS_SETALARM,	//Schedule a synchronous alarm
	 
	  SYS_ABORT,		//Panic: OS is unable to continue
	 
	  SYS_GETINFO,		//Request system information
	  
	  SYS_TSL			//Test and set latch instruction, 
	  					//this ensure that the code about to be executed will not be interrupted
}
