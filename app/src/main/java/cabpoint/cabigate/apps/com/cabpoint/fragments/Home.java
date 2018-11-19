package cabpoint.cabigate.apps.com.cabpoint.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.activities.LoginActivity;
import cabpoint.cabigate.apps.com.cabpoint.activities.MainActivity;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.listeners.GPSTracker;
import cabpoint.cabigate.apps.com.cabpoint.models.LogoutInput.LogoutInput;
import cabpoint.cabigate.apps.com.cabpoint.models.LogoutOutput.LogoutOutput;
import cabpoint.cabigate.apps.com.cabpoint.models.calculatefareinput.FareInput;
import cabpoint.cabigate.apps.com.cabpoint.models.calculatefareoutput.FareOutput;
import cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistInput.VehicleListInput;
import cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistoutput.VehicleListOutput;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.DirectionsJSONParser;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;
import cabpoint.cabigate.apps.com.cabpoint.utilities.HttpHandler;

import static android.content.ContentValues.TAG;
import static cabpoint.cabigate.apps.com.cabpoint.utilities.Constants.loclat;

public class Home extends android.support.v4.app.Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private TextView whereTo;
    LatLng pickUplocation;
    LatLng dropOfflocation;
    SupportMapFragment fm;
    LocationRequest mLocationRequest;
    public static double currentLat, currentLng;
    private GoogleMap mGoogleMap;
    ImageView imgMyLocation;
    Button btnRequest,btnJourney;
    View rootView;
    private Marker tripdrop;
    private Marker trippick;
    SharedPreferencesHelper sHelper;
    Polyline polyline;
    String distance = "";
    String duration = "";
    Dialog popular;
    LinearLayout layout;
    Window window;
    WindowManager.LayoutParams wlp;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    ImageView sheettext,sheetimg,sheetforward,sheetback;
    TextView sheetname;
    ArrayList<HashMap<String, String>> imgList;
    public String carImageurl;
    public String carname;
    public String carmodel,carmodeltype;
    int height = 110;
    int width = 90;

    private int counter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sHelper = new SharedPreferencesHelper(getActivity());
        initMap();

    }

    public void checkPopular() {
        if (Constants.populateConfirmation) {
            sendRequest();
            Constants.populateConfirmation = false;
            //passengeracceptcheck = true;
        }
    }

    //Method use for confirmtrip draw path between passenger dropoff and pickup Address
    private void sendRequest() {
        pickUplocation = new LatLng(Double.parseDouble(sHelper.getString(WhereTo.userToLat)), Double.parseDouble(sHelper.getString(WhereTo.userToLng)));
        dropOfflocation = new LatLng(Double.parseDouble(sHelper.getString(WhereTo.userFromLat)), Double.parseDouble(sHelper.getString(WhereTo.userFromlng)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(dropOfflocation, 10);
        mGoogleMap.animateCamera(cameraUpdate);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.locationicon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        tripdrop = mGoogleMap.addMarker(new MarkerOptions()
                .position(pickUplocation)
                .title("Drop off Address")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        trippick = mGoogleMap.addMarker(new MarkerOptions()
                .position(dropOfflocation)
                .title("Pick Up Address")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(pickUplocation, dropOfflocation);
        showpopularDialouge();

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + Constants.MY_API_KEY;
        return url;
    }

    private void showpopularDialouge() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        getavailablecars();
        //layoutBottomSheet.setVisibility(View.VISIBLE);

    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
//            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);


                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance").replaceAll("[^\\.0123456789]", "");
                        //tripdistance = Integer.valueOf(distance);
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    if (lat > 0 && lng > 0) {
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    } else {
                        //Methods.customSingleAlert(activity, R.string.latlng);
                    }
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                // Drawing polyline in the Google Map for the i-th route
                if (null != lineOptions)
                    polyline = mGoogleMap.addPolyline(lineOptions);
                //getavailablecars();
//                if (regPre.getMode().equals("1")) {
//                    gettripfare();
//                }

            }

        }
    }

    private void getavailablecars() {

        new AvailableVehicle().execute();
    }

    //get available cars

    private class AvailableVehicle extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
                String jsonObject = new Gson().toJson(vehiclelistBody(), VehicleListInput.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_VEHICLE_LIST, headerParams, bodyParams, null);
                Log.e("Logout Url", Constants.CABPOINT_LOGOUT);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);
                VehicleListOutput vehicle = new Gson().fromJson(response, VehicleListOutput.class);
                if (vehicle.getStatus().equals(1)) {
                    if (vehicle.getResponse() != null) {
                        int count = Integer.parseInt(vehicle.getResponse().getCount());
                        if (vehicle.getResponse().getList().size() > 0  && vehicle.getResponse().getList()!=null) {
                            for (int i = 0; i < vehicle.getResponse().getList().size(); i++) {
                                carImageurl = vehicle.getResponse().getList().get(i).getIcon()==null ||vehicle.getResponse().getList().get(i).getIcon()==""?null:vehicle.getResponse().getList().get(i).getIcon();
                                carname = vehicle.getResponse().getList().get(i).getName()==null ||vehicle.getResponse().getList().get(i).getName()==""?null:vehicle.getResponse().getList().get(i).getName();
                                carmodel = vehicle.getResponse().getList().get(i).getTypeid()==null ||vehicle.getResponse().getList().get(i).getTypeid()==""?null:vehicle.getResponse().getList().get(i).getTypeid();
                                HashMap<String, String> contact = new HashMap<>();
                                contact.put("carname", carname);
                                contact.put("carimgcrul", carImageurl);
                                contact.put("carmodel",carmodel);
                                imgList.add(contact);
                                }
                        }



                        }
                } else {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        if (json.getString("error") != null) {
                            String error = json.getString("error");
                            Helpers.displayMessage(getActivity(), true, error);
                        }
                        String errorMessage = json.getString("error_msg");
                        String status = json.getString("status");
                        if (status.equals("0")) {
                            Helpers.displayMessage(getActivity(), true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(getActivity(), true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(getActivity(), true, exception.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (null != sheetimg)
                Picasso.with(getActivity()).
                        load(imgList.get(0).get("carimgcrul")).
                        memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE).
                        into(sheetimg);
            sheetname.setText(imgList.get(0).get("carname"));
            carmodeltype = imgList.get(0).get("carmodel");
            calculatetripfare();

//            parseErrorResponse(result);
        }
    }

    private VehicleListInput vehiclelistBody() {
        VehicleListInput headerRequest = new VehicleListInput();
        headerRequest.setLat(String.valueOf(currentLat));
        headerRequest.setLng(String.valueOf(currentLng));
        return headerRequest;
    }
    private FareInput fareBody() {
        FareInput headerRequest = new FareInput();
      headerRequest.setCompanySerial(Constants.COMPANYID);
      headerRequest.setUserid(Constants.USERID);
      headerRequest.setDropLat(sHelper.getString(WhereTo.userFromLat));
      headerRequest.setDropLng(sHelper.getString(WhereTo.userFromlng));
      headerRequest.setPickupLat(sHelper.getString(WhereTo.userToLat));
      headerRequest.setPickupLng(sHelper.getString(WhereTo.userToLng));
      headerRequest.setTaxiModel(carmodeltype);
        return headerRequest;
    }

    //calculate trip fare

    private class CalculateTripFare extends AsyncTask<String, Void, String>

    {

        private HttpHandler httpHandler;

        //ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(getActivity());
//            pDialog.setMessage(getResources().getString(R.string.loading));
//            pDialog.setIndeterminate(false);
//            pDialog.show();
//            pDialog.setCancelable(false);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... Url) {
            String response = "";
            try {
                httpHandler = new HttpHandler();
                HashMap<String, String> headerParams = new HashMap<>();
                HashMap<String, String> bodyParams = new HashMap<>();
                String jsonObject = new Gson().toJson(fareBody(), FareInput.class);
                bodyParams.put("data", jsonObject);
                response = httpHandler.httpPost(Constants.CABPOINT_CALCULATE_FARE, headerParams, bodyParams, null);
                Log.e("Fare Url", Constants.CABPOINT_LOGOUT);
                Log.e("body", String.valueOf(bodyParams));
                Log.e("Response", response);
               FareOutput fare = new Gson().fromJson(response,  FareOutput.class);
                if (fare.getStatus().equals(1)) {
                    if (fare.getResponse() != null) {
//                        int count = Integer.parseInt(vehicle.getResponse().getCount());
//                        if (vehicle.getResponse().getList().size() > 0  && vehicle.getResponse().getList()!=null) {
//                            for (int i = 0; i < vehicle.getResponse().getList().size(); i++) {
//                                carImageurl = vehicle.getResponse().getList().get(i).getIcon()==null ||vehicle.getResponse().getList().get(i).getIcon()==""?null:vehicle.getResponse().getList().get(i).getIcon();
//                                carname = vehicle.getResponse().getList().get(i).getName()==null ||vehicle.getResponse().getList().get(i).getName()==""?null:vehicle.getResponse().getList().get(i).getName();
//                                HashMap<String, String> contact = new HashMap<>();
//                                contact.put("carname", carname);
//                                contact.put("carimgcrul", carImageurl);
//                                imgList.add(contact);
//                            }
//                        }



                    }
                } else {
                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        if (json.getString("error") != null) {
                            String error = json.getString("error");
                            Helpers.displayMessage(getActivity(), true, error);
                        }
                        String errorMessage = json.getString("error_msg");
                        String status = json.getString("status");
                        if (status.equals("0")) {
                            Helpers.displayMessage(getActivity(), true, errorMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


                //  return response.toString();
            } catch (Exception exception) {
                if (response.equals("")) {
                    Helpers.displayMessage(getActivity(), true, exception.getMessage());
                    //showResponseDialog( mContext.getResources().getString(R.string.alert),exception.getMessage());
                    //pDialog.dismiss();
                } else {
                    Helpers.displayMessage(getActivity(), true, exception.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
           // pDialog.dismiss();
//            if (null != sheetimg)
//                Picasso.with(getActivity()).
//                        load(imgList.get(0).get("carimgcrul")).
//                        memoryPolicy(MemoryPolicy.NO_CACHE)
//                        .networkPolicy(NetworkPolicy.NO_CACHE).
//                        into(sheetimg);
//            sheetname.setText(imgList.get(0).get("carname"));
            //calculatetripfare();

//            parseErrorResponse(result);
        }
    }

    private void initMap() {
        fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        imgMyLocation = (ImageView) rootView.findViewById(R.id.imgMyLocation);
        whereTo = (TextView) rootView.findViewById(R.id.where_to);
        imgList = new ArrayList<>();
        layoutBottomSheet = rootView.findViewById(R.id.bottom_sheet);
        sheettext = rootView.findViewById(R.id.sheettest);
        sheetimg = rootView.findViewById(R.id.imgCar);
        sheetback=rootView.findViewById(R.id.backward);
        sheetforward = rootView.findViewById(R.id.forward);
        sheetname = rootView.findViewById(R.id.carname);
        btnRequest = rootView.findViewById(R.id.requestride);
        btnJourney = rootView.findViewById(R.id.bookjourney);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRide();

            }
        });
        btnJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookJourney bookJourney = new BookJourney();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.Container, bookJourney);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        counter = 0;
        sheetback.setImageDrawable(null);
        sheetback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter--;
               sheetforward.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp));
               imgList.get(counter);

                sheetname.setText(imgList.get(counter).get("carname"));

                if (null != sheetimg)
                    Picasso.with(getActivity()).
                            load( imgList.get(counter).get("carimgcrul")).
                            memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE).
                            into(sheetimg);
                carmodeltype = (imgList.get(counter).get("carmodel"));
                calculatetripfare();

                if (counter == 0) {
                   sheetback.setImageDrawable(null);
                }
            }


        });
        sheetforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (counter < imgList.size() - 1) {
                    counter++;
                    sheetback.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));
                    imgList.get(counter);

                    sheetname.setText(imgList.get(counter).get("carname"));

                    if (null != sheetimg)
                        Picasso.with(getActivity()).
                                load( imgList.get(counter).get("carimgcrul")).
                                memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE).
                                into(sheetimg);
                    if (counter == imgList.size() - 1) {
                       sheetforward.setImageDrawable(null);
                    }
                }


            }

        });

        //layoutBottomSheet.setVisibility(View.GONE);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //sheetBehavior.setPeekHeight(0);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                           sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp, getActivity().getApplicationContext().getTheme()));
                        } else {
                            sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                        }
                        //sheettext.setText("Close Sheet");
                        //sheettext.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp, getActivity().getApplicationContext().getTheme()));
                        } else {
                            sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                        }
                        //sheettext.setBackgroundResource(R.drawable.i);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        sheettext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp, getActivity().getApplicationContext().getTheme()));
                    } else {
                        sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
                    }
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp, getActivity().getApplicationContext().getTheme()));
                    } else {
                        sheettext.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
                    }
                }
            }
        });


        imgMyLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMyLocation();

            }
        });
        whereTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhereTo whereto = new WhereTo();
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.Container, whereto);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //fm.getMapAsync(this);
        if (fm == null) {
            android.support.v4.app.FragmentManager fg = getFragmentManager();
            FragmentTransaction ft = fg.beginTransaction();
            fm = SupportMapFragment.newInstance();
            ft.replace(R.id.home_map, fm).commit();
        }
        fm.getMapAsync(this);
    }

    private void getMyLocation() {
        LatLng latLng = new LatLng(currentLat, currentLng);
        if (currentLat > 0 && currentLng > 0) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        map.clear();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
       // map.setTrafficEnabled(true);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.mystyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        buildGoogleApiClient();
        mGoogleMap.setMyLocationEnabled(true);
        checkPopular();
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        currentLat = location.getLatitude();
        currentLng = location.getLongitude();
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        setMarker(location);
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private  void  requestRide()
    {

    }
    private void calculatetripfare()
    {
        new CalculateTripFare().execute();
    }

    private void setMarker(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.locationicon);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }


}
