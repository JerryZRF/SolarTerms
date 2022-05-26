package cf.jerryzrf.solarterms;

import android.app.AlertDialog;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.*;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;

/**
 * @author JerryZRF
 * 源码来自https://www.jianshu.com/p/f058bf833af6。做了稍许修改
 */
public class ARCameraActivity extends AppCompatActivity {
    ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        findViewById(R.id.load).setOnClickListener(v -> addObject(Uri.parse("obj.sfb")));
    }

    private void addObject(Uri parse) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        android.graphics.Point point = getScreenCenter();
        if (frame != null) {
            List<HitResult> hits = frame.hitTest(point.x, point.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane)trackable).isPoseInPolygon(hit.getHitPose())) {
                    placeObject(arFragment, hit.createAnchor(), parse);
                    break;
                }
            }
        }

    }

    private void placeObject(ArFragment fragment, Anchor createAnchor, Uri model) {
        ModelRenderable.builder()
                .setSource(fragment.getContext(), model)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(fragment,createAnchor, modelRenderable))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder =new AlertDialog.Builder(ARCameraActivity.this);
                    builder.setMessage(throwable.getMessage()).setTitle("error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;

                }) ;
    }

    private void addNodeToScene(ArFragment fragment, Anchor createAnchor, ModelRenderable renderable) {
        AnchorNode anchorNode =new AnchorNode(createAnchor);
        TransformableNode transformableNode =new TransformableNode(fragment.getTransformationSystem());
        transformableNode.setRenderable(renderable);
        transformableNode.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();

    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(R.id.ux_fragment);
        return new Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }
}
