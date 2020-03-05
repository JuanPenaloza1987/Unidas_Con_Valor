package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADSchoolsAU;
import com.creatio.imm.Objects.OSchools;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Login extends AppCompatActivity {
    private CircularProgressButton btnLogin;
    private Button btnLoginYaTengo,btnCancel;
    private Switch swType;
    private EditText edtCode;
    private Spinner sp;
    private Context context;
    private ArrayList<OSchools> data = new ArrayList<>();
    List<String> spinnerArray = new ArrayList<String>();
    private String ID_rel;
    private SharedPreferences prefs;
    private String token_fb = "Sin registro";
    private String type = "1";
    // private TextView txtPassLost;
    private TextView txtHelp;
    private ImageView imgFondo;
    private AutoCompleteTextView edtAU;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = Login.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        FirebaseApp.initializeApp(this);
        token_fb = FirebaseInstanceId.getInstance().getToken();
        swType = findViewById(R.id.swType);
        imgFondo = findViewById(R.id.imgFondo);
        btnCancel = findViewById(R.id.btnCancel);
        checkBox = findViewById(R.id.check);
        String checkBoxText = "Acepto los <a style='color:#FFFFFF;' "+R.string.principalurl +"/politica/' > terminos y la política de privacidad.</a>";

        checkBox.setText(Html.fromHtml(checkBoxText));
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        //txtPassLost = findViewById(R.id.txtPassLost);
        txtHelp = findViewById(R.id.txtHelp);
        sp = findViewById(R.id.sp);
        edtCode = findViewById(R.id.edtCode);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginYaTengo = findViewById(R.id.btnLoginYaTengo);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation();
                Login();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnLoginYaTengo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HaveAccount.class);
                startActivityForResult(intent, 300);
            }
        });
        swType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ReadAllCompany();
                    imgFondo.setImageResource(R.drawable.img_fondo2);
                    type = "0";
                    //edtCode.setHint(R.string.edt_login2);
                } else {
                    type = "1";
                    imgFondo.setImageResource(R.drawable.img_fondo);
                    ReadAllSchool();
                    //edtCode.setHint(R.string.edt_login);
                }
            }
        });
        edtAU = (AutoCompleteTextView) findViewById(R.id.edtAU);
        edtAU.setThreshold(2);
        edtAU.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OSchools d : data){
                    Log.e("name",d.getName());
                    String name = d.getName() + " " + d.getDescription();
                    if(d.getName() != null && name.contains(item.toString())){
                        ID_rel = d.getID();
                    }
                    //something here
                }

            }
        });
        edtAU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                for(OSchools d : data){
                    Log.e("name",d.getName());
                    String name = d.getName() + " " + d.getDescription();
                    if(d.getName() != null && name.contains(item.toString())){
                        ID_rel = d.getID();
                    }
                    //something here
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ReadAllSchool();
    }

    public void ReadAllSchool() {
        type = "1";
        data.clear();
        spinnerArray.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllSchool")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        data.add(new OSchools("0", "¿A qué escuela perteneces?", "", "", ""));
                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            spinnerArray.add(name + " " + description);
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");
                            data.add(new OSchools(ID, name, description, create_date, image));

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, spinnerArray);
                        sp.setAdapter(adapter);
                        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                ID_rel = data.get(position).getID();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        final ADSchoolsAU adapterAU = new ADSchoolsAU(context, R.layout.item_au, data);
                        edtAU.setAdapter(adapter);
                        edtAU.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                System.out.println("Text [" + s + "]");

                                adapterAU.getFilter().filter(s.toString());

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

    public void ReadAllCompany() {
        type = "0";
        data.clear();
        spinnerArray.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAllCompany")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        data.add(new OSchools("0", "¿A qué empresa perteneces?", "", "", ""));
                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");


                            String description = obj.optString("description");
                            spinnerArray.add(name + " " + description);
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");
                            data.add(new OSchools(ID, name, description, create_date, image));

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, spinnerArray);
                        sp.setAdapter(adapter);
                        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                ID_rel = data.get(position).getID();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        final ADSchoolsAU adapterAU = new ADSchoolsAU(context, R.layout.item_au, data);
                        edtAU.setAdapter(adapter);
                        edtAU.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                System.out.println("Text [" + s + "]");

                                adapterAU.getFilter().filter(s.toString());

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

    public void Login() {

        if (!checkBox.isChecked()) {
            Alerter.create(Login.this)
                    .setTitle("Oh oh")
                    .setText("Debes aceptar los terminos y la política de privacidad para continuar.")
                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                    .show();
            btnLogin.revertAnimation();

            return;
        }
        edtCode.setError(null);
        final String code = edtCode.getText().toString();
        if (code.trim().equalsIgnoreCase("")) {
            edtCode.setError("El código es necesario.");
            btnLogin.revertAnimation();
            return;
        }
        if (ID_rel.equalsIgnoreCase("0")) {
            btnLogin.revertAnimation();
            Alerter.create(Login.this)
                    .setTitle("Oh oh")
                    .setText("Debes seleccionar la empresa o escuela a la que perteneces.")
                    .setBackgroundColorInt(getResources().getColor(R.color.yellow))
                    .show();
            return;
        }
        String url = getResources().getString(R.string.apiurl) + "Login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e("response", response);
                    btnLogin.revertAnimation();
                    String success = json.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        JSONArray arr = json.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("email");
                            String code = obj.optString("password");
                            String type = obj.optString("type_rel");
                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString("ID", ID);
                            editor.putString("name", name);
                            editor.putString("last_name", last_name);
                            editor.putString("email", email);
                            editor.putBoolean("policy", true);
                            editor.putString("code", code);
                            editor.putString("type", type);
                            editor.apply();


                        }

                        Intent intent = new Intent(context, PhoneValidator.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Alerter.create(Login.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados.")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Alerter.create(Login.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    btnLogin.revertAnimation();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("apikey", getResources().getString(R.string.apikey));
                params.put("codigo", edtCode.getText().toString());
                params.put("ID_rel", ID_rel);
                params.put("token", token_fb);
                params.put("type", type);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
