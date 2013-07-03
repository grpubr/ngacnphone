package gov.pianzong.holocnbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import gov.pianzong.adapter.NewsListAdapter;
import gov.pianzong.bean.NewsInfo;
import gov.pianzong.holocnbeta.R;
import gov.pianzong.interfaces.NewsClickedListener;
import gov.pianzong.task.NewsListLoadTask;
import gov.pianzong.util.AppConstants;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class MainActivity extends Activity implements NewsClickedListener {

    PullToRefreshAttacher helper;
    boolean dualScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        helper = new PullToRefreshAttacher(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(NewsInfo info) {
        if(dualScreen){
            setDetailFragment(info);
        }
        else
        {
            openNewActivity(info);
        }
    }

    @Override
    public void registRefreshableView(ListView lv) {
        final ListView listView = lv;
        helper.setRefreshableView(lv,new PullToRefreshAttacher.OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                new NewsListLoadTask((NewsListAdapter) listView.getAdapter(),MainActivity.this).executeOnExecutor(NewsListLoadTask.THREAD_POOL_EXECUTOR,0);

            }
        });
    }

    @Override
    public void startLoad() {
        if(!helper.isRefreshing())
            helper.setRefreshing(true);
    }

    @Override
    public void loadFinish() {
        helper.setRefreshComplete();
    }


    private void openNewActivity(NewsInfo info) {
        Intent intent = new Intent();
        intent.setClass(this, gov.pianzong.holocnbeta.NewsContentActivity.class);
        intent.putExtra(AppConstants.ARTICLE_ID,info.getArticleID());
        intent.putExtra(AppConstants.COMMENT_COUNT,info.getCmtnum());
        this.startActivity(intent);

    }
    private void setDetailFragment(NewsInfo info) {
    }


}
