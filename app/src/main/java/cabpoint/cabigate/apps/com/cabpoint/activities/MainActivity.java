package cabpoint.cabigate.apps.com.cabpoint.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.fragments.Home;
import cabpoint.cabigate.apps.com.cabpoint.listeners.GPSTracker;
import cabpoint.cabigate.apps.com.cabpoint.models.LogoutInput.LogoutInput;
import cabpoint.cabigate.apps.com.cabpoint.models.LogoutOutput.LogoutOutput;
import cabpoint.cabigate.apps.com.cabpoint.models.loginInput.LoginModel;
import cabpoint.cabigate.apps.com.cabpoint.models.loginoutput.LoginOutput;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;
import cabpoint.cabigate.apps.com.cabpoint.utilities.HttpHandler;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    ImageView ivBack, ivMenu;
    Toolbar toolbar;
    MenuItem searchMenuItem;
    private FragmentManager fragment;
    private GPSTracker gps;
    SharedPreferencesHelper sharedPreferencesHelper;
    Uri uri;
    Context context;
    Intent intent;
    public static GoogleApiClient mGoogleApiClient;
    private static final int GOOGLE_API_CLIENT_ID = 0;


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
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        ivBack.setVisibility(View.GONE);
        ivMenu.setVisibility(View.VISIBLE);
        checkLocationPermission();
        initmGoogleApiClient();
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
                        logout();
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

    private void logout()
    {

        new LogoutTask().execute();
    }
    private class LogoutTask extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.show();
        pDialog.setCancelable(false);
    }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... Url) {
        String response = "";
        try {
            httpHandler = new HttpHandler();
            HashMap<String, String> headerParams = new HashMap<>();
            HashMap<String, String> bodyParams = new HashMap<>();
            String jsonObject = new Gson().toJson(requestBody(), LogoutInput.class);
            bodyParams.put("data", jsonObject);
            response = httpHandler.httpPost(Constants.CABPOINT_LOGOUT, headerParams, bodyParams, null);
            Log.e("Logout Url", Constants.CABPOINT_LOGOUT);
            Log.e("body", String.valueOf(bodyParams));
            Log.e("Response", response);
            LogoutOutput logoutcode = new Gson().fromJson(response,LogoutOutput.class);
            if(logoutcode.getStatus().equals(1))
            {
                if(logoutcode.getResponse()!=null)
                {

                    Helpers.displayMessage(MainActivity.this, true, logoutcode.getResponse().getMsg());
                    sharedPreferencesHelper.clearPreferenceStore();
                    //moveTaskToBack(true);
                    finish();
                    Intent main =new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(main);


                }
            }
            else
            {

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    if(json.getString("error")!=null) {
                        String error = json.getString("error");
                        Helpers.displayMessage(MainActivity.this, true, error);
                    }
                    String errorMessage = json.getString("error_msg");
                    String status = json.getString("status");
                    if(status.equals("0")) {
                        Helpers.displayMessage(MainActivity.this, true, errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


            //  return response.toString();
        } catch (Exception exception) {
            if (response.equals("")) {
                Helpers.displayMessage(MainActivity.this, true, exception.getMessage());
                //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                //pDialog.dismiss();
            } else {
                Helpers.displayMessage(MainActivity.this, true, exception.getMessage());
            }
        }

        return null;
    }

        @Override
        protected void onPostExecute(String result) {
        pDialog.dismiss();

//            parseErrorResponse(result);
    }


    }
    private LogoutInput requestBody() {
        LogoutInput headerRequest = new LogoutInput ();
     headerRequest.setCompanySerial("2");
     headerRequest.setUserid(Integer.valueOf(sharedPreferencesHelper.getString(Constants.USERID)));
     headerRequest.setToken(sharedPreferencesHelper.getString(Constants.TOKEN));
     return headerRequest;
    }
    private void initmGoogleApiClient() {


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();

    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    private boolean checkGps() {
        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            return true;
        } else {
            gps.showSettingsAlert();
        }
        return false;
    }
    public void checkLocationPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (checkGps())
                            {
                                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.Container, new Home());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            //Toast.makeText(getActivity().getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

    }

    private void exitdialog() {
        AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        }


        // set title
        alertDialogBuilder.setTitle("CabPoint");

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)

                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.finishAffinity(MainActivity.this);
                        finish();
                        moveTaskToBack(true);
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
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
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }
        if (checkGps())
        {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.Container, new Home());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
