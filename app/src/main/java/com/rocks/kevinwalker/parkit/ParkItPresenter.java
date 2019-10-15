package com.rocks.kevinwalker.parkit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

public abstract class ParkItPresenter {
    protected ParkItPresenter() {};

    protected ParkItPresenter getPresenter() {
        return this;
    }

    protected abstract void cleanUp();
}
