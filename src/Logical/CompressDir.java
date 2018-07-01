package Logical;

import java.io.BufferedOutputStream;
import java.io.File;

public class CompressDir {
    private File file;
    private long size;
    private BufferedOutputStream outputStream;

    public CompressDir(File file, BufferedOutputStream outputStream) {
        this.file = file;
        this.outputStream = outputStream;
    }

    public long compressDir() throws Exception {
        outputStream.write(1);//1代表目录，第一个文件一定是目录

        size = compressDir(file, outputStream);
        return size;
    }

    private long compressDir(File srcFile, BufferedOutputStream outputStream) throws Exception {
        //写入第一个目录信息，然后递归调用

        File[] files = srcFile.listFiles();
        int length = files.length;
        if (length == 0) {
            outputStream.write(0);//空目录
        } else {
            outputStream.write(1); //非空目录
            outputStream.write(length);
            for (File file : files) {
                if (file.isDirectory()) {
                    //TODO
                    outputStream.write(1);
                    String name = file.getName();
                    outputStream.write(name.length());
                    outputStream.write(name.getBytes());
                    compressDir(file, outputStream);
                } else {
                    size += file.length();
                    new Compress(file, outputStream).compressFile();
                }
            }
        }
        return size;
    }

}
