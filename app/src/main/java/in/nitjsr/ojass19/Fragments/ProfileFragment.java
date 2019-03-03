package in.nitjsr.ojass19.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import in.nitjsr.ojass19.Activity.LoginActivity;
import in.nitjsr.ojass19.Activity.RegisterActivity;
import in.nitjsr.ojass19.R;
import in.nitjsr.ojass19.Utils.Constants;
import in.nitjsr.ojass19.Utils.SharedPrefManager;

import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_NAME;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_PARTICIPATED_EVENTS;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_PARTICIPATED_EVENT_BRANCH;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_PARTICIPATED_EVENT_NAME;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_PARTICIPATED_EVENT_RESULT;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_PARTICIPATED_EVENT_TIME;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static in.nitjsr.ojass19.Utils.Constants.FIREBASE_REF_USERS;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseUser mUser;
    private TextView tvUserName, tvUserOjId, tvUserEmail, tvUserMobile, tvTshirtSize;
    private CircleImageView userImage;
    private ImageView ivTShirt, ivKit;
    private ProgressDialog pd;
    private ImageButton ibRefresh;
    private Button btnLogOut, btnRegister;
    private DatabaseReference userRef;
    private RecyclerView rvEvents;

    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_profile,container,false);

        tvUserName = view.findViewById(R.id.tv_profile_name);
        tvUserOjId = view.findViewById(R.id.tv_profile_ojass_id);
        tvUserEmail = view.findViewById(R.id.tv_profile_email);
        tvUserMobile = view.findViewById(R.id.tv_profile_number);
        userImage = view.findViewById(R.id.iv_profile_img);
        ivTShirt = view.findViewById(R.id.iv_tshirt);
        ivKit = view.findViewById(R.id.iv_kit);
        ibRefresh = view.findViewById(R.id.ib_refresh);
        btnLogOut = view.findViewById(R.id.ib_logOut);
        btnRegister = view.findViewById(R.id.btn_click_to_register);
        rvEvents = view.findViewById(R.id.rv_profile_events);
        tvTshirtSize = view.findViewById(R.id.tv_tshirt_size);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference(FIREBASE_REF_USERS).child(mUser.getUid());
        userRef.keepSynced(true);
        //Utilities.setPicassoImage(view.getContext(), mUser.getPhotoUrl().toString(), circleImageView, Constants.SQUA_PLACEHOLDER);
        pd = new ProgressDialog(getContext());
        pd.setTitle("Please Wait");
        pd.setMessage("Loading...");
        pd.show();

        tvUserEmail.setText(mUser.getEmail());

        fetchData(0);

        ibRefresh.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        return view;
    }

    private void fetchData(final int flag) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    try {
                        btnRegister.setVisibility(View.GONE);
                        tvTshirtSize.setText("Tshirt ("+ dataSnapshot.child(FIREBASE_REF_TSHIRT_SIZE).getValue().toString() + ")");
                        tvUserName.setText(dataSnapshot.child(FIREBASE_REF_NAME).getValue().toString());
                        tvUserMobile.setText("+91 "+dataSnapshot.child(Constants.FIREBASE_REF_MOBILE).getValue().toString());
                        if (dataSnapshot.child(Constants.FIREBASE_REF_OJASS_ID).getValue() != null) {
                            tvUserOjId.setText(dataSnapshot.child(Constants.FIREBASE_REF_OJASS_ID).getValue().toString());
                            tvUserOjId.setTextColor(getResources().getColor(R.color.forest_green));
                        } else {
                            tvUserOjId.setText(Constants.PAYMENT_DUE);
                            tvUserOjId.setTextColor(Color.RED);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            if (dataSnapshot.child(Constants.FIREBASE_REF_TSHIRT).exists()){
                                ivTShirt.setImageDrawable(getActivity().getDrawable(android.R.drawable.checkbox_on_background));
                            } else {
                                ivTShirt.setImageDrawable(getActivity().getDrawable(android.R.drawable.checkbox_off_background));
                            }
                            if (dataSnapshot.child(Constants.FIREBASE_REF_KIT).exists()){
                                ivKit.setImageDrawable(getActivity().getDrawable(android.R.drawable.checkbox_on_background));
                            } else {
                                ivKit.setImageDrawable(getActivity().getDrawable(android.R.drawable.checkbox_off_background));
                            }
                        } else {
                            ivTShirt.setVisibility(View.GONE);
                            ivKit.setVisibility(View.GONE);
                        }
                        if (dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENTS).exists()){
                            //prepareRecyclerView(dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENTS));
                        }
                        if (pd.isShowing()) pd.dismiss();
                        if (flag == 1){
                            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        if (pd.isShowing()) pd.dismiss();
                    }
                } else {
                    if (pd.isShowing()) pd.dismiss();
                    tvUserOjId.setText(Constants.NOT_REGISTERED);
                    tvUserOjId.setTextColor(Color.RED);
                    Toast.makeText(getContext(), Constants.NOT_REGISTERED, Toast.LENGTH_SHORT).show();
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void prepareRecyclerView(DataSnapshot child) {
//        LinearLayoutManager ll = new LinearLayoutManager(rvEvents.getContext(), LinearLayoutManager.HORIZONTAL, false);
//        rvEvents.setHasFixedSize(true);
//        rvEvents.setLayoutManager(ll);
//        ArrayList<Modal> modals = new ArrayList<>();
//        int count = 0;
//        for (DataSnapshot dataSnapshot : child.getChildren()){
//            modals.add(new Modal(
//                    getImage(dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENT_BRANCH).getValue().toString()),
//                    dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENT_NAME).getValue().toString(),
//                    dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENT_RESULT).getValue().toString(),
//                    dataSnapshot.child(FIREBASE_REF_PARTICIPATED_EVENT_TIME).getValue().toString()
//            ));
//            count++;
//        }
//        float per = (float) (Math.round(((count * 100) / 81.0) *100) / 100.0); //Rounding off to 2-decimal places, Don't worry :)
//        tvPercentage.setText(""+per+"%");
//        if (modals.size() != 0){
//            Collections.sort(modals, new ProfileEventComparator());
//            rvEvents.setAdapter(new ProfileEventAdapter(rvEvents.getContext(), modals));
//        }
//    }

    @Override
    public void onClick(View view) {
        if (view == ibRefresh) {
            pd.show();
            fetchData(1);
        } else if (view == btnLogOut) {
            signOut();
        } else if (view == btnRegister){
            startActivity(new Intent(getContext(), RegisterActivity.class));
            getActivity().finish();
        }
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mGoogleSignInClient.signOut();
        FirebaseAuth.getInstance().signOut();
        SharedPrefManager shared = new SharedPrefManager(getContext());
        shared.setIsLoggedIn(false);
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finishAffinity();
    }
}
