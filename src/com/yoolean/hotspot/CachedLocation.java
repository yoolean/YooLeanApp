package com.yoolean.hotspot;

import com.yoolean.common.model.Location;

/**
 * Created by chenhang on 2015/4/11.
 */
public class CachedLocation {
    private static Location location = new Location(0, 0);

    public static synchronized Location get() {
        return location;
    }

    public static synchronized void put(double latitude,
                                        double longitude) {
        location = new Location(latitude, longitude);
    }
}
