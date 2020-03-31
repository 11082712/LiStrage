package jp.ac.hal.nagoya.nhs70244.list;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CosSereActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos_sere);

        //データベース取得
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        //データベース読み込み
        Cursor dbrow;
        dbrow = mydb.query("t_series",new String[]{"id","name"},null,null,null,null,null);

        //リスト出力
        LinearLayout lin = (LinearLayout)findViewById(R.id.linView);
        lin.removeAllViews();

        if (dbrow.getCount() > 0){
            while (dbrow.moveToNext()){
                View viewchild = getLayoutInflater().inflate(R.layout.record3,null);
                lin.addView(viewchild);
                //シリーズ名
                TextView txtname = (TextView)viewchild.findViewById(R.id.rec3_textView);
                txtname.setText(dbrow.getString(dbrow.getColumnIndex("name")));
                //ID
                ImageButton imbut = (ImageButton)viewchild.findViewById(R.id.rec3_imageButton);
                imbut.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
            }
        }

    }

    public void btnaddsere(View v){
        //データベース取得
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();
        final EditText editText = new EditText(this);
        //editText.setHint("");
        new AlertDialog.Builder(this)
                .setTitle("シリーズの追加")
                .setMessage("追加するシリーズ名")
                .setView(editText)
                .setPositiveButton("追加", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // お好きな処理をどうぞ

                        String strSere =  editText.getText().toString();

                        if(strSere.matches("")){
                            Toast.makeText(getApplicationContext(),"未入力での登録はできません",Toast.LENGTH_LONG).show();
                        }else{

                            String strSQL =  "";
                            strSQL = "insert into t_series(name) values('"+strSere+"')";
                            mydb.execSQL(strSQL);
                            Toast.makeText(getApplicationContext(),"登録しました",Toast.LENGTH_LONG).show();

                            //データベース読み込み
                            Cursor dbrow;
                            dbrow = mydb.query("t_series",new String[]{"id","name"},null,null,null,null,null);

                            //リスト出力
                            LinearLayout lin = (LinearLayout)findViewById(R.id.linView);
                            lin.removeAllViews();

                            if (dbrow.getCount() > 0){
                                while (dbrow.moveToNext()){
                                    View viewchild = getLayoutInflater().inflate(R.layout.record3,null);
                                    lin.addView(viewchild);
                                    //シリーズ名
                                    TextView txtname = (TextView)viewchild.findViewById(R.id.rec3_textView);
                                    txtname.setText(dbrow.getString(dbrow.getColumnIndex("name")));
                                    //ID
                                    ImageButton imbut = (ImageButton)viewchild.findViewById(R.id.rec3_imageButton);
                                    imbut.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
                                }
                            }

                        }

                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();

    }

    public void btndelSere(View v){
        // クリック時の処理

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        final String strid = v.getTag().toString();
        final Integer intid = Integer.parseInt(strid);

        //データベース読み込み
        Cursor dbrow;
        dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","tag","str"},"series like ? ",new String[]{""+intid+""},null,null,null);

        //表示対象冊数
        Integer intRes =  dbrow.getCount();

        if (intid == 1 || intRes >= 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("このシリーズは削除できません。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            //処理なし
                        }
                    });
            builder.show();
        }else if (intRes >= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("書籍情報に登録されているシリーズは削除できません。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            //処理なし
                        }
                    });
            builder.show();
        }else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("このシリーズを削除しますよ？")
                    .setPositiveButton("削除する", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            // ボタンをクリックしたときの動作

                            String strSQL =  "";
                            strSQL = " delete from t_series where id = "+intid;
                            mydb.execSQL(strSQL);

                            Toast.makeText(getApplicationContext(),"削除しました",Toast.LENGTH_LONG).show();

                            //データベース読み込み
                            Cursor dbrow;
                            dbrow = mydb.query("t_series",new String[]{"id","name"},null,null,null,null,null);

                            //リスト出力
                            LinearLayout lin = (LinearLayout)findViewById(R.id.linView);
                            lin.removeAllViews();

                            if (dbrow.getCount() > 0){
                                while (dbrow.moveToNext()){
                                    View viewchild = getLayoutInflater().inflate(R.layout.record3,null);
                                    lin.addView(viewchild);
                                    //シリーズ名
                                    TextView txtname = (TextView)viewchild.findViewById(R.id.rec3_textView);
                                    txtname.setText(dbrow.getString(dbrow.getColumnIndex("name")));
                                    //ID
                                    ImageButton imbut = (ImageButton)viewchild.findViewById(R.id.rec3_imageButton);
                                    imbut.setTag(dbrow.getString(dbrow.getColumnIndex("id") ) );
                                }
                            }
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

    }

    public void btnback(View myview){
        finish();
    }
}
