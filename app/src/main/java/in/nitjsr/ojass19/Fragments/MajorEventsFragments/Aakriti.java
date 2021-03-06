package in.nitjsr.ojass19.Fragments.MajorEventsFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import in.nitjsr.ojass19.Adapters.SubEventsAdapter;
import in.nitjsr.ojass19.R;

import static in.nitjsr.ojass19.Utils.Constants.EventList;

public class Aakriti extends Fragment {
    List<String> subEvents = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aakriti,container,false);
        RecyclerView rView = view.findViewById(R.id.rview);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        rView.setLayoutManager(lm);

        SubEventsAdapter mAdapter = new SubEventsAdapter(getSubEvents());
        rView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation));
        mAdapter.notifyDataSetChanged();
        rView.scheduleLayoutAnimation();
        rView.setAdapter(mAdapter);
        return view;
    }
    List<String> getSubEvents(){
        subEvents.clear();
        for(int i = 0;i<EventList[5].length;i++){
            subEvents.add(EventList[5][i]);
        }
        return subEvents;
    }
}