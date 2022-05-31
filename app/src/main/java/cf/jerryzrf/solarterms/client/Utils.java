package cf.jerryzrf.solarterms.client;

import android.app.Activity;
import android.app.AlertDialog;
import org.json.JSONException;
import org.json.JSONObject;

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
        is.close();
    }

    public static Boolean isHeadOrTail(int year, String st) {
        try {
            if (Json.getDateData(year, 0).getString("name").equals(st)) {
                return false;
            } else if (Json.getDateData(year, 23).getString("name").equals(st)) {
                return true;
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNumByStName(int year, String st) {
        try {
            for (int i = 0; i < 24; i++) {
                JSONObject object = Json.getDateData(year, i);
                if (object.getString("name").equals(st)) {
                    return i;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getNextSt(int year, String st) {
        try {
            return Json.getDateData(year, getNumByStName(year, st) + 1).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLastSt(int year, String st) {
        try {
            return Json.getDateData(year, getNumByStName(year, st) - 1).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
