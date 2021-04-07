package com.appcare.followconnect.MyviewPostdisplay;

import android.app.Activity;
import android.content.Context;

import com.appcare.followconnect.Common.Constants;
import com.appcare.followconnect.MyviewPostdisplay.bean.GetPostFeedResponse;
import com.appcare.followconnect.MyviewPostdisplay.bean.GetPostRequestBean;
import com.appcare.followconnect.Network.APIInterface;
import com.appcare.followconnect.Network.APIResponse;
import com.appcare.followconnect.Network.RequestClient;
import com.appcare.followconnect.R;

import retrofit2.Call;
import retrofit2.Callback;

public class MyviewPresenter {

    Context mcontext;
    APIResponse apiResponse;
    public MyviewPresenter(Context mContext, APIResponse apiResponse) {
        this.mcontext = mContext;
        this.apiResponse = apiResponse;
    }

    public void getFeedList(GetPostRequestBean bean) {


        ((Activity) mcontext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    apiResponse.showProgress();
                }
            });

            try {
                if (Constants.isNetworkAvailable(mcontext)) {
                    Call<GetPostFeedResponse> call = RequestClient.getClient().create(APIInterface.class).getFeedresponse(bean);
                    call.enqueue(new Callback<GetPostFeedResponse>() {
                        @Override
                        public void onResponse(Call<GetPostFeedResponse> call, retrofit2.Response<GetPostFeedResponse> response) {
                            try {
                                GetPostFeedResponse profileBeanResponse = response.body();
                                apiResponse.dismissProgress();
                                if (profileBeanResponse.getStatus()) {
                                    apiResponse.onSuccess(profileBeanResponse);
                                } else {
                                    apiResponse.onServerError(profileBeanResponse.getMessage());
                                }

                            } catch (Exception e) {
                                apiResponse.dismissProgress();
                                apiResponse.onServerError(mcontext.getResources().getString(R.string.server_error));
                            }
                        }

                        @Override
                        public void onFailure(Call<GetPostFeedResponse> call, Throwable t) {
                            call.cancel();

                            ((Activity) mcontext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (apiResponse != null) {
                                        apiResponse.dismissProgress();
                                    }

                                }
                            });

                            apiResponse.onServerError(mcontext.getResources().getString(R.string.server_error));
                            System.out.println("profiledata is 11===  " +t.getMessage());
                        }
                    });
                } else {
                    apiResponse.networkError(mcontext.getResources().getString(R.string.check_network));
                }
            } catch (Exception e) {
                e.printStackTrace();
                apiResponse.onServerError(mcontext.getResources().getString(R.string.server_error));
            }


        }





}