package jzq.util;



public class BytesUtils {

    //--- {byte[] <-> int} ---------------------------------------------------------------------------------------------

    /**
     * 将 byte 数组转换为 int, 取前 4 位.
     *
     * @param b byte 数据
     * @return int 值
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * 将 int 数据转换为 byte 数组
     *
     * @param a int 数据
     * @return byte 数组, 4 位
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    //--- {byte[] <-> short} -------------------------------------------------------------------------------------------

    /**
     * 将 byte 数组转换为 short, 取前 2 位.
     *
     * @param b byte 数据
     * @return short 值
     */
    public static short byteArrayToShort(byte[] b) {
        return (short) (b[1] & 0xFF |
                (b[0] & 0xFF) << 8);
    }

    /**
     * 将 short 数据转换为 byte 数组
     *
     * @param a short 数据
     * @return short 数组, 2 位
     */
    public static byte[] shortToByteArray(short a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    //--- {byte[] <-> hex string} --------------------------------------------------------------------------------------

    /**
     * 实现字节数组转向十六进制的转换的方法, 默认使用 空格 作为分隔符
     */

    public static String b2h(byte... src) {
        return b2h(" ", src);
    }

    /**
     * 实现字节数组转向十六进制的转换的方法
     *
     * @param split 分割符
     * @param src   字节数组
     * @return Hex String
     */

    public static String b2h(String split, byte... src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0XFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if (split != null && i != src.length - 1) {
                stringBuilder.append(split);
            }
        }
        return stringBuilder.toString().toUpperCase();
    }

    private static String hex_regex = "^[A-Fa-f0-9]+$";

    /**
     * 实现十六进制String转向字节数组转换的方法.
     * 在进行数据转换时进行了严格的数据校验,一旦发现数据格式不正常,则直接返回 null
     *
     * @param hex   Hex String
     * @param split 分割符
     * @return 解析后的 byte[] 或者 null
     */

    public static byte[] h2b(String hex, String split) {
        if (hex == null || hex.length() <= 0) {
            return null;
        }
        // 没有分割符号
        if (null == split || split.length() == 0) {
            hex = hex.replace(" ", "");
            if (!hex.matches(hex_regex)) {
                return null;
            }
            if (hex.length() % 2 != 0) return null;  // 长度不符合要求
            byte[] result = new byte[hex.length() / 2];
            int j = 0;
            for (int i = 0; i < result.length; i++) {
                char c0 = hex.charAt(j++);
                char c1 = hex.charAt(j++);
                result[i] = (byte) ((parse(c0) << 4) | parse(c1));
            }
            return result;
        }
        // 有分割符号
        else {
            String[] hexs = hex.split(split);
            byte[] result = new byte[hexs.length];
            for (int i = 0; i < hexs.length; i++) {
                int len = hexs[i].length();
                if (len == 0 || len > 2) {
                    return null;
                }
                String tem = hexs[i].replace(" ", "");
                if (!tem.matches(hex_regex)) {
                    return null;
                }
                try {
                    result[i] = (byte) Integer.parseInt(tem, 16);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return result;
        }
    }


    private static int parse(char c) {
        if (c >= 'a') {
            return (c - 'a' + 10) & 0x0f;
        }
        if (c >= 'A') {
            return (c - 'A' + 10) & 0x0f;
        }
        return (c - '0') & 0x0f;
    }
}
