package com.growthbeat.message;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by mugi65 on 2016/06/29.
 */
public class MessageImageCacheManager extends LruCache<String, Bitmap> {

    public MessageImageCacheManager() {
        this((int) (Runtime.getRuntime().maxMemory() / 5));
    }

    public MessageImageCacheManager(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getByteCount();
    }

}
