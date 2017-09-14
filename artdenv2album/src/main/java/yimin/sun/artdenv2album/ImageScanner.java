package yimin.sun.artdenv2album;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmm on 2016/10/18.
 */

public class ImageScanner {

    /**
     * Loader的唯一ID号
     */
    private final static int IMAGE_LOADER_ID = 1000;
    /**
     * 加载数据的映射
     */
    private final static String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    };
    private ImpScanImageFinish scanImageFinish;

    public void setScanImageFinish(ImpScanImageFinish scanImageFinish) {
        this.scanImageFinish = scanImageFinish;
    }

    public void startScanImage(final Context context, LoaderManager loaderManager) {
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                CursorLoader imageCursorLoader = new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION, null, null, /*MediaStore.Images.Media.DEFAULT_SORT_ORDER*/IMAGE_PROJECTION[2] + " DESC");
                return imageCursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data == null || data.getCount() == 0) {
                    scanImageFinish.onFinish(null);
                } else {

                    final ArrayList<Image> images = new ArrayList<>();
                    final List<ImageFolder> imageFolders = new ArrayList<>();

                    final ImageFolder defaultFolder = new ImageFolder();
                    defaultFolder.setName("全部照片");
                    defaultFolder.setPath("");
                    imageFolders.add(defaultFolder);


                    while (data.moveToNext()) {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setFolderName(bucket);

                        images.add(image);

                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        if (!imageFolders.contains(folder)) {
                            folder.getImages().add(image);
                            folder.setAlbumPath(image.getPath());//默认相册封面
                            imageFolders.add(folder);
                        } else {
                            // 更新
                            ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                            f.getImages().add(image);
                        }


                    }

                    defaultFolder.getImages().addAll(images);
                    scanImageFinish.onFinish(imageFolders);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };

        loaderManager.initLoader(IMAGE_LOADER_ID, null, loaderCallbacks);

    }
}
