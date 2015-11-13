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

import pack.knowyourdoctor.Constants.Strings;

//Execute all Web POST Requests
public class WebTask_ExecutePostRequests
        extends AsyncTask<String, Void, String>
        implements WebTask_Interface {
    private Context context;
    private String message;
    private JSONObject jObject;
    private String url;

    //Getters and setters
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Run in background thread - Execution of web task
    @Override
    protected String doInBackground(String... params) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(params[0]);
        try {
            post.setEntity(new StringEntity(jObject.toString(), Strings.TEXT_TYPE));
            client.execute(post);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Execute after the web task executed
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //Method to start execution of current web task
    @Override
    public void executeWebTask() {
        this.execute(url);
    }
}
