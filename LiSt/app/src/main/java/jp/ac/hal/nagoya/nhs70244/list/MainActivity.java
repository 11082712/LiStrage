package jp.ac.hal.nagoya.nhs70244.list;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbHelper;
    private int REQUEST_CODE = 0;

    //起動
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //データベース取得
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        //データベース読み込み
        Cursor dbrow;
        dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","bookimg","tag","str"},null,null,null,null,"id DESC");

        //表示対象冊数
        String strRes = "";
        strRes += dbrow.getCount()+"冊";

        //冊数出力
        TextView txtRes = (TextView)findViewById(R.id.txtRes);
        txtRes.setText(strRes);
        TextView txtAllRes = (TextView)findViewById(R.id.txtAllres);
        txtAllRes.setText(strRes);

        //リスト出力
        LinearLayout lin = (LinearLayout)findViewById(R.id.linLayout);
        lin.removeAllViews();

        if (dbrow.getCount() > 0){
            while (dbrow.moveToNext()){
                View viewchild = getLayoutInflater().inflate(R.layout.record,null);
                lin.addView(viewchild);
                //画像
                ImageView bookimg = (ImageView)viewchild.findViewById(R.id.rec_imView);
                //画像出力
                byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
                Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
                bookimg.setImageBitmap(bitimage);
                //タイトル
                TextView txtname = (TextView)viewchild.findViewById(R.id.rec_txtTitle);
                txtname.setText(dbrow.getString(dbrow.getColumnIndex("title")));
                //ID
                //record.xmlの修正ボタンを使用可にする
                ImageButton btnEdit = (ImageButton)viewchild.findViewById(R.id.rec_imgSeri);
                //修正ボタンのTagに値(id)を設定
                btnEdit.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
            }
        }
    }

    //検索
    public void btnSerchClich(final View v) {
        //データベース取得
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        REQUEST_CODE = 1;
        Intent serch = new Intent(MainActivity.this,SerchActivity.class);
        startActivityForResult(serch,REQUEST_CODE);

    }

    //追加
    public void btnAddClic(View myview){
        //追加ページへ

        if (REQUEST_CODE == 1){
            REQUEST_CODE = 4;
        }else if (REQUEST_CODE == 4) {
            REQUEST_CODE = 4;
        }else {
            REQUEST_CODE = 2;
        }
        Intent AddAci = new Intent(MainActivity.this,addActivity.class);
        startActivityForResult(AddAci,REQUEST_CODE);
    }

    //詳細
    public void btndetailClick(View myview){
        //編集ページへ

        if (REQUEST_CODE == 1){
            //絞り込み検索がされている
            REQUEST_CODE = 4;
        }else if (REQUEST_CODE == 4) {
            REQUEST_CODE = 4;
        }else {
            REQUEST_CODE = 2;
        }
        // クリック時の処理
        Intent inEdsub = new Intent(MainActivity.this,detailsActivity.class);
        String strid = myview.getTag().toString();
        inEdsub.putExtra("id",String.valueOf(strid));
        //SubActivityの開始
        startActivityForResult(inEdsub,REQUEST_CODE);
    }

    //設定
    public void btnCosClick(View myview){
        //設定画面へ
        Intent intCos = new Intent(MainActivity.this,CosActivity.class);
        startActivity(intCos);
    }

    //検索解除
    public void btnClear(View v){

        //データベース取得
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        //データベース読み込み
        Cursor dbrow;
        dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","bookimg","tag","str"},null,null,null,null,"id DESC");

        //表示対象冊数
        String strRes = "";
        strRes += dbrow.getCount()+"冊";

        //冊数出力
        TextView txtRes = (TextView)findViewById(R.id.txtRes);
        txtRes.setText(strRes);
        TextView txtAllRes = (TextView)findViewById(R.id.txtAllres);
        txtAllRes.setText(strRes);

        //リスト出力
        LinearLayout lin = (LinearLayout)findViewById(R.id.linLayout);
        lin.removeAllViews();

        if (dbrow.getCount() > 0){
            while (dbrow.moveToNext()){
                View viewchild = getLayoutInflater().inflate(R.layout.record,null);
                lin.addView(viewchild);
                //画像
                ImageView bookimg = (ImageView)viewchild.findViewById(R.id.rec_imView);
                //画像出力
                byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
                Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
                bookimg.setImageBitmap(bitimage);
                //タイトル
                TextView txtname = (TextView)viewchild.findViewById(R.id.rec_txtTitle);
                txtname.setText(dbrow.getString(dbrow.getColumnIndex("title")));
                //ID
                //record.xmlの修正ボタンを使用可にする
                ImageButton btnEdit = (ImageButton)viewchild.findViewById(R.id.rec_imgSeri);
                //修正ボタンのTagに値(id)を設定
                btnEdit.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
            }
        }
    }

    //SubActivityから戻ってきたときの処理(onActivityResult)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Intent受け取り
        if (data != null){
            //Intentがある
            data.getExtras();
        }else if(data == null){
            //Intentがない
            REQUEST_CODE = 0;
        }

        //DBアクセス用
        Cursor dbrow = null;

        //if (requestCode == Activity.RESULT_OK){

            if (REQUEST_CODE == 1){
                //検索画面からの戻り

                //変数設定
                String serchclass = "title";
                String serchtext = "";
                Integer inttag  = 1;
                Integer intser = 1;
                String sort = "title";
                String sort2 = "ASC";

                if (data != null){
                    serchclass  =data.getStringExtra("serchradio");
                    serchtext = data.getStringExtra("serchtitle");
                    String serchtag = data.getStringExtra("serchtag");
                    String serchsere = data.getStringExtra("serchSere");
                    inttag = Integer.parseInt(serchtag);
                    intser = Integer.parseInt(serchsere);
                    sort = data.getStringExtra("sorttag");
                    sort2 = data.getStringExtra("sortdo");
                }

                if (inttag == 1 && intser == 1) {
                    //検索条件 -> タグ・シリーズ絞り込み無し
                    dbrow = mydb.query("t_book",
                            new String[]{"id","title","author","publication","series","bookimg","tag","str"},
                            serchclass+" like ? ",
                            new String[]{"%"+serchtext+"%"},
                            null,null,sort +" "+ sort2);
                }else if (inttag == 1 && intser != 1){
                    //検索条件 -> タグ絞り込み無し、シリーズ絞り込み有り
                    dbrow = mydb.query("t_book",
                            new String[]{"id","title","author","publication","series","bookimg","tag","str"},
                            serchclass+" like ? AND series like ? ",
                            new String[]{"%"+serchtext+"%",""+intser+""},
                            null,null,sort +" "+ sort2);
                }else if (inttag != 1 && intser == 1){
                    //検索条件 -> タグ絞り込み有り、シリーズ絞り込み無し
                    dbrow = mydb.query("t_book",
                            new String[]{"id","title","author","publication","series","bookimg","tag","str"},
                            serchclass+" like ? AND tag like ? ",
                            new String[]{"%"+serchtext+"%",""+inttag+""},
                            null,null,sort +" "+ sort2);
                }else if (inttag != 1 && intser != 1){
                    //検索条件 -> タグ・シリーズ絞り込み有り
                    dbrow = mydb.query("t_book",
                            new String[]{"id","title","author","publication","series","bookimg","tag","str"},
                            serchclass+" like ? && series like ? && tag like ? ",
                            new String[]{"%"+serchtext+"%",""+intser+"",""+inttag+""},
                            null,null,sort +" "+ sort2);
                }

                //表示対象冊数
                String strRes = "";
                strRes = dbrow.getCount()+"冊";

                //冊数出力
                TextView txtRes = (TextView)findViewById(R.id.txtRes);
                txtRes.setText(strRes);

                //リスト出力
                LinearLayout lin = (LinearLayout)findViewById(R.id.linLayout);
                lin.removeAllViews();

                if (dbrow.getCount() > 0){
                    while (dbrow.moveToNext()){
                        View viewchild = getLayoutInflater().inflate(R.layout.record,null);
                        lin.addView(viewchild);
                        //画像
                        ImageView bookimg = (ImageView)viewchild.findViewById(R.id.rec_imView);
                        //画像出力
                        byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
                        Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
                        bookimg.setImageBitmap(bitimage);
                        //タイトル
                        TextView txtname = (TextView)viewchild.findViewById(R.id.rec_txtTitle);
                        txtname.setText(dbrow.getString(dbrow.getColumnIndex("title")));
                        //ID
                        //record.xmlの修正ボタンを使用可にする
                        ImageButton btnEdit = (ImageButton)viewchild.findViewById(R.id.rec_imgSeri);
                        //修正ボタンのTagに値(id)を設定
                        btnEdit.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
                    }
                }

            }else if (REQUEST_CODE == 2 || REQUEST_CODE == 3){
                //追加、詳細画面からの戻り

                dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","bookimg","tag","str"},null,null,null,null,"id DESC");

                //表示対象冊数
                String strRes = "";
                strRes = dbrow.getCount()+"冊";

                //冊数出力
                TextView txtRes = (TextView)findViewById(R.id.txtRes);
                txtRes.setText(strRes);
                TextView txtAllRes = (TextView)findViewById(R.id.txtAllres);
                txtAllRes.setText(strRes);

                //リスト出力
                LinearLayout lin = (LinearLayout)findViewById(R.id.linLayout);
                lin.removeAllViews();

                if (dbrow.getCount() > 0){
                    while (dbrow.moveToNext()){
                        View viewchild = getLayoutInflater().inflate(R.layout.record,null);
                        lin.addView(viewchild);
                        //画像
                        ImageView bookimg = (ImageView)viewchild.findViewById(R.id.rec_imView);
                        //画像出力
                        byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
                        Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
                        bookimg.setImageBitmap(bitimage);
                        //タイトル
                        TextView txtname = (TextView)viewchild.findViewById(R.id.rec_txtTitle);
                        txtname.setText(dbrow.getString(dbrow.getColumnIndex("title")));
                        //ID
                        //record.xmlの修正ボタンを使用可にする
                        ImageButton btnEdit = (ImageButton)viewchild.findViewById(R.id.rec_imgSeri);
                        //修正ボタンのTagに値(id)を設定
                        btnEdit.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
                    }
                }
            }else if (REQUEST_CODE == 4){
                //絞り込み後、追加・詳細画面から更新無しで戻り
            }else if (REQUEST_CODE == 0){
                //でふぉると
            }
            /*
        }else if (requestCode == Activity.RESULT_CANCELED){

        }
        */
    }

}
