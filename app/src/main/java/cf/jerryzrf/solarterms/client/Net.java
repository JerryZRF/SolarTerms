package cf.jerryzrf.solarterms.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @author JerryZRF
 */
public class Net {
    public static final String SERVER_ADDRESS = "fd00:6868:6868::11b";

    static Socket socket;
    static BufferedReader reader;
    static PrintWriter writer;

    public static void connect(Activity activity) throws IOException {
        socket = new Socket(SERVER_ADDRESS, 20220);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        try {
            JSONObject result = new JSONObject(reader.readLine());
            if (result.getInt("code") == 1) {
                error(activity, "你已被该服务器禁封");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void error(Activity activity, String message) {
        activity.runOnUiThread(() -> {
            AlertDialog errorDialog = new AlertDialog.Builder(activity)
                    .setTitle("连接服务器失败")
                    .setMessage(message)
                    .setIcon(R.mipmap.ic_launcher)
                    .create();
            errorDialog.show();
        });
    }

    public static void uploadPhotos(Context context, List<Uri> uri, String st) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        uri.forEach(u -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(u));
                byte[] photoBytes = compressImage(bitmap);
                System.out.println("data:" + Base64.getEncoder().encodeToString(photoBytes));
                array.put(Base64.getEncoder().encodeToString(photoBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            json.put("code", "0");
            json.put("photos", array);
            json.put("st", st);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writer.println(json);
        System.out.println("发送成功");
    }

    private static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里压缩options%，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100 && options >= 10) {  //循环判断如果压缩后图片是否大于100Kb,大于继续压缩
            baos.reset();
            // 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        return baos.toByteArray();
    }
}
