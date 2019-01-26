package fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.medhealth.ItemDetailsActivity;
import com.medhealth.MyApplication;
import com.medhealth.NewViewCartActivity;
import com.medi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Config.ConstValue;
import imgLoader.AnimateFirstDisplayListener;
import models.FruitsModel;
import models.HomeModel;
import models.NewModelArray;
import util.AlertInterface;
import util.ConnectionDetector;
import util.Interfac;
import util.ObjectSerializer;


@SuppressWarnings("ALL")
public class CatDynamicFragment extends Fragment {
    ProgressDialog dialog;
    public SharedPreferences settings;
    public ConnectionDetector cd;
    NewProductsAdapter adapter;
    ArrayList<FruitsModel> al;
    ListView products_listview;
    DisplayImageOptions options;
    ImageLoaderConfiguration imgconfig;
    RelativeLayout noInternet_connection;

    TextView txtcount, totalItem, totalPrice, Item;

    ArrayList<HashMap<String, String>> cartArray;
    MyCart cart;
    HomeModel homeModel;
    String id, name;
    Interfac interfac;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    HashMap<String, Integer> dialogHashMap;
    int cartQty = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_products, container, false);
        noInternet_connection = view.findViewById(R.id.noInternet_connection);
        settings = getContext().getSharedPreferences(ConstValue.MAIN_PREF, 0);
        cd = new ConnectionDetector(getActivity());
        initializeDialog();
        if (cd.isConnectingToInternet()){
            showProgressDialog();
            noInternet_connection.setVisibility(View.GONE);
            hitUrlForProduct();
        }else {
            noInternet_connection.setVisibility(View.VISIBLE);
            //NO Internet Layout
        }



     //   showProgressDialog();


        al = new ArrayList<>();
        products_listview = view.findViewById(R.id.listView1);
        adapter = new NewProductsAdapter(getActivity(), al, interfac);
        products_listview.setAdapter(adapter);

        File cacheDir = StorageUtils.getCacheDirectory(getActivity());
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .imageScaleType(ImageScaleType.NONE)
                .build();

        imgconfig = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        ImageLoader.getInstance().init(imgconfig);

        ArrayList<HashMap<String, String>> categoryArray = new ArrayList<HashMap<String, String>>();
        try {
            categoryArray = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(settings.getString("categoryname", ObjectSerializer.serialize(new ArrayList<HashMap<String, String>>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cartArray = new ArrayList<HashMap<String, String>>();
        cart = new MyCart(getActivity());
        cartArray = cart.get_items();
        TextView txtTitle = view.findViewById(R.id.catname);
        txtTitle.setText(name);
        txtcount = view.findViewById(R.id.textcount);
        totalItem = view.findViewById(R.id.totalitem);
        return view;
    }


    private void hitUrlForProduct() {
        Log.d("USerId",settings.getString("userid", "00"));
        dialogHashMap = new HashMap<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("productCATDYN", response);
                hideProgressDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        FruitsModel fruitsModel = new FruitsModel();
                        fruitsModel.catId = jsonObject.getString("category_id");
                        fruitsModel.cod = jsonObject.getString("cod");
                        fruitsModel.deliverycharge = jsonObject.getString("deliverycharge");
                        fruitsModel.description = jsonObject.getString("description");
                        fruitsModel.emi = jsonObject.getString("emi");
                        fruitsModel.id = jsonObject.getString("id");
                        fruitsModel.image = jsonObject.getString("image");
                        fruitsModel.on_date = jsonObject.getString("on_date");
                        fruitsModel.slug = jsonObject.getString("slug");
                        fruitsModel.status = jsonObject.getString("status");
                        fruitsModel.tax = jsonObject.getString("tax");
                        fruitsModel.title = jsonObject.getString("title");
                        ArrayList<NewModelArray> priceArray = new ArrayList<>();

                        JSONArray itemArray = jsonObject.getJSONArray("discounts");
                        dialogHashMap.put(fruitsModel.id, 0);
                        for (int j = 0; j < itemArray.length(); j++) {
                            NewModelArray newModelArray = new NewModelArray();
                            JSONObject itemObj = itemArray.getJSONObject(j);
                            newModelArray.cartquantity = itemObj.getString("cartquantity");
                            newModelArray.currency = itemObj.getString("currency");
                            newModelArray.discount = itemObj.getString("discount");
                            newModelArray.gmqty = itemObj.getString("gmqty");
                            Log.d("GmQty",newModelArray.gmqty);
                            newModelArray.id = itemObj.getString("id");
                            newModelArray.market_price = itemObj.getString("market_price");
                            newModelArray.product_id = itemObj.getString("product_id");
                            newModelArray.sale_price = itemObj.getString("sale_price");
                            newModelArray.slug = itemObj.getString("slug");
                            newModelArray.unit = itemObj.getString("unit");
                            priceArray.add(newModelArray);
                        }
                        fruitsModel.productArray = priceArray;
                        al.add(fruitsModel);
                        if (al.size() > 0) {
                            adapter.notifyDataSetChanged();
                            noInternet_connection.setVisibility(View.GONE);
                        }
                    }
                    if (al.size() == 0) {
                        products_listview.setVisibility(View.GONE);
                        noInternet_connection.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                 Log.d("VolleyError", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userId", settings.getString("userid", "00"));
                params.put("id", id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public static CatDynamicFragment newInstance(HomeModel homeModel, String id, String name, Interfac interfac) {

        CatDynamicFragment catDynamicFragment = new CatDynamicFragment();
        catDynamicFragment.setData(homeModel, id, name, interfac);
        return catDynamicFragment;
    }
    public void setData(HomeModel homeModel, String id, String name, Interfac interfac) {
        this.homeModel = homeModel;
        this.id = id;
        this.name = name;
        this.interfac = interfac;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getActivity().finish();
        } else if (id == R.id.action_viewcart) {
            Intent intent = new Intent(getActivity(), NewViewCartActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else if (id == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NewProductsAdapter extends BaseAdapter {


        public ArrayList<FruitsModel> al;
        public Context context;
        DisplayImageOptions options;
        ImageLoaderConfiguration imgconfig;
         String  cartItemQty ;
        Interfac interfac;
        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

        public NewProductsAdapter(Context context, ArrayList<FruitsModel> al, Interfac interfac) {

            this.context = context;
            this.al = al;
            this.interfac = interfac;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.loading)
                    .showImageOnFail(R.drawable.loading)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

            imgconfig = new ImageLoaderConfiguration.Builder(context)
                    .build();

            ImageLoader.getInstance().init(imgconfig);
        }

        @Override
        public int getCount() {

            if(al == null || al.size()>0)
                return al.size();
            else
                return 0;

        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater)
                        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.row_products, null);
            }

            if (al.size() >= 0) {

                final FruitsModel model = al.get(position);


                final NewModelArray newModelArray = model.productArray.get(0);

                cartItemQty = newModelArray.cartquantity;

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ItemDetailsActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("qty",cartItemQty);

                        bundle.putSerializable("itemDeatil",model);

                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
                final TextView price_text_dialog = convertView.findViewById(R.id.price_text_dialog);

                RelativeLayout custom_dialog_layout = convertView.findViewById(R.id.custom_dialog_layout);
                custom_dialog_layout.setTag(position);

                TextView dis_pric_symbol = convertView.findViewById(R.id.dis_pric_symbol);

                ImageView imgProduct = (ImageView) convertView.findViewById(R.id.proimage);
                ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH + model.image, imgProduct, options, animateFirstListener);

                TextView txtTitle = (TextView) convertView.findViewById(R.id.proTitle);
                txtTitle.setText(model.title);

                final TextView txtPrice = convertView.findViewById(R.id.txtprice);

                txtPrice.setText(String.valueOf(Math.round(Double.parseDouble(newModelArray.market_price))));


                final TextView txtUnit = (TextView) convertView.findViewById(R.id.txtunit);
                txtUnit.setText(newModelArray.gmqty);

                final TextView textDiscount = (TextView) convertView.findViewById(R.id.textDiscount);
                textDiscount.setVisibility(View.GONE);

                TextView txtDiscountFlag = (TextView) convertView.findViewById(R.id.textDiscountFlag);
                txtDiscountFlag.setVisibility(View.GONE);

                if (!newModelArray.discount.equalsIgnoreCase("0") && !newModelArray.discount.equalsIgnoreCase("")) {
                    txtDiscountFlag.setVisibility(View.VISIBLE);

                    txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txtPrice.setTextColor(Color.RED);
                    txtPrice.setTextSize(13.0f);
                    textDiscount.setVisibility(View.VISIBLE);
                    textDiscount.setText(String.valueOf(Math.round(Float.parseFloat(newModelArray.sale_price))));
                    txtDiscountFlag.setVisibility(View.VISIBLE);
                    dis_pric_symbol.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    dis_pric_symbol.setVisibility(View.VISIBLE);
                    dis_pric_symbol.setTextColor(Color.RED);
                    dis_pric_symbol.setTextSize(13.0f);

                    txtDiscountFlag.setText(Math.round(Float.parseFloat(newModelArray.discount)) + "% off");


                } else {
                    txtDiscountFlag.setVisibility(View.GONE);
                }

                TextView textCurrency = (TextView) convertView.findViewById(R.id.textCurrency);
                // textCurrency.setText(model.currency);

                RelativeLayout _decrease = (RelativeLayout) convertView.findViewById(R.id.decrese);
                RelativeLayout _increase = (RelativeLayout) convertView.findViewById(R.id.increse);
                RelativeLayout addtocart = (RelativeLayout) convertView.findViewById(R.id.btnBuy);
                final RelativeLayout quantityLayout = convertView.findViewById(R.id.quantity_layout);
                final RelativeLayout addLayout = convertView.findViewById(R.id.add_layout);


                final RelativeLayout cartPriceLayout = convertView.findViewById(R.id.cart_price_layout);
                final TextView cartProductPrice = convertView.findViewById(R.id.cart_txtprice);
                final TextView cartProductQuantity = convertView.findViewById(R.id.cart_txtunit);
                TextView cartCurrency = convertView.findViewById(R.id.cart_textCurrency);
                cartCurrency.setText(newModelArray.currency);
                TextView cartProductUnit = convertView.findViewById(R.id.cart_txtgm);
                cartProductUnit.setText(newModelArray.unit);

                TextView txtgm = (TextView) convertView.findViewById(R.id.txtgm);

                final TextView _value = (TextView) convertView.findViewById(R.id.value1);

                _decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (Integer.parseInt(_value.getText().toString()) > 1) {
                            _value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) - 1));

                            cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Math.round(Float.parseFloat(textDiscount.getText().toString()))));
                            cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));

                            decrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);

                            interfac.change();

                            SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
                            // Log.d("UserIDs", userPref.getString("userid", "0"));

                            initializeDialog();
                            showProgressDialog();

                            hitUrlToAddProductInCart(userPref.getString("userid", "0"), model.productArray.get(0).product_id, _value.getText().toString(), model.productArray.get(dialogHashMap.get(model.id)).id);

                        } else if (Integer.parseInt(_value.getText().toString()) == 1) {

                            _value.setText("0");
                            cartPriceLayout.setVisibility(View.GONE);
                            quantityLayout.setVisibility(View.GONE);
                            addLayout.setVisibility(View.VISIBLE);
                            decrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                            interfac.change();
                            SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
                            // Log.d("UserIDs", userPref.getString("userid", "0"));
                            initializeDialog();
                            showProgressDialog();
                            hitUrlToAddProductInCart(userPref.getString("userid", "0"), model.productArray.get(0).product_id, _value.getText().toString(), model.productArray.get(dialogHashMap.get(model.id)).id);
                        }
                    }
                });

                _increase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        initializeDialog();
                        showProgressDialog();
                        _value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) + 1));
                        // Log.d("double", String.valueOf(Integer.parseInt(_value.getText().toString()) * Float.parseFloat(textDiscount.getText().toString())));
                        cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Math.round(Float.parseFloat(textDiscount.getText().toString()))));
                        cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));
                        incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                        interfac.change();
                        SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
                        // Log.d("UserIDs", userPref.getString("userid", "0"));
                        hitUrlToAddProductInCart(userPref.getString("userid", "0"), model.productArray.get(0).product_id, _value.getText().toString(), model.productArray.get(dialogHashMap.get(model.id)).id);
                        cartPriceLayout.setVisibility(View.GONE);
                    }
                });

                addLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        initializeDialog();
                        showProgressDialog();

                        quantityLayout.setVisibility(View.VISIBLE);
                        addLayout.setVisibility(View.GONE);

                        try {
                            _value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) + 1));
                            cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Math.round(Float.parseFloat(textDiscount.getText().toString()))));
                            cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));
                            incrementCart(Float.parseFloat(textDiscount.getText().toString()), 1);
                            interfac.change();
                            SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);

                            // Log.d("CART_DIS_ID", model.productArray.get(dialogHashMap.get(model.id)).id);
                            hitUrlToAddProductInCart(userPref.getString("userid", "0"), model.productArray.get(0).product_id, _value.getText().toString(), model.productArray.get(dialogHashMap.get(model.id)).id);
                            cartPriceLayout.setVisibility(View.GONE);
                        }catch (Exception e){e.printStackTrace();}
                    }
                });

                if (newModelArray.cartquantity.equals("0")) {
                    quantityLayout.setVisibility(View.GONE);
                    addLayout.setVisibility(View.VISIBLE);
                    _value.setText("0");
                    cartPriceLayout.setVisibility(View.GONE);
                } else {
                    addLayout.setVisibility(View.GONE);
                    quantityLayout.setVisibility(View.VISIBLE);
                    _value.setText(newModelArray.cartquantity);
                    cartPriceLayout.setVisibility(View.GONE);
                    if (! _value.getText().toString().equalsIgnoreCase("")) {
                        try {
                            cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Math.round(Float.parseFloat(textDiscount.getText().toString()))));
                            cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));
                        }catch (Exception e){}
                    }

                }

                custom_dialog_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final ArrayList<String> dialogList = new ArrayList<>();

                        for (int j = 0; j < model.productArray.size(); j++) {
                            final NewModelArray modelForPrice = model.productArray.get(j);
                            dialogList.add(String.valueOf(Math.round((Float.parseFloat(modelForPrice.sale_price)))) + "/" + String.valueOf(Integer.parseInt(modelForPrice.gmqty)) + modelForPrice.unit);
                        }

                        AlertInterface alertInterface = new AlertInterface() {
                            @Override
                            public void select(int positionAlert, String price) {

                                textDiscount.setText(model.productArray.get(positionAlert - 1).sale_price);


//                                // Log.d("In cart", _value.getText().toString());
                                txtPrice.setText(String.valueOf(Math.round(Float.parseFloat(model.productArray.get(positionAlert-1).market_price))));
                                price_text_dialog.setText(price);

                                int inCart = Integer.parseInt(_value.getText().toString());
//                                // // Log.d("check", "Kuldeep");

                                SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
                                initializeDialog();
                                showProgressDialog();
                                String url = ConstValue.CHECK_PRODUCT_IN_CART+"&userId="+userPref.getString("userid", "0")+"&id="+model.id+"&discount_id="+model.productArray.get(positionAlert - 1).id;
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideProgressDialog();

                                         Log.d("CartProduct",response);
                                        try {
                                            JSONObject qtyObj = new JSONObject(response);
                                            if (!qtyObj.getBoolean("error")){
                                                cartQty = qtyObj.getInt("cartquantity");
                                                Log.d("cartQty",String.valueOf(cartQty));
                                                if (cartQty == 0) {
                                                    _value.setText(String.valueOf(cartQty));
                                                    addLayout.setVisibility(View.VISIBLE);
                                                    quantityLayout.setVisibility(View.GONE);
                                                } else {
                                                    _value.setText(String.valueOf(cartQty));
                                                    addLayout.setVisibility(View.GONE);
                                                    quantityLayout.setVisibility(View.VISIBLE);
                                                }
                                            }else {
                                                _value.setText(String.valueOf(0));
                                                addLayout.setVisibility(View.VISIBLE);
                                                quantityLayout.setVisibility(View.GONE);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error",String.valueOf(error));
                                        hideProgressDialog();
                                    }
                                }) {
               /* @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("", userId);
                    params.put("", productId);
                    params.put("", discountId);
                    return params;
                }*/
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(context);
                                requestQueue.add(stringRequest);

                                  Log.d("CartCodeEx","Code exe");
                                dialogHashMap.put(model.id, positionAlert - 1);

                            }
                        };
                        SelectCartItemDialog selectCartItemDialog = new SelectCartItemDialog(context, dialogList, alertInterface, model.id);
                        selectCartItemDialog.show();
                    }
                });
                price_text_dialog.setText(String.valueOf(textDiscount.getText().toString()) + "/" + newModelArray.gmqty + newModelArray.unit);

            }
            return convertView;
        }

        private void hitUrlToAddProductInCart(final String userId, final String productId, final String quantity, final String discountId) {

            String url = ConstValue.JSON_ADD_PRODUCT_TO_SERVER+"&userId="+userId+"&id="+productId+"&discount_id="+discountId+"&cartquantity="+quantity;


             Log.d("ADD-Uid-proId-Qty-DISID", String.valueOf(userId + "," + productId + "," + quantity + "," + discountId));
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgressDialog();

                            cartItemQty = String.valueOf(Integer.parseInt(cartItemQty)+1);



                            // Log.d("ISDDDD", response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

               /* @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userId", userId);
                    params.put("productId", productId);
                    params.put("qty", quantity);
                    params.put("dis_id", discountId);
                    return params;
                }*/
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

        private String calculateMoney(String discountStr, String selQty, String priceStr) {

            Double discount = Double.parseDouble(discountStr);
            Double price = Double.parseDouble(priceStr);

            price = price * Double.parseDouble(selQty);

            Double discount_amount = discount * price / 100;

            Double effected_price = price - discount_amount;

            return String.valueOf(effected_price);
        }

        private void decrementCart(Float decrementedPrice, int decerementQty) {

            Float price = Float.valueOf(MyApplication.getInstance().getTotalPrice());
            // Log.d("price,discount DEc", String.valueOf(price + "," + decrementedPrice));
            Float totalSum = price - (decrementedPrice * decerementQty);
            MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalSum)));
            MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) - decerementQty));
        }

        private void incrementCart(Float incrementedPrice, int incrementQty) {
            // Log.d("numbr", String.format("%.2f", incrementedPrice));

            Float price = Float.valueOf(MyApplication.getInstance().getTotalPrice());

            // Log.d("price,discount ADD", String.valueOf(price + "," + incrementedPrice));

            Float totalSum = price + (incrementedPrice * incrementQty);

            MyApplication.getInstance().setTotalPrice(String.valueOf(Math.round(totalSum)));

            MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity()) + incrementQty));
        }

    }


    public class SelectCartItemDialog extends AlertDialog implements DialogInterface.OnDismissListener {
        Context context;
        AlertInterface alertInterface;
        private ArrayList<String> postItems;
        String proId;

        public SelectCartItemDialog(@NonNull Context context, ArrayList<String> postItems, AlertInterface alertInterface, String proId) {
            super(context);
            this.context = context;
            this.postItems = postItems;
            this.alertInterface = alertInterface;
            this.proId = proId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.select_pack_recycler);
            RecyclerView select_item_recycler = findViewById(R.id.dialog_recycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            select_item_recycler.setLayoutManager(linearLayoutManager);
            CartItemAdapter cartItemAdapter = new CartItemAdapter(context, postItems, proId);
            select_item_recycler.setAdapter(cartItemAdapter);
            // Log.d("Sizes", String.valueOf(postItems.size()));
            setOnDismissListener(this);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {


        }

        public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyCartHolderDialog> {
            Context context;
            private ArrayList<String> postItems;
            String proId;

            public CartItemAdapter(Context context, ArrayList<String> postItems, String proId) {
                this.context = context;
                this.postItems = postItems;
                this.proId = proId;
            }

            @Override
            public CartItemAdapter.MyCartHolderDialog onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_pack_item, parent, false);
                return new CartItemAdapter.MyCartHolderDialog(view);
            }

            @Override
            public void onBindViewHolder(MyCartHolderDialog holder, int position) {


                holder.dialog_price.setText(postItems.get(position));

                // Log.d("price", postItems.get(position));

            }

            @Override
            public int getItemCount() {
                return postItems.size();

            }

            public class MyCartHolderDialog extends RecyclerView.ViewHolder {
                TextView dialog_price;

                public MyCartHolderDialog(View itemView) {
                    super(itemView);
                    dialog_price = itemView.findViewById(R.id.dialog_price);


                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            int type = getAdapterPosition();
                            // Log.d("type", String.valueOf(type));
                            alertInterface.select((getAdapterPosition() + 1), dialog_price.getText().toString());

                        }
                    });
                }
            }
        }

    }

    private void initializeDialog() {
        dialog = new ProgressDialog(getActivity(),R.style.ProgrssTheme);
        dialog.setCancelable(false);
        dialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
    }
    private void showProgressDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void hideProgressDialog() {
        dialog.cancel();

    }
}
