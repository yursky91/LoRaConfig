package com.yursky.loraconfig;

class LoraParam {

    boolean saveHard = true;
    int address = 0;
    String parity = PARITY_8N1;
    String uartRate = UART_RATE_9600;
    String airRate = AIR_RATE_2k4;
    int channel = 23;
    String fixedMode = STATE_DISABLE;
    String ioMode = IO_MODE_PUSHPULL;
    String worTiming = WOR_TIMING_250;
    String fec = STATE_ENABLE;
    String power = POWER_30;

    final static String STATE_DISABLE = "0";
    final static String STATE_ENABLE = "1";

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
        String binHex = Integer.toBinaryString(0x100 | intHex).substring(1);    //0x100: 100 = 256 = 100000000
        char[] bitArray = binHex.toCharArray();

        String bitInfo = "" + bitArray[0] + bitArray[1];
        parity = bitInfo;

        bitInfo = "" + bitArray[2] + bitArray[3] + bitArray[4];
        uartRate = bitInfo;

        bitInfo = "" + bitArray[5] + bitArray[6] + bitArray[7];
        airRate = bitInfo;

        //5 byte
        channel = Integer.parseInt(hexs[4], 16);

        //6 byte
        intHex = Integer.parseInt(hexs[5], 16);
        binHex = Integer.toBinaryString(0x100 | intHex).substring(1);
        bitArray = binHex.toCharArray();

        fixedMode = String.valueOf(bitArray[0]);
        ioMode = String.valueOf(bitArray[1]);

        bitInfo = "" + bitArray[2] + bitArray[3] + bitArray[4];
        worTiming = bitInfo;

        fec = String.valueOf(bitArray[5]);

        bitInfo = "" + bitArray[6] + bitArray[7];
        power = bitInfo;
    }

    LoraParam() {
    }

    byte[] getBytes() {
        StringBuilder stringBuilder = new StringBuilder();

        //1 byte
        if (saveHard) stringBuilder.append("C0");
        else stringBuilder.append("C2");
        stringBuilder.append("_");

        //2-3 byte
        stringBuilder.append(Integer.toHexString(0x100 | (address / 256)).substring(1));
        stringBuilder.append("_");
        stringBuilder.append(Integer.toHexString(0x100 | (address - address / 256)).substring(1));
        stringBuilder.append("_");

        //4 byte
        String dataByte = parity + uartRate + airRate;
        String hex = Integer.toHexString(0x100 | Integer.parseInt(dataByte, 2)).substring(1);
        stringBuilder.append(hex);
        stringBuilder.append("_");

        //5 byte
        stringBuilder.append(Integer.toHexString(0x100 | channel).substring(1));
        stringBuilder.append("_");

        //6 byte
        dataByte = fixedMode + ioMode + worTiming + fec + power;
        hex = Integer.toHexString(0x100 | Integer.parseInt(dataByte, 2)).substring(1);
        stringBuilder.append(hex);

        String[] hexs = stringBuilder.toString().split("_");
        byte[] bytes = {(byte) Integer.parseInt(hexs[0], 16),
                (byte) Integer.parseInt(hexs[1], 16),
                (byte) Integer.parseInt(hexs[2], 16),
                (byte) Integer.parseInt(hexs[3], 16),
                (byte) Integer.parseInt(hexs[4], 16),
                (byte) Integer.parseInt(hexs[5], 16)};

        return bytes;
    }
}
