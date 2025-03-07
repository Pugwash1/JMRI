package jmri.jmrix.can.cbus;

/**
 * CbusConstants.java
 *
 * Constants to represent CBUS protocol
 *
 * @author Andrew Crosland Copyright (C) 2008, 2021
 */
public final class CbusConstants {

    // Class only supplies static methods
    private CbusConstants(){}

    /**
     * Handle used by service mode programmer
     */
    public static final int SERVICE_HANDLE = 255;

    public static final int DEFAULT_STANDARD_ID = 0x7a;
    public static final int DEFAULT_EXTENDED_ID = 0x7a;

    /**
     * Get a String Array of valid CBUS CAN IDs for use with fixed IDs.
     * CBUS dev guide 6b was 120-127, 6c is 100-127.
     * @return list of CAN IDs from 100 to 127
     */
    public static String[] getValidFixedCanIds() {
        final byte MAX = 28;
        String[] result = new String[MAX];
        for (int i = 0; i < MAX; i++) {
            result[i] = String.valueOf(i + 100);
        }
        return result;
    }

    /**
     * Get the Default JMRI CAN ID for new CBUS connections, 126, in String format.
     */
    public static final String DEFAULT_JMRI_CAN_ID_STRING = "126";

    /**
     * CBUS Manufacturer definitions
     * Where the manufacturer already has an NMRA code, this is used
     */
    public static final int MANU_DEV = 13;
    public static final int SPROG_DCC = 44;         // http://www.merg.co.uk
    public static final int MANU_ROCRAIL = 70;      // http://www.rocrail.net
    public static final int MANU_SPECTRUM = 80;     // http://animatedmodeler.com  (Spectrum Engineering)
    public static final int MANU_MERG = 165;        // http://www.merg.co.uk
    public static final int MANU_RME = 248;
    public static final int MANU_SYSPIXIE = 249;
    public static final int MANU_VLCB = 250;

    /**
     * SPROG DCC module types
     * 
     * Must agree with firmware cbusdefs.h include file from MERGDEV repo
     */
    public static final int MTYP_CANPiSPRG3 = 1;    // Pi-SPROG 3 (not v2) firmware on Pi-SPROG One hardware
    public static final int MTYP_CANSPROG3P = 2;    // Sprog 3 Plus, Pi-SPROG 3v2 and Pi-SPROG 3 Plus common firmware
    public static final int MTYP_CANSPROG = 3;
    public static final int MTYP_SBOOST = 4;
    //public static final int Unsupported = 5;
    public static final int MTYP_CANSOLNOID = 8;   // Servo I/O module similar to MERG CANSOLIIO
    public static final int MTYP_CANSERVOIO = 50;   // Servo I/O module similar to MERG CANMIO-SVO
    public static final int MTYP_CANISB = 100;
    public static final int MTYP_CANSOLIO = 101;    // Solenoid I/O module with inputs

    /**
     * CBUS Opcodes
     */
    // Opcodes with no data
    public static final int CBUS_ACK = 0x00;
    public static final int CBUS_NAK = 0x01;
    public static final int CBUS_HLT = 0x02;
    public static final int CBUS_BON = 0x03;
    public static final int CBUS_TOF = 0x04;
    public static final int CBUS_TON = 0x05;
    public static final int CBUS_ESTOP = 0x06;
    public static final int CBUS_ARST = 0x07;
    public static final int CBUS_RTOF = 0x08;
    public static final int CBUS_RTON = 0x09;
    public static final int CBUS_RESTP = 0x0A;

    public static final int CBUS_RSTAT = 0x0C;
    public static final int CBUS_QNN = 0x0D;

    public static final int CBUS_RQNP = 0x10;
    public static final int CBUS_RQMN = 0x11;

    // Opcodes with 1 data
    public static final int CBUS_KLOC = 0x21;
    public static final int CBUS_QLOC = 0x22;
    public static final int CBUS_DKEEP = 0x23;

    public static final int CBUS_DBG1 = 0x30;

    public static final int CBUS_EXTC = 0x3F;

    // Opcodes with 2 data
    public static final int CBUS_RLOC = 0x40;
    public static final int CBUS_QCON = 0x41;
    public static final int CBUS_SNN = 0x42;
    public static final int CBUS_ALOC = 0x43;
    public static final int CBUS_STMOD = 0x44;
    public static final int CBUS_PCON = 0x45;
    public static final int CBUS_KCON = 0x46;
    public static final int CBUS_DSPD = 0x47;
    public static final int CBUS_DFLG = 0x48;
    public static final int CBUS_DFNON = 0x49;
    public static final int CBUS_DFNOF = 0x4A;
    public static final int CBUS_SSTAT = 0x4C;
    public static final int CBUS_NNRSM = 0x4F;
    public static final int CBUS_RQNN = 0x50;
    public static final int CBUS_NNREL = 0x51;
    public static final int CBUS_NNACK = 0x52;
    public static final int CBUS_NNLRN = 0x53;
    public static final int CBUS_NNULN = 0x54;
    public static final int CBUS_NNCLR = 0x55;
    public static final int CBUS_NNEVN = 0x56;
    public static final int CBUS_NERD = 0x57;
    public static final int CBUS_RQEVN = 0x58;
    public static final int CBUS_WRACK = 0x59;
    public static final int CBUS_RQDAT = 0x5A;
    public static final int CBUS_RQDDS = 0x5B;
    public static final int CBUS_BOOTM = 0x5C;
    public static final int CBUS_ENUM = 0x5D;
    public static final int CBUS_NNRST = 0x5E;
    public static final int CBUS_EXTC1 = 0x5F;

    // Opcodes with 3 data
    public static final int CBUS_DFUN = 0x60;
    public static final int CBUS_GLOC = 0x61;
    public static final int CBUS_ERR = 0x63;

    public static final int CBUS_CMDERR = 0x6F;

    public static final int CBUS_EVNLF = 0x70;
    public static final int CBUS_NVRD = 0x71;
    public static final int CBUS_NENRD = 0x72;
    public static final int CBUS_RQNPN = 0x73;
    public static final int CBUS_NUMEV = 0x74;
    public static final int CBUS_CANID = 0x75;

    public static final int CBUS_EXTC2 = 0x7F;

    // Opcodes with 4 data
    public static final int CBUS_RDCC3 = 0x80;
    public static final int CBUS_WCVO = 0x82;
    public static final int CBUS_WCVB = 0x83;
    public static final int CBUS_QCVS = 0x84;
    public static final int CBUS_PCVS = 0x85;

    public static final int CBUS_ACON = 0x90;
    public static final int CBUS_ACOF = 0x91;
    public static final int CBUS_AREQ = 0x92;
    public static final int CBUS_ARON = 0x93;
    public static final int CBUS_AROF = 0x94;
    public static final int CBUS_EVULN = 0x95;
    public static final int CBUS_NVSET = 0x96;
    public static final int CBUS_NVANS = 0x97;
    public static final int CBUS_ASON = 0x98;
    public static final int CBUS_ASOF = 0x99;
    public static final int CBUS_ASRQ = 0x9A;
    public static final int CBUS_PARAN = 0x9B;
    public static final int CBUS_REVAL = 0x9C;
    public static final int CBUS_ARSON = 0x9D;
    public static final int CBUS_ARSOF = 0x9E;
    public static final int CBUS_EXTC3 = 0x9F;

    // OPcodes with 5 data
    public static final int CBUS_RDCC4 = 0xA0;
    public static final int CBUS_WCVS = 0xA2;
    public static final int CBUS_VCVS = 0xA4;

    public static final int CBUS_ACON1 = 0xB0;
    public static final int CBUS_ACOF1 = 0xB1;
    public static final int CBUS_REQEV = 0xB2;
    public static final int CBUS_ARON1 = 0xB3;
    public static final int CBUS_AROF1 = 0xB4;
    public static final int CBUS_NEVAL = 0xB5;
    public static final int CBUS_PNN = 0xB6;
    public static final int CBUS_ASON1 = 0xB8;
    public static final int CBUS_ASOF1 = 0xB9;
    public static final int CBUS_ARSON1 = 0xBD;
    public static final int CBUS_ARSOF1 = 0xBE;
    public static final int CBUS_EXTC4 = 0xBF;

    // Opcodes with 6 data
    public static final int CBUS_RDCC5 = 0xC0;
    public static final int CBUS_WCVOA = 0xC1;
    public static final int CBUS_CABDAT = 0xC2;
    public static final int CBUS_FCLK = 0xCF;

    public static final int CBUS_ACON2 = 0xD0;
    public static final int CBUS_ACOF2 = 0xD1;
    public static final int CBUS_EVLRN = 0xD2;
    public static final int CBUS_EVANS = 0xD3;
    public static final int CBUS_ARON2 = 0xD4;
    public static final int CBUS_AROF2 = 0xD5;

    public static final int CBUS_ASON2 = 0xD8;
    public static final int CBUS_ASOF2 = 0xD9;

    public static final int CBUS_ARSON2 = 0xDD;
    public static final int CBUS_ARSOF2 = 0xDE;
    public static final int CBUS_EXTC5 = 0xDF;

    // Opcodes with 7 data
    public static final int CBUS_RDCC6 = 0xE0;
    public static final int CBUS_PLOC = 0xE1;
    public static final int CBUS_NAME = 0xE2;
    public static final int CBUS_STAT = 0xE3;

    public static final int CBUS_PARAMS = 0xEF;

    public static final int CBUS_ACON3 = 0xF0;
    public static final int CBUS_ACOF3 = 0xF1;
    public static final int CBUS_ENRSP = 0xF2;
    public static final int CBUS_ARON3 = 0xF3;
    public static final int CBUS_AROF3 = 0xF4;
    public static final int CBUS_EVLRNI = 0xF5;
    public static final int CBUS_ACDAT = 0xF6;
    public static final int CBUS_ARDAT = 0xF7;
    public static final int CBUS_ASON3 = 0xF8;
    public static final int CBUS_ASOF3 = 0xF9;
    public static final int CBUS_DDES = 0xFA;
    public static final int CBUS_DDRS = 0xFB;
    public static final int CBUS_DDWS = 0xFC;

    public static final int CBUS_ARSON3 = 0xFD;
    public static final int CBUS_ARSOF3 = 0xFE;
    public static final int CBUS_EXTC6 = 0xFF;

    /**
     * Extended opcodes, currently only used by the bootloader
     * <p>
     * These are all responses from the bootloader
     */
    public static final int CBUS_EXT_BOOT_ERROR = 0x00;
    public static final int CBUS_EXT_BOOT_OK = 0x01;
    public static final int CBUS_EXT_BOOTC = 0x02;
    public static final int CBUS_EXT_BOOT_OUT_OF_RANGE = 0x03;
    public static final int CBUS_EXT_DEVID = 0x05;
    public static final int CBUS_EXT_BOOTID = 0x06;

    /**
     * Bootloader commands
     *
     * These are used in the data payload of bootloader control frames.
     */
    public static final int CBUS_BOOT_NOP = 0x00;
    public static final int CBUS_BOOT_RESET = 0x01;
    public static final int CBUS_BOOT_INIT = 0x02;
    public static final int CBUS_BOOT_CHECK = 0x03;
    public static final int CBUS_BOOT_TEST = 0x04;
    public static final int CBUS_BOOT_DEVID = 0x05;
    public static final int CBUS_BOOT_BOOTID = 0x06;
    public static final int CBUS_BOOT_ENABLES = 0x07;

    /**
     * Bootloader protocol
     *
     * These define options for the bootloader mode
     */
    public static final int CBUS_BOOT_MODE_WRT_UNLCK  = 0x01;   // Unlock write and erase
    public static final int CBUS_BOOT_MODE_ERASE_ONLY = 0x02;   // Erase without write
    public static final int CBUS_BOOT_MODE_AUTO_ERASE = 0x04;   // Enable auto erase before write
    public static final int CBUS_BOOT_MODE_AUTO_INC   = 0x08;   // Enable auto inc the address
    public static final int CBUS_BOOT_MODE_ACK        = 0x10;   // Acknowledge mode

    /**
     * Programming modes
     */
    public static final int CBUS_PROG_DIRECT_BYTE = 0;
    public static final int CBUS_PROG_DIRECT_BIT = 1;
    public static final int CBUS_PROG_PAGED = 2;
    public static final int CBUS_PROG_REGISTER = 3;
    public static final int CBUS_PROG_ADDRESS = 4;
    public static final int CBUS_OPS_BYTE = 5;
    
    /**
     * Error codes returned by CBUS_ERR
     */
    public static final int ERR_LOCO_STACK_FULL = 1;
    public static final int ERR_LOCO_ADDRESS_TAKEN = 2;
    public static final int ERR_SESSION_NOT_PRESENT = 3;
    public static final int ERR_CONSIST_EMPTY = 4;
    public static final int ERR_LOCO_NOT_FOUND = 5;
    public static final int ERR_CAN_BUS_ERROR = 6;
    public static final int ERR_INVALID_REQUEST = 7;
    public static final int ERR_SESSION_CANCELLED = 8;

    /**
     * Status codes for OPC_SSTAT
     */
    public static final int SSTAT_NO_ACK = 1;
    public static final int SSTAT_OVLD = 2;
    public static final int SSTAT_WR_ACK = 3;
    public static final int SSTAT_BUSY = 4;
    public static final int SSTAT_CV_ERROR = 5;

    /**
     * Event types
     */
    public static final int EVENT_ON = 0;
    public static final int EVENT_OFF = 1;
    public static final int EVENT_EITHER = 2;
    public static final int EVENT_NEITHER = 3;

    /**
     * Event directions
     */
    public static final int EVENT_DIR_UNSET = 0;
    public static final int EVENT_DIR_IN = 1;
    public static final int EVENT_DIR_OUT = 2;
    public static final int EVENT_DIR_EITHER = 3;

    /**
     * CBUS Priorities
     */
    public static final int DEFAULT_DYNAMIC_PRIORITY = 2;
    public static final int DEFAULT_MINOR_PRIORITY = 3;

    /**
     * Function bits for group1
     */
    public static final int CBUS_F0 = 16;
    public static final int CBUS_F1 = 1;
    public static final int CBUS_F2 = 2;
    public static final int CBUS_F3 = 4;
    public static final int CBUS_F4 = 8;

    /**
     * Function bits for group2
     */
    public static final int CBUS_F5 = 1;
    public static final int CBUS_F6 = 2;
    public static final int CBUS_F7 = 4;
    public static final int CBUS_F8 = 8;

    /**
     * Function bits for group3
     */
    public static final int CBUS_F9 = 1;
    public static final int CBUS_F10 = 2;
    public static final int CBUS_F11 = 4;
    public static final int CBUS_F12 = 8;

    /**
     * Function bits for group4
     */
    public static final int CBUS_F13 = 1;
    public static final int CBUS_F14 = 2;
    public static final int CBUS_F15 = 4;
    public static final int CBUS_F16 = 8;
    public static final int CBUS_F17 = 0x10;
    public static final int CBUS_F18 = 0x20;
    public static final int CBUS_F19 = 0x40;
    public static final int CBUS_F20 = 0x80;

    /**
     * Function bits for group5
     */
    public static final int CBUS_F21 = 1;
    public static final int CBUS_F22 = 2;
    public static final int CBUS_F23 = 4;
    public static final int CBUS_F24 = 8;
    public static final int CBUS_F25 = 0x10;
    public static final int CBUS_F26 = 0x20;
    public static final int CBUS_F27 = 0x40;
    public static final int CBUS_F28 = 0x80;

    /**
     * Function bits for group6
     */
    public static final int CBUS_F29 = 1;
    public static final int CBUS_F30 = 2;
    public static final int CBUS_F31 = 4;
    public static final int CBUS_F32 = 8;
    public static final int CBUS_F33 = 16;
    public static final int CBUS_F34 = 32;
    public static final int CBUS_F35 = 64;
    public static final int CBUS_F36 = 128;

    static final int[] CBUS_FUNCTION_BITS = new int[]{ CBUS_F0,
        CBUS_F1, CBUS_F2, CBUS_F3, CBUS_F4, CBUS_F5, CBUS_F6, CBUS_F7,
        CBUS_F8, CBUS_F9, CBUS_F10, CBUS_F11, CBUS_F12, CBUS_F13, CBUS_F14,
        CBUS_F15, CBUS_F16, CBUS_F17, CBUS_F18, CBUS_F19, CBUS_F20, CBUS_F21,
        CBUS_F22, CBUS_F23, CBUS_F24, CBUS_F25, CBUS_F26, CBUS_F27, CBUS_F28,
        CBUS_F29, CBUS_F30, CBUS_F31, CBUS_F32, CBUS_F33, CBUS_F34, CBUS_F35, CBUS_F36 };

    /**
     * Throttle modes
     */
    public static final int CBUS_SS_128 = 0;
    public static final int CBUS_SS_14 = 1;
    public static final int CBUS_SS_28_INTERLEAVE = 2;
    public static final int CBUS_SS_28 = 3;

    /**
     * Number of function buttons on a throttle
     */
    public static int MAX_FUNCTIONS = 32; 
    
    /**
     * Number of slots supported by the command station
     */
    public static final int CBUS_MAX_SLOTS = 32;
}
