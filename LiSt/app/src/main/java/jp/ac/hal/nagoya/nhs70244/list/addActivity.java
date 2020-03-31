package jp.ac.hal.nagoya.nhs70244.list;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class addActivity extends AppCompatActivity {

    //画像系
    static final int REQUEST_CAPTURE_IMAGE = 100;

    //DB系
    private SQLiteDatabase mydb;
    private DBHelper dbhelper;

    //非同期処理
    private SerchAPI serchAPI;

    //UI
    ImageButton bookimg;
    EditText edTiele;
    EditText edAut;
    EditText edPubli;

    Integer intx = 0;

    //ACtivity読込時
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //EditTextに値を設定
        EditText edTitle = (EditText)findViewById(R.id.edTitle);
        edTitle.setText("タイトル");
        //edTitle.setText("9784040692869");
        EditText edAur = (EditText)findViewById(R.id.edAuthor);
        edAur.setText("著者");
        EditText edPubu = (EditText)findViewById(R.id.edPublication);
        edPubu.setText("出版:登録なし");

        //Spinnerに値を設定
        settagSpinner();
        setserSpinner();

        //画像系
        findViews();
        setListeners();

    }

    //ISBN検索処理（API実行前処理）
    public void onIsbn(View v){

        //final String text = edTiele.getText().toString();
        //API
        final Button buttonStart = (Button) findViewById(R.id.btnISBN);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle("ISBN検索")
                .setMessage("ISBNコード")
                .setView(editText)
                .setPositiveButton("追加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // お好きな処理をどうぞ

                        final String strTag =  editText.getText().toString();

                        if(strTag.matches("")){
                            Toast.makeText(getApplicationContext(),"ISBNコードを入力してください",Toast.LENGTH_LONG).show();
                        }else{
                            conectserchapi(strTag);
                        }

                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void conectserchapi(String strTag){
        // ボタンをタップして非同期処理を開始
        serchAPI = new SerchAPI(this);
        // Listenerを設定
        serchAPI.setListener(createListener());
        serchAPI.execute(strTag);
    }

    //API・非同期処理
    @Override
    protected void onDestroy() {
        serchAPI.setListener(null);
        super.onDestroy();
    }
    @Override
    protected void onStop(){
        super.onStop();
    }

    private SerchAPI.Listener createListener() {
        return new SerchAPI.Listener() {
            @Override
            public void onSuccess(String[] count) {

                for (int i = 0; i <= 2; i++){
                    if (i == 0){
                        if(count[i] != ""){
                            edTiele = (EditText) findViewById(R.id.edTitle);
                            edTiele.setText(count[i]);
                        }else{
                            Toast.makeText(getApplicationContext(),"書籍情報を取得できませんでした。",Toast.LENGTH_LONG).show();
                        }
                    }else if (i == 1){
                        if (count[i] != ""){
                            edAut = (EditText)findViewById(R.id.edAuthor);
                            edAut.setText(count[i]);
                        }else{
                            Toast.makeText(getApplicationContext(),"書籍情報を取得できませんでした。",Toast.LENGTH_LONG).show();
                        }
                    }else if (i == 2){
                        if (count[i] != ""){
                            bookimg = (ImageButton)findViewById(R.id.imageButton);
                            //画像取得スレッド起動
                            ImageGetTask task = new ImageGetTask(bookimg);
                            task.execute(count[i]);
                        }else{
                            Toast.makeText(getApplicationContext(),"画像情報を取得できませんでした。",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };
    }

    // Image取得用スレッドクラス
    class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
        private ImageButton ulimage;

        public ImageGetTask(ImageButton _image) {
            ulimage = _image;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image;
            try {
                URL imageUrl = new URL(params[0]);
                InputStream imageIs;
                imageIs = imageUrl.openStream();
                image = BitmapFactory.decodeStream(imageIs);
                return image;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            // 取得した画像をImageViewに設定します。
            ulimage.setImageBitmap(result);
        }
    }

    //画像系・ImageButton
    protected void findViews(){
        bookimg = (ImageButton)findViewById(R.id.imageButton);
    }

    //ImageButtonのクリックイベント
    protected void setListeners(){
        bookimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
            }
        });
    }

    //画像設定
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent date){
        if (REQUEST_CAPTURE_IMAGE == requestCode && resultCode == Activity.RESULT_OK){
            Bitmap captureImage = (Bitmap)date.getExtras().get("data");
            bookimg.setImageBitmap(null);
            bookimg.setImageBitmap(captureImage);
        }
    }

    //追加処理
    public void onNextCilck(View myview){
        //追加

        //Toast.makeText(getApplicationContext(),"ISBNコードを入力してください",Toast.LENGTH_LONG).show();


        dbhelper = new DBHelper(this);
        mydb = dbhelper.getWritableDatabase();

        //入力されたタイトル・出版・著者を取得
        edTiele = (EditText)findViewById(R.id.edTitle);
        edAut = (EditText)findViewById(R.id.edAuthor);
        edPubli = (EditText)findViewById(R.id.edPublication);
        final String strTitle = edTiele.getText().toString();
        final String strAuthor = edAut.getText().toString();
        final String strPubli = edPubli.getText().toString();

        //入力されたタグとシリーズ
        Spinner spiSereas = (Spinner)findViewById(R.id.spiSereas);
        Spinner spiTag = (Spinner)findViewById(R.id.spiTag);
        final String strSere = (String)spiSereas.getSelectedItem();
        final String strTag = (String)spiTag.getSelectedItem();

        //入力判定
        if(strTitle.matches("") || strAuthor.matches("") || strPubli.matches("")){
            //入力されていない→アラートダイアログ（警告）
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("入力されてない項目があります")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // ボタンをクリックしたときの動作
                        }
                    });
            builder.show();
        }else {

            Cursor dbrow;
            dbrow = mydb.query("t_book",new String[]{"id","title","author","publication","series","tag","bookimg","str"},"title = '" + strTitle+"'",null,null,null,null);
            dbrow.moveToFirst();

            if (dbrow.getCount() > 0){
                AlertDialog.Builder daialog = new AlertDialog.Builder(this);
                daialog.setMessage("同じタイトルの書籍が登録されています。" + "\n"
                                    + strTitle + "\n"
                                    + "登録しますか？")
                        .setPositiveButton("はい",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int id){
                                dbadd(strTitle,strAuthor,strPubli,strSere,strTag);
                            }
                        });
                daialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 何もしないで閉じる
                    }
                });
                daialog.show();
            }else{
                dbadd(strTitle,strAuthor,strPubli,strSere,strTag);
            }

        }


    }

    //追加処理dbadd
    public void dbadd(String strTitle,String strAuthor,String strPubli,String strSere,String strTag){

        //入力されている→アラートダイアログ（確認）
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("この内容で追加しますよ？" + "\n"
                + "タイトル：" + strTitle + "\n"
                + "著者：" + strAuthor + "\n"
                + "出版：" + strPubli + "\n"
                + "シリーズ:" + strSere + "\n"
                + "タグ:" + strTag)
                .setPositiveButton("登録", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ボタンをクリックしたときの動作

                        //入力されたタイトル・出版・著者を取得
                        EditText edTiele = (EditText)findViewById(R.id.edTitle);
                        EditText edAut = (EditText)findViewById(R.id.edAuthor);
                        EditText edPubli = (EditText)findViewById(R.id.edPublication);
                        String strTitle = edTiele.getText().toString();
                        String strAuthor = edAut.getText().toString();
                        String strPubli = edPubli.getText().toString();

                        //入力されたタグ・シリーズ
                        Spinner spiSereas = (Spinner)findViewById(R.id.spiSereas);
                        Spinner spiTag = (Spinner)findViewById(R.id.spiTag);
                        String strSere = (String)spiSereas.getSelectedItem();
                        String strTag = (String)spiTag.getSelectedItem();

                        //写真
                        Drawable drawable = bookimg.getDrawable();
                        Bitmap bitmap =  ((BitmapDrawable) drawable).getBitmap();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bytegazo = bos.toByteArray();
                        String strdec = Base64.encodeToString(bytegazo,Base64.DEFAULT);

                        //タグIDを取得
                        Cursor dbrowtag;
                        dbrowtag = mydb.query("t_tag",new String[]{"id","name"},"name like ?",new String[]{strTag},null,null,null);
                        dbrowtag.moveToFirst();
                        String strTagsid = dbrowtag.getString(dbrowtag.getColumnIndex("id"));
                        Integer intTag = Integer.parseInt(strTagsid);

                        //シリーズIDを取得
                        Cursor dbrowsere;
                        dbrowsere = mydb.query("t_series",new String[]{"id","name"},"name like ?",new String[]{strSere},null,null,null);
                        dbrowsere.moveToFirst();
                        String strSeresid = dbrowsere.getString(dbrowsere.getColumnIndex("id"));
                        Integer intSere = Integer.parseInt(strSeresid);

                        //データベース登録
                        String strSQL =  "";
                        strSQL = "insert into t_book(title,author,publication,series,tag,bookimg,str) values('"+strTitle+"','"+strAuthor+"','"+strPubli+"',"+intSere+","+intTag+",'"+strdec+"',1)";
                        mydb.execSQL(strSQL);

                        Toast.makeText(getApplicationContext(),"追加しました",Toast.LENGTH_LONG).show();

                        //Intentの生成
                        Intent intent = new Intent(addActivity.this,MainActivity.class);
                        //intentに値を設定
                        intent.putExtra("","");
                        //返り
                        setResult(Activity.RESULT_OK,intent);

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
    public void onbackClick(View myview){
        //Intentの生成
        Intent intent = new Intent(addActivity.this,MainActivity.class);
        //intentに値を設定
        intent.putExtra("","");
        //返り
        setResult(Activity.RESULT_CANCELED,intent);

        finish();
    }

    //Spinnerに値を設定します
    public void settagSpinner(){
        ArrayList<String> list = new ArrayList<>();

        dbhelper = new DBHelper(this);
        mydb = dbhelper.getWritableDatabase();
        Cursor dbrow;
        dbrow = mydb.query("t_tag",new String[]{"id","name"},null,null,null,null,null);

        while (dbrow.moveToNext()){
            list.add(dbrow.getString(dbrow.getColumnIndex("name")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,list
        );

        Spinner sptag = (Spinner)findViewById(R.id.spiTag);
        sptag.setAdapter(adapter);

    }
    public void setserSpinner(){
        ArrayList<String> list = new ArrayList<>();

        dbhelper = new DBHelper(this);
        mydb = dbhelper.getWritableDatabase();
        Cursor dbrow;
        dbrow = mydb.query("t_series",new String[]{"id","name"},null,null,null,null,null);

        while (dbrow.moveToNext()){
            list.add(dbrow.getString(dbrow.getColumnIndex("name")));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item,list
        );

        Spinner spser = (Spinner)findViewById(R.id.spiSereas);
        spser.setAdapter(adapter);

    }


}
