//package com.imagesandwallpaper.bazaar.iwb.utils;
//
//
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//class DownloadImageAsynkTask extends AsyncTask<String, String, String> {
//
//    File sdCardRoot;
//
//    @Override
//    protected String doInBackground(String... strings) {
//
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strings[0]);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setDoOutput(true);
//            urlConnection.connect();
//
//
//            sdCardRoot = new File(Environment.getExternalStorageDirectory(), "MyProfile");
//
//            if (!sdCardRoot.exists()) {
//                sdCardRoot.mkdirs();
//            }
//
//            Log.e("check_path", "" + sdCardRoot.getAbsolutePath());
//
//            String fileName =
//                    strings[0].substring(strings[0].lastIndexOf('/') + 1, strings[0].length());
//            Log.e("dfsdsjhgdjh", "" + fileName);
//            File imgFile =
//                    new File(sdCardRoot, fileName);
//            if (!sdCardRoot.exists()) {
//                imgFile.createNewFile();
//            }
//            InputStream inputStream = urlConnection.getInputStream();
//            int totalSize = urlConnection.getContentLength();
//            FileOutputStream outPut = new FileOutputStream(imgFile);
//            int downloadedSize = 0;
//            byte[] buffer = new byte[2024];
//            int bufferLength = 0;
//            while ((bufferLength = inputStream.read(buffer)) > 0) {
//                outPut.write(buffer, 0, bufferLength);
//                downloadedSize += bufferLength;
//                Log.e("Progress:", "downloadedSize:" + Math.abs(downloadedSize * 100 / totalSize));
//            }
//            Log.e("Progress:", "imgFile.getAbsolutePath():" + imgFile.getAbsolutePath());
//
//            Log.e(TAG, "check image path 2" + imgFile.getAbsolutePath());
//
//            mImageArray.add(imgFile.getAbsolutePath());
//            outPut.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("checkException:-", "" + e);
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        imagecount++;
//        Log.e("check_count", "" + totalimagecount + "==" + imagecount);
//        if (totalimagecount == imagecount) {
//            pDialog.dismiss();
//            imagecount = 0;
//        }
//        Log.e("ffgnjkhjdh", "checkvalue checkvalue" + checkvalue);
//
//
//    }
//
//
//}