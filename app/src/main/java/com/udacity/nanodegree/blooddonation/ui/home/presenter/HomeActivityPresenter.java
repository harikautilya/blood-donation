package com.udacity.nanodegree.blooddonation.ui.home.presenter;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.nanodegree.blooddonation.R;
import com.udacity.nanodegree.blooddonation.constants.FireBaseConstants;
import com.udacity.nanodegree.blooddonation.data.model.ReceiverDonorRequestType;
import com.udacity.nanodegree.blooddonation.data.model.User;
import com.udacity.nanodegree.blooddonation.ui.home.HomeActivityContract;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ankush Grover(ankushgrover02@gmail.com) on 23/04/2018.
 */
public class HomeActivityPresenter
        implements HomeActivityContract.Presenter {

    private final HomeActivityContract.View mView;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseDatabase mFireBaseDataBase;

    private ValueEventListener mUserDetailsValueEventListener;
    private ValueEventListener mUserReceiverValueEventListener;
    private ValueEventListener mUserDonorValueEventListener;
    private ValueEventListener mDonorLocationListener;
    private ValueEventListener mReceiverLocationListener;

    private DatabaseReference mDonorLocationRefernce;
    private DatabaseReference mReceiverLocationRefernce;
    private DatabaseReference mUserDetailDabaseReference;
    private DatabaseReference mUserRequestDetailsDatabaseRef;
    private DatabaseReference mUserDonortDetailsDatabaseRef;


    private User mUser;

    private GeoFire geoFireReceiver;
    private GeoFire geoFireDonor;

    private float radius;


    public HomeActivityPresenter(HomeActivityContract.View view, FirebaseAuth firebaseAuth,
                                 FirebaseDatabase firebaseDatabase) {
        this.mView = view;
        mFireBaseAuth = firebaseAuth;
        mFireBaseDataBase = firebaseDatabase;
    }

    @Override
    public void onCurrentLocationClicked() {

        mView.fetchCurrentLocation();
    }

    @Override
    public void onAddClicked() {
        if (mUser == null)
            mView.generalResponse(R.string.msg_please_wait);
        else
            mView.openCreateRequestDialog(mUser);
    }

    @Override
    public void onCreate() {

        createGeoFire();
        createUserDetailsValueEventListener();
        createUserRequestDetailsValueEventListener();
        createUserDonroDeteilsValueEventListener();

        radius = 0.6f;
        mUserDetailDabaseReference = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.USERS)
                .child(mFireBaseAuth.getUid());

        mUserRequestDetailsDatabaseRef = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.RECEIVER);

        mUserDonortDetailsDatabaseRef = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.DONOR);

        mUserDetailDabaseReference.addListenerForSingleValueEvent(mUserDetailsValueEventListener);


        mDonorLocationRefernce = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.GEOFIRE_DONOR);

        mReceiverLocationRefernce = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.GEOFIRE_RECEIVER);

        mDonorLocationRefernce.addValueEventListener(mDonorLocationListener);
        mReceiverLocationRefernce.addValueEventListener(mReceiverLocationListener);
        mUserRequestDetailsDatabaseRef.addValueEventListener(mUserReceiverValueEventListener);
        mUserDonortDetailsDatabaseRef.addValueEventListener(mUserDonorValueEventListener);

    }


    private void createGeoFire() {


        geoFireReceiver = new GeoFire(FirebaseDatabase.getInstance().getReference(FireBaseConstants.GEOFIRE_RECEIVER));
        geoFireDonor = new GeoFire(FirebaseDatabase.getInstance().getReference(FireBaseConstants.GEOFIRE_DONOR));
        mDonorLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sendDonorQuery();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReceiverLocationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sendReceiverQuery();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void sendReceiverQuery() {
        geoFireDonor.queryAtLocation(new GeoLocation(mView.getCurrentLocation().getLatitude(), mView.getCurrentLocation().getLongitude()), radius).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void sendDonorQuery() {
        geoFireDonor.queryAtLocation(new GeoLocation(mView.getCurrentLocation().getLatitude(), mView.getCurrentLocation().getLongitude()), radius).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createUserDetailsValueEventListener() {
        mUserDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void createUserDonroDeteilsValueEventListener() {
        mUserDonorValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReceiverDonorRequestType> receiverDonorRequestTypeList = null;
                if (dataSnapshot.exists()) {
                    receiverDonorRequestTypeList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        receiverDonorRequestTypeList.add(dataSnapshot1.getValue(ReceiverDonorRequestType.class));
                    }
                }
                updateCameraAfterGettingUserRequestDetails(false, receiverDonorRequestTypeList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateCameraAfterGettingUserRequestDetails(false, null);
            }
        };
    }

    private void createUserRequestDetailsValueEventListener() {
        mUserReceiverValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ReceiverDonorRequestType> receiverDonorRequestTypeList = null;
                if (dataSnapshot.exists()) {
                    receiverDonorRequestTypeList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        receiverDonorRequestTypeList.add(dataSnapshot1.getValue(ReceiverDonorRequestType.class));
                    }
                }
                updateCameraAfterGettingUserRequestDetails(true, receiverDonorRequestTypeList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateCameraAfterGettingUserRequestDetails(true, null);
            }
        };
    }


    private void updateCameraAfterGettingUserRequestDetails(boolean isReceiver, List<ReceiverDonorRequestType> receiverDonorRequestTypeList) {
        if (receiverDonorRequestTypeList != null) {
            mView.showHideLoader(false);
            for (ReceiverDonorRequestType receiverDonorRequestType : receiverDonorRequestTypeList) {
                if (receiverDonorRequestType != null) {
                    if (isReceiver) {
                        mView.addRequestMarker(receiverDonorRequestType);
                    } else {
                        mView.addDonorMarker(receiverDonorRequestType);
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mUserDetailDabaseReference.removeEventListener(mUserDetailsValueEventListener);
        mUserRequestDetailsDatabaseRef.removeEventListener(mUserReceiverValueEventListener);
        mDonorLocationRefernce.removeEventListener(mDonorLocationListener);
        mReceiverLocationRefernce.removeEventListener(mReceiverLocationListener);
        mUserRequestDetailsDatabaseRef.removeEventListener(mUserReceiverValueEventListener);
        mUserDonortDetailsDatabaseRef.removeEventListener(mUserDonorValueEventListener);

    }

    @Override
    public void onBloodRequest(ReceiverDonorRequestType requestDetails) {

        geoFireReceiver.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(requestDetails.getLocation().getLatitude(), requestDetails.getLocation().getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error == null) {
                    Timber.d("Success");
                } else {
                    Timber.e(error.getMessage());
                }

            }
        });

    }

    @Override
    public void onDonateRequest(ReceiverDonorRequestType requestDetails) {
        geoFireDonor.setLocation(FirebaseAuth.getInstance().getUid(), new GeoLocation(requestDetails.getLocation().getLatitude(), requestDetails.getLocation().getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error == null) {
                    Timber.d("Success");
                } else {
                    Timber.e(error.getMessage());
                }

            }
        });

    }

}
