package com.lu.mydemo.Notification;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.lu.mydemo.R;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import com.lu.mydemo.Config.ColorManager;
import com.lu.mydemo.ToolFor2045_Site.InformationUploader;
import com.lu.mydemo.Utils.String.FormatCheck;
import com.lu.mydemo.Utils.Thread.MyThreadController;
import com.lu.mydemo.View.PopWindow.SetEmailPopupWindow;
import com.tapadoo.alerter.OnHideAlertListener;

public class AlertCenter {

    private static boolean isLoadingShowing = false;

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
                        .setOnHideListener(new OnHideAlertListener() {
                            @Override
                            public void onHide() {
                                isLoadingShowing = false;
                            }
                        })
                        .show();
            }
        });
    }

    public static void showAlert(final Activity context, final String message) {
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
        isLoadingShowing = false;
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
                                if(FormatCheck.isEmail(InformationUploader.USER_MAIL)) {
                                    MyThreadController.commit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                InformationUploader.reportException(exception, user_id);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    Toast.makeText(context, "感谢您的反馈^_^", Toast.LENGTH_SHORT).show();
                                    hideAlert(context);
                                }
                                else {
                                    SetEmailPopupWindow window = new SetEmailPopupWindow(context, new SetEmailPopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            MyThreadController.commit(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        InformationUploader.reportException(exception, user_id);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            Toast.makeText(context, "感谢您的反馈^_^", Toast.LENGTH_SHORT).show();
                                            hideAlert(context);
                                        }
                                    }, context.getWindow().getDecorView().getHeight(), context.getWindow().getDecorView().getWidth());
                                    window.setFocusable(true);
                                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                    window.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                }
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
        isLoadingShowing = false;
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
                                if(FormatCheck.isEmail(InformationUploader.USER_MAIL)) {
                                    MyThreadController.commit(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                InformationUploader.reportException(exceptions, user_id);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    Toast.makeText(context, "感谢您的反馈^_^", Toast.LENGTH_SHORT).show();
                                    hideAlert(context);
                                }
                                else {
                                    SetEmailPopupWindow window = new SetEmailPopupWindow(context, new SetEmailPopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            MyThreadController.commit(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        InformationUploader.reportException(exceptions, user_id);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            Toast.makeText(context, "感谢您的反馈^_^", Toast.LENGTH_SHORT).show();
                                            hideAlert(context);
                                        }
                                    }, context.getWindow().getDecorView().getHeight(), context.getWindow().getDecorView().getWidth());
                                    window.setFocusable(true);
                                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                    window.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public static boolean isLoadingShowing() {
        return isLoadingShowing;
    }

}
