package com.patatascrucks.mobile.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.patatascrucks.mobile.R;
import com.patatascrucks.mobile.model.Charge;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_bill:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.bill));
                startActivity(intent);
                return true;
            case R.id.nav_note:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.note));
                startActivity(intent);
                return true;
            case R.id.nav_charge:
                intent = new Intent(this, Charge.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.charge));
                startActivity(intent);
                return true;
            case R.id.nav_dispatch:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.dispatch));
                startActivity(intent);
                return true;
            case R.id.nav_return:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.returns));
                startActivity(intent);
                return true;
            case R.id.nav_change:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.change));
                startActivity(intent);
                return true;
            case R.id.nav_gift:
                intent = new Intent(this, com.patatascrucks.mobile.model.Transaction.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.gift));
                startActivity(intent);
                return true;
            case R.id.nav_diary:
                intent = new Intent(this, com.patatascrucks.mobile.reports.Diary.class);
                intent.putExtra(getResources().getString(R.string.app_name), getResources().getString(R.string.diary));
                startActivity(intent);
                return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
