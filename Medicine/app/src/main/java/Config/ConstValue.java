package Config;

import java.util.HashMap;

import org.json.JSONObject;



public class ConstValue {

	//---Client ID for Paypal. Create your own app and add client id here. https://developer.paypal.com/developer/applications/
	public static final String CONFIG_CLIENT_ID = "Aa4VQ8QL_oWS78tLrR9voNg-Pqi0lu3bcCMisE1Lez_OKwXQgo4t5mpP5dSxauY8JVkaowHc8AVFfJxX";

	public static final String CURRENCY = "USD";
	public static final String wallet = "50";

	//public static  String SITE_URL = "http://www.webappmob.co.in";
	public static String Service_key="5642vcb546gfnbvb7r6ewc211365vhh34";
	public static String Service_URL = "http://medhelth.mobileappdevelop.xyz/visitapi/index.php/visit";
	public static String SITE_URL = "http://medhelth.mobileappdevelop.xyz";
	public static String LAB_BOOKING = SITE_URL+"/index.php?component=json&action=show_booklab";
	public static String CONTACT_US = SITE_URL+"/index.php?component=json&action=get_contact";
	public static String GET_NEWS = SITE_URL+"/index.php?component=json&action=get_news";
	public static String ABOUT_US = SITE_URL+"/index.php?component=json&action=get_about";
	public static String PRISCRIPTION_FORM = SITE_URL+"/index.php?component=json&action=add_prescription";
	public static String APPOINTMENT_FORM = SITE_URL+"/index.php?component=json&action=appointment_form";
	public static String APPOINTMENT_DETAIL = SITE_URL+"/index.php?component=json&action=appointment_details";
	public static String ITEM_DETAIL = SITE_URL+"/index.php?component=json&action=item_details";
	public static String CHECK_PRODUCT_IN_CART = SITE_URL+"/index.php?component=json&action=check_product_in_cart";
	public static String REPEAT_ORDER = SITE_URL+"/index.php?component=json&action=repeat_order";
	public static String RESCHEDULE_ORDER = SITE_URL+"/index.php?component=json&action=upadate_delivery_date_time";
	public static String JSON_LOGIN = SITE_URL+"/index.php?component=json&action=login";
	public static String JSON_FORGOT = SITE_URL+"/index.php?component=json&action=forgot";
	public static String JSON_REGISTER=SITE_URL+"/index.php?component=json&action=signup";
	public static String JSON_CITY=SITE_URL+"/index.php?component=json&action=get_city";
	public static String JSON_CATEGORY = SITE_URL+"/index.php?component=json&action=get_categories";

	public static String JSON_PRODUCTS=SITE_URL+"/index.php?component=json&action=latest_product";
	//public static String JSON_PRODUCTS=SITE_URL+"/index.php?component=json&action=get_products_by_category";
	public static String JSON_PRODUCTS_DETAIL=SITE_URL+"/index.php?component=json&action=get_product_detail";
	public static String JSON_PROFILE_UPDATE=SITE_URL+"/visitapi/index.php/book_service";
	public static String JSON_ADD_ORDER=SITE_URL+"/index.php?component=json&action=add_order";
	public static String JSON_LIST_ORDER=SITE_URL+"/index.php?component=json&actio n=list_order";
	public static String JSON_ORDER_DETAIL=SITE_URL+"/index.php?component=json&action=order_detail";
	public static String JSON_SLIDER_IMAGE=SITE_URL+"/index.php?component=json&action=get_slider_image";
	public static String JSON_CANCLE_ORDER=SITE_URL+"/index.php?component=json&action=cancle_order";
	public static String JSON_HOME_URL = SITE_URL+"/index.php?component=json&action=home_page";
	//public static String JSON_HOME_URL = "http://appsdevelopment.info/vegetablesmarket/index.php?component=json&action=home_page";
	public static String JSON_ORDER_HISTORY_URL = SITE_URL+"/index.php?component=json&action=get_order";
	public static String JSON_DELIVERY_TIME_URL = SITE_URL+"/index.php?component=json&action=get_schedule_delivery_time";
	public static String JSON_CHECK_COUPON = SITE_URL+"/index.php?component=json&action=check_coupon";

	public static String JSON_SETTINGS=SITE_URL+"/index.php?component=json&action=get_settings";
	public static String JSON_ADD_PRODUCT_TO_SERVER = SITE_URL+"/index.php?component=json&action=add_cart";
	public static String GET_CART_URL = SITE_URL+"/index.php?component=json&action=get_cart";
	public static String DELETE_CART = SITE_URL+"/index.php?component=json&action=delete_cart";
	
	public static String CAT_IMAGE_BIG_PATH=SITE_URL+"/userfiles/contents/big/";
	public static String CAT_IMAGE_SMALL_PATH=SITE_URL+"/userfiles/contents/small/";
	public static String CAT_IMAGE_ICON_PATH=SITE_URL+"/userfiles/contents/icon/";
	
	public static String SLIDER_IMAGE_BIG_PATH=SITE_URL+"/userfiles/contents/big/";
	public static String SLIDER_IMAGE_SMALL_PATH=SITE_URL+"/userfiles/contents/small/";
	public static String SLIDER_IMAGE_ICON_PATH=SITE_URL+"/userfiles/contents/icon/";
	public static String APPOINTMENT_IMAGE = SITE_URL+"/userfiles/contents/appointment/";
	
	public static String PRO_IMAGE_BIG_PATH=SITE_URL+"/userfiles/products/big/";
	public static String PRO_IMAGE_SMALL_PATH=SITE_URL+"/userfiles/products/small/";
	public static String PRO_IMAGE_ICON_PATH=SITE_URL+"/userfiles/products/icon/";
	
	public static String IMAGE_PROFILE_PATH=SITE_URL+"/userfiles/profile/big/";

	//public static String OTP_URL = "http://amazesms.in/api/pushsms.php?usr=";

	//public static String OTP_URL ="http://103.209.146.119/vendorsms/pushsms.aspx?clientid=85b911b3-8497-4cbb-8182-5ed99ba70bf0&apikey=28c9758d-5ee0-450a-b414-d8a90a713270";

	public static String OTP_URL = "http://control.msg91.com/api/sendotp.php";
	public static String NEW_LOGIN_URL = SITE_URL+"/index.php?component=json&action=get_search_password";
	
	public static String GCM_SENDER_ID = "720391900040";
	
	public static String MAIN_PREF = "Vegitable";
	public static String LINK_PEF = "link_Pref";
	public static String CART_PREF = "VegitableCart";
	public static String PREFS_MAIN_CAT = "prefs_main_category";
	
	public static HashMap<String, String> selected_service; 
	public static HashMap<String, String> selected_job;
	public static HashMap<String, String> selected_city;
	
	public static JSONObject selected_clinic;

	public static final String IMAGE_DIRECTORY_NAME = "Vegitable";
	
	public static String SIGNUP_SERVICE =SITE_URL+"/index.php?component=json&action=register_gcm";

	public static String SATISFACTION=SITE_URL+"/index.php?component=json&action=add_satisfaction";

	public static String CHECK_PAYMENT_STATUS=SITE_URL+"/index.php?component=json&action=check_payment_status";

	public static String CLEAR_CART = SITE_URL+"/index.php?component=json&action=clear_user_cart";

	//0 pending//1 confirmed//2
}
