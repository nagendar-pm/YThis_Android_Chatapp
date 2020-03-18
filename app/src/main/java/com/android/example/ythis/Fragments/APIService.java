package com.android.example.ythis.Fragments;

import com.android.example.ythis.Notifications.MyResponse;
import com.android.example.ythis.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA-2pxoq0:APA91bHkI7jtmEnClDa5UlJ-Rr9309dDyf47zI9a9QfXRCza9kEQIXGJrgmUXT-kaz2FihOKnFGk7cZdIO8G4o9c-jZvrukFSYt_Hy6RnUciBoS7pPRo_mZy6bV12oTz184MPI2d7S3B"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
