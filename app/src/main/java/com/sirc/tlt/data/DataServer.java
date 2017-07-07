package com.sirc.tlt.data;

import com.sirc.tlt.model.toutiao.ChannelItem;
import com.sirc.tlt.model.toutiao.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/22.
 */

public class DataServer {
    private DataServer() {
    }
    public static List<NewsItem> initNewsItemData() {
        List<NewsItem> list = new ArrayList<>();
        list = new ArrayList<>();
        NewsItem newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_ONE_PICTURE);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_THREE_PICTURE);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_GALLERY);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_ONE_PICTURE);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_THREE_PICTURE);
        list.add(newsItem);
        newsItem = new NewsItem();
        newsItem.setItemType(NewsItem.ITEM_TYPE_TEXT_GALLERY);
        list.add(newsItem);
        return list;
    }
    public static List<ChannelItem> initChannelItemData() {
        List<ChannelItem> list = new ArrayList<>();
        list = new ArrayList<>();
        ChannelItem channelItem = new ChannelItem();
        channelItem.setItemType(ChannelItem.ITEM_TYPE_MY);
        list.add(channelItem);

        channelItem = new ChannelItem();
        channelItem.setTitle("头条");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("创业");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("投资");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("健康");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_MY_CHANNEL);
        list.add(channelItem);

        channelItem = new ChannelItem();
        channelItem.setItemType(ChannelItem.ITEM_TYPE_OTHER);
        list.add(channelItem);

        channelItem = new ChannelItem();
        channelItem.setTitle("趣图");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("体育");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("房产");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);
        list.add(channelItem);
        channelItem = new ChannelItem();
        channelItem.setTitle("小说");
        channelItem.setItemType(ChannelItem.ITEM_TYPE_OTHER_CHANNEL);
        list.add(channelItem);

        return list;
    }
}
