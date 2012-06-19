package sp.phone.fragment;

import gov.pianzong.androidnga.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

public class PostCommentDialogFragment extends DialogFragment {
	EditText input=null;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());  
        input = new EditText(getActivity());
        alert.setView(input);  
		alert.setTitle(R.string.post_comment);
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) { 
            	Toast.makeText(getActivity(), "¿ª·¢ÖÐ", Toast.LENGTH_SHORT).show();
            }
		});
		
		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                dialog.dismiss();
            }
		});
		return alert.create();
	}
	

}
