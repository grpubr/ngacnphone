package sp.phone.adapter;

import gov.pianzong.androidnga.R;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TopicListAdapter extends BaseAdapter
	implements OnTopListLoadFinishedListener{

	private LayoutInflater inflater;
	private TopicListInfo topicListInfo=null;

	public TopicListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	public Object getItem(int arg0) {
		if(topicListInfo!=null){
			ThreadPageInfo entry = topicListInfo.getArticleEntryList().get(arg0);
			if( entry== null ||entry.getTid()==0)
			{
				return null;
			}
			
			String ret = "tid="+entry.getTid();
			if(entry.getPid() != 0){
				return ret + "&pid=" + entry.getPid();
			}
			
			return ret;
		}
		
		return null;
	}

	public int getCount() {
		if(topicListInfo!=null)
			return topicListInfo.get__T__ROWS();
		
		return 0;
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
			convertView = inflater.inflate(R.layout.relative_topic_list, null);
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


			handleJsonList( holder,position);
			return convertView;

		
		
		
	}
	
	private void handleJsonList(ViewHolder holder, int position){
		ThreadPageInfo entry = this.topicListInfo.getArticleEntryList().get(position);
		if(entry == null){
			return;
		}
		Resources  res = inflater.getContext().getResources();
		ThemeManager theme = ThemeManager.getInstance();
		boolean night = false;
		int nightLinkColor = res.getColor(R.color.night_link_color);
		if(theme.getMode() == ThemeManager.MODE_NIGHT )
			night  = true;
		holder.author.setText("楼主:" + entry.getAuthor());
		if(night)
			holder.author.setTextColor(nightLinkColor);
			
		String lastPoster = entry.getLastposter_org();
		if(StringUtil.isEmpty(lastPoster))
			lastPoster = entry.getLastposter();
		holder.lastReply.setText("最后回复:" + lastPoster);
		holder.num.setText("" + entry.getReplies());
		if(night)
		{
			holder.lastReply.setTextColor(nightLinkColor);
			holder.num.setTextColor(nightLinkColor);
		}
		
		
		holder.title.setTextColor(res.getColor(
				theme.getForegroundColor()));
		float size = PhoneConfiguration.getInstance().getTextSize();
		
		String titile = entry.getContent();
		if(StringUtil.isEmpty(titile ))
		{
				titile = entry.getSubject();
				holder.title.setText(StringUtil.unEscapeHtml(titile));
				
		}else
		{
			holder.title.setText(
					StringUtil.removeBrTag(
							StringUtil.unEscapeHtml(titile)));
		}
		
		
		
		holder.title.setTextSize(size);
		final TextPaint tp = holder.title.getPaint();
		tp.setFakeBoldText(false);
		
		if(!StringUtil.isEmpty(entry.getTitlefont()) )
		{
			final String font = entry.getTitlefont();
			if(font.equals("~1~~") || font.equals("~~~1")){
				tp.setFakeBoldText(true);
			}else if(font.startsWith("green")){
				holder.title.setTextColor(res.getColor(R.color.green));
			}else if(font.startsWith("blue")){
				holder.title.setTextColor(res.getColor(R.color.blue));
			}else if(font.startsWith("red"))
			{
				holder.title.setTextColor(res.getColor(R.color.red));
			}
		}
		
	}



	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		this.topicListInfo = result;
		this.notifyDataSetChanged();
		
	}


}
