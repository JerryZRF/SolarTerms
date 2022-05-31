package cf.jerryzrf.solarterms.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author JerryZRF
 */
public final class User {
    InputStream is;
    OutputStream os;
    public User(Socket s) throws IOException {
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    public void receive() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while (true) {
                process(reader.readLine());
            }
        } catch (IOException e) {
            if ("Connection reset".equalsIgnoreCase(e.getMessage())) {
                System.out.println("客户端断开连接");
                return;
            }
            e.printStackTrace();
        }
    }

    private void process(String data) {
        JSONObject json = JSONObject.parseObject(new String(Base64.getDecoder().decode(data)));
        switch (json.getString("type")) {
            case "up":
                JSONObject result = new JSONObject();
                json.put("code", uploadPhoto(json));
                PrintWriter pw = new PrintWriter(os, true);
                pw.println(Base64.getEncoder().encodeToString(JSONObject.toJSONBytes(result)));
                break;
            case "down":
                downloadPhotos(json);
                break;
            default:
        }
    }

    private int uploadPhoto(JSONObject json) {
        try {
            byte[] data = Base64.getDecoder().decode(json.getString("photo"));
            File outFile = new File("./cache" + json.getString("st") + "/" +
                            getSha1(data));
            if (outFile.exists()) {
                return 2;  //文件已存在
            }
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
            dos.write(data);
            return 0;  //上传成功
        } catch (Exception e) {
            e.printStackTrace();
            return 3;  //服务端错误
        }
    }

    private void downloadPhotos(JSONObject json) {
        String st = json.getString("st");
        File[] filesArray = (new File("./cache/" + st)).listFiles();
        List<String> photoList = new ArrayList<>();
        JSONArray array = json.getJSONArray("local");
        for (File f : filesArray) {
            if (!array.contains(f.getName())) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    photoList.add(Base64.getEncoder().encodeToString(fis.readAllBytes()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();
        resultArray.addAll(photoList.subList(0, json.getIntValue("num")));
        result.put("data", resultArray);
        PrintWriter pw = new PrintWriter(os, true);
        pw.println(Base64.getEncoder().encodeToString(JSONObject.toJSONBytes(result)));
    }

    public void onDisable() {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSha1(byte[] input) {
        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] result = mDigest.digest(input);
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
