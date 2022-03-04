package br.edu.uemg.progiv.weatherviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //lista de objetos Weather q representam a previsão
    private List<Weather> weatherList = new ArrayList<>();

    //ArrayAdapter para vincular objetos Weather a uma ListView:
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView; //exibe as informações de previsão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //cria ArrayAdapter para vincular weatherList a weatherListView
        weatherListView = (ListView) findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        //configura FAB para ocultar o teclado e iniciar a solicitação ao webservice:
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtem texto de locationEditText e cria a URL do webservice para executar:
                EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
                URL url = createURL(locationEditText.getText().toString());
                //oculta o teclado e inicia uma GetWeatherTask para o download
                // de dados climáticos do OpenWeatherMap.org em uma thread separada:
                if(url != null){
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    //remover o teclado via código quando o usuário pressionar o botao FAB
    private void dismissKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(weatherListView.getWindowToken(), 0);
    }

    //criar a URL do webservice de openweathermap.org usando cidade:
    private URL createURL(String cidade){
        String apiKey = getString(R.string.api_key);
        String baseURL = getString(R.string.web_service_url);
        try{
            //criar a url para a cidade e para as unidade específicas (Fahrenheit)
            String urlString = baseURL + URLEncoder.encode(cidade, "UTF-8") +
                    "&units=imperial&cnt=16&appid=" + apiKey; //para °Celsius = metric
            return new URL(urlString);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    //faz a chamada ao webservice REST para obter os dados e os salva em um arquivo HTML local
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) urls[0].openConnection();
                int response = connection.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK){
                    StringBuilder builder = new StringBuilder();
                    try(BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                            )){
                        String line;
                        while((line = reader.readLine()) != null ){
                            builder.append(line);
                        }
                    }catch (IOException e){
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString()); //aki!!!
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }catch (Exception ex){
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                ex.printStackTrace();
            }finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            convertJSONtoArrayList(jsonObject);
            weatherArrayAdapter.notifyDataSetChanged();//vincular a listview
            weatherListView.smoothScrollByOffset(0);//rola para o topo
        }

        //cirar objetos weather do JSONObject que contém a previsão:
        private void convertJSONtoArrayList(JSONObject forecast){
            weatherList.clear();
            try{
                //ober a lista JSONArray da previsao:
                JSONArray list = forecast.getJSONArray("list");
                //loop
                for(int i = 0; i < list.length(); ++i){
                    JSONObject day = list.getJSONObject(i);
                    JSONObject temperaturas = day.getJSONObject("temp");
                    //obter a descrição para o ícone:
                    JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                    //list:
                    weatherList.add(
                      new Weather(
                              day.getLong("dt"),
                              temperaturas.getDouble("min"),
                              temperaturas.optDouble("max"),
                              day.getDouble("humidity"),
                              weather.getString("description"),
                              weather.getString("icon")
                      )
                    );
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }


}