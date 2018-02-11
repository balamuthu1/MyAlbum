package leboncoin.test.com.myphotos.views;

import android.database.sqlite.SQLiteException;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import leboncoin.test.com.myphotos.R;
import leboncoin.test.com.myphotos.adapters.MyPhotoAdapter;
import leboncoin.test.com.myphotos.api.IMyPhotoRetrofitService;
import leboncoin.test.com.myphotos.api.MyPhotoRetrofitClient;
import leboncoin.test.com.myphotos.callbacks.MyPhotoAdapterCallback;
import leboncoin.test.com.myphotos.callbacks.MyPhotoScrollListener;
import leboncoin.test.com.myphotos.models.Album;
import leboncoin.test.com.myphotos.models.LocalAlbum;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MyPhotoAdapterCallback{
    MyPhotoAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Parcelable layoutManagerSavedState;

    private String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;

    private IMyPhotoRetrofitService iMyPhotoRetrofitService;
    private List<LocalAlbum> localAlbumList;

    @BindView(R.id.main_recycler)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new MyPhotoAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);
        //custom scroll listner to load data bloc by bloc
        recyclerView.addOnScrollListener(new MyPhotoScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                //if scrolled the load next page
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        //init service and load data
        iMyPhotoRetrofitService = MyPhotoRetrofitClient.getClient().create(IMyPhotoRetrofitService.class);

        try {
            //load data from cache
            localAlbumList = LocalAlbum.listAll(LocalAlbum.class);
            adapter.addAll(localAlbumList);
            //restore the scroll position if we com from other activity
            restoreLayoutManagerPosition();

            //get current page index after restoring from cache
            if(null != localAlbumList && localAlbumList.size()>0){
               currentPage = localAlbumList.get(localAlbumList.size()-1).getAlbumId();
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
            }

        } catch (SQLiteException e) {
            //here we catch exception when database is not created or table is not created
            e.printStackTrace();

        }

        if(null == localAlbumList || localAlbumList.size() ==0){
            //if no cach --> opening for first time, so load first page
            loadFirstPage();
        }
    }
    private void loadFirstPage() {
        iMyPhotoRetrofitService.getAlbums(currentPage).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                adapter.addAll(convertToLocalAlbumsList(response.body()));
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                //show first error layout
            }
        });

    }

    //Method to convert Server object to local database object to cache
    private List<LocalAlbum> convertToLocalAlbumsList(List<Album> albumList){
        List<LocalAlbum> list = new ArrayList<>();
        for (Album album : albumList) {
            LocalAlbum localAlbum = new LocalAlbum(album.getAlbumId(),album.getId(),album.getTitle(),album.getUrl(),album.getThumbnailUrl());
            list.add(localAlbum);
            localAlbum.save();
        }
        return list;
    }
    private void loadNextPage() {
        Log.d("Mainactivity", "loadNextPage: " + currentPage);

        iMyPhotoRetrofitService.getAlbums(currentPage).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Album> results = response.body();
                adapter.addAll(convertToLocalAlbumsList(results));

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true,getString(R.string.error_msg));
            }
        });
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }


    /********************************************************************
     *
     * @param outState
     * This block is to store and restore the state of recyclerview
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState instanceof Bundle) {
            layoutManagerSavedState = ((Bundle) savedInstanceState).getParcelable(SAVED_LAYOUT_MANAGER);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    /********************************************************************/


    //While back is pressed if you are not at the top of the list, then scroll to top, else quit the app
    @Override
    public void onBackPressed() {
       if(linearLayoutManager.findFirstVisibleItemPosition() == 0){
           super.onBackPressed();
       }else{
           recyclerView.smoothScrollToPosition(0);
       }

    }
}
