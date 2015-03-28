package com.xunce.electrombile.network;

import android.preference.PreferenceActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by heyukun on 2015/3/28.
 */
public class JsonManager {
    public JsonManager(){

    }

    public int requestHttp(final String url,final String[] key, final int[] value) {
        FutureTask<Integer> task = new FutureTask<Integer>(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {

                        int status = 0;
                        DefaultHttpClient mHttpClient = new DefaultHttpClient();
                        HttpPut mPut = new HttpPut(url);

                        //handle key, value
                        JSONObject param = new JSONObject();
                        int size = key.length;
                        for (int i = 0; i < size; i++) {
                            try {
                                param.put(key[i], value[i]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //bind  param to entity
                        try {
                            StringEntity se = new StringEntity(param.toString(), HTTP.UTF_8);
                            mPut.setEntity(se);
                        } catch (UnsupportedEncodingException e1) {
                            // TODOAuto-generated catch block
                            e1.printStackTrace();
                        }
                        try {
                            //Socket timeout 6s
                            mHttpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 6000);

                            // connect timeout 6s
                            mHttpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 6000);

                            //execute post
                            HttpResponse response = mHttpClient.execute(mPut);

                            //handle response
                            int res = response.getStatusLine().getStatusCode();
                            if (res == 200) {
                                status = 1;
                            } else if (res == 404) {
                                status = 404;
                            } else if (res == 500) {
                                status = 500;
                            }
                        } catch (ClientProtocolException e) {
                            // TODOAuto-generated catchblock
                            e.printStackTrace();
                            status = 900;
                        } catch (ConnectTimeoutException e) {
                            // TODOAuto-generated catchblock
                            e.printStackTrace();
                            status = 901;
                        } catch (InterruptedIOException e) {
                            // TODOAuto-generated catchblock
                            e.printStackTrace();
                            status = 902;
                        } catch (IOException e) {
                            // TODOAuto-generated catchblock
                            e.printStackTrace();
                            status = 903;
                        }
                        return status;
                    }
                });
        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
