package cf.jerryzrf.solarterms;

import android.app.Activity;
import android.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author JerryZRF
 */
public final class Utils {
    public static void crashByJson(Activity activity) {
        AlertDialog errorDialog = new AlertDialog.Builder(activity)
                .setTitle("致命错误！")
                .setMessage("读取资源文件时出错了！5s后退出")
                .setIcon(R.mipmap.ic_launcher)
                .create();
        errorDialog.show();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.exit(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 拷贝文件
     * @param is 源文件
     * @param os 目标文件
     */
    public static void copyFile(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[4096];
        int n;
        while ((n = is.read(buffer)) != -1) {
            os.write(buffer, 0, n);
        }
        os.close();
    }
}
