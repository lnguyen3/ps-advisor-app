package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.support.annotation.Nullable;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Repository
 */

public abstract class BaseRepository {
    private AtomicBoolean mIsAlive = null;

    public boolean sync(AtomicBoolean isAlive, @Nullable Date lastSync)
    {
        mIsAlive = isAlive;
        return sync(lastSync);
    }

    /**
     * Main sync function. Any repository implementing this function should call {@link #shouldAbortSync()} periodically
     * throughout syncing and abort the sync if faalse. The function should also call {@link #clearSyncStatus()} after sync
     * is finished.
     */
    abstract boolean sync(@Nullable Date lastSync);

    boolean shouldAbortSync()
    {
        return !(mIsAlive == null || mIsAlive.get());
    }

    void clearSyncStatus()
    {
        mIsAlive = null;
    }
}
