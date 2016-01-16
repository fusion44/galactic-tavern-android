package me.stammberger.starcitizeninformer.stores;

import com.pkmmte.pkrss.Article;

import java.util.ArrayList;

/**
 * Interface for the comm link store
 */
public interface CommLinkStoreInterface {
    /**
     * @return A list of all loaded comm links. List is empty if nothing has been loaded.
     */
    ArrayList<Article> getCommLinks();
}
