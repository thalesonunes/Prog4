package br.edu.uemg.progiv.weatherviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    private static class ViewHolder{
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    //armazenamento de Bitmaps baixados, para reutilização:
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    //construtor para iniciar membros herdados da superclasse:
    public WeatherArrayAdapter(Context context, List<Weather> forecast){
        super(context, -1, forecast);
    }

    //Cria as views personalizadas para os itens de ListView:
    @SuppressLint("StringFormatInvalid")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //obter o objeto Weather para esta posição de ListView especificada:
        Weather day = getItem(position);
        ViewHolder viewHolder; //objeto que referencia as views do item da lista
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.conditionImageView = (ImageView) convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView        = (TextView)  convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView        = (TextView)  convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView         = (TextView)  convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView   = (TextView)  convertView.findViewById(R.id.humidityTextView);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //se o ícone da condição climática já foi baixado, utilizá-lo, caso contrário, fazer download:
        if(bitmaps.containsKey(day.iconURL)){
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURL));
        }else{
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURL);
        }
        //obter outros dados do objeto Weather e colocar na view:
        Context context = getContext();

        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.hiTextView.setText(context.getString(R.string.hight_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap>{
        private ImageView imageView; //exibe a miniatura
        public LoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                try(InputStream inputStream = connection.getInputStream()){
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(strings[0],bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                connection.disconnect();
            }
            return bitmap;
        }

        //configura a imagem da condição no item da lista
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

}
