package cf.jerryzrf.solarterms.client;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author JerryZRF
 */
public final class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于");
        ((TextView) findViewById(R.id.authors)).setText("思路提供：张任飞 张焱 顾君昊\n程序制作：张任飞\nUI设计： 杜思睿 张霆轩 张焱\n资源收集： 张任飞 张焱 张霆轩 杜思睿 王子谦\n审核：顾君昊 张焱 张霆轩 杜思睿 王子谦 周杰");
    }
}

