package cc.coocol.jinxiujob.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.coocol.jinxiujob.R;
import cc.coocol.jinxiujob.enums.JobListType;
import cc.coocol.jinxiujob.models.BaseJobItemModel;

/**
 * Created by raymond on 16-2-28.
 */
public class AppliedListAdapter extends RecyclerView.Adapter<AppliedListAdapter.PlaceHolder> {

    private Context context;
    private List<BaseJobItemModel> jobItemModels;

    public AppliedListAdapter(Context context, List<BaseJobItemModel> jobItemModels) {
        this.context = context;
        this.jobItemModels = jobItemModels;
    }

    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applied_list,parent, false);
        PlaceHolder placeHolder = new PlaceHolder(view);
        return placeHolder;
    }

    @Override
    public void onBindViewHolder(PlaceHolder holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return jobItemModels.size();
    }


    class PlaceHolder extends RecyclerView.ViewHolder {

        public TextView timeView;
        public TextView statusView;
        public TextView jobView;
        public TextView companyView;

        public PlaceHolder(View itemView) {
            super(itemView);
            timeView = (TextView) itemView.findViewById(R.id.apply_time);
            statusView = (TextView) itemView.findViewById(R.id.apply_status);
            jobView = (TextView) itemView.findViewById(R.id.job_name);
            companyView = (TextView) itemView.findViewById(R.id.company);
        }
    }
}
