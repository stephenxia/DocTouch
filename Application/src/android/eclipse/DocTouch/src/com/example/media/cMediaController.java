package com.example.media;

import android.app.AlertDialog;  
import android.app.AlertDialog.Builder;  
import android.content.Context;  
import android.content.DialogInterface;  
import android.view.Gravity;  
import android.view.MotionEvent;
import android.view.View;  
import android.widget.FrameLayout;  
import android.widget.ImageButton;  
import android.widget.MediaController;  
import android.widget.Toast;  
   
 public class cMediaController extends MediaController {  
   
    ImageButton mCCBtn;  
    Context mContext;  
    AlertDialog mLangDialog;  
   
    public cMediaController(Context context) {  
        super(context);  
        mContext = context;  
    }
   
    @Override  
    public void setAnchorView(View view) {  
        super.setAnchorView(view);  
   
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        frameParams.gravity = Gravity.RIGHT|Gravity.TOP;  
   
        View v = makeCCView();  
        addView(v, frameParams);  
   
    }  
   
    private View makeCCView() {  
        mCCBtn = new ImageButton(mContext);  
        //mCCBtn.setImageResource(R.drawable.cc);  
   
        mCCBtn.setOnClickListener(new OnClickListener() {  
   
   
            public void onClick(View v) {  
                Builder builder = new AlertDialog.Builder(mContext);
                CharSequence[] choices = {"Red", "Blue", "Yellow"};
                builder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener() {  
   
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        //Save Preference and Dismiss the Dialog here  
                        Toast.makeText(mContext, "Which ::: "+which, Toast.LENGTH_LONG).show();  
                    }                      
   
                });  
                mLangDialog = builder.create();  
                mLangDialog.show();
                
            }  
        });  
   
        return mCCBtn;  
    }  
   
}  