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
    static JSONObject cnDateData;

    public static void init(AssetManager assetManager, File dataFolder) {
        try {
            mainData = loadData(assetManager, "solar_terms.json");
            poemsData = loadData(assetManager, "poems.json");
            dateData = loadData(assetManager, "date.json");
            cnDateData = loadData(assetManager, "date_cn.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getMainData(String st) throws JSONException {
        return mainData.getJSONObject(st);
    }

    public static String getPoemsData(String st) throws JSONException {
        JSONArray array = poemsData.getJSONArray(st);
        int i = (new Random()).nextInt(array.length());
        return (String) array.get(i);
    }

    public static JSONObject getDateData(int year, int st) {
        try {
            return dateData.getJSONArray(String.valueOf(year)).getJSONObject(st);
        } catch (JSONException e) {
            throw new IllegalArgumentException("仅支持2022~2025使用");
        }
    }

    public static JSONObject getCnDateData(String year, int st) {
        try {
            return cnDateData.getJSONArray(year).getJSONObject(st);
        } catch (JSONException e) {
            throw new IllegalArgumentException("仅支持壬寅年~乙巳年使用");
        }
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
