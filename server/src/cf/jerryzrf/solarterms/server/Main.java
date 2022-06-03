package cf.jerryzrf.solarterms.server;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author JerryZRF
 */
public final class Main {
    private static final LinkedList<User> users = new LinkedList<>();
    public static void main(String[] args) {
        System.out.println("开始监听20220端口");
        listener();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::onDisable));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.startsWith("/ban")) {
                String ip = command.substring(5);
                if (!banIp(ip)) {
                    System.out.println("错误的ip格式！");
                }
            }
        }
    }

    private static void listener() {
        try {
            ServerSocket ss = new ServerSocket(20220);
            new Thread(() -> {
                while (true) {
                    try {
                        users.add(new User(ss.accept()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onDisable() {
        users.forEach(User::onDisable);
    }

    private static boolean banIp(String ip) {
        if (!Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ip) && !Pattern.matches(
                "(^((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4}){1}|:))"
                        + "|(([0-9A-Fa-f]{1,4}:){6}((:[0-9A-Fa-f]{1,4}){1}|"
                        + "((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                        + "(([0-9A-Fa-f]{1,4}:){5}((:[0-9A-Fa-f]{1,4}){1,2}|"
                        + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                        + "(([0-9A-Fa-f]{1,4}:){4}((:[0-9A-Fa-f]{1,4}){1,3}"
                        + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){3}((:[0-9A-Fa-f]{1,4}){1,4}|"
                        + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                        + "(([0-9A-Fa-f]{1,4}:){2}((:[0-9A-Fa-f]{1,4}){1,5}|"
                        + ":((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))"
                        + "|(([0-9A-Fa-f]{1,4}:){1}((:[0-9A-Fa-f]{1,4}){1,6}"
                        + "|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|"
                        + "([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|"
                        + "(:((:[0-9A-Fa-f]{1,4}){1,7}|(:[fF]{4}){0,1}:((22[0-3]|2[0-1][0-9]|"
                        + "[0-1][0-9][0-9]|([0-9]){1,2})"
                        + "([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:)))$)", ip)) {
            return false;
        }
        try {
            File jsonFile = new File("./ban.json");
            JSONArray array = new JSONArray();
            if (jsonFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                array = JSONArray.parseArray(reader.readLine());
                reader.close();
            } else {
                jsonFile.createNewFile();
            }
            array.add(ip);
            PrintWriter writer = new PrintWriter(new FileOutputStream("./ban.json"));
            writer.println(array.toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
