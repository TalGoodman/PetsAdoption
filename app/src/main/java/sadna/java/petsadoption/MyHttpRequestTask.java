package sadna.java.petsadoption;

import android.os.AsyncTask;
import android.util.Log;
//import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHttpRequestTask extends AsyncTask<String,Integer,String> {
    public interface ApplicationConstant {
        String TAG = "DEBUG1";
    }
    @Override
    protected String doInBackground(@NonNull String... params) {
        String my_url = params[0];
        String my_data = params[1];
        try {
            URL url = new URL(my_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // setting the  Request Method Type
            httpURLConnection.setRequestMethod("GET");
            // adding the headers for request
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            try{
                //to tell the connection object that we will be writing some data on the server and then will fetch the output result
                httpURLConnection.setDoOutput(true);
                // this is used for just in case we don't know about the data size associated with our request
                httpURLConnection.setChunkedStreamingMode(1);

                // to write tha data in our request
                OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(my_data);
                outputStreamWriter.flush();
                outputStreamWriter.close();


                // to log the response code of your request
                Log.d(ApplicationConstant.TAG, "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseCode());
                // to log the response message from your server after you have tried the request.
                Log.d(ApplicationConstant.TAG, "MyHttpRequestTask doInBackground : " +httpURLConnection.getResponseMessage());
                Log.d(ApplicationConstant.TAG, "MyHttpRequestTask doInBackground : " +httpURLConnection.getInputStream());

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                // this is done so that there are no open connections left when this task is going to complete
                httpURLConnection.disconnect();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}