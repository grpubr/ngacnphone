package sp.phone.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import sp.phone.bean.NearbyUser;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NearbyUsersAdapter extends BaseAdapter {
	private final List<NearbyUser> list;
	
	

	public NearbyUsersAdapter(List<NearbyUser> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		if(list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list == null)
			return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView ret = null;
		if(convertView == null){
			ret = new TextView(parent.getContext());
		}else{
			ret = (TextView) convertView;
		}
		
		String text = list.get(position).getNickName();
		try {
			text = URLDecoder.decode(text,"utf-8");
		} catch (UnsupportedEncodingException e) {

		}
		ret.setTextSize( 50.0f);
		ret.setText(text);
		
		return ret;
	}

}
