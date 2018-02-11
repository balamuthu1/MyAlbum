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
    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private String SAVED_LAYOUT_MANAGER = "SAVED_LAYOUT_MANAGER";
    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;

    private IMyPhotoRetrofitService iMyPhotoRetrofitService;
    private List<LocalAlbum> localAlbumList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = (RecyclerView) findViewById(R.id.main_recycler);
        adapter = new MyPhotoAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);
        rv.addOnScrollListener(new MyPhotoScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

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
            localAlbumList = LocalAlbum.listAll(LocalAlbum.class);
            adapter.addAll(localAlbumList);
            restoreLayoutManagerPosition();
            if(null != localAlbumList && localAlbumList.size()>0){
               currentPage = localAlbumList.get(localAlbumList.size()-1).getAlbumId();
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
            }

        } catch (SQLiteException e) {
            e.printStackTrace();

        }

        if(null == localAlbumList || localAlbumList.size() ==0){
            loadFirstPage();
        }
    }
    private void loadFirstPage() {
        // To ensure list is visible when retry button in error view is clicked
        //hideErrorView();
        iMyPhotoRetrofitService.getAlbums(currentPage).enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                // To ensure list is visible when retry button in error view is clicked
                //hideErrorView();



                adapter.addAll(convertToLocalAlbumsList(response.body()));
                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                //showErrorView(t);
            }
        });

    }

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
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.GONE);

            txtError.setText(getString(R.string.error_msg));
        }
    }
    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            //progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, rv.getLayoutManager().onSaveInstanceState());
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
            rv.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    @Override
    public void onBackPressed() {
       if(linearLayoutManager.findFirstVisibleItemPosition() == 0){
           super.onBackPressed();
       }else{
           rv.smoothScrollToPosition(0);
       }



    }
}
