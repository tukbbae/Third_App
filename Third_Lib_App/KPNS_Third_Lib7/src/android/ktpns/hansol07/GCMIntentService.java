/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.ktpns.hansol07;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.ktpns.lib.service.ServiceGateway;
import com.ktpns.lib.util.Constant;

/**
 * This {@code IntentService} does the actual handling of the GCM message. {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver})
 * holds a partial wake lock for this service while the service does its work. When the service is finished, it calls
 * {@code completeWakefulIntent()} to release the wake lock.
 */
public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(Constant.SENDER_ID);
    }

    @Override
    protected void onError(Context arg0, String arg1) {
        
    }

    @Override
    protected void onMessage(Context arg0, Intent intent) {
        if (intent.getExtras().getString(Constant.KPNS_MSG_TYPE) != null) {
            if (intent.getExtras().getString(Constant.KPNS_MSG_TYPE).equals(Constant.KPNS_MSG_WAKEUP)) {
                ServiceGateway.startPushClientService(getApplicationContext(), intent);
            } else if (intent.getExtras().getString(Constant.KPNS_MSG_TYPE).equals(Constant.KPNS_MSG_TUNNELING)) {
                ServiceGateway.receivedGCMPushMessage(getApplicationContext(), intent);
            }
        }
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        ServiceGateway.registerGCMRegiID(context, registrationId, null);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        ServiceGateway.unregisterGCMRegiID(context, registrationId);
    }
}