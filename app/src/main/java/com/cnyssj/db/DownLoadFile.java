package com.cnyssj.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.cnyssj.db.util.DownloadProgressListener;
import com.cnyssj.db.util.FileDownloader;
import com.cnyssj.db.util.FoodJson;
import com.cnyssj.db.util.GZipUtils;
import com.cnyssj.db.util.Goods;
import com.cnyssj.db.util.Order;

/**
 * 文件操作类(下载，解压缩(gzip),读取文件)
 * 断点续传
 *
 * @author lb
 */
public class DownLoadFile {
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;
    private Handler handler = new UIHandler();
    Handler handler2 = new Handler();
    private DownloadTask task;
    FoodJson foodjson = new FoodJson();
    FoodJson1 foodjson1 = new FoodJson1();
    private Context context;
    private int Maxsize;//文件最大长度
    private int downsize;//下载的长度
    private String filepath;
    private String checksums;
    private String path = "http://cnyssj.net/data/test/test_product_list_50000.json";
    private DBManager dbManager;
    private String GzipfileName;//压缩文件的文件名
    private String filename;//加压后文件的文件名
    private int updateCount = 0;//下载百分比

    public DownLoadFile(Context context) {
        this.context = context;
        dbManager = new DBManager(context);
    }

    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    downsize = msg.getData().getInt("size");
                    updateCount = downsize * 100 / Maxsize;
                    Message msg2 = new Message();
                    msg2.what = 9;
                    msg2.getData().putInt("updateCount", updateCount);
                    handler2.sendMessage(msg2);
                    if (Maxsize == downsize) {
                        Log.d("DownLoadFile", "下载完成,正在解压。。");
//						Toast.makeText(context, "下载完成,正在解压。。",Toast.LENGTH_LONG).show();
                        getFileMD5(filepath, checksums);
                    }

                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(context, "下载失败",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    /**
     * 开始下载
     *
     * @param path
     */
    public void Download(String path, String checksum, Handler handler) {
        checksums = checksum;
        filepath = path;
        this.handler2 = handler;
        String filename = path.substring(filepath.lastIndexOf('/') + 1);

        try {
            // URL编码（这里是为了将中文进行URL编码）
            filename = URLEncoder.encode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        path = path.substring(0, path.lastIndexOf("/") + 1) + filename;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // File savDir =
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            // 保存路径
            File savDir = Environment.getExternalStorageDirectory();

            download(path, savDir);

        } else {
            Toast.makeText(context,
                    "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 暂停下载
     */
    public void StopDownFile() {
        exit();
    }
    /*
     * 由于用户的输入事件(点击button, 触摸屏幕....)是由主线程负责处理的，如果主线程处于工作状态，
	 * 此时用户产生的输入事件如果没能在5秒内得到处理，系统就会报“应用无响应”错误。
	 * 所以在主线程里不能执行一件比较耗时的工作，否则会因主线程阻塞而无法处理用户的输入事件，
	 * 导致“应用无响应”错误的出现。耗时的工作应该在子线程里执行。
	 */
    //	private DownloadTask task;

    private void exit() {
        if (task != null)
            task.exit();
    }

    private void download(String path, File savDir) {
        task = new DownloadTask(path, savDir);
        new Thread(task).start();
    }

    /**
     * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
     * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
     */
    private final class DownloadTask implements Runnable {
        private String path;
        private File saveDir;
        private FileDownloader loader;

        public DownloadTask(String path, File saveDir) {
            this.path = path;
            this.saveDir = saveDir;
        }

        /**
         * 退出下载
         */
        public void exit() {
            if (loader != null)
                loader.exit();
        }

        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
            @Override
            public void onDownloadSize(int size) {
                Message msg = new Message();
                msg.what = PROCESSING;
                msg.getData().putInt("size", size);
                handler.sendMessage(msg);
            }
        };

        public void run() {
            try {
                // 实例化一个文件下载器
                loader = new FileDownloader(context, path, saveDir, 3);
                // 设置进度条最大值
                Maxsize = loader.getFileSize();
                loader.download(downloadProgressListener);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(FAILURE)); // 发送一条空消息对象
            }
        }

    }

    /**
     * 获取下载的文件名
     *
     * @param filepath
     */
    public void getfileName(String filepath) {
        URL mUrl;
        try {
            mUrl = new URL(filepath);
            String GzipfileName = new File(mUrl.getFile()).getName();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public final static String[] hexDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 获取文件的MD5值
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        String md5 = null;
        FileInputStream fis = null;
        FileChannel fileChannel = null;
        try {
            fis = new FileInputStream(file);
            fileChannel = fis.getChannel();
            MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(byteBuffer);
                md5 = byteArrayToHexString(md.digest());
            } catch (NoSuchAlgorithmException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            try {
                fileChannel.close();
                fis.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        return md5;
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param digest
     * @return
     */
    private static String byteArrayToHexString(byte[] digest) {

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            buffer.append(byteToHexString(digest[i]));
        }
        return buffer.toString();
    }


    /**
     * 字节转十六进制字符串
     *
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        //  int d1 = n/16;
        int d1 = (b & 0xf0) >> 4;

        //   int d2 = n%16;
        int d2 = b & 0xf;
        return hexDigits[d1] + hexDigits[d2];
    }

    /*
     * 解压缩文件
     * filename 解压后的文件名
     */
    public void GzipFile(String filename) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            GZipUtils.decompress(file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容(商品信息)
     *
     * @param goodlist
     */
    public List<Goods> oneReadFile(String filename) {
        List<Goods> goodlist = new ArrayList<Goods>();
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                System.out.println("readline:" + readline);
                sb.append(readline);
            }
            br.close();
            goodlist = foodjson1.parseJsons(sb.toString());

            System.out.println("读取成功：" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodlist;
    }

    /**
     * 读取文件内容(订单信息)
     *
     * @param Path
     * @return
     */
    public List<Order> twoReadFile(String filename) {
        List<Order> goodlist = new ArrayList<Order>();
        BufferedReader reader = null;
        String laststr = "";
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, "utf-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
            goodlist = foodjson1.parseOrderJsons(laststr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return goodlist;
    }

    /**
     * 读取文件内容(三)
     *
     * @param filename
     * @return
     */
    public List<Goods> threeReadFile(String filename) {
        List<Goods> goodlist = new ArrayList<Goods>();
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            BufferedReader br = new BufferedReader(new FileReader(file));// 读取原始json文件
            String s = null, ws = null;
            while ((s = br.readLine()) != null) {
                // System.out.println(s);
                try {
                    JSONArray features = new JSONArray(s);// 找到json数组
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject info = features.getJSONObject(i);// 获取数组的第i个json对象
                        String id = info.getString("id");
                        String title = info.getString("title");
                        String productsn = info.getString("productsn");
                        double price = info.getDouble("price");
                        String thumb = info.getString("thumb");
                        Goods goods = new Goods(id, title, productsn, price, thumb);
                        goodlist.add(goods);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            br.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return goodlist;
    }


    //md5值校验
    public void getFileMD5(final String filepath, final String checksum) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Looper.prepare();

                URL mUrl;
                int count = 1;
                try {
                    mUrl = new URL(filepath);
                    GzipfileName = new File(mUrl.getFile()).getName();
                    File file = new File(Environment.getExternalStorageDirectory(), GzipfileName);
                    //"checksum": "e281ecefabfc71698b679ffc32d1a131"
                    //首先获取文件的MD5
                    String md5 = getFileMD5(file);
                    //MD5校验正确，进行解压
                    if (md5.equals(checksum)) {
                        UnpackGzipFile(GzipfileName);
                    } else {
                        if (count <= 2) {
                            if (file.exists()) { //判断文件是否存在
                                if (file.isFile()) { //判断是否是文件
                                    file.delete(); //删除文件
                                    //文件出错，重新请求下载
                                    Download(filepath, checksum, handler);
                                    count++;
                                } else {
                                    System.out.println("不是文件");
                                }
                            } else {
                                System.out.println("文件不存在");
                            }

                        } else {
                            file.delete();
                            Toast.makeText(context, "文件发生未知错误", 1).show();
                        }
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();

    }

    //解压缩文件
    public void UnpackGzipFile(String GzipfileName) {

        GzipFile(GzipfileName);

        Log.d("DownLoadFile", "解压完成,正在添加商品信息。。");

        filename = GzipfileName.replace(".gz", "");
        //读取文件内容
        readGoodsFile(filename);
    }

    //读取下载后的文件，添加进数据库商品表
    public void readGoodsFile(String filename) {
        List<Goods> goodlist = oneReadFile(filename);
        dbManager.addallFoods(goodlist);
    }


}
