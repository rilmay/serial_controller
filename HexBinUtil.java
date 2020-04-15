public class HexBinUtil {
    public static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i + 1), 16));
//        }
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