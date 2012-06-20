package sp.phone.adapter;

import gov.pianzong.androidnga.R;
import sp.phone.bean.RSSFeed;
import sp.phone.bean.RSSItem;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopicListAdapter extends BaseAdapter
	implements OnTopListLoadFinishedListener{

	private LayoutInflater inflater;
	private TopicListInfo topicListInfo=null;
	private RSSFeed rssFeed=null;

	public TopicListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	public Object getItem(int arg0) {
		if(topicListInfo!=null){
			return "tid="+topicListInfo.getArticleEntryList().get(arg0).getTid();
		}
		
		if(rssFeed==null){
			return null;
		}
		return rssFeed.getItems().get(arg0).getGuid();
	}

	public int getCount() {
		if(topicListInfo!=null)
			return topicListInfo.get__T__ROWS();
		
		if(rssFeed == null){
			return 0;
		}
		return rssFeed.getItems().size();
	}

	public long getItemId(int arg0) {
		return arg0;
	}
	long start;
	long end;
	
	class ViewHolder{
		public TextView num ;
		public TextView title ;
		public TextView author ;
		public TextView lastReply ;
		
	}
	public View getView(int position, View view, ViewGroup parent) {
		if(position ==0){
			start = System.currentTimeMillis();
		}
		
		View convertView = view;//m.get(position);
		ViewHolder holder = null;
		if (convertView == null ) {
			convertView = inflater.inflate(R.layout.linear_topic_list, null);
			TextView num = (TextView) convertView.findViewById(R.id.num);
			TextView title = (TextView) convertView
					.findViewById(R.id.title);
			TextView author = (TextView) convertView
					.findViewById(R.id.author);
			TextView lastReply = (TextView) convertView
					.findViewById(R.id.last_reply);
			holder = new ViewHolder();
			holder.num = num;
			holder.title = title;
			holder.author = author;
			holder.lastReply = lastReply;
			convertView.setTag(holder);

		}else{
			holder = (ViewHolder) convertView.getTag();
			
		}
		int colorId = ThemeManager.getInstance().getBackgroundColor();
		convertView.setBackgroundResource(colorId);

		if(topicListInfo != null){
			handleJsonList( holder,position);
			return convertView;
		}
		
		
		RSSItem item = rssFeed.getItems().get(position);

		String description = item.getDescription();
		String[] arr = description.split("\n");

		holder.author.setText("楼主:" + item.getAuthor());
		if (arr[1] != null) {
			int start = arr[1].indexOf('(') + 1;// 93个回复 于 (hgfan)
			int end = arr[1].indexOf(')');
			String lastReplyUser = arr[1].substring(start, end);
			holder.lastReply.setText("最后回复:" + lastReplyUser);
		}
		int last_index = arr.length - 1;
		String reply_count = "0";
		int count_in_desc = arr[last_index].indexOf("个");
		if (count_in_desc != -1)
			reply_count = arr[last_index].substring(0,
					arr[last_index].indexOf("个"));
		holder.num.setText("" + reply_count);
		// replies.setText("[" + reply_count + " RE]");
		try {
			holder.title.setTextColor(parent.getResources().getColor(
					ThemeManager.getInstance().getForegroundColor()));
			float size = PhoneConfiguration.getInstance().getTextSize();
			holder.title.setTextSize(size);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), Log.getStackTraceString(e));
		}
		holder.title.setText(arr[0]);

		// }

		if (position == this.getCount() - 1) {
			end = System.currentTimeMillis();
			Log.i(getClass().getSimpleName(), "render cost:" + (end - start));
		}
		return convertView;
	}
	
	private void handleJsonList(ViewHolder holder, int position){
		ThreadPageInfo entry = this.topicListInfo.getArticleEntryList().get(position);
		
		holder.author.setText("楼主:" + entry.getAuthor());
		holder.lastReply.setText("最后回复:" + entry.getLastposter());
		holder.num.setText("" + entry.getReplies());
		
		Resources  res = inflater.getContext().getResources();
		holder.title.setTextColor(res.getColor(
				ThemeManager.getInstance().getForegroundColor()));
		float size = PhoneConfiguration.getInstance().getTextSize();
		holder.title.setTextSize(size);
		holder.title.setText(entry.getSubject());

		if(!StringUtil.isEmpty(entry.getTitlefont()) )
		{
			final String font = entry.getTitlefont();
			if(font.equals("~1~~")){
				TextPaint tp = holder.title.getPaint();
	            tp.setFakeBoldText(true);
			}else if(font.startsWith("green")){
				holder.title.setTextColor(res.getColor(R.color.green));
			}else if(font.startsWith("blue")){
				holder.title.setTextColor(res.getColor(R.color.blue));
			}else
			{
				holder.title.setTextColor(res.getColor(R.color.red));
			}
		}
		
	}

	@Override
	public void finishLoad(RSSFeed feed) {
		this.rssFeed = feed;
		this.notifyDataSetChanged();
		
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		this.topicListInfo = result;
		this.notifyDataSetChanged();
		
	}


}
