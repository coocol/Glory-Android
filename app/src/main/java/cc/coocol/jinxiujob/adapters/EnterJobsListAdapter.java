package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.ButterKnife;
import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;
import cc.coocol.jinxiujob.models.NearbyJobItemModel;

/**
 * Created by raymond on 16-2-28.
 */
public class EnterJobsListAdapter extends RecyclerView.Adapter<EnterJobsListAdapter.PlaceHolder> implements View.OnClickListener{

    private Context context;

    private List<BaseJobItemModel> jobItemModels;


    private OnLastItemVisibleListener lastItemVisibleListener;


    public EnterJobsListAdapter(Context context, List<BaseJobItemModel> jobItemModels,
                                OnLastItemVisibleListener lastItemVisibleListener) {
        this.lastItemVisibleListener = lastItemVisibleListener;
        this.jobItemModels = jobItemModels;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View v);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enter_jobs_list, parent, false);
        view.setOnClickListener(this);
        PlaceHolder placeHolder = new PlaceHolder(view);
        return placeHolder;
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        final BaseJobItemModel model = jobItemModels.get(position);
        holder.jobView.setText(model.getName());
        holder.optionView.setText(model.getTime());
        String addr = model.getAddress();
        if (addr != null && addr.length() > 14) {
            holder.addressView.setText(model.getAddress().substring(0, 14) + "...");
        } else {
            holder.addressView.setText(model.getAddress());
        }
        if (jobItemModels.size() > 9 && position == jobItemModels.size() - 1) {
            lastItemVisibleListener.loadMore();
        }
        holder.cardView.setTag(model.getId());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (jobItemModels == null) {
            return 0;
        }
        return jobItemModels.size();
    }


    class PlaceHolder extends RecyclerView.ViewHolder{

        public TextView optionView;
        public TextView addressView;
        public TextView jobView;
        public View cardView;

        public PlaceHolder(View itemView) {
            super(itemView);
            optionView = (TextView) itemView.findViewById(R.id.option);
            addressView = (TextView) itemView.findViewById(R.id.job_addr);
            jobView = (TextView) itemView.findViewById(R.id.job_name);
            cardView = itemView;
        }


    }

    public interface OnLastItemVisibleListener {

        public void loadMore();
    }
}
