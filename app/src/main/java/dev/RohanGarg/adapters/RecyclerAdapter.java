package dev.RohanGarg.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.RohanGarg.R;
import dev.RohanGarg.models.CardItemModel;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public List<CardItemModel> cardItems;
    Context mContext;

    public RecyclerAdapter(Context context, List<CardItemModel> cardItems) {
        this.cardItems = cardItems;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(cardItems.get(position).title);
        holder.rating.setText(cardItems.get(position).rating);
        holder.popularity.setText(cardItems.get(position).popularity);
        Picasso.with(mContext).load(cardItems.get(position).posterImgURL).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cardTitle)
        TextView title;
        @Bind(R.id.cardRating)
        TextView rating;
        @Bind(R.id.cardPopularity)
        TextView popularity;
        @Bind(R.id.cardImage)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
