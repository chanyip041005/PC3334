/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

/**
 *
 * @author Jonathan
 */
public class HttpConnect {

    protected String baseURL = "http://127.0.0.1/PasswordManager/";

    public HttpConnect() {

    }

    public JSONObject SendRequest(String pagePath) {
        return this.SendRequest(pagePath, null);
    }

    public JSONObject SendRequest(String pagePath, Map<String, Object> parameters) {
        JSONObject jsonObject = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(baseURL + pagePath + ".php");

        HttpResponse response;

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                urlParameters.add(new BasicNameValuePair(entry.getKey(), this.ConvertObjectToString(entry.getValue())));
            }
            try {
                //request.addHeader("content-type", "application/json");
                request.setEntity(new UrlEncodedFormEntity(urlParameters));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return jsonObject;
            }
        }

        try {
            response = httpClient.execute(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Send Request Fail");
            return jsonObject;
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            jsonObject = new JSONObject(result.toString());
            System.out.println(jsonObject.toString());
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Convert JSON Object Error.");
            return jsonObject;
        }
        
        return jsonObject;
    }

    public String ConvertObjectToString(Object object) {
        if (object instanceof Integer) {
            return Integer.toString((int) object);
        } else if (object instanceof Float) {
            return Float.toString((float) object);
        } else if (object instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String reportDate = df.format(object);
            return df.format(object);
        } else {
            return object.toString();
        }
    }
}
