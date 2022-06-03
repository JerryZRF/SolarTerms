package cf.jerryzrf.solarterms.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author JerryZRF
 */
public final class User {
    PrintWriter writer;
    BufferedReader reader;
    Socket socket;

    Map<String, List<String>> notUpdated = new HashMap<>();  //客户端没有的图片

    public User(Socket s) throws IOException {
        System.out.println("有客户端连接，ip:" + s.getInetAddress().getHostAddress());
        socket = s;
        writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
        reader = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        if (!init()) {
            System.out.println("ip为" + s.getInetAddress().getHostAddress() + "的客户端被阻止连接");
            return;
        }
        (new Thread(this::receive)).start();
    }

    public static String getSha1(byte[] source) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(source);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public void receive() {
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

    private boolean init() {
        JSONObject resultJson = new JSONObject();
        File banFile = new File("./ban.json");
        resultJson.put("code", 0);
        if (banFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(banFile));
                JSONArray array = JSONArray.parseArray(reader.readLine());
                if (array.contains(socket.getInetAddress().getHostAddress())) {
                    resultJson.put("code", 1);
                    writer.println(JSONObject.toJSONString(resultJson));
                    onDisable();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writer.println(JSONObject.toJSONString(resultJson));
        return true;
    }

    private void process(String data) {
        System.out.println("接收到数据");
        JSONObject json = JSONObject.parseObject(data);
        switch (json.getIntValue("code")) {
            case 0 -> {
                JSONObject result = new JSONObject();
                result.put("code", uploadPhotos(json));
                writer.println(JSONObject.toJSONString(result));
            }
            case 1 -> downloadPhotos(json);
            default -> {
            }
        }
    }

    private int uploadPhotos(JSONObject json) {
        try {
            for (Object basePhoto : json.getJSONArray("photos")) {
                byte[] data = Base64.getDecoder().decode((String) basePhoto);
                File outFile = new File("./cache/" + json.getString("st") + "/" + getSha1(data) + ".jpeg");
                System.out.println("已保存为" + outFile.getPath());
                if (outFile.exists()) {
                    return 2;  //文件已存在
                } else {
                    outFile.createNewFile();
                }
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
                dos.write(data);
                dos.close();
                System.out.println("收到来自用户" + socket.getInetAddress().getHostAddress() + "的一张图片");
                return 0;  //上传成功
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 3;  //服务端错误
    }

    private void downloadPhotos(JSONObject json) {
        String st = json.getString("st");
        File[] filesArray = (new File("./cache/" + st)).listFiles();
        JSONArray array = json.getJSONArray("local");
        if (!notUpdated.containsKey(st)) {
            List<String> notUpdatedList = new ArrayList<>();
            for (File f : filesArray) {
                if (!array.contains(f.getName())) {
                    try (FileInputStream fis = new FileInputStream(f)) {
                        notUpdatedList.add(Base64.getEncoder().encodeToString(fis.readAllBytes()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            notUpdated.put(st, notUpdatedList);
        }
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();
        resultArray.addAll(notUpdated.get(st).subList(0, json.getIntValue("num")));
        notUpdated.get(st).removeAll(resultArray);
        result.put("data", resultArray);
        writer.println(JSONObject.toJSONString(result));
    }

    public void onDisable() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

