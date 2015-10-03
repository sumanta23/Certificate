package org.sumanta.rest.api;

import java.util.HashMap;
import java.util.Map;

public class ContentHolder {
    private Map<String, Content> holder = new HashMap<>();

    static ContentHolder contentHolder;

    private ContentHolder() {

    }

    public static ContentHolder getInstance() {
        if (contentHolder == null) {
            contentHolder = new ContentHolder();
        }
        return contentHolder;
    }

    public Map<String, Content> getHolder() {
        return holder;
    }

    public void setHolder(Map<String, Content> holder) {
        this.holder = holder;
    }

}
