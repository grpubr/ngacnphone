package sp.phone.bean;

import sp.phone.activity.R;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {

	private View baseView;

	public ViewCache(View baseView) {
		this.baseView = baseView;
	}

	public ImageView getAvatarImage() {
		if (avatarImage == null) {
			avatarImage = (ImageView) baseView.findViewById(R.id.avatarImage);
		}
		return avatarImage;
	}

	public TextView getNickName() {

		if (nickName == null) {
			nickName = (TextView) baseView.findViewById(R.id.nickName);
		}

		return nickName;
	}
//
//	public TextView getPostNum() {
//		if (postNum == null) {
//			postNum = (TextView) baseView.findViewById(R.id.postNum);
//		}
//		return postNum;
//	}

	public TextView getTitle() {
		if (title == null) {
			title = (TextView) baseView.findViewById(R.id.title);
		}
		return title;
	}

	public WebView getContent() {
		if (content == null) {
			content = (WebView) baseView.findViewById(R.id.content);
		}
		return content;
	}

	public TextView getFloor() {
		if (floor == null) {
			floor = (TextView) baseView.findViewById(R.id.floor);
		}
		return floor;
	}

	public TextView getPostTime() {
		if (postTime == null) {
			postTime = (TextView) baseView.findViewById(R.id.postTime);
		}
		return postTime;
	}

	private ImageView avatarImage;
	private TextView nickName;
//	private TextView postNum;
	private TextView title;
	private WebView content;
	private TextView floor;
	private TextView postTime;

}
