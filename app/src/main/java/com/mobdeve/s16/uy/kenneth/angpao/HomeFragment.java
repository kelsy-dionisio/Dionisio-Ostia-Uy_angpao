package com.mobdeve.s16.uy.kenneth.angpao;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton btnAddAngpao, amountBtn, frequencyBtn;
    ShareButton sbPhoto;
    TextView amountTitle, frequencyTitle;
    TextView p1Title, p1Name, p1Desc;
    TextView p2Title, p2Name, p2Desc;
    TextView p3Title, p3Name, p3Desc;

    TextView totalText;
    LinearLayout linearLayout2;
    ImageView podium;
    ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private LoginManager loginManager;
    private static final String EMAIL = "email";
    Bitmap screenshot;
    String[] required_permissions  = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES
    };

    boolean is_storage_image_permitted = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());

        totalText = view.findViewById(R.id.totalText);

        btnAddAngpao = view.findViewById(R.id.btnAddAngpao);
        amountBtn = view.findViewById(R.id.amountBtn);
        frequencyBtn = view.findViewById(R.id.frequencyBtn);
        amountTitle = view.findViewById(R.id.amountTitle);
        frequencyTitle = view.findViewById(R.id.freqTitle);
        linearLayout2 = view.findViewById(R.id.linearLayout2);

        p1Name=view.findViewById(R.id.p1Name);
        p2Name=view.findViewById(R.id.p2Name);
        p3Name=view.findViewById(R.id.p3Name);

        p1Desc=view.findViewById(R.id.p1Desc);
        p2Desc=view.findViewById(R.id.p2Desc);
        p3Desc=view.findViewById(R.id.p3Desc);

        p1Title=view.findViewById(R.id.p1Title);
        p2Title=view.findViewById(R.id.p2Title);
        p3Title=view.findViewById(R.id.p3Title);

        podium=view.findViewById(R.id.imageView);
        sbPhoto=view.findViewById(R.id.shareBtn);

        //facebook api
        callbackManager = CallbackManager.Factory.create();
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setPermissions(Arrays.asList("email","public_profile", "user_photos"));
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_photos"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            sbPhoto.setEnabled(true);
        } else {
            sbPhoto.setEnabled(false);
        }

        if(!is_storage_image_permitted){
            requestPermissionStorageImages();
        }

        sbPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenshot = captureScreenShot(container);

                shareDialog = new ShareDialog(HomeFragment.this);
                if (ShareDialog.canShow(SharePhotoContent.class)) {
//                    BitmapDrawable image2 = (BitmapDrawable) podium.getDrawable();
//                    Bitmap image = image2.getBitmap();

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(screenshot)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .setShareHashtag(new ShareHashtag.Builder().setHashtag("#angpao").build())
                            .build();
//            ShareDialog.show(this, content);
//            MessageDialog.show(this, content);
                    sbPhoto.setShareContent(content);
                }
            }
        });




        int totalAngpaoAmount = (myDB.getAccumulatedAngpaoAmount());
        if(totalAngpaoAmount <=0){
            totalText.setText("Bankrupt :(");
        }else {
            totalText.setText("₱" + String.valueOf(myDB.getAccumulatedAngpaoAmount()));
        }

        btnAddAngpao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddAngpaoActivity.class);
                intent.putExtra("ANGPAO_STATUS", "add");
                getActivity().startActivity(intent);
            }
        });

        // Set the active button to amountBtn
        setAmountButtonClickFunctionality();

        // Set the click listener for amountBtn
        amountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(amountBtn, frequencyBtn);
                setAmountButtonClickFunctionality();
            }
        });

        // Set the click listener for frequencyBtn
        frequencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(frequencyBtn, amountBtn);
                setFrequencyButtonClickFunctionality();
            }
        });

        return view;
    }

    public Bitmap captureScreenShot(View view){
        Bitmap returnBitmap=Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgdrawable = view.getBackground();
        if(bgdrawable!=null){
            bgdrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnBitmap;
    }

    public void requestPermissionStorageImages(){
        if (ContextCompat.checkSelfPermission(getContext(), required_permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            is_storage_image_permitted=true;
        } else {

        }
    }

    public void onResume() {
        super.onResume();

        // Reload all the data when the fragment is resumed
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());

        int totalAngpaoAmount = myDB.getAccumulatedAngpaoAmount();
        if (totalAngpaoAmount <= 0) {
            totalText.setText("Bankrupt :(");
        } else {
            totalText.setText("₱" + String.valueOf(myDB.getAccumulatedAngpaoAmount()));
        }

        // Reload the amountBtn functionality
        setAmountButtonClickFunctionality();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            sbPhoto.setEnabled(true);
        } else {
            sbPhoto.setEnabled(false);
        }
    }


    // Method to set the active button
    private void setActiveButton(ImageButton activeButton, ImageButton inactiveButton) {
        activeButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.yellow)));
        activeButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)));
        inactiveButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
        inactiveButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.yellow)));
        frequencyTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.darkgray)));
        amountTitle.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.yellow)));
    }

    // Method to set the functionality for amountBtn
    private void setAmountButtonClickFunctionality() {
        // Get the accumulated amounts for each Ninong
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
        List<NinongAccumulatedAmount> ninongAccumulatedAmounts = myDB.getNinongAccumulatedAmounts();
        linearLayout2.removeAllViews();

        // Display up to 3 Ninongs, fill in with N/A if fewer than 3
        for (int i = 0; i < 3; i++) {
            if (i < ninongAccumulatedAmounts.size()) {
                // If there is a Ninong at this index
                switch (i) {
                    case 0:
                        p1Title.setText(ninongAccumulatedAmounts.get(i).getPrefix());
                        p1Name.setText(ninongAccumulatedAmounts.get(i).getName());
                        p1Desc.setText("₱" + ninongAccumulatedAmounts.get(i).getAccumulatedAmount());
                        break;
                    case 1:
                        p2Title.setText(ninongAccumulatedAmounts.get(i).getPrefix());
                        p2Name.setText(ninongAccumulatedAmounts.get(i).getName());
                        p2Desc.setText("₱" + ninongAccumulatedAmounts.get(i).getAccumulatedAmount());
                        break;
                    case 2:
                        p3Title.setText(ninongAccumulatedAmounts.get(i).getPrefix());
                        p3Name.setText(ninongAccumulatedAmounts.get(i).getName());
                        p3Desc.setText("₱" + ninongAccumulatedAmounts.get(i).getAccumulatedAmount());
                        break;
                }
            } else {
                // If there is no Ninong at this index
                switch (i) {
                    case 0:
                        p1Title.setText("N/A");
                        p1Name.setText("N/A");
                        p1Desc.setText("₱0");
                        break;
                    case 1:
                        p2Title.setText("N/A");
                        p2Name.setText("N/A");
                        p2Desc.setText("₱0");
                        break;
                    case 2:
                        p3Title.setText("N/A");
                        p3Name.setText("N/A");
                        p3Desc.setText("₱0");
                        break;
                }
            }
        }
        displayRemainingNinongsAmount(ninongAccumulatedAmounts);
    }

    private void setFrequencyButtonClickFunctionality() {
        // Get the accumulated times for each Ninong
        MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
        List<NinongAccumulatedTimes> ninongAccumulatedTimesList = myDB.getNinongAccumulatedTimes();
        linearLayout2.removeAllViews();

        // Display up to 3 Ninongs, fill in with N/A if fewer than 3
        for (int i = 0; i < 3; i++) {
            if (i < ninongAccumulatedTimesList.size()) {
                // If there is a Ninong at this index
                switch (i) {
                    case 0:
                        p1Title.setText(ninongAccumulatedTimesList.get(i).getType());
                        p1Name.setText(ninongAccumulatedTimesList.get(i).getName());
                        p1Desc.setText(String.valueOf(ninongAccumulatedTimesList.get(i).getAccumulatedTimes()) + "x");
                        break;
                    case 1:
                        p2Title.setText(ninongAccumulatedTimesList.get(i).getType());
                        p2Name.setText(ninongAccumulatedTimesList.get(i).getName());
                        p2Desc.setText(String.valueOf(ninongAccumulatedTimesList.get(i).getAccumulatedTimes())+ "x");
                        break;
                    case 2:
                        p3Title.setText(ninongAccumulatedTimesList.get(i).getType());
                        p3Name.setText(ninongAccumulatedTimesList.get(i).getName());
                        p3Desc.setText(String.valueOf(ninongAccumulatedTimesList.get(i).getAccumulatedTimes())+ "x");
                        break;
                }
            } else {
                // If there is no Ninong at this index
                switch (i) {
                    case 0:
                        p1Title.setText("N/A");
                        p1Name.setText("N/A");
                        p1Desc.setText("0x");
                        break;
                    case 1:
                        p2Title.setText("N/A");
                        p2Name.setText("N/A");
                        p2Desc.setText("0x");
                        break;
                    case 2:
                        p3Title.setText("N/A");
                        p3Name.setText("N/A");
                        p3Desc.setText("0x");
                        break;
                }
            }
        }
        displayRemainingNinongsFrequency(ninongAccumulatedTimesList);
    }

    private void displayRemainingNinongsAmount(List<NinongAccumulatedAmount> ninongAccumulatedAmounts) {

        for (int i = 3; i < ninongAccumulatedAmounts.size(); i++) {
            View ninongView = LayoutInflater.from(getContext()).inflate(R.layout.remaining_ninong_item, linearLayout2, false);

            TextView placeTxt = ninongView.findViewById(R.id.placeTxt3);
            TextView nameTxt = ninongView.findViewById(R.id.nameTxt3);
            TextView descTxt = ninongView.findViewById(R.id.descTxt3);

            placeTxt.setText(i+1+".");
            nameTxt.setText(ninongAccumulatedAmounts.get(i).getPrefix() + " " +ninongAccumulatedAmounts.get(i).getName());
            descTxt.setText("₱" + ninongAccumulatedAmounts.get(i).getAccumulatedAmount());

            linearLayout2.addView(ninongView);
        }
    }

    private void displayRemainingNinongsFrequency(List<NinongAccumulatedTimes> ninongAccumulatedTimes) {

        for (int i = 3; i < ninongAccumulatedTimes.size(); i++) {
            View ninongView = LayoutInflater.from(getContext()).inflate(R.layout.remaining_ninong_item, linearLayout2, false);

            TextView placeTxt = ninongView.findViewById(R.id.placeTxt3);
            TextView nameTxt = ninongView.findViewById(R.id.nameTxt3);
            TextView descTxt = ninongView.findViewById(R.id.descTxt3);

            placeTxt.setText(i+1+".");
            nameTxt.setText(ninongAccumulatedTimes.get(i).getType() + " " +ninongAccumulatedTimes.get(i).getName());
            descTxt.setText(ninongAccumulatedTimes.get(i).getAccumulatedTimes() + "x");

            linearLayout2.addView(ninongView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FacebookShare", "onActivityResult called");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}