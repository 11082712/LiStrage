package jp.ac.hal.nagoya.nhs70244.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Strings;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;

import java.io.IOException;
import java.net.URL;

/**
 * Created by b-show on 2020/02/04.
 */

public class SerchAPI extends AsyncTask<String, String, String> {

    private Listener listener;
    private Activity mainActivity;
    public ProgressDialog m_ProgressDialog;
    private DialogInterface.OnClickListener clickListener;
    private Context cont;

    public SerchAPI(Context cont) {
        this.cont = cont;
    }
    public SerchAPI(Activity activity){
        this.mainActivity = activity;
    }

    public SerchAPI(DialogInterface.OnClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    //実行前処理
    @Override
    protected void onPreExecute() {

        // プログレスダイアログの生成
        this.m_ProgressDialog = new ProgressDialog(mainActivity);

        // プログレスダイアログの設定
        this.m_ProgressDialog.setMessage("読み込み中...");  // メッセージをセット

        // プログレスダイアログの表示
        this.m_ProgressDialog.show();

        return;
    }

    // 非同期処理
    @Override
    protected String doInBackground(String... params) {

        String str[] = {"","",""};
        final Books book = new Books.Builder(new NetHttpTransport(), new AndroidJsonFactory(), null)
                .setBooksRequestInitializer(new BooksRequestInitializer("AIzaSyAcPyhn07nRFTtOvkcExwYGNVh7DYvCEYs"))
                .build();
        String text = params[0];

        try {
            Volumes volumes = book.volumes()
                    .list(text)
                    .setMaxResults((long) 1)
                    .setOrderBy("relevance")
                    .execute();

            Volume.VolumeInfo volumeInfo = volumes.getItems().get(0).getVolumeInfo();

            String tag = "tag";

            if (volumeInfo != null) {
                /*
                //Log.d(tag, "[Book Volume]title:      " + volumeInfo.getTitle());
                //Log.d(tag, "[Book Volume]subTitle:   " + volumeInfo.getSubtitle());
                //Log.d(tag, "[Book Volume]description: " + volumeInfo.getDescription());
                //Log.d(tag, "[Book Volume]thumbnail:   " + volumeInfo.getImageLinks().getThumbnail());
                //str = "[Book Volume]title:      " + volumeInfo.getTitle();
                //params[1] = "[Book Volume]title:      " + volumeInfo.getTitle();
                */
                str[0] = volumeInfo.getTitle().toString();
                str[1] = volumeInfo.getAuthors().toString();
                str[2] = volumeInfo.getImageLinks().getThumbnail();
                publishProgress(str);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //return params[1];
        return str[0];
    }

    // 途中経過をメインスレッドに返す
    @Override
    protected void onProgressUpdate(String... progress) {

        if (listener != null) {
                listener.onSuccess(progress);
        }

    }

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.equals(result);
        }
        // プログレスダイアログを閉じる
        if (this.m_ProgressDialog != null && this.m_ProgressDialog.isShowing()) {
            this.m_ProgressDialog.dismiss();
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String[] count);
    }



}