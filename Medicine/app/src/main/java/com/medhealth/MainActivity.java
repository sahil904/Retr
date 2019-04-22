package com.medhealth;

import fragments.AboutUsFragment;
import fragments.BookAppointmentFragment;
import fragments.BookLabTest;
import fragments.DynamicFragment;
import fragments.HomeNew;
import fragments.NewMyOrderFragment;
import fragments.Contact;
import fragments.UploadPrescriptionFragment;
import gcm.QuickstartPreferences;
import gcm.RegistrationIntentService;
import imgLoader.AnimateFirstDisplayListener;
import imgLoader.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.medi.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import util.ConnectionDetector;
import fragments.MyProfileFragment;
import Config.ConstValue;
import adapters.MenuAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {
	ActionBar actionBar;
	public static final String TAG = "MainActivity";
    private boolean mLogShown;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDeawerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    String titleNews,desc;

    ArrayList<HashMap<String, String>> menuArray;
    ListView mListView;
    MenuAdapter mAdapter;
    
    public SharedPreferences settings;
	public ConnectionDetector cd;
	 private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	    DisplayImageOptions options;
		ImageLoaderConfiguration imgconfig;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//hitUrlForGetAName();
		titleNews = getIntent().getStringExtra("title");
		desc = getIntent().getStringExtra("desc");

		//Log.d("titleNews",titleNews);

		settings = getSharedPreferences(ConstValue.MAIN_PREF, 0);

		cd = new ConnectionDetector(this);

		File cacheDir = StorageUtils.getCacheDirectory(this);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icons_side_menu_03)
				.showImageForEmptyUri(R.drawable.icons_side_menu_03)
				.showImageOnFail(R.drawable.icons_side_menu_03)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new SimpleBitmapDisplayer())
				.imageScaleType(ImageScaleType.EXACTLY)
				.build();

		imgconfig = new ImageLoaderConfiguration.Builder(this)
				.build();
		ImageLoader.getInstance().init(imgconfig);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.action_bar_title, null);
		actionBar.setCustomView(v);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDeawerView = (LinearLayout) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/**
			 * Called when a drawer has settled in a completely open state.
			 */

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
			}
		};

		// Set the drawer toggle as the DrawerListener

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		menuArray = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("title", "My Profile");
		map.put("menu_icon",Integer.toString(R.drawable.profile_menu_icon));
		//map.put("desc", "Update Your Profile");
		menuArray.add(map);

//		map = new HashMap<String, String>();
//		map.put("title", "Upload Prescription");
//		map.put("menu_icon",Integer.toString(R.drawable.upload_menu_icon));
//		//map.put("desc", "Your Order Detail");
//		menuArray.add(map);

		map = new HashMap<String, String>();
		map.put("title", "My Order");
		map.put("menu_icon",Integer.toString(R.drawable.myorder_menu_icon));
		//map.put("desc", "Current Item In Cart");
		menuArray.add(map);

//		map = new HashMap<String, String>();
//		map.put("title", "View Cart");
//		map.put("menu_icon",Integer.toString(R.drawable.view_cart_menu_icon));
//		//map.put("desc", "view terms & condition ");
//		menuArray.add(map);
//
//		map = new HashMap<>();
//		map.put("title", "Consult A Doctor");
//		map.put("menu_icon",Integer.toString(R.drawable.consult_menu_icon));
//		//map.put("desc", "Contact Us");
//		menuArray.add(map);
//
//        //menuArray.add(map);
//
//		map = new HashMap<String, String>();
//		map.put("title", "Book Lab Test");
//		map.put("menu_icon",Integer.toString(R.drawable.labbook_menu_icon));
//		//map.put("desc", "view terms & condition ");
//		menuArray.add(map);

		map = new HashMap<String, String>();
		map.put("title", "Contact Us");
		map.put("menu_icon",Integer.toString(R.drawable.conytact_uc_menu_icon));
		//map.put("desc", "view terms & condition ");
		menuArray.add(map);

//		map = new HashMap<>();
//		map.put("title",settings.getString("title",""));
//		map.put("menu_icon",Integer.toString(R.drawable.news_menu_icon));
//		menuArray.add(map);

		map = new HashMap<String, String>();
		map.put("title", "About Us");
		map.put("menu_icon",Integer.toString(R.drawable.about_us_menu_icon));
		//map.put("desc", "view terms & condition ");
		menuArray.add(map);

		map = new HashMap<String, String>();
		map.put("title", "Logout");
		map.put("menu_icon",Integer.toString(R.drawable.logout_menu_icon));
        menuArray.add(map);


		mListView = (ListView) findViewById(R.id.listView1);

		LayoutInflater inflatorMenu = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View menu_header = inflatorMenu.inflate(R.layout.menu_header_view, null);
		TextView txtUserName = (TextView) menu_header.findViewById(R.id.left_user_title);
		txtUserName.setText(settings.getString("user_name", ""));
		TextView txtLocation = (TextView) menu_header.findViewById(R.id.left_user_desc);
		txtLocation.setText(settings.getString("user_city", ""));
		RoundedImageView imageProfile = (RoundedImageView) menu_header.findViewById(R.id.left_user_image);
		imageProfile.setImageResource(R.drawable.logo_med);

		/*ImageLoader.getInstance().displayImage(ConstValue.IMAGE_PROFILE_PATH + settings.getString("user_image", ""), imageProfile, options, animateFirstListener);

		Log.d("Image_Profile", settings.getString("user_image", ""));
*/
		mListView.addHeaderView(menu_header);

		mAdapter = new MenuAdapter(getApplicationContext(), menuArray);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				set_fragment_page(arg2);
			}
		});
		if (getIntent().hasExtra("fragment_position")) {
			set_fragment_page(getIntent().getExtras().getInt("fragment_position"));
		} else {
			set_fragment_page(0);
		}
		//----GCM SERVICE START----//
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences sharedPreferences =
						PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences
						.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
				if (sentToken) {

				} else {

				}
			}
		};

		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(this, RegistrationIntentService.class);
			startService(intent);
		}

		TextView tvAddress = (TextView) findViewById(R.id.tvAddress);

		/*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			return;
		}
		Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
		String city =locationhere(location.getLatitude(),location.getLongitude());
		tvAddress.setText(city);
*/
	}
/*
	private String locationhere(double lat,double lon){
		String cityname="";
		String sublocality="";
		Geocoder geocoder=new Geocoder(this,Locale.getDefault());
		List<Address> addresses;
		try {
			addresses=geocoder.getFromLocation(lat,lon,10);
			if (addresses.size()>=0){for (Address adr:addresses){
				if (adr.getLocality()!=null && adr.getLocality().length()>0){
					cityname=adr.getLocality()+" "+adr.getSubLocality();
					break;
				}
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityname;
	}*/
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this,MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
		else if(id==R.id.action_viewcart){
				Intent intent = new Intent(MainActivity.this,NewViewCartActivity.class);
				startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
	@Override
	public void onTabSelected(Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		set_fragment_page(Integer.parseInt(arg0.getTag().toString()));
	}
	@Override
	public void onTabUnselected(Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onTabReselected(Tab arg0,
			android.support.v4.app.FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
	public void set_fragment_page(int position){
		Fragment fragment = null;
		Intent intent = null;
		Bundle args;
		switch (position) {
		case 0:
			fragment = new HomeNew();
	        args = new Bundle();
	        fragment.setArguments(args);
			break;
			case 1:
				 fragment = new MyProfileFragment();

                break;
//			case 2:
//				fragment = new UploadPrescriptionFragment();
//				break;
			case 2:
				fragment = new NewMyOrderFragment();
				break;
//			case 4:
//				intent = new Intent(MainActivity.this, NewViewCartActivity.class);
//				break;
//			case 5:
//				fragment = new BookAppointmentFragment();
//				break;
//			case 6:
//				fragment = new BookLabTest();
//				break;
			case 3:
				fragment = new Contact();
				break;
//			case 8:
//				fragment = new DynamicFragment();
//				break;
            case 4:
                fragment = new AboutUsFragment();
                break;
			case 5:
				if (cd.isConnectingToInternet()){
					SharedPreferences myPrefs = getSharedPreferences(ConstValue.MAIN_PREF, MODE_PRIVATE);
					SharedPreferences.Editor editor = myPrefs.edit();
					editor.clear();
					editor.commit();
					Toast.makeText(getApplicationContext(),getString(R.string.mainactivity_logout_sucessfull), Toast.LENGTH_SHORT).show();
					Intent intent1 = new Intent(MainActivity.this, BeforeLoginActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
					break;
				}else {
					showSnackBar(this.getWindow().getDecorView().findViewById(android.R.id.content),"No Internet Connection",R.color.fab_color_1);
					//Toast.makeText(this, "Please Connect Your Phone to Internet", Toast.LENGTH_SHORT).show();
					break;
				}
		default:
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
		if (fragment!=null) {
	        // Insert the fragment by replacing any existing fragment
	        FragmentManager fragmentManager = getFragmentManager();
	        if (position==0) {
	        	fragmentManager.beginTransaction()
	            .replace(R.id.sample_content_fragment, fragment)
	            .commit();
			}else{
				fragmentManager.beginTransaction()
	            .replace(R.id.sample_content_fragment, fragment)
	            .addToBackStack(null)
	            .commit();
			}
			mDrawerLayout.closeDrawer(mDeawerView);
	    }
	}
	@Override
	public void onBackPressed() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.mainactivity_mainactivity_are_exit))
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							finish();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
	}
	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
	}
	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG,getString(R.string.mainactivity_this_device));
				finish();
			}
			return false;
		}
		return true;
	}

	private void showSnackBar(View view,String message,int colorId){
		Snackbar snackbar;
		snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
				.setAction("Action", null);
		View snackBarView = snackbar.getView();
		snackBarView.setBackgroundColor(colorId);
		snackbar.show();
	}
}
