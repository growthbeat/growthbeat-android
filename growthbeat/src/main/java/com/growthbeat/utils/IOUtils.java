package com.growthbeat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Build;

public final class IOUtils {

    public static String toString(InputStream inputStream) throws IOException {

        InputStreamReader objReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(objReader);
        StringBuilder stringBuilder = new StringBuilder();

        try {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();

        } catch (IOException e) {
            if (DeviceUtils.getApiVersion() >= Build.VERSION_CODES.GINGERBREAD)
                throw new IOException("Failed to convert InputStream to String.", e);
            else
                throw new IOException("Failed to convert InputStream to String.");
        } finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    if (DeviceUtils.getApiVersion() >= Build.VERSION_CODES.GINGERBREAD)
                        throw new IOException("Failed to close InputStream.", e);
                    else
                        throw new IOException("Failed to close InputStream.");
                }
            }

        }

    }

}
