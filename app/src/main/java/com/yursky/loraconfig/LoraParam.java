package com.yursky.loraconfig;

class LoraParam {

    boolean saveHard = true;
    int address = 0;
    String parity = PARITY_8N1;
    String uartRate = UART_RATE_9600;
    String airRate = AIR_RATE_2k4;
    int channel = 23;
    boolean fixedMode = false;
    String ioMode = IO_MODE_PUSHPULL;
    String worTiming = WOR_TIMING_250;
    boolean fec = true;
    String power = POWER_30;

    final static String PARITY_8N1 = "00";
    final static String PARITY_8O1 = "01";
    final static String PARITY_8E1 = "10";

    final static String UART_RATE_1200 = "000";
    final static String UART_RATE_2400 = "001";
    final static String UART_RATE_4800 = "010";
    final static String UART_RATE_9600 = "011";
    final static String UART_RATE_19200 = "100";
    final static String UART_RATE_38400 = "101";
    final static String UART_RATE_57600 = "110";
    final static String UART_RATE_115200 = "111";

    final static String AIR_RATE_0k3 = "000";
    final static String AIR_RATE_1k2 = "001";
    final static String AIR_RATE_2k4 = "010";
    final static String AIR_RATE_4k8 = "011";
    final static String AIR_RATE_9k6 = "100";
    final static String AIR_RATE_19k2 = "101";

    final static String IO_MODE_PUSHPULL = "1";
    final static String IO_MODE_OPENDRAIN = "0";

    final static String WOR_TIMING_250 = "000";
    final static String WOR_TIMING_500 = "001";
    final static String WOR_TIMING_750 = "010";
    final static String WOR_TIMING_1000 = "011";
    final static String WOR_TIMING_1250 = "100";
    final static String WOR_TIMING_1500 = "101";
    final static String WOR_TIMING_1750 = "110";
    final static String WOR_TIMING_2000 = "111";

    final static String POWER_21 = "11";
    final static String POWER_24 = "10";
    final static String POWER_27 = "01";
    final static String POWER_30 = "00";

    LoraParam(String data) throws Exception {

        String[] hexs = data.split("_");

        //1 byte
        if (hexs[0].equals("C0")) saveHard = true;
        else if (hexs[0].equals("C2")) saveHard = false;

        //2-3 byte
        address = 255 * Integer.parseInt(hexs[1], 16) + Integer.parseInt(hexs[2], 16);

        //4 byte
        int intHex = Integer.parseInt(hexs[3], 16);
        String binHex = Integer.toBinaryString(0x100 | intHex).substring(1);
        char[] bitArray = binHex.toCharArray();

        String bitInfo = "" + bitArray[0] + bitArray[1];
        if (bitInfo.equals("00")) parity = PARITY_8N1;
        else if (bitInfo.equals("01")) parity = PARITY_8O1;
        else if (bitInfo.equals("10")) parity = PARITY_8E1;

        bitInfo = "" + bitArray[2] + bitArray[3] + bitArray[4];
        if (bitInfo.equals("000")) uartRate = UART_RATE_1200;
        else if (bitInfo.equals("001")) uartRate = UART_RATE_2400;
        else if (bitInfo.equals("010")) uartRate = UART_RATE_4800;
        else if (bitInfo.equals("011")) uartRate = UART_RATE_9600;
        else if (bitInfo.equals("100")) uartRate = UART_RATE_19200;
        else if (bitInfo.equals("101")) uartRate = UART_RATE_38400;
        else if (bitInfo.equals("110")) uartRate = UART_RATE_57600;
        else if (bitInfo.equals("111")) uartRate = UART_RATE_115200;

        bitInfo = "" + bitArray[5] + bitArray[6] + bitArray[7];
        if (bitInfo.equals("000")) airRate = AIR_RATE_0k3;
        else if (bitInfo.equals("001")) airRate = AIR_RATE_1k2;
        else if (bitInfo.equals("010")) airRate = AIR_RATE_2k4;
        else if (bitInfo.equals("011")) airRate = AIR_RATE_4k8;
        else if (bitInfo.equals("100")) airRate = AIR_RATE_9k6;
        else if (bitInfo.equals("101")) airRate = AIR_RATE_19k2;

        //5 byte
        channel = Integer.parseInt(hexs[4], 16);

        //6 byte
        intHex = Integer.parseInt(hexs[5], 16);
        binHex = Integer.toBinaryString(0x100 | intHex).substring(1);
        bitArray = binHex.toCharArray();

        if (bitArray[0] == '0') fixedMode = false;
        else if (bitArray[0] == '1') fixedMode = true;

        if (bitArray[1] == '0') ioMode = IO_MODE_OPENDRAIN;
        else if (bitArray[1] == '1') ioMode = IO_MODE_PUSHPULL;

        bitInfo = "" + bitArray[2] + bitArray[3] + bitArray[4];
        if (bitInfo.equals("000")) worTiming = WOR_TIMING_250;
        else if (bitInfo.equals("001")) worTiming = WOR_TIMING_500;
        else if (bitInfo.equals("010")) worTiming = WOR_TIMING_750;
        else if (bitInfo.equals("011")) worTiming = WOR_TIMING_1000;
        else if (bitInfo.equals("100")) worTiming = WOR_TIMING_1250;
        else if (bitInfo.equals("101")) worTiming = WOR_TIMING_1500;
        else if (bitInfo.equals("110")) worTiming = WOR_TIMING_1750;
        else if (bitInfo.equals("111")) worTiming = WOR_TIMING_2000;

        if (bitArray[5] == '0') fec = false;
        else if (bitArray[5] == '1') fec = true;

        bitInfo = "" + bitArray[6] + bitArray[7];
        if (bitInfo.equals("00")) power = POWER_30;
        else if (bitInfo.equals("01")) power = POWER_27;
        else if (bitInfo.equals("10")) power = POWER_24;
        else if (bitInfo.equals("11")) power = POWER_21;
    }
}
