package com.growthbeat.message;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

/**
 * Created by mugi65 on 2016/06/29.
 */
public class MessageImageCacheManager extends LruCache<String, Bitmap> {

    public MessageImageCacheManager() {
        this((int) (Runtime.getRuntime().maxMemory() / 4));
    }

    public MessageImageCacheManager(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return bitmap.getByteCount();
        return bitmap.getAllocationByteCount();
    }

}
