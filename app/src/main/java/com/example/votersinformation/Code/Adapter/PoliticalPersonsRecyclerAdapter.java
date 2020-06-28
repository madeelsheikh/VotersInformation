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

import com.example.votersinformation.Code.DataObjects.PoliticalPersonsWithAffiliation;
import com.example.votersinformation.R;

import java.util.ArrayList;
import java.util.List;

public class PoliticalPersonsRecyclerAdapter extends
        RecyclerView.Adapter<PoliticalPersonsRecyclerAdapter.MyViewHolder>
        implements Filterable {

    //region interface

    public interface PoliticalPersonsAffiliationAdapterListener {
        void onPoliticalPersonWithAffiliationSelected(PoliticalPersonsWithAffiliation politicalPersonWithAffiliation);
    }

    //endregion

    //region field(s)

    private Context context;
    private List<PoliticalPersonsWithAffiliation> mPoliticalPersonAffiliation;
    private List<PoliticalPersonsWithAffiliation> mPoliticalPersonAffiliationFiltered;
    private PoliticalPersonsAffiliationAdapterListener listener;

    //endregion

    //region constructor(s)

    public PoliticalPersonsRecyclerAdapter(Context context, List<PoliticalPersonsWithAffiliation> politicalPersonAffiliationList, PoliticalPersonsAffiliationAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.mPoliticalPersonAffiliation = politicalPersonAffiliationList;
        this.mPoliticalPersonAffiliationFiltered = politicalPersonAffiliationList;
    }

    //endregion

    //region viewholder

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, alive, dead;
        public Button details;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_political_person_name);
            alive = view.findViewById(R.id.tv_affiliated_alive_person);
            dead = view.findViewById(R.id.tv_dead_person);
            details = view.findViewById(R.id.btn_political_person_detail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onPoliticalPersonWithAffiliationSelected(mPoliticalPersonAffiliationFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    //endregion

    //region override

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.i_rv_pp, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final PoliticalPersonsWithAffiliation politicalPersonWithAffiliation = mPoliticalPersonAffiliationFiltered.get(position);

        holder.details.setId(politicalPersonWithAffiliation.getId());

        RelativeLayout.LayoutParams lp_name = (RelativeLayout.LayoutParams) holder.name.getLayoutParams();
        lp_name.addRule(RelativeLayout.RIGHT_OF, holder.details.getId());

        RelativeLayout.LayoutParams lp_alive = (RelativeLayout.LayoutParams) holder.alive.getLayoutParams();
        lp_alive.addRule(RelativeLayout.RIGHT_OF, holder.details.getId());

        holder.name.setLayoutParams(lp_name);
        holder.alive.setLayoutParams(lp_alive);

        holder.name.setText(politicalPersonWithAffiliation.getName());
        holder.alive.setText("Alive: " + String.valueOf(politicalPersonWithAffiliation.getAlive()));
        holder.dead.setText("Dead: " + String.valueOf(politicalPersonWithAffiliation.getDead()));

    }

    @Override
    public int getItemCount() {
        return mPoliticalPersonAffiliationFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mPoliticalPersonAffiliationFiltered = mPoliticalPersonAffiliation;
                } else {
                    List<PoliticalPersonsWithAffiliation> filteredList = new ArrayList<>();
                    for (PoliticalPersonsWithAffiliation row : mPoliticalPersonAffiliation) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mPoliticalPersonAffiliationFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mPoliticalPersonAffiliationFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mPoliticalPersonAffiliationFiltered = (ArrayList<PoliticalPersonsWithAffiliation>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //endregion
}
