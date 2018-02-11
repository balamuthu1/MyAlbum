package leboncoin.test.com.myphotos.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import leboncoin.test.com.myphotos.R;
import leboncoin.test.com.myphotos.callbacks.MyPhotoAdapterCallback;

/**
 * Created by Muthu on 09/02/2018.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ProgressBar progressBar;
    public Button retryButton;
    public TextView errorText;
    public LinearLayout errorLayout;
    public MyPhotoAdapterCallback mCallback;
    public FooterViewHolder(View itemView, MyPhotoAdapterCallback mCallback) {
        super(itemView);
        //progressBar = (ProgressBar) itemView.findViewById(R.id.);
        retryButton = (Button) itemView.findViewById(R.id.error_btn_retry);
        errorText = (TextView) itemView.findViewById(R.id.error_txt_cause);
        errorLayout = (LinearLayout) itemView.findViewById(R.id.error_layout);
        this.mCallback = mCallback;
        retryButton.setOnClickListener(this);
        errorLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.error_btn_retry:
            case R.id.error_layout:

                //Send Event
                //
                mCallback.retryPageLoad();

                break;
        }
    }
}
