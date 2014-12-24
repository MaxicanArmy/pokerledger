package org.pokerledger.pokerledgermobile;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
}
