package cf.jerryzrf.solarterms.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * @author JerryZRF
 */
public final class Main {
    private static final LinkedList<User> users = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("开始监听20220端口");
        listener();
    }

    private static void listener() {
        try (ServerSocket ss = new ServerSocket(20220)){
            new Thread(() -> {
                while (true) {
                    try {
                        users.add(new User(ss.accept()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void onDisable() {
        users.forEach(user -> user.onDisable());
    }
}
