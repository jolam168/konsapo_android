package com.asuha.konkatsuten.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lamyiucho on 22/10/2017.
 */

public class NoticeListContent {

    public static final List<NoticeListContent.NoticeItem> ITEMS = new ArrayList<NoticeListContent.NoticeItem>();

    public static final Map<String, NoticeListContent.NoticeItem> ITEM_MAP = new HashMap<String, NoticeListContent.NoticeItem>();

    public static void addItem(NoticeListContent.NoticeItem item) {
        ITEMS.add(item);
//        ITEM_MAP.put(item.id, item);
    }

//    private static NoticeListContent.NoticeItem createDummyItem(int position) {
//        return new NoticeListContent.NoticeItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


    public static class NoticeItem{

        public final String titleText;
        public final String messageText;
        public final Long timeStamp;
        public final String url;

        public NoticeItem(String titleText, String messageText, Long timeStamp,String url){
            this.titleText = titleText;
            this.messageText = messageText;
            this.timeStamp = timeStamp;
            this.url = url;
        }

    }
}
