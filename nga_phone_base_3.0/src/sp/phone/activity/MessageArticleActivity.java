package sp.phone.activity;



import java.util.zip.ZipFile;

import sp.phone.bean.Article;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.OnThreadLoadCompleteLinstener;
import sp.phone.forumoperation.FloorOpener;
import sp.phone.task.ArticleLoadTask;
import sp.phone.task.AvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MessageArticleActivity extends MyAbstractActivity 
	implements OnThreadLoadCompleteLinstener{

	static final int CONTEXT_MENU_ITEM_REPLY = 0;
	static final int CONTEXT_MENU_ITEM_WHOLE_THREAD = 1;
	private ListView listView;
	String tid;
	String pid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = (ListView) getLayoutView().findViewById(R.id.floor_list);
		Intent intent = getIntent();
		tid = intent.getStringExtra("tid");
		pid = intent.getStringExtra("pid");
		String page = intent.getStringExtra("page");
		
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(R.layout.message_article);
        this.asyncLoadThread(tid, pid, page);
        //OnItemLongClickListener listener = new FloorClickListener();
       // listView.setOnItemLongClickListener(listener);
        this.registerForContextMenu(listView);
        listView.setAdapter(new ThreadPageAdapter(this));
        ActivityUtil.getInstance().noticeSaying(this);
}


	@Override
	protected int getLayoutId() {
		
		return R.layout.message_article;
	}

	@Override
	protected int getOptionMenuId() {
		//no option menu
		return 0;
	}
	
	private void asyncLoadThread(String tid, String pid, String page){
		StringBuffer remotePath = new StringBuffer();
		if(HttpUtil.HOST != null && !HttpUtil.HOST.equals("")){
			remotePath.append(HttpUtil.HOST);
		}else{
			remotePath.append(HttpUtil.Server);
		}
		remotePath.append("/read.php?");
		
		if(tid != null){
			remotePath.append("tid=");
			remotePath.append(tid);
			remotePath.append('&');
		}
		
		if(pid != null){
			remotePath.append("pid=");
			remotePath.append(pid);
			remotePath.append('&');
		}
		
		if(page != null){
			remotePath.append("page=");
			remotePath.append(page);
			remotePath.append('&');
		}
		
		new ArticleLoadTask(this,this).execute(remotePath.toString());
		//ActivityUtil.getInstance().noticeSaying(this);
	}
	
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, CONTEXT_MENU_ITEM_REPLY, 0, "喷回去");
		menu.add(0, CONTEXT_MENU_ITEM_WHOLE_THREAD, 0, "查看整个帖子");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/*private String getPid(String url) {
		int start = url.indexOf("pid=")+4;
		int end = url.indexOf("&");
		if(end == -1)
			end = url.length();
		return url.substring(start,end);
	}*/
	
	private String buildQuoteContent(String nickName, String postTime,String content){
		StringBuffer postPrefix = new StringBuffer();
		postPrefix.append("[quote][tid=");
		postPrefix.append(tid);
		postPrefix.append("]Topic[/pid] [b]Post by ");
		postPrefix.append(nickName);
		postPrefix.append(" (");
		postPrefix.append(postTime);
		postPrefix.append("):[/b]\n");
		postPrefix.append(content);
		postPrefix.append("[/quote]");

		postPrefix.append("\n[@");
		postPrefix.append(nickName);
		postPrefix.append("]\n");
		
		return postPrefix.toString();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Article article = (Article) listView.getItemAtPosition(info.position);
		String content = article.getContent();
		final String name = article.getUser().getNickName();
		String mention= name;
		final String postTime =article.getLastTime();
		
		switch(item.getItemId()){
		case CONTEXT_MENU_ITEM_REPLY :	
			final String postPrefix = buildQuoteContent(name, postTime, content);
			Intent ReplyIntent = new Intent();
			ReplyIntent.putExtra("mention", mention);
			ReplyIntent.putExtra("prefix", StringUtil.removeBrTag(postPrefix) );
			ReplyIntent.putExtra("tid", tid);
			ReplyIntent.putExtra("action", "reply");	
			ReplyIntent.setClass(this, PostActivity.class);
			startActivity(ReplyIntent);
			break;
		case CONTEXT_MENU_ITEM_WHOLE_THREAD :	
			String url = StringUtil.buildThreadURLByTid(tid);
			new FloorOpener(this).handleFloor(url);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sp.phone.bean.OnThreadLoadCompleteLinstener#NotifyLoadComplete(sp.phone.bean.ArticlePage)
	 */
	@Override
	public void NotifyLoadComplete(ArticlePage articlePage) {
		ActivityUtil.getInstance().dismiss();
		if(articlePage == null){
			Toast.makeText(this, R.string.thread_load_error, Toast.LENGTH_LONG);
		}else{
			
			ThreadPageAdapter adapter = (ThreadPageAdapter) listView.getAdapter();
			adapter.setArticlePage(articlePage);
			adapter.notifyDataSetChanged();
		}
		
	}

	class ThreadPageAdapter extends BaseAdapter{
		
		ArticlePage articlePage;
		final Activity activity;
	
		public ThreadPageAdapter(ArticlePage articlePage, Activity activity) {
			super();
			this.articlePage = articlePage;
			this.activity = activity;
		}
		

		public ThreadPageAdapter(Activity activity) {
			this(null, activity);
		}

		
		
		public void setArticlePage(ArticlePage articlePage) {
			this.articlePage = articlePage;
		}


		@Override
		public int getCount() {
			if(articlePage == null)
				return 0;
			else
			{
				return articlePage.getListArticle().size();
			}
		}

		@Override
		public Object getItem(int position) {
			
			return articlePage.getListArticle().get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		class FloorViewHolder{
			public ImageView avatarIV;
			public TextView nickNameTV ;
			public WebView contentTV ;
			public TextView floorTV;
			public TextView postTimeTV ;
			public TextView titleTV;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;
			

			if(convertView == null){
				rowView = LayoutInflater.from(activity)
						.inflate(R.layout.relative_aritclelist, null);
				int colorId = ThemeManager.getInstance().getBackgroundColor();
				rowView.setBackgroundResource(colorId);
				
				FloorViewHolder holder = new FloorViewHolder();
				holder.avatarIV = (ImageView) rowView.findViewById(R.id.avatarImage);
				holder.nickNameTV = (TextView) rowView.findViewById(R.id.nickName);
				holder.contentTV = (WebView) rowView.findViewById(R.id.content);
				holder.floorTV = (TextView) rowView.findViewById(R.id.floor);
				holder.postTimeTV = (TextView) rowView.findViewById(R.id.postTime);
				holder.titleTV = (TextView) rowView.findViewById(R.id.floor_title);
				
				
				rowView.setTag(holder);			
			}else{
				rowView = convertView;
			}
			
			FloorViewHolder holder = (FloorViewHolder) rowView.getTag();
			
			Article article = (Article) this.getItem(position);
			
			initAvatar(article, holder.avatarIV);
			
			int fgColorId = ThemeManager.getInstance().getForegroundColor();
			int fgColor = parent.getContext().getResources().getColor(fgColorId);
			initNickName(article,  holder.nickNameTV, fgColor);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.nickNameTV.getLayoutParams();
			params.width = PhoneConfiguration.getInstance().nikeWidth;//the basement in the layout
			
			
			ColorDrawable colorDrawable = (ColorDrawable) rowView.getBackground();
			int bgColor = colorDrawable.getColor();
			initContent(article, holder.contentTV, fgColor,bgColor );
			
			initFloor(article, holder.floorTV);
			initPostTime(article, holder.postTimeTV);
			initTitle(article,holder.titleTV, fgColor);
			return rowView;
		}
		
		private void initAvatar(Article article, ImageView avatarIV){
			MyApp app = (MyApp)activity.getApplication();
			ZipFile zf = app.getZf();
			
			final String avatarUrl = article.getUser().getAvatarImage();// 头像
			final String userId = "" + article.getUser().getUserId();
			if (!StringUtil.isEmpty(avatarUrl)) {
				final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
				final boolean downImg = isInWifi()||PhoneConfiguration.getInstance().isDownAvatarNoWifi();
				new AvatarLoadTask(avatarIV, zf, downImg).execute(avatarUrl, avatarPath, userId);
				
			}
		}
		
		private void initNickName(Article article, TextView nickNameTV, int fgColor ){
			nickNameTV.setText(article.getUser().getNickName());
			nickNameTV.setTextColor(fgColor);
			TextPaint tp = nickNameTV.getPaint();
            tp.setFakeBoldText(true);//bold for Chinese character
		
		}
		
		private void initContent(Article article, WebView contentTV, int fgColor,int bgColor ){
			
			contentTV.setBackgroundColor(0);
			contentTV.setFocusable(false);
			
			bgColor = bgColor & 0xffffff;
			String bgcolorStr = String.format("%06x",bgColor);
			
			int htmlfgColor = fgColor & 0xffffff;
			String fgColorStr = String.format("%06x",htmlfgColor);
			
			String ngaHtml = StringUtil.decodeForumTag(article.getContent());
			ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=UTF-8 \">" 
				+ "<body bgcolor= '#"+ bgcolorStr +"'>"
				+ "<font color='#"+ fgColorStr + "' size='2'>"
				+ ngaHtml + 
				"</font></div></body>";
				

			WebSettings setting = contentTV.getSettings();
			
			setting.setBlockNetworkImage(false);
			if(PhoneConfiguration.getInstance().isDownAvatarNoWifi() || isInWifi() )
				setting.setBlockNetworkImage(true);

			contentTV.loadDataWithBaseURL(null,ngaHtml, "text/html", "utf-8",null);
			//contentTV.setOnTouchListener(gestureListener);
			contentTV.getSettings().setDefaultFontSize(
					PhoneConfiguration.getInstance().getWebSize());
			
		}
		
		private void initFloor(Article article, TextView floorTV){
			final int floor = article.getFloor();
			floorTV.setText("[" + floor + " 楼]");
		}
		private void initPostTime(Article article, TextView postTimeTV){
			postTimeTV.setText(article.getLastTime());
		}
		
		private void initTitle(Article article, TextView titleTV,int fgColor){
			if (!StringUtil.isEmpty(article.getTitle())) {
				titleTV.setText(article.getTitle());
				titleTV.setTextColor(fgColor);
				TextPaint tp = titleTV.getPaint();
	            tp.setFakeBoldText(true);//bold for Chinese character
			} else {
				titleTV.setVisibility(View.GONE);
			}
		}
		
		private boolean isInWifi(){
			ConnectivityManager conMan = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			return wifi == State.CONNECTED;
		}
		
	}

	

}
