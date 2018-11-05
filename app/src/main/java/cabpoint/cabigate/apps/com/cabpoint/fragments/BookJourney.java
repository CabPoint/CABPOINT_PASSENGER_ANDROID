package cabpoint.cabigate.apps.com.cabpoint.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cabpoint.cabigate.apps.com.cabpoint.R;
import cabpoint.cabigate.apps.com.cabpoint.activities.LoginActivity;
import cabpoint.cabigate.apps.com.cabpoint.activities.SignupActivity;

public class BookJourney extends android.support.v4.app.Fragment {
    ImageView ivBack;
    TextView txtdashboard;
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.book_journey, container, false);
        initializeViews();

        // Helpers.customTextView(txtterms);


        return rootView;
    }
    private void  initializeViews()
    {
        ivBack = rootView.findViewById(R.id.toolbar_iv_back);
        txtdashboard = rootView.findViewById(R.id.tv_dashboard);
        txtdashboard.setVisibility(View.VISIBLE);
        txtdashboard.setText("Book your journey");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Home home = new Home();
              android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
              android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.Container, home);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
