package com.growthbeat.message;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by mugi65 on 2016/06/29.
 */
public class ResourceCacheManager extends LruCache<String, Bitmap> {

    public ResourceCacheManager() {
        this((int) (Runtime.getRuntime().maxMemory() / 10));
    }

    public ResourceCacheManager(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getByteCount();
    }

}
