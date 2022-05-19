package cf.jerryzrf.solarterms;

import android.content.res.AssetManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author JerryZRF
 */
public class Json {
    static JSONObject object;

    public static void init(AssetManager assetManager) {
        try {
            InputStream is = assetManager.open("solar_terms.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonStr = new StringBuilder();
            String tmp;
            while ((tmp = reader.readLine()) != null) {
                jsonStr.append(tmp);
            }
            object = new JSONObject(jsonStr.toString());
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 起始支持年份 */
    private final static int START_YEAR = 2022;
    /** 终止支持年份 */
    private final static int END_YEAR = 2025;

    public static JSONObject getObject(int year, int st) throws JSONException, IllegalArgumentException {
        if (!(year >= START_YEAR && year <= END_YEAR)) {
            throw new IllegalArgumentException("仅支持2022~2025使用");
        }
        return object.getJSONArray(String.valueOf(year)).getJSONObject(st);
    }
}
