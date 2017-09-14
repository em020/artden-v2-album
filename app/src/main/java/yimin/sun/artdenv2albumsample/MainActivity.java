package yimin.sun.artdenv2albumsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

import yimin.sun.artdenv2album.AlbumActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFresco();
    }

    private void initFresco() {

        ImagePipelineConfig.Builder builder = ImagePipelineConfig.newBuilder(this);
        builder.setDownsampleEnabled(true);

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this).setMaxCacheSize(128 * ByteConstants.MB).build();
        builder.setMainDiskCacheConfig(diskCacheConfig);

        if (false) {
            //log查看
            Set<RequestListener> requestListeners = new HashSet<>();
            requestListeners.add(new RequestLoggingListener());
            builder.setRequestListeners(requestListeners);
        }

        ImagePipelineConfig config = builder.build();
        Fresco.initialize(this, config);

        if (false) {
            FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        }
    }

    public void onClickPickImage(View view) {
        startActivityForResult(new Intent(this, AlbumActivity.class), 1000);
    }
}
