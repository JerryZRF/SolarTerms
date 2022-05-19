package cf.jerryzrf.solarterms;

import android.app.Activity;
import android.app.AlertDialog;

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
}
