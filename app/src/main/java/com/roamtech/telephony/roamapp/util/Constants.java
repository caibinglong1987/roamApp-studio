/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roamtech.telephony.roamapp.util;

public class Constants {

    /**
     * Log tag for performance measurement.
     * To enable: adb shell setprop log.tag.ContactsPerf VERBOSE
     */
    // Used for lookup URI that contains an encoded JSON string.
    /**
     * Log tag for enabling/disabling StrictMode violation log.
     * To enable: adb shell setprop log.tag.ContactsStrictMode DEBUG
     */
    public static final String EXTRA_IS_VIDEO_CALL = "com.android.phone.extra.video";
    public static final String EXTRA_IS_IP_DIAL = "com.android.phone.extra.ip";

    public static final int DIAL_NUMBER_INTENT_NORMAL = 0;
    public static final int DIAL_NUMBER_INTENT_IP = 1;
    public static final int DIAL_NUMBER_INTENT_VIDEO = 2;
}
