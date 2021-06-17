package com.rsin.dotfood.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rsin.dotfood.DataModel;
import com.rsin.dotfood.R;
import com.rsin.dotfood.adapters.HelpersAdapter;
import com.rsin.dotfood.adapters.NeedyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class NeedyFragment extends Fragment {
    public FusedLocationProviderClient fusedLocationProviderClient;
    public TextView location_view;
    public List<Address> addresses;
    RecyclerView recyclerView;
    NeedyAdapter needyAdapter;
    FirebaseFirestore db;
    List<DataModel> formBeens;
    ArrayList<DataModel> mArrayList;
    ImageView filter_btn;
    String state_value,city_value, category_value;
    RadioGroup radioGroup;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_needy, container, false);
        recyclerView = root.findViewById(R.id.needy_recycleview);
        location_view = root.findViewById(R.id.location_edittext);
        filter_btn = root.findViewById(R.id.filter_btn_needy);
        radioGroup = root.findViewById(R.id.radio_group_needy);
        db = FirebaseFirestore.getInstance();



        //code here
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // when permission granted
            get_location();

        } else {
            //when permission denied
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }
        getdata();

        filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.filter_dialog_layout,null);
                mBuilder.setTitle("filter");
                Spinner state = mView.findViewById(R.id.choose_sate_spinner);
                Spinner city = mView.findViewById(R.id.choose_city_spinner);
                ArrayList<String> state_list = new ArrayList<>();
                ArrayList<String> city_list = new ArrayList<>();
                try {
                    JSONObject obj = new JSONObject(loadJSONFromAsset());
                    Iterator<String> iter = obj.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            Object value = obj.get(key);

                            state_list.add(key);

                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, state_list);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(arrayAdapter);

                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String selected = parent.getSelectedItem().toString();
                        try {
                            JSONObject obj = new JSONObject(loadJSONFromAsset());
                            JSONArray jsonArray = obj.getJSONArray(selected);
                            ArrayList<String> citylist  = new ArrayList<String>();

                            for(int i=0; i<jsonArray.length(); i++) {
                                citylist.add(jsonArray.getString(i));
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, citylist);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            city.setAdapter(arrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mBuilder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state_value = state.getSelectedItem().toString();
                        city_value = city.getSelectedItem().toString();

                        Log.e("values",state_value+" "+city_value+" "+category_value);

                        db.collection("taker")
                                .whereEqualTo("state", state_value)
                                .whereEqualTo("city",city_value)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().isEmpty())
                                {
                                    Toast.makeText(getContext(), "no data founded", Toast.LENGTH_SHORT).show();
                                }


                                if (task.isSuccessful())
                                {
                                    ArrayList<DataModel>formBeens= new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.e("TAG 1", document.getId() + " => " + document.getData());
                                        DataModel formBeen = document.toObject(DataModel.class);
                                        formBeens.add(formBeen);
                                    }
                                    needyAdapter = new NeedyAdapter(getContext(),formBeens);
                                    recyclerView.setAdapter(needyAdapter);
                                }
                                else {
                                    Log.e("TAG 2", "Error getting documents: ", task.getException());
                                }

                            }
                        });

//
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radio_5km:
                        Toast.makeText(getContext(), "5km", Toast.LENGTH_SHORT).show();
                        inrange(5);

                        break;

                    case R.id.radio_10km:
                        Toast.makeText(getContext(), "10km", Toast.LENGTH_SHORT).show();
                        inrange(10);
                        break;

                    case R.id.radio_20km:
                        Toast.makeText(getContext(), "20km", Toast.LENGTH_SHORT).show();
                        inrange(20);
                        break;

                    case R.id.radio_50km:
                        Toast.makeText(getContext(), "50km", Toast.LENGTH_SHORT).show();
                        inrange(50);
                        break;
                }

            }
        });

        return root;
    }

    private void get_location() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // initialized location
                Location location = task.getResult();

                if (location != null) {

                    try {
                        //initialized  geocoder
                        Geocoder geocoder = new Geocoder(getContext(),
                                Locale.getDefault());
                        //initialized address list;
                        addresses = geocoder.
                                getFromLocation(location.getLatitude(), location.getLongitude(), 1);


                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    //set location on view
                    if (addresses!=null)
                    {

                        location_view.setText(addresses.get(0).getLocality());

                    }
                    else
                    {
                        Toast.makeText(getContext(), "No Location found", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    void getdata() {
        mArrayList= new ArrayList<>();

        db.collection("taker")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d("TAG", "onSuccess: LIST EMPTY");
                    return;
                } else {
                    // Convert the whole Query Snapshot to a list
                    // of objects directly! No need to fetch each
                    // document.
                    List<DataModel> types = queryDocumentSnapshots.toObjects(DataModel.class);

                    // Add all to your list
                    mArrayList.addAll(types);
                    needyAdapter = new NeedyAdapter(getContext(),mArrayList);
                    recyclerView.setAdapter(needyAdapter);
                    Log.e("TAG rsin", "onSuccess: " + mArrayList);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    private static boolean distance(double lat1, double lon1, double lat2, double lon2, String unit, int range) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return true;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            //convert distance in km
            dist = dist * 1.609344;

            if (dist>range)
            {
                return false;
            }
            else {
                return true;
            }

//            if (unit.equals("K")) {
//                dist = dist * 1.609344;
//            } else if (unit.equals("N")) {
//                dist = dist * 0.8684;
//            }
//            return (dist);
        }
    }

    void inrange(int range)
    {
        ArrayList<DataModel> by_distace_list = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // when permission granted
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    db.collection("taker").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    DataModel data = document.toObject(DataModel.class);
                                    boolean check =  distance(location.getLatitude(),location.getLongitude(),Double.parseDouble(data.getLatitude()),Double.parseDouble(data.getLongitude()),"K",range);
                                    if (check)
                                    {
                                        by_distace_list.add(data);
                                    }
                                }

                                needyAdapter = new NeedyAdapter(getContext(),by_distace_list);
                                if (by_distace_list.isEmpty())
                                {
                                    Toast.makeText(getContext(), "no near result", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    recyclerView.setAdapter(needyAdapter);
                                }


                            } else {
                                Log.d("dd", "Error getting documents: ", task.getException());
                            }
                        }
                    });

                }
            });

        } else {
            //when permission denied
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }




    }
}