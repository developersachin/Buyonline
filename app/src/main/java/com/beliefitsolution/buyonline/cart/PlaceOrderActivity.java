package com.beliefitsolution.buyonline.cart;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beliefitsolution.buyonline.R;
import com.beliefitsolution.buyonline.Utility.AppUtilits;
import com.beliefitsolution.buyonline.Utility.Constant;
import com.beliefitsolution.buyonline.Utility.NetworkUtility;
import com.beliefitsolution.buyonline.Utility.SharePreferenceUtils;
import com.beliefitsolution.buyonline.WebServices.ServiceWrapper;
import com.beliefitsolution.buyonline.beanResponse.PlaceOrder;
import com.beliefitsolution.buyonline.home.HomeActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends AppCompatActivity {
    InstamojoPay instamojoPay = new InstamojoPay();
    InstapayListener listener;
    private String TAG = " PlaceOrderActivity";
    private String totalamount = "0", addressid = "0", delivermode = "instant_pay";
    private TextView place_order, continueshipping, orderIdtxt;
    private RadioButton radio_payment_gateway, radio_cash_on;
    private RelativeLayout relative_lay1, relative_lay2;
    private Boolean gotoHomeflag = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeorder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        final Intent intent = getIntent();
        totalamount = intent.getExtras().getString("amount");
        addressid = intent.getExtras().getString("addressid");

        radio_payment_gateway = (RadioButton) findViewById(R.id.radio_payment_gateway);
        radio_cash_on = (RadioButton) findViewById(R.id.radio_cash_on);
        orderIdtxt = (TextView) findViewById(R.id.orderIdtxt);
        continueshipping = (TextView) findViewById(R.id.continueshipping);

        relative_lay1 = (RelativeLayout) findViewById(R.id.relative_lay1);
        relative_lay2 = (RelativeLayout) findViewById(R.id.relative_lay2);


        place_order = (TextView) findViewById(R.id.place_order);
        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radio_payment_gateway.isChecked()) {
                    delivermode = "instant_pay";
                } else {
                    delivermode = "cash_ondelivery";
                }

                if (!addressid.equalsIgnoreCase("0")) {

                    if (delivermode.equals("instant_pay")) {

                        callInstamojoPay(SharePreferenceUtils.getInstance().getString(Constant.USER_email), SharePreferenceUtils.getInstance().getString(Constant.USER_phone),
                                totalamount, "Purchase from BuyOnline app ", SharePreferenceUtils.getInstance().getString(Constant.USER_name));
                    } else {
                        String orderid = "cashondelivery123";
                        String paymentid = "cashondelivery123456";
                        CallPlaceOrderAPI(orderid, paymentid, totalamount, addressid);
                    }
                } else {
                    AppUtilits.displayMessage(PlaceOrderActivity.this, getResources().getString(R.string.select_address));
                }

            }
        });

        continueshipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();

            }
        });


    }

    //............instamojo
    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;

        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, " payment done succesfully " + response);

                String resArray[] = response.split(":");
                Log.e(TAG, " array index 0 " + resArray[0] + "--orderid -" + resArray[1] + "---paymentid--" + resArray[3]);
                String orderid = resArray[1].substring(resArray[1].indexOf("=") + 1);
                String paymentid = resArray[3].substring(resArray[3].indexOf("=") + 1);

                Log.e(TAG, " order id " + orderid + " paymentid " + paymentid);

                CallPlaceOrderAPI(orderid, paymentid, totalamount, addressid);

                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                unregisterReceiver(instamojoPay);
            }

            @Override
            public void onFailure(int code, String reason) {
                Log.e(TAG, " payment fail " + code + "  reason -- " + reason);
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG).show();
            }
        };
    }

    public void CallPlaceOrderAPI(String orderid, String paymentid, String totalamount, String addressid) {

        if (!NetworkUtility.isNetworkConnected(PlaceOrderActivity.this)) {
            AppUtilits.displayMessage(PlaceOrderActivity.this, getString(R.string.network_not_connected));

        } else {

            ServiceWrapper serviceWrapper = new ServiceWrapper(null);
            //   Call<PlaceOrderActivity>  call = serviceWrapper.placceOrdercall("1234", SharePreferenceUtils.getInstance().getString(Constant.USER_DATA) )
            Call<PlaceOrder> call = serviceWrapper.placceOrdercall("1234", SharePreferenceUtils.getInstance().getString(Constant.USER_DATA), orderid, paymentid,
                    addressid, totalamount, SharePreferenceUtils.getInstance().getString(Constant.QUOTE_ID), delivermode);

            call.enqueue(new Callback<PlaceOrder>() {
                @Override
                public void onResponse(Call<PlaceOrder> call, Response<PlaceOrder> response) {
                    Log.e(TAG, "response is " + response.body() + "  ---- " + new Gson().toJson(response.body()));
                    //  Log.e(TAG, "  ss sixe 1 ");
                    if (response.body() != null && response.isSuccessful()) {
                        //    Log.e(TAG, "  ss sixe 2 ");
                        if (response.body().getStatus() == 1) {

                            gotoHomeflag = true;
                            relative_lay1.setVisibility(View.GONE);
                            relative_lay2.setVisibility(View.VISIBLE);

                            orderIdtxt.setText(response.body().getInformation().getOrderId());
                            SharePreferenceUtils.getInstance().saveString(Constant.QUOTE_ID, "");


                        } else {
                            AppUtilits.displayMessage(PlaceOrderActivity.this, response.body().getMsg());
                        }
                    } else {
                        AppUtilits.displayMessage(PlaceOrderActivity.this, getString(R.string.network_error));
                    }
                }

                @Override
                public void onFailure(Call<PlaceOrder> call, Throwable t) {

                    Log.e(TAG, "  fail- get user address " + t.toString());
                    AppUtilits.displayMessage(PlaceOrderActivity.this, getString(R.string.fail_togetaddress));

                }
            });


        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (gotoHomeflag) {
            Intent intent1 = new Intent(PlaceOrderActivity.this, HomeActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();
        }

    }
}
