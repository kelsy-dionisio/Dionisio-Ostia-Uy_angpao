package com.mobdeve.s16.uy.kenneth.angpao;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button deleteHistoryBtn;
    private ArrayList<HistoryData> history;
    private MyDatabaseHelper myDB;
    private RecyclerView recyclerView;
    private AlertDialog dialog;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
//        history = (ArrayList<HistoryData>) myDB.getAllAngpaos();
//
//
//        RecyclerView recyclerView  = view.findViewById(R.id.recyclerView1);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new HistoryAdapter(getContext(), history));

        myDB = new MyDatabaseHelper(getActivity());
        history = (ArrayList<HistoryData>) myDB.getAllAngpaos();

        recyclerView  = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), history);
        historyAdapter.setOnItemClickListener(new HistoryAdapter.AdapterOnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // Handle edit click
                HistoryData selectedHistory = history.get(position);
                Intent intent = new Intent(requireContext(), AddAngpaoActivity.class);
                intent.putExtra("ANGPAO_ID_KEY", selectedHistory.getAngpaoId());
                intent.putExtra("ANGPAO_NINONG_TYPE_KEY", selectedHistory.getNinongPrefix());
                intent.putExtra("ANGPAO_NINONG_NAME_KEY", selectedHistory.getNinongName());
                intent.putExtra("ANGPAO_AMOUNT_KEY", selectedHistory.getAmount());
                intent.putExtra("ANGPAO_DATE_KEY", selectedHistory.getDate());
                intent.putExtra("ANGPAO_STATUS", "edit");
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                showDeleteConfirmationDialog(position);
            }
        });

        recyclerView.setAdapter(historyAdapter);
    }

    public void onResume() {
        super.onResume();

        // Reload data when the fragment resumes
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
        history = (ArrayList<HistoryData>) myDB.getAllAngpaos();

        recyclerView = requireView().findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new HistoryAdapter(getContext(), history));
    }

    private void showDeleteConfirmationDialog(int position) {
        // Your existing code for the dialog creation
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

        // Set up your dialog elements and buttons here
        TextView message = dialogView.findViewById(R.id.messageText);
        Button confirmButton = dialogView.findViewById(R.id.dialog_confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_cancel_button);

        // Set the message
        message.setText("Are you sure you want to delete this item?");

        // Set up the Confirm button click listener
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call deleteAngpao method here using the position parameter
                int result = myDB.deleteAngpao(history.get(position).getAngpaoId());
                if (result > 0) {
                    Toast.makeText(requireContext(), "Angpao deleted", Toast.LENGTH_SHORT).show();
                    // Update the RecyclerView data after deletion
                    history.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                } else {
                    Toast.makeText(requireContext(), "Failed to delete Angpao", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        // Set up the Cancel button click listener
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();
    }
}