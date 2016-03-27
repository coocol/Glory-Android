package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.configs.MyConfig;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.AppliedJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;
import cc.coocol.jinxiujob.models.NearbyJobItemModel;
import cc.coocol.jinxiujob.networks.URL;

/**
 * Created by raymond on 16-2-28.
 */
public class JobsListAdapter extends RecyclerView.Adapter<JobsListAdapter.PlaceHolder> implements View.OnClickListener {

    private Context context;

    private List<BaseJobItemModel> jobItemModels;

    private JobListType jobListType;

    private OnLastItemVisibleListener lastItemVisibleListener;


    public JobsListAdapter(Context context, List<BaseJobItemModel> jobItemModels, JobListType jobListType,
                           OnLastItemVisibleListener lastItemVisibleListener) {
        this.lastItemVisibleListener = lastItemVisibleListener;
        this.jobItemModels = jobItemModels;
        this.jobListType = jobListType;
        this.context = context;
        this.lastItemVisibleListener = lastItemVisibleListener;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_list, parent, false);
        PlaceHolder placeHolder = new PlaceHolder(view);
        placeHolder.companyLayout.setOnClickListener(this);
        placeHolder.cardView.setOnClickListener(this);
        return placeHolder;
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {
        final BaseJobItemModel model = jobItemModels.get(position);
        holder.jobView.setText(model.getName());
        String addr = model.getAddress();
        if (addr != null && addr.length() > 14) {
            holder.addressView.setText(model.getAddress().substring(0, 14) + "...");
        } else {
            holder.addressView.setText(model.getAddress());
        }
        String nick = model.getNick();
        if (nick != null && !nick.equals("")) {
            holder.companyView.setText(nick);
        } else {
            holder.companyView.setText(model.getCompany());
        }
        if (jobListType == JobListType.AllJob) {
            AllJobItemModel allJobItemModel = (AllJobItemModel) model;
            holder.optionView.setText(allJobItemModel.getTime());
        } else if (jobListType == JobListType.HotJob) {
            HotJobItemModel hotJobItemModel = (HotJobItemModel) model;
            holder.optionView.setText(hotJobItemModel.getApply() + "人申请");
        } else if (jobListType == JobListType.NearbyJob) {
            NearbyJobItemModel nearbyJobItemModel = (NearbyJobItemModel) model;
            double d = nearbyJobItemModel.getDistance();
            if (d >= 1) {
                holder.optionView.setText("距离" + String.format("%.1f" ,d) + "千米");
            } else {
                holder.optionView.setText("距离" + (int)(d * 10000) + "米");
            }
        } else if (jobListType == JobListType.Collect) {
            holder.optionView.setText("删除");
            holder.optionView.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.optionView.setTag(position);
            holder.optionView.setOnClickListener(this);
        } else if (jobListType == JobListType.Apply) {
            int s = ((AppliedJobItemModel)model).getStatus();
            if (s == 0) {
                holder.optionView.setText("已投递");
                holder.optionView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else if (s == 1) {
                holder.optionView.setText("已接受");
                holder.optionView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            } else if (s == 2) {
                holder.optionView.setText("已拒绝");
                holder.optionView.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
        }
        Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/logo/" + model.getCompanyId() + ".jpg");
        holder.logoView.setImageURI(uri);
        if (jobItemModels.size() > 9 && position == jobItemModels.size() - 1) {
            lastItemVisibleListener.loadMore();
        }
        holder.companyLayout.setTag(model.getCompanyId());
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


    class PlaceHolder extends RecyclerView.ViewHolder {

        public TextView optionView;
        public TextView companyView;
        public TextView addressView;
        public TextView jobView;
        public SimpleDraweeView logoView;
        public View cardView;
        public TextView statusView;
        public RelativeLayout companyLayout;


        public PlaceHolder(View itemView) {
            super(itemView);
            optionView = (TextView) itemView.findViewById(R.id.option);
            companyView = (TextView) itemView.findViewById(R.id.company_name);
            addressView = (TextView) itemView.findViewById(R.id.job_addr);
            jobView = (TextView) itemView.findViewById(R.id.job_name);
            logoView = (SimpleDraweeView) itemView.findViewById(R.id.logo_image);
            statusView = (TextView) itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.container);
            companyLayout = (RelativeLayout) itemView.findViewById(R.id.company);
        }


    }

    public interface OnLastItemVisibleListener {

        public void loadMore();
    }
}
