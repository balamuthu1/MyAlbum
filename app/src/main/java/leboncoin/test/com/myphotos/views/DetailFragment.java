package leboncoin.test.com.myphotos.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import leboncoin.test.com.myphotos.R;

/**
 * Created by Muthu on 11/02/2018.
 */

public class DetailFragment extends DialogFragment {
    private String imgUrl, description;
    @BindView(R.id.imgIcon)
    ImageView imgIcon;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    public DetailFragment(){}

    public static DetailFragment newInstance(String imgUrl, String description) {

        DetailFragment frag = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("url", imgUrl);
        args.putString("description", description);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_layout, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgUrl = getArguments().getString("url", "");
        description = getArguments().getString("description", "");
        txtDescription.setText(description);
        Glide.with(getActivity())
                .load(imgUrl)
                .asBitmap()
                .placeholder(imgIcon.getDrawable())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgIcon);
    }
}
