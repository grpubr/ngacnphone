package sp.phone.fragment;

import sp.phone.interfaces.EmotionCategorySelectedListener;
import gov.pianzong.androidnga.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class EmotionCategorySelectFragment extends NoframeDialogFragment {



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.emotion_category, container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		OnClickListener listener = new OnClickListener(){

			@Override
			public void onClick(View v) {

				EmotionCategorySelectedListener father = null;
				try{
					father = (EmotionCategorySelectedListener)getActivity();
				}catch(ClassCastException e){
					Log.e(EmotionCategorySelectFragment.class.getSimpleName(), e.getMessage());
				}
				
				if(father != null)
				{
					switch(v.getId())
					{
						case R.id.emotion_basic:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BASIC);
							break;
						case R.id.emotion_baozou:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BAOZOU);
							break;
							
						case R.id.emotion_ali:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ALI);
							break;
						case R.id.emotion_dayanmao:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_DAYANMAO);
							break;
						case R.id.emotion_luoxiaohei:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_LUOXIAOHEI);
							break;
						case R.id.emotion_zhaiyin:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ZHAIYIN);
							break;
						case R.id.emotion_yangcongtou:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_YANGCONGTOU);
							break;
						case R.id.emotion_acniang:
							father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_ACNIANG);
							break;
						case R.id.emotion_bierde:
								father.onEmotionCategorySelected(EmotionCategorySelectedListener.CATEGORY_BIERDE);
								break;
						
						default:
					}		
						
					
				}
				
			}
			
		};
		
		view.findViewById(R.id.emotion_basic).setOnClickListener(listener);
		view.findViewById(R.id.emotion_baozou).setOnClickListener(listener);
		view.findViewById(R.id.emotion_ali).setOnClickListener(listener);
		view.findViewById(R.id.emotion_dayanmao).setOnClickListener(listener);
		view.findViewById(R.id.emotion_luoxiaohei).setOnClickListener(listener);
		view.findViewById(R.id.emotion_zhaiyin).setOnClickListener(listener);
		view.findViewById(R.id.emotion_yangcongtou).setOnClickListener(listener);
		view.findViewById(R.id.emotion_acniang).setOnClickListener(listener);
		view.findViewById(R.id.emotion_bierde).setOnClickListener(listener);
		
		super.onViewCreated(view, savedInstanceState);
	}

}
