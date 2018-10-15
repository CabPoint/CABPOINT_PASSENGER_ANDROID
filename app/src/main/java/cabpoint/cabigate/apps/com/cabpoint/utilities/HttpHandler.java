package cabpoint.cabigate.apps.com.cabpoint.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by Muhammad Umair on 02-03-2018.
 */

public class HttpHandler {

    public HttpHandler() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String httpPost(String requestUrl, HashMap<String,String>headerParams, HashMap<String,String>bodyParams,String jsonData) throws Exception {
        try
        {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(Constants.POST);
            conn.setRequestProperty(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_FORM_URL_ENCODED);
            for (HashMap.Entry<String, String> entry : headerParams.entrySet())
            {
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }
            byte[] postData = Helpers.urlParamBuilders(bodyParams).getBytes( StandardCharsets.UTF_8 );
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream=new BufferedInputStream(conn.getInputStream());
                return convertStreamToString(inputStream);
            }
            else
            {
                InputStream inputStream = new BufferedInputStream(conn.getErrorStream());
                return convertStreamToString(inputStream);
            }
        }
        catch (Exception e) {
            throw new Exception("Exception occured : " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String httpGet(String requestUrl,HashMap<String, String> headerParams) throws Exception {
        try
        {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            for (HashMap.Entry<String, String> entry : headerParams.entrySet())
            {
                conn.setRequestProperty(entry.getKey(),entry.getValue());
            }
            conn.setRequestMethod(Constants.GET);
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK)
            {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                return convertStreamToString(inputStream);
            }
            else
            {
                InputStream inputStream = new BufferedInputStream(conn.getErrorStream());
                return convertStreamToString(inputStream);
            }
        }
        catch (Exception e) {
            throw new Exception("Exception occured : " + e.getMessage());
        }
    }





    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
