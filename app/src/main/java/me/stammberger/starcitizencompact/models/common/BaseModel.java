package me.stammberger.starcitizencompact.models.common;

/**
 * Base for all models used in this app.
 * Provides fields related to the layout manage for {@link android.support.v7.widget.RecyclerView}
 */
public class BaseModel {

    /**
     * Android layout file id
     */
    public int layout = -1;

    /**
     * Span count for this particular model
     */
    public int spanCount = 1;
}
