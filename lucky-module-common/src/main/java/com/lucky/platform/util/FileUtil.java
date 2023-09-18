package com.lucky.platform.util;

import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author: Loki
 * @data: 2021-09-28 10:33
 **/
public class FileUtil {

    public static byte[] getFileByte(File file){
        try (FileChannel channel = new FileInputStream(file).getChannel()){
            final ByteBuffer buf = ByteBuffer.allocate((int) file.length());
            while (true){
                buf.clear();
                final int read = channel.read(buf);
                if (read == -1){
                    break;
                }
            }
            return buf.array();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static File getFileToChannel(byte[] bfile, String filePath, String fileName) {
        File dir = new File(filePath);
        //判断文件目录是否存在
        if (!dir.exists() && dir.isDirectory()) {
            dir.mkdirs();
        }
        File file = new File(filePath + File.separator + fileName);
        try (FileChannel fos = new FileOutputStream(file).getChannel()){
            ByteBuffer buf = ByteBuffer.allocate(bfile.length);
            buf.put(bfile);
            buf.flip();
            while (buf.hasRemaining()){
                fos.write(buf);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return file;

    }
    public static File getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
}
