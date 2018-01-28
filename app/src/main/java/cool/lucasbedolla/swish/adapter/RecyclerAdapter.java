package cool.lucasbedolla.swish.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotosetPost;
import com.tumblr.jumblr.types.Post;

import java.util.List;

import cool.lucasbedolla.swish.R;
import cool.lucasbedolla.swish.util.MyPrefs;
import cool.lucasbedolla.swish.util.ViewHolderBinder;
import cool.lucasbedolla.swish.view.viewholders.BasicViewHolder;

/**
 * Created by LUCASURE on 2/4/2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<BasicViewHolder> implements View.OnClickListener {

    enum PostType {
        PHOTO, TEXT, VIDEO, QUESTION, ANSWER, CHAT, AUDIO, QUOTE, UNKNOWN, LOADING, LINK
    }

    public static final String TAG = "RECYCLER ADAPTER";
    private final List<Post> itemList;
    private Context ctx;

    public RecyclerAdapter(Context context, List<Post> inputList) {
        this.ctx = context;
        itemList = inputList;
    }

    @Override
    public BasicViewHolder onCreateViewHolder(ViewGroup parent, int postType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //staggered
        if (MyPrefs.getIsDualMode(parent.getContext())) {
            //tweak a new set of this layout to have smaller scale for dual mode
            return new BasicViewHolder(inflater.inflate(R.layout.view_holder_base, parent, false));
        } else {
            return new BasicViewHolder(inflater.inflate(R.layout.view_holder_base, parent, false));
        }
    }


    @Override
    public int getItemViewType(int position) {
        Post post = itemList.get(position);
        if (post instanceof PhotosetPost ||
                post instanceof PhotoPost) {
            return 0;
        } else {
            return 666;
        }
    }

    @Override
    public void onBindViewHolder(BasicViewHolder holder, int position) {

        Post post = itemList.get(position);

        PostType type = mapPost(holder);

        switch (type) {
            case PHOTO:
                ViewHolderBinder.placePhotos(ctx, holder, post, this);
                break;
            case TEXT:
                ViewHolderBinder.placeText(holder, post);
                break;
            case CHAT:
                ViewHolderBinder.placeChat(ctx, holder, post, this);
                break;
            case AUDIO:
                ViewHolderBinder.placeAudio(ctx, holder, post, this);
                break;
            case QUOTE:
                ViewHolderBinder.placeQuote(ctx, holder, post, this);
                break;
            case VIDEO:
                ViewHolderBinder.placeVideo(ctx, holder, post, this);
                break;
            case ANSWER:
                ViewHolderBinder.placeAnswer(ctx, holder, post, this);
                break;
            case UNKNOWN:
                ViewHolderBinder.placeUnknown(ctx, holder, post, this);
                break;
            case LOADING:
                ViewHolderBinder.placeLoading(ctx, holder, this);

                break;

        }

    }

    private PostType mapPost(BasicViewHolder holder) {
        switch (holder.getItemViewType()) {
            case 0:
                return PostType.PHOTO;
            case 1:
                return PostType.PHOTO;
            case 2:
                return PostType.TEXT;
            case 3:
                return PostType.ANSWER;
            case 4:
                return PostType.VIDEO;
            case 5:
                return PostType.QUOTE;
            case 6:
                return PostType.CHAT;
            case 7:
                return PostType.LINK;
            case 8:
                return PostType.UNKNOWN;
            case 9:
                return PostType.LOADING;
            case 10:
                return PostType.AUDIO;
            default:
                return PostType.UNKNOWN;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.extras_parent:
                animateExtras(view);
                break;
            case R.id.like_button:
                Toast.makeText(view.getContext(), "Liked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.reblog_button:
                Toast.makeText(view.getContext(), "Reblogged!", Toast.LENGTH_SHORT).show();

                break;
        }

    }


    private void animateExtras(final View view) {
        final ImageView buttonSpin = view.findViewById(R.id.extras_button);
        final LinearLayout extraContentLayout = view.findViewById(R.id.extras_content);

        if (extraContentLayout.getVisibility() == View.GONE) {

            extraContentLayout.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.extras_content_animate_down));
            buttonSpin.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.arrow_clockwise_rotate));
            extraContentLayout.setVisibility(View.VISIBLE);
        } else {

            Animation upAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.extras_content_animate_up);
            upAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    extraContentLayout.setVisibility(View.GONE);
                    //animate arrow
                    buttonSpin.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.arrow_counterclockwise_rotate));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            extraContentLayout.startAnimation(upAnimation);

        }
    }
}

