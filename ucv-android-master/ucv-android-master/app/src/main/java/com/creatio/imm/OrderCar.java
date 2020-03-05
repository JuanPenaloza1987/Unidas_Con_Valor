package com.creatio.imm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.imm.Adapters.ADCar;
import com.creatio.imm.Objects.OCar;
import com.creatio.imm.Objects.OInfo;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrderCar extends AppCompatActivity {
    private SharedPreferences prefs;
    private Context context;
    private Bundle extras;
    private ListView lvOrder;
    private ArrayList<OCar> data = new ArrayList<>();
    private ArrayList<OInfo> dataBranch = new ArrayList<>();
    private ArrayList<OInfo> dataArea = new ArrayList<>();
    private TextView txtTotal, txtReser;
    private Button btnMore;
    private ImageButton btnClose;
    private Button btnReserv;
    private String hourrev = "";
    private String daterev = "";
    private String personrev = "";
    private String area = "";
    private String has_reservation = "0";
    private String ID_car = "";
    private String totalgral = "";
    private String ID_business = "";
    Calendar dateSelected = Calendar.getInstance();
    private DatePickerDialog datePickerDialog;
    private Button btnCard;
    private String ID_branch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_car);
        context = OrderCar.this;
        extras = getIntent().getExtras();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        lvOrder = findViewById(R.id.lvOrder);
        btnClose = findViewById(R.id.btnClose);
        txtReser = findViewById(R.id.txtReser);
        txtTotal = findViewById(R.id.txtTotal);
        btnMore = findViewById(R.id.btnMore);
        btnReserv = findViewById(R.id.btnReserv);
        btnCard = findViewById(R.id.btnCard);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        btnReserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.layout_alert_reserv);
                // set the custom dialog components - text, image and button


                Button btnAceptar = dialog.findViewById(R.id.btnAceptar);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                final Button btnPerson = dialog.findViewById(R.id.btnPerson);
                final Button btnDate = dialog.findViewById(R.id.btnDate);
                final Button btnHour = dialog.findViewById(R.id.btnHour);
                final Button btnArea = dialog.findViewById(R.id.btnArea);

                btnArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu pop = new PopupMenu(context, btnArea);
                        for (int i = 0; i < dataArea.size(); i++) {
                            pop.getMenu().add(0, Integer.parseInt(dataArea.get(i).getPhone()), i, dataArea.get(i).getName());
                        }

                        pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                area = dataArea.get(item.getOrder()).getPhone();
                                btnArea.setText(item.getTitle().toString());
                                return true;
                            }
                        });
                        pop.show();
                    }
                });
                if (has_reservation.equalsIgnoreCase("1")) {
                    btnPerson.setText("Número de personas: " + personrev);
                    btnDate.setText(daterev);
                    btnHour.setText(hourrev);
                }
                btnPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final NumberPicker numberPicker = new NumberPicker(context);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setView(numberPicker);
                        numberPicker.setMaxValue(10);
                        numberPicker.setMinValue(0);


                        builder.setTitle("Número de personas");
                        builder.setMessage("Selecciona la cantidad");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnPerson.setText("Número de personas: " + numberPicker.getValue());
                                personrev = "" + numberPicker.getValue();
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create();
                        builder.show();
                    }
                });
                btnDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar newCalendar = dateSelected;
                        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                                if (new Date().before(dateSelected.getTime())){
                                    String myFormat = "yyyy-MM-dd"; //In which you need put here
                                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                                    btnDate.setText(sdf.format(dateSelected.getTime()));
                                    daterev = sdf.format(dateSelected.getTime());
                                }



                            }

                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.setTitle("Selecciona fecha de reservación");

                        datePickerDialog.show();
                    }

                });
                btnHour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                if (selectedMinute < 10) {
                                    btnHour.setText(selectedHour + ":0" + selectedMinute);
                                } else {
                                    btnHour.setText(selectedHour + ":" + selectedMinute);
                                }

                                hourrev = selectedHour + ":" + selectedMinute;
                            }
                        }, hour, minute, false);
                        mTimePicker.setTitle("Selecciona hora de reservación");
                        mTimePicker.show();

                    }
                });
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!personrev.equalsIgnoreCase("") && !hourrev.equalsIgnoreCase("") && !daterev.equalsIgnoreCase("")) {
                            has_reservation = "1";
                            txtReser.setText("Reservado");
                            btnReserv.setText(btnDate.getText().toString() + " " + btnHour.getText().toString());
                        } else {
                            Toast.makeText(OrderCar.this, "Configuración erronea en la asignación de reservación.", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        final Button btnBranch = findViewById(R.id.btnBranch);
        btnBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(context, btnBranch);
                for (int i = 0; i < dataBranch.size(); i++) {
                    pop.getMenu().add(0, Integer.parseInt(dataBranch.get(i).getPhone()), i, dataBranch.get(i).getName());
                }
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ID_branch = dataBranch.get(item.getOrder()).getPhone();
                        btnBranch.setText(item.getTitle().toString());
                        ReadAreas(ID_branch);
                        return true;
                    }
                });
                pop.show();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(155);
                finish();
            }
        });
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                dialog.setContentView(R.layout.layout_alert_pay);
                dialog.setCancelable(false);
                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnAcept = dialog.findViewById(R.id.btnAcept);
                final RadioButton radioButton3 = dialog.findViewById(R.id.radioButton3);
                final RadioButton radioButton4 = dialog.findViewById(R.id.radioButton4);

                btnAcept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String payment = "0";
                        if (radioButton3.isChecked()) {
                            payment = "1";
                        } else if (radioButton4.isChecked()) {
                            payment = "2";
                        } else {
                            payment = "2"; // efectivo.
                        }
                        Intent intent = new Intent(context, CardForm.class);
                        intent.putExtra("id_car", ID_car);
                        intent.putExtra("total", totalgral);
                        intent.putExtra("ID_business", ID_business);
                        intent.putExtra("ID_branch", ID_branch);
                        intent.putExtra("has_reservation", has_reservation);
                        intent.putExtra("personrev", personrev);
                        intent.putExtra("hourrev", hourrev);
                        intent.putExtra("area", area);
                        intent.putExtra("daterev", daterev);
                        intent.putExtra("payment", payment);
                        if (ID_branch.equalsIgnoreCase("")){
                            Alerter.create(OrderCar.this)
                                    .setTitle("Oh oh")
                                    .setText("Debese seleccionar una sucursal.")
                                    .setBackgroundColorInt(getResources().getColor(R.color.red))
                                    .show();
                        }else{

                            startActivityForResult(intent, 1);
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 199) {
            setResult(199);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetCar();
    }

    public void GetCar() {
        final SkeletonScreen skeletonScreen = Skeleton.bind(lvOrder)
                .load(R.layout.item_skeleton)
                .show();
        data.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "GetCar")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_user", prefs.getString("ID", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("e", response.toString());
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        double totalgr = 0.0;
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject object = arr.getJSONObject(i);
                            String ID = object.optString("ID_line");
                            String ID_menu = object.optString("ID_menu");
                            String name_menu = object.optString("name");
                            String price_menu = object.optString("price");
                            String quantity = object.optString("quantity");
                            String total = object.optString("total");
                            ID_car = object.optString("ID_car");
                            ID_business = object.optString("ID_business");
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString("ID_business", ID_business);
                            edit.apply();
                            String image = object.optString("image");
                            String ID_cupon = object.optString("ID_cupon");
                            if (price_menu.equalsIgnoreCase("0")) {
                                totalgr = totalgr + (Double.parseDouble(price_menu) * Double.parseDouble(quantity));

                            } else {
                                totalgr = totalgr + (Double.parseDouble(price_menu) * Double.parseDouble(quantity));

                            }
                            data.add(new OCar(ID, ID_menu, name_menu, price_menu, quantity, total, ID_car, image, ID_cupon));
                        }
                        skeletonScreen.hide();
                        totalgral = String.valueOf(totalgr);
                        txtTotal.setText(HelperClass.formatDecimal(totalgr) + " MXN");
                        ADCar adapter = new ADCar(context, data);
                        lvOrder.setAdapter(adapter);
                        ReadBusinessByID();

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

    public void ReadBusinessByID() {
        dataBranch.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadBusinessByID")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID", ID_business)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {


                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String description = obj.optString("description");
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");

                        }

                        JSONArray arrb = response.getJSONArray("datab");
                        for (int i = 0; i < arrb.length(); i++) {
                            JSONObject obj = arrb.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");
                            String address = obj.optString("address");
                            String create_date = obj.optString("create_date");
                            String image = obj.optString("image");
                            String phone = obj.optString("tel");
                            String horario = obj.optString("horario");

                            dataBranch.add(new OInfo(name, address, R.drawable.ic_branch, ID, horario));
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
    public void ReadAreas(String ID_branchh) {
        dataArea.clear();
        AndroidNetworking.post(getResources().getString(R.string.apiurl) + "ReadAreas")
                .addBodyParameter("apikey", getResources().getString(R.string.apikey))
                .addBodyParameter("ID_branch", ID_branchh)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String success = response.getString("success");
                    if (success.equalsIgnoreCase("true")) {




                        JSONArray arrb = response.getJSONArray("data");
                        for (int i = 0; i < arrb.length(); i++) {
                            JSONObject obj = arrb.getJSONObject(i);
                            String ID = obj.optString("ID");
                            String name = obj.optString("name");


                            dataArea.add(new OInfo(name, "", R.drawable.ic_branch, ID, ""));
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
}
