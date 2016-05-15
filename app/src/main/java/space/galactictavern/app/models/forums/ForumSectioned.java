package space.galactictavern.app.models.forums;

/**
 * An abstraction to the {@link Forum} type to enable section of the RecyclerView.
 * The RSI forums are divided by sections which the API does not provide.
 * To work around this I'm adding these manually
 */
public class ForumSectioned {
    /**
     * Defines this object as a header type
     */
    public static int TYPE_SECTION_HEADER = 0;

    /**
     * Defines this object as a forum type
     */
    public static int TYPE_FORUM = 1;

    /**
     * Item type. Can be {@link #TYPE_SECTION_HEADER} or {@link #TYPE_FORUM}
     */
    public int type;

    /**
     * Spancount of the item. Mostly used if {@link #type} is {@link #TYPE_SECTION_HEADER} to
     * fill the whole width of the RecyclerView
     */
    public int spanCount = 1;

    /**
     * The actual {@link Forum} data object. Is null if {@link #type} is a {@link #TYPE_SECTION_HEADER}
     */
    public Forum forum;

    /**
     * Section this item belongs to
     */
    public String section;
}
