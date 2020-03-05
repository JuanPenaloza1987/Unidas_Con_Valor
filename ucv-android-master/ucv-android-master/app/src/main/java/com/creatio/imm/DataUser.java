package com.creatio.imm;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADCitysAU;
import com.creatio.imm.Objects.OCitys;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.PlaceDetails;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class DataUser extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private com.seatgeek.placesautocomplete.model.Place placeSel;
    private double lat, lng;
    private String direction = "";
    private String genre = "";
    private String id_city = "";
    private EditText edtDate, edtEmail, edtName;
    private PlacesAutocompleteTextView placesAutocomplete;
    private RadioGroup rdGroup;
    private ArrayList<OCitys> data_citys = new ArrayList<>();
    private AutoCompleteTextView edtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_user);
        context = DataUser.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Datos de usuario");
        placesAutocomplete = findViewById(R.id.places_autocomplete);
        placesAutocomplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull com.seatgeek.placesautocomplete.model.Place place) {
                Log.e("place", "Place: " + place.description);
                direction = place.description;
                placeSel = place;
                placesAutocomplete.getDetailsFor(placeSel, new DetailsCallback() {
                    @Override
                    public void onSuccess(PlaceDetails details) {
                        lat = details.geometry.location.lat;
                        lng = details.geometry.location.lng;
                    }

                    @Override
                    public void onFailure(Throwable failure) {
                    }

                    public void onComplete() {
                    }
                });
            }
        });

        edtCity = (AutoCompleteTextView) findViewById(R.id.edtCity);
        edtCity.setThreshold(2);
        edtCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OCitys d : data_citys){
                    String name = d.getCity() + ", " + d.getState();
                    if(d.getCity() != null && name.contains(item.toString())){
                        id_city = d.getID();
                    }
                    //something here
                }
            }
        });
        edtCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OCitys d : data_citys){
                    String name = d.getCity() + ", " + d.getState();
                    if(d.getCity() != null && name.contains(item.toString())){
                        id_city = d.getID();
                    }
                    //something here
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtDate = findViewById(R.id.edtDate);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new
                DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        edtDate.setText(sdf.format(myCalendar.getTime()));
                    }

                };
        edtDate.setFocusable(false);
        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(DataUser.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String hpb, latlng, email, namec;
                hpb = edtDate.getText().toString();
                email = edtEmail.getText().toString();
                namec = edtName.getText().toString();
                edtDate.setError(null);
                edtEmail.setError(null);
                if (hpb.trim().equalsIgnoreCase("")) {
                    edtDate.setError("La fecha de tu cumpleaños es necesaria.");
                    return;
                }
                if (namec.trim().equalsIgnoreCase("")) {
                    edtName.setError("Tu nombre es necesario.");
                    return;
                }
                if (email.trim().equalsIgnoreCase("")) {
                    edtEmail.setError("Correo no válido");
                    return;
                }
                if (!email.contains("@")) {
                    edtEmail.setError("Correo no válido");
                    return;
                }
                if (genre.equalsIgnoreCase("")) {
                    Alerter.create(DataUser.this)
                            .setTitle("Campos obligatorios")
                            .setText("El genero es necesario para continuar.")
                            .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                            .show();
                    return;
                }

                latlng = String.valueOf(lat) + "," + String.valueOf(lng);
                AndroidNetworking.post(getResources().getString(R.string.apiurl) + "UpdateDataUser")
                        .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                        .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                        .addBodyParameter("latlng", latlng)
                        .addBodyParameter("id_city", id_city)
                        .addBodyParameter("email", email)
                        .addBodyParameter("name", namec)
                        .addBodyParameter("direction", direction)
                        .addBodyParameter("hpb", hpb)
                        .addBodyParameter("genre", genre)
                        .setPriority(Priority.IMMEDIATE)
                        .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("res", response.toString());
                            String success = response.getString("success");
                            if (success.equalsIgnoreCase("true")) {

                                Alerter.create(DataUser.this)
                                        .setTitle("Hecho ")
                                        .setText("Los datos enviados se han actualizado correctamente.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.green))
                                        .setOnHideListener(new OnHideAlertListener() {
                                            @Override
                                            public void onHide() {
                                                finish();
                                            }
                                        })
                                        .show();
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putBoolean("datauser", true);
                                edit.putString("email", email);
                                edit.putString("name", namec);
                                edit.putString("last_name", "");
                                edit.apply();
                            } else {
                                Alerter.create(DataUser.this)
                                        .setTitle("Error")
                                        .setText("Los datos enviados no son los correctos.")
                                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Alerter.create(DataUser.this)
                                    .setTitle("Error")
                                    .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Alerter.create(DataUser.this)
                                .setTitle("Error")
                                .setText("Ya estamos trabajando en ello. Disculpa las molestias")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                });
            }
        });
        rdGroup = findViewById(R.id.rdGroup);
        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radioButton) {
                    genre = "M";
                } else {
                    genre = "F";
                }
            }
        });
        GetInformationUser();
        GetCitys();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!prefs.getBoolean("datauser", false)) {
            Alerter.create(DataUser.this)
                    .setTitle("Oh oh.")
                    .setText("Esta información es necesaria para continuar.")
                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!prefs.getBoolean("datauser", false)) {
            Alerter.create(DataUser.this)
                    .setTitle("Oh oh.")
                    .setText("Esta información es necesaria para continuar.")
                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                    .show();
            return false;
        } else {
            finish();
            return super.onSupportNavigateUp();
        }

    }

    public void GetInformationUser() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetInformationUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .addBodyParameter("type", prefs.getString("type", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("email");
                            String code = obj.optString("create_date");
                            String status = obj.optString("status");
                            String date_birthday = obj.optString("date_birthday");
                            String image = obj.optString("profile_img");
                            direction = obj.optString("direction");
                            String nivel = obj.getString("nivel");
                            String city = obj.getString("city");
                            String genre = obj.getString("genre");
                            String id_citys = obj.getString("id_city");
                            String latlng = obj.getString("latlng");
                            if (genre.equalsIgnoreCase("M")) {
                                rdGroup.check(R.id.radioButton);
                            } else {
                                rdGroup.check(R.id.radioButton2);
                            }
                            if (date_birthday.contains("null")) {
                                date_birthday = "";
                            }
                            if (direction.contains("null")) {
                                direction = "";
                            }
                            if (city.contains("null")) {
                                city = "";
                            }
                            edtCity.setText(city);
                            id_city = id_citys;
                            edtName.setText(name + " " + last_name);
                            edtEmail.setText(email);
                            edtDate.setText(date_birthday);
                            placesAutocomplete.setText(direction);
                            if (latlng.contains(",")) {
                                String[] alatlng = latlng.split(",");
                                lat = Double.parseDouble(alatlng[0]);
                                lng = Double.parseDouble(alatlng[1]);
                            }

                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void GetCitys() {

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadCitysUser")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("info usetr", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("id");
                            String city = obj.optString("municipio");
                            String state = obj.optString("estado");

                            data_citys.add(new OCitys(ID, city, state));

                        }


                        final ADCitysAU adapter = new ADCitysAU(context, R.layout.item_au, data_citys);
                        edtCity.setAdapter(adapter);
                        edtCity.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                System.out.println("Text [" + s + "]");

                                adapter.getFilter().filter(s.toString());

                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count,
                                                          int after) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}
