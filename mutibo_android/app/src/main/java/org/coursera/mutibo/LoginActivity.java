package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class LoginActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnLogin_clicked(View p_view)
    {
        Intent f_intent = new Intent(this, MenuActivity.class);
        startActivity(f_intent);
    }
}
