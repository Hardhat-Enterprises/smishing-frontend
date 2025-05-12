package com.example.smishingdetectionapp.news.Models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * POJOs for parsing an RSS feed with Simple-XML.
 */
public class RSSFeedModel {

    /* ───── root <rss> ───── */
    @Root(name = "rss", strict = false)
    public static class Feed {
        @Element(name = "channel") public Channel channel;
    }

    /* ───── <channel> ───── */
    @Root(name = "channel", strict = false)
    public static class Channel {
        @ElementList(entry = "item", inline = true)
        public List<Article> articles;
    }

    /* ───── <item> (article) ───── */
    @Root(name = "item", strict = false)
    public static class Article {
        @Element(name = "title")                     public String title;
        @Element(name = "link")                      public String link;
        @Element(name = "description", required = false) public String description;
        @Element(name = "pubDate",    required = false) public String pubDate;

        /* bookmark flag */
        private boolean isBookmarked = false;
        public boolean isBookmarked()                { return isBookmarked; }
        public void setBookmarked(boolean b)         { isBookmarked = b;    }

        /* nicer date for UI */
        public String getFormattedDate() {
            try {
                SimpleDateFormat in  = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                Date d = in.parse(pubDate);
                SimpleDateFormat out = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
                return out.format(d);
            } catch (Exception e) {
                return pubDate;   // fallback
            }
        }

        /* ---------- DiffUtil helpers ---------- */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Article)) return false;
            Article a = (Article) obj;

            return  link.equals(a.link) &&
                    title.equals(a.title) &&
                    String.valueOf(description).equals(String.valueOf(a.description)) &&
                    String.valueOf(pubDate).equals(String.valueOf(a.pubDate)) &&
                    isBookmarked == a.isBookmarked;
        }

        @Override
        public int hashCode() {
            int result = link.hashCode();
            result = 31 * result + title.hashCode();
            result = 31 * result + (description == null ? 0 : description.hashCode());
            result = 31 * result + (pubDate     == null ? 0 : pubDate.hashCode());
            result = 31 * result + (isBookmarked ? 1 : 0);
            return result;
        }
    }

    /* Retrofit interface for fetching the feed */
    public interface RSSApi {
        @GET("{Feed}")
        Call<Feed> getArticles(@Path("Feed") String Feed);
    }
}
