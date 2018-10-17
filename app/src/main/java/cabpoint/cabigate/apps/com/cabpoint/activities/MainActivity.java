package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import cabpoint.cabigate.apps.com.cabpoint.R;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    ImageView ivBack,ivMenu;
    Toolbar toolbar;
    MenuItem searchMenuItem;
    Uri uri;
    Context context;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        //setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ivBack = findViewById(R.id.toolbar_iv_back);
        ivMenu = findViewById(R.id.iv_drawer);
ivBack.setVisibility(View.GONE);
ivMenu.setVisibility(View.VISIBLE);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.menu_go_ride:

                        return true;
                    case R.id.menu_go_payment:

                        return true;
                    case R.id.menu_go_history:
                        return true;
                    case R.id.menu_go_freerides:
                        return true;
                    case R.id.menu_go_promotion:
                        return true;
                    case R.id.menu_go_setting:

                        return true;
                   case R.id.menu_go_contact_us:

                        return true;
                    case R.id.menu_go_logout:



                        return true;



                }
                return false;
            }
        });
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        setHeader();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void exitdialog()
    {
        AlertDialog.Builder   alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        }


        // set title
        alertDialogBuilder.setTitle("Sant Maskeen ji Katha");

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)

                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                       System.exit(0);
                        finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Rate Us",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                uri = Uri.parse("https://play.google.com/store/apps/details?id=sant.singh.ji.maskeen"); // missing 'http://' will cause crashed
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                //dialog.cancel();
                            }
                        });



        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void setHeader() {
        View header = navigationView.getHeaderView(0);
        TextView txtHeaderName = (TextView) header.findViewById(R.id.header_name);
        TextView txtHeaderEmail = (TextView) header.findViewById(R.id.header_email);
      ImageView imgProfile = (ImageView) header.findViewById(R.id.header_image);

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        //mAdd.resume(this);
        super.onResume();

    }
    @Override
    public void onBackPressed() {
        exitdialog();
    }

    @Override
    protected void onDestroy() {

        //mAdd.destroy(this);
        super.onDestroy();
    }
}
