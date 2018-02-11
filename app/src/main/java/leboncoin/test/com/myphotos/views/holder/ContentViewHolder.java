package leboncoin.test.com.myphotos.views.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import leboncoin.test.com.myphotos.R;

/**
 * Created by Muthu on 09/02/2018.
 */

public class ContentViewHolder  extends RecyclerView.ViewHolder {
    public TextView txtDescription, txtId;
    public CircleImageView imgIcone;
    public ImageView imgRightArrow;
    public RelativeLayout item;
    public ContentViewHolder(View itemView) {
        super(itemView);
        txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
        txtId = (TextView) itemView.findViewById(R.id.txtId);
        imgIcone = (CircleImageView) itemView.findViewById(R.id.imgIcon);
        imgRightArrow = (ImageView) itemView.findViewById(R.id.imgRightArrow);
        item = (RelativeLayout) itemView.findViewById(R.id.item);
    }
}
