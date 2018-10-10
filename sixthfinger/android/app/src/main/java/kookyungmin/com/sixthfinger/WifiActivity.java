package kookyungmin.com.sixthfinger;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class WifiActivity extends AppCompatActivity implements  View.OnClickListener{
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    Thread mWorkerThread = null;

    EditText mEditReceive, mEditSSIDSend, mEditPassSend;

    //문자열 끝
    String mStrDelimiter = "\n";
    char mDelimiter = '\n';

    //수신한 데이터 버퍼
    byte[] readBuffer;
    int readBufferPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        Intent intent = new Intent(this.getIntent());
        BluetoothDevice selectedDevice = intent.getParcelableExtra("selectedDevice");
        connectToSelectedDevice(selectedDevice);

        mEditSSIDSend = (EditText)findViewById(R.id.ssid);
        mEditPassSend = (EditText)findViewById(R.id.password);

        Button button =(Button)findViewById(R.id.sendButton);
        button.setOnClickListener(this);
    }

    //원격 블루투스 장치와의 연결
    protected void connectToSelectedDevice(BluetoothDevice selectedDevice){
        mRemoteDevice = selectedDevice;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try{
            //소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            //RFCOMM 채널을 통한 연결
            mSocket.connect();

            //데이터 송수신을 위한 스트림 얻기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            Toast.makeText(WifiActivity.this, "블루투스 연결 성공", Toast.LENGTH_SHORT).show();
            //데이터 수신 준비
            beginListenForData();
        }catch(Exception e){
            //블루투스 연결 중 오류 발생
            Toast.makeText(WifiActivity.this, "블루투스 연결 실패", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void sendData(String msg){
        try{
            msg += mStrDelimiter;
            mOutputStream.write(msg.getBytes("UTF-8")); //문자열 전송
        }catch(Exception e){
            //문자열 도중 오류 발생
            Toast.makeText(WifiActivity.this, "문자열 전송 실패", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void beginListenForData(){
        final Handler handler = new Handler();

        readBuffer = new byte[1024];
        readBufferPosition = 0;

        //문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable(){
            public void run(){
                while(!Thread.currentThread().isInterrupted()){
                    try{
                        int bytesAvailable = mInputStream.available();
                        if(bytesAvailable > 0){
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i = 0; i < bytesAvailable; i++){
                                byte b = packetBytes[i];
                                if(b == mDelimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes,"utf-8");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(data.equals("FAIL")){
                                                Toast.makeText(WifiActivity.this, "연결실패! 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                                            } else{
                                                Toast.makeText(WifiActivity.this, "와이파이가 연결되었습니다.", Toast.LENGTH_SHORT).show();
                                                MyApplication app = (MyApplication)getApplication();
                                                app.setData(data);
                                                finish();
                                            }
                                        }
                                    });
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }catch(IOException e){
                        finish();
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    @Override
    public void onClick(View v){
        Toast.makeText(WifiActivity.this, "연결 시도! 10초만 기다려주세요~", Toast.LENGTH_SHORT).show();
        String ssid = mEditSSIDSend.getText().toString();
        String pass = mEditPassSend.getText().toString();

        if(ssid.equals("") || ssid == null){
            Toast.makeText(WifiActivity.this, "ssid가 비어있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        sendData("BlueToothConnected");
        sendData(ssid);
        sendData(pass);
    }



    @Override
    protected void onDestroy(){
        try{
            mWorkerThread.interrupt();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }

}
