package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.models.AllJobItemModel;
import cc.coocol.jinxiujob.models.AppliedJobItemModel;
import cc.coocol.jinxiujob.models.BaseJobItemModel;
import cc.coocol.jinxiujob.models.HotJobItemModel;
import cc.coocol.jinxiujob.models.NearbyJobItemModel;
import cc.coocol.jinxiujob.models.NotificationItemModel;

/**
 * Created by raymond on 16-2-28.
 */
public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context context;

    private List<NotificationItemModel> itemModels;


    private OnLastItemVisibleListener lastItemVisibleListener;


    public NotificationListAdapter(Context context, List<NotificationItemModel> itemModels
                                  , OnLastItemVisibleListener lastItemVisibleListener) {
        this.lastItemVisibleListener = lastItemVisibleListener;
        this.context = context;
        this.itemModels = itemModels;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_noti_new_job, parent, false);
            NewJobPlaceHolder placeHolder = new NewJobPlaceHolder(view);
            placeHolder.companyLayout.setOnClickListener(this);
            placeHolder.cardView.setOnClickListener(this);
            placeHolder.deleteView.setOnClickListener(this);
            return placeHolder;
        } else if (viewType == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_noti_apply_status, parent, false);
            JobStatusPlaceHolder placeHolder = new JobStatusPlaceHolder(view);
            placeHolder.deleteView.setOnClickListener(this);
            placeHolder.jobView.setOnClickListener(this);
            return placeHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NotificationItemModel model = itemModels.get(position);
        if (model.getType() == 1) {
            NewJobPlaceHolder placeHolder = (NewJobPlaceHolder) holder;
            placeHolder.timeView.setText(model.getTime());
            placeHolder.companyView.setText(model.getCompanyName());
            placeHolder.jobView.setText(model.getJobName());
            Uri uri = Uri.parse("http://115.28.22.98:7652/api/v1.0/static/logo/" + model.getCompanyId() + ".jpg");
            placeHolder.logoView.setImageURI(uri);
            placeHolder.companyLayout.setTag(model.getCompanyId());
            placeHolder.cardView.setTag(model.getJobId());
            placeHolder.deleteView.setTag(position);

        } else if (model.getType() == 2) {
            JobStatusPlaceHolder placeHolder = (JobStatusPlaceHolder) holder;
            placeHolder.timeView.setText(model.getTime());
            placeHolder.jobView.setText(model.getJobName());
            if (model.getStatus() == 1) {
                placeHolder.statusView.setText("已接受");
                placeHolder.tipView.setText("请等待企业电话或邮件");
            } else if (model.getStatus() == 2) {
                placeHolder.statusView.setText("已拒绝");
                placeHolder.tipView.setText("对方回绝了你的申请");
                placeHolder.statusView.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }
            placeHolder.deleteView.setTag(position);
            placeHolder.jobView.setTag(model.getJobId());
        }
        if (itemModels.size() > 9 && position == itemModels.size() - 1) {
            lastItemVisibleListener.loadMore();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (itemModels == null) {
            return 1;
        }
        return itemModels.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (itemModels == null || itemModels.size() == 0) {
            return 0;
        }
        return itemModels.get(position).getType();
    }

    class JobStatusPlaceHolder extends RecyclerView.ViewHolder {

        public TextView timeView;
        public TextView statusView;
        public TextView jobView;
        public TextView tipView;
        public TextView deleteView;

        public JobStatusPlaceHolder(View itemView) {
            super(itemView);
            jobView = (TextView) itemView.findViewById(R.id.a_job);
            statusView = (TextView) itemView.findViewById(R.id.a_status);
            tipView = (TextView) itemView.findViewById(R.id.a_tip);
            timeView = (TextView) itemView.findViewById(R.id.a_time);
            deleteView = (TextView) itemView.findViewById(R.id.a_delete);
        }
    }

    class NewJobPlaceHolder extends RecyclerView.ViewHolder {

        public TextView timeView;
        public TextView companyView;
        public TextView jobView;
        public SimpleDraweeView logoView;
        public View cardView;
        public TextView deleteView;
        public RelativeLayout companyLayout;


        public NewJobPlaceHolder(View itemView) {
            super(itemView);
            timeView = (TextView) itemView.findViewById(R.id.n_time);
            companyView = (TextView) itemView.findViewById(R.id.n_company);
            jobView = (TextView) itemView.findViewById(R.id.n_job);
            logoView = (SimpleDraweeView) itemView.findViewById(R.id.n_logo);
            cardView = itemView.findViewById(R.id.container);
            deleteView = (TextView) itemView.findViewById(R.id.n_delete);
            companyLayout = (RelativeLayout) itemView.findViewById(R.id.company_rl);
        }


    }

    public interface OnLastItemVisibleListener {

        public void loadMore();
    }
}
