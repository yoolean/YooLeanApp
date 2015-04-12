package com.yoolean.hotspot;

import com.yoolean.client.HotSpotClient;
import com.yoolean.common.model.HotSpot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenhang on 2015/4/11.
 */
public class HotSpotCreator extends Thread {
    private String url;
    private HotSpot hotSpot;

    public HotSpotCreator(String url, HotSpot hotSpot) {
        super();
        this.url = url;
        this.hotSpot = hotSpot;
    }

    @Override
    public void run() {
        HotSpotClient hotSpotClient = new HotSpotClient(url);
        hotSpotClient.createHotSpot(hotSpot);
    }
}
