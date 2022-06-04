package cf.jerryzrf.solarterms.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String getMd5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            return Base64.getEncoder().encodeToString(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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

    public void receive() {
        try {
            while (true) {
                process(reader.readLine());
            }
        } catch (IOException e) {
            onDisable();
        }
    }

    private void process(String data) {
        if (data == null) {
            onDisable();
            return;
        }
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
                System.out.println("收到来自用户" + socket.getInetAddress().getHostAddress() + "的一张图片");
                byte[] data = Base64.getDecoder().decode((String) basePhoto);
                File outFile = new File("./cache/" + json.getString("st") + "/" + getMd5(data) + ".jpeg");
                if (outFile.exists()) {
                    System.out.println("文件重复");
                    continue;
                } else {
                    outFile.createNewFile();
                }
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFile));
                dos.write(data);
                dos.close();
                System.out.println("已保存为" + outFile.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 3;  //服务端错误
        }
        return 2;  //上传成功
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
        result.put("code", 4);
        result.put("data", resultArray);
        writer.println(JSONObject.toJSONString(result));
    }

    public synchronized void onDisable() {
        if (socket.isClosed()) {
            return;
        }
        System.out.println("断开于" + socket.getInetAddress() + "的连接");
        Main.users.remove(this);
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return socket.getInetAddress().getHostAddress().equals(o);
    }

    @Override
    public int hashCode() {
        return socket.getInetAddress().getHostAddress().hashCode();
    }
}

