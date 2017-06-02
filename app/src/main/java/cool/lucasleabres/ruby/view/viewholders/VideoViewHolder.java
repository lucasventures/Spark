package cool.lucasleabres.ruby.view.viewholders;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import cool.lucasleabres.ruby.R;

/**
 * Created by Lucas Bedolla on 5/31/2017.
 */


public class VideoViewHolder extends BasicViewHolder {

    private TextView vTitle;
    private TextView vDescription;
    private WebView vid;

    public VideoViewHolder(View v) {
        super(v);
        vTitle = (TextView) v.findViewById(R.id.vTitle);
        vDescription = (TextView) v.findViewById(R.id.vDescription);
        vid = (WebView) v.findViewById(R.id.video_view);

    }
}