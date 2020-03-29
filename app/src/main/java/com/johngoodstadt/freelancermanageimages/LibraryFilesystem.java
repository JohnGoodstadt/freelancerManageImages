package com.johngoodstadt.freelancermanageimages;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LibraryFilesystem {

    public static String GROUP_PREFIX = "depot";
    public static String LARGE_SUFFIX = "large";

    public static String getDocumentsDirectoryString() {
        File cacheDirectory = MyApplication.getAppContext().getExternalFilesDir(null);
        if (cacheDirectory == null) {
            return "";
        }

        return cacheDirectory.toString();
    }

    public static Uri getUriFromFilename(String filename) {

        String path = getDocumentsDirectoryString() + "/" + filename;


        File imgFile = new  File(path);
        if(imgFile.exists())
        {
            return Uri.fromFile(imgFile);

        }


        return null;
    }
    public static void writeImageFileToFileSystemFiles(Bitmap btmp, String fName) {

        File fImageThumb = new File(MyApplication.getAppContext().getExternalFilesDir(""), fName);
        try {
            fImageThumb.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            btmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(fImageThumb);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int getCountOfPhotoScorePages(String UID) {
        ArrayList<String> myData = new ArrayList<String>();
        String path = getDocumentsDirectoryString();
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return 0;
        }
        String startString = GROUP_PREFIX + "_" + UID + "_" + LARGE_SUFFIX + "_";
        String endString = ".png";
        String[] files = directory.list();
        if (files == null)
            return 0;
        for (int i = 0; i < files.length; i++) {
            String strChk = files[i];
            if (strChk.startsWith(startString) && strChk.endsWith(endString)) {
                myData.add(files[i]);
            }
        }
        return myData.size();

    }
    public static String getFileNameByUID(String UID, String index) {

        return GROUP_PREFIX + "_" + UID + "_" + LARGE_SUFFIX + "_" + index + ".png";

    }
    public static void renameFile(String UID, String oldindex, String newindex) {

        String from = getFileNameByUID(UID,oldindex);
        String to = getFileNameByUID(UID,newindex);

        Log.i("java","from:" + from + " to:" + to);

        renameFile(from,to);


    }
    public static boolean removeFile(String fileName) {
        try {
            File dir = MyApplication.getAppContext().getExternalFilesDir(null);
            if (dir != null && dir.isDirectory()) {
                //String[] children = dir.list();
                // for (int i = 0; i < children.length; i++) {
                // boolean success = deleteDir(new File(dir, children[i]));
                boolean success = deleteDir(new File(dir, fileName));
                if (!success) {
                    return false;
                }
                // }
                return dir.delete();
            } else if (dir != null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }


        } catch (Exception e) {
            Log.e("", "" + e);
        }
        return false;

    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    public static void renameFile(String strSrc, String strDst) {
        try {
            File ff1 = new File(MyApplication.getAppContext().getExternalFilesDir(null), strSrc);
            File ff2 = new File(MyApplication.getAppContext().getExternalFilesDir(null), strDst);
            ff1.renameTo(ff2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
