package sp.phone.activity;

import java.util.List;
import com.alibaba.fastjson.JSON;

import sp.phone.bean.Bookmark;
import sp.phone.forumoperation.FloorOpener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

public class BookmarkActivity extends Activity 
	implements OnItemClickListener {
	List<Bookmark> bookmarks;//reference
	ListView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 //getLayoutInflater().inflate(R.layout.bookmarks, null);
		//view.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
		ThemeManager.SetContextTheme(this);
		this.setContentView(R.layout.bookmarks);
		 view = (ListView)findViewById(R.id.bookmark_listview);
		bookmarks = 
				PhoneConfiguration.getInstance().getBookmarks();

		
        view.setAdapter(new BookmarkAdapter());
        view.setOnItemClickListener(this);
        //this.getListView();
        this.registerForContextMenu(view);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int flags = 15;
		/*ActionBar.DISPLAY_SHOW_HOME;
		flags |= ActionBar.DISPLAY_USE_LOGO;
		flags |= ActionBar.DISPLAY_SHOW_TITLE;
		flags |= ActionBar.DISPLAY_HOME_AS_UP;
		flags |= ActionBar.DISPLAY_SHOW_CUSTOM;*/
		//final ActionBar bar = getActionBar();
		//bar.setDisplayOptions(flags);
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			default:
			//case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		menu.add(0,0,0,"ɾ��");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch(item.getItemId())
		{
			case 0:

				SharedPreferences  share = 
						getSharedPreferences("perference", MODE_PRIVATE);

					Editor editor = share.edit();
					bookmarks.remove(info.position);
					String jsonString = "";
					if(bookmarks.size()>0)
						jsonString =JSON.toJSONString(bookmarks.getClass());
					editor.putString("bookmarks",jsonString  );
					editor.commit();
					
					BookmarkAdapter ad = (BookmarkAdapter) view.getAdapter();
					ad.notifyDataSetChanged();
				break;
			default:
		}
		return super.onContextItemSelected(item);
	}
	
	private class BookmarkAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bookmarks.size();
		}

		@Override
		public Object getItem(int position) {
			
			return bookmarks.get(position).getTitle();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView ==null){
				convertView = new TextView(BookmarkActivity.this);
			}
			
			((TextView) convertView).setText(
					bookmarks.get(position).getTitle());
			Drawable draw = getResources().getDrawable(android.R.drawable.btn_star_big_on);
			((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(draw,null,null,null);
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		new FloorOpener(this).handleFloor(bookmarks.get(position).getUrl());
		
	}
	
	
	

}
