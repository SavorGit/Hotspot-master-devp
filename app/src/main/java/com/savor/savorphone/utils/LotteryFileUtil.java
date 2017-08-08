package com.savor.savorphone.utils;

import android.support.annotation.NonNull;

import com.common.api.codec.binary.Base64;
import com.common.api.utils.LogUtils;
import com.savor.savorphone.SavorApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 抽奖缓存文件工具类
 * Created by hezd on 2017/5/10.
 */

public class LotteryFileUtil {
    public static volatile LotteryFileUtil instance = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private LotteryFileUtil(){}

    public static LotteryFileUtil getInstance() {
        if(instance == null) {
            synchronized (LotteryFileUtil.class) {
                if(instance == null) {
                    instance = new LotteryFileUtil();
                }
            }
        }
        return instance;
    }

    @NonNull
    private File getLottoryFile() {
        String lottoryFileDir = SavorApplication.getInstance().getLottoryNumDir();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        String format = simpleDateFormat.format(date);
        return new File(lottoryFileDir+File.separator+format);
    }

    public boolean isCacheLottory() {
        File lottoryFile = getLottoryFile();
        if(lottoryFile== null||!lottoryFile.exists())
            return false;
        return true;
    }

    public void saveLottoryNum(final String lottery_num) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 删除所在目录下所有文件，新建并写入抽奖次数并进行base64编码
                String lottoryFileDir = SavorApplication.getInstance().getLottoryNumDir();
                File file = new File(lottoryFileDir);
                if(!file.exists()) {
                    file.mkdirs();
                }

                String base64Str = new String(Base64.encodeBase64(lottery_num.getBytes()));
                File lottoryFile = getLottoryFile();
                File[] files = file.listFiles();
                for(File f : files) {
                    if(!f.getName().equals(lottoryFile.getName()))
                        f.delete();
                }
                if(!lottoryFile.exists()) {
                    try {
                        lottoryFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    FileWriter fileWriter = new FileWriter(lottoryFile,false);
                    fileWriter.write(base64Str);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 当前抽奖次数
     * @param lotteryCount
     */
    public void saveLottoryCount(final String lotteryCount) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                // 删除所在目录下所有文件，新建并写入抽奖次数并进行base64编码
                String lottoryFileDir = SavorApplication.getInstance().getLottoryCountDir();
                File file = new File(lottoryFileDir);
                if(!file.exists()) {
                    file.mkdirs();
                }

                String base64Str = new String(Base64.encodeBase64(lotteryCount.getBytes()));
                String lottoryCountFileDir = SavorApplication.getInstance().getLottoryCountDir();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = new Date(System.currentTimeMillis());
                String format = simpleDateFormat.format(date);
                File lottoryCountFile = new File(lottoryCountFileDir+File.separator+format);
                File[] files = file.listFiles();
                for(File f : files) {
                    if(!f.getName().equals(lottoryCountFile.getName()))
                        f.delete();
                }
                if(!lottoryCountFile.exists()) {
                    try {
                        lottoryCountFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    FileWriter fileWriter = new FileWriter(lottoryCountFile,false);
                    fileWriter.write(base64Str);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 获取当前抽奖次数
     * @return
     */
    public int getLottoryCount() {
        int result = 1;
        String lottoryCount = SavorApplication.getInstance().getLottoryCountDir();
        File countDir = new File(lottoryCount);
        if(countDir.exists()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date(System.currentTimeMillis());
            String format = simpleDateFormat.format(date);
            String randomPath = lottoryCount+File.separator+format;
            File countFile = new File(randomPath);
            if(countFile.exists()) {
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(randomPath);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = bufferedReader.readLine();
                    String count = new String(Base64.decodeBase64(line));
                    result = Integer.valueOf(count);
                    fileReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * 获取第几次中奖
     * @return
     */
    public int getLottoryRandom() {
        int random = 5;
        String lottoryRandom = SavorApplication.getInstance().getLottoryRandomDir();
        File randomDir = new File(lottoryRandom);
        if(randomDir.exists()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date(System.currentTimeMillis());
            String format = simpleDateFormat.format(date);
            String randomPath = lottoryRandom+File.separator+format;
            File randomFile = new File(randomPath);
            if(randomFile.exists()) {
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(randomPath);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = bufferedReader.readLine();
                    String randomNum = new String(Base64.decodeBase64(line));
                    random = Integer.valueOf(randomNum);
                    fileReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return random;
    }

    public int getLottoryNum() {
        int lottoryNum = 0;
        String lottoryFileDir = SavorApplication.getInstance().getLottoryNumDir();
        File lottoryDir = new File(lottoryFileDir);
        if(lottoryDir.exists()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date(System.currentTimeMillis());
            String format = simpleDateFormat.format(date);
            String lottoryFilePath = lottoryFileDir+File.separator+format;
            File path = new File(lottoryFilePath);
            if(path.exists()) {
                try {
                    FileReader fileReader = new FileReader(path);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line = bufferedReader.readLine();
                    String num = new String(Base64.decodeBase64(line));
                    lottoryNum = Integer.valueOf(num);
                    fileReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtils.d("savor:lottory 获取抽奖次数,当前可抽奖次数"+lottoryNum+"次");
        return lottoryNum;
    }


    /**
     * 生成随机中奖次数
     */
    public void createRadomLottoryNum(int random) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String lottoryFileDir = SavorApplication.getInstance().getLottoryRandomDir();
                File lottoryDir = new File(lottoryFileDir);
                if(!lottoryDir.exists()) {
                    lottoryDir.mkdirs();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = new Date(System.currentTimeMillis());
                String format = simpleDateFormat.format(date);
                String ramdomPath = lottoryFileDir+File.separator+format;
                Random random = new Random();
                int lottoryRandom = Math.abs(random.nextInt(5))+1;
                LogUtils.d("savor:lottory 生成中奖次数 为第"+lottoryRandom+"次中奖");
                File file = new File(ramdomPath);
                // 清除非当天的文件
                File[] files = lottoryDir.listFiles();
                for(File f : files) {
                    if(!f.getName().equals(file.getName())) {
                        f.delete();
                    }
                }
                if(!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        String base64Str = new String(Base64.encodeBase64(String.valueOf(lottoryRandom).getBytes()));
                        LogUtils.d("savor:lottory 中奖次数base64结果"+base64Str);
                        FileWriter fileWriter = new FileWriter(ramdomPath);
                        fileWriter.write(base64Str);
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
