package cf.jerryzrf.solarterms.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JerryZRF
 */
public final class InfoActivity extends AppCompatActivity {

    int nowId;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_info);
        EasyPhotos.preLoad(this);
        //上传照片
        findViewById(R.id.addPhotos).setVisibility(View.GONE);
        findViewById(R.id.addPhotos).setOnClickListener((View view) -> EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                .setFileProviderAuthority("cf.jerryzrf.solarterms.client")
                .setCount(10)
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        List<Uri> uris = new ArrayList<>();
                        photos.forEach(p -> uris.add(p.uri));
                        (new Thread(() -> Net.uploadPhotos(InfoActivity.this, uris, getIntent().getStringExtra("st")))).start();
                    }

                    @Override
                    public void onCancel() {
                    }
                })
        );
        //左右滑动
        final float[] mPosX = {0};
        TextView content = findViewById(R.id.content);
        content.setLongClickable(true);
        content.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPosX[0] = event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getRawX() - mPosX[0] > 50) {
                        if (nowId != 0) {
                            display(nowId - 1);
                        }
                    } else if (mPosX[0] - event.getRawX() > 50) {
                        if (nowId != 3) {
                            display(nowId + 1);
                        }
                    }
                    BottomNavigationView bnv = findViewById(R.id.nav_view);
                    switch (nowId) {
                        case 0:
                            bnv.setSelectedItemId(R.id.item_introduction);
                            break;
                        case 1:
                            bnv.setSelectedItemId(R.id.item_xisu);
                            break;
                        case 2:
                            bnv.setSelectedItemId(R.id.item_yiji);
                            break;
                        case 3:
                            bnv.setSelectedItemId(R.id.item_photos);
                            break;
                        default:
                    }
                    break;
                default:
            }
            return true;
        });

        try {
            nowId = 0;
            display(0);
            ((BottomNavigationView) findViewById(R.id.nav_view)).setOnItemSelectedListener((item) -> {
                findViewById(R.id.addPhotos).setVisibility(View.GONE);
                switch (item.getItemId()) {
                    case R.id.item_introduction:
                        display(0);
                        break;
                    case R.id.item_xisu:
                        display(1);
                        break;
                    case R.id.item_yiji:
                        display(2);
                        break;
                    case R.id.item_photos:
                        (new Thread(() -> {
                            try {
                                Net.connect(this);
                            } catch (IOException e) {
                                Net.error(this, "未知错误");
                                e.printStackTrace();
                            }
                        })).start();
                        display(3);
                    default:
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.crashByJson(this);
        }
    }

    @SuppressLint("SetTextI18n")
    private void display(int id) {
        String st = getIntent().getStringExtra("st");
        TextView content = findViewById(R.id.content);
        JSONObject object;
        nowId = id;
        TextView title = findViewById(R.id.title_info);
        try {
            object = Json.getMainData(st);
            switch (id) {
                case 0:
                    title.setText(st + "的简介");
                    content.setText(object.getString("introduction"));
                    break;
                case 1:
                    title.setText(st + "的习俗");
                    content.setText(object.getString("xisu"));
                    break;
                case 2:
                    title.setText(st + "的宜忌");
                    content.setText(object.getString("yiji"));
                    break;
                case 3:
                    title.setText(st + "的美图");
                    findViewById(R.id.addPhotos).setVisibility(View.VISIBLE);
                    content.setText(null);
                    break;
                default:
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Glide4.x的加载图片引擎实现,单例模式
     * Glide4.x的缓存机制更加智能，已经达到无需配置的境界。如果使用Glide3.x，需要考虑缓存机制。
     * Created by huan on 2018/1/15.
     */
    public static class GlideEngine implements ImageEngine {
        //单例
        private static volatile GlideEngine instance = null;

        //单例模式，私有构造方法
        private GlideEngine() {
        }

        //获取单例
        public static GlideEngine getInstance() {
            if (null == instance) {
                synchronized (GlideEngine.class) {
                    if (null == instance) {
                        instance = new GlideEngine();
                    }
                }
            }
            return instance;
        }

        /**
         * 加载图片到ImageView
         *
         * @param context   上下文
         * @param uri       图片路径Uri
         * @param imageView 加载到的ImageView
         */
        //安卓10推荐uri，并且path的方式不再可用
        @Override
        public void loadPhoto(@NotNull Context context, @NotNull Uri uri, @NotNull ImageView imageView) {
            Glide.with(context).load(uri).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
        }

        /**
         * 加载gif动图图片到ImageView，gif动图不动
         *
         * @param context   上下文
         * @param gifUri    gif动图路径Uri
         * @param imageView 加载到的ImageView
         *                  <p>
         *                  备注：不支持动图显示的情况下可以不写
         */
        //安卓10推荐uri，并且path的方式不再可用
        @Override
        public void loadGifAsBitmap(@NotNull Context context, @NotNull Uri gifUri, @NotNull ImageView imageView) {
            Glide.with(context).asBitmap().load(gifUri).into(imageView);
        }

        /**
         * 加载gif动图到ImageView，gif动图动
         *
         * @param context   上下文
         * @param gifUri    gif动图路径Uri
         * @param imageView 加载动图的ImageView
         *                  <p>
         *                  备注：不支持动图显示的情况下可以不写
         */
        //安卓10推荐uri，并且path的方式不再可用
        @Override
        public void loadGif(@NotNull Context context, @NotNull Uri gifUri, @NotNull ImageView imageView) {
            Glide.with(context).asGif().load(gifUri).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
        }


        /**
         * 获取图片加载框架中的缓存Bitmap
         *
         * @param context 上下文
         * @param uri     图片路径
         * @param width   图片宽度
         * @param height  图片高度
         * @return Bitmap
         * @throws Exception 异常直接抛出，EasyPhotos内部处理
         */
        //安卓10推荐uri，并且path的方式不再可用
        @Override
        public Bitmap getCacheBitmap(@NotNull Context context, @NotNull Uri uri, int width, int height) throws Exception {
            return Glide.with(context).asBitmap().load(uri).submit(width, height).get();
        }


    }
}