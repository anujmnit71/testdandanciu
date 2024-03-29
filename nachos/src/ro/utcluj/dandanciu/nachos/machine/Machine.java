package ro.utcluj.dandanciu.nachos.machine;

// Machine.java
//	Class for simulating the execution of user programs.
//	running on top of Nachos.
//
//	User programs are loaded into "mainMemory"; to Nachos,
//	this looks just like an array of bytes.  Of course, the Nachos
//	kernel is in memory too -- but as in most machines these days,
//	the kernel is loaded into a separate memory region from user
//	programs, and accesses to kernel memory are not translated or paged.
//
//	In Nachos, user programs are executed one instruction at a time, 
//	by the simulator.  Each memory reference is translated, checked
//	for errors, etc.
//
//  DO NOT CHANGE -- part of the machine emulation
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.


import java.io.IOException;

import ro.utcluj.dandanciu.nachos.machine.exceptions.MachineException;
import ro.utcluj.dandanciu.nachos.threads.*;

// The following class defines the simulated host workstation hardware, as 
// seen by user programs -- the CPU registers, main memory, etc.
// User programs shouldn't be able to tell that they are running on our 
// simulator or on the real hardware, except 
//	we don't support floating point instructions
//	the system call interface to Nachos is not the same as UNIX 
//	  (10 system calls in Nachos vs. 200 in UNIX!)
// If we were to implement more of the UNIX system calls, we ought to be
// able to run Nachos on top of Nachos!

public class Machine {

	private static final long LOW32BITS = 0x00000000ffffffffL;

	// this constant is used when we're doing calculations on unsigned
	// 32-bit quantities. To do this in Java, we cast them to a 64-bit
	// long, which sign-extends, and then mask out the top bits (the
	// sign-extension, if applicable). The above constant is the
	// bitmask used.

	private static final boolean USE_TLB = false;

	// Definitions related to the size, and format of user memory

	public static final int PageSize = 128; // SectorSize

	// set the page size equal to
	// the disk sector size, for
	// simplicity

	public static final int NumPhysPages = 128; /* 32; */

	static final int MemorySize = NumPhysPages * PageSize;

	static final int TLBSize = 4; // if there is a TLB, make it small

	// Textual names of the exceptions that can be generated by user program
	// execution, for debugging.

	public static final int NoException = 0;

	// Everything ok!
	public static final int SyscallException = 1;

	// A program executed a system call.
	public static final int PageFaultException = 2;

	// No valid translation found
	public static final int ReadOnlyException = 3;

	// Write attempted to page marked "read-only"
	public static final int BusErrorException = 4;

	// Translation resulted in an invalid physical address
	public static final int AddressErrorException = 5;

	// Unaligned reference or one that was beyond the end of the address space
	public static final int OverflowException = 6;

	// Integer overflow in add or sub.
	public static final int IllegalInstrException = 7;

	// Unimplemented or reserved instr.
	public static final int NumExceptionTypes = 8;

	static String exceptionNames[] = { "no exception", "syscall",
			"page fault/no TLB entry", "page read only", "bus error",
			"address error", "overflow", "illegal instruction" };

	// User program CPU state. The full set of MIPS registers, plus a few
	// more because we need to be able to start/stop a user program between
	// any two instructions (thus we need to keep track of things like load
	// delay slots, etc.)

	public static final int StackReg = 29; // User's stack pointer

	public static final int RetAddrReg = 31;

	// Holds return address for procedure calls
	public static final int NumGPRegs = 32;

	// 32 general purpose registers on MIPS
	public static final int HiReg = 32;

	// Double register to hold multiply result
	public static final int LoReg = 33;

	public static final int PCReg = 34; // Current program counter

	public static final int NextPCReg = 35;

	// Next program counter (for branch delay)
	public static final int PrevPCReg = 36;

	// Previous program counter (for debugging)
	public static final int LoadReg = 37;

	// The register target of a delayed load.
	public static final int LoadValueReg = 38;

	// The value to be loaded by a delayed load.
	public static final int BadVAddrReg = 39;

	// The failing virtual address on an exception

	public static final int NumTotalRegs = 40;

	public static final int SIGN_BIT = 0x80000000;

	public static final int R31 = 31;

	// Data structures -- all of these are accessible to Nachos kernel code.
	// "public" for convenience.
	//
	// Note that *all* communication between the user program and the kernel
	// are in terms of these data structures.

	public static byte mainMemory[] = new byte[MemorySize];

	// physical memory to store user program,
	// code and data, while executing

	public static int registers[] = new int[NumTotalRegs];

	// CPU registers, for executing user programs

	static int mresult[] = new int[2];

	// NOTE: the hardware translation of virtual addresses in the user program
	// to physical addresses (relative to the beginning of "mainMemory")
	// can be controlled by one of:
	// a traditional linear page table
	// a software-loaded translation lookaside buffer (tlb) -- a cache of
	// mappings of virtual page #'s to physical page #'s
	//
	// If "tlb" is NULL, the linear page table is used
	// If "tlb" is non-NULL, the Nachos kernel is responsible for managing
	// the contents of the TLB. But the kernel can use any data structure
	// it wants (eg, segmented paging) for handling TLB cache misses.
	// 
	// For simplicity, both the page table pointer and the TLB pointer are
	// public. However, while there can be multiple page tables (one per address
	// space, stored in memory), there is only one TLB (implemented in
	// hardware).
	// Thus the TLB pointer should be considered as *read-only*, although
	// the contents of the TLB are free to be modified by the kernel software.

	static TranslationEntry tlb[]; // should be considered

	// "read-only" to Nachos kernel code

	public static TranslationEntry pageTable[];

	public static int pageTableSize;

	private static boolean singleStep;

	// drop back into the debugger after each
	// simulated instruction
	private static int runUntilTime;
	// drop back into the debugger when simulated
	// time reaches this value

	// ----------------------------------------------------------------------
	// static Machine construtor
	// Initialize the simulation of user program execution.
	//
	// "debug" -- if TRUE, drop into the debugger after each user instruction
	// is executed.
	// ----------------------------------------------------------------------

	static {
		int i;

		for (i = 0; i < NumTotalRegs; i++)
			registers[i] = 0;

		for (i = 0; i < MemorySize; i++)
			mainMemory[i] = 0;

		if (USE_TLB) {
			tlb = new TranslationEntry[TLBSize];
			for (i = 0; i < TLBSize; i++) {
				tlb[i] = new TranslationEntry();
				tlb[i].valid = false;
			}
			pageTable = null;
		} else { // use linear page table
			tlb = null;
			pageTable = null;
		}

		singleStep = false;
	}

	// Turns on user-program debugging, which is off by default.
	public static void enableDebugging() {
		singleStep = true;
	}

	// ----------------------------------------------------------------------
	// run
	// Simulate the execution of a user-level program on Nachos.
	// Called by the kernel when the program starts up; never returns.
	//
	// This routine is re-entrant, in that it can be called multiple
	// times concurrently -- one for each thread executing user code.
	// ----------------------------------------------------------------------

	public static void run() {
		Instruction instr = new Instruction(); // storage for decoded
												// instruction

		Debug.println('m', "Starting user thread "
				+ Thread.currentThread().getName() + "at time "
				+ Nachos.stats.totalTicks);

		Interrupt.setStatus(Interrupt.UserMode);
		for (;;) {
			oneInstruction(instr);
			Interrupt.oneTick();
			if (singleStep && (runUntilTime <= Nachos.stats.totalTicks))
				debugger();
		}
	}

	// ----------------------------------------------------------------------
	// raiseException
	// Transfer control to the Nachos kernel from user mode, because
	// the user program either invoked a system call, or some exception
	// occured (such as the address translation failed).
	//
	// "which" -- the cause of the kernel trap
	// "badVaddr" -- the virtual address causing the trap, if appropriate
	// ----------------------------------------------------------------------
	public static void raiseException(int which, int badVAddr) {
		Debug.println('m', "Exception: " + exceptionNames[which]);

		Debug.ASSERT(Interrupt.getStatus() == Interrupt.UserMode);
		registers[BadVAddrReg] = badVAddr;
		delayedLoad(0, 0); // finish anything in progress
		Interrupt.setStatus(Interrupt.SystemMode);
		Nachos.exceptionHandler(which); // interrupts are enabled at this point
		Interrupt.setStatus(Interrupt.UserMode);
	}

	// ----------------------------------------------------------------------
	// debugger
	// Primitive debugger for user programs. Note that we can't use
	// gdb to debug user programs, since gdb doesn't run on top of Nachos.
	// It could, but you'd have to implement *a lot* more system calls
	// to get it to work!
	//
	// So just allow single-stepping, and printing the contents of memory.
	// ----------------------------------------------------------------------

	public static void debugger() {
		// Debug.println('m', "Debugger not implemented");

		char inputChar; // char read from input stream

		Interrupt.dumpState();
		dumpState();
		System.out.print(Nachos.stats.totalTicks + "> ");
		System.out.flush();

		// get user input, character at a time.

		try {
			inputChar = (char) (System.in.read());
		} catch (IOException _) {
			inputChar = 'x';
		}

		if (inputChar == 'c')
			singleStep = false;
		else if (inputChar == '?') {
			System.out.println("Machine commands:");
			System.out.println("    <return>  execute one instruction");
			System.out.println("    <number>  run until the given timer tick");
			System.out.println("    c         run until completion");
			System.out.println("    ?         print this help message");
		} else if (Character.isDigit(inputChar)) {
			int tempNum = 0;

			while (Character.isDigit(inputChar)) {
				tempNum *= 10;
				tempNum += Character.digit(inputChar, 10);
				try {
					inputChar = (char) (System.in.read());
				} catch (IOException _) {
					inputChar = 'x';
					break;
				}
				runUntilTime = tempNum;
			}
		}

	}

	// ----------------------------------------------------------------------
	// dumpState
	// Print the user program's CPU state. We might print the contents
	// of memory, but that seemed like overkill.
	// ----------------------------------------------------------------------

	public static void dumpState() {
		int i;
		long l; // so we don't get negative hex numbers. <rrrrrr...>

		System.out.println("Machine registers:\n");
		for (i = 0; i < NumGPRegs; i++) {
			l = registers[i] & LOW32BITS;
			switch (i) {
			case StackReg:
				System.out.print("\tSP(" + i + "):\t0x" + Long.toString(l, 16));
				if ((i % 4) == 3)
					System.out.println();
				break;
			case RetAddrReg:
				System.out.print("\tRA(" + i + "):\t0x" + Long.toString(l, 16));
				if ((i % 4) == 3)
					System.out.println();
				break;
			default:
				System.out.print("\t" + i + ":\t0x" + Long.toString(l, 16));
				if ((i % 4) == 3)
					System.out.println();
				break;
			}
		}

		l = registers[HiReg] & LOW32BITS;
		System.out.print("\tHi:\t0x" + Long.toString(l, 16));
		l = registers[LoReg] & LOW32BITS;
		System.out.println("\tLo:\t0x" + Long.toString(l, 16));
		l = registers[PCReg] & LOW32BITS;
		System.out.print("\tPC:\t0x" + Long.toString(l, 16));
		l = registers[NextPCReg] & LOW32BITS;
		System.out.print("\tNextPC:\t0x" + Long.toString(l, 16));
		l = registers[PrevPCReg] & LOW32BITS;
		System.out.println("\tPrevPC:\t0x" + Long.toString(l, 16));
		l = registers[LoadReg] & LOW32BITS;
		System.out.print("\tLoad:\t0x" + Long.toString(l, 16));
		l = registers[LoadValueReg] & LOW32BITS;
		System.out.println("\tLoadV:\t0x" + Long.toString(l, 16));
		System.out.println();
	}

	// ----------------------------------------------------------------------
	// Machine::ReadRegister/WriteRegister
	// Fetch or write the contents of a user program register.
	// ----------------------------------------------------------------------

	static public int readRegister(int num) {
		return registers[num];
	}

	public static void writeRegister(int num, int value) {
		// DEBUG('m', "WriteRegister %d, value %d\n", num, value);
		registers[num] = value;
	}

	// ----------------------------------------------------------------------
	// readMem
	// Read and return "size" (1, 2, or 4) bytes of virtual memory
	// at "addr"
	//
	// throws "MachineException" if the translation step
	// from virtual to physical memory failed.
	//
	// "addr" -- the virtual address to read from
	// "size" -- the number of bytes to read (1, 2, or 4)
	// ----------------------------------------------------------------------

	static int readMem(int addr, int size) throws MachineException {
		int data = 0;
		int physicalAddress;

		if (Debug.isEnabled('a')) {
			Debug.printf('a', "Reading VA 0x%x, size %d\n", new Integer(addr),
					new Integer(size));
		}

		try {
			physicalAddress = translate(addr, size, false);
		} catch (MachineException e) {
			raiseException(e.getException(), addr);
			throw e;
		}

		switch (size) {
		case 1:
			data = (mainMemory[physicalAddress] & 0xff);
			break;

		case 2:
			data = ((mainMemory[physicalAddress] & 0xff) << 8)
					| (mainMemory[physicalAddress + 1] & 0xff);
			break;

		case 4:
			data = (mainMemory[physicalAddress + 3] << 24)
					| ((mainMemory[physicalAddress + 2] & 0xff) << 16)
					| ((mainMemory[physicalAddress + 1] & 0xff) << 8)
					| (mainMemory[physicalAddress] & 0xff);
			break;

		default:
			Debug.ASSERT(false);
		}

		if (Debug.isEnabled('a')) {
			Debug.printf('a', "\tvalue read = 0x%x\n", new Long(data
					& LOW32BITS));
		}

		return data;
	}

	// ----------------------------------------------------------------------
	// writeMem
	// Write "size" (1, 2, or 4) bytes of the contents of "value" into
	// virtual memory at location "addr".
	//
	// Returns FALSE if the translation step from virtual to physical memory
	// failed.
	//
	// "addr" -- the virtual address to write to
	// "size" -- the number of bytes to be written (1, 2, or 4)
	// "value" -- the data to be written
	// ----------------------------------------------------------------------

	static boolean writeMem(int addr, int size, int value) {
		int physicalAddress;

		if (Debug.isEnabled('a')) {
			Debug.printf('a', "Writing VA 0x%x, size %d, value 0x%x\n",
					new Integer(addr), new Integer(size), new Integer(value));
		}

		try {
			physicalAddress = translate(addr, size, true);
		} catch (MachineException e) {
			raiseException(e.getException(), addr);
			return false;
		}

		switch (size) {
		case 1:
			mainMemory[physicalAddress] = (byte) (value & 0xff);
			break;

		case 2:
			mainMemory[physicalAddress] = (byte) ((value >> 8) & 0xff);
			mainMemory[physicalAddress + 1] = (byte) (value & 0xff);
			break;

		case 4:
			mainMemory[physicalAddress + 3] = (byte) ((value >> 24) & 0xff);
			mainMemory[physicalAddress + 2] = (byte) ((value >> 16) & 0xff);
			mainMemory[physicalAddress + 1] = (byte) ((value >> 8) & 0xff);
			mainMemory[physicalAddress] = (byte) (value & 0xff);
			break;

		default:
			Debug.ASSERT(false);
		}

		return true;
	}

	// ----------------------------------------------------------------------
	// translate
	// Translate a virtual address into a physical address, using
	// either a page table or a TLB. Check for alignment and all sorts
	// of other errors, and if everything is ok, set the use/dirty bits in
	// the translation table entry, and store the translated physical
	// address in "physAddr". If there was an error, returns the type
	// of the exception.
	//
	// "virtAddr" -- the virtual address to translate
	// "physAddr" -- the place to store the physical address
	// "size" -- the amount of memory being read or written
	// "writing" -- if TRUE, check the "read-only" bit in the TLB
	// ----------------------------------------------------------------------

	static private int translate(int virtAddr, int size, boolean writing)
			throws MachineException {
		int i = 0;
		long vpn, offset;
		TranslationEntry entry;
		long pageFrame;
		int physAddr;

		if (Debug.isEnabled('a')) {
			if (writing)
				Debug.printf('a', "\tTranslate 0x%x, %s: ", new Integer(
						virtAddr), "write");
			else
				Debug.printf('a', "\tTranslate 0x%x, %s: ", new Integer(
						virtAddr), "read");
		}

		// check for alignment errors
		if (((size == 4) && (virtAddr & 0x3) != 0)
				|| ((size == 2) && (virtAddr & 0x1) != 0)) {
			Debug.println('a', "alignment problem at " + virtAddr + ", size "
					+ size);
			throw new MachineException(exceptionNames[AddressErrorException],
					AddressErrorException);
		}

		// we must have either a TLB or a page table, but not both!
		Debug.ASSERT(tlb == null || pageTable == null);
		Debug.ASSERT(tlb != null || pageTable != null);

		// calculate the virtual page number, and offset within the page,
		// from the virtual address
		vpn = ((long) virtAddr & LOW32BITS) / PageSize;
		offset = ((long) virtAddr & LOW32BITS) % PageSize;

		if (tlb == null) { // => page table => vpn is index into table
			if (vpn >= pageTableSize) {
				Debug.println('a', "virtual page # " + virtAddr
						+ " too large for page table size " + pageTableSize);
				throw new MachineException(
						exceptionNames[AddressErrorException],
						AddressErrorException);
			} else if (!pageTable[(int) vpn].valid) {
				Debug.println('a', "virtual page # " + virtAddr
						+ " too large for page table size " + pageTableSize);
				throw new MachineException(exceptionNames[PageFaultException],
						PageFaultException);
			}
			entry = pageTable[(int) vpn];
		} else {
			for (entry = null, i = 0; i < TLBSize; i++)
				if (tlb[i].valid && (tlb[i].virtualPage == vpn)) {
					entry = tlb[i]; // FOUND!
					break;
				}
			if (entry == null) { // not found
				Debug.println('a',
						"** no valid TLB entry found for this virtual page!");

				// really, this is a TLB fault,
				// the page may be in memory,
				// but not in the TLB
				throw new MachineException(exceptionNames[PageFaultException],
						PageFaultException);
			}
		}

		if (entry.readOnly && writing) { // trying to write to a read-only
											// page
			Debug.println('a', virtAddr + " mapped read-only at " + i
					+ " in TLB!");
			throw new MachineException(exceptionNames[ReadOnlyException],
					ReadOnlyException);
		}
		pageFrame = entry.physicalPage;

		// if the pageFrame is too big, there is something really wrong!
		// An invalid translation was loaded into the page table or TLB.
		if (pageFrame >= NumPhysPages) {
			Debug.println('a', "*** frame " + pageFrame + " > " + NumPhysPages);
			throw new MachineException(exceptionNames[BusErrorException],
					BusErrorException);
		}
		entry.use = true; // set the use, dirty bits
		if (writing)
			entry.dirty = true;
		physAddr = (int) (pageFrame * PageSize + offset);

		Debug.ASSERT((physAddr >= 0) && ((physAddr + size) <= MemorySize));
		if (Debug.isEnabled('a')) {
			Debug.printf('a', "phys addr = 0x%x\n", new Integer(physAddr));
		}

		return physAddr;
	}

	/**
	 * Execute one instruction from a user-level program
	 *
	 * If there is any kind of exception or interrupt, we invoke the
	 * exception handler, and when it returns, we return to Run(), which
	 * will re-invoke us in a loop. This allows us to
	 * re-start the instruction execution from the beginning, in
	 * case any of our state has changed. On a syscall,
	 * the OS software must increment the PC so execution begins
	 * at the instruction immediately after the syscall.
	
	 * This routine is re-entrant, in that it can be called multiple
	 * times concurrently -- one for each thread executing user code.
	 * We get re-entrancy by never caching any data -- we always re-start the
	 * simulation from scratch each time we are called (or after trapping
	 * back to the Nachos kernel on an exception or interrupt), and we always
	 * store all data back to the machine registers and memory before
	 * leaving. This allows the Nachos kernel to control our behavior
	 * by controlling the contents of memory, the translation table,
	 * and the register set.
	 */
	static private void oneInstruction(Instruction instr) {
		int nextLoadReg = 0;
		int nextLoadValue = 0; // record delayed load operation, to apply
		// in the future

		// Fetch instruction
		try {
			instr.value = readMem(registers[PCReg], 4);
		} catch (MachineException e) {
			return; // exception occurred
		}
		instr.decode();

		String str = Instruction.opStrings[instr.opCode];
		byte args[] = Instruction.opRegs[instr.opCode];
		Debug.ASSERT(instr.opCode <= Instruction.MaxOpcode);
		if (Debug.isEnabled('m')) {
			Debug.printf('m', "At PC = 0x%x: ", new Integer(registers[PCReg]));
			Debug.printf('m', str + "\n",
					new Integer(instr.typeToReg(args[0])), new Integer(instr
							.typeToReg(args[1])), new Integer(instr
							.typeToReg(args[2])));
		}

		// Compute next pc, but don't install in case there's an error or
		// branch.
		int pcAfter = registers[NextPCReg] + 4;
		int sum, diff, tmp, value;
		long rs, rt, imm;

		// Execute the instruction (cf. Kane's book)
		switch (instr.opCode) {

		case Instruction.OP_ADD:
			sum = registers[instr.rs] + registers[instr.rt];
			if (((registers[instr.rs] ^ registers[instr.rt]) & SIGN_BIT) == 0
					&& ((registers[instr.rs] ^ sum) & SIGN_BIT) != 0) {
				raiseException(OverflowException, 0);
				return;
			}
			registers[instr.rd] = sum;
			break;

		case Instruction.OP_ADDI:
			sum = registers[instr.rs] + instr.extra;
			if (((registers[instr.rs] ^ instr.extra) & SIGN_BIT) == 0
					&& ((instr.extra ^ sum) & SIGN_BIT) != 0) {
				raiseException(OverflowException, 0);
				return;
			}
			registers[instr.rt] = sum;
			break;

		case Instruction.OP_ADDIU:
			// the registers are int, so we have to do them unsigned.
			// Now, precisely *WHY* didn't java give us unsigned types, again?

			rs = registers[instr.rs] & LOW32BITS;
			imm = instr.extra & LOW32BITS;
			rt = rs + imm;

			registers[instr.rt] = (int) rt;
			break;

		case Instruction.OP_ADDU:

			rs = registers[instr.rs] & LOW32BITS;
			rt = registers[instr.rt] & LOW32BITS;

			registers[instr.rd] = (int) (rs + rt);
			break;

		case Instruction.OP_AND:
			registers[instr.rd] = registers[instr.rs] & registers[instr.rt];
			break;

		case Instruction.OP_ANDI:
			registers[instr.rt] = registers[instr.rs] & (instr.extra & 0xffff);
			break;

		case Instruction.OP_BEQ:
			if (registers[instr.rs] == registers[instr.rt])
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_BGEZAL:
			registers[R31] = registers[NextPCReg] + 4;
		case Instruction.OP_BGEZ:
			if ((registers[instr.rs] & SIGN_BIT) == 0)
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_BGTZ:
			if (registers[instr.rs] > 0)
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_BLEZ:
			if (registers[instr.rs] <= 0)
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_BLTZAL:
			registers[R31] = registers[NextPCReg] + 4;
		case Instruction.OP_BLTZ:
			if ((registers[instr.rs] & SIGN_BIT) != 0)
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_BNE:
			if (registers[instr.rs] != registers[instr.rt])
				pcAfter = registers[NextPCReg] + (instr.extra << 2);
			break;

		case Instruction.OP_DIV:
			if (registers[instr.rt] == 0) {
				registers[LoReg] = 0;
				registers[HiReg] = 0;
			} else {
				registers[LoReg] = registers[instr.rs] / registers[instr.rt];
				registers[HiReg] = registers[instr.rs] % registers[instr.rt];
			}
			break;

		case Instruction.OP_DIVU:
			// don't sign extend
			rs = (registers[instr.rs] & LOW32BITS);
			rt = (registers[instr.rt] & LOW32BITS);
			if (rt == 0) {
				registers[LoReg] = 0;
				registers[HiReg] = 0;
			} else {
				tmp = (int) (rs / rt);
				registers[LoReg] = tmp;
				tmp = (int) (rs % rt);
				registers[HiReg] = tmp;
			}
			break;

		case Instruction.OP_JAL:
			registers[R31] = registers[NextPCReg] + 4;
		case Instruction.OP_J:
			pcAfter = (pcAfter & 0xf0000000) | instr.extra << 2;
			break;

		case Instruction.OP_JALR:
			registers[instr.rd] = registers[NextPCReg] + 4;
		case Instruction.OP_JR:
			pcAfter = registers[instr.rs];
			break;

		case Instruction.OP_LB:
		case Instruction.OP_LBU:
			tmp = registers[instr.rs] + instr.extra;
			try {
				value = readMem(tmp, 1);
			} catch (MachineException e) {
				return; // exception occurred
			}

			if ((value & 0x80) != 0 && (instr.opCode == Instruction.OP_LB))
				value |= 0xffffff00;
			else
				value &= 0xff;
			nextLoadReg = instr.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LH:
		case Instruction.OP_LHU:
			tmp = registers[instr.rs] + instr.extra;
			if ((tmp & 0x1) != 0) {
				raiseException(AddressErrorException, tmp);
				return;
			}
			try {
				value = readMem(tmp, 2);
			} catch (MachineException e) {
				return; // exception occurred
			}

			if ((value & 0x8000) != 0 && (instr.opCode == Instruction.OP_LH))
				value |= 0xffff0000;
			else
				value &= 0xffff;
			nextLoadReg = instr.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LUI:

			if (Debug.isEnabled('m'))
				Debug.printf('m', "Executing: LUI r%d,%d\n", new Integer(
						instr.rt), new Integer(instr.extra));
			registers[instr.rt] = instr.extra << 16;
			break;

		case Instruction.OP_LW:
			tmp = registers[instr.rs] + instr.extra;
			if ((tmp & 0x3) != 0) {
				raiseException(AddressErrorException, tmp);
				return;
			}
			try {
				value = readMem(tmp, 4);
			} catch (MachineException e) {
				return; // exception occurred
			}
			nextLoadReg = instr.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LWL:
			tmp = registers[instr.rs] + instr.extra;

			// ReadMem assumes all 4 byte requests are aligned on an even
			// word boundary. Also, the little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			Debug.ASSERT((tmp & 0x3) == 0);

			try {
				value = readMem(tmp, 4);
			} catch (MachineException e) {
				return; // exception occurred
			}
			if (registers[LoadReg] == instr.rt)
				nextLoadValue = registers[LoadValueReg];
			else
				nextLoadValue = registers[instr.rt];
			switch (tmp & 0x3) {
			case 0:
				nextLoadValue = value;
				break;
			case 1:
				nextLoadValue = (nextLoadValue & 0xff) | (value << 8);
				break;
			case 2:
				nextLoadValue = (nextLoadValue & 0xffff) | (value << 16);
				break;
			case 3:
				nextLoadValue = (nextLoadValue & 0xffffff) | (value << 24);
				break;
			}
			nextLoadReg = instr.rt;
			break;

		case Instruction.OP_LWR:
			tmp = registers[instr.rs] + instr.extra;

			// ReadMem assumes all 4 byte requests are aligned on an even
			// word boundary. Also, the little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			Debug.ASSERT((tmp & 0x3) == 0);

			try {
				value = readMem(tmp, 4);
			} catch (MachineException e) {
				return; // exception occurred
			}
			if (registers[LoadReg] == instr.rt)
				nextLoadValue = registers[LoadValueReg];
			else
				nextLoadValue = registers[instr.rt];
			switch (tmp & 0x3) {
			case 0:
				nextLoadValue = (nextLoadValue & 0xffffff00)
						| ((value >> 24) & 0xff);
				break;
			case 1:
				nextLoadValue = (nextLoadValue & 0xffff0000)
						| ((value >> 16) & 0xffff);
				break;
			case 2:
				nextLoadValue = (nextLoadValue & 0xff000000)
						| ((value >> 8) & 0xffffff);
				break;
			case 3:
				nextLoadValue = value;
				break;
			}
			nextLoadReg = instr.rt;
			break;

		case Instruction.OP_MFHI:
			registers[instr.rd] = registers[HiReg];
			break;

		case Instruction.OP_MFLO:
			registers[instr.rd] = registers[LoReg];
			break;

		case Instruction.OP_MTHI:
			registers[HiReg] = registers[instr.rs];
			break;

		case Instruction.OP_MTLO:
			registers[LoReg] = registers[instr.rs];
			break;

		case Instruction.OP_MULT:
			mult(registers[instr.rs], registers[instr.rt], true, mresult);
			registers[HiReg] = mresult[0];
			registers[LoReg] = mresult[1];
			break;

		case Instruction.OP_MULTU:
			mult(registers[instr.rs], registers[instr.rt], false, mresult);
			registers[HiReg] = mresult[0];
			registers[LoReg] = mresult[1];
			break;

		case Instruction.OP_NOR:
			registers[instr.rd] = ~(registers[instr.rs] | registers[instr.rt]);
			break;

		case Instruction.OP_OR:
			registers[instr.rd] = registers[instr.rs] | registers[instr.rt];
			break;

		case Instruction.OP_ORI:
			registers[instr.rt] = registers[instr.rs] | (instr.extra & 0xffff);
			break;

		case Instruction.OP_SB:
			if (!writeMem((registers[instr.rs] + instr.extra), 1,
					registers[instr.rt]))
				return;
			break;

		case Instruction.OP_SH:
			if (!writeMem((registers[instr.rs] + instr.extra), 2,
					registers[instr.rt]))
				return;
			break;

		case Instruction.OP_SLL:
			registers[instr.rd] = registers[instr.rt] << instr.extra;
			break;

		case Instruction.OP_SLLV:
			registers[instr.rd] = registers[instr.rt] << (registers[instr.rs] & 0x1f);
			break;

		case Instruction.OP_SLT:
			if (registers[instr.rs] < registers[instr.rt])
				registers[instr.rd] = 1;
			else
				registers[instr.rd] = 0;
			break;

		case Instruction.OP_SLTI:
			if (registers[instr.rs] < instr.extra)
				registers[instr.rt] = 1;
			else
				registers[instr.rt] = 0;
			break;

		case Instruction.OP_SLTIU:
			rs = (registers[instr.rs] & LOW32BITS);
			imm = (instr.extra & LOW32BITS);
			if (rs < imm)
				registers[instr.rt] = 1;
			else
				registers[instr.rt] = 0;
			break;

		case Instruction.OP_SLTU:
			rs = registers[instr.rs] & LOW32BITS;
			rt = registers[instr.rt] & LOW32BITS;
			if (rs < rt)
				registers[instr.rd] = 1;
			else
				registers[instr.rd] = 0;
			break;

		case Instruction.OP_SRA:
			registers[instr.rd] = registers[instr.rt] >> instr.extra;
			break;

		case Instruction.OP_SRAV:
			registers[instr.rd] = registers[instr.rt] >> (registers[instr.rs] & 0x1f);
			break;

		case Instruction.OP_SRL:
			tmp = registers[instr.rt];
			tmp >>= instr.extra;
			registers[instr.rd] = tmp;
			break;

		case Instruction.OP_SRLV:
			tmp = registers[instr.rt];
			tmp >>= (registers[instr.rs] & 0x1f);
			registers[instr.rd] = tmp;
			break;

		case Instruction.OP_SUB:
			diff = registers[instr.rs] - registers[instr.rt];
			if (((registers[instr.rs] ^ registers[instr.rt]) & SIGN_BIT) != 0
					&& ((registers[instr.rs] ^ diff) & SIGN_BIT) != 0) {
				raiseException(OverflowException, 0);
				return;
			}
			registers[instr.rd] = diff;
			break;

		case Instruction.OP_SUBU:

			rs = (registers[instr.rs] & LOW32BITS);
			rt = (registers[instr.rt] & LOW32BITS);

			registers[instr.rd] = (int) (rs - rt);
			break;

		case Instruction.OP_SW:
			if (!writeMem((registers[instr.rs] + instr.extra), 4,
					registers[instr.rt]))
				return;
			break;

		case Instruction.OP_SWL:
			tmp = registers[instr.rs] + instr.extra;

			// The little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			Debug.ASSERT((tmp & 0x3) == 0);

			try {
				value = readMem((tmp & ~0x3), 4);
			} catch (MachineException e) {
				return; // exception occurred
			}
			switch (tmp & 0x3) {
			case 0:
				value = registers[instr.rt];
				break;
			case 1:
				value = (value & 0xff000000)
						| ((registers[instr.rt] >> 8) & 0xffffff);
				break;
			case 2:
				value = (value & 0xffff0000)
						| ((registers[instr.rt] >> 16) & 0xffff);
				break;
			case 3:
				value = (value & 0xffffff00)
						| ((registers[instr.rt] >> 24) & 0xff);
				break;
			}
			if (!writeMem((tmp & ~0x3), 4, value))
				return;
			break;

		case Instruction.OP_SWR:
			tmp = registers[instr.rs] + instr.extra;

			// The little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			Debug.ASSERT((tmp & 0x3) == 0);

			try {
				value = readMem((tmp & ~0x3), 4);
			} catch (MachineException e) {
				return; // exception occurred
			}
			switch (tmp & 0x3) {
			case 0:
				value = (value & 0xffffff) | (registers[instr.rt] << 24);
				break;
			case 1:
				value = (value & 0xffff) | (registers[instr.rt] << 16);
				break;
			case 2:
				value = (value & 0xff) | (registers[instr.rt] << 8);
				break;
			case 3:
				value = registers[instr.rt];
				break;
			}
			if (!writeMem((tmp & ~0x3), 4, value))
				return;
			break;

		case Instruction.OP_SYSCALL:
			raiseException(SyscallException, 0);
			return;

		case Instruction.OP_XOR:
			registers[instr.rd] = registers[instr.rs] ^ registers[instr.rt];
			break;

		case Instruction.OP_XORI:
			registers[instr.rt] = registers[instr.rs] ^ (instr.extra & 0xffff);
			break;

		case Instruction.OP_RES:
		case Instruction.OP_UNIMP:
			raiseException(IllegalInstrException, 0);
			return;

		default:
			System.out.println("Bogus opcode, should not happen");
			return;
		}

		// Now we have successfully executed the instruction.

		// Do any delayed load operation
		delayedLoad(nextLoadReg, nextLoadValue);

		// Advance program counters.
		registers[PrevPCReg] = registers[PCReg]; // for debugging, in case we
		// are jumping into lala-land
		registers[PCReg] = registers[NextPCReg];
		registers[NextPCReg] = pcAfter;
	}

	// ----------------------------------------------------------------------
	// delayedLoad
	// Simulate effects of a delayed load.
	//
	// NOTE -- RaiseException/CheckInterrupts must also call DelayedLoad,
	// since any delayed load must get applied before we trap to the kernel.
	// ----------------------------------------------------------------------

	static public void delayedLoad(int nextReg, int nextValue) {
		registers[registers[LoadReg]] = registers[LoadValueReg];
		registers[LoadReg] = nextReg;
		registers[LoadValueReg] = nextValue;
		registers[0] = 0; // and always make sure R0 stays zero.
	}

	// ----------------------------------------------------------------------
	// mult
	// Simulate R2000 multiplication.
	// The int array result[] is overwritten with the
	// double-length result of the multiplication.
	// ----------------------------------------------------------------------

	static private void mult(int a, int b, boolean signedArith, int result[]) {

		long multiplier, multiplicand; // holds the 64-bit versions of a & b
		long answer;

		if ((a == 0) || (b == 0)) {
			result[0] = result[1] = 0;
			return;
		}

		if (signedArith) {
			multiplier = a; // this will sign-extend the 32-bit into 64 bits
			multiplicand = b;
		} else {
			multiplier = a; // sign-extends....
			multiplicand = b;
			multiplier &= LOW32BITS; // ...then chops off the top 32 bits
			multiplicand &= LOW32BITS;
			// so what we've got here is the SAME bit patterns as were in a & b
			// originally, but now interpreted as 32-bit *unsigned* integers.
		}

		answer = multiplier * multiplicand; // do the 64-bit multiplication.

		result[1] = (int) (answer & 0xFFFFFFFFL);
		result[0] = (int) (answer >> 32);
	}
}