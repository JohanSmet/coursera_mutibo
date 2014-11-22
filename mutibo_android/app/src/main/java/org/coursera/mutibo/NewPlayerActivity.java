package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NewPlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_player);

        txtNickName = (EditText) findViewById(R.id.txtNickname);
        lblFailed   = (TextView) findViewById(R.id.lblFailed);

        txtNickName.setText(GlobalState.getNickName());
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        syncServiceClient.bind();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        syncServiceClient.unbind();
    }

    public void btnNewLogin_clicked(View p_view)
    {
        new AsyncTask<Integer, Integer, Boolean>() {
            @Override
            public Boolean doInBackground(Integer... params)
            {
                if (txtNickName.getText().length() <= 0 || txtNickName.getText().length() > 32)
                    return false;

                return syncServiceClient.getSyncService().changeUserName(txtNickName.getText().toString());
            }

            protected void onPostExecute(Boolean result)
            {
                lblFailed.setVisibility((result) ? View.VISIBLE : View.INVISIBLE);

                if (result)
                {
                    finish();
                    startActivity(new Intent(NewPlayerActivity.this, MenuActivity.class));
                }
            }

        }.execute();
    }

    // member variables
    SyncServiceClient syncServiceClient = new SyncServiceClient(this);
    EditText          txtNickName;
    TextView          lblFailed;
}
