package knf.kuma.directory;

import android.arch.paging.PagedListAdapter;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.kuma.animeinfo.ActivityAnime;
import knf.kuma.commons.PicassoSingle;
import knf.kuma.pojos.AnimeObject;
import knf.kuma.R;

/**
 * Created by Jordy on 06/01/2018.
 */

public class DirectorypageAdapter extends PagedListAdapter<AnimeObject,DirectorypageAdapter.ItemHolder> {

    private Fragment fragment;

    public DirectorypageAdapter(Fragment fragment) {
        super(DIFF_CALLBACK);
        this.fragment = fragment;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(getLayType(), parent, false));
    }

    @LayoutRes
    private int getLayType(){
        if (PreferenceManager.getDefaultSharedPreferences(fragment.getContext()).getString("lay_type","0").equals("0")){
            return R.layout.item_dir;
        }else {
            return R.layout.item_dir_grid;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final AnimeObject object=getItem(position);
        if (object!=null){
            PicassoSingle.get(fragment.getContext()).load(object.img).into(holder.imageView);
            holder.textView.setText(object.name);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityAnime.open(fragment,object,holder.imageView);
                }
            });
        }
    }


    public static final DiffCallback<AnimeObject> DIFF_CALLBACK = new DiffCallback<AnimeObject>() {
        @Override
        public boolean areItemsTheSame(@NonNull AnimeObject oldItem, @NonNull AnimeObject newItem) {
            return oldItem.key==newItem.key;
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimeObject oldItem, @NonNull AnimeObject newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };

    class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card)
        CardView cardView;
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textView;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
