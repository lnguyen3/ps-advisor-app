package org.fundacionparaguaya.adviserplatform.data.repositories;

import android.support.annotation.Nullable;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base Repository class. Provides functionality for checking if the sync should be aborted {@link #shouldAbortSync()}
 *
 * Child classes should override the {@link #sync(Date)} function, and call periodically call {@link #shouldAbortSync()}
 * to handle job cancellations mid-sync
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
     * throughout syncing and abort the sync if false. The function should also call {@link #clearSyncStatus()} after sync
     * is finished.
     */
    abstract boolean sync(@Nullable Date lastSync);

    public boolean shouldAbortSync()
    {
        return !(mIsAlive == null || mIsAlive.get());
    }

    public void clearSyncStatus()
    {
        mIsAlive = null;
    }
}
