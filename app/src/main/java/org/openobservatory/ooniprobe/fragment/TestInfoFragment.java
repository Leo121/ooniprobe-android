package org.openobservatory.ooniprobe.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.data.TestData;
import org.openobservatory.ooniprobe.model.NetworkMeasurement;
import org.openobservatory.ooniprobe.model.OONITests;

public class TestInfoFragment extends Fragment {
    private MainActivity mActivity;
    private AppCompatButton runButton;
    private AppCompatButton learn_moreButton;
    private ImageView testImage;
    private TextView testDesc;
    private ProgressBar test_progress;
    private static String test_name;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onViewSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_info, container, false);
        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getArguments();
        test_name = extras.getString("test_name");
        mActivity.setTitle(NetworkMeasurement.getTestName(mActivity, test_name));

        learn_moreButton = (AppCompatButton) v.findViewById(R.id.learn_more_button);
        learn_moreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getTestUrl(test_name)));
                startActivity(browserIntent);
            }
        });
        runButton = (AppCompatButton) v.findViewById(R.id.run_test_button);
        runButton.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        TestData.doNetworkMeasurements(mActivity, test_name, null);
                    }
                }
        );
        testImage = (ImageView) v.findViewById(R.id.test_logo);
        testImage.setImageResource(NetworkMeasurement.getTestImageBig(test_name));

        testDesc = (TextView) v.findViewById(R.id.testDesc);
        testDesc.setText(getTestDesc(test_name));

        test_progress = (ProgressBar) v.findViewById(R.id.progressIndicator);
        updateButtons();
        return v;
    }

    public void updateButtons(){
        NetworkMeasurement current = TestData.getInstance(mActivity, mActivity).getTestWithName(test_name);
        if (current != null) {
            if (!current.running) {
                test_progress.setVisibility(View.GONE);
                runButton.setVisibility(View.VISIBLE);
            }
            else {
                test_progress.setVisibility(View.VISIBLE);
                runButton.setVisibility(View.GONE);
            }
        }
        else {
            test_progress.setVisibility(View.GONE);
            runButton.setVisibility(View.VISIBLE);
        }
    }

    private String getTestUrl(String name){
        switch (name) {
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return "https://ooni.torproject.org/nettest/http-invalid-request-line/";
            case OONITests.NDT:
                return "https://ooni.torproject.org/nettest/ndt/";
            case OONITests.WEB_CONNECTIVITY:
                return "https://ooni.torproject.org/nettest/web-connectivity/";
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return "https://ooni.torproject.org/nettest/http-header-field-manipulation/";
            case OONITests.DASH:
                return "https://ooni.torproject.org/nettest/dash/";
            default:
                return "";
        }
    }

    public String getTestDesc(String name) {
        switch (name) {
            case OONITests.WEB_CONNECTIVITY:
                return mActivity.getString(R.string.web_connectivity_longdesc);
            case OONITests.HTTP_INVALID_REQUEST_LINE:
                return mActivity.getString(R.string.http_invalid_request_line_longdesc);
            case OONITests.HTTP_HEADER_FIELD_MANIPULATION:
                return mActivity.getString(R.string.http_header_field_manipulation_longdesc);
            case OONITests.NDT:
                return mActivity.getString(R.string.ndt_longdesc);
            case OONITests.DASH:
                return mActivity.getString(R.string.dash_longdesc);
            default:
                return "";
        }
    }
}
