package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.pokerledger.pokerledgermobile.helper.DatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 12/23/14.
 */
public class BackupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.add_session :
                FragmentManager manager = getFragmentManager();

                AddSessionFragment dialog = new AddSessionFragment();
                dialog.show(manager, "AddSession");
                break;
            case R.id.history :
                Intent history = new Intent(this, HistoryActivity.class);
                this.startActivity(history);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleLoginRegister(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        View confirm = findViewById(R.id.confirm);
        View restore = findViewById(R.id.restore);

        // Check which radio button was clicked
        if (checked) {
            switch (view.getId()) {
                case R.id.radio_login:
                    confirm.setVisibility(View.GONE);
                    restore.setVisibility(View.VISIBLE);
                    break;
                case R.id.radio_register:
                    confirm.setVisibility(View.VISIBLE);
                    restore.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public void backupDatabase(View view) {
        RadioButton login = (RadioButton) findViewById(R.id.radio_login);
        RadioButton register = (RadioButton) findViewById(R.id.radio_register);

        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String confirm = ((EditText) findViewById(R.id.confirm)).getText().toString();
        String backupUrl = "http://www.pokerledger.org/backupDB.php";
        String dbPath = this.getDatabasePath("sessionManager").getAbsolutePath();

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        if (login.isChecked()) {
            new UploadDatabase(this).execute(backupUrl, dbPath, username, password, macAddress, "login");
        }
        else if (register.isChecked()) {
            if (password.equals(confirm)) {
                new UploadDatabase(this).execute(backupUrl, dbPath, username, password, macAddress, "register");
            }
            else {
                Toast.makeText(this, "Password does not match confirmation.", Toast.LENGTH_SHORT);
            }
        }
    }

    public void restoreDatabase(View view) {
        RadioButton login = (RadioButton) findViewById(R.id.radio_login);

        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String restoreUrl = "http://www.pokerledger.org/restoreDB.php";

        if (login.isChecked()) {
            new CheckCredentials(this).execute(username, password, restoreUrl);
        }
        else {
            Toast.makeText(this, "You cannot restore a database while registering.", Toast.LENGTH_SHORT);
        }
    }

    public class UploadDatabase extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private ProgressDialog pdia;

        public UploadDatabase (Context c) {
            this.mContext = c;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(mContext);
            pdia.setMessage("Uploading database...");
            pdia.setIndeterminate(false);
            pdia.setMax(100);
            pdia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdia.setCancelable(true);
            //pdia.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String reply = "failure";
            try {
                reply = postFile(args[0], args[1], args[2], args[3], args[4], args[5]);
            } catch (Exception e) {
                //gasp an exception, fuck off java
            }
            return reply;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pdia.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            Log.v("response", response);
            if (response.equals("success")) {
                Toast.makeText(mContext, "Your database has been saved to the server.", Toast.LENGTH_SHORT);
            }
            else if (response.equals("duplicate")) {
                Toast.makeText(mContext, "That username is taken.", Toast.LENGTH_SHORT);
            }
            else if (response.equals("failure")) {
                Toast.makeText(mContext, "An error has occurred. Backup failed.", Toast.LENGTH_SHORT);
            }
            pdia.dismiss();
        }

        public String postFile(String URL, String fileName, String userName, String password, String macAddress, String process) throws Exception {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            final File file = new File(fileName);
            FileBody fb = new FileBody(file);

            builder.addPart("database", fb);
            builder.addTextBody("username", userName);
            builder.addTextBody("password", password);
            builder.addTextBody("process", process);
            builder.addTextBody("macAddress",  macAddress); //not sure there is any reason to send the macAddress
            final HttpEntity yourEntity = builder.build();

            class ProgressiveEntity implements HttpEntity {
                @Override
                public void consumeContent() throws IOException {
                    yourEntity.consumeContent();
                }
                @Override
                public InputStream getContent() throws IOException,
                        IllegalStateException {
                    return yourEntity.getContent();
                }
                @Override
                public Header getContentEncoding() {
                    return yourEntity.getContentEncoding();
                }
                @Override
                public long getContentLength() {
                    return yourEntity.getContentLength();
                }
                @Override
                public Header getContentType() {
                    return yourEntity.getContentType();
                }
                @Override
                public boolean isChunked() {
                    return yourEntity.isChunked();
                }
                @Override
                public boolean isRepeatable() {
                    return yourEntity.isRepeatable();
                }
                @Override
                public boolean isStreaming() {
                    return yourEntity.isStreaming();
                } // CONSIDER put a _real_ delegator into here!

                @Override
                public void writeTo(OutputStream outstream) throws IOException {

                    class ProxyOutputStream extends FilterOutputStream {
                        /**
                         * @author Stephen Colebourne
                         */

                        public ProxyOutputStream(OutputStream proxy) {
                            super(proxy);
                        }
                        public void write(int idx) throws IOException {
                            out.write(idx);
                        }
                        public void write(byte[] bts) throws IOException {
                            out.write(bts);
                        }
                        public void write(byte[] bts, int st, int end) throws IOException {
                            out.write(bts, st, end);
                        }
                        public void flush() throws IOException {
                            out.flush();
                        }
                        public void close() throws IOException {
                            out.close();
                        }
                    } // CONSIDER import this class (and risk more Jar File Hell)

                    class ProgressiveOutputStream extends ProxyOutputStream {
                        long totalSent;
                        public ProgressiveOutputStream(OutputStream proxy) {
                            super(proxy);
                            totalSent = 0;
                        }
                        public void write(byte[] bts, int st, int end) throws IOException {

                            totalSent += end;
                            //progress.publish((int) ((totalSent / (float) totalSize) * 100)); needs to be in a Asynctask to work
                            out.write(bts, st, end);
                        }
                    }

                    yourEntity.writeTo(new ProgressiveOutputStream(outstream));
                }

            };
            ProgressiveEntity myEntity = new ProgressiveEntity();

            post.setEntity(myEntity);
            HttpResponse response = client.execute(post);

            return getContent(response);
        }

        public String getContent(HttpResponse response) throws IOException {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String body = "";
            String content = "";

            while ((body = rd.readLine()) != null)
            {
                content += body + "\n";
            }
            return content.trim();
        }
    }

    private class CheckCredentials extends AsyncTask<String, Integer, String>{
        private Context mContext;
        private ProgressDialog pdia;

        public CheckCredentials (Context c) {
            this.mContext = c;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(mContext);
            pdia.setMessage("Validating Credentials...");
            pdia.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return postData(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(final String responseString){
            pdia.dismiss();

            if (!responseString.equals("")) {
                new RestoreDatabase(mContext).execute(responseString);
            }
        }

        public String postData(String username, String password, String url) {
            HttpClient httpclient = new DefaultHttpClient();
            // specify the URL you want to post to
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = null;
            try {
                // create a list to store HTTP variables and their values
                List nameValuePairs = new ArrayList();
                // add an HTTP variable and value pair
                nameValuePairs.add(new BasicNameValuePair("username", username));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // send the variable and value, in other words post, to the URL
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // process exception
            } catch (IOException e) {
                // process exception
            }

            HttpEntity entity = response.getEntity();
            String responseString = "";
            try {
                responseString = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                //really?
            }

            return responseString;
        }
    }

    public class RestoreDatabase extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private ProgressDialog pdia;

        public RestoreDatabase (Context c) {
            this.mContext = c;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(mContext);
            pdia.setMessage("Downloading update...");
            pdia.setIndeterminate(false);
            pdia.setMax(100);
            pdia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdia.setCancelable(true);
            pdia.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String path = mContext.getDatabasePath("sessionManager").getAbsolutePath();

            Log.v("testing", sUrl[0]);
            try {
                URL url = new URL(sUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000); //set timeout to 10 seconds
                connection.setDoOutput(true);
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (java.net.SocketTimeoutException e) {
                //MAKE TOAST that states connection timed out
                return null;
            } catch (Exception e) {
                //more exception catching, thanks java
            }
            return path;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pdia.setProgress(progress[0]);
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(String path) {
            pdia.dismiss();
        }
    }
}
