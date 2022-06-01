package cf.jerryzrf.solarterms.client;

import android.content.res.AssetManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Random;

/**
 * @author JerryZRF
 */
public final class Json {
    public static JSONObject mainData;
    static JSONObject poemsData;
    static JSONObject dateData;
    static JSONObject config;

    public static void init(AssetManager assetManager, File dataFolder) {
        try {
            mainData = loadData(assetManager, "solar_terms.json");
            poemsData = loadData(assetManager, "poems.json");
            dateData = loadData(assetManager, "date.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 起始支持年份
     */
    private final static int START_YEAR = 2022;
    /**
     * 终止支持年份
     */
    private final static int END_YEAR = 2025;

    public static JSONObject getMainData(String st) throws JSONException {
        return mainData.getJSONObject(st);
    }

    public static String getPoemsData(String st) throws JSONException {
        JSONArray array = poemsData.getJSONArray(st);
        int i = (new Random()).nextInt(array.length());
        return (String) array.get(i);
    }

    public static JSONObject getDateData(int year, int st) throws JSONException {
        if (!(year >= START_YEAR && year <= END_YEAR)) {
            throw new IllegalArgumentException("仅支持2022~2025使用");
        }
        return dateData.getJSONArray(String.valueOf(year)).getJSONObject(st);
    }

    private static JSONObject loadData(AssetManager assetManager, String fileName) throws IOException, JSONException {
        InputStream is = assetManager.open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder jsonStr = new StringBuilder();
        String tmp;
        while ((tmp = reader.readLine()) != null) {
            jsonStr.append(tmp);
        }
        return new JSONObject(jsonStr.toString());
    }
}
