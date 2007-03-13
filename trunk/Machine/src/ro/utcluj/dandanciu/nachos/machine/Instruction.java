package ro.utcluj.dandanciu.nachos.machine;

/**
 * The following class defines an instruction, represented in both undecoded
 * binary form decoded to identify operation to do registers to act on any
 * immediate operand value
 */
public class Instruction { 

		/*
		 * OpCode values. The names are straight from the MIPS manual except for the
		 * following special ones:
		 * 
		 * OP_UNIMP - means that this instruction is legal, but hasn't been
		 * implemented in the simulator yet. OP_RES - means that this is a reserved
		 * opcode (it isn't supported by the architecture).
		 */

		public static final int OP_ADD = 1;
		public static final int OP_ADDI = 2;
		public static final int OP_ADDIU = 3;
		public static final int OP_ADDU = 4;
		public static final int OP_AND = 5;
		public static final int OP_ANDI = 6;
		public static final int OP_BEQ = 7;
		public static final int OP_BGEZ = 8;
		public static final int OP_BGEZAL = 9;
		public static final int OP_BGTZ = 10;
		public static final int OP_BLEZ = 11;
		public static final int OP_BLTZ = 12;
		public static final int OP_BLTZAL = 13;
		public static final int OP_BNE = 14;
		public static final int OP_DIV = 16;
		public static final int OP_DIVU = 17;
		public static final int OP_J = 18;
		public static final int OP_JAL = 19;
		public static final int OP_JALR = 20;
		public static final int OP_JR = 21;
		public static final int OP_LB = 22;
		public static final int OP_LBU = 23;
		public static final int OP_LH = 24;
		public static final int OP_LHU = 25;
		public static final int OP_LUI = 26;
		public static final int OP_LW = 27;
		public static final int OP_LWL = 28;
		public static final int OP_LWR = 29;
		public static final int OP_MFHI = 31;
		public static final int OP_MFLO = 32;
		public static final int OP_MTHI = 34;
		public static final int OP_MTLO = 35;
		public static final int OP_MULT = 36;
		public static final int OP_MULTU = 37;
		public static final int OP_NOR = 38;
		public static final int OP_OR = 39;
		public static final int OP_ORI = 40;
		public static final int OP_RFE = 41;
		public static final int OP_SB = 42;
		public static final int OP_SH = 43;
		public static final int OP_SLL = 44;
		public static final int OP_SLLV = 45;
		public static final int OP_SLT = 46;
		public static final int OP_SLTI = 47;
		public static final int OP_SLTIU = 48;
		public static final int OP_SLTU = 49;
		public static final int OP_SRA = 50;
		public static final int OP_SRAV = 51;
		public static final int OP_SRL = 52;
		public static final int OP_SRLV = 53;
		public static final int OP_SUB = 54;
		public static final int OP_SUBU = 55;
		public static final int OP_SW = 56;
		public static final int OP_SWL = 57;
		public static final int OP_SWR = 58;
		public static final int OP_XOR = 59;
		public static final int OP_XORI = 60;
		public static final int OP_SYSCALL = 61;
		public static final int OP_UNIMP = 62;
		public static final int OP_RES = 63;
		public static final int MaxOpcode = 63;

		/*
		 * The table below is used to translate bits 31:26 of the instruction into a
		 * value suitable for the "opCode" field of a MemWord structure, or into a
		 * special value for further decoding.
		 */

		public static final int SPECIAL = 100;
		public static final int BCOND = 101;
		public static final int IFMT = 1;
		public static final int JFMT = 2;
		public static final int RFMT = 3;

		static final byte opTable[][] = { { SPECIAL, RFMT }, { BCOND, IFMT },
				{ OP_J, JFMT }, { OP_JAL, JFMT }, { OP_BEQ, IFMT },
				{ OP_BNE, IFMT }, { OP_BLEZ, IFMT }, { OP_BGTZ, IFMT },
				{ OP_ADDI, IFMT }, { OP_ADDIU, IFMT }, { OP_SLTI, IFMT },
				{ OP_SLTIU, IFMT }, { OP_ANDI, IFMT }, { OP_ORI, IFMT },
				{ OP_XORI, IFMT }, { OP_LUI, IFMT }, { OP_UNIMP, IFMT },
				{ OP_UNIMP, IFMT }, { OP_UNIMP, IFMT }, { OP_UNIMP, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_LB, IFMT }, { OP_LH, IFMT }, { OP_LWL, IFMT },
				{ OP_LW, IFMT }, { OP_LBU, IFMT }, { OP_LHU, IFMT },
				{ OP_LWR, IFMT }, { OP_RES, IFMT }, { OP_SB, IFMT },
				{ OP_SH, IFMT }, { OP_SWL, IFMT }, { OP_SW, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_SWR, IFMT },
				{ OP_RES, IFMT }, { OP_UNIMP, IFMT }, { OP_UNIMP, IFMT },
				{ OP_UNIMP, IFMT }, { OP_UNIMP, IFMT }, { OP_RES, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_UNIMP, IFMT }, { OP_UNIMP, IFMT }, { OP_UNIMP, IFMT },
				{ OP_UNIMP, IFMT }, { OP_RES, IFMT }, { OP_RES, IFMT },
				{ OP_RES, IFMT }, { OP_RES, IFMT } };

		static final int OpCode = 0; /* Translated op code. */

		static final int Format = 1; /* Format type (IFMT or JFMT or RFMT) */

		/*
		 * The table below is used to convert the "funct" field of SPECIAL
		 * instructions into the "opCode" field of a MemWord.
		 */

		static final byte specialTable[] = { OP_SLL, OP_RES, OP_SRL, OP_SRA,
				OP_SLLV, OP_RES, OP_SRLV, OP_SRAV, OP_JR, OP_JALR, OP_RES, OP_RES,
				OP_SYSCALL, OP_UNIMP, OP_RES, OP_RES, OP_MFHI, OP_MTHI, OP_MFLO,
				OP_MTLO, OP_RES, OP_RES, OP_RES, OP_RES, OP_MULT, OP_MULTU, OP_DIV,
				OP_DIVU, OP_RES, OP_RES, OP_RES, OP_RES, OP_ADD, OP_ADDU, OP_SUB,
				OP_SUBU, OP_AND, OP_OR, OP_XOR, OP_NOR, OP_RES, OP_RES, OP_SLT,
				OP_SLTU, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES,
				OP_RES, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES, OP_RES,
				OP_RES, OP_RES, OP_RES, OP_RES, OP_RES };

		// Stuff to help print out each instruction, for debugging
		static final int NONE = 0;

		static final int RS = 1;

		static final int RT = 2;

		static final int RD = 3;

		static final int EXTRA = 4;

		static final String opStrings[] = { "Shouldn't happen", "ADD r%d,r%d,r%d",
				"ADDI r%d,r%d,%d", "ADDIU r%d,r%d,%d", "ADDU r%d,r%d,r%d",
				"AND r%d,r%d,r%d", "ANDI r%d,r%d,%d", "BEQ r%d,r%d,%d",
				"BGEZ r%d,%d", "BGEZAL r%d,%d", "BGTZ r%d,%d", "BLEZ r%d,%d",
				"BLTZ r%d,%d", "BLTZAL r%d,%d", "BNE r%d,r%d,%d",
				"Shouldn't happen", "DIV r%d,r%d", "DIVU r%d,r%d", "J %d",
				"JAL %d", "JALR r%d,r%d", "JR r%d,r%d", "LB r%d,%d(r%d)",
				"LBU r%d,%d(r%d)", "LH r%d,%d(r%d)", "LHU r%d,%d(r%d)",
				"LUI r%d,%d", "LW r%d,%d(r%d)", "LWL r%d,%d(r%d)",
				"LWR r%d,%d(r%d)", "Shouldn't happen", "MFHI r%d", "MFLO r%d",
				"Shouldn't happen", "MTHI r%d", "MTLO r%d", "MULT r%d,r%d",
				"MULTU r%d,r%d", "NOR r%d,r%d,r%d", "OR r%d,r%d,r%d",
				"ORI r%d,r%d,%d", "RFE", "SB r%d,%d(r%d)", "SH r%d,%d(r%d)",
				"SLL r%d,r%d,%d", "SLLV r%d,r%d,r%d", "SLT r%d,r%d,r%d",
				"SLTI r%d,r%d,%d", "SLTIU r%d,r%d,%d", "SLTU r%d,r%d,r%d",
				"SRA r%d,r%d,%d", "SRAV r%d,r%d,r%d", "SRL r%d,r%d,%d",
				"SRLV r%d,r%d,r%d", "SUB r%d,r%d,r%d", "SUBU r%d,r%d,r%d",
				"SW r%d,%d(r%d)", "SWL r%d,%d(r%d)", "SWR r%d,%d(r%d)",
				"XOR r%d,r%d,r%d", "XORI r%d,r%d,%d", "SYSCALL", "Unimplemented",
				"Reserved" };

		static final byte opRegs[][] = { { NONE, NONE, NONE }, { RD, RS, RT },
				{ RT, RS, EXTRA }, { RT, RS, EXTRA }, { RD, RS, RT },
				{ RD, RS, RT }, { RT, RS, EXTRA }, { RS, RT, EXTRA },
				{ RS, EXTRA, NONE }, { RS, EXTRA, NONE }, { RS, EXTRA, NONE },
				{ RS, EXTRA, NONE }, { RS, EXTRA, NONE }, { RS, EXTRA, NONE },
				{ RS, RT, EXTRA }, { NONE, NONE, NONE }, { RS, RT, NONE },
				{ RS, RT, NONE }, { EXTRA, NONE, NONE }, { EXTRA, NONE, NONE },
				{ RD, RS, NONE }, { RD, RS, NONE }, { RT, EXTRA, RS },
				{ RT, EXTRA, RS }, { RT, EXTRA, RS }, { RT, EXTRA, RS },
				{ RT, EXTRA, NONE }, { RT, EXTRA, RS }, { RT, EXTRA, RS },
				{ RT, EXTRA, RS }, { NONE, NONE, NONE }, { RD, NONE, NONE },
				{ RD, NONE, NONE }, { NONE, NONE, NONE }, { RS, NONE, NONE },
				{ RS, NONE, NONE }, { RS, RT, NONE }, { RS, RT, NONE },
				{ RD, RS, RT }, { RD, RS, RT }, { RT, RS, EXTRA },
				{ NONE, NONE, NONE }, { RT, EXTRA, RS }, { RT, EXTRA, RS },
				{ RD, RT, EXTRA }, { RD, RT, RS }, { RD, RS, RT },
				{ RT, RS, EXTRA }, { RT, RS, EXTRA }, { RD, RS, RT },
				{ RD, RT, EXTRA }, { RD, RT, RS }, { RD, RT, EXTRA },
				{ RD, RT, RS }, { RD, RS, RT }, { RD, RS, RT }, { RT, EXTRA, RS },
				{ RT, EXTRA, RS }, { RT, EXTRA, RS }, { RD, RS, RT },
				{ RT, RS, EXTRA }, { NONE, NONE, NONE }, { NONE, NONE, NONE },
				{ NONE, NONE, NONE } };

		// instance variable

		long value; // binary representation of the instruction

		byte opCode; // Type of instruction. This is NOT the same as the

		// opcode field from the instruction: see defs in mips.h
		byte rs, rt, rd; // Three registers from instruction.

		int extra; // Immediate or target or shamt field or offset.

		// Immediates are sign-extended.

		// ----------------------------------------------------------------------
		// decode
		// Decode a MIPS instruction
		// ----------------------------------------------------------------------
		public void decode() {
			byte op[];

			rs = (byte) ((value >> 21) & 0x1f);
			rt = (byte) ((value >> 16) & 0x1f);
			rd = (byte) ((value >> 11) & 0x1f);
			op = opTable[(int) ((value >> 26) & 0x3f)];
			opCode = op[OpCode];
			if (op[Format] == IFMT) {
				extra = (int) value & 0xffff;
				if ((extra & 0x8000) != 0) {
					extra |= 0xffff0000;
				}
			} else if (op[Format] == RFMT) {
				extra = (int) (value >> 6) & 0x1f;
			} else {
				extra = (int) value & 0x3ffffff;
			}
			if (opCode == SPECIAL) {
				opCode = specialTable[(int) value & 0x3f];
			} else if (opCode == BCOND) {
				int i = (int) value & 0x1f0000;

				if (i == 0) {
					opCode = OP_BLTZ;
				} else if (i == 0x10000) {
					opCode = OP_BGEZ;
				} else if (i == 0x100000) {
					opCode = OP_BLTZAL;
				} else if (i == 0x110000) {
					opCode = OP_BGEZAL;
				} else {
					opCode = OP_UNIMP;
				}
			}
		}

		// ----------------------------------------------------------------------
		// typeToReg
		// Retrieve the register value referred to in the instruction.
		// ----------------------------------------------------------------------

		public int typeToReg(int reg) {
			switch (reg) {
			case RS:
				return rs;
			case RT:
				return rt;
			case RD:
				return rd;
			case EXTRA:
				return extra;
			default:
				return -1;
			}
		}

	}
