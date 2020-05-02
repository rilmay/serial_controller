package util;

public class HexBinUtil {
    public static byte[] hexStringToByteArray(String s) throws NumberFormatException{
        byte b = Byte.parseByte(s);
        return byteArray(b);
    }

    public static String stringFromByteArray(byte[] bArray) {
        StringBuilder output = new StringBuilder();
        for (byte b : bArray) {
            output.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            output.append(" ");
        }
        return output.toString();
    }

    public static String stringFromByteArray(byte b) {
        return stringFromByteArray(byteArray(b));
    }

    public static byte[] byteArray(byte b){
        byte[] bytes = new byte[1];
        bytes[0] = b;
        return bytes;
    }
}