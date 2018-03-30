package com.loader.rscl.net.xml;

import com.loader.rscl.frame.elements.ArchiveBox;
import com.loader.rscl.frame.elements.NewsBox;

import java.awt.Rectangle;

public class XMLReader
{
    private static Feed news;
    private static Feed archives;
    
    public static void init(final NewsBox newsBox) {
        try {
            XMLReader.news = getNews();
            XMLReader.archives = getArchivedNews();
            newsBox.getTitle().setText((XMLReader.news == null) ? "No news available" : XMLReader.news.getMessages().get(0).getTitle());
            final String newsText = (XMLReader.news == null) ? "-----" : XMLReader.news.getMessages().get(0).getCleanDesc();
            String[] split;
            for (int length = (split = newsText.split("<br>")).length, i = 0; i < length; ++i) {
                final String line = split[i];
                newsBox.append("<p style='color:#AAAAAA;margin:0;font-size:8px;font-family:arial;'> " + line + " </p>");
            }
            if (XMLReader.archives == null) {
                return;
            }
            int startY = 0;
            for (final FeedMessage msg : XMLReader.archives.getMessages()) {
                newsBox.add(new ArchiveBox(msg, new Rectangle(246, startY, 194, 39)));
                startY += 50;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Feed getArchivedNews() {
        final RSSFeedParser parser = new RSSFeedParser("http://www.rsclegacy.com/extern.php?action=feed&type=rss&fid=14");
        final Feed feed = parser.readFeed();
        return feed;
    }
    
    public static Feed getNews() {
        final RSSFeedParser parser = new RSSFeedParser("http://www.rsclegacy.com/extern.php?action=feed&type=rss&fid=1&show=1&order=commented");
        final Feed feed = parser.readFeed();
        return feed;
    }
}
