package cf.jerryzrf.solarterms.server;

import com.alibaba.fastjson.JSONArray;

import java.io.*;
import java.net.ServerSocket;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author JerryZRF
 */
public final class Main {
    public static final Set<User> users = new LinkedHashSet<>();
    public static void main(String[] args) {
        System.out.println("开始监听20220端口");
        listener();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::onDisable));
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.startsWith("/ban")) {
                String ip = command.substring(5);
                if (ban(ip)) {
                    System.out.println("已封禁ip为" + ip + "的用户");
                } else {
                    System.out.println("封禁失败");
                }
            }
            if (command.startsWith("/free")) {
                String ip = command.substring(6);
                if (free(ip)) {
                    System.out.println("已解封ip为" + ip + "的用户");
                } else {
                    System.out.println("解封失败");
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
        users.clear();
    }

    private static boolean ban(String ip) {
        if (checkIp(ip)) {
            System.out.println("ip格式错误！");
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
            if (array.contains(ip)) {
                System.out.println("该用户已被封禁");
                return false;
            }
            array.add(ip);
            PrintWriter writer = new PrintWriter(new FileOutputStream("./ban.json"));
            writer.println(array.toJSONString());
            writer.close();
            users.forEach(user -> {
                if (user.socket.getInetAddress().getHostAddress().equals(ip)) {
                    user.onDisable();
                }
            });
        } catch (Exception e) {
            System.out.println("未知错误：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean free(String ip) {
        if (checkIp(ip)) {
            System.out.println("ip格式错误！");
            return false;
        }
        try {
            File jsonFile = new File("./ban.json");
            JSONArray array;
            if (jsonFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
                array = JSONArray.parseArray(reader.readLine());
                reader.close();
            } else {
                System.out.println("未封禁任何用户！");
                return false;
            }
            if (!array.remove(ip)) {
                System.out.println("未封禁该用户！");
                return false;
            }
            PrintWriter writer = new PrintWriter(new FileOutputStream("./ban.json"));
            writer.println(array.toJSONString());
            writer.close();
        } catch (Exception e) {
            System.out.println("未知错误：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean checkIp(String ip) {
        return !Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ip)
                && !Pattern.matches("(^((([\\dA-Fa-f]{1,4}:){7}(([\\dA-Fa-f]{1,4})|:))"
                + "|(([\\dA-Fa-f]{1,4}:){6}((:[\\dA-Fa-f]{1,4})|"
                + "((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})){3})|:))|"
                + "(([\\dA-Fa-f]{1,4}:){5}((:[\\dA-Fa-f]{1,4}){1,2}|"
                + ":((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})){3})|:))|"
                + "(([\\dA-Fa-f]{1,4}:){4}((:[\\dA-Fa-f]{1,4}){1,3}"
                + "|:((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})){3})|:))|(([\\dA-Fa-f]{1,4}:){3}((:[\\dA-Fa-f]{1,4}){1,4}|"
                + ":((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})){3})|:))|"
                + "(([\\dA-Fa-f]{1,4}:){2}((:[\\dA-Fa-f]{1,4}){1,5}|"
                + ":((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})){3})|:))"
                + "|(([\\dA-Fa-f]{1,4}:)((:[\\dA-Fa-f]{1,4}){1,6}"
                + "|:((22[0-3]|2[0-1]\\d|[0-1]\\d\\d|"
                + "(\\d){1,2})([.](25[0-5]|2[0-4]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})){3})|:))|"
                + "(:((:[\\dA-Fa-f]{1,4}){1,7}|(:[fF]{4})?:((22[0-3]|2[0-1]\\d|"
                + "[0-1]\\d\\d|(\\d){1,2})"
                + "([.](25[0-5]|2[0-4]\\d|[0-1]\\d\\d|(\\d){1,2})){3})|:)))$)", ip);
    }
}
