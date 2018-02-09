package leboncoin.test.com.myphotos.api;

import leboncoin.test.com.myphotos.models.Album;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Muthu on 09/02/2018.
 */

public interface IMyPhotoRetrofitService {

    @GET("photos")
    Call<Album> getAlbums(@Query("albumId") int pageIndex);

}
