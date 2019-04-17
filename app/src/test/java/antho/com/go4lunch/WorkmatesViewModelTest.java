package antho.com.go4lunch;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jraska.livedata.TestLifecycle;
import com.jraska.livedata.TestObserver;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import antho.com.go4lunch.model.workmate.Workmate;

import antho.com.go4lunch.viewmodel.WorkmateViewModel;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkmatesViewModelTest
{
    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }
    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();
    private WorkmateViewModel viewModel;
    // Set up dummy viewmodel to perform tests
    @Before
    public void setUp() {
        viewModel = new WorkmateViewModel();
    }
    //
    @Test
    public void directAssertion()
    {
        LiveData<List<Workmate>> liveData = viewModel.getWorkmates();
        TestObserver.test(liveData)
                .assertNoValue();
    }
    // Test live data set value use case for viewmodel
    @Test
    public void setValueTest()
    {
        MutableLiveData<List<Workmate>> liveData = viewModel.getMutableLiveData();
        TestObserver<List<Workmate>> testObserver = TestObserver.test(liveData);
        List<Workmate> dummyList = TestUtility.getTestingWorkmateListOfSize(3);
        liveData.setValue(dummyList);
        List<Workmate> value = testObserver.value();

        assertThat(value).isEqualTo(dummyList);
        liveData.removeObserver(testObserver);
        assertThat(liveData.hasObservers()).isFalse();
    }
    // Test live data history for viewmodel
    @Test
    public void counterHistoryTest()
    {
        MutableLiveData<List<Workmate>> liveData = viewModel.getMutableLiveData();
        TestObserver<List<Workmate>> testObserver = TestObserver.test(liveData);
        List<Workmate> dummyList = TestUtility.getTestingWorkmateListOfSize(3);
        liveData.setValue(dummyList);
        testObserver.assertHasValue()
                .assertHistorySize(1);
        for(int i = 0; i < 4; i++)
        {
            liveData.setValue(TestUtility.getTestingWorkmateListOfSize(30));
        }
        testObserver.
                assertHasValue()
                .assertHistorySize(5);
    }
    // Test live data history for viewmodel using a test observer with lifecycle
    @Test
    public void useObserverWithLifecycle()
    {
        TestObserver<List<Workmate>> testObserver = TestObserver.create();
        TestLifecycle testLifecycle = TestLifecycle.initialized();
        viewModel.getWorkmates().observe(testLifecycle, testObserver);
        testObserver.assertNoValue();
        testLifecycle.resume();
        for (int i = 0; i < 4; i++)
        {
            viewModel
                    .getMutableLiveData()
                    .setValue(TestUtility.getTestingWorkmateListOfSize(4));
        }
        List<Workmate> newsList = TestUtility.getTestingWorkmateListOfSize(10);
        viewModel.getMutableLiveData().setValue(newsList);
        testObserver.assertHasValue()
                .assertValue(newsList)
                .assertHistorySize(5);
        viewModel.getWorkmates().removeObserver(testObserver);
    }
}
