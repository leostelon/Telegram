package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.pluto.types.UserWallet;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;

public class CustomActivity extends BaseFragment {
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
//                ArrayList<PlutoAuthTokensHelper.Token> token = PlutoAuthTokensHelper.getSavedLogInTokens();
//                Log.d("token", token.get(0).accessToken);
//                if(token.get(0) != null) {
//                    User.getWallet(token.get(0).accessToken);
//                }
            }
        });
        UserWallet user = getUserWalletConfig().userWallet;
        Log.d("Wallet Shit", user.walletAddress);
        actionBar.setTitle(LocaleController.getString("Wallet", R.string.Wallet)+" ("+user.walletAddress.substring(0,4)+"...."+user.walletAddress.substring(user.walletAddress.length()-4)+")");

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);

        fragmentView = scrollView;

        return fragmentView;
    }
}
