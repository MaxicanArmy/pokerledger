package org.pokerledger.pokerledgermobile;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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
public class BackupActivity extends BaseActivity {
    private String username;
    private String password;
    private String confirm;
    private String dbPath;

    private WifiManager wifiManager;
    private WifiInfo wInfo;
    private String macAddress;

    private final String validateURL = "http://www.pokerledger.org/validation.php";
    private final String registerURL = "http://www.pokerledger.org/registration.php";
    private final String backupURL = "http://www.pokerledger.org/backupDB.php";
    private final String repositoryURL = "http://www.pokerledger.org/backups/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        dbPath = this.getDatabasePath("sessionManager").getAbsolutePath();

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
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

        username = ((EditText) findViewById(R.id.username)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();
        confirm = ((EditText) findViewById(R.id.confirm)).getText().toString();

        if (login.isChecked()) {
            if (checkInput(username, password, null)) {
                new ValidateLogin(this, 1).execute();
            }
        }
        else if (register.isChecked()) {
            if (checkInput(username, password, confirm)) {
                new RegisterUser(this).execute();
            }
        }
    }

    public void restoreDatabase(View view) {
        RadioButton login = (RadioButton) findViewById(R.id.radio_login);

        username = ((EditText) findViewById(R.id.username)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();
        confirm = ((EditText) findViewById(R.id.confirm)).getText().toString();


        if (login.isChecked()) {
            if (checkInput(username, password, null)) {
                new ValidateLogin(this, 0).execute();
            }
        }
        else {
            Toast.makeText(this, "You cannot restore a database while registering.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkInput(String u, String p, String c) {
        Boolean response = true;
        if (c != null) {
            if (!c.equals(p)) {
                Toast.makeText(this, "Password must equal confirmation.", Toast.LENGTH_SHORT).show();
                response = false;
            }
        }

        if (u.length() < 6 || u.length() > 12 || p.length() < 6 || p.length() > 12) {
            Toast.makeText(this, "Username and password must be 6 to 12 characters.", Toast.LENGTH_SHORT).show();
            response = false;
        } else {
            if (!p.matches("[a-zA-Z0-9]{6,12}")) {
                Toast.makeText(this, "Password can only contain alphanumeric characters.", Toast.LENGTH_SHORT).show();
                response = false;
            }

            if (!u.matches("[a-zA-Z0-9]{6,12}")) {
                Toast.makeText(this, "Username can only contain alphanumeric characters.", Toast.LENGTH_SHORT).show();
                response = false;
            }
        }

        if (response) {
            //if save info checkbox is checked
                //do db save info call here
        }

        return response;
    }

    public class ValidateLogin extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private ProgressDialog pdia;
        private int operation;

        public ValidateLogin (Context c, int i) {
            this.mContext = c;
            this.operation = i;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(mContext);
            pdia.setMessage("Logging in, please wait..." );
            pdia.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return postData();
        }

        @Override
        protected void onPostExecute(final String response){
            pdia.dismiss();

            if (response.equals("1")) {
                if (operation == 1) {
                    new UploadDatabase(mContext).execute();
                } else if (operation == 0) {
                    new RestoreDatabase(mContext).execute();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Information is incorrect.", Toast.LENGTH_SHORT).show();
            }
        }

        public String postData() {
            HttpClient httpclient = new DefaultHttpClient();
            // specify the URL you want to post to
            HttpPost httppost = new HttpPost(validateURL);
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

    public class RegisterUser extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private ProgressDialog pdia;

        public RegisterUser (Context c) {
            this.mContext = c;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = new ProgressDialog(mContext);
            pdia.setMessage("Registering, please wait...");
            pdia.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return postData();
        }

        @Override
        protected void onPostExecute(final String response){
            pdia.dismiss();

            if (response.equals("1")) {
                new UploadDatabase(mContext).execute();
            }
            else {
                Toast.makeText(getApplicationContext(), "That username is taken.", Toast.LENGTH_SHORT).show();
            }
        }

        public String postData() {
            HttpClient httpclient = new DefaultHttpClient();
            // specify the URL you want to post to
            HttpPost httppost = new HttpPost(registerURL);
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
            pdia.show();
        }

        @Override
        protected String doInBackground(String... args) {
            String reply = "failure";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(backupURL);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                File file = new File(dbPath);
                FileBody fb = new FileBody(file);

                builder.addPart("database", fb);
                builder.addTextBody("username", username);
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
                             * author Stephen Colebourne
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
                                publishProgress((int) ((totalSent / (float) totalSent) * 100)); //needs to be in a Asynctask to work
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
            pdia.dismiss();
            if (response.equals("success")) {
                Toast.makeText(getApplicationContext(), "Your database has been saved to the server.", Toast.LENGTH_SHORT).show();
            }
            else if (response.equals("failure")) {
                Toast.makeText(getApplicationContext(), "An error has occurred. Backup failed.", Toast.LENGTH_SHORT).show();
            }
        }

        public String getContent(HttpResponse response) throws IOException {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String body;
            String content = "";

            while ((body = rd.readLine()) != null)
            {
                content += body + "\n";
            }
            return content.trim();
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
            pdia.setMessage("Downloading database...");
            pdia.setIndeterminate(false);
            pdia.setMax(100);
            pdia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdia.setCancelable(true);
            pdia.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String path = mContext.getDatabasePath("sessionManager").getAbsolutePath();

            try {
                URL url = new URL(repositoryURL + username);
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
