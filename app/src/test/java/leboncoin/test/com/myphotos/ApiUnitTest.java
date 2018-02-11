package leboncoin.test.com.myphotos;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import leboncoin.test.com.myphotos.api.ApiUtils;
import leboncoin.test.com.myphotos.api.IMyPhotoRetrofitService;
import leboncoin.test.com.myphotos.models.Album;
import leboncoin.test.com.myphotos.models.LocalAlbum;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.Assert.assertEquals;
/**
 * Created by Muthu on 11/02/2018.
 */


//test if we get 50 objects for each album id
public class ApiUnitTest {

    //test with mock server --> Good practice
    @Test
    public void testMockserver() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                //TODO Add your Retrofit parameters here
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LocalAlbum album = new LocalAlbum(1,1, "test", "url", "thumb");
        List<LocalAlbum> list = new ArrayList<>();
        list.add(album);
        //Set a response for retrofit to handle. You can copy a sample
        //response from your server to simulate a correct result or an error.
        //MockResponse can also be customized with different parameters
        //to match your test needs
        String json = new Gson().toJson(list);
        mockWebServer.enqueue(new MockResponse().setBody(json));

        IMyPhotoRetrofitService service = retrofit.create(IMyPhotoRetrofitService.class);

        //With your service created you can now call its method that should
        //consume the MockResponse above. You can then use the desired
        //assertion to check if the result is as expected. For example:
        Call<List<Album>> call = service.getAlbums(1);
        assertEquals(1,((List<Album>)call.execute().body()).size());

        //Finish web server
        mockWebServer.shutdown();
    }

    //test with real server
    @Test
    public void testRealserver() throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                //TODO Add your Retrofit parameters here
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IMyPhotoRetrofitService service = retrofit.create(IMyPhotoRetrofitService.class);

        //With your service created you can now call its method that should
        //consume the MockResponse above. You can then use the desired
        //assertion to check if the result is as expected. For example:
        Call<List<Album>> call = service.getAlbums(1);
        assertEquals(50,((List<Album>)call.execute().body()).size());

        //Finish web server
        mockWebServer.shutdown();
    }
}
