package com.leohao.android.alistlite.interfaces;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author LeoHao
 */
public class DownloadBlobFileJsInterface {

    private final Context mContext;
    private DownloadGifSuccessListener mDownloadGifSuccessListener;

    public DownloadBlobFileJsInterface(Context context) {
        this.mContext = context;
    }

    public void setDownloadGifSuccessListener(DownloadGifSuccessListener listener) {
        mDownloadGifSuccessListener = listener;
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data) {
        Log.i("DOWNLOAD", "getBase64FromBlobData");
        downloadBase64File(base64Data);
    }

    /**
     * 根据 base64 数据保存文件，自动识别文件类型
     */
    private void downloadBase64File(String base64Data) {
        try {
            // 解析 data:[<mime>];base64,<data> 格式
            String mimeType = "application/octet-stream";
            String base64Content = base64Data;
            if (base64Data.startsWith("data:")) {
                int commaIdx = base64Data.indexOf(",");
                if (commaIdx > 0) {
                    String header = base64Data.substring(0, commaIdx);
                    base64Content = base64Data.substring(commaIdx + 1);
                    int semicolonIdx = header.indexOf(";");
                    if (semicolonIdx > 5) {
                        mimeType = header.substring(5, semicolonIdx);
                    }
                }
            }
            // 根据 MIME 类型确定扩展名
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            if (extension == null) {
                extension = "bin";
            }
            // 生成文件名
            String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
            String fileName = "Download_" + currentDateTime + "." + extension;
            byte[] fileBytes = Base64.decode(base64Content, Base64.DEFAULT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+：通过 MediaStore 写入（适配分区存储）
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = mContext.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream os = mContext.getContentResolver().openOutputStream(uri);
                    if (os != null) {
                        os.write(fileBytes);
                        os.flush();
                        os.close();
                    }
                }
                Toast.makeText(mContext, "已保存到 Downloads/" + fileName, Toast.LENGTH_LONG).show();
            } else {
                // Android 9 及以下：直接写入公共目录
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outFile = new File(downloadDir, fileName);
                FileOutputStream os = new FileOutputStream(outFile, false);
                os.write(fileBytes);
                os.flush();
                os.close();
                Toast.makeText(mContext, "已保存到 Downloads/" + fileName, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("DOWNLOAD", "downloadBase64File failed", e);
            Toast.makeText(mContext, "文件下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    public interface DownloadGifSuccessListener {
        void downloadGifSuccess(String absolutePath);
    }
}
