package jp.ac.hal.nagoya.nhs70244.list;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbHelper;

    String strid ;
    Integer intid;

    //読込時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //spinner設定
        settagSpinner();
        setserSpinner();

        //DB系
        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        //Intentから値の受け取り
        Bundle extras = getIntent().getExtras();
        strid = extras.getString("id");
        intid = Integer.parseInt(strid);

        //DB検索
        Cursor dbrow;
        dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","tag","bookimg","str"},"id like ? ",new String[]{""+intid+""},null,null,null);
        dbrow.moveToFirst();

        //画像出力
        byte[] image = Base64.decode( dbrow.getString(dbrow.getColumnIndex("bookimg")),Base64.DEFAULT );
        Bitmap bitimage = BitmapFactory.decodeByteArray(image,0,image.length);
        ImageButton imbook = (ImageButton)findViewById(R.id.imgeditimage);
        imbook.setImageBitmap(bitimage);

        EditText edTitle = (EditText)findViewById(R.id.edTitle) ;
        EditText edAur = (EditText)findViewById(R.id.edAut);
        EditText edPubli = (EditText)findViewById(R.id.edPubli);
        Spinner spSer = (Spinner)findViewById(R.id.spSer);
        Spinner spTag = (Spinner)findViewById(R.id.spTag);

        String strSer = dbrow.getString(dbrow.getColumnIndex("series"));
        String strTag = dbrow.getString(dbrow.getColumnIndex("tag"));
        Integer intSer = Integer.parseInt(strSer);
        Integer intTag = Integer.parseInt(strTag);

        intSer--;
        intTag--;
        intSer--;
        //intTag--;

        edTitle.setText(dbrow.getString(dbrow.getColumnIndex("title")));
        edAur.setText(dbrow.getString(dbrow.getColumnIndex("author")));
        edPubli.setText(dbrow.getString(dbrow.getColumnIndex("publication")));
        spSer.setSelection(intSer);
        spTag.setSelection(intTag);

    }

    //編集
    public void btnEditClick(View myview){

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();

        EditText edTitle = (EditText)findViewById(R.id.edTitle) ;
        EditText edAur = (EditText)findViewById(R.id.edAut);
        EditText edPubli = (EditText)findViewById(R.id.edPubli);
        String strTitle = edTitle.getText().toString();
        String strAur  = edAur.getText().toString();
        String strPubli = edPubli.getText().toString();


        Spinner spiSereas = (Spinner)findViewById(R.id.spSer);
        Spinner spiTag = (Spinner)findViewById(R.id.spTag);
        String strSere = (String)spiSereas.getSelectedItem();
        String strTag = (String)spiTag.getSelectedItem();


        Cursor dbrowtag;
        dbrowtag = mydb.query("t_tag",new String[]{"id","name"},"name like ?",new String[]{strTag},null,null,null);
        dbrowtag.moveToFirst();
        String strTagsid = dbrowtag.getString(dbrowtag.getColumnIndex("id"));
        Integer intTag = Integer.parseInt(strTagsid);

        Cursor dbrowsere;
        dbrowsere = mydb.query("t_series",new String[]{"id","name"},"name like ?",new String[]{strSere},null,null,null);
        dbrowsere.moveToFirst();

        String strSeresid = dbrowsere.getString(dbrowsere.getColumnIndex("id"));
        Integer intSere = Integer.parseInt(strSeresid);





        String strSQL = "";
        strSQL = " update t_book set title = '" + strTitle + "', author = '" + strAur + "' , publication = '" + strPubli + "' , series = " + intSere + " , tag = " + intTag + " where id = " + intid ;
        mydb.execSQL(strSQL);

        Toast.makeText(getApplicationContext(),"変更しました",Toast.LENGTH_LONG).show();

        //値をMainに渡す(Intent)
        //Intentの生成
        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        //intentに値を設定
        intent.putExtra("id",strid);
        //返り
        setResult(Activity.RESULT_OK,intent);

        //終了
        finish();
    }

    //キャンセル
    public void btnFinishClick(View myview){
        //値をMainに渡す(Intent)
        //Intentの生成
        Intent intent = new Intent(EditActivity.this,MainActivity.class);
        //intentに値を設定
        intent.putExtra("id",strid);
        //返り
        setResult(Activity.RESULT_CANCELED,intent);

        //終了
        finish();
    }


    //spinner
    public void settagSpinner(){
        ArrayList<String> list = new ArrayList<>();

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();
        Cursor dbrow;
        dbrow = mydb.query("t_tag",new String[]{"id","name"},null,null,null,null,null);

        while (dbrow.moveToNext()){
            list.add(dbrow.getString(dbrow.getColumnIndex("name")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,list
        );

        Spinner sptag = (Spinner)findViewById(R.id.spTag);
        sptag.setAdapter(adapter);

    }
    public void setserSpinner(){
        ArrayList<String> list = new ArrayList<>();

        dbHelper = new DBHelper(this);
        mydb = dbHelper.getWritableDatabase();
        Cursor dbrow;
        dbrow = mydb.query("t_series",new String[]{"id","name"},null,null,null,null,null);

        while (dbrow.moveToNext()){
            list.add(dbrow.getString(dbrow.getColumnIndex("name")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,list
        );

        Spinner spser = (Spinner)findViewById(R.id.spSer);
        spser.setAdapter(adapter);

    }



}
