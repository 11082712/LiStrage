package jp.ac.hal.nagoya.nhs70244.list;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SerchActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbHelper;
    private int REQUEAnInt = 1;

    //読込
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serch);

        settagSpinner();
        setserSpinner();
    }

    //検索実行
    public void onClickSerch(View v){

        String strradio = "";
        String strradiosort = "";
        String strradiosort2 = "";

        //ラジオボタン（検索対象）取得
        RadioGroup rgroup = (RadioGroup)findViewById(R.id.radioGroup);
        Integer checkid = rgroup.getCheckedRadioButtonId();
        if (checkid != -1){
            RadioButton checkbutton = (RadioButton)findViewById(checkid);
            strradio = checkbutton.getText().toString();
        }
        //ラジオボタン（ソート対象）取得
        RadioGroup rgroupsort = (RadioGroup)findViewById(R.id.radioGroupsort);
        Integer checkidsort = rgroupsort.getCheckedRadioButtonId();
        if (checkidsort != -1){
            RadioButton checkbutton = (RadioButton)findViewById(checkidsort);
            strradiosort = checkbutton.getText().toString();
        }
        //ラジオボタン（昇順降順）取得
        RadioGroup rgroupsort2 = (RadioGroup)findViewById(R.id.radioGroupsort2);
        Integer checkidsort2 = rgroupsort2.getCheckedRadioButtonId();
        if (checkidsort2 != -1){
            RadioButton checkbutton = (RadioButton)findViewById(checkidsort2);
            strradiosort2 = checkbutton.getText().toString();
        }

        //検索対象はどれ
        String serch = "title";
        switch (strradio){
            case "タイトル":
                serch = "title";
                break;
            case "著者":
                serch = "author";
                break;
            case "出版":
                serch = "publication";
                break;
            default:
                serch = "title";
                break;
        }
        //ソート対象はどれ
        String sort = "title";
        switch (strradiosort){
            case "タイトル":
                sort = "title";
                break;
            case "著者":
                sort = "author";
                break;
            case "登録順":
                sort = "id";
                break;
            default:
                sort = "title";
                break;
        }
        //昇順降順
        String sort2 = "ASC";
        switch (strradiosort2){
            case "昇順":
                sort2 = "ASC";
                break;
            case "降順":
                sort2 = "DESC";
                break;
            default:
                sort2 = "ASC";
                break;
        }


        //エディットテキスト（検索テキスト）取得
        EditText edserch = (EditText)findViewById(R.id.edserch);
        String strserch = edserch.getText().toString();

        //スピナー（タグ・シリーズ）取得
        Spinner spiSereas = (Spinner)findViewById(R.id.spiSerchSere);
        Spinner spiTag = (Spinner)findViewById(R.id.spiSerchtag);
        String strSere = (String)spiSereas.getSelectedItem();
        String strTag = (String)spiTag.getSelectedItem();

        Integer intTag = 0;
        //タグIDを取得
        Cursor dbrowtag;
        dbrowtag = mydb.query("t_tag",new String[]{"id","name"},"name like ?",new String[]{strTag},null,null,null);
        dbrowtag.moveToFirst();
  //      if (dbrowtag.getCount() >=1){
            String strTagsid = dbrowtag.getString(dbrowtag.getColumnIndex("id"));
            intTag = Integer.parseInt(strTagsid);
    //    }

        Integer intSere = 0;
        //シリーズIDを取得
        Cursor dbrowsere;
        dbrowsere = mydb.query("t_series",new String[]{"id","name"},"name like ?",new String[]{strSere},null,null,null);
        dbrowsere.moveToFirst();
            String strSeresid = dbrowsere.getString(dbrowsere.getColumnIndex("id"));
            intSere = Integer.parseInt(strSeresid);


        //Intent生成
        Intent serchres = new Intent(SerchActivity.this,MainActivity.class);
        serchres.putExtra("serchradio",serch.toString());
        serchres.putExtra("serchtitle",strserch.toString());
        serchres.putExtra("serchtag",intTag.toString());
        serchres.putExtra("serchSere",intSere.toString());
        serchres.putExtra("sorttag",sort.toString());
        serchres.putExtra("sortdo",sort2.toString());

        //返り値
        setResult(Activity.RESULT_OK,serchres);
        finish();


    }

    //spinner設定
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

        Spinner sptag = (Spinner)findViewById(R.id.spiSerchtag);
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

        Spinner spser = (Spinner)findViewById(R.id.spiSerchSere);
        spser.setAdapter(adapter);

    }
}
