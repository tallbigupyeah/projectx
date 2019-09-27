package com.minchainx.networklite.callback;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public abstract class FileCallback extends Callback<File> {

    /**
     * 目标文件存储目录路径
     */
    private String mDestFileDir;
    /**
     * 目标文件存储文件名
     */
    private String mDestFileName;

    public FileCallback(String mDestFileDir, String mDestFileName) {
        this.mDestFileDir = mDestFileDir;
        this.mDestFileName = mDestFileName;
    }

    @Override
    public File parseResponse(Response response, int id) throws Exception {
        return saveFile(response, id);
    }

    /**
     * 保存文件
     *
     * @param response 响应结果
     * @param id       请求id
     * @return
     */
    protected File saveFile(Response response, final int id) throws IOException {
        byte[] buf = new byte[4096];
        int len = 0;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;
            File dir = new File(mDestFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, mDestFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onProgressUpdate(finalSum * 1.0f / total, total, id);
                    }
                });
            }
            fos.flush();
            return file;
        } finally {
            try {
                response.body().close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
