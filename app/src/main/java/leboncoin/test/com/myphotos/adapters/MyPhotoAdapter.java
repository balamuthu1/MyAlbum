package leboncoin.test.com.myphotos.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;

import leboncoin.test.com.myphotos.R;
import leboncoin.test.com.myphotos.callbacks.MyPhotoAdapterCallback;
import leboncoin.test.com.myphotos.models.Album;
import leboncoin.test.com.myphotos.models.LocalAlbum;
import leboncoin.test.com.myphotos.views.holder.ContentViewHolder;
import leboncoin.test.com.myphotos.views.holder.FooterViewHolder;
import leboncoin.test.com.myphotos.views.holder.HeaderViewHolder;

/**
 * Created by Muthu on 09/02/2018.
 */

public class MyPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // View Types
    private static final int HEADER = 0;
    private static final int CONTENT = 1;
    private static final int FOOTER = 2;


    private List<LocalAlbum> albumList;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private MyPhotoAdapterCallback mCallback;

    private String errorMsg;
    private int lastPosition = -1;

    public MyPhotoAdapter(Context context){
        this.context = context;
        albumList = new ArrayList<>();
        this.mCallback = (MyPhotoAdapterCallback) context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case HEADER:
                //header content
                View viewHeader = inflater.inflate(R.layout.main_content_layout, parent, false);
                viewHolder = new ContentViewHolder(viewHeader);
                break;
            case CONTENT:
                //Main content
                View viewItem = inflater.inflate(R.layout.main_content_layout, parent, false);
                viewHolder = new ContentViewHolder(viewItem);
                break;
            case FOOTER:
                //footer content
                View viewFooter = inflater.inflate(R.layout.footer_content_layout, parent, false);
                viewHolder = new FooterViewHolder(viewFooter, mCallback);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case HEADER:
                //Bind the header view data
                LocalAlbum album = albumList.get(position);
                final ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.txtId.setText("" + album.getPhotoId());

                contentViewHolder.txtDescription.setText(album.getTitle());
                //use glide to load images
                contentViewHolder.imgIcone.setImageResource(R.mipmap.ic_launcher_round);
                Glide.with(context)
                        .load(album.getThumbnailUrl())
                        .asBitmap()
                        .placeholder(contentViewHolder.imgIcone.getDrawable())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(contentViewHolder.imgIcone);


                break;
            case CONTENT:
                //Bind main content view with data
                LocalAlbum album2 = albumList.get(position);
                final ContentViewHolder contentViewHolder2 = (ContentViewHolder) holder;
                contentViewHolder2.txtId.setText("" + album2.getPhotoId());
                contentViewHolder2.txtDescription.setText(album2.getTitle());

                //use glide to load images
                Glide.with(context)
                        .load(album2.getThumbnailUrl())
                        .asBitmap()
                        .placeholder(contentViewHolder2.imgIcone.getDrawable())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(contentViewHolder2.imgIcone);


                break;

            case FOOTER:
                //Bind footer view
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;

                if (retryPageLoad) {
                    footerViewHolder.errorLayout.setVisibility(View.VISIBLE);
                    footerViewHolder.errorText.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    footerViewHolder.errorLayout.setVisibility(View.GONE);
                }
                break;
        }
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            final ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            contentViewHolder.imgIcone.setImageResource(R.mipmap.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return (position == albumList.size() - 1 && isLoadingAdded) ? FOOTER : CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    public List<LocalAlbum> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<LocalAlbum> albumList) {
        this.albumList = albumList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isLoadingAdded() {
        return isLoadingAdded;
    }

    public void setLoadingAdded(boolean loadingAdded) {
        isLoadingAdded = loadingAdded;
    }

    public boolean isRetryPageLoad() {
        return retryPageLoad;
    }

    public void setRetryPageLoad(boolean retryPageLoad) {
        this.retryPageLoad = retryPageLoad;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public void add(LocalAlbum album) {
        albumList.add(album);
        notifyItemInserted(albumList.size() - 1);
    }

    public void addAll(List<LocalAlbum> albumList) {
        for (LocalAlbum album : albumList) {
            add(album);

        }
    }

    public void remove(LocalAlbum album) {
        int position = albumList.indexOf(album);
        if (position > -1) {
            albumList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;

    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;


    }

    public LocalAlbum getItem(int position) {
        return albumList.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(albumList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

}
