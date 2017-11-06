/*
ContactPickerActivity.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.linphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.roamtech.telephony.roamapp.event.EventNetworkConnect;

import org.greenrobot.eventbus.EventBus;

/**
 * Intercept network state changes and update linphone core through LinphoneManager.
 */
public class NetworkManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LinphoneService.isReady()) {
            LinphoneManager.getInstance(LinphoneService.instance()).updateNetworkReachAbility();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo eventInfo = cm.getActiveNetworkInfo();
            if (eventInfo != null && eventInfo.getState() == NetworkInfo.State.CONNECTED) {
                new RunningThread().start();
            }
        }
    }

    private static class RunningThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new EventNetworkConnect());
        }
    }

}
