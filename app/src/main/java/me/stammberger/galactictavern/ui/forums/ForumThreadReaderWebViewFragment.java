package me.stammberger.galactictavern.ui.forums;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import me.stammberger.galactictavern.R;

/**
 * A fragment representing a single thread reader screen.
 * This fragment is either contained in a {@link ForumThreadListActivity}
 * in two-pane mode (on tablets) or a {@link ForumThreadReaderActivity}
 * on handsets.
 */
public class ForumThreadReaderWebViewFragment extends Fragment {
    /**
     * The fragment argument representing the thread ID that this fragment
     * represents.
     */
    public static final String ARG_THREAD_ID = "thread_id";

    /**
     * Thread Id
     */
    private long mForumThreadId;

    private WebView mWebView;
    private ProgressBar mProgress;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForumThreadReaderWebViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_THREAD_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mForumThreadId = getArguments().getLong(ARG_THREAD_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mForumId.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*
            TODO: Fix the API

            I totally hate this but I've wasted to much time on this now.
            Unfortunately the API source is written in PHP and looks horrible due to over optimization.
         */

        View root = inflater.inflate(
                R.layout.fragment_forum_thread_reader_web_view, container, false);
        mWebView = (WebView) root.findViewById(R.id.forumThreadReaderWebView);
        mProgress = (ProgressBar) root.findViewById(R.id.forumThreadReaderProgressBar);

        setupWebView();

        return root;
    }

    @SuppressLint("SetJavaScriptEnabled")
    // I don't think we are vulnerable to XSS events in this app
    private void setupWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.canGoBackOrForward(5);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    mProgress.setVisibility(View.GONE);
                } else {
                    mProgress.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });

        String url = "https://forums.robertsspaceindustries.com/discussion/" + mForumThreadId;
        mWebView.loadUrl(url);
    }
}
