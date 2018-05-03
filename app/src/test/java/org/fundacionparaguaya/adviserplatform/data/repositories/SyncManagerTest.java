package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.test.suitebuilder.annotation.SmallTest;
import org.fundacionparaguaya.adviserplatform.data.remote.ConnectivityWatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.LAST_SYNC_ERROR_MARGIN;
import static org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager.SyncState.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * A test for the SyncManager.
 */

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class SyncManagerTest {
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    SharedPreferences.Editor sharedPreferencesEditor;
    @Mock
    FamilyRepository familyRepository;
    @Mock
    SurveyRepository surveyRepository;
    @Mock
    SnapshotRepository snapshotRepository;
    @Mock
    ConnectivityWatcher connectivityWatcher;
    @Mock
    Observer observer;
    @Mock
    ImageRepository mImageRepository;

    private MutableLiveData<Boolean> isOnline;

    private AtomicBoolean isAlive;

    @Rule
    public InstantTaskExecutorRule instantTask = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        isOnline = new MutableLiveData<>();
        isOnline.setValue(true);
        isAlive = new AtomicBoolean(true);


        when(connectivityWatcher.status()).thenReturn(isOnline);
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
    }

    @Test
    public void sync_ShouldSyncEachRepository() {
        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        verify(familyRepository, times(1)).sync(any(), any());
        verify(surveyRepository, times(1)).sync(any(), any());
        verify(snapshotRepository, times(1)).sync(any(), any());
        verify(mImageRepository, times(1)).sync(any(), any());
    }

    @Test
    public void sync_ShouldSyncEachRepository_error() {
        when(familyRepository.sync(any(), any())).thenThrow(new RuntimeException());

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        verify(familyRepository, times(1)).sync(any(), any());
        verify(surveyRepository, times(1)).sync(any(), any());
        verify(snapshotRepository, times(1)).sync(any(), any());
        verify(mImageRepository, times(1)).sync(any(), any());
    }

    @Test
    public void sync_ShouldDeliverSyncResult_success() {
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        assertThat(syncManager.sync(isAlive), is(true));
    }

    @Test
    public void sync_ShouldDeliverSyncResult_failure() {
        when(familyRepository.sync(any(), any())).thenReturn(false);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        assertThat(syncManager.sync(isAlive), is(false));
    }

    @Test
    public void sync_ShouldDeliverSyncResult_error() {
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenThrow(new RuntimeException());
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        assertThat(syncManager.sync(isAlive), is(false));
    }

    @Test
    public void sync_ShouldUpdateProgress_success() {
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(SYNCED));
    }

    @Test
    public void sync_ShouldUpdateProgress_failure() {
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(false);
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(ERROR_OTHER));
    }

    @Test
    public void sync_ShouldUpdateProgress_pending() {
        SyncManager syncManager = syncManager();
        syncManager.getProgress().observeForever(observer);
        syncManager.sync(isAlive);

        ArgumentCaptor<SyncManager.SyncProgress> syncProgressCaptor =
                ArgumentCaptor.forClass(SyncManager.SyncProgress.class);
        verify(observer, atLeastOnce()).onChanged(syncProgressCaptor.capture());
        assertThat(syncProgressCaptor.getAllValues().get(1).getSyncState(), is(SYNCING));
    }

    @Test
    public void sync_ShouldUseLastSyncTime() {
        long lastSyncedTime = 10000L;
        setLastSynced(lastSyncedTime);
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(false);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        Date date = new Date(lastSyncedTime - LAST_SYNC_ERROR_MARGIN);
        verify(familyRepository, times(1)).sync(isAlive, date);
        verify(surveyRepository, times(1)).sync(isAlive, date);
        verify(snapshotRepository, times(1)).sync(isAlive, date);
    }

    @Test
    public void sync_ShouldUseLastSyncTime_first() {
        setNeverSynced();
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(false);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        verify(familyRepository, times(1)).sync(isAlive, null);
        verify(surveyRepository, times(1)).sync(isAlive,null);
        verify(snapshotRepository, times(1)).sync(isAlive,null);
    }

    @Test
    public void progress_ShouldReflectConnectivityState_online() {
        setOnline();

        SyncManager syncManager = syncManager();

        assertThat(syncManager.getProgress().getValue().getSyncState(), not(ERROR_NO_INTERNET));
    }

    @Test
    public void progress_ShouldReflectConnectivityState_offline() {
        setOffline();

        SyncManager syncManager = syncManager();

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(ERROR_NO_INTERNET));
    }

    @Test
    public void progress_ShouldReflectConnectivityState_offlineSync() {
        setOffline();

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(ERROR_NO_INTERNET));
    }

    @Test
    public void progress_ShouldReflectConnectivityState_delayed() {
        setOnline();

        SyncManager syncManager = syncManager();
        setOffline();

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(ERROR_NO_INTERNET));
    }

    @Test
    public void progress_ShouldUpdateLastSyncTime_load() {
        long lastSyncedTime = 10000L;
        when(sharedPreferences.getLong(eq(SyncManager.KEY_LAST_SYNC_TIME), anyLong()))
                .thenReturn(lastSyncedTime);

        SyncManager syncManager = syncManager();

        assertThat(syncManager.getProgress().getValue().getLastSyncedTime(), is(lastSyncedTime));
    }

    @Test
    public void progress_ShouldUpdateLastSyncTime_save() {
        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(true);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        long lastSyncedTime = syncManager.getProgress().getValue().getLastSyncedTime();
        syncManager.sync(isAlive);

        long newSyncedTime = syncManager.getProgress().getValue().getLastSyncedTime();
        assertThat(newSyncedTime > lastSyncedTime, is(true));
        verify(sharedPreferencesEditor).putLong(SyncManager.KEY_LAST_SYNC_TIME, newSyncedTime);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void progress_ShouldUpdateLastSyncTime_failure() {
        long lastSyncedTime = 10000L;
        setLastSynced(lastSyncedTime);

        when(familyRepository.sync(any(), any())).thenReturn(true);
        when(surveyRepository.sync(any(), any())).thenReturn(true);
        when(snapshotRepository.sync(any(), any())).thenReturn(false);
        when(mImageRepository.sync(any(), any())).thenReturn(true);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAlive);

        verify(sharedPreferencesEditor, never()).putLong(
                eq(SyncManager.KEY_LAST_SYNC_TIME), anyLong());
        assertThat(syncManager.getProgress().getValue().getLastSyncedTime(), is(lastSyncedTime));
    }

    @Test
    public void progress_ShouldUpdateLastSyncTime_clean() {
        long lastSyncedTime = 10000L;
        setLastSynced(lastSyncedTime);

        SyncManager syncManager = syncManager();
        syncManager.clean();

        long newSyncedTime = syncManager.getProgress().getValue().getLastSyncedTime();
        assertThat(newSyncedTime, is(-1L));
        verify(sharedPreferencesEditor).remove(SyncManager.KEY_LAST_SYNC_TIME);
        verify(sharedPreferencesEditor).apply();
    }

    @Test
    public void clean_ShouldCleanEachRepository() {
        SyncManager syncManager = syncManager();
        syncManager.clean();

        verify(familyRepository).clean();
        verify(surveyRepository).clean();
        verify(snapshotRepository).clean();
        verify(mImageRepository).clean();
    }

    @Test
    public void clean_ShouldCleanEachRepository_offline() {
        isOnline.postValue(false);

        SyncManager syncManager = syncManager();
        syncManager.clean();

        verify(familyRepository).clean();
        verify(surveyRepository).clean();
        verify(snapshotRepository).clean();
        verify(mImageRepository).clean();
    }

    @Test
    public void abort_ShouldAbortEarly() {
        AtomicBoolean isAliveFalse = new AtomicBoolean(false);

        SyncManager syncManager = syncManager();
        syncManager.sync(isAliveFalse);

        verify(familyRepository, times(0)).sync(any(), any());
        verify(surveyRepository, times(0)).sync(any(), any());
        verify(snapshotRepository, times(0)).sync(any(), any());
        verify(mImageRepository, times(0)).sync(any(), any());
    }

    @Test
    public void clean_ShouldUpdateProgress() {
        long lastSyncedTime = 10000L;
        when(sharedPreferences.getLong(eq(SyncManager.KEY_LAST_SYNC_TIME), anyLong()))
                .thenReturn(lastSyncedTime);

        SyncManager syncManager = syncManager();
        syncManager.clean();

        assertThat(syncManager.getProgress().getValue().getSyncState(), is(NEVER));
    }

    private void setNeverSynced() {
        when(sharedPreferences.getLong(eq(SyncManager.KEY_LAST_SYNC_TIME), anyLong()))
                .thenAnswer(i ->  i.getArguments()[1]);
    }

    private void setLastSynced(long lastSyncedTime) {
        when(sharedPreferences.getLong(eq(SyncManager.KEY_LAST_SYNC_TIME), anyLong()))
                .thenReturn(lastSyncedTime);
    }

    private void setOnline() {
        isOnline.postValue(true);
    }

    private void setOffline() {
        isOnline.postValue(false);
    }

    private SyncManager syncManager() {
        return new SyncManager(familyRepository, surveyRepository, snapshotRepository, mImageRepository,
                sharedPreferences, connectivityWatcher);
    }
}
