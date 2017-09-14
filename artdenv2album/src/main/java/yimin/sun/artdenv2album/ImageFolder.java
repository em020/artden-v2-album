package yimin.sun.artdenv2album;

import java.util.ArrayList;

/**
 * Created by gmm on 2017/2/5.
 */

public class ImageFolder {
    private String name;
    private String path;
    private String albumPath;
    private ArrayList<Image> images = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o != null && o instanceof ImageFolder) {
//            if (((ImageFolder) o).getPath() == null && path != null)
//                return false;
//            String oPath = ((ImageFolder) o).getPath().toLowerCase();
//            return oPath.equals(this.path.toLowerCase());
//        }
//        return false;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFolder that = (ImageFolder) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
