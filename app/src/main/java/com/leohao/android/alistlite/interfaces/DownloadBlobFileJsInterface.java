package com.leohao.android.alistlite.interfaces;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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
        Log.i("DOWNLOAD", "getBase64FromBlobData: "+base64Data);
        convertToGifAndProcess(base64Data);
    }

    public static String getBase64StringFromBlobUrl(String blobUrl) {
        if (blobUrl.startsWith("blob")) {
            return "javascript: " +
                    "var xhr = new XMLHttpRequest();" +
                    "xhr.open('GET', '" + blobUrl + "', true);" +
                    "xhr.setRequestHeader('Content-type','application/octet-stream');" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobFile = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsDataURL(blobFile);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            Android.getBase64FromBlobData(base64data);" +
                    "        }" +
                    "    }" +
                    "};" +
                    "xhr.send();";
        }
        return "javascript: console.log('It is not a Blob URL');";
    }

    private void convertToGifAndProcess(String base64) {
        String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        File gifFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test_" + currentDateTime + "_.gif");
        saveGifToPath(base64, gifFile);
        Toast.makeText(mContext, "文件已下载 "+gifFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        if (mDownloadGifSuccessListener != null) {
            mDownloadGifSuccessListener.downloadGifSuccess(gifFile.getAbsolutePath());
        }
    }

    private void saveGifToPath(String base64, File gifFilePath) {
        try {
            byte[] fileBytes = Base64.decode(base64.replaceFirst("data:image/gif;base64,", ""), 0);
            FileOutputStream os = new FileOutputStream(gifFilePath, false);
            os.write(fileBytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DownloadGifSuccessListener {
        void downloadGifSuccess(String absolutePath);
    }
}
