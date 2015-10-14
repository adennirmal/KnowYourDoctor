package WebServiceAccess;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class WebTask_ExecutePostRequests extends AsyncTask<String, Void, String> {
    private Context context;
    private String message;
    private JSONObject jObject;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getjObject() {
        return jObject;
    }

    public void setjObject(JSONObject jObject) {
        this.jObject = jObject;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(params[0]);
        try {
            post.setEntity(new StringEntity(jObject.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
