package com.t_robop.yuusuke.esp32_rc_controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    View inputView;  //Dialogレイアウトの取得用変数
    TextView textView;
    EditText editName;


    //たまビュー
    ImageView tamaV;
    private int preDx, preDy, newDx, newDy;

    int defX,defY;

    boolean tamaFrontFlag =false;
    boolean tamaBackFlag =false;
    boolean tamaLeftFlag =false;
    boolean tamaRightFlag =false;

    int VIEW_HEIGHT;
    int VIEW_WIDTH;
    FrameLayout fL;


    final HttpGet http = new HttpGet();
    String ip = "192.168.12.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tamaV = findViewById(R.id.tama);
        //Dialogレイアウト呼び出し
        LayoutInflater inflater = LayoutInflater.from(this);
        inputView = inflater.inflate(R.layout.result_dialog,null);

        textView = inputView.findViewById(R.id.text);
        editName = inputView.findViewById(R.id.editText);
        tamaSetup();

        fL= findViewById(R.id.frame_layout);

    }

    // メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // メニューアイテム選択イベント
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_setting:
                // 設定
                intent = new Intent(this,robotSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.item_connect:
                //接続

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void tamaSetup(){
        preDx = preDy = newDx = newDy = 0;
        defX=tamaV.getWidth();
        defY=tamaV.getHeight();


        tamaV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // x,y 位置取得
                newDx = (int) event.getRawX();
                newDy = (int) event.getRawY();

                VIEW_HEIGHT =fL.getHeight()+tamaV.getHeight();
                VIEW_WIDTH =fL.getWidth()+tamaV.getWidth();

                switch (event.getAction()) {
                    // タッチダウンでdragされた
                    case MotionEvent.ACTION_MOVE:
                        // ACTION_MOVEでの位置
                        int dx = tamaV.getLeft() + (newDx - preDx);
                        int dy = tamaV.getTop() + (newDy - preDy);

                        // 画像の位置を設定する
                        //右回転
                        if (dx > (VIEW_WIDTH /2)-tamaV.getWidth()/2) {
                            dx = (VIEW_WIDTH /2)-tamaV.getWidth()/2;

                            http.setRequest(ip,"/?2=RIGHT");
                            http.sendHttp();

                            tamaRightFlag =true;
                        }
                        else{
                            tamaRightFlag =false;
                        }
                        //左回転
                        if (dx < (VIEW_WIDTH /2)-tamaV.getWidth()-tamaV.getWidth()/2) {
                            dx = (VIEW_WIDTH /2)-tamaV.getWidth()-tamaV.getWidth()/2;
                            http.setRequest(ip,"/?3=LEFT");
                            http.sendHttp();
                            tamaLeftFlag = true;
                        }
                        else{
                            tamaLeftFlag = false;
                        }
                        //前進
                        if (dy < (VIEW_HEIGHT /2)-tamaV.getHeight()-tamaV.getHeight()/2) {
                            dy = (VIEW_HEIGHT /2)-tamaV.getHeight()-tamaV.getHeight()/2;
                            http.setRequest(ip,"/?0=FORWARD");
                            http.sendHttp();
                            tamaFrontFlag = true;
                        }
                        else{
                            tamaFrontFlag = false;
                        }
                        //後進
                        if (dy > (VIEW_HEIGHT /2)-tamaV.getHeight()/2) {
                            dy = (VIEW_HEIGHT /2)-tamaV.getHeight()/2;
                            http.setRequest(ip,"/?1=BACK");
                            http.sendHttp();
                            tamaBackFlag = true;
                        }
                        else{
                            tamaBackFlag = false;
                        }
                        tamaV.layout(dx, dy, dx + tamaV.getWidth(), dy + tamaV.getHeight());

                        Log.d("onTouch", "ACTION_MOVE: dx=" + dx + ", dy=" + dy + "," + newDx + "," + newDy);

                        break;

                    //指が離れた時
                    case MotionEvent.ACTION_UP:
                        //中心は（VIEW_WIDTH /2, VIEW_HEIGHT /2）である
                        tamaV.layout((VIEW_WIDTH /2)-tamaV.getWidth(), (VIEW_HEIGHT /2)-tamaV.getHeight(),VIEW_WIDTH /2, VIEW_HEIGHT /2);
                        http.setRequest(ip,"/?4=STOP");
                        http.sendHttp();
                        //bt.send("0005"+"000"+"000", false);
                        break;
                }

                // タッチした位置を古い位置とする
                preDx = newDx;
                preDy = newDy;

                return true;
            }
        });



    }
}
