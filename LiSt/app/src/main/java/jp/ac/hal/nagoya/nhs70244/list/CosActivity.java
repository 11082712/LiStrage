package jp.ac.hal.nagoya.nhs70244.list;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CosActivity extends AppCompatActivity {

    private SQLiteDatabase mydb;
    private DBHelper dbhelper;

    //読込時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cos);


    }

    //シリーズ設定
    public void btnCosSereClick(View myview){
        Intent intcossere = new Intent(CosActivity.this,CosSereActivity.class);
        startActivity(intcossere);
    }

    //タグ設定
    public void btnCosTagClick(View myview){
        Intent intCosTag = new Intent(CosActivity.this,CosTagActivity.class);
        startActivity(intCosTag);
    }

    //棚設定
    public void btnStrCosClick(View myview){
        Intent intStr = new Intent(CosActivity.this,StrageActivity.class);
        startActivity(intStr);
    }


    public void btnbackClick(View myview){
        finish();
    }
}
