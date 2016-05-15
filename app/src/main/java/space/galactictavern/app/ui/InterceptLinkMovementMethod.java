package space.galactictavern.app.ui;

import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;

/**
 * Class to intercept clicked links in TextViews with Html contents.
 * <p>
 * Code from here: http://stackoverflow.com/a/16644228/236779
 */
public class InterceptLinkMovementMethod extends LinkMovementMethod {
    private static InterceptLinkMovementMethod linkMovementMethod = new InterceptLinkMovementMethod();
    private static LinkClickedCallback callback;

    /**
     * Gets an instance to the {@link InterceptLinkMovementMethod} singleton class
     *
     * @param callback The {@link LinkClickedCallback} instance
     * @return {@link android.text.method.MovementMethod} instance
     */
    public static android.text.method.MovementMethod getInstance(LinkClickedCallback callback) {
        if (callback == null) {
            throw new NullPointerException("Callback must not be null");
        }

        InterceptLinkMovementMethod.callback = callback;
        return linkMovementMethod;
    }

    public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String url = link[0].getURL();
                if (callback != null) {
                    callback.onLinkClick(url);
                }
                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    /**
     * Interface for reacting to clicked links
     */
    public interface LinkClickedCallback {
        /**
         * Called when a link is clicked
         *
         * @param url The clicked URL
         */
        void onLinkClick(String url);
    }
}