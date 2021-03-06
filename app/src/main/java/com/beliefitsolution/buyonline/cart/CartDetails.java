package com.beliefitsolution.buyonline.cart;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.beliefitsolution.buyonline.R;
import com.beliefitsolution.buyonline.Utility.AppUtilits;
import com.beliefitsolution.buyonline.Utility.Constant;
import com.beliefitsolution.buyonline.Utility.NetworkUtility;
import com.beliefitsolution.buyonline.Utility.SharePreferenceUtils;
import com.beliefitsolution.buyonline.WebServices.ServiceWrapper;
import com.beliefitsolution.buyonline.beanResponse.getCartDetails;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartDetails extends AppCompatActivity {

    //private NavigationView navigationView;
    //private DrawerLayout drawer;
    public static Menu mainmenu;
    public TextView cart_totalamt;
    private String TAG = "cartdetailss";
    private RecyclerView recycler_cartitem;
    private TextView cart_count, continuebtn;
    private Cart_Adapter cartAdapter;
    private ArrayList<Cartitem_Model> cartitemModels = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartdetails);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        //  drawer.addDrawerListener(toggle);
        //toggle.syncState();


        // navigationView = (NavigationView) findViewById(R.id.nav_view);
        // navigationView.setNavigationItemSelectedListener(this);

        recycler_cartitem = (RecyclerView) findViewById(R.id.recycler_cartitem);
        cart_count = (TextView) findViewById(R.id.cart_count);
        cart_totalamt = (TextView) findViewById(R.id.cart_totalamt);
        continuebtn = (TextView) findViewById(R.id.continuebtn);

        LinearLayoutManager mLayoutManger3 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_cartitem.setLayoutManager(mLayoutManger3);
        recycler_cartitem.setItemAnimator(new DefaultItemAnimator());

        cartAdapter = new Cart_Adapter(this, cartitemModels);
        recycler_cartitem.setAdapter(cartAdapter);


        getUserCartDetails();

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String temp = cart_totalamt.getText().toString().replace("Rs ", "");
                if (!temp.equalsIgnoreCase("") && Float.valueOf(temp) > 0) {

                    // then go to order summer page
                    Intent intent = new Intent(CartDetails.this, Order_Summary.class);
                    startActivity(intent);

                } else {

                    AppUtilits.displayMessage(CartDetails.this, getString(R.string.no_prod_into_cart));
                }

            }
        });

    }

    public void getUserCartDetails() {

        if (!NetworkUtility.isNetworkConnected(CartDetails.this)) {
            AppUtilits.displayMessage(CartDetails.this, getString(R.string.network_not_connected));

        } else {
            //  Log.e(TAG, "  user value "+ SharePreferenceUtils.getInstance().getString(Constant.USER_DATA));
            ServiceWrapper service = new ServiceWrapper(null);
            Call<getCartDetails> call = service.getCartDetailsCall("1234", SharePreferenceUtils.getInstance().getString(Constant.QUOTE_ID),
                    SharePreferenceUtils.getInstance().getString(Constant.USER_DATA));

            call.enqueue(new Callback<getCartDetails>() {
                @Override
                public void onResponse(Call<getCartDetails> call, Response<getCartDetails> response) {
                    Log.e(TAG, "response is " + response.body() + "  ---- " + new Gson().toJson(response.body()));
                    //  Log.e(TAG, "  ss sixe 1 ");
                    if (response.body() != null && response.isSuccessful()) {
                        //    Log.e(TAG, "  ss sixe 2 ");
                        if (response.body().getStatus() == 1) {
                            //      Log.e(TAG, "  ss sixe 3 ");

                            cart_totalamt.setText("Rs " + response.body().getInformation().getTotalprice());
                            cart_count.setText(getString(R.string.you_have) + " " + String.valueOf(response.body().getInformation().getProdDetails().size()) + " " +
                                    getString(R.string.product_in_cart));

                            Log.e(TAG, " size is  " + String.valueOf(response.body().getInformation().getProdDetails().size()));
                            SharePreferenceUtils.getInstance().saveInt(Constant.CART_ITEM_COUNT, response.body().getInformation().getProdDetails().size());
                            AppUtilits.UpdateCartCount(CartDetails.this, mainmenu);


                            cartitemModels.clear();

                            for (int i = 0; i < response.body().getInformation().getProdDetails().size(); i++) {


                                cartitemModels.add(new Cartitem_Model(response.body().getInformation().getProdDetails().get(i).getId(),
                                        response.body().getInformation().getProdDetails().get(i).getName(),
                                        response.body().getInformation().getProdDetails().get(i).getImgUrl(), response.body().getInformation().getProdDetails().get(i).getMrp(),
                                        response.body().getInformation().getProdDetails().get(i).getPrice(), response.body().getInformation().getProdDetails().get(i).getQty()));

                            }

                            cartAdapter.notifyDataSetChanged();


                        } else {
                            AppUtilits.displayMessage(CartDetails.this, response.body().getMsg());
                        }
                    } else {
                        AppUtilits.displayMessage(CartDetails.this, getString(R.string.network_error));
                    }

                }

                @Override
                public void onFailure(Call<getCartDetails> call, Throwable t) {
                    Log.e(TAG, "  fail- add to cart item " + t.toString());
                    AppUtilits.displayMessage(CartDetails.this, getString(R.string.fail_toGetcart));

                }
            });


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.myaccount_toolbar_menu, menu);
        mainmenu = menu;
        if (mainmenu != null)
            AppUtilits.UpdateCartCount(CartDetails.this, mainmenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.e(TAG, "  option click " + item.getTitle());
        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {

            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) mainmenu.findItem(R.id.search).getActionView();
            final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            searchEditText.setHint(getString(R.string.search_name));

            searchEditText.setHintTextColor(getResources().getColor(R.color.white));
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    //  Log.e("onClick: ", "-- " + searchEditText.getText().toString().trim());
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //run query to the server
                        Log.e("onClick: ", "-- " + searchEditText.getText().toString().trim());
                        if (null != searchEditText.getText().toString().trim() && !searchEditText.getText().toString().trim().equals("")) {

                        }
                        //  AppUtils.GetSearchResult( HomeActivity.this, TAG, searchEditText.getText().toString());
                    }
                    return false;
                }
            });
            return true;
        } else if (id == R.id.cart) {
            //  Intent intent = new Intent(this, CartDetails.class);
            // startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

/*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id =  item.getItemId();
        Log.e(TAG, "navi option "+ item.getTitle());

        if (id == R.id.nav_home){


        }else if (id == R.id.nav_new_prod){


        }else if (id == R.id.nav_myaccount){


        }else if (id == R.id.nav_wishlist){
            Intent intent = new Intent(this, WishlistDetails.class);
            startActivity(intent);

        }else if (id == R.id.nav_logout){


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        if (mainmenu != null)
            AppUtilits.UpdateCartCount(CartDetails.this, mainmenu);
    }

}
