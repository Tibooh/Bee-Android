package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.ui.adapter.IconAdapter;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.preferences.Sensor;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;

/**
 * Common class for Activities showing details of an experiment
 * <p/>
 * Warning: The layout of the Activity extending this class must
 * include the layout "common_experiment_details", i.e. contain the line:
 * <include layout="@layout/common_experiment_details"/>
 */
public abstract class ExperimentDetailsActivity extends BeeGameActivity {
    protected TextView nameView;
    protected TextView organizationView;
    protected TextView versionView;
    protected TextView descriptionView;
    protected GridView stingGridView;
    protected CropPermissionHandler cropPermissionHandler;

    protected Crop crop;
    protected APISENSE.Sdk apisenseSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();
    }

    protected void initExperimentDetailsActivity() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        crop = b.getParcelable("crop");
        cropPermissionHandler = prepareCropPermissionHandler();

        initializeViews();
        displayExperimentInformation();
    }

    protected CropPermissionHandler prepareCropPermissionHandler() {
        return new CropPermissionHandler(this, crop, new OnCropStarted(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    protected void initializeViews() {
        nameView = (TextView) findViewById(R.id.detail_exp_name);
        versionView = (TextView) findViewById(R.id.detail_exp_version);
        organizationView = (TextView) findViewById(R.id.detail_exp_organization);
        stingGridView = (GridView) findViewById(R.id.detail_exp_used_stings);
        descriptionView = (TextView) findViewById(R.id.detail_exp_description);

    }

    public void displayExperimentInformation() {
        getSupportActionBar().setTitle(crop.getName());
        nameView.setText(getString(R.string.exp_details_name, crop.getName()));
        organizationView.setText(getString(R.string.exp_details_organization, crop.getOwner()));
        versionView.setText(getString(R.string.exp_details_version, crop.getVersion()));
        descriptionView.setText(getString(R.string.exp_details_description, crop.getShortDescription()));
        List<Integer> sensorIcons = getIconsForStings(crop.getUsedStings());
        IconAdapter sensorIconsAdapter = new IconAdapter(getBaseContext(), R.layout.grid_item_icon, sensorIcons);
        stingGridView.setAdapter(sensorIconsAdapter);
    }

    protected List<Integer> getIconsForStings(List<String> usedStings) {
        List<Integer> result = new ArrayList<>();
        Sensor sensor;
        for (String sting : usedStings) {
            sensor = apisenseSdk.getPreferencesManager().retrieveSensorForSting(sting);
            if (sensor != null) {
                result.add(sensor.iconID);
            }
        }
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
