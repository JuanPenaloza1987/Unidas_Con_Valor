package com.creatio.imm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gerardo on 22/06/18.
 */

public class HelperClass {
    public static String formatDecimal(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("$ #,###.00", symbols);
        String prezzo = decimalFormat.format(number);
        return prezzo;
    }

    public static void ShowAlert(Context context, String title, String msj, int type) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_alert_lottie);
        // set the custom dialog components - text, image and button
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
        txtTitle.setText(title);
        txtMsj.setText(msj);


        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
        if (type == 0) {
            btnCancelar.setVisibility(View.INVISIBLE);
        }
        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void ShowAlertCongratsCard(final Context context, final CardForm activity, String title, String msj, String badge) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alert_icupon);
        // set the custom dialog components - text, image and button
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
        TextView txtAlert = (TextView) dialog.findViewById(R.id.txtAlert);
        txtTitle.setText(title);
        txtMsj.setText(msj);
        txtAlert.setText(badge);


        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);

        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.finish();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void ShowAlertCongrats(final Context context, final ItemsMenu activity, String title, String msj, String badge) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_alert_icupon);
        // set the custom dialog components - text, image and button
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
        TextView txtAlert = (TextView) dialog.findViewById(R.id.txtAlert);
        txtTitle.setText(title);
        txtMsj.setText(msj);
        txtAlert.setText(badge);


        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);

        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.create(activity)
                        .setTitle("Puedes ver tus cupones")
                        .setText("Pulsa aqui para ir.")
                        .setDuration(5000)
                        .setBackgroundColorInt(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setIcon(R.drawable.ic_done_white_48dp)
                        .enableVibration(true)
                        .enableSwipeToDismiss()
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, MyCupons.class);
                                activity.finish();
                                context.startActivity(intent);

                            }
                        }).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void TheCuponIsReserved(String ID_cupon, final Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        AndroidNetworking.post(context.getResources().getString(R.string.apiurl) + "ReservedCupon")
                .addBodyParameter("apikey", context.getResources().getString(R.string.apikey))
                .addBodyParameter("ID_cupon", ID_cupon)
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(com.androidnetworking.common.Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {

                    } else {

                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error catch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(context, "Error HTTP", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public static String FormatDate(String strCurrentDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date newDate = format.parse(strCurrentDate);

            format = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            String date = format.format(newDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

}
