package jp.ac.hal.nagoya.nhs70244.list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.Strings;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;

public class detailsActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbHelper;

    private int REQUEST_CODE = 1;
    String strid;
    Integer intid;

    //activity起動時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();
        Integer intid = 0;

        //Intentから値の受け取り
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            strid = extras.getString("id");
            intid = Integer.parseInt(strid);
        }

        //dbアクセス（select）
        Cursor dbrow;
        dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","tag","bookimg","str"},"id like ? ",new String[]{""+intid+""},null,null,null);
        dbrow.moveToFirst();

        //画像出力
        byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
        Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
        ImageView imbook = (ImageView)findViewById(R.id.imageView);
        imbook.setImageBitmap(bitimage);

        //タグ、シリーズの取得
        String strSer = dbrow.getString(dbrow.getColumnIndex("series"));
        String strTag = dbrow.getString(dbrow.getColumnIndex("tag"));
        Integer intSer = Integer.parseInt(strSer);
        Integer intTag = Integer.parseInt(strTag);

        Cursor dbtag;
        dbtag = mydb.query("t_tag",new String[]{"id","name"},"id like ? ",new String[]{""+intTag+""},null,null,null);
        dbtag.moveToFirst();
        Cursor dbseri;
        dbseri = mydb.query("t_series",new String[]{"id","name"},"id like ? ",new String[]{""+intSer+""},null,null,null);
        dbseri.moveToFirst();

        //各種値の出力
        TextView tvTitle = (TextView) findViewById(R.id.txtTitle) ;
        TextView tvAur = (TextView)findViewById(R.id.txtAut);
        TextView tvPubli = (TextView)findViewById(R.id.txtPubli);
        TextView tvSer = (TextView)findViewById(R.id.txtSeri);
        TextView tvTag = (TextView)findViewById(R.id.txtTag);
        tvTitle.setMaxWidth(330);
        tvTitle.setMaxLines(1);
        tvAur.setMaxWidth(330);
        tvAur.setMaxLines(1);
        tvSer.setMaxWidth(150);
        tvSer.setMaxLines(1);
        tvTag.setMaxWidth(150);
        tvTag.setMaxLines(1);
        String strtitel = dbrow.getString(dbrow.getColumnIndex("title"));
        Integer con = strtitel.length();
        if (con > 15){
            strtitel.substring(0,15);
            strtitel= strtitel + "...";
        }
        //tvTitle.setText(strtitel + "...");
        tvTitle.setText(strtitel);
        tvAur.setText(dbrow.getString(dbrow.getColumnIndex("author")));
        tvPubli.setText(dbrow.getString(dbrow.getColumnIndex("publication")));
        tvSer.setText(dbseri.getString(dbseri.getColumnIndex("name")));
        tvTag.setText(dbtag.getString(dbtag.getColumnIndex("name")));
    }

    //書籍情報編集
    public void btnEditClick(View v){
        Intent intentEdi = new Intent(detailsActivity.this,EditActivity.class);

        intentEdi.putExtra("id",String.valueOf(strid));

       // startActivity(intentEdi);
        //SubActivityの開始
        startActivityForResult(intentEdi,REQUEST_CODE);

    }

    //削除しますよ
    public void btnDelClick(View v){

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("この書籍データを削除しますよ？")
                .setPositiveButton("削除する", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                    // ボタンをクリックしたときの動作

                        //Intentから値の受け取り
                        Bundle extras = getIntent().getExtras();
                        strid = extras.getString("id");
                        intid = Integer.parseInt(strid);

                        String strSQL =  "";
                        strSQL = " delete from t_book where id = "+intid;
                        mydb.execSQL(strSQL);

                        Toast.makeText(getApplicationContext(),"削除しました",Toast.LENGTH_LONG).show();


                        //Intentの生成
                        Intent intent = new Intent(detailsActivity.this,MainActivity.class);
                        //intentに値を設定
                        intent.putExtra("","");

                        REQUEST_CODE = 2;
                        //書籍情報が変更されている
                        setResult(Activity.RESULT_OK,intent);

                        //終了
                        finish();
                    }
                });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 何もしないで閉じる
            }
        });
        builder.show();

    }

    //戻ります
    public void btnEndClick(View v){
        //値をMainに渡す(Intent)
        //Intentの生成
        Intent intent = new Intent(detailsActivity.this,MainActivity.class);
        //intentに値を設定
        intent.putExtra("","");

        if (REQUEST_CODE == 1){
            //書籍情報が変更されていない
            //返り
            setResult(Activity.RESULT_CANCELED,intent);
        }else if (REQUEST_CODE == 2){
            //書籍情報が変更されている
            setResult(Activity.RESULT_OK,intent);
        }

        //終了
        finish();
    }

    //SubActivityから戻ってきたときの処理(onActivityResult)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK ) {

            String strid = data.getStringExtra("id");
            Integer intid = Integer.parseInt(strid);

            Cursor dbrow;
            dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","tag","str"},"id like ? ",new String[]{""+intid+""},null,null,null);
            dbrow.moveToFirst();

            TextView tvTitle = (TextView) findViewById(R.id.txtTitle) ;
            TextView tvAur = (TextView)findViewById(R.id.txtAut);
            TextView tvPubli = (TextView)findViewById(R.id.txtPubli);
            TextView tvSer = (TextView)findViewById(R.id.txtSeri);
            TextView tvTag = (TextView)findViewById(R.id.txtTag);

            String strSer = dbrow.getString(dbrow.getColumnIndex("series"));
            String strTag = dbrow.getString(dbrow.getColumnIndex("tag"));
            Integer intSer = Integer.parseInt(strSer);
            Integer intTag = Integer.parseInt(strTag);

            Cursor dbtag;
            dbtag = mydb.query("t_tag",new String[]{"id","name"},"id like ? ",new String[]{""+intTag+""},null,null,null);
            dbtag.moveToFirst();
            Cursor dbseri;
            dbseri = mydb.query("t_series",new String[]{"id","name"},"id like ? ",new String[]{""+intSer+""},null,null,null);
            dbseri.moveToFirst();

            tvTitle.setText(dbrow.getString(dbrow.getColumnIndex("title")));
            tvAur.setText(dbrow.getString(dbrow.getColumnIndex("author")));
            tvPubli.setText(dbrow.getString(dbrow.getColumnIndex("publication")));
            tvSer.setText(dbseri.getString(dbseri.getColumnIndex("name")));
            tvTag.setText(dbtag.getString(dbtag.getColumnIndex("name")));
            REQUEST_CODE = 2;
        } else if (resultCode == RESULT_CANCELED) {

        }

    }

}
