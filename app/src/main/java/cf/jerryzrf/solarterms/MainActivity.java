package cf.jerryzrf.solarterms;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * @author JerryZRF
 */
public final class MainActivity extends AppCompatActivity {
    String nowSt = null;

    String nextSt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalDate date = LocalDate.now();
        if (nowSt != null) {
            solarTerm(date, nowSt, null);
        }
        findViewById(R.id.gif_view).setVisibility(View.INVISIBLE);
        Json.init(getAssets());  //加载json
        new Thread(this::init).start();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String today = null;  //今天的节气（-1表示没有节气在今天）
        try {
            //找今天是什么节气
            for (int i = 0; i < 24; i++) {
                JSONObject dateConfig = Json.getDateConfig(date.getYear(), i);
                LocalDate tmp = LocalDate.parse(dateConfig.getString("date"));
                int stMonth = tmp.getMonthValue();
                int stDay = tmp.getDayOfMonth();
                if (month < stMonth) {
                    nextSt = dateConfig.getString("name");
                    break;
                } else if (month == stMonth) {
                    if (day == stDay) {
                        today = dateConfig.getString("name");
                        break;
                    } else if (day < stDay) {
                        nextSt = dateConfig.getString("name");
                        break;
                    }
                }
            }

            final float[] mPosX = {0};
            String finalNext = nextSt;
            View video = findViewById(R.id.gif_layout);
            video.setLongClickable(true);
            video.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX[0] = event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        video.performClick();
                        if (event.getRawX() - mPosX[0] > 50) {
                            if (Boolean.FALSE.equals(Utils.isHeadOrTail(LocalDate.now().getYear(), nowSt))) {
                                break;
                            }
                            if (nowSt == null) {
                                solarTerm(date, Utils.getLastSt(date.getYear(), finalNext), null);
                                break;
                            }
                            solarTerm(date, Utils.getLastSt(date.getYear(), nowSt), null);
                        } else if (mPosX[0] - event.getRawX() > 50) {
                            if (Boolean.TRUE.equals(Utils.isHeadOrTail(LocalDate.now().getYear(), nowSt))) {
                                break;
                            }
                            if (nowSt == null) {
                                solarTerm(date, finalNext, null);
                                break;
                            }
                            solarTerm(date, Utils.getNextSt(date.getYear(), nowSt), null);
                        }
                        break;
                    default:
                }
                return true;
            });

            String finalToday = today;
            solarTerm(date, today, nextSt);
        } catch (Exception e) {
            Utils.crashByJson(this);
            e.printStackTrace();
        }
    }

    /**
     * 在主页面显示指定节气
     * @param date 当前日期
     * @param nextSt 当num==-1时启用，表示今天下一个的节气
     * @throws IllegalArgumentException 当num==-1时，l不应为null
     */
    public void solarTerm(LocalDate date, String st, @Nullable String nextSt) throws IllegalArgumentException {
        nowSt = st;
        ((TextView) findViewById(R.id.poem)).setText("");
        ((TextView) findViewById(R.id.reason)).setText("");
        TextView textView = findViewById(R.id.today);
        try {
            if (st == null) {  //不是节气
                if (nextSt == null) {
                    throw new IllegalArgumentException("当num==-1时，l不应为null");
                }
                textView.setTextSize(20);
                textView.setText("没有节气在今天呢");
                ((TextView) findViewById(R.id.textView)).setText("");
                ((TextView) findViewById(R.id.date)).setText(date.format(DateTimeFormatter.ISO_DATE));
                findViewById(R.id.gif_view).setVisibility(View.INVISIBLE);
            } else {
                //背景视频
                VideoView gif = findViewById(R.id.gif_view);
                gif.setVideoPath(getDataDir().getPath() + "/" + st + ".mp4");
                gif.start();
                gif.setVisibility(View.VISIBLE);

                findViewById(R.id.textView).setVisibility(View.GONE);
                JSONObject mainConfig = Json.getMainConfig(st);
                JSONObject dateConfig = Json.getDateConfig(date.getYear(), Utils.getNumByStName(date.getYear(), st));
                ((TextView) findViewById(R.id.date)).setText(dateConfig.getString("date"));
                try {
                    ((TextView) findViewById(R.id.today)).setText(st);
                    ((TextView) findViewById(R.id.poem)).setText(Json.getPoems(st));
                } catch (JSONException e) {
                    Utils.crashByJson(this);
                    e.printStackTrace();
                }
                ((TextView) findViewById(R.id.reason)).setText(mainConfig.getString("reason"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.crashByJson(this);
        }
    }

    public void init() {
        AssetManager assetManager = getAssets();
        for (Iterator<String> it = Json.mainConfig.keys(); it.hasNext(); ) {
            String fileName = it.next();
            File out = new File(this.getDataDir(), fileName + ".mp4");
            if (out.exists()) {
                continue;
            }
            try {
                out.createNewFile();
                Utils.copyFile(assetManager.open("videos/" + fileName + ".mp4"), Files.newOutputStream(out.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
                Utils.crashByJson(this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nowSt != null) {
            solarTerm(LocalDate.now(), nowSt, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //导入菜单布局
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //创建菜单项的点击事件
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_today:
                solarTerm(LocalDate.now(), null, nextSt);
                break;
            case R.id.menu_about:
                intent.setClass(this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_info:
                if (nowSt == null) {
                    Toast.makeText(this, "请在一个节气页面查看详细", Toast.LENGTH_LONG).show();
                    break;
                }
                intent.setClass(this, InfoActivity.class);
                intent.putExtra("st", nowSt);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}