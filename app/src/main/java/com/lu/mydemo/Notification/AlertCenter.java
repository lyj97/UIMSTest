package com.lu.mydemo.Notification;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import Config.ColorManager;
import ToolFor2045_Site.ExceptionReporter;

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
                        .setTitle("错误")
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

    public static void showErrorAlertWithReportButton(final Activity context, final String message, final Exception exception, final String user_id) {
        showErrorAlertWithReportButton(context, "错误", message, exception, user_id);
    }

    public static void showErrorAlertWithReportButton(final Activity context, final String title, final String message, final Exception exception, final String user_id) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setIcon(R.drawable.ic_error_outline_white_24dp)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_error())
                        .addButton("Report!", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ExceptionReporter.reportException(exception, user_id);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                Toast.makeText(context, "感谢您的反馈!\n^_^", Toast.LENGTH_SHORT).show();
                                hideAlert(context);
                            }
                        })
                        .show();
            }
        });
    }

    public static void showErrorAlertWithReportButton(final Activity context, final String message, final List<Exception> exception, final String user_id) {
        showErrorAlertWithReportButton(context, "错误", message, exception, user_id);
    }

    public static void showErrorAlertWithReportButton(final Activity context, final String title, final String message, final List<Exception> exceptions, final String user_id) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Alerter.create(context)
                        .setIcon(R.drawable.ic_error_outline_white_24dp)
                        .setTitle(title)
                        .setText(message)
                        .enableSwipeToDismiss()
                        .setBackgroundColorInt(ColorManager.getTopAlertBackgroundColor_error())
                        .addButton("Report!", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ExceptionReporter.reportException(exceptions, user_id);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                Toast.makeText(context, "感谢您的反馈!\n^_^", Toast.LENGTH_SHORT).show();
                                hideAlert(context);
                            }
                        })
                        .show();
            }
        });
    }

}
