package ro.utcluj.dandanciu.nachos.machine;

import ro.utcluj.dandanciu.nachos.machine.exceptions.IllegalWordSizeException;
import ro.utcluj.dandanciu.nachos.machine.utils.ConfigOptions;
import ro.utcluj.dandanciu.nachos.machine.utils.Constants;

public class Processor {

	private ProcessorState state;
	
	private LocalApic localApic;

	private int id;

	public Processor(int id) {
		this.id = id;
		state = ProcessorState.IDLE;
		localApic = new LocalApic(id);		
		registers = new Register[ConfigOptions.NoOfRegisters];
		for (int i = 0; i < ConfigOptions.NoOfRegisters; i++) {
			registers[i] = new Register();
		}
	}
	
	public void tick(){
		if (this.getState().equals(ProcessorState.RUNNING)) {
			try {
				this.oneInstruction();
			} catch (PhysicalException e) {
				//TODO: handle this
				e.handle();
			}
		}
		
		
	}

	// User program CPU state. The full set of MIPS registers, plus a few
	// more because we need to be able to start/stop a user program between
	// any two instructions (thus we need to keep track of things like load
	// delay slots, etc.)

	public static final int StackReg = 29; // User's stack pointer

	/**
	 * Holds return address for procedure calls
	 */
	public static final int RetAddrReg = 31;

	private static final int R31 = 31;

	/**
	 * Double register to hold multiply result, High Part
	 */
	public static final int HiReg = 32;

	/**
	 * Double register to hold multiply result, Low Part
	 */
	public static final int LoReg = 33;

	/**
	 * Current program counter
	 */
	public static final int PCReg = 34;

	/**
	 * Next program counter, for branch delay
	 */
	public static final int NextPCReg = 35;

	/**
	 * Previous program counter (for debugging)
	 */
	public static final int PrevPCReg = 36;

	/**
	 * The register target of a delayed load.
	 */
	public static final int LoadReg = 37;

	/**
	 * The value to be loaded by a delayed load.
	 */
	public static final int LoadValueReg = 38;

	/**
	 * The failing virtual address on an exception
	 */
	public static final int BadVAddrReg = 39;

	/**
	 * The failing virtual address on an exception
	 */
	public static final int CauseReg = 40;

	/**
	 * Indicates the number of virtual pages
	 */
	public static int pageTableSize;
	
	// TODO: add pageTable

	/**
	 * The registers of this processor
	 */
	private Register[] registers;

	private Memory cache = new Memory(1, 4, 0.25);

	/**
	 * Execute one instruction from a user-level program
	 * 
	 * If there is any kind of exception or interrupt, we invoke the exception
	 * handler, and when it returns, we return to Run(), which will re-invoke us
	 * in a loop. This allows us to re-start the instruction execution from the
	 * beginning, in case any of our state has changed. On a syscall, the OS
	 * software must increment the PC so execution begins at the instruction
	 * immediately after the syscall.
	 * 
	 * This routine is re-entrant, in that it can be called multiple times
	 * concurrently -- one for each thread executing user code. We get
	 * re-entrancy by never caching any data -- we always re-start the
	 * simulation from scratch each time we are called (or after trapping back
	 * to the Nachos kernel on an exception or interrupt), and we always store
	 * all data back to the machine registers and memory before leaving. This
	 * allows the Nachos kernel to control our behavior by controlling the
	 * contents of memory, the translation table, and the register set.
	 */
	public void oneInstruction() throws PhysicalException {
		int nextLoadReg = 0;
		int nextLoadValue = 0; // record delayed load operation, to apply
		// in the future

		Instruction instruction = null;

		// Fetch instruction
		instruction.value = readMem(registers[PCReg].intValue(), 4);

		instruction.decode();

		// Compute next pc, but don't install in case there's an error or
		// branch.
		int pcAfter = registers[NextPCReg].intValue() + 4;
		int sum, diff, tmp, value;
		long rs, rt, imm;

		// Execute the instruction (cf. Kane's book)
		switch (instruction.opCode) {

		case Instruction.OP_ADD:
			sum = registers[instruction.rs].intValue()
					+ registers[instruction.rt].intValue();
			if (((registers[instruction.rs].intValue() ^ registers[instruction.rt]
					.intValue()) & Constants.SIGN_BIT) == 0
					&& ((registers[instruction.rs].intValue() ^ sum) & Constants.SIGN_BIT) != 0) {
				raiseException(PhysicalException.OVERFLOW_EXCEPTION, 0);
				return;
			}
			registers[instruction.rd].setValue(sum);
			break;

		case Instruction.OP_ADDI:
			sum = registers[instruction.rs].hashCode() + instruction.extra;
			if (((registers[instruction.rs].intValue() ^ instruction.extra) & Constants.SIGN_BIT) == 0
					&& ((instruction.extra ^ sum) & Constants.SIGN_BIT) != 0) {
				raiseException(PhysicalException.OVERFLOW_EXCEPTION, 0);
				return;
			}
			registers[instruction.rt].setValue(sum);
			break;

		case Instruction.OP_ADDIU:
			// the registers are int, so we have to do them unsigned.
			// Now, precisely *WHY* didn't java give us unsigned types, again?

			rs = registers[instruction.rs].intValue() & Constants.LOW32BITS;
			imm = instruction.extra & Constants.LOW32BITS;
			rt = rs + imm;

			registers[instruction.rt].setValue((int) rt);
			break;

		case Instruction.OP_ADDU:

			rs = registers[instruction.rs].intValue() & Constants.LOW32BITS;
			rt = registers[instruction.rt].intValue() & Constants.LOW32BITS;

			registers[instruction.rd].setValue((int) (rs + rt));
			break;

		case Instruction.OP_AND:
			registers[instruction.rd].setValue(registers[instruction.rs]
					.intValue()
					& registers[instruction.rt].intValue());
			break;

		case Instruction.OP_ANDI:
			registers[instruction.rt].setValue(registers[instruction.rs]
					.intValue()
					& (instruction.extra & 0xffff));
			break;

		case Instruction.OP_BEQ:
			if (registers[instruction.rs] == registers[instruction.rt])
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_BGEZAL:
			registers[R31].setValue(registers[NextPCReg].intValue() + 4);
		case Instruction.OP_BGEZ:
			if ((registers[instruction.rs].intValue() & Constants.SIGN_BIT) == 0)
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_BGTZ:
			if (registers[instruction.rs].intValue() > 0)
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_BLEZ:
			if (registers[instruction.rs].intValue() <= 0)
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_BLTZAL:
			registers[R31].setValue(registers[NextPCReg].intValue() + 4);
		case Instruction.OP_BLTZ:
			if ((registers[instruction.rs].intValue() & Constants.SIGN_BIT) != 0)
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_BNE:
			if (registers[instruction.rs] != registers[instruction.rt])
				pcAfter = registers[NextPCReg].intValue()
						+ (instruction.extra << 2);
			break;

		case Instruction.OP_DIV:
			if (registers[instruction.rt].intValue() == 0) {
				registers[LoReg].setValue(0);
				registers[HiReg].setValue(0);
			} else {
				registers[LoReg].setValue(registers[instruction.rs].intValue()
						/ registers[instruction.rt].intValue());
				registers[HiReg].setValue(registers[instruction.rs].intValue()
						% registers[instruction.rt].intValue());
			}
			break;

		case Instruction.OP_DIVU:
			// don't sign extend
			rs = (registers[instruction.rs].intValue() & Constants.LOW32BITS);
			rt = (registers[instruction.rt].intValue() & Constants.LOW32BITS);
			if (rt == 0) {
				registers[LoReg].setValue(0);
				registers[HiReg].setValue(0);
			} else {
				tmp = (int) (rs / rt);
				registers[LoReg].setValue(tmp);
				tmp = (int) (rs % rt);
				registers[HiReg].setValue(tmp);
			}
			break;

		case Instruction.OP_JAL:
			registers[R31].setValue(registers[NextPCReg].intValue() + 4);
		case Instruction.OP_J:
			pcAfter = (pcAfter & 0xf0000000) | instruction.extra << 2;
			break;

		case Instruction.OP_JALR:
			registers[instruction.rd]
					.setValue(registers[NextPCReg].intValue() + 4);
		case Instruction.OP_JR:
			pcAfter = registers[instruction.rs].intValue();
			break;

		case Instruction.OP_LB:
		case Instruction.OP_LBU:
			tmp = registers[instruction.rs].intValue() + instruction.extra;

			value = readMem(tmp, 1);

			if ((value & 0x80) != 0
					&& (instruction.opCode == Instruction.OP_LB))
				value |= 0xffffff00;
			else
				value &= 0xff;
			nextLoadReg = instruction.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LH:
		case Instruction.OP_LHU:
			tmp = registers[instruction.rs].intValue() + instruction.extra;
			if ((tmp & 0x1) != 0) {
				raiseException(PhysicalException.ADDRESS_ERROR_EXCEPTION, tmp);
				return;
			}
			value = readMem(tmp, 2);

			if ((value & 0x8000) != 0
					&& (instruction.opCode == Instruction.OP_LH))
				value |= 0xffff0000;
			else
				value &= 0xffff;
			nextLoadReg = instruction.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LUI:
			registers[instruction.rt].setValue(instruction.extra << 16);
			break;

		case Instruction.OP_LW:
			tmp = registers[instruction.rs].intValue() + instruction.extra;
			if ((tmp & 0x3) != 0) {
				raiseException(PhysicalException.ADDRESS_ERROR_EXCEPTION, tmp);
				return;
			}
			value = readMem(tmp, 4);
			nextLoadReg = instruction.rt;
			nextLoadValue = value;
			break;

		case Instruction.OP_LWL:
			tmp = registers[instruction.rs].intValue() + instruction.extra;

			// ReadMem assumes all 4 byte requests are aligned on an even
			// word boundary. Also, the little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			assert ((tmp & 0x3) == 0);

			value = readMem(tmp, 4);
			if (registers[LoadReg].intValue() == instruction.rt)
				nextLoadValue = registers[LoadValueReg].intValue();
			else
				nextLoadValue = registers[instruction.rt].intValue();
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
			nextLoadReg = instruction.rt;
			break;

		case Instruction.OP_LWR:
			tmp = registers[instruction.rs].intValue() + instruction.extra;

			// ReadMem assumes all 4 byte requests are aligned on an even
			// word boundary. Also, the little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			assert ((tmp & 0x3) == 0);

			value = readMem(tmp, 4);
			if (registers[LoadReg].intValue() == instruction.rt)
				nextLoadValue = registers[LoadValueReg].intValue();
			else
				nextLoadValue = registers[instruction.rt].intValue();
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
			nextLoadReg = instruction.rt;
			break;

		case Instruction.OP_MFHI:
			registers[instruction.rd] = registers[HiReg];
			break;

		case Instruction.OP_MFLO:
			registers[instruction.rd] = registers[LoReg];
			break;

		case Instruction.OP_MTHI:
			registers[HiReg] = registers[instruction.rs];
			break;

		case Instruction.OP_MTLO:
			registers[LoReg] = registers[instruction.rs];
			break;

		case Instruction.OP_MULT:
			int[] mresult = new int[2];			
			mult(registers[instruction.rs].intValue(),
					registers[instruction.rt].intValue(), true, mresult);
			registers[HiReg].setValue(mresult[0]);
			registers[LoReg].setValue(mresult[1]);
			break;

		case Instruction.OP_MULTU:
			int[] mres = new int[2];	
			mult(registers[instruction.rs].intValue(),
					registers[instruction.rt].intValue(), false, mres);
			registers[HiReg].setValue(mres[0]);
			registers[LoReg].setValue(mres[1]);
			break;

		case Instruction.OP_NOR:
			registers[instruction.rd].setValue(~(registers[instruction.rs]
					.intValue() | registers[instruction.rt].intValue()));
			break;

		case Instruction.OP_OR:
			registers[instruction.rd].setValue(registers[instruction.rs]
					.intValue()
					| registers[instruction.rt].intValue());
			break;

		case Instruction.OP_ORI:
			registers[instruction.rt].setValue(registers[instruction.rs]
					.intValue()
					| (instruction.extra & 0xffff));
			break;

		case Instruction.OP_SB:
			if (!writeMem(
					(registers[instruction.rs].intValue() + instruction.extra),
					1, registers[instruction.rt].intValue()))
				return;
			break;

		case Instruction.OP_SH:
			if (!writeMem(
					(registers[instruction.rs].intValue() + instruction.extra),
					2, registers[instruction.rt].intValue()))
				return;
			break;

		case Instruction.OP_SLL:
			registers[instruction.rd].setValue(registers[instruction.rt]
					.intValue() << instruction.extra);
			break;

		case Instruction.OP_SLLV:
			registers[instruction.rd]
					.setValue(registers[instruction.rt].intValue() << (registers[instruction.rs]
							.intValue() & 0x1f));
			break;

		case Instruction.OP_SLT:
			if (registers[instruction.rs].intValue() < registers[instruction.rt]
					.intValue())
				registers[instruction.rd].setValue(1);
			else
				registers[instruction.rd].setValue(0);
			break;

		case Instruction.OP_SLTI:
			if (registers[instruction.rs].intValue() < instruction.extra)
				registers[instruction.rt].setValue(1);
			else
				registers[instruction.rt].setValue(0);
			break;

		case Instruction.OP_SLTIU:
			rs = (registers[instruction.rs].intValue() & Constants.LOW32BITS);
			imm = (instruction.extra & Constants.LOW32BITS);
			if (rs < imm)
				registers[instruction.rt].setValue(1);
			else
				registers[instruction.rt].setValue(0);
			break;

		case Instruction.OP_SLTU:
			rs = registers[instruction.rs].intValue() & Constants.LOW32BITS;
			rt = registers[instruction.rt].intValue() & Constants.LOW32BITS;
			if (rs < rt)
				registers[instruction.rd].setValue(1);
			else
				registers[instruction.rd].setValue(0);
			break;

		case Instruction.OP_SRA:
			registers[instruction.rd].setValue(registers[instruction.rt]
					.intValue() >> instruction.extra);
			break;

		case Instruction.OP_SRAV:
			registers[instruction.rd]
					.setValue(registers[instruction.rt].intValue() >> (registers[instruction.rs]
							.intValue() & 0x1f));
			break;

		case Instruction.OP_SRL:
			tmp = registers[instruction.rt].intValue();
			tmp >>= instruction.extra;
			registers[instruction.rd].setValue(tmp);
			break;

		case Instruction.OP_SRLV:
			tmp = registers[instruction.rt].intValue();
			tmp >>= (registers[instruction.rs].intValue() & 0x1f);
			registers[instruction.rd].setValue(tmp);
			break;

		case Instruction.OP_SUB:
			diff = registers[instruction.rs].intValue()
					- registers[instruction.rt].intValue();
			if (((registers[instruction.rs].intValue() ^ registers[instruction.rt]
					.intValue()) & Constants.SIGN_BIT) != 0
					&& ((registers[instruction.rs].intValue() ^ diff) & Constants.SIGN_BIT) != 0) {
				raiseException(PhysicalException.OVERFLOW_EXCEPTION, 0);
				return;
			}
			registers[instruction.rd].setValue(diff);
			break;

		case Instruction.OP_SUBU:

			rs = (registers[instruction.rs].intValue() & Constants.LOW32BITS);
			rt = (registers[instruction.rt].intValue() & Constants.LOW32BITS);

			registers[instruction.rd].setValue((int) (rs - rt));
			break;

		case Instruction.OP_SW:
			if (!writeMem(
					(registers[instruction.rs].intValue() + instruction.extra),
					4, registers[instruction.rt].intValue()))
				return;
			break;

		case Instruction.OP_SWL:
			tmp = registers[instruction.rs].intValue() + instruction.extra;

			// The little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			assert ((tmp & 0x3) == 0);

			value = readMem((tmp & ~0x3), 4);
			switch (tmp & 0x3) {
			case 0:
				value = registers[instruction.rt].intValue();
				break;
			case 1:
				value = (value & 0xff000000)
						| ((registers[instruction.rt].intValue() >> 8) & 0xffffff);
				break;
			case 2:
				value = (value & 0xffff0000)
						| ((registers[instruction.rt].intValue() >> 16) & 0xffff);
				break;
			case 3:
				value = (value & 0xffffff00)
						| ((registers[instruction.rt].intValue() >> 24) & 0xff);
				break;
			}
			if (!writeMem((tmp & ~0x3), 4, value))
				return;
			break;

		case Instruction.OP_SWR:
			tmp = registers[instruction.rs].intValue() + instruction.extra;

			// The little endian/big endian swap code would
			// fail (I think) if the other cases are ever exercised.
			assert ((tmp & 0x3) == 0);

			value = readMem((tmp & ~0x3), 4);
			switch (tmp & 0x3) {
			case 0:
				value = (value & 0xffffff)
						| (registers[instruction.rt].intValue() << 24);
				break;
			case 1:
				value = (value & 0xffff)
						| (registers[instruction.rt].intValue() << 16);
				break;
			case 2:
				value = (value & 0xff)
						| (registers[instruction.rt].intValue() << 8);
				break;
			case 3:
				value = registers[instruction.rt].intValue();
				break;
			}
			if (!writeMem((tmp & ~0x3), 4, value))
				return;
			break;

		case Instruction.OP_SYSCALL:
			raiseException(PhysicalException.SYSCALL_EXCEPTION, 0);
			return;

		case Instruction.OP_XOR:
			registers[instruction.rd].setValue(registers[instruction.rs]
					.intValue()
					^ registers[instruction.rt].intValue());
			break;

		case Instruction.OP_XORI:
			registers[instruction.rt].setValue(registers[instruction.rs]
					.intValue()
					^ (instruction.extra & 0xffff));
			break;

		case Instruction.OP_RES:
		case Instruction.OP_UNIMP:
			raiseException(PhysicalException.ILLEGAL_INSTRUCTION_EXCEPTION, 0);
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
		registers[NextPCReg].setValue(pcAfter);
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
			multiplier &= Constants.LOW32BITS; // ...then chops off the top 32
			// bits
			multiplicand &= Constants.LOW32BITS;
			// so what we've got here is the SAME bit patterns as were in a & b
			// originally, but now interpreted as 32-bit *unsigned* integers.
		}

		answer = multiplier * multiplicand; // do the 64-bit multiplication.

		result[1] = (int) (answer & 0xFFFFFFFFL);
		result[0] = (int) (answer >> 32);
	}

	/**
	 * delayedLoad Simulate effects of a delayed load.
	 * 
	 * NOTE -- RaiseException/CheckInterrupts must also call DelayedLoad, since
	 * any delayed load must get applied before we trap to the kernel.
	 * 
	 * @param nextReg
	 * @param nextValue
	 */
	private void delayedLoad(int nextReg, int nextValue) {
		registers[registers[LoadReg].intValue()]
				.setValue(registers[LoadValueReg].getValue());
		registers[LoadReg].setValue(nextReg);
		registers[LoadValueReg].setValue(nextValue);
		registers[0].setValue(0); // and always make sure R0 stays zero.
	}

	private void raiseException(PhysicalException exception, int badVAddr)
			throws PhysicalException {
		registers[BadVAddrReg].setValue(badVAddr);
		registers[CauseReg].setValue(exception.getType());
		delayedLoad(0, 0); // finish anything in progress
		throw exception;
	}

	private int readMem(int address, int size) throws PhysicalException {
		Word dataWord = null;
		Word addressWord = null;
		try {
			dataWord = Word.getWordOfSize(size);
			addressWord = Word.getWordOfSize(cache.getAddressWordSize());
			addressWord.setValue(address);
			cache.load(addressWord, dataWord);
			return dataWord.intValue();
		} catch (IllegalWordSizeException e) {
			raiseException(PhysicalException.ADDRESS_ERROR_EXCEPTION, address);
			assert false;
		}
		return 0;
	}

	private boolean writeMem(int address, int size, int value)
			throws PhysicalException {
		Word dataWord = null;
		Word addressWord = null;
		try {
			dataWord = Word.getWordOfSize(size);
			addressWord = Word.getWordOfSize(cache.getAddressWordSize());
			addressWord.setValue(address);
			dataWord.setValue(value);
			cache.store(addressWord, dataWord);
			return true;
		} catch (IllegalWordSizeException e) {
			raiseException(PhysicalException.ADDRESS_ERROR_EXCEPTION, address);
			assert false;
		}
		return false;
	}

	/**
	 * @return the state
	 */
	public ProcessorState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(ProcessorState state) {
		this.state = state;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the localApic
	 */
	public LocalApic getLocalApic() {
		return localApic;
	}

	/**
	 * @param localApic the localApic to set
	 */
	public void setLocalApic(LocalApic localApic) {
		this.localApic = localApic;
	}

}
