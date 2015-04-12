package com.yoolean.hotspot;

import com.yoolean.client.HotSpotClient;
import com.yoolean.common.model.HotArea;
import com.yoolean.common.model.Location;

import java.util.concurrent.Callable;

/**
 * Created by chenhang on 2015/4/12.
 */
public class HotAreaTask implements Callable<HotArea> {
    private String url;
    private Location location;
    private long radius;

    public HotAreaTask(String url, Location location, long radius) {
        this.url = url;
        this.location = location;
        this.radius = radius;
    }

    @Override
    public HotArea call() throws Exception {
        HotSpotClient hotSpotClient = new HotSpotClient(url);
        return hotSpotClient.getHotArea(location, radius);
    }
}
