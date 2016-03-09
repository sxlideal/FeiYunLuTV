package com.liveutil;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

public class CsMideaProjectionObject {
    private static CsMideaProjectionObject mCsMideaProjection = null;
    private MediaProjection mMediaProjection = null;
    private boolean mInit = false;

    public static CsMideaProjectionObject m8091a() {
        if (mCsMideaProjection == null) {
            mCsMideaProjection = new CsMideaProjectionObject();
        }
        return mCsMideaProjection;
    }

    public boolean m8093b() {
        return this.mInit;
    }

    public void m8092a(Context context, Intent intent) {
        this.mMediaProjection = ((MediaProjectionManager) context.getSystemService("media_projection")).getMediaProjection(-1, intent);
        this.mInit = true;
    }

    public MediaProjection m8094c() {
        return this.mMediaProjection;
    }

    public void m8095d() {
        if (this.mMediaProjection != null) {
            this.mMediaProjection.stop();
            this.mMediaProjection = null;
            mCsMideaProjection = null;
        }
    }
}
