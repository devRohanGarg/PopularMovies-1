package dev.RohanGarg;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.RohanGarg.models.CardItemModel;

public class ScrollingActivity extends AppCompatActivity {

    @Bind(R.id.backDrop)
    ImageView imageView;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.rating)
    TextView rating;
    @Bind(R.id.popularity)
    TextView popularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardItemModel cardItem = getIntent().getParcelableExtra("cardItem");
        Picasso.with(getApplicationContext()).load(cardItem.backDropImgURL).into(imageView);
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.offwhite));
        collapsingToolbarLayout.setTitle(cardItem.title);
        overview.setText(cardItem.overview);
        date.setText(cardItem.releaseDate);
        rating.setText(cardItem.rating);
        popularity.setText(cardItem.popularity);



        View[] animatedViews = new View[] {
                imageView, overview, date, rating,popularity
        };

// see here for using the right interpolator is important:
// http://www.google.com/design/spec/animation/authentic-motion.html#authentic-motion-mass-weight
// and here for how to use them:
// http://developer.android.com/guide/topics/graphics/prop-animation.html#interpolators
        Interpolator interpolator = new DecelerateInterpolator();

        for (int i = 0; i < animatedViews.length; ++i) {
            View v = animatedViews[i];

            // let's enable hardware acceleration for better performance
            // http://blog.danlew.net/2015/10/20/using-hardware-layers-to-improve-animation-performance/
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            // initial state: hide the view and move it down slightly
            v.setAlpha(0f);
            v.setTranslationY(75);

            v.animate()
                    .setInterpolator(interpolator)
                    .alpha(1.0f)
                    .translationY(0)
                            // this little calculation here produces the staggered effect we
                            // saw, so each animation starts a bit after the previous one
                    .setStartDelay(100 + 75 * i)
                    .start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void favourite() {
        Snackbar.make(ButterKnife.findById(this, R.id.root), "Love", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
