package com.beliefitsolution.buyonline.wishlist;

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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.beliefitsolution.buyonline.R;
import com.beliefitsolution.buyonline.Utility.AppUtilits;
import com.beliefitsolution.buyonline.Utility.Constant;
import com.beliefitsolution.buyonline.Utility.NetworkUtility;
import com.beliefitsolution.buyonline.Utility.SharePreferenceUtils;
import com.beliefitsolution.buyonline.WebServices.ServiceWrapper;
import com.beliefitsolution.buyonline.beanResponse.getWishlist;
import com.beliefitsolution.buyonline.cart.CartDetails;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WishlistDetails extends AppCompatActivity {

    //private NavigationView navigationView;
    //  private DrawerLayout drawer;
    private Menu mainmenu;
    private String TAG = "wishlist";
    private RecyclerView recycler_wishlist;
    private TextView wishlist_count;
    private Wishlist_Adapter wishlistAdapter;
    private ArrayList<Wishlist_Model> modelArrayList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/
        recycler_wishlist = (RecyclerView) findViewById(R.id.recycler_wishlist);
        wishlist_count = (TextView) findViewById(R.id.wishlist_count);

        LinearLayoutManager mLayoutManger3 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_wishlist.setLayoutManager(mLayoutManger3);
        recycler_wishlist.setItemAnimator(new DefaultItemAnimator());

        wishlistAdapter = new Wishlist_Adapter(this, modelArrayList);
        recycler_wishlist.setAdapter(wishlistAdapter);


        getUserWishlistDetails();


    }

    public void getUserWishlistDetails() {


        if (!NetworkUtility.isNetworkConnected(WishlistDetails.this)) {
            AppUtilits.displayMessage(WishlistDetails.this, getString(R.string.network_not_connected));

        } else {
            //  Log.e(TAG, "  user value "+ SharePreferenceUtils.getInstance().getString(Constant.USER_DATA));
            ServiceWrapper service = new ServiceWrapper(null);
            Call<getWishlist> call = service.getuserWisshlistCall("1234", SharePreferenceUtils.getInstance().getString(Constant.USER_DATA));
            call.enqueue(new Callback<getWishlist>() {
                @Override
                public void onResponse(Call<getWishlist> call, Response<getWishlist> response) {
                    Log.e(TAG, "response is " + response.body() + "  ---- " + new Gson().toJson(response.body()));
                    if (response.body() != null && response.isSuccessful()) {
                        if (response.body().getStatus() == 1) {

                            wishlist_count.setText(getString(R.string.you_have) + " " + String.valueOf(response.body().getInformation().size()) + " " +
                                    getString(R.string.product_in_cart));

                            modelArrayList.clear();

                            for (int i = 0; i < response.body().getInformation().size(); i++) {


                                modelArrayList.add(new Wishlist_Model(response.body().getInformation().get(i).getId(), response.body().getInformation().get(i).getName(),
                                        response.body().getInformation().get(i).getImgUrl(), response.body().getInformation().get(i).getPrice(),
                                        response.body().getInformation().get(i).getRating(), response.body().getInformation().get(i).getRatingCount()));

                            }

                            wishlistAdapter.notifyDataSetChanged();


                        } else {
                            AppUtilits.displayMessage(WishlistDetails.this, response.body().getMsg());
                        }
                    } else {
                        AppUtilits.displayMessage(WishlistDetails.this, getString(R.string.network_error));
                    }


                }

                @Override
                public void onFailure(Call<getWishlist> call, Throwable t) {

                    Log.e(TAG, "  fail- get wihslist item " + t.toString());
                    AppUtilits.displayMessage(WishlistDetails.this, getString(R.string.fail_togetwishlist));

                }
            });

        }
    }

    public void updatecartcount() {

        AppUtilits.UpdateCartCount(WishlistDetails.this, mainmenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.myaccount_toolbar_menu, menu);
        mainmenu = menu;
        if (mainmenu != null)
            AppUtilits.UpdateCartCount(WishlistDetails.this, mainmenu);
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
            Intent intent = new Intent(this, CartDetails.class);
            startActivity(intent);
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
          //  Intent intent = new Intent(this, WishlistDetails.class);
           // startActivity(intent);

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
            AppUtilits.UpdateCartCount(WishlistDetails.this, mainmenu);
    }


}