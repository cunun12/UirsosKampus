package com.uirsos.www.uirsoskampus.POJO;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

/**
 * Created by cunun12 on 22/03/2018.
 */

public class PostId {

    @Exclude
    public String PostId;

    public <T extends PostId> T withId(@NonNull final String id){
        this.PostId = id;
        return (T) this;
    }
}
