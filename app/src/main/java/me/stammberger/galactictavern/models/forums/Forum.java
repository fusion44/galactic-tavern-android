package me.stammberger.galactictavern.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import me.stammberger.galactictavern.stores.db.tables.forums.ForumModelTable;
import me.stammberger.galactictavern.stores.db.tables.user.UserSearchHistoryEntryTable;

@StorIOSQLiteType(table = UserSearchHistoryEntryTable.TABLE)
public class Forum {
    @StorIOSQLiteColumn(name = ForumModelTable.COLUMN_ID, key = true)
    public Long id;

    @SerializedName("forum_title")
    @Expose
    public String forumTitle;
    @SerializedName("forum_id")
    @Expose
    public String forumId;
    @SerializedName("forum_description")
    @Expose
    public String forumDescription;
    @SerializedName("forum_url")
    @Expose
    public String forumUrl;
    @SerializedName("forum_rss")
    @Expose
    public String forumRss;
    @SerializedName("forum_discussion_count")
    @Expose
    public String forumDiscussionCountString;
    @StorIOSQLiteColumn(name = ForumModelTable.COLUMN_FORUM_DISCUSSION_COUNT)
    public Integer forumDiscussionCount;
    @SerializedName("forum_post_count")
    @Expose
    public String forumPostCountString;
    @StorIOSQLiteColumn(name = ForumModelTable.COLUMN_FORUM_POST_COUNT)
    public Integer forumPostCount;

}
