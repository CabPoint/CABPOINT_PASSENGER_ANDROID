package cabpoint.cabigate.apps.com.cabpoint.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.adapters.PlaceSearchAdapter;
import cabpoint.cabigate.apps.com.cabpoint.database.SharedPreferencesHelper;
import cabpoint.cabigate.apps.com.cabpoint.listeners.GPSTracker;
import cabpoint.cabigate.apps.com.cabpoint.models.AddressDao;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Constants;
import cabpoint.cabigate.apps.com.cabpoint.utilities.Helpers;

import static cabpoint.cabigate.apps.com.cabpoint.activities.MainActivity.mGoogleApiClient;


/**
 * Created by Danish on 11/06/2018.
 */
public class WhereTo extends android.support.v4.app.Fragment {
    private ViewFlipper where_to_container;
    private View whereToView;
    private View searchLocationView;
    private EditText currentLocation, searchLocation, customLocation;
    private Button backbut;
    private Button backbut2;
    private ImageView closbtnwhereto;
    //    private TextView homeAddress, workAddress;
//    private LinearLayout home, work;
    ListView listView, listView2;
    SharedPreferencesHelper sHelper;
    private PendingResult<AutocompletePredictionBuffer> mResult;
    private PendingResult<AutocompletePredictionBuffer> mResultCustom;
    private String tempString;
    //    private ArrayList<AutocompletePrediction> mSearchList;
    private ArrayList<AddressDao> searchAddressList;
    private ArrayList<AddressDao> searchAddressCustomList;
    private String placeId;
    private String placeId2;
    LocationRequest mLocationRequest;
    private String placeid4;
    public String Bydefaultcurrent;
    GPSTracker gps;
    private double latitude;
    private double longitude;
    private double addressLat;
    private double addressLng;
    private String dropAdress;
    public String fromAdress;
    private double fromLat;
    private double fromLng;
    private double fromLat2;
    private double fromLng2;
    private static double currentLat;
    private static double currentLng;
    private double fromdestinationlat;
    private double fromdestinationlng;
    private double todestinationlat;
    private double todestinationlng;
    private LatLngBounds mBounds;
    private boolean isCurrent, isSearch, isHome, isWork, isSearchSelected = false;
    private String countryCodeValue;
    private boolean resultStop;
    View view;
    AddressDao homeModel;
    AddressDao workModel;
    public static String userWorkAddress = "userWorkAddress";
    public static String userHomeAddress = "userHomeAddress";
    public static String userWorkPlaceid = "userWorkId";
    public static String userHomePlaceid = "userHomeId";
    public static String userFromLat = "userfromLat";
    public static String userFromlng = "userfromLng";
    public static String userToLat = "usertoLat";
    public static String userToLng = "usertoLng";
    public static String userDropAddress = "userDropAddress";
    public static String userFromAddress = "userfromAddress";

    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.whereto_container, container, false);
        return  rootView;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        initializeViewFlipper();
        sHelper = new SharedPreferencesHelper(getActivity());
        searchAddressList = new ArrayList<>();
        searchAddressCustomList = new ArrayList<>();
        homeModel = new AddressDao();
        workModel = new AddressDao();
        homeModel.setPrimaryText(getResources().getString(R.string.title_home));
        homeModel.setSecondaryText(getResources().getString(R.string.title_home));
        workModel.setPrimaryText(getResources().getString(R.string.title_work));
        workModel.setSecondaryText(getResources().getString(R.string.title_work));
        initContent();
        loadAddress();
        countryCodeValue = Helpers.getCountryCodeFromGps(getActivity());
        //currentLocation.setText(Bydefaultcurrent);
        //currentLocation.removeTextChangedListener(generalTextWatcher);
        //getcountrycodefromgps();
        addHomeWorkInListView();
        setWhereToList();
        super.onActivityCreated(savedInstanceState);
    }

    private void addHomeWorkInListView(){
        searchAddressList.clear();
        searchAddressList.add(homeModel);
        searchAddressList.add(workModel);

    }

    private void setWhereToList(){
        listView.setAdapter(new PlaceSearchAdapter(getActivity(), searchAddressList));
    }
    private void initContent() {

        listView = (ListView) rootView.findViewById(R.id.location_listView);
        listView2 = (ListView) rootView.findViewById(R.id.location_listView2);
        backbut2 = (Button) rootView.findViewById(R.id.backbutton2);
        closbtnwhereto = (ImageView) rootView.findViewById(R.id.closebutton2);
        currentLocation = (EditText) rootView.findViewById(R.id.currentLocation_autocomplteTextView);
        backbut = (Button) rootView.findViewById(R.id.backbutton);
        searchLocation = (EditText) rootView.findViewById(R.id.searchLocation_autocomplteTextView);
        customLocation = (EditText) rootView.findViewById(R.id.customLocation_autocomplteTextView);
//        homeAddress = (TextView) contentView.findViewById(R.id.homeAddress_whereTo);
//        workAddress = (TextView) contentView.findViewById(R.id.workAddress_whereTo);
//        home = (LinearLayout) contentView.findViewById(R.id.homeLl_whereTo);
//        work = (LinearLayout) contentView.findViewById(R.id.workLl_whereTo);
        currentLocation.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchLocation.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        customLocation.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        home.setOnClickListener(this);

//        work.setOnClickListener(this);

        currentLocation.addTextChangedListener(generalTextWatcher);
        searchLocation.requestFocus();
        isSearch = true;
        currentLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isCurrent = true;
                isSearch = false;
                //currentLocation.addTextChangedListener(generalTextWatcher);
                return false;
            }
        });

        searchLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isCurrent = false;
                isSearch = true;
                //searchLocation.addTextChangedListener(generalTextWatcher);
                return false;
            }
        });

   /*    customLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (customLocation.getRight() - customLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    customLocation.setText("");
                        listView2.setVisibility(View.GONE);

                        return true;
                    }
                }
                return false;
            }
        });*/
        searchLocation.addTextChangedListener(generalTextWatcher);
        customLocation.addTextChangedListener(generalTextWatcherCustom);
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedTopBar();
            }
        });
        backbut2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                onBackPressedTopBar();
            }
        });
        closbtnwhereto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView2.setVisibility(View.GONE);
                customLocation.setText("");
            }
        });
        // searchLocation.performClick();

//Getting Current location Address Using GeoCoder
        gps = new GPSTracker(getActivity());
        // check if GPS enabled
        if (gps.canGetLocation()) {
//            currentLat = gps.getLatitude();
//            currentLng = gps.getLongitude();
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Home.currentLat, Home.currentLng, 1);
                String street = addresses.get(0).getAddressLine(0);
                Bydefaultcurrent = street;
                //currentLocation.setText(street);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + currentLat + "\nLong: " + currentLng, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        Log.v("current", "" + currentLocation.getText().toString());
        Log.v("search", "" + searchLocation.getText().toString());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                removeFocus();
                resultStop = true;
                if (searchAddressList.get(i).getPrimaryText().equals(getResources().getString(R.string.title_home))){
                    homePressed();
                }else if (searchAddressList.get(i).getPrimaryText().equals(getResources().getString(R.string.work_lbl))){
                    workPressed();
                }else {
                    tempString = searchAddressList.get(i).getFullText();
                    placeId2 = searchAddressList.get(i).getPlaceId();
                    setText(tempString, placeId2);
                }
                addHomeWorkInListView();
                setWhereToList();


                //getPlaceDetail();
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                tempString = searchAddressCustomList.get(i).getFullText();
                placeId = searchAddressCustomList.get(i).getPlaceId().toString();

                view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //setText(tempString);
                if (isHome) {
                    sHelper.setString(userHomeAddress,tempString);
                    sHelper.setString(userHomePlaceid,placeId);


                    //getPlaceDetail();
                    //showBackBtnWithTitle(R.string.where_to);

                    backToSpecificView(whereToView.getId());
                    homeModel.setSecondaryText(sHelper.getString(userHomeAddress));
                    homeModel.setPlaceId(placeId);
//                    homeAddress.setText(sHelper.getHomeAddress());
                    listView2.setVisibility(View.GONE);

                }
                if (isWork) {
                    sHelper.setString(userWorkAddress,tempString);
                    sHelper.setString(userWorkPlaceid,placeId);

                    //getPlaceDetail();
                    //showBackBtnWithTitle(R.string.where_to);
                    backToSpecificView(whereToView.getId());
//                    workAddress.setText(sHelper.getWorkAddress());

                    workModel.setSecondaryText(sHelper.getString(userWorkAddress));
                    workModel.setPlaceId(placeId);

                    listView2.setVisibility(View.GONE);
                }

                addHomeWorkInListView();
                setWhereToList();


            }
        });
    }

    public void checkAdress() {


        if (searchLocation.getText().toString().trim().length() > 0) {
            if (currentLocation.getText().toString().trim().length() > 0) {
                if (fromdestinationlat <= 0) {
                    fromdestinationlat = fromLat2;
                    fromdestinationlng = fromLng2;
                    Log.v("From3", String.valueOf(fromdestinationlat));
                }

            } else {
                fromdestinationlat = currentLat;
                fromdestinationlng = currentLng;
                //fromAdress = Bydefaultcurrent;
                Log.v("From1", String.valueOf(fromdestinationlat));
            }

           /* if (todestinationlat <= 0) {
                todestinationlat = fromLat;
                todestinationlng = fromLng;
            }*/

            if (currentLocation.getText().toString().trim().equals("")) {
                fromAdress = Bydefaultcurrent;
            } else {
                fromAdress = currentLocation.getText().toString();
            }
            dropAdress = searchLocation.getText().toString();
            sHelper.setString(userFromLat,String.valueOf(fromdestinationlat));
            sHelper.setString(userFromlng,String.valueOf(fromdestinationlng));
            sHelper.setString(userToLat,String.valueOf(todestinationlat));
            sHelper.setString(userToLng,String.valueOf(todestinationlng));
            sHelper.setString(userDropAddress,dropAdress);
            sHelper.setString(userFromAddress,fromAdress);
            Log.v("From2", String.valueOf(todestinationlat));

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchLocation.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
//        sHelper.ispopulate(true);
                Constants.populateConfirmation = true;
                onBackPressedTopBar();
            }

       /* if (searchLocation.getText().toString().trim().length() > 0) {
            if (currentLocation.getText().toString().trim().length() > 0) {
                if(fromdestinationlat<=0) {
                    fromdestinationlat = fromLat2;
                    fromdestinationlng = fromLng2;
                    Log.v("From3", String.valueOf(fromdestinationlat));
                }
            }else {
                fromdestinationlat = currentLat;
                fromdestinationlng = currentLng;
                Log.v("From1", String.valueOf(fromdestinationlat));
            }
            if(todestinationlat<=0)
            todestinationlat = fromLat;
            todestinationlng = fromLng;
            dropAdress = searchLocation.getText().toString();
            fromAdress = currentLocation.getText().toString();
            sHelper.setFromLat(String.valueOf(fromdestinationlat));
            sHelper.setFromLng(String.valueOf(fromdestinationlng));
            sHelper.setToLat(String.valueOf(todestinationlat));
            sHelper.setToLng(String.valueOf(todestinationlng));
            sHelper.setDropAdres(dropAdress);
            sHelper.setFromAdres(fromAdress);
            Log.v("From2", String.valueOf(todestinationlat));
            sHelper.ispopulate(true);
           replaceFragment(R.string.m_Home, false);
            //replaceFragment();
        }*/
        }


    private void initializeViewFlipper() {
        where_to_container = (ViewFlipper) rootView.findViewById(R.id.whereto_container);
        where_to_container.removeAllViews();
        whereToView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_where_to, null);
        whereToView.setId(R.id.whereToView);
        searchLocationView = LayoutInflater.from(getActivity()).inflate(R.layout.search_location, null);
        searchLocationView.setId(R.id.searchLocationView);
        where_to_container.addView(whereToView);
        where_to_container.addView(searchLocationView);
    }

    private void setSearchView() {
        customLocation.setText("");
        searchAddressCustomList.clear();
        listView2.setVisibility(View.GONE);
        //showBackBtnWithTitle(R.string.search_location);
        showSpecificView(searchLocation.getId());
    }

    private void showSpecificView(int viewId) {
        nextViewAnim();
        where_to_container.setDisplayedChild(where_to_container.indexOfChild(rootView.findViewById(viewId)));
    }

    private void nextViewAnim() {
       Helpers.hideKeyboard(getActivity());
        where_to_container.setInAnimation(getActivity(), R.anim.in_from_right);
        where_to_container.setOutAnimation(getActivity(), R.anim.out_to_left);
    }


    private void searchPlace(String query) {
        //double radiusDegrees = 1.0;
        //LatLng center = new LatLng(currentLat,currentLng);
        LatLng northEast = new LatLng(Home.currentLat,Home.currentLng);
        LatLng southWest = new LatLng(Home.currentLat, Home.currentLng);
        LatLngBounds bounds = LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build();
        //LatLng currentlocation = new LatLng(currentLat, currentLng);
        //mBounds = new LatLngBounds(new LatLng(currentLat, currentLng),new LatLng(currentLat, currentLng));
        if (!resultStop) {


            AutocompleteFilter filter =
                    new AutocompleteFilter.Builder().setCountry(countryCodeValue).build();

            mResult = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query,
                    bounds, filter);

            mResult.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    if (!resultStop) {
                        addHomeWorkInListView();

                        for (int i = 0; i < autocompletePredictions.getCount(); i++) {
                            AutocompletePrediction place = autocompletePredictions.get(i);

                            AddressDao addressModel = new AddressDao();
                            addressModel.setFullText(place.getFullText(null).toString());
                            addressModel.setPlaceId(place.getPlaceId());
                            addressModel.setPrimaryText(place.getPrimaryText(null).toString());
                            addressModel.setSecondaryText(place.getSecondaryText(null).toString());

                            searchAddressList.add(addressModel);
                        }
                        listView.setAdapter(new PlaceSearchAdapter(getActivity(), searchAddressList));

                    } else {
                        if (where_to_container.getCurrentView().getId() == whereToView.getId()) {
                            addHomeWorkInListView();
                            setWhereToList();
                        }
                    }
                }
            });
        } else {
            if (mResult != null) {
                mResult.cancel();
            }
        }

    }


    private void searchCustomPlace(String query) {

        if (!resultStop) {


            AutocompleteFilter filter =
                    new AutocompleteFilter.Builder().setCountry(countryCodeValue).build();

            mResultCustom = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, query,
                    null, filter);

            mResultCustom.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    if (!resultStop) {
                        if (searchAddressCustomList.size() > 0) {
                            searchAddressCustomList.clear();
                        }

                        for (int i = 0; i < autocompletePredictions.getCount(); i++) {
                            AutocompletePrediction place = autocompletePredictions.get(i);

                            AddressDao addressModel = new AddressDao();
                            addressModel.setFullText(place.getFullText(null).toString());
                            addressModel.setPlaceId(place.getPlaceId());
                            addressModel.setPrimaryText(place.getPrimaryText(null).toString());
                            addressModel.setSecondaryText(place.getSecondaryText(null).toString());
                            searchAddressCustomList.add(addressModel);
                        }

//                         if (where_to_container.getCurrentView().getId() == searchLocationView.getId()) {
                        listView2.setVisibility(View.VISIBLE);
                        listView2.setAdapter(new PlaceSearchAdapter(getActivity(), searchAddressCustomList));
//                        }
                    } else {
                        if (where_to_container.getCurrentView().getId() == searchLocationView.getId()) {
                            listView2.setVisibility(View.GONE);
                        }
                    }

                }
            });
        } else {
            if (mResultCustom != null) {
                mResultCustom.cancel();
            }
        }

    }

    private void getPlaceDetail() {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            LatLng queriedLocation = myPlace.getLatLng();
                            addressLng = queriedLocation.longitude;
                            addressLat = queriedLocation.latitude;
                            //addressLat =
                            if (isCurrent) {
                                fromdestinationlat = addressLat;
                                fromdestinationlng = addressLng;
                                //Log.v("get", String.valueOf(sHelper.getHomeLat()));
                            }
                            if (isSearch) {
                                todestinationlng = addressLng;
                                todestinationlat = addressLat;
                               checkAdress();
                                //Log.v("gethv", String.valueOf(sHelper.getHomeLat()));
                            }
                            Log.v("Latitude is", "" + queriedLocation.latitude);
                            Log.v("Longitude is", "" + queriedLocation.longitude);

                            Log.i("WhereTo", "Place found: " + myPlace.getName());
                        } else {
                            Log.e("WhereTo", "Place not found");
                        }
                        places.release();
                    }
                });


    }


    private void getPlaceDetail2(String placeid2) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeid2)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            LatLng queriedLocation = myPlace.getLatLng();
                            Log.v("Latitude is", "" + queriedLocation.latitude);
                            Log.v("Longitude is", "" + queriedLocation.longitude);
                            latitude = queriedLocation.latitude;
                            longitude = queriedLocation.longitude;
                            if (isCurrent) {

                                fromLat2 = latitude;
                                fromLng2 = longitude;
                            }
                            if (isSearch) {

                                todestinationlat = latitude;
                                todestinationlng = longitude;
                                checkAdress();
                            }


                            Log.i("WhlaereTo", "Place found: " + myPlace.getName());

                        } else {
                            Log.e("WhereTo", "Place not found");
                        }
                        places.release();
                    }
                });


    }





    public void onBackPressedTopBar()
    {
        Home home = new Home();
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Container, home);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void workPressed() {
        isWork = true;
        isHome = false;
        if (sHelper.getString(userWorkAddress)!=null &&sHelper.getString(userWorkAddress).length() > 1) {
                placeId = sHelper.getString(userWorkPlaceid);
                setText2(sHelper.getString(userWorkAddress));

            } else {
                setSearchView();
                isSearchSelected = true;
            }

    }

    private void homePressed() {
        isWork = false;
        isHome = true;

            if (sHelper.getString(userHomeAddress)!=null && sHelper.getString(userHomeAddress).length() > 1) {
                placeId = sHelper.getString(userHomePlaceid);
                setText2(sHelper.getString(userHomeAddress));

            } else {
                setSearchView();
                isSearchSelected = true;
            }

    }

//    @Override
//    public boolean onBackPressed() {
//        if (where_to_container.getCurrentView().getId() == whereToView.getId()) {
//            return super.onBackPressed();
//        } else {
//
//            if (isSearchSelected) {
//                isSearchSelected = false;
////                backToSpecificView(accountsView.getId());
////                showBackBtnWithTitle(R.string.accounts);
////                return true;
//            }
//            //showBackBtnWithTitle(R.string.where_to);
//            backToSpecificView(whereToView.getId());
//
//            return true;
//        }
//    }

    public void setText(String text, String place) {

        if (getActivity().getCurrentFocus().getId() == currentLocation.getId()) {
            currentLocation.removeTextChangedListener(generalTextWatcher);
            currentLocation.setText(text);
            currentLocation.setSelection(currentLocation.length());
            getPlaceDetail2(place);

        } else if (getActivity().getCurrentFocus().getId() == searchLocation.getId()) {
            searchLocation.removeTextChangedListener(generalTextWatcher);
            searchLocation.setText(text);
            searchLocation.setSelection(searchLocation.length());
            getPlaceDetail2(place);


        }
    }

   /* private void removeFocus() {
        if (activity.getCurrentFocus().getId() == currentLocation.getId()) {
            currentLocation.removeTextChangedListener(generalTextWatcher);
        } else if (activity.getCurrentFocus().getId() == searchLocation.getId()) {
            searchLocation.removeTextChangedListener(generalTextWatcher);
        }
    }*/

    public void setText2(String text) {

        if (getActivity().getCurrentFocus().getId() == currentLocation.getId()) {
           // Cache.homeworkcheck = false;
            currentLocation.removeTextChangedListener(generalTextWatcher);
            currentLocation.setText(text);
            addHomeWorkInListView();
            setWhereToList();
            currentLocation.setSelection(currentLocation.length());
            getPlaceDetail();


        } else if (getActivity().getCurrentFocus().getId() == searchLocation.getId()) {
            //Cache.homeworkcheck = false;
            searchLocation.removeTextChangedListener(generalTextWatcher);
            searchLocation.setText(text);
            searchLocation.setSelection(searchLocation.length());
            getPlaceDetail();


        }

    }

//    private void showBackBtnWithTitle(String title) {
//        hideAllViews();
//        showTitle();
//        setcustomTitle(title);
//        showBackBtn();
//    }
//
//
//    private void showBackBtnWithTitle(int title) {
//        hideAllViews();
//        showTitle();
//        setcustomTitle(title);
//        showBackBtn();
//    }
//
//    private void setcustomTitle(String title) {
//        setTitle(title);
//    }
//
//    private void setcustomTitle(int title) {
//        setTitle(title);
//    }
//
//    private void showTitleOnly(int title) {
//        hideAllViews();
//        showTitle();
//        setcustomTitle(title);
//    }

    private void backToSpecificView(int viewId) {
        previousViewAnim();
        where_to_container.setDisplayedChild(where_to_container.indexOfChild(rootView.findViewById(viewId)));
    }

    private void previousViewAnim() {
       Helpers.hideKeyboard(getActivity());
        where_to_container.setInAnimation(getActivity(), R.anim.in_from_left);
        where_to_container.setOutAnimation(getActivity(), R.anim.out_to_right);
    }

    private void loadAddress() {
        if(sHelper.getString(userHomeAddress)!=null) {
            if (sHelper.getString(userHomeAddress).length() > 0) {
                homeModel.setSecondaryText(sHelper.getString(userHomeAddress));
            }
        }
        if(sHelper.getString(userWorkAddress)!=null) {
            if (sHelper.getString(userWorkAddress).length() > 0) {
                workModel.setSecondaryText(sHelper.getString(userWorkAddress));
            }
        }
    }

    private TextWatcher generalTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count2) {
          /* if (activity.getCurrentFocus().getId() == searchLocation.getId()){
               if( searchLocation.getText().toString().trim().equals(sHelper.getWorkAddress()) ||  searchLocation.getText().toString().trim().equals(sHelper.getHomeAddress())){

                    adress();
                }
            }*/
        }


        @Override
        public void afterTextChanged(final Editable editable) {

            if (editable.length() > 1 && !editable.toString().equalsIgnoreCase(tempString)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resultStop = false;
                        searchPlace(editable.toString());
                    }
                }, 1000);
            }
        }
    };


    private TextWatcher generalTextWatcherCustom = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count2) {
          /* if (activity.getCurrentFocus().getId() == searchLocation.getId()){
               if( searchLocation.getText().toString().trim().equals(sHelper.getWorkAddress()) ||  searchLocation.getText().toString().trim().equals(sHelper.getHomeAddress())){

                    adress();
                }
            }*/
        }


        @Override
        public void afterTextChanged(final Editable editable) {

            if (editable.length() > 1 && !editable.toString().equalsIgnoreCase(tempString)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resultStop = false;
                        searchCustomPlace(editable.toString());
                    }
                }, 1000);
            }
        }
    };


}
