package yimin.sun.artdenv2album;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yimin.sun.statusbarfucker.StatusBarFucker;

/**
 * 注意必须要先初始化fresco，初始化代码参考app module中MainActivity
 */
public class AlbumActivity extends AppCompatActivity {


    private Activity activity = this;

    private View titleButton;
    private View headerView;
    private TextView tvTitle;
    private ImageView imgDown;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private TextView tvProgress;
    private TextView tvDialogButton;
    private TextView tvMessage;
    private View loadingContainer;

    private AlbumPhotoAdapter adapter;
    private List<ImageFolder> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);

        StatusBarFucker fucker = new StatusBarFucker();
        fucker.setWindowExtend(1);
        fucker.setStatusBarColor(Color.TRANSPARENT);
        fucker.setUseDarkNotiIcon(true);
        fucker.fuck(getWindow());

        View back = findViewById(R.id.appheader_img_back);
        titleButton = findViewById(R.id.ll_button);
        tvTitle = (TextView) findViewById(R.id.appheader_txt_title);
        imgDown = (ImageView) findViewById(R.id.img_down);
        TextView tvRightButton = (TextView) findViewById(R.id.appheader_txtbtn_right);
        headerView = findViewById(R.id.appheader_view);

        tvRightButton.setText(android.R.string.ok);

        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpWindow();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = adapter.getImgFilePath();
                if (path == null) {
                    Toast.makeText(activity, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("result_path", path);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        // ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(new MyItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setWillNotDraw(false);//为了显示滚条
        recyclerView.addItemDecoration(new SpaceItemDecoration(5));  //添加空隙
        adapter = new AlbumPhotoAdapter();
        recyclerView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            scanImages();
        }



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户已授权
                scanImages();
            } else {
                // 用户未授权
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("提示");
                b.setMessage("请允许ARTDEN访问您的存储空间以获取照片");
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                b.create().show();
            }


        }
    }

    private void scanImages() {


        //搜索图片
        ImageScanner imageScanner = new ImageScanner();
        //成功回调
        imageScanner.setScanImageFinish(new ImpScanImageFinish() {
            @Override
            public void onFinish(List<ImageFolder> imageFolders) {
                titleButton.setClickable(imageFolders != null);
                imgDown.setVisibility(imageFolders == null ? View.GONE : View.VISIBLE);
                if (imageFolders == null) {
                    tvTitle.setText("暂无图片");
                    adapter.data.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }

                data.addAll(imageFolders);
                tvTitle.setText(imageFolders.get(0).getName());
                adapter.setImgFiles(imageFolders.get(0).getImages());
                adapter.notifyDataSetChanged();

            }

        });
        //开始搜索
        imageScanner.startScanImage(this, getSupportLoaderManager());
    }



    private void showPopUpWindow() {

        if (popupWindow == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_albumlist, null);
            RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setHasFixedSize(true);
            recycler.setWillNotDraw(false);//为了显示滚条
            recycler.setAdapter(new AlbumAdapter());
            popupWindow = new PopupWindow(view, -1, -1);
            popupWindow.setHeight(recyclerView.getHeight());
            popupWindow.setWidth(recyclerView.getWidth());

            view.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }

        popupWindow.setFocusable(true); // default is false
        popupWindow.setTouchable(true); // default is true
        popupWindow.setOutsideTouchable(true); // default is false
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.Popupwindow_album);
        popupWindow.showAsDropDown(headerView);
        //动画
        final int dura = 300;
        imgDown.animate().rotation(180).setDuration(dura).setInterpolator(new DecelerateInterpolator()).start();

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imgDown.animate().rotation(0).setDuration(dura).setInterpolator(new DecelerateInterpolator()).start();
            }
        });
    }





    private class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_folder, parent, false);
            return new FolderItemHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            FolderItemHolder hold = (FolderItemHolder) holder;

            hold.tvAlbum.setText(data.get(position).getName());
            hold.tvImageNum.setText(String.valueOf(data.get(position).getImages().size()));

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class FolderItemHolder extends RecyclerView.ViewHolder {

            View line;
            TextView tvAlbum;
            TextView tvImageNum;

            FolderItemHolder(View itemView) {

                super(itemView);
                line = itemView.findViewById(R.id.line);
                tvAlbum = (TextView) itemView.findViewById(R.id.tv_album_name);
                tvImageNum = (TextView) itemView.findViewById(R.id.tv_image_num);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getLayoutPosition();
                        tvTitle.setText(data.get(position).getName());
                        adapter.setImgFiles(data.get(position).getImages());
                        adapter.resetSelect();
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(0);
                        popupWindow.dismiss();
                    }
                });
            }
        }
    }


    private static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) % 3 == 0) {
                outRect.left = 0;
            }
        }

    }


    private class MyItemAnimator extends DefaultItemAnimator {
        @Override
        public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {

            View view = newHolder.itemView;
            ImageView imgMask = (ImageView) view.findViewById(R.id.image_selection_mask);

            if (imgMask.getAlpha() == 0f) {
                imgMask.setAlpha(1f);
                imgMask.animate().alpha(0f).setDuration(120).start();
            } else {
                imgMask.setAlpha(0f);
                imgMask.animate().alpha(1f).setDuration(120).start();
            }

            return true;
        }
    }
}
