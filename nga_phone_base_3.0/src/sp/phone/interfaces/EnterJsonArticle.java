package sp.phone.interfaces;

import gov.pianzong.androidnga.R;
import gov.pianzong.androidnga.activity.ArticleListActivity;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EnterJsonArticle implements OnItemClickListener {

	private final Activity activity;
	public EnterJsonArticle(Activity activity) {
		super();
		this.activity = activity;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String guid = (String) parent.getItemAtPosition(position);
		if(StringUtil.isEmpty(guid))
			return;
		
		guid = guid.trim();

		int pid = StringUtil.getUrlParameter(guid, "pid");
		int tid = StringUtil.getUrlParameter(guid, "tid");
		int authorid = StringUtil.getUrlParameter(guid, "authorid");
		
		Intent intent = new Intent();
		intent.putExtra("tab", "1");
		intent.putExtra("tid",tid );
		intent.putExtra("pid",pid );
		intent.putExtra("authorid",authorid );
		ListView listview = (ListView)parent;
		TopicListAdapter adapter = (TopicListAdapter)listview.getAdapter();
		adapter.setSelected(position);
		listview.setItemChecked(position, true);
		
		intent.setClass(activity, ArticleListActivity.class);
		activity.startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
			activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	
		
	}

}
