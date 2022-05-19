package cf.jerryzrf.solarterms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author JerryZRF
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Json.init(getAssets());  //加载json
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        int mid = -1;  //今天的节气（-1表示没有节气在今天）
        Integer next = null;  //下一个节气（当mid==-1时）
        try {
            //找今天是什么节气
            for (int i = 0; i < 24; i++) {
                LocalDate tmp = LocalDate.parse(Json.getObject(date.getYear(), i).getString("date"));
                int stMonth = tmp.getMonthValue();
                int stDay = tmp.getDayOfMonth();
                if (month < stMonth) {
                    next = i;
                    break;
                } else if (month == stMonth) {
                    if (day == stDay) {
                        mid = i;
                        break;
                    } else if (day > stDay) {
                        mid = -1;
                        next = i;
                        break;
                    }
                }
            }
            int finalMid = mid;
            Integer finalNext = next;
            //“今天”按钮
            findViewById(R.id.back).setOnClickListener((View view) -> {
                try {
                    solarTerm(date, finalMid, finalNext - 1);
                } catch (JSONException e) {
                    Utils.crashByJson(this);
                    e.printStackTrace();
                }
            });
            //“关于”按钮
            findViewById(R.id.about).setOnClickListener((View view) -> {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            });
            solarTerm(date, mid, next);
        } catch (Exception e) {
            Utils.crashByJson(this);
            e.printStackTrace();
        }
    }

    /**
     * 在主页面显示指定节气
     * @param date 当前日期
     * @param num 第几个节气
     * @param nextSt 当num==-1时启用，表示今天下一个的节气
     * @throws IllegalArgumentException 当num==-1时，l不应为null
     */
    public void solarTerm(LocalDate date, int num, @Nullable Integer nextSt) throws JSONException, IllegalArgumentException {
        Button lastButton = findViewById(R.id.last);
        Button nextButton = findViewById(R.id.next);
        lastButton.setEnabled(true);
        nextButton.setEnabled(true);
        ((TextView) findViewById(R.id.poem)).setText("");
        ((TextView) findViewById(R.id.reason)).setText("");
        if (num == 0) {
            lastButton.setEnabled(false);
        } else if (num == 23) {
            nextButton.setEnabled(false);
        }
        TextView textView = findViewById(R.id.today);
        if (num == -1) {  //不是节气
            if (nextSt == null) {
                throw new IllegalArgumentException("当num==-1时，l不应为null");
            }
            num = nextSt;
            textView.setTextSize(20);
            textView.setText("没有节气在今天呢");
            findViewById(R.id.back).setEnabled(false);
            ((TextView)findViewById(R.id.textView2)).setText("");
            ((TextView)findViewById(R.id.date)).setText(date.format(DateTimeFormatter.ISO_DATE));
            findViewById(R.id.info).setEnabled(false);
        } else {
            findViewById(R.id.back).setEnabled(true);
            findViewById(R.id.textView2).setVisibility(View.GONE);
            findViewById(R.id.info).setEnabled(true);
            JSONObject object = Json.getObject(date.getYear(), num);
            ((TextView)findViewById(R.id.date)).setText(object.getString("date"));
            try {
                ((TextView) findViewById(R.id.today)).setText(object.getString("name"));
            } catch (JSONException e) {
                Utils.crashByJson(this);
                e.printStackTrace();
            }
            ((TextView) findViewById(R.id.poem)).setText(object.getString("poem"));
            ((TextView) findViewById(R.id.reason)).setText(object.getString("reason"));
            final int finalNum = num;
            //"详细"按钮
            findViewById(R.id.info).setOnClickListener((View view) -> {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, InfoActivity.class);
                intent.putExtra("st", finalNum);
                startActivity(intent);
            });
        }
        int finalNum = num;
        //下一个
        lastButton.setOnClickListener((View view) -> {
            try {
                solarTerm(date, finalNum - 1, null);
            } catch (JSONException e) {
                Utils.crashByJson(this);
                e.printStackTrace();
            }
        });
        //上一个
        nextButton.setOnClickListener((View view) -> {
            try {
                solarTerm(date, finalNum + 1, null);
            } catch (JSONException e) {
                Utils.crashByJson(this);
                e.printStackTrace();
            }
        });
    }
}