package space.galactictavern.app.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThreadPost {

    @SerializedName("author")
    @Expose
    public ForumThreadPostAuthor author;
    @SerializedName("post")
    @Expose
    public ForumThreadPostData post;

}
