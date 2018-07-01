package Logical;


import java.io.*;
import java.util.PriorityQueue;

public class Decompress {
    private File srcFile;
    private BufferedInputStream inputStream;

    public Decompress(File srcFile, BufferedInputStream inputStream) throws Exception {
        this.srcFile = srcFile;
        this.inputStream = inputStream;
    }

    public void decompressFile() throws Exception {
        String postfix = srcFile.getName().substring(srcFile.getName().lastIndexOf("."));
        if (!postfix.equals(".hfm")) {
            System.out.println("不支持的类型");
        } else {
            srcFile = new File(srcFile.getAbsolutePath().substring(0, srcFile.getAbsolutePath().lastIndexOf(".")));
            decompress(srcFile); //TODO
        }
    }

    private void decompress(File file) throws Exception {
        int isDirectory;
        if ((isDirectory = inputStream.read()) != -1)
            if (isDirectory != 0) {//目录
                directory(file);
            } else {//文件
                File tmpFile = new File(file.getParent());
                singleFile(tmpFile);
            }
    }

    private void directory(File file) throws Exception {
        int isDirectory;
        File tmpFile;
        if (!file.exists())
            if (file.mkdirs()) {
                System.out.println("创建目录成功！");
            }
        int isEmpty;
        if ((isEmpty = inputStream.read()) != -1)
            if (isEmpty != 0) {//目录非空
                int length = inputStream.read();
                for (int i = 0; i < length; i++) {
                    if ((isDirectory = inputStream.read()) != -1) {
                        if (isDirectory != 0) {
                            int nameLen = inputStream.read();
                            byte[] bytes = new byte[nameLen];
                            inputStream.read(bytes);
                            String name = new String(bytes);
                            String tmpPath = file.getAbsolutePath() + "\\" + name;
                            System.out.println(tmpPath);
                            tmpFile = new File(tmpPath);
                            directory(tmpFile);
                        } else singleFile(file);
                    }
                }
            }
    }

    private void singleFile(File file) throws Exception {
        File tmpFile;
        if (inputStream.read() == 0) {          //空文件
            tmpFile = new File(getOutputPath(file));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
            outputStream.close();
        } else {
            tmpFile = new File(getOutputPath(file));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
            writeFile(outputStream);
            outputStream.flush();
            outputStream.close();
        }
    }

    private void writeFile(BufferedOutputStream outputStream) throws Exception {
        HuffmanTree.Node root = getHuffmanTree();//获取Huffman树
        HuffmanTree.Node node = root;
        long bytesLength = inputStream.read() + (inputStream.read() << 8) + (inputStream.read() << 16) + (inputStream.read() << 24) + (inputStream.read() << 32);//获取文件字节数
        int tmpByte, length = 0;
        StringBuilder string = new StringBuilder("");
        boolean complete = false;
        while (length < bytesLength && (tmpByte = inputStream.read()) != -1) {
            string.append(FileUtil.byte2bits(tmpByte));
            length++;
            if (length == bytesLength - 1) {
                tmpByte = inputStream.read();//补零个数
                tmpByte = tmpByte == 0 ? 8 : tmpByte;
                string.delete(string.length() - tmpByte, string.length());
                complete = true;
            }

            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == '0') {
                    node = node.getLeftChild();
                } else
                    node = node.getRightChild();
                if (node.isLeaf()) {
                    outputStream.write(node.getValue());
                    node = root;
                }
            }
            string = new StringBuilder("");
            if (complete)
                break;
        }

    }

    private String getOutputPath(File file) throws Exception {
        //读取源文件名
        int len = inputStream.read();
        byte[] bytes = new byte[len];
        inputStream.read(bytes);
        String name = new String(bytes);
        return file.getAbsolutePath() + "\\" + name;
    }

    private HuffmanTree.Node getHuffmanTree() throws Exception {
        //读取源文件字节数并构建Huffman树
        PriorityQueue<HuffmanTree.Node> queue = new PriorityQueue<>();
        HuffmanTree tree = new HuffmanTree();
        HuffmanTree.Node root;
        int charsLength = inputStream.read();
        charsLength = charsLength == 0 ? 256 : charsLength;//byte表示，256等于0，不必考虑空文件
        int index;
        for (int i = 0; i < charsLength; i++) {
            index = inputStream.read();
            int tmp = inputStream.read() + (inputStream.read() << 8) + (inputStream.read() << 16) + (inputStream.read() << 24);
            HuffmanTree.Node node = new HuffmanTree.Node(index, tmp);
            queue.add(node);
        }
        root = tree.getHuffmanTree(queue);
        return root;
    }

}
