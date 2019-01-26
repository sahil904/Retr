//package adapters;
//
//
//import imgLoader.AnimateFirstDisplayListener;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.freshvegi.MyApplication;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.nostra13.universalimageloader.utils.StorageUtils;
//
//import Config.ConstValue;
//import models.FruitsModel;
//import util.Interfac;
//import util.ObjectSerializer;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Paint;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.freshvegi.R;
//
//
//public class ProductsAdapter extends BaseAdapter {
//	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
//String ProductsAdapter = "ProductsAdapter";
//
//
//	 public static HashMap<String, String> selectedProduct;
//	private Context context;
//	private ArrayList<HashMap<String, String>> postItems;
//	public static SharedPreferences settings;
//	public final String PREFS_NAME = "Products";
//
//	DisplayImageOptions options;
//	ImageLoaderConfiguration imgconfig;
//	Interfac interfac;
//	ArrayList<HashMap<String, String>> cartArray;
//	ArrayList<FruitsModel> al ;
//
//
//	public ProductsAdapter(Context context, ArrayList<HashMap<String, String>> arraylist,Interfac interfac,ArrayList<HashMap<String, String>> cartArray, ArrayList<FruitsModel> al){
//		this.context = context;
//		this.cartArray = cartArray;
//		this.interfac = interfac;
//		this.al = al;
//		postItems = arraylist;
//		File cacheDir = StorageUtils.getCacheDirectory(context);
//		options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.loading)
//		.showImageForEmptyUri(R.drawable.loading)
//		.showImageOnFail(R.drawable.loading)
//		.cacheInMemory(true)
//		.cacheOnDisk(true)
//		.considerExifParams(true)
//		.displayer(new SimpleBitmapDisplayer())
//		.imageScaleType(ImageScaleType.EXACTLY)
//		.build();
//
//		imgconfig = new ImageLoaderConfiguration.Builder(context)
//		.build();
//		ImageLoader.getInstance().init(imgconfig);
//
//
//		settings = context.getSharedPreferences(PREFS_NAME, 0);
//		//Log.d("setting",String.valueOf(settings));
//
//
//	}
//	public static HashMap<String, String> selectedProduct() {
//		selectedProduct = new HashMap<String, String>();
//
//		try {
//			selectedProduct = (HashMap<String, String>) ObjectSerializer.deserialize(settings.getString("selectedProduct",ObjectSerializer.serialize(new HashMap<String, String>())));
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return selectedProduct;
//	}
//
//	@Override
//	public int getCount() {
//		return al.size();
//	}
//	@Override
//	public Object getItem(int position) {
//		return al.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//
//		if (convertView == null) {
//			LayoutInflater mInflater = (LayoutInflater)
//					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//			convertView = mInflater.inflate(R.layout.row_products , null);
//		}
//		if (postItems.size() > 0) {
//
//
//		final FruitsModel map21 = al.get(position);
//		final  HashMap<String,String> map = new HashMap();
//
//		RelativeLayout custom_dialog_layout = convertView.findViewById(R.id.custom_dialog_layout);
//
//		/*custom_dialog_layout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				SelectCartItemDialog selectCartItemDialog = new SelectCartItemDialog(context , postItems);
//				selectCartItemDialog.show();
//
//			}
//		});*/
//		ImageView imgProduct = (ImageView) convertView.findViewById(R.id.proimage);
//		ImageLoader.getInstance().displayImage(ConstValue.PRO_IMAGE_BIG_PATH + map21.image, imgProduct, options, animateFirstListener);
//
//		TextView txtTitle = (TextView) convertView.findViewById(R.id.proTitle);
//		txtTitle.setText(map21.title);
//
//		final TextView txtPrice = convertView.findViewById(R.id.txtprice);
//		txtPrice.setText(map21.price);
//		final TextView txtUnit = (TextView) convertView.findViewById(R.id.txtunit);
//		txtUnit.setText(map.get("gmqty"));
//
//		final TextView textDiscount = (TextView) convertView.findViewById(R.id.textDiscount);
//		textDiscount.setVisibility(View.GONE);
//
//		TextView txtDiscountFlag = (TextView) convertView.findViewById(R.id.textDiscountFlag);
//		txtDiscountFlag.setVisibility(View.GONE);
//
//		TextView textCurrency = (TextView) convertView.findViewById(R.id.textCurrency);
//		textCurrency.setText(map.get("currency"));
//
//		RelativeLayout _decrease = (RelativeLayout) convertView.findViewById(R.id.decrese);
//		RelativeLayout _increase = (RelativeLayout) convertView.findViewById(R.id.increse);
//		RelativeLayout addtocart = (RelativeLayout) convertView.findViewById(R.id.btnBuy);
//		final RelativeLayout quantityLayout = convertView.findViewById(R.id.quantity_layout);
//		final RelativeLayout addLayout = convertView.findViewById(R.id.add_layout);
//
//
//		final RelativeLayout cartPriceLayout = convertView.findViewById(R.id.cart_price_layout);
//		/*final TextView cartProductPrice = convertView.findViewById(R.id.cart_txtprice);
//		final TextView cartProductQuantity = convertView.findViewById(R.id.cart_txtunit);
//		TextView cartCurrency = convertView.findViewById(R.id.cart_textCurrency);
//		cartCurrency.setText(map.get("currency"));
//		TextView cartProductUnit = convertView.findViewById(R.id.cart_txtgm);
//		cartProductUnit.setText(map.get("unit"));
//*/
//		TextView txtgm = (TextView) convertView.findViewById(R.id.txtgm);
//		txtgm.setText(map.get("unit"));
//
//		long a = Integer.parseInt(map.get("total_qty_stock").toString());
//		long b = Integer.parseInt(map.get("consume_qty_stock").toString());
//
//		long result = a - b;
//		TextView txtstock = (TextView) convertView.findViewById(R.id.stock);
//		txtstock.setText(String.valueOf(result) + " " + map.get("type") + " In Stock");
//
//
//		final TextView _value = (TextView) convertView.findViewById(R.id.value1);
//
//
//		_decrease.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Log.d("src", "Decresing value");
//				if (Integer.parseInt(_value.getText().toString()) > 1) {
//					_value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) - 1));
//
//					Log.d("Posss", String.valueOf(position));
//					/*HashMap<String, String> selectedProduct = new HashMap<>();
//					selectedProduct = postItems.get(position);
//					//Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
//					selectedProduct.put("qty", _value.getText().toString());
//					selectedProduct.put("price", txtPrice.getText().toString());*/
//
//					String str = map.get("qty");
//					String string = map.get("price");
//
//					/*cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Float.parseFloat(textDiscount.getText().toString())));
//					cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));
//*/
//
//					decrementCart(Double.parseDouble(textDiscount.getText().toString()));
//					interfac.change();
//					SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
//					Log.d("UserIDs", userPref.getString("userid", "0"));
//					hitUrlToAddProductInCart(userPref.getString("userid", "0"), postItems.get(position).get("id"), _value.getText().toString());
//
//				} else if (Integer.parseInt(_value.getText().toString()) == 1) {
//					_value.setText("0");
//					cartPriceLayout.setVisibility(View.GONE);
//					quantityLayout.setVisibility(View.GONE);
//					addLayout.setVisibility(View.VISIBLE);
//					decrementCart(Double.parseDouble(textDiscount.getText().toString()));
//					interfac.change();
//
//					SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
//					Log.d("UserIDs", userPref.getString("userid", "0"));
//					hitUrlToAddProductInCart(userPref.getString("userid", "0"), postItems.get(position).get("id"), _value.getText().toString());
//
//				}
//			}
//		});
//		_increase.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				_value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) + 1));
//
//
//				HashMap<String, String> selectedProduct = new HashMap<>();
//				selectedProduct = postItems.get(position);
//				//Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
//				selectedProduct.put("qty", _value.getText().toString());
//				selectedProduct.put("price", txtPrice.getText().toString());
//
///*
//				cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Float.parseFloat(textDiscount.getText().toString())));
//				cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));*/
//				incrementCart(Double.parseDouble(textDiscount.getText().toString()));
//				interfac.change();
//				SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
//				Log.d("UserIDs", userPref.getString("userid", "0"));
//				hitUrlToAddProductInCart(userPref.getString("userid", "0"), postItems.get(position).get("id"), _value.getText().toString());
//				cartPriceLayout.setVisibility(View.VISIBLE);
//
//
//			}
//		});
//		addtocart.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//
//				//ConstValue.productToCart.add(selected_product);
//			}
//		});
//		textDiscount.setText(map.get("price"));
//		if (!map.get("discount").equalsIgnoreCase("") && !map.get("discount").equalsIgnoreCase("0")) {
//			Double discount = Double.parseDouble(map.get("discount"));
//			Double price = Double.parseDouble(map.get("price"));
//			Double discount_amount = discount * price / 100;
//
//			Double effected_price = price - discount_amount;
//			//txtPrice.setBackgroundResource(R.drawable.strike_trough);
//			txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//			textDiscount.setVisibility(View.VISIBLE);
//			textDiscount.setText(effected_price.toString());
//
//			txtDiscountFlag.setVisibility(View.VISIBLE);
//			txtDiscountFlag.setText(discount + "% off");
//
//		}
//
//		//TextView txtUnit = (TextView)convertView.findViewById(R.id.txtunit);
//		//txtUnit.setText(map.get("gmqty"));
//
//
//		//txtstock.setText(map.get("stock")+" "+map.get("type")+" In Stock");
//
//
//		addLayout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				quantityLayout.setVisibility(View.VISIBLE);
//				addLayout.setVisibility(View.GONE);
//				_value.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) + 1));
//
//
//				HashMap<String, String> selectedProduct = new HashMap<>();
//				selectedProduct = postItems.get(position);
//				//Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
//				selectedProduct.put("qty", _value.getText().toString());
//				selectedProduct.put("price", txtPrice.getText().toString());
//
///*
//
//				cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Float.parseFloat(textDiscount.getText().toString())));
//				cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));
//*/
//				incrementCart(Double.parseDouble(textDiscount.getText().toString()));
//				interfac.change();
//				SharedPreferences userPref = context.getSharedPreferences(ConstValue.MAIN_PREF, 0);
//				Log.d("UserIDs", userPref.getString("userid", "0"));
//				hitUrlToAddProductInCart(userPref.getString("userid", "0"), postItems.get(position).get("id"), _value.getText().toString());
//				cartPriceLayout.setVisibility(View.VISIBLE);
//			}
//		});
//
//		if (map.get("my_qty").equals("0")) {
//			quantityLayout.setVisibility(View.GONE);
//			addLayout.setVisibility(View.VISIBLE);
//			_value.setText("0");
//			cartPriceLayout.setVisibility(View.GONE);
//		} else {
//			addLayout.setVisibility(View.GONE);
//			quantityLayout.setVisibility(View.VISIBLE);
//			_value.setText(map.get("my_qty"));
//			cartPriceLayout.setVisibility(View.VISIBLE);
//			/*cartProductPrice.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Float.parseFloat(textDiscount.getText().toString())));
//			cartProductQuantity.setText(String.valueOf(Integer.parseInt(_value.getText().toString()) * Integer.parseInt(txtUnit.getText().toString())));*/
//		}
//	}
//
//        return convertView;
//	}
//
//	private void hitUrlToAddProductInCart(final String userId, final String productId, final String quantity){
//		Log.d("ISDDDD",String.valueOf(userId+","+productId+","+quantity));
//		Log.d("URLSS",ConstValue.JSON_ADD_PRODUCT_TO_SERVER);
//		StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.JSON_ADD_PRODUCT_TO_SERVER,
//				new Response.Listener<String>() {
//					@Override
//					public void onResponse(String response) {
//
//						Log.d("ISDDDD",response);
//					}
//				}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//
//			}
//		}){
//			@Override
//			public String getBodyContentType() {
//				return "application/x-www-form-urlencoded; charset=UTF-8";
//			}
//			@Override
//			protected Map<String,String> getParams(){
//				Map<String,String> params = new HashMap<String, String>();
//				params.put("userId",userId);
//				params.put("productId",productId);
//				params.put("qty",quantity);
//
//				return params;
//			}
//
//		};
//
//		stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//				10000,
//				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//		RequestQueue requestQueue = Volley.newRequestQueue(context);
//		requestQueue.add(stringRequest);
//	}
//
//
//	private String calculateMoney(String discountStr,String selQty,String priceStr) {
//
//		Double discount = Double.parseDouble(discountStr);
//		Double price = Double.parseDouble(priceStr);
//
//		price = price * Double.parseDouble(selQty);
//
//		Double discount_amount = discount * price / 100;
//
//		Double effected_price = price - discount_amount;
//
//		return String.valueOf(effected_price);
//
//	}
//
//	private void incrementCart(Double incrementedPrice){
//		Double price = Double.valueOf(MyApplication.getInstance().getTotalPrice());
//		MyApplication.getInstance().setTotalPrice(String.valueOf(price+incrementedPrice));
//		MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity())+1));
//	}
//
//	private void decrementCart(Double decrementedPrice){
//		Double price = Double.valueOf(MyApplication.getInstance().getTotalPrice());
//		MyApplication.getInstance().setTotalPrice(String.valueOf(price-decrementedPrice));
//		MyApplication.getInstance().setTotalQuantity(String.valueOf(Integer.parseInt(MyApplication.getInstance().getTotalQuantity())-1));
//	}
//
//}
//
