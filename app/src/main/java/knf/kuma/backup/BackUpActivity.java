package knf.kuma.backup;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dropbox.core.android.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import knf.kuma.R;
import knf.kuma.backup.objects.BackupObject;
import knf.kuma.custom.SyncItemView;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 15/02/2018.
 */

public class BackUpActivity extends AppCompatActivity implements BUUtils.LoginInterface, SyncItemView.OnClick {

    @BindView(R.id.colorChanger)
    View colorChanger;
    @BindView(R.id.lay_main)
    View lay_main;
    @BindView(R.id.lay_buttons)
    View lay_buttons;
    @BindViews({R.id.sync_favs, R.id.sync_history, R.id.sync_following, R.id.sync_seen})
    List<SyncItemView> syncItems;
    private boolean waitingLogin = false;

    public static void start(Context context) {
        context.startActivity(new Intent(context, BackUpActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        BUUtils.init(this, savedInstanceState == null);
        if (BUUtils.isLogedIn()) {
            setState(true);
            showColor(savedInstanceState == null);
            initSyncButtons();
        } else {
            setState(false);
        }
    }

    private void initSyncButtons() {
        for (SyncItemView itemView : syncItems) {
            itemView.init(this);
        }
    }

    private void clearSyncButtons() {
        for (SyncItemView itemView : syncItems) {
            itemView.clear();
        }
    }

    @OnClick(R.id.login_dropbox)
    void onDropBoxLogin(Button button) {
        waitingLogin = true;
        BUUtils.startClient(BUUtils.BUType.DROPBOX, false);
    }

    @OnClick(R.id.login_drive)
    void onDriveLogin(Button button) {
        waitingLogin = true;
        BUUtils.startClient(BUUtils.BUType.DRIVE, false);
    }

    @Override
    public void onAction(final SyncItemView syncItemView, String id, boolean isBackup) {
        if (isBackup) {
            BUUtils.backup(id, new BUUtils.BackupInterface() {
                @Override
                public void onResponse(@Nullable BackupObject backupObject) {
                    if (backupObject == null)
                        Toaster.toast("Error al respaldar");
                    syncItemView.enableBackup(backupObject, BackUpActivity.this);
                }
            });
        } else {
            BUUtils.restoreDialog(id, syncItemView.getBakup());
        }
    }

    @OnClick(R.id.logOut)
    void onLogOut(Button button) {
        new MaterialDialog.Builder(this)
                .content("Los datos se quedaran en el dispositivo, ¿desea continuar?")
                .positiveText("continuar")
                .negativeText("cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        BUUtils.logOut();
                        revertColor();
                        setState(false);
                        clearSyncButtons();
                    }
                }).build().show();
    }

    @Override
    public void onLogin() {
        if (BUUtils.isLogedIn()) {
            setState(true);
            showColor(true);
            initSyncButtons();
        } else if (waitingLogin) {
            Toaster.toast("Error al iniciar sesión");
        }
        waitingLogin = false;
    }

    @ColorInt
    private int getBackColor() {
        switch (BUUtils.getType()) {
            default:
            case LOCAL:
                return getResources().getColor(android.R.color.transparent);
            case DRIVE:
                return getResources().getColor(R.color.drive);
            case DROPBOX:
                return getResources().getColor(R.color.dropbox);
        }
    }

    private void showColor(final boolean animate) {
        colorChanger.post(new Runnable() {
            @Override
            public void run() {
                colorChanger.setBackgroundColor(getBackColor());
                if (animate) {
                    Rect bounds = new Rect();
                    colorChanger.getDrawingRect(bounds);
                    int centerX = bounds.centerX();
                    int centerY = bounds.centerY();
                    int finalRadius = Math.max(bounds.width(), bounds.height());
                    Animator animator = ViewAnimationUtils.createCircularReveal(colorChanger, centerX, centerY, 0f, finalRadius);
                    animator.setDuration(1000);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    colorChanger.setVisibility(View.VISIBLE);
                    animator.start();
                } else {
                    colorChanger.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void revertColor() {
        colorChanger.post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                colorChanger.getDrawingRect(bounds);
                int centerX = bounds.centerX();
                int centerY = bounds.centerY();
                int finalRadius = Math.max(bounds.width(), bounds.height());
                Animator animator = ViewAnimationUtils.createCircularReveal(colorChanger, centerX, centerY, finalRadius, 0f);
                animator.setDuration(1000);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        colorChanger.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            }
        });
    }

    private void setState(final boolean isLogedIn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lay_main.setVisibility(isLogedIn ? View.GONE : View.VISIBLE);
                lay_buttons.setVisibility(isLogedIn ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (waitingLogin) {
            String token = Auth.getOAuth2Token();
            if (token != null)
                BUUtils.setType(BUUtils.BUType.DROPBOX);
            BUUtils.setDropBoxClient(token);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BUUtils.LOGIN_CODE) {
            Log.e("Result", "Code: " + resultCode);
            if (resultCode == RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(data);
                BUUtils.setType(BUUtils.BUType.DRIVE);
                BUUtils.setDriveClient();
            } else {
                onLogin();
            }
        }
    }
}
