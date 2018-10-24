package kookyungmin.com.sixthfinger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;
//import retrofit2.http.GET;
//import retrofit2.http.Path;
//import retrofit2.http.Query;

public class TemperatureActivity extends AppCompatActivity {
    private MyApplication app;
    private WebSocketClient mWebSocketClient;
    private TextView tempTxt;
    private TextView humTxt;

    private void connectWebSocket(){
        URI uri;
        try{
            uri = new URI("ws://35.189.144.126:8080/CommunicationToArduino");
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
                    Toast.makeText(TemperatureActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onMessage(String s){
                Log.i("Websocket", s);
                final String[] temp = s.split("/");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tempTxt.setText(temp[0] + "℃");
                        humTxt.setText(temp[1] + "％");
                    }
                });
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
        setContentView(R.layout.activity_temperature);
        app = (MyApplication) getApplication();
        ImageView temp = (ImageView)findViewById(R.id.temperatureImage);
        temp.setImageResource(R.drawable.temperature);
        ImageView hum = (ImageView)findViewById(R.id.humdityImage);
        hum.setImageResource(R.drawable.humidity);

        tempTxt = (TextView)findViewById(R.id.temperature);
        humTxt = (TextView)findViewById(R.id.humidity);

        Button reset = (Button)findViewById(R.id.resetButton);

        connectWebSocket();

        reset.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        try{
                            mWebSocketClient.send("temperature,android," + app.getData() + ",requestTemp");
                        }catch(Exception e){
                            Toast.makeText(TemperatureActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        //getTemperature();
                    }
                }
        );
    }


    /* retrofit 이용

    protected void getTemperature(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.189.144.126/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TemperatureService service = retrofit.create(TemperatureService.class);
        service.receiveTemperature(app.getData()).enqueue(new Callback<Temperature>() {
            @Override
            public void onResponse(Call<Temperature> call, Response<Temperature> response) {
                if(response.isSuccessful()){
                   int temperature = response.body().getTemperature();
                   int humidity = response.body().getHumidity();
                   tempTxt.setText(temperature + "℃");
                   humTxt.setText(humidity + "％");
                }else{
                    Toast.makeText(TemperatureActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Temperature> call, Throwable t) {
                Toast.makeText(TemperatureActivity.this, "와이파이 접속을 해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private interface TemperatureService {
        @GET("sixfinger/temperature")
        Call<Temperature> receiveTemperature(@Query("id") String id);
    }
    */
}
