package kookyungmin.com.sixthfinger;

public class Temperature {
    private int temperature;
    private int humidity;
    public void setTemperature(int temperature){
        this.temperature = temperature;
    }
    public void setHumidity(int humidity){
        this.humidity = humidity;
    }
    public int getTemperature(){
        return this.temperature;
    }
    public int getHumidity(){
        return this.humidity;
    }
}
