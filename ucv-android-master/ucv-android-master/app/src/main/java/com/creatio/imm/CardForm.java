package com.creatio.imm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONObject;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;

public class CardForm extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {

    private static final CardType[] SUPPORTED_CARD_TYPES = {CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY};
    Bundle extras;
    private SupportedCardTypesView mSupportedCardTypesView;
    private Button btnPagar;
    protected com.braintreepayments.cardform.view.CardForm mCardForm;
    private SharedPreferences pref;
    private boolean oxxo = false;
    private String latlng;
    private boolean updateCard = false;

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);

        btnPagar = (Button) findViewById(R.id.btnPagar);
        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        extras = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Solicitud de pago </font>"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_close);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

            //El pago es con tarjeta
            if (pref.getBoolean("conekta", false) && !updateCard) {
                latlng = "0,0";
                SaveOrder();
            }





        mCardForm = findViewById(R.id.card_form);
        mCardForm.isCardScanningAvailable();
        mCardForm.getCountryCodeEditText().setText("52");
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .postalCodeRequired(false)
                .cvvRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("\nHola " + pref.getString("nombre", "") + ", tus datos son encriptados y guardados con seguridad.")
                .actionLabel("ENVIAR")
                .setup(this);

        mCardForm.setOnCardTypeChangedListener(this);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(CardForm.this);
                final ProgressDialog dialogs = new ProgressDialog(CardForm.this);
                dialogs.setMessage("Guardando información");
                dialogs.show();
                if (mCardForm.isValid()) {
                    Activity activity = CardForm.this;

                    Conekta.setPublicKey("key_JyR36LT8uqzqtVs5XqMhBrw");
                    Conekta.setApiVersion("1.0.0");
                    Conekta.collectDevice(activity);

                    Card card = new Card(
                            pref.getString("nombre", "No registro") + " " + pref.getString("apellido", "Sin registro"),
                            mCardForm.getCardNumber(), mCardForm.getCvv(),
                            mCardForm.getExpirationMonth(),
                            mCardForm.getExpirationYear());
                    Token token = new Token(activity);

                    token.onCreateTokenListener(new Token.CreateToken() {
                        @Override
                        public void onCreateTokenReady(JSONObject data) {
                            try {
                                try {

                                    if (updateCard) {
                                        Toast.makeText(CardForm.this, "Actualizando tarjeta", Toast.LENGTH_SHORT).show();
                                        AndroidNetworking.post(R.string.apiurl + "UpdateCard")
                                                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                                                .addBodyParameter("ID_user", pref.getString("ID", "0"))
                                                .addBodyParameter("token", data.getString("id"))
                                                .addBodyParameter("phone", mCardForm.getMobileNumber())
                                                .setPriority(Priority.IMMEDIATE)
                                                .build().getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("Dta desc", response);
                                                dialogs.dismiss();

                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Log.e("Tag Error", anError.toString());
                                                dialogs.dismiss();
                                            }
                                        });
                                    } else {
                                        AndroidNetworking.post(R.string.apiurl + "ConektaCustumer")
                                                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                                                .addBodyParameter("ID_user", pref.getString("ID", "0"))
                                                .addBodyParameter("token_id", data.getString("id"))
                                                .addBodyParameter("phone", mCardForm.getMobileNumber())
                                                .setPriority(Priority.IMMEDIATE)
                                                .build().getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response) {
                                                Log.e("Dta desc", response);
                                                if (response.contains("id") || response.contains("creado")) {
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putBoolean("conekta", true);
                                                    editor.apply();
                                                    SaveOrder();

                                                }
                                                dialogs.dismiss();
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                dialogs.dismiss();
                                            }
                                        });
                                    }
                                } catch (Exception err) {
                                    dialogs.dismiss();
                                }

                            } catch (Exception err) {
                                dialogs.dismiss();
                            }
                        }
                    });

                    token.create(card);
                } else {
                    mCardForm.validate();
                    Toast.makeText(CardForm.this, "Invalido", Toast.LENGTH_SHORT).show();
                    dialogs.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 199 && resultCode == -1) {
            //Lanzar alerta metodo de pago
            final Place place = PlacePicker.getPlace(data, this);
            latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
            if (pref.getBoolean("conekta", false) && !updateCard) {
                SaveOrder();
            }
        }
    }

    public void SaveOrder() {
        final ProgressDialog dialogs = new ProgressDialog(CardForm.this);
        dialogs.setMessage("Guardando orden");
        dialogs.show();
        AndroidNetworking.post(R.string.apiurl + "SaveOrder")
                .addBodyParameter("ID_user", pref.getString("ID", "0"))
                .addBodyParameter("id_car", extras.getString("id_car", "0"))
                .addBodyParameter("total", extras.getString("total", "0"))
                .addBodyParameter("ID_business", extras.getString("ID_business", "0"))
                .addBodyParameter("ID_branch", extras.getString("ID_branch", "0"))
                .addBodyParameter("has_reservation", extras.getString("has_reservation", "0"))
                .addBodyParameter("personrev", extras.getString("personrev", "0"))
                .addBodyParameter("hourrev", extras.getString("hourrev", "0"))
                .addBodyParameter("daterev", extras.getString("daterev", "0"))
                .addBodyParameter("area", extras.getString("area", "0"))
                .addBodyParameter("payment", extras.getString("payment", "0"))
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Order ", response.toString());
                dialogs.dismiss();
                setResult(199);
                HelperClass.ShowAlertCongratsCard(CardForm.this, CardForm.this, "¡Felicidades!", "Tu orden se ha guardado correctamente. Puedes verla a tra vez de tus ordenes.", "Gracias por usarnos, no se te hara ningún cargo hasta que tengas tu pedido en la mano.");
            }

            @Override
            public void onError(ANError error) {
                // handle error
                dialogs.dismiss();
                Log.e("order error", error.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onCardFormSubmit() {

    }
}
