package leboncoin.test.com.myphotos;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;

import leboncoin.test.com.myphotos.adapters.MyPhotoAdapter;
import leboncoin.test.com.myphotos.views.MainActivity;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Muthu on 11/02/2018.
 */

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> rule  = new  ActivityTestRule<>(MainActivity.class);
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("leboncoin.test.com.myphotos", appContext.getPackageName());
    }
    @Test
    public void ensureRViewIsPresent() throws Exception {
        MainActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.main_recycler);
        assertThat(viewById,notNullValue());
        assertThat(viewById, instanceOf(RecyclerView.class));
        RecyclerView rv = (RecyclerView) viewById;
        MyPhotoAdapter adapter = (MyPhotoAdapter) rv.getAdapter();
        assertThat(adapter, instanceOf(MyPhotoAdapter.class));
       // assertThat(adapter.getItemCount(), greaterThan(1));

    }
}
