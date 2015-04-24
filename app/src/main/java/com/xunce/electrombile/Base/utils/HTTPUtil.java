package com.xunce.electrombile.Base.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by heyukun on 2015/4/24.
 */
public class HTTPUtil {
    public static JSONArray HTTPGet(final String httpUrl){
        FutureTask<JSONArray> task = new FutureTask<JSONArray>(
                new Callable<JSONArray>() {
                    @Override
                    public JSONArray call() throws Exception {
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet(httpUrl);
                        try {
                            HttpResponse response = client.execute(get);
                            if(response.getStatusLine().getStatusCode() == 200){
                                String result = EntityUtils.toString(response.getEntity());
                                return new JSONArray(result);
                            }
                            return null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });

        new Thread(task).start();

        try {
            //wait the result of http get, if wait for more than 5 secs, return null
            return task.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            return null;
        }
    }
}
