package kookyungmin.com.sixthfinger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class TemperatureActivity extends Activity {
    private MyApplication app;
    private TextView tempTxt;
    private TextView humTxt;

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
        reset.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        getTemperature();
                    }
                }
        );
    }
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
}
