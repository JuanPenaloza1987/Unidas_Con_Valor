package com.creatio.imm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class HaveAccount extends AppCompatActivity {
    private CircularProgressButton btnLogin;
    private Context context;
    private SharedPreferences prefs;
    private String token_fb = "";
    private String type = "1";
    private EditText edtCode;
    private EditText edtPass;
    private TextView txtPassLost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_account);
        context = HaveAccount.this;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ya tengo una cuenta");
        btnLogin = findViewById(R.id.btnLogin);
        edtCode = findViewById(R.id.edtCode);
        edtPass = findViewById(R.id.edtPass);
        txtPassLost = findViewById(R.id.txtPassLost);
        token_fb = FirebaseInstanceId.getInstance().getToken();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation();
                Login();
            }
        });
        txtPassLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PhoneValidatorHaveAccount.class);
                startActivity(intent);
            }
        });
        Glide.with(this).load(R.drawable.img_fondo)
                .apply(bitmapTransform(new BlurTransformation(25, 3)))
                .into((ImageView) findViewById(R.id.img_fondo));
    }


    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();

    }

    public void Login() {
        final String phone = edtCode.getText().toString();
        final String pass = edtPass.getText().toString();

        edtCode.setError(null);
        if (phone.trim().equalsIgnoreCase("")) {
            edtCode.setError("El teléfono es necesario.");
            btnLogin.revertAnimation();
            return;
        }
        edtPass.setError(null);
        if (pass.trim().equalsIgnoreCase("")) {
            edtPass.setError("El password es necesario.");
            btnLogin.revertAnimation();
            return;
        }

        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "LoginHaveAccount")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("phone", phone)
                .addBodyParameter("password", pass)
                .addBodyParameter("token", token_fb)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response",response.toString());
                try {
                    btnLogin.revertAnimation();
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String last_name = obj.optString("last_name");
                            String email = obj.optString("email");
                            String phone = obj.optString("email");
                            String code = obj.optString("password");
                            String type = obj.optString("type_rel");
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("login", true);
                            editor.putBoolean("datauser", true);
                            editor.putString("ID", ID);
                            editor.putString("name", name);
                            editor.putString("last_name", last_name);
                            editor.putString("phone", phone);
                            editor.putString("email", email);
                            editor.putString("code", code);
                            editor.putString("type", type);
                            editor.apply();


                        }

                        setResult(300);
                        finish();

                    } else {
                        Alerter.create(HaveAccount.this)
                                .setTitle("Oh oh")
                                .setText("No existe información con los datos proporcionados.")
                                .setBackgroundColorInt(getResources().getColor(R.color.red))
                                .show();
                    }
                } catch (JSONException e) {
                    Alerter.create(HaveAccount.this)
                            .setTitle("Oh oh")
                            .setText("No existe información con los datos proporcionados.")
                            .setBackgroundColorInt(getResources().getColor(R.color.red))
                            .show();
                    btnLogin.revertAnimation();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Alerter.create(HaveAccount.this)
                        .setTitle("Oh oh")
                        .setText("No existe información con los datos proporcionados.")
                        .setBackgroundColorInt(getResources().getColor(R.color.red))
                        .show();
                btnLogin.revertAnimation();
            }
        });
    }
}
