package dev.RohanGarg;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dev.RohanGarg.adapters.RecyclerAdapter;
import dev.RohanGarg.adapters.RecyclerItemClickListener;
import dev.RohanGarg.models.CardItemModel;
import dev.RohanGarg.utils.AppController;
import dev.RohanGarg.utils.GridAutofitLayoutManager;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    public int page;
    public int selected;
    @BindString(R.string.apiBaseUrl)
    String URL;
    @BindString(R.string.TAG_JSON)
    String TAG_JSON;
    @BindString(R.string.POPULARITY)
    String sortBy;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    ArrayList<CardItemModel> cardItems = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSwipeRefreshLayout();

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            page = 1;
            selected = 0;
            fetch();
        }

        setupRecycleView();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch();
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page = savedInstanceState.getInt("page");
        selected = savedInstanceState.getInt("selected");
        sortBy = savedInstanceState.getString("sortBy");
        cardItems = savedInstanceState.getParcelableArrayList("cardItems");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        outState.putInt("page", page);
        outState.putInt("selected", selected);
        outState.putString("sortBy", sortBy);
        outState.putParcelableArrayList("cardItems", cardItems);
    }

    @OnClick(R.id.fab)
    void displayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle("Sort by")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.options, selected,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, which + " selected");
                                if (which != selected) {
                                    selected = which;
                                    if (selected == 0)
                                        sortBy = getResources().getString(R.string.POPULARITY);
                                    else
                                        sortBy = getResources().getString(R.string.RATING);
                                    page = 1;
                                    cardItems = new ArrayList<>();
                                    recyclerAdapter = new RecyclerAdapter(getApplicationContext(), cardItems);
                                    recyclerView.setAdapter(recyclerAdapter);
                                    fetch();
                                }
                                dialog.dismiss();
                            }
                        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void fetch() {
        String finalURL = URL + "sort_by=" + sortBy + "&page=" + page + "&api_key=" + getResources().getString(R.string.KEY);
        //Log.d(TAG, finalURL);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(finalURL,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cardItems.add(new CardItemModel(
                                        "http://image.tmdb.org/t/p/w342/" + jsonObject.getString("poster_path"),
                                        jsonObject.getBoolean("adult"),
                                        jsonObject.getString("overview"),
                                        jsonObject.getString("release_date"),
                                        jsonObject.getString("title"),
                                        "http://image.tmdb.org/t/p/w780/" + jsonObject.getString("backdrop_path"),
                                        jsonObject.getString("popularity"),
                                        jsonObject.getString("vote_count"),
                                        jsonObject.getString("vote_average")
                                ));
                            }
                            recyclerAdapter.notifyDataSetChanged();
                            //Log.d(TAG, "On page no: " + page);
                            page++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(R.id.root), "Aw, Snap! Something went wrong", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, TAG_JSON);
    }

    private void setupRecycleView() {
        gridLayoutManager = new GridAutofitLayoutManager(this, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getApplicationContext().getResources().getDisplayMetrics()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new RecyclerAdapter(getApplicationContext(), cardItems);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == cardItems.size() - 1) {
                    fetch();
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(
                                // the source view from which to animate the new Activity
                                // defines the co-ordinate space for initial (x, y) location
                                view,
                                // starting (x, y) position for animation
                                // NOTE: these co-ordinates are relative to the source view above
                                0, 0,
                                // initial width and height of the new Activity
                                view.getWidth(), view.getHeight());
                        startActivity(new Intent(getApplicationContext(), ScrollingActivity.class).putExtra("cardItem", cardItems.get(position)), opts.toBundle());
                    }
                })
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            displayDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}

