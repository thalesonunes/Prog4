package br.edu.uemg.progiv.weatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURL;

    public Weather(
            long timeStamp,
            double minTemp,
            double maxTemp,
            double humidity,
            String description,
            String iconName
    ){
        //NumberFormat serve para formatar temperaturas em double
        // arredondando-as em inteiros: 15.6546 = 16°F
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u00B0F";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0F";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.description = description;
        this.iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    private static String convertTimeStampToDay(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000); //configurar a hora
        TimeZone timeZone = TimeZone.getDefault(); //obtem o fuso horário do dispositivo
        calendar.add(
                Calendar.MILLISECOND,
                timeZone.getOffset(calendar.getTimeInMillis())
        ); //ajusta a hora para o fuso horário escolhido
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(calendar.getTime());
    }

}
