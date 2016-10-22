package com.example.proto.prototype_orange;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.TypedArrayUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FilterMenu extends Fragment {

    private RadioGroup radioGroup;
    private String[] filterItems;
    private OnFragmentInteractionListener mListener;

    public FilterMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.filter_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        radioGroup = (RadioGroup) getView().findViewById(R.id.filterGroup);
        filterItems = getResources().getStringArray(R.array.filter_options);
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("com.example.proto.prototype_orange", Context.MODE_PRIVATE);

        for (String s: filterItems) {
            Switch switchView = new Switch(this.getActivity());
            switchView.setText(s);
            switchView.setMinWidth(600);
            switchView.setSwitchPadding(70);
            switchView.setChecked(sharedPrefs.getBoolean(s, false));
            radioGroup.addView(switchView);
        }
        filterItems = getResources().getStringArray(R.array.diet_options);
        for (String s: filterItems) {
            Switch switchView = new Switch(this.getActivity());
            switchView.setText(s);
            switchView.setMinWidth(600);
            switchView.setSwitchPadding(70);
            switchView.setChecked(sharedPrefs.getBoolean(s, false));
            radioGroup.addView(switchView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        View s;
        filterItems = getResources().getStringArray(R.array.filter_options);
        String[] test = getResources().getStringArray(R.array.filter_options);
        String[] test2 = getResources().getStringArray(R.array.diet_options);

        ArrayList<String> filterList = new ArrayList<String>();
        List<String> filter = Arrays.asList(test);
        filterList.addAll(filter);
        filter = Arrays.asList(test2);
        filterList.addAll(filter);

        for(int i = 0; i < radioGroup.getChildCount(); i++){
            s = radioGroup.getChildAt(i);

            if (s instanceof Switch){
                if(((Switch) s).isChecked() == true){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.example.proto.prototype_orange", Context.MODE_PRIVATE).edit();
                    editor.putBoolean(filterList.get(i),true);
                    editor.commit();
                }else{
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.example.proto.prototype_orange", Context.MODE_PRIVATE).edit();
                    editor.putBoolean(filterList.get(i),false);
                    editor.commit();
                }
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
