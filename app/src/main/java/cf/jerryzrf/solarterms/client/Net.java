package cf.jerryzrf.solarterms.client;

import android.annotation.SuppressLint;
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
    @SuppressLint("StaticFieldLeak")
    static Activity activity;

    public static void connect(Activity a) {
        activity = a;
        try {
            socket = new Socket(SERVER_ADDRESS, 20220);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (Exception e) {
            e.printStackTrace();
            dialog(activity, "无法连接到服务器", e.getMessage());
        }
        (new Thread(Net::receive)).start();
        activity.findViewById(R.id.addPhotos).setEnabled(true);
    }

    private static void receive() {
        try {
            while (true) {
                process(reader.readLine());
            }
        } catch (Exception e) {
            dialog(activity, "无法连接到服务器", "与服务器断开连接");
            activity.findViewById(R.id.addPhotos).setEnabled(false);
            onDisable();
        }
    }

    private static void process(String data) {
        try {
            JSONObject json = new JSONObject(data);
            switch (json.getInt("code")) {
                case 0: {
                    dialog(activity, "已连接到服务器", "连接成功");
                    break;
                }
                case 1: {
                    dialog(activity, "无法连接到服务器", "你已被该服务器禁封");
                    activity.findViewById(R.id.addPhotos).setEnabled(true);
                    break;
                }
                case 2: {
                    dialog(activity, "上传成功", "待审核通过，您的图片将会被展示于此~");
                    break;
                }
                case 3: {
                    dialog(activity, "上传失败", "服务器错误");
                    break;
                }
                case 4: {
                    break;
                }
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void dialog(Activity activity, String title, String message) {
        activity.runOnUiThread(() -> {
            AlertDialog errorDialog = new AlertDialog.Builder(activity)
                    .setTitle(title)
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
                array.put(Base64.getEncoder().encodeToString(photoBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            json.put("code", "0");
            json.put("photos", array);
            json.put("st", st);
            writer.println(json);
        } catch (Exception e) {
            e.printStackTrace();
            dialog(activity, "上传失败", e.getMessage());
        }
    }

    private static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 90;
        //<=100KB
        while (baos.toByteArray().length / 1024 > 100 && options >= 10) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        return baos.toByteArray();
    }

    public static void onDisable() {
        activity = null;
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
