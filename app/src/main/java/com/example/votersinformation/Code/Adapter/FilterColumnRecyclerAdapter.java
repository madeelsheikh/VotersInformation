package com.example.votersinformation.Code.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.votersinformation.Code.DataObjects.FilterColumn;
import com.example.votersinformation.R;

import java.util.ArrayList;
import java.util.List;

public class FilterColumnRecyclerAdapter extends
        RecyclerView.Adapter<FilterColumnRecyclerAdapter.MyViewHolder>
        implements Filterable {

    //region interface

    public interface FilterColumnAdapterListener {
        void onFilterColumnSelected(FilterColumn filterColumn);
    }

    //endregion

    //region field(s)

    private Context context;
    private List<FilterColumn> mFilterColumn;
    private List<FilterColumn> mFilterColumnFiltered;
    private FilterColumnRecyclerAdapter.FilterColumnAdapterListener listener;

    //endregion

    //region constructor(s)

    public FilterColumnRecyclerAdapter(Context context, List<FilterColumn> filterColumnList, FilterColumnRecyclerAdapter.FilterColumnAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.mFilterColumn = filterColumnList;
        this.mFilterColumnFiltered = filterColumnList;
    }

    //endregion

    //region viewholder class

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public Button details;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_generic_column_name);
            details = view.findViewById(R.id.btn_generic_column_id);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onFilterColumnSelected(mFilterColumnFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    //endregion

    //region overridde

    @Override
    public FilterColumnRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.i_rv_s_c_d, parent, false);

        return new FilterColumnRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterColumnRecyclerAdapter.MyViewHolder holder, final int position) {
        final FilterColumn filterColumn = mFilterColumnFiltered.get(position);

        holder.details.setId(filterColumn.getId());

        RelativeLayout.LayoutParams lp_name = (RelativeLayout.LayoutParams) holder.name.getLayoutParams();
        lp_name.addRule(RelativeLayout.RIGHT_OF, holder.details.getId());

        holder.name.setLayoutParams(lp_name);

        holder.name.setText(filterColumn.getName());
    }

    @Override
    public int getItemCount() {
        return mFilterColumnFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilterColumnFiltered = mFilterColumn;
                } else {
                    List<FilterColumn> filteredList = new ArrayList<>();
                    for (FilterColumn row : mFilterColumn) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilterColumnFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilterColumnFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilterColumnFiltered = (ArrayList<FilterColumn>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //endregion
}