package com.bidridego.user;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bidridego.R;
import com.bidridego.RegisterActivity;
import com.bidridego.UserMainActivity;
import com.bidridego.models.BidRideLocation;
import com.bidridego.models.Trip;
import com.bidridego.services.SaveOrUpdateCallback;
import com.bidridego.services.TripService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    Context mThis;
    Activity mActivity;
    private GoogleMap googleMap;
    private MapView mapView;
    private Marker currentLocationMarker = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private AlertDialog locationSettingsDialog;
    private Geocoder geocoder;
    private static boolean isFirstLocationUpdate = true;
    private AutoCompleteTextView sourceEditText, destinationEditText;
    private static final String  OPENCAGE_API_KEY  = "ce0b8aa59a7d44ebb30298a06a04cbdc";
    static ArrayAdapter<String> sourceAdapter;
    static ArrayAdapter<String> destinationAdapter;

    private Marker destinationMarker;
    private Polyline routePolyline;
    private EditText date,time, cost, passengers;
    private Switch isCarPool;
    private Button rideNow;
    private RadioGroup rideTypeRadioGroup;
    Trip trip;
    AtomicBoolean isToSet;
    AtomicBoolean isFromSet;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mThis = getContext();
        mActivity = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        sourceEditText = rootView.findViewById(R.id.sourceEditText);
        destinationEditText = rootView.findViewById(R.id.destinationEditText);

        // Set up the adapter for autocomplete suggestions
        sourceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        destinationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);

        sourceEditText.setAdapter(sourceAdapter);
        destinationEditText.setAdapter(destinationAdapter);
        date = rootView.findViewById(R.id.date);
        time = rootView.findViewById(R.id.time);
        cost = rootView.findViewById(R.id.cost);
        passengers = rootView.findViewById(R.id.seats);
        passengers.setFilters(new InputFilter[]{new RangeInputFilter(1, 4)});
        isCarPool = rootView.findViewById(R.id.is_car_pool);
        rideTypeRadioGroup = rootView.findViewById(R.id.ride_type_radio_group);
        rideNow = rootView.findViewById(R.id.ride_now);
        rideNow.setEnabled(false);
        trip = new Trip();

        trip.setPostedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
        trip.setRideType("SUV");
        isToSet = new AtomicBoolean(false);
        isFromSet = new AtomicBoolean(false);

        rideNow.setOnClickListener(v -> {
            String dateData = date.getText().toString();
            String timeData = time.getText().toString();
            trip.setDateAndTime(dateData + " " + timeData);
            TripService.getInstance().saveOrUpdate(trip, new SaveOrUpdateCallback() {
                @Override
                public void onSuccess() {
                    // The save or update operation was successful
                    showSnackbar("Your Trip scheduled!");
                    UserMainActivity activity = (UserMainActivity) getActivity();

                    // Check if the activity is not null and an instance of UserMainActivity
                    if (activity != null && activity instanceof UserMainActivity) {
                        activity.navigateToMyTrips();
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle the case where the save or update operation failed
                    showSnackbar("Failed to save or update trip: " + errorMessage);
                }
            });
        });

        rideTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = rootView.findViewById(checkedId);
            trip.setRideType(String.valueOf(radioButton.getText()));
            isValidTrip(trip);
        });

        date.setOnClickListener(v -> {

                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        mThis,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                if (selectedYear > currentYear ||
                                        (selectedYear == currentYear && selectedMonth > currentMonth) ||
                                        (selectedYear == currentYear && selectedMonth == currentMonth && selectedDayOfMonth >= currentDayOfMonth)) {
                                    date.setText(selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear);
                                } else {
                                    Toast.makeText(mThis, "Please select a future date", Toast.LENGTH_SHORT).show();
                                    date.setText(currentDayOfMonth + "/" + (currentMonth + 1) + "/" + currentYear);
                                }
                            }
                        },
                        currentYear, currentMonth, currentDayOfMonth
                );

                // Show the dialog
                datePickerDialog.show();
        });

        time.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(mThis, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // Get the current time
                    Calendar currentTime = Calendar.getInstance();
                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = currentTime.get(Calendar.MINUTE);

                    // Check if the selected time is in the future
                    if (hourOfDay > currentHour || (hourOfDay == currentHour && minute >= currentMinute)) {
                        // Format the time
                        String hr = (hourOfDay < 10) ? "0" + hourOfDay : String.valueOf(hourOfDay);
                        String min = (minute < 10) ? "0" + minute : String.valueOf(minute);

                        // Update the TextView with the selected time
                        time.setText(hr + ":" + min);
                        isValidTrip(trip);
                    } else {
                        // Show a message or take appropriate action
                        Toast.makeText(mThis, "Please select a future time", Toast.LENGTH_SHORT).show();
                        // Optionally, you can reset the time to the current time
                        String currentHr = (currentHour < 10) ? "0" + currentHour : String.valueOf(currentHour);
                        String currentMin = (currentMinute < 10) ? "0" + currentMinute : String.valueOf(currentMinute);
                        time.setText(currentHr + ":" + currentMin);
                    }
                }
            }, 0, 0, true);
            dialog.show();
        });

        isCarPool.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                trip.setCarPool(true);
            } else {
                trip.setCarPool(false);
            }
        });

        // Add a TextChangedListener to fetch suggestions as the user types
        sourceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fetch suggestions as the user types
                if(charSequence.toString().length() >= 3) {
                    fetchLocationSuggestions(charSequence.toString(), true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // Add a TextChangedListener to fetch suggestions as the user types
        destinationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fetch suggestions as the user types
                if(charSequence.toString().length() >= 3) {
                    fetchLocationSuggestions(charSequence.toString(), false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        sourceEditText.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item from the adapter
            String selectedItem = (String) parent.getItemAtPosition(position);

            // Use the geocoder to get the details (latitude, longitude) for the selected item
            try {
                List<Address> addresses = geocoder.getFromLocationName(selectedItem, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address selectedAddress = addresses.get(0);
                    double selectedLatitude = selectedAddress.getLatitude();
                    double selectedLongitude = selectedAddress.getLongitude();

                    BidRideLocation from = new BidRideLocation(selectedLatitude, selectedLongitude, selectedItem);
                    trip.setFrom(from);
                    isFromSet.set(true);
                    if(isToSet.get()) trip.setDistance(distance(trip.getTo(), from));
                    isValidTrip(trip);
                    // Update the marker on the map with the selected location
                    updateMarker(new LatLng(selectedLatitude, selectedLongitude), selectedItem, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        destinationEditText.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected item from the adapter
            String selectedItem = (String) parent.getItemAtPosition(position);

            // Use the geocoder to get the details (latitude, longitude) for the selected item
            try {
                List<Address> addresses = geocoder.getFromLocationName(selectedItem, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address selectedAddress = addresses.get(0);
                    double selectedLatitude = selectedAddress.getLatitude();
                    double selectedLongitude = selectedAddress.getLongitude();

                    // Now you have the name (selectedItem), latitude, and longitude
                    BidRideLocation to = new BidRideLocation(selectedLatitude, selectedLongitude, selectedItem);
                    trip.setTo(to);
                    isToSet.set(true);
                    if(isFromSet.get()) trip.setDistance(distance(trip.getFrom(), to));
                    isValidTrip(trip);

                    // Update the marker on the map with the selected location
                    updateMarker(new LatLng(selectedLatitude, selectedLongitude), selectedItem, false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

//        cost
        cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) trip.setCost(Double.parseDouble(s.toString()));
                isValidTrip(trip);
            }
        });

//        passengers.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                try {
//                    trip.setPassengers(Integer.parseInt(s.toString()));
//                    isValidTrip(trip);
//                } catch (NumberFormatException e) {
//                    Toast.makeText(getActivity(), "Error: Invalid number of passengers", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(!s.toString().isEmpty()) trip.setPassengers(Integer.parseInt(s.toString()));
//                isValidTrip(trip);
//            }
//        });

        // Add a TextChangedListener to fetch suggestions as the user types
        sourceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fetch suggestions as the user types
                if(charSequence.toString().length() >= 3) {
                    fetchLocationSuggestions(charSequence.toString(), true);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // Add a TextChangedListener to fetch suggestions as the user types
        destinationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fetch suggestions as the user types
                if(charSequence.toString().length() >= 3) {
                    fetchLocationSuggestions(charSequence.toString(), false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Check and request location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestLocationPermissions();
        } else {
            // Permissions are already granted for devices below Android 6.0
            initMap();
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }
    private double distance(BidRideLocation to, BidRideLocation from) {
        double lon1 = Math.toRadians(to.getLng());
        double lon2 = Math.toRadians(from.getLng());

        double lat1 = Math.toRadians(to.getLat());
        double lat2 = Math.toRadians(from.getLng());

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles use 6371
        double r = 3956;

        // calculate the result
        return(c * r);
    }

    private boolean isValidTrip(Trip trip){
        boolean result = false;
        String dateString = date.getText().toString();
        String timeString = time.getText().toString();
        if(
                (trip.getCost() > 0) &&
                        (trip.getPassengers() > 0)&&
                        ( dateString != "")&&
                        ( timeString != "")&&
                        (trip.getFrom() != null)&&
                        (trip.getTo() != null)&&
                        (trip.getPostedBy() != null && !trip.getPostedBy().trim().isEmpty())&&
                        (trip.getRideType() != null && !trip.getRideType().trim().isEmpty())

        ) {
            result = true;
            rideNow.setEnabled(true);
        }

        return result;
    }

    private void updateMarker(LatLng latLng, String locationName, boolean isSource) {
        // Remove the previous marker if exists
        if (isSource) {
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }
            // Add a marker for the source (current location)
            currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(locationName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        } else {
            if (destinationMarker != null) {
                destinationMarker.remove();
            }
            // Add a marker for the destination
            destinationMarker = googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(locationName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        // Move the camera to the marker's position
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // Update source marker
        if (currentLocationMarker != null && destinationMarker != null) {
            drawPolyline();
        }
    }
    private void drawPolyline() {
        if (routePolyline != null) {
            routePolyline.remove(); // Remove previous polyline
        }

        // Draw a polyline between source and destination
        LatLng sourceLatLng = currentLocationMarker.getPosition();
        LatLng destinationLatLng = destinationMarker.getPosition();

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(sourceLatLng, destinationLatLng)
                .width(5) // Set the width of the polyline
                .color(ContextCompat.getColor(requireContext(), R.color.teal_200)); // Set the color

        routePolyline = googleMap.addPolyline(polylineOptions);
    }
    private void fetchLocationSuggestions(String input, boolean isSource) {
        // Perform the Geocoding API request in a background thread
        new FetchLocationSuggestionsTask(isSource).execute(input);
    }

    private static class FetchLocationSuggestionsTask extends AsyncTask<String, Void, ArrayList<String>> {
        boolean isSource;
        FetchLocationSuggestionsTask(boolean isSource) {
            this.isSource = isSource;
        }
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> suggestions = new ArrayList<>();

            try {
                // Construct the URL for the Geocoding API request
                String apiUrl = "https://api.opencagedata.com/geocode/v1/json" +
                        "?q=" + URLEncoder.encode(params[0], "UTF-8") +
                        "&key=" + OPENCAGE_API_KEY + "&countrycode=ca";

                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String response = readStream(in);

                    // Parse the response and extract suggestions
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray results = jsonResponse.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String address = result.getString("formatted");
                        Log.i("Mapdata ==>", address);
                        suggestions.add(address);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return suggestions;
        }

        @Override
        protected void onPostExecute(ArrayList<String> suggestions) {
            // Update the adapter with the fetched suggestions
            if(isSource) {
                sourceAdapter.clear();
                sourceAdapter.addAll(suggestions);
                sourceAdapter.notifyDataSetChanged();
            } else {
                destinationAdapter.clear();
                destinationAdapter.addAll(suggestions);
                destinationAdapter.notifyDataSetChanged();
            }
        }
    }

    private static String readStream(InputStream is) {
        try (Scanner s = new Scanner(is).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(mThis, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, get the location
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location
                initMap();
            } else {
                // Permission denied, handle accordingly
            }
        }
    }
    private void initMap() {
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }
    private void showLocationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Location Services Disabled")
                .setMessage("Please enable location services to use this feature.")
                .setPositiveButton("Settings", (dialog, which) -> openLocationSettings())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        locationSettingsDialog = builder.create();

        // Show the dialog
        locationSettingsDialog.show();
    }

    private void openLocationSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
    private void dismissLocationSettingsDialog() {
        // Dismiss the location settings dialog if it is currently showing
        if (locationSettingsDialog != null && locationSettingsDialog.isShowing()) {
            locationSettingsDialog.dismiss();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Enable location layer (blue dot)
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        // Initialize the geocoder
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        // Set up location updates
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (isFirstLocationUpdate && currentLocationMarker == null) {
                        // Handle the location change
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);

                        // Remove the previous marker if exists
                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove();
                        }

                        // Add a marker at the current location
                        currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Current Location"));

                        // Move the camera to the current location
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        // Display the name of the current location in a toast
                        String locationName = showLocationName(latitude, longitude);
                        if(locationName != null) {
                            BidRideLocation from = new BidRideLocation(latitude, longitude, locationName);
                            trip.setFrom(from);
                            isFromSet.set(true);
                            if(isToSet.get()) trip.setDistance(distance(trip.getTo(), from));
                            isValidTrip(trip);
                        }
                        // Remove location updates after the first successful update
                        locationManager.removeUpdates(this);
                        isFirstLocationUpdate = false;
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                    dismissLocationSettingsDialog();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // Location services are disabled, prompt the user to enable them
                    showLocationSettingsDialog();
                }
            });
        }
    }
    private String showLocationName(double latitude, double longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String locationName = address.getAddressLine(0); // You can customize this based on your needs
                sourceEditText.setText(locationName);
                return locationName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void showSnackbar(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    public class RangeInputFilter implements InputFilter {

        private int minValue;
        private int maxValue;

        public RangeInputFilter(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Combine the existing text with the new input
                String newValue = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend, dest.length()).toString();

                // Parse the new value
                int input = Integer.parseInt(newValue);

                // Check if the value is within the specified range
                if (isInRange(minValue, maxValue, input)) {
                    return null; // Accept the new input
                } else {
                    // Reject the new input
                    return "";
                }
            } catch (NumberFormatException e) {
                // If the input cannot be parsed as an integer, reject it
                return "";
            }
        }

        private boolean isInRange(int minValue, int maxValue, int value) {
            return value >= minValue && value <= maxValue;
        }
    }
}