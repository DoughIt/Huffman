package Logical;

import java.io.*;

public class FileUtil {
    //统计文件字节及相应字节出现次数
    public static int[] countByte(File file) {
        if (!file.exists()) {
            return null;
        } else if (file.length() == 0)
            return null;
        int[] chars = new int[256];
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            int size;
            while ((size = inputStream.read()) != -1) {
                chars[size]++;
            }
            inputStream.close();
        } catch (Exception e) {

        }
        return chars;
    }

    public static String[] getCharsCode(HuffmanTree.Node root) {
        String[] charsCode = new String[256];
        String code = "";
        getCharsCode(root, code, charsCode);
        return charsCode;
    }

    private static void getCharsCode(HuffmanTree.Node node, String code, String[] charsCode) {
        if (node != null) {
            code += node.getChildType();
            if (node.getLeftChild() == null && node.getRightChild() == null) {
                charsCode[node.getValue()] = code;
            }
            getCharsCode(node.getLeftChild(), code, charsCode);
            getCharsCode(node.getRightChild(), code, charsCode);
        }
    }

    //将01字符串转换成二进制
    public static byte bit2byte(String string) {
        byte result = 0;
        for (int i = string.length() - 1, j = 0; i >= 0; i--, j++) {
            result += (Byte.parseByte(string.charAt(i) + "") * Math.pow(2, j));
        }
        return result;
    }

    //将二进制转换成01字符串
    public static String byte2bits(int value) {
        int tmp = value;
        tmp |= 256;
        String str = Integer.toBinaryString(tmp);
        int len = str.length();
        return str.substring(len - 8, len);
    }
}



