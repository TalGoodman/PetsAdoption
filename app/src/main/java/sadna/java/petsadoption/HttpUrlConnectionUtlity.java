package sadna.java.petsadoption;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class HttpUrlConnectionUtlity extends AsyncTask<Integer, Void, String> {
    private static final String TAG = "HttpUrlConnectionUtlity";
    Context mContext;
    public static final int GET_METHOD = 0,
            POST_METHOD = 1,
            PUT_METHOD = 2,
            HEAD_METHOD = 3,
            DELETE_METHOD = 4,
            TRACE_METHOD = 5,
            OPTIONS_METHOD = 6;
    HashMap<String, String> headerMap;

    String entityString;
    String url;
    int requestType = -1;
    final String timeOut = "TIMED_OUT";

    int TIME_OUT = 60 * 1000;

    public HttpUrlConnectionUtlity (Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {
        int requestType = getRequestType();
        String response = "";
        try {


            URL url = getUrl();
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection = setRequestMethod(urlConnection, requestType);
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setReadTimeout(TIME_OUT);
            urlConnection.setDoOutput(false);
            urlConnection = setHeaderData(urlConnection);
            urlConnection = setEntity(urlConnection);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                response = readResponseStream(urlConnection.getInputStream());
                Log.v(TAG, response);
            }
            urlConnection.disconnect();
            return response;


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            return timeOut;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "ALREADY CONNECTED");
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (TextUtils.isEmpty(response)) {
            //empty response
        } else if (response != null && response.equals(timeOut)) {
            //request timed out
        } else    {
            //process your response
        }
    }


    private String getEntityString() {
        return entityString;
    }

    public void setEntityString(String s) {
        this.entityString = s;
    }

    private String readResponseStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private HttpURLConnection setEntity(HttpURLConnection urlConnection) throws IOException {
        if (getEntityString() != null) {
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(getEntityString());
            writer.flush();
            writer.close();
            outputStream.close();
        } else {
            Log.w(TAG, "NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND ||NO ENTITY DATA TO APPEND");
        }
        return urlConnection;
    }

    private HttpURLConnection setHeaderData(HttpURLConnection urlConnection) throws UnsupportedEncodingException {
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        if (getHeaderMap() != null) {
            for (Map.Entry<String, String> entry : getHeaderMap().entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        } else {
            Log.w(TAG, "NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND ||NO HEADER DATA TO APPEND");
        }
        return urlConnection;
    }

    private HttpURLConnection setRequestMethod(HttpURLConnection urlConnection, int requestMethod) {
        try {
            switch (requestMethod) {
                case GET_METHOD:
                    urlConnection.setRequestMethod("GET");
                    break;
                case POST_METHOD:
                    urlConnection.setRequestMethod("POST");
                    break;
                case PUT_METHOD:
                    urlConnection.setRequestMethod("PUT");
                    break;
                case DELETE_METHOD:
                    urlConnection.setRequestMethod("DELETE");
                    break;
                case OPTIONS_METHOD:
                    urlConnection.setRequestMethod("OPTIONS");
                    break;
                case HEAD_METHOD:
                    urlConnection.setRequestMethod("HEAD");
                    break;
                case TRACE_METHOD:
                    urlConnection.setRequestMethod("TRACE");
                    break;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return urlConnection;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }   }