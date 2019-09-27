package com.bitstobyte.babajishivram.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


import java.util.HashMap;

/**
 * @author Tushar Sonu Lambole
 * 
 */
public class HttpAsyncTask {

    IHttpAsyncTask iHttpAsyncTask;
    String strUrl, diaMsg;
    Context curContext;
    Boolean showProcessDia = false;
    HashMap<Object, Object> hashMapPostParam;
    int task_id;
    Object object;

    public HttpAsyncTask(Context context, IHttpAsyncTask asyncTask, String url,
                         Boolean showProcessDialog, String ProcessMsg,
                         HashMap<Object, Object> PostParam, int taskID) {
        curContext = context;
        showProcessDia = showProcessDialog;
        iHttpAsyncTask = asyncTask;
        strUrl = url;
        diaMsg = ProcessMsg;
        task_id = taskID;

        if (ProcessMsg.equalsIgnoreCase("") || ProcessMsg == null)
            diaMsg = "Loading.. Please wait";

        hashMapPostParam = new HashMap<Object, Object>(2);
        hashMapPostParam.put("url", url);
        hashMapPostParam.put("param", PostParam);

        if (PostParam == null) {
            Toast.makeText(curContext, "Error. Supply data for Post Method.",
                    Toast.LENGTH_LONG).show();
        } else if (PostParam.size() <= 0) {
            Toast.makeText(curContext,
                    "Error. Supplied blank data for Post Method.",
                    Toast.LENGTH_LONG).show();
        } else if (APIConstants.IsNetworkConnected(curContext)) {
            new HttpAsyncPOSTTask().execute(hashMapPostParam);
        } else {
            Toast.makeText(curContext, "No Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }


    private class HttpAsyncPOSTTask extends
            AsyncTask<HashMap<Object, Object>, Void, Object> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            if (showProcessDia) {
                pd = new ProgressDialog(curContext);
                pd.setMessage(diaMsg);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(HashMap<Object, Object>... urls) {
            return SoapAPICommonMethods.POST(urls[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            if (showProcessDia && pd != null)
                pd.dismiss();
            iHttpAsyncTask.APIResult(result, task_id);
            iHttpAsyncTask.APIResultWithObject(result, task_id, object);
        }

    }

}
