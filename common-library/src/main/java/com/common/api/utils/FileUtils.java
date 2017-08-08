package com.common.api.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileUtils<T> {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static void saveObject(Context context, String path, Object object) {
       // LogUtils.e(TAG, path);

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            // fos = context.openFileOutput(path, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (Exception exception) {
            System.out.println("手机缓存文件出问题了,重新开机后再试");
            exception.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "resource"})
    public static <T> T readObject(Context context, String path, Class<T> resultType) {
        T t = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            fis = new FileInputStream(file);
            // fis = context.openFileInput(path);
            ois = new ObjectInputStream(fis);
            t = (T) ois.readObject();
        } catch (Exception exception) {
           // LogUtils.e("FileUtils", "readObject :" + exception.getMessage());
            exception.printStackTrace();
        }
        return t;
    }

    public static boolean copyFile(File file, String copyToPath, String fileName, OnProgressChangeListener onProgressChangeListener)  {
        String filePath = copyToPath+(copyToPath.endsWith("/")?"":File.separator)+fileName;
        File copyFile = new File(filePath);
        long length = file.length();
        if(!copyFile.exists()) {
            try {
                copyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(copyFile));
            byte[] buffer = new byte[1024];
            long count = 0;
            int len = 0;
            int oldProgress = -1;
            while ((len = bufferedInputStream.read(buffer))!=-1) {
                count += len;
                bufferedOutputStream.write(buffer,0,len);
                int progress = (int) ((count*100)/length);
                if(progress!=oldProgress) {
                    if(onProgressChangeListener!=null)
                     onProgressChangeListener.onProgressChange(progress+"%");
                    LogUtils.d("load video progress:count="+count+",lenght="+length+",progress="+progress);
                }
                oldProgress = progress;
            }
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedInputStream!=null)
                    bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(bufferedOutputStream!=null)
                        bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    public static boolean copyFile(File file, String copyToPath)  {
        File copyFile = new File(copyToPath);
        if(!copyFile.exists()) {
            try {
                copyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(copyFile));
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer))!=-1) {
                bufferedOutputStream.write(buffer,0,len);
            }
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedInputStream!=null)
                    bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(bufferedOutputStream!=null)
                        bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
    // 计算文件的 MD5 值
    public static String getFilMd5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        BufferedInputStream in = null;
//        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[8192];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new BufferedInputStream(new FileInputStream(file));
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();

            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public interface OnProgressChangeListener {
        void onProgressChange(String progress);
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream in = null;

        byte[] var2;
        try {
            in = openInputStream(file);
            var2 = IOUtils.toByteArray(in, file.length());
        } finally {
            IOUtils.closeQuietly(in);
        }

        return var2;
    }

    public static FileInputStream openInputStream(File file) throws IOException {
        if(file.exists()) {
            if(file.isDirectory()) {
                throw new IOException("File \'" + file + "\' exists but is a directory");
            } else if(!file.canRead()) {
                throw new IOException("File \'" + file + "\' cannot be read");
            } else {
                return new FileInputStream(file);
            }
        } else {
            throw new FileNotFoundException("File \'" + file + "\' does not exist");
        }
    }

    public static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(in!=null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inStream!=null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outStream!=null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteFileAndFoder(String path){
        LogUtils.d("savor:splash 删除文件夹");
        File f=new File(path);
        if(f.isDirectory()){//如果是目录，先递归删除
            String[] list=f.list();
            for(int i=0;i<list.length;i++){
                deleteFileAndFoder(path+"//"+list[i]);//先删除目录下的文件
            }
        }
        f.delete();
    }
}
