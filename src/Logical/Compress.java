package Logical;


import java.io.*;
import java.util.*;

public class Compress {
    private File srcFile;
    private int[] chars;
    private String[] charsCode;
    private BufferedOutputStream outputStream;
    private BufferedInputStream inputStream;


    public Compress(File srcFile, BufferedOutputStream outputStream) throws Exception {
        this.srcFile = srcFile;
        this.outputStream = outputStream;
        this.inputStream = new BufferedInputStream(new FileInputStream(srcFile));
    }

    public void compressFile() throws Exception {
        if (srcFile.length() != 0) {
            this.chars = FileUtil.countByte(srcFile);
            HuffmanTree tree = new HuffmanTree();// 构建优先队列
            PriorityQueue<HuffmanTree.Node> queue = tree.toQueue(chars);// 构建树
            HuffmanTree.Node root = tree.getHuffmanTree(queue);
            compressFile(root);
        } else {
            outputStream.write(0);//0代表是文件
            outputStream.write(0);//第二个零代表空文件
            writeName();
            inputStream.close();
        }
    }

    private void compressFile(HuffmanTree.Node root) throws Exception {
        charsCode = FileUtil.getCharsCode(root);
        writeHead();
        writeFile();
        System.out.println("压缩完毕~~~");
        inputStream.close();
    }

    private void writeHead() throws Exception {
        outputStream.write(0);//0代表是文件，1代表文件夹
        outputStream.write(1);//第二个1代表不是空文件
        writeName();
        writeByteCount();
        writeBytesSize();
    }

    private void writeName() throws Exception {
        //写入源文件名
        String name = srcFile.getName();
        outputStream.write(name.length());
        outputStream.write(name.getBytes());
    }

    private void writeByteCount() throws Exception {
        int size = 0;
        for (int i = 0; i < chars.length; i++)
            if (chars[i] > 0)
                size++;
        outputStream.write(size);
        //写入文件字节次数，用于解压时构建Huffman树
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] > 0) {
                outputStream.write(i);
                outputStream.write((byte) chars[i]);
                outputStream.write((byte) (chars[i] >> 8));
                outputStream.write((byte) (chars[i] >> 16));
                outputStream.write((byte) (chars[i] >> 24));
            }
        }
    }

    private void writeBytesSize() throws Exception {
        //写入文件重新编码后文件主体的字节数
        long bytesLength = getBytesLength();
        outputStream.write((byte) bytesLength);
        outputStream.write((byte) (bytesLength >> 8));
        outputStream.write((byte) (bytesLength >> 16));
        outputStream.write((byte) (bytesLength >> 24));
        outputStream.write((byte) (bytesLength >> 32));
    }

    private void writeFile() throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        int tmp;
        while ((tmp = inputStream.read()) != -1) {
            stringBuilder.append(charsCode[tmp]);
            if (stringBuilder.length() >= 8) {
                outputStream.write(Integer.parseInt(stringBuilder.substring(0, 8), 2));
                stringBuilder.delete(0, 8);
            }
        }

        int hfmLength = stringBuilder.length();
        int byteNumber = hfmLength / 8;
        int restNumber = hfmLength % 8;
        for (int i = 0; i < byteNumber; i++) {
            String str = stringBuilder.substring(i * 8, (i + 1) * 8);
            outputStream.write(FileUtil.bit2byte(str));
        }

        // 补0操作
        int zeroNumber = (8 - restNumber) % 8;
        StringBuilder str = new StringBuilder(stringBuilder.substring(hfmLength - restNumber));
        str.append("0");//若restNumber=0，则写入0，保证格式整齐，便于解压
        for (int i = 1; i < zeroNumber; i++) {
            str.append("0");
        }
        byte by = FileUtil.bit2byte(str.toString());
        outputStream.write(by);

        String zeroLenStr = Integer.toBinaryString(zeroNumber);// 将补0的长度也记录下来保存到文件末尾
        byte zeroB = FileUtil.bit2byte(zeroLenStr);
        outputStream.write(zeroB);
        outputStream.flush();
    }

    private long getBytesLength() {
        long bytesLength = 0, size = chars.length;
        for (int i = 0; i < size; i++) {
            if (chars[i] > 0)
                bytesLength += chars[i] * charsCode[i].length();//字节出现次数乘以字节对应的Huffman编码长度
        }
        bytesLength /= 8;//字节数
        bytesLength += 2;//最后不足八位的编码一字节，以及补零的一字节
        return bytesLength;
    }

}
