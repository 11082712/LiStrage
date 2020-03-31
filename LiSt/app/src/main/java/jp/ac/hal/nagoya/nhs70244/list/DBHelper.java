package jp.ac.hal.nagoya.nhs70244.list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by b-show on 2019/11/25.
 */

public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context cn){
        super(cn,"LiSt.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        String strSQL = "";

        strSQL = " drop table if exists t_book ";
        db.execSQL(strSQL);

        strSQL = " create table t_book ";
        strSQL += " ( ";
        strSQL += " id integer primary key autoincrement, ";
        strSQL += " title text, ";
        strSQL += " author text, ";
        strSQL += " publication text, ";
        strSQL += " series integer, ";
        strSQL += " tag integer, ";
        strSQL += " bookimg BLOB,";
        strSQL += " str integer ";
        strSQL += " ) ";
        db.execSQL(strSQL);

        strSQL = " drop table if exists t_tag ";
        db.execSQL(strSQL);

        strSQL = " create table t_tag ";
        strSQL += " ( ";
        strSQL += " id integer primary key autoincrement, ";
        strSQL += " name text ";
        strSQL += " ) ";
        db.execSQL(strSQL);

        strSQL = " drop table if exists t_series ";
        db.execSQL(strSQL);

        strSQL = " create table t_series ";
        strSQL += " ( ";
        strSQL += " id integer primary key autoincrement, ";
        strSQL += " name text ";
        strSQL += " ) ";
        db.execSQL(strSQL);

        String arySQL[]=
                {
//                        " insert into t_book(title,author,publication,series,tag,str) values ('xxx','aaa','qq',1,1,1) ",
//                        " insert into t_book(title,author,publication,series,tag,str) values ('ccc','eee','rr',1,1,1) ",
//                        " insert into t_book(title,author,publication,series,tag,str) values ('www','eee','tt',1,1,1) "
                        " insert into t_tag(name) values ('登録なし') ",
                        " insert into t_tag(name) values ('漫画') ",
                        " insert into t_tag(name) values ('文庫') ",
                        " insert into t_tag(name) values ('ライトノベル') ",
                        " insert into t_series(name) values ('登録なし') "
                };
        for (int i=0;i<arySQL.length;i++){
            db.execSQL(arySQL[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

}
