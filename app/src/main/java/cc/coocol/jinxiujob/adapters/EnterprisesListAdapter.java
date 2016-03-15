package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.models.AllEnterItemModel;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;
import cc.coocol.jinxiujob.models.HotEnterItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;
import cc.coocol.jinxiujob.models.NearbyEnterItemModel;

/**
 * Created by raymond on 16-2-28.
 */
public class EnterprisesListAdapter extends RecyclerView.Adapter<EnterprisesListAdapter.PlaceHolder> {

    private Context context;
    private List<BaseEnterItemModel> enterItemModels;

    private EntersListType entersListType;

    private OnLastItemVisibleListener lastItemVisibleListener;

    public EnterprisesListAdapter(Context context, List<BaseEnterItemModel> enterItemModels, EntersListType entersListType, EnterprisesListAdapter.OnLastItemVisibleListener lastItemVisibleListener) {
        this.context = context;
        this.enterItemModels = enterItemModels;
        this.entersListType = entersListType;
        this.lastItemVisibleListener = lastItemVisibleListener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (enterItemModels == null) {
            return 0;
        }
        return enterItemModels.size();
    }


    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vIew = LayoutInflater.from(
                context).inflate(R.layout.item_enters_list, parent,
                false);
        PlaceHolder placeHolder = new PlaceHolder(vIew);
        return placeHolder;
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        BaseEnterItemModel model = enterItemModels.get(position);
        String addr = model.getAddress();
        if (addr != null && addr.length() > 14) {
            holder.addrTextView.setText(model.getAddress().substring(0, 14) + "...");
        } else {
            holder.addrTextView.setText(model.getAddress());
        }

        if (model.getNick() != null) {
            holder.nickTextView.setText(model.getNick());
            holder.nameTextView.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(model.getName());
        } else {
            holder.nickTextView.setText(model.getName());
        }
        if (entersListType == EntersListType.AllEnters) {
            holder.optionTextView.setText(model.getTime());
        } else if (entersListType == EntersListType.HotEnters) {
            holder.optionTextView.setText(((HotEnterItemModel)model).getApply() + "人申请");
        } else if (entersListType == EntersListType.NearbyEnters) {
            holder.optionTextView.setText("距离" + ((NearbyEnterItemModel) model).getDistance()+"米");
        }
        Uri uri = Uri.parse("http://115.28.22.98/api/v1.0/static/logo/" + model.getCompanyId() + ".jpg");
        holder.logoImageView.setImageURI(uri);
        if (enterItemModels.size() > 9 && position == enterItemModels.size() - 1) {
            lastItemVisibleListener.loadMore();
        }
    }


    class PlaceHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView logoImageView;
        public TextView nameTextView;
        public TextView addrTextView;
        public TextView optionTextView;
        public TextView nickTextView;

        public PlaceHolder(View itemView) {
            super(itemView);
            optionTextView = (TextView) itemView.findViewById(R.id.option);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            addrTextView = (TextView) itemView.findViewById(R.id.addr);
            logoImageView = (SimpleDraweeView) itemView.findViewById(R.id.logo);
            nickTextView = (TextView) itemView.findViewById(R.id.nick);
        }
    }

    public interface OnLastItemVisibleListener {

        public void loadMore();
    }
}
