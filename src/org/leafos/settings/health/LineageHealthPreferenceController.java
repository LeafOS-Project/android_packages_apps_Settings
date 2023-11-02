/*
 * Copyright (C) 2023 The LeafOS Project
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
package org.leafos.settings.health;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import com.android.internal.lineage.health.HealthInterface;

public class LineageHealthPreferenceController extends BasePreferenceController implements
        Preference.OnPreferenceChangeListener  {

    Context mContext;
    Preference mPreference;

    public LineageHealthPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public void updateState(Preference preference) {
        mPreference = preference;
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return HealthInterface.isChargingControlSupported(mContext) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isSliceable() {
        return mPreference != null;
    }

    @Override
    public boolean isPublicSlice() {
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }

    @Override
    public CharSequence getSummary() {
        HealthInterface healthInterface = HealthInterface.getInstance(mContext);
        if (healthInterface.getEnabled()) {
            return mContext.getString(R.string.enabled);
        }
        return mContext.getString(R.string.disabled);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object value) {
        return false;
    }
}
