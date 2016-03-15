package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nispok.snackbar.Snackbar;

import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.activities.MainActivity;
import cc.coocol.jinxiujob.enums.EntersListType;
import cc.coocol.jinxiujob.models.BaseEnterItemModel;

/**
 * Created by raymond on 16-2-28.
 */
public class CollectedListAdapter extends RecyclerView.Adapter<CollectedListAdapter.PlaceHolder> {

    private Context context;
    private List<BaseEnterItemModel> enterItemModels;

    private EntersListType entersListType;

    public CollectedListAdapter(Context context, List<BaseEnterItemModel> enterItemModels, EntersListType entersListType) {
        this.context = context;
        this.enterItemModels = enterItemModels;
        this.entersListType = entersListType;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enters_list, parent,false);
        PlaceHolder placeHolder = new PlaceHolder(view);
        return placeHolder;
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        if (entersListType == EntersListType.AllEnters) {
            holder.optionTextView.setText("");
        } else if (entersListType == EntersListType.HotEnters) {
            holder.optionTextView.setText("20人关注");
        } else if (entersListType == EntersListType.NearbyEnters) {
            holder.optionTextView.setText("距离450米");
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return enterItemModels.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = View.inflate(context, R.layout.item_enters_list, null);
//            placeHolder = new PlaceHolder();
//            convertView.setTag(placeHolder);
//            placeHolder.optionTextView = (TextView) convertView.findViewById(R.id.option);
//            if (entersListType == EntersListType.AllEnters) {
//                placeHolder.optionTextView.setText("");
//            } else if (entersListType == EntersListType.HotEnters) {
//                placeHolder.optionTextView.setText("20人关注");
//            } else if (entersListType == EntersListType.NearbyEnters) {
//                placeHolder.optionTextView.setText("距离450米");
//            }
//        } else {
//            placeHolder = (PlaceHolder) convertView.getTag();
//        }
//        return convertView;
//    }

    class PlaceHolder extends RecyclerView.ViewHolder {
        public RoundedImageView logoImageView;
        public TextView nameTextView;
        public TextView addrTextView;
        public TextView optionTextView;

        public PlaceHolder(View itemView) {
            super(itemView);
            optionTextView = (TextView) itemView.findViewById(R.id.option);
        }

    }
}
