package yimin.sun.artdenv2album;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmm on 2016/10/18.
 */

public class AlbumPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Image> data = new ArrayList<>();

    private int selectPosition = -1;
    private String imgFilePath;

    public void setImgFiles(List<Image> data) {
        this.data = data;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ablum_photo, parent, false);
        return new ItemPhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        configureItem((ItemPhotoHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void configureItem(ItemPhotoHolder holder, int position) {

        String imgPath = data.get(position).getPath();
        String tag = (String) holder.draweeView.getTag();
        if (tag == null || !imgPath.equals(tag)) {
            showThumb(Uri.fromFile(new File(imgPath)), holder.draweeView);
            holder.draweeView.setTag(imgPath);
        }

        if (selectPosition == position) {
            holder.imgMask.setAlpha(1f);
        } else {
            holder.imgMask.setAlpha(0f);
        }
    }

    private class ItemPhotoHolder extends RecyclerView.ViewHolder{

        ImageView imgMask;
        SimpleDraweeView draweeView;

        ItemPhotoHolder(View itemView) {
            super(itemView);
            imgMask = (ImageView) itemView.findViewById(R.id.image_selection_mask);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.img_photo);
            ViewGroup.LayoutParams params = draweeView.getLayoutParams();
            params.height = itemView.getResources().getDisplayMetrics().widthPixels/3;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickPosition = getLayoutPosition();

                    if (selectPosition == clickPosition) {
                        // 自己本来是高亮的，然后现在点击了自己
                        selectPosition = -1;
                        imgFilePath = null;
                    } else {
                        int oldSelected = selectPosition;
                        selectPosition = clickPosition;
                        imgFilePath = data.get(clickPosition).getPath();
                        notifyItemChanged(oldSelected);
                    }
                    notifyItemChanged(clickPosition);
                }
            });
        }
    }

    public void resetSelect(){
        selectPosition = -1;
    }

    public static void showThumb(Uri uri, SimpleDraweeView draweeView){
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(200, 200))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }
}
