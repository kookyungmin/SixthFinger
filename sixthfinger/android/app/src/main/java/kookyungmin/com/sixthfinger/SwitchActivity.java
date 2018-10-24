package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

//웹 소켓
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;


//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;
//import retrofit2.http.Body;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//import retrofit2.http.Path;
//import retrofit2.http.Query;

public class SwitchActivity extends AppCompatActivity {
    private MyApplication app;
    private WebSocketClient mWebSocketClient;

    private void connectWebSocket(){
        URI uri;
        try{
            uri = new URI("ws://35.189.144.126/CommunicationToArduino");
        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri){
            @Override
            public void onOpen(ServerHandshake serverHandShake){
                Log.i("Websocket", "Opened");
                if(app.getData() != null) {
                    mWebSocketClient.send("connected,android," + app.getData() + ",connect!");
                }else{
                    Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onMessage(String s){
                Log.i("Websocket", s);
            }
            @Override
            public void onClose(int i, String s, boolean b){
                Log.i("Websocket", "closed" + s);
            }
            @Override
            public void onError(Exception e){
                Log.i("Websocket", "Error" + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        app = (MyApplication)getApplication();

        Button lightOn = (Button)findViewById(R.id.lightOn);
        Button lightOff = (Button)findViewById(R.id.lightOff);

        final ImageView Image = (ImageView)findViewById(R.id.lightImage);
        Image.setImageResource(R.drawable.light_on);

        connectWebSocket();

        lightOn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Image.setImageResource(R.drawable.light_on);
                        try{
                            mWebSocketClient.send("switch,android," + app.getData() + ",on");
                        }catch(Exception e){
                            Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //setSwitch("on");
                    }
                }
        );
        lightOff.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Image.setImageResource(R.drawable.light_off);
                        try {
                            mWebSocketClient.send("switch,android," + app.getData() + ",off");
                        }catch(Exception e){
                            Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //setSwitch("off");
                    }
                }
        );
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mWebSocketClient.close();
    }

    /*
        retrofit 을 이용한 http 통신

    protected void setSwitch(String state){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.189.144.126/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LightService service = retrofit.create(LightService.class);
        service.lightSwitch(state, app.getData()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SwitchActivity.this, "스위치 " + response.body().toString(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(SwitchActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private interface LightService {
        @GET("sixfinger/light/{state}")
        Call<String> lightSwitch(@Path("state") String state, @Query("id") String id);
    }
    */
}
