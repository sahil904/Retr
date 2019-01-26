package adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.medi.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<HashMap<String, String>> postItems;
	int count = 0;
	
	public MenuAdapter(Context context, ArrayList<HashMap<String, String>> arraylist){
		this.context = context;
		postItems = arraylist;
		
	}

	@Override
	public int getCount() {
		return postItems.size();
	}
	@Override
	public HashMap<String, String> getItem(int position) {		
		return postItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
			if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater)
	            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.drawer_list_row, null);	
	        }
			
			HashMap<String, String> map = new HashMap<String, String>();
			map = getItem(position);
			
			TextView txtTitle = (TextView)convertView.findViewById(R.id.textView1);
			txtTitle.setText(map.get("title"));
			
			TextView txtCount = (TextView)convertView.findViewById(R.id.textView2);
			txtCount.setText(map.get("desc"));
		    ImageView imageView = convertView.findViewById(R.id.icon_menu);
		    imageView.setImageResource(Integer.parseInt(map.get("menu_icon")));
		    Log.d("Image_icon",String.valueOf(map.get("menu_icon")));

		  //  int imageIcon = map.get("menu_icon");
		   // imageView.setImageResource();
			/*Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Walkway_UltraBold.ttf");
			txtTitle.setTypeface(tf);*/
			
			//ImageLoader.getInstance().displayImage(map.get("selfie_image"), imgIcon, options, animateFirstListener);
	    	
			
        return convertView;
	}

	

}

