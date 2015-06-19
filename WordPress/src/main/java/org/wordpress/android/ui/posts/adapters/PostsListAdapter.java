package org.wordpress.android.ui.posts.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.models.PostStatus;
import org.wordpress.android.models.PostsListPost;
import org.wordpress.android.models.PostsListPostList;
import org.wordpress.android.ui.posts.PostsListActivity;
import org.wordpress.android.ui.posts.PostsListActivity.PostListFilter;
import org.wordpress.android.ui.posts.PostsListFragment;
import org.wordpress.android.ui.reader.utils.ReaderUtils;
import org.wordpress.android.util.DisplayUtils;
import org.wordpress.android.widgets.PostListButton;
import org.wordpress.android.widgets.WPNetworkImageView;

/**
 * Adapter for Posts/Pages list
 */
public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.PostViewHolder> {

    public interface OnPostButtonClickListener {
        void onPostButtonClicked(int buttonId, PostsListPost post);
    }

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnPostsLoadedListener mOnPostsLoadedListener;
    private OnPostSelectedListener mOnPostSelectedListener;
    private OnPostButtonClickListener mOnPostButtonClickListener;

    private PostListFilter mFilter;

    private final int mLocalTableBlogId;
    private final int mPhotonWidth;
    private final int mPhotonHeight;

    private final boolean mIsPage;
    private final boolean mIsPrivateBlog;

    private final PostsListPostList mPosts = new PostsListPostList();
    private final LayoutInflater mLayoutInflater;

    private static final long ROW_ANIM_DURATION = 150;

    public PostsListAdapter(Context context, int localTableBlogId, boolean isPage, boolean isPrivateBlog) {
        mLocalTableBlogId = localTableBlogId;
        mIsPage = isPage;
        mIsPrivateBlog = isPrivateBlog;
        mLayoutInflater = LayoutInflater.from(context);

        int displayWidth = DisplayUtils.getDisplayPixelWidth(context);
        int cardSpacing = context.getResources().getDimensionPixelSize(R.dimen.content_margin);
        mPhotonWidth = displayWidth - (cardSpacing * 2);
        mPhotonHeight = context.getResources().getDimensionPixelSize(R.dimen.reader_featured_image_height);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setOnPostsLoadedListener(OnPostsLoadedListener listener) {
        mOnPostsLoadedListener = listener;
    }

    public void setOnPostSelectedListener(OnPostSelectedListener listener) {
        mOnPostSelectedListener = listener;
    }

    public void setOnPostButtonClickListener(OnPostButtonClickListener listener) {
        mOnPostButtonClickListener = listener;
    }

    public void setFilter(PostListFilter filter) {
        if (filter == null || filter == mFilter) {
            return;
        }
        mFilter = filter;
        loadPosts();
    }

    public PostsListPost getItem(int position) {
        if (isValidPosition(position)) {
            return mPosts.get(position);
        }
        return null;
    }

    private boolean isValidPosition(int position) {
        return (position >= 0 && position < mPosts.size());
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.post_cardview, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        PostsListPost post = mPosts.get(position);
        Context context = holder.itemView.getContext();

        if (post.hasTitle()) {
            holder.txtTitle.setText(post.getTitle());
        } else {
            holder.txtTitle.setText("(" + context.getResources().getText(R.string.untitled) + ")");
        }

        if (post.hasExcerpt()) {
            holder.txtExcerpt.setVisibility(View.VISIBLE);
            holder.txtExcerpt.setText(post.getExcerpt());
        } else {
            holder.txtExcerpt.setVisibility(View.GONE);
        }

        if (post.hasFeaturedImage()) {
            final String imageUrl = ReaderUtils.getResizedImageUrl(post.getFeaturedImageUrl(),
                    mPhotonWidth, mPhotonHeight, mIsPrivateBlog);
            holder.imgFeatured.setVisibility(View.VISIBLE);
            holder.imgFeatured.setImageUrl(imageUrl, WPNetworkImageView.ImageType.PHOTO);
        } else {
            holder.imgFeatured.setVisibility(View.GONE);
        }

        if (post.isLocalDraft()) {
            holder.txtDate.setVisibility(View.GONE);
            holder.btnTrash.setButtonType(PostListButton.BUTTON_DELETE);
        } else {
            holder.txtDate.setText(post.getFormattedDate());
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.btnTrash.setButtonType(PostListButton.BUTTON_TRASH);
        }

        updateStatusText(holder.txtStatus, post);

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postButtonClicked((PostListButton) v, position);
            }
        };
        holder.btnEdit.setOnClickListener(btnClickListener);
        holder.btnPublish.setOnClickListener(btnClickListener);
        holder.btnView.setOnClickListener(btnClickListener);
        holder.btnStats.setOnClickListener(btnClickListener);
        holder.btnTrash.setOnClickListener(btnClickListener);

        // posts with local changes have publish button, no view button, no stats button
        if (post.isLocalDraft() || post.hasLocalChanges()) {
            holder.btnStats.setVisibility(View.INVISIBLE);
            holder.btnView.setVisibility(View.GONE);
            holder.btnPublish.setVisibility(View.VISIBLE);
        } else {
            holder.btnStats.setVisibility(View.VISIBLE);
            holder.btnView.setVisibility(View.VISIBLE);
            holder.btnPublish.setVisibility(View.GONE);
        }

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonRow(holder.buttonRow2, holder.buttonRow1);
            }
        });

        holder.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonRow(holder.buttonRow1, holder.buttonRow2);
            }
        });

        // load more posts when we near the end
        if (mOnLoadMoreListener != null && position >= getItemCount() - 1
                && position >= PostsListFragment.POSTS_REQUEST_COUNT - 1) {
            mOnLoadMoreListener.onLoadMore();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPostSelectedListener != null) {
                    PostsListPost selectedPost = getItem(position);
                    if (selectedPost != null) {
                        mOnPostSelectedListener.onPostSelected(selectedPost);
                    }
                }
            }
        });
    }

    /*
     * buttons appear in two rows which are toggled through the "more" and "back" buttons, this
     * routine is used to animate the new row in and the old row out
     */
    private void animateButtonRow(final ViewGroup rowToAnimateIn, final ViewGroup rowToAnimateOut) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.5f);
        ObjectAnimator animOut = ObjectAnimator.ofPropertyValuesHolder(rowToAnimateOut, scaleX, scaleY);
        animOut.setDuration(ROW_ANIM_DURATION);
        animOut.setInterpolator(new AccelerateInterpolator());

        animOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rowToAnimateOut.setVisibility(View.GONE);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
                ObjectAnimator animIn = ObjectAnimator.ofPropertyValuesHolder(rowToAnimateIn, scaleX, scaleY);
                animIn.setDuration(ROW_ANIM_DURATION);
                animIn.setInterpolator(new DecelerateInterpolator());
                animIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        rowToAnimateIn.setVisibility(View.VISIBLE);
                    }
                });
                animIn.start();
            }
        });

        animOut.start();
    }

    private void updateStatusText(TextView txtStatus, PostsListPost post) {
        if ((post.getStatusEnum() == PostStatus.PUBLISHED) && !post.isLocalDraft() && !post.hasLocalChanges()) {
            txtStatus.setVisibility(View.GONE);
        } else {
            int statusTextResId = 0;
            int statusIconResId = 0;
            int statusColorResId = R.color.grey_darken_10;

            if (post.isUploading()) {
                statusTextResId = R.string.post_uploading;
                statusColorResId = R.color.alert_yellow;
            } else if (post.isLocalDraft()) {
                statusTextResId = R.string.local_draft;
                statusIconResId = R.drawable.noticon_scheduled;
                statusColorResId = R.color.alert_yellow;
            } else if (post.hasLocalChanges()) {
                statusTextResId = R.string.local_changes;
                statusIconResId = R.drawable.noticon_scheduled;
                statusColorResId = R.color.alert_yellow;
            } else {
                switch (post.getStatusEnum()) {
                    case DRAFT:
                        statusTextResId = R.string.draft;
                        statusIconResId = R.drawable.noticon_scheduled;
                        statusColorResId = R.color.alert_yellow;
                        break;
                    case PRIVATE:
                        statusTextResId = R.string.post_private;
                        break;
                    case PENDING:
                        statusTextResId = R.string.pending_review;
                        statusIconResId = R.drawable.noticon_scheduled;
                        statusColorResId = R.color.alert_yellow;
                        break;
                    case SCHEDULED:
                        statusTextResId = R.string.scheduled;
                        statusIconResId = R.drawable.noticon_scheduled;
                        statusColorResId = R.color.alert_yellow;
                        break;
                    case TRASHED:
                        statusTextResId = R.string.trashed;
                        statusIconResId = R.drawable.noticon_trashed;
                        statusColorResId = R.color.alert_red;
                        break;
                }
            }

            Resources resources = txtStatus.getContext().getResources();
            txtStatus.setTextColor(resources.getColor(statusColorResId));
            txtStatus.setText(statusTextResId != 0 ? resources.getString(statusTextResId) : "");
            Drawable drawable = (statusIconResId != 0 ? resources.getDrawable(statusIconResId) : null);
            txtStatus.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            txtStatus.setVisibility(View.VISIBLE);
        }
    }

    private void postButtonClicked(PostListButton view, int position) {
        if (mOnPostButtonClickListener == null) {
            return;
        }

        PostsListPost post = getItem(position);
        if (post == null) {
            return;
        }

        mOnPostButtonClickListener.onPostButtonClicked(view.getButtonType(), post);
    }

    @Override
    public long getItemId(int position) {
        return mPosts.get(position).getPostId();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void loadPosts() {
        new LoadPostsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public int getRemotePostCount() {
        if (mPosts == null) {
            return 0;
        }

        int remotePostCount = 0;
        for (PostsListPost post : mPosts) {
            if (!post.isLocalDraft())
                remotePostCount++;
        }

        return remotePostCount;
    }

    public void removePost(PostsListPost post) {
        int position = mPosts.indexOfPost(post);
        if (position == -1) {
            return;
        }
        mPosts.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnPostSelectedListener {
        void onPostSelected(PostsListPost post);
    }

    public interface OnPostsLoadedListener {
        void onPostsLoaded(int postCount);
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle;
        private final TextView txtExcerpt;
        private final TextView txtDate;
        private final TextView txtStatus;

        private final PostListButton btnEdit;
        private final PostListButton btnPublish;
        private final PostListButton btnView;
        private final PostListButton btnMore;

        private final PostListButton btnStats;
        private final PostListButton btnTrash;
        private final PostListButton btnBack;

        private final ViewGroup buttonRow1;
        private final ViewGroup buttonRow2;

        private final WPNetworkImageView imgFeatured;

        public PostViewHolder(View view) {
            super(view);

            txtTitle = (TextView) view.findViewById(R.id.text_title);
            txtExcerpt = (TextView) view.findViewById(R.id.text_excerpt);
            txtDate = (TextView) view.findViewById(R.id.text_date);
            txtStatus = (TextView) view.findViewById(R.id.text_status);

            btnEdit = (PostListButton) view.findViewById(R.id.btn_edit);
            btnPublish = (PostListButton) view.findViewById(R.id.btn_publish);
            btnView = (PostListButton) view.findViewById(R.id.btn_view);
            btnMore = (PostListButton) view.findViewById(R.id.btn_more);

            btnStats = (PostListButton) view.findViewById(R.id.btn_stats);
            btnTrash = (PostListButton) view.findViewById(R.id.btn_trash);
            btnBack = (PostListButton) view.findViewById(R.id.btn_back);

            buttonRow1 = (ViewGroup) view.findViewById(R.id.layout_buttons_row1);
            buttonRow2 = (ViewGroup) view.findViewById(R.id.layout_buttons_row2);

            imgFeatured = (WPNetworkImageView) view.findViewById(R.id.image_featured);
        }
    }

    private class LoadPostsTask extends AsyncTask<Void, Void, Boolean> {
        PostsListPostList loadedPosts;

        @Override
        protected Boolean doInBackground(Void... nada) {
            loadedPosts = WordPress.wpDB.getPostsListPosts(mLocalTableBlogId, mIsPage, mFilter);
            return !loadedPosts.isSameList(mPosts);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                mPosts.clear();
                mPosts.addAll(loadedPosts);
                notifyDataSetChanged();

                if (mOnPostsLoadedListener != null && mPosts != null) {
                    mOnPostsLoadedListener.onPostsLoaded(mPosts.size());
                }
            }
        }
    }
}
