package com.lu.mydemo.Notification;

import android.app.Activity;
import android.content.Context;

import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import Config.ColorManager;

public class AlertCenter {

    public static void hideAlert(final Activity context){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.hide();
            }
        });
    }

    public static void showLoading(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setText(message)
                        .enableProgress(true)
                        .setDismissable(false)
                        .setProgressColorRes(R.color.color_alerter_progress_bar)
                        .setDuration(Integer.MAX_VALUE)
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public static void showAlert(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public static void showAlert(final Activity context, final String title, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor())
                        .show();
            }
        });
    }

    public static void showWarningAlert(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_warning())
                        .show();
            }
        });
    }

    public static void showWarningAlert(final Activity context, final String title, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_warning())
                        .show();
            }
        });
    }

    public static void showErrorAlert(final Activity context, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setIcon(R.drawable.ic_error_outline_white_24dp)
                        .setTitle("提示")
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_error())
                        .show();
            }
        });
    }

    public static void showErrorAlert(final Activity context, final String title, final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setIcon(R.drawable.ic_error_outline_white_24dp)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_error())
                        .show();
            }
        });
    }

}
