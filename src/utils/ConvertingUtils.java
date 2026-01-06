package utils;

import java.util.HexFormat;

public class ConvertingUtils {

    public static byte[] toByteArray(String input) {
        return input.getBytes();
    }

    public static String toHexString(byte[] input) {
        return HexFormat.of().formatHex(input);
    }

    public static byte[] fromHexString(String input) {
        return HexFormat.of().parseHex(input);
    }

}
