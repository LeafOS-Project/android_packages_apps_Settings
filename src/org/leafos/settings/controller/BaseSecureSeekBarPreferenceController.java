/*
 * Copyright (C) 2022 The LeafOS Project
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
package org.leafos.settings.controller;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.widget.SeekBarPreference;

public class BaseSecureSeekBarPreferenceController extends SliderPreferenceController implements
        Preference.OnPreferenceChangeListener  {

    private SeekBarPreference mPreference;

    public BaseSecureSeekBarPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void updateState(Preference preference) {
        mPreference = (SeekBarPreference)preference;
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
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
    public int getSliderPosition() {
        return Settings.Secure.getInt(mContext.getContentResolver(), getPreferenceKey(),
            mPreference.getValue());
    }

    @Override
    public boolean setSliderPosition(int position) {
        Settings.Secure.putInt(mContext.getContentResolver(), getPreferenceKey(), position);
        return true;
    }

    @Override
    public int getMax() {
        return mPreference.getMax();
    }

    @Override
    public int getMin() {
        return mPreference.getMin();
    }
}
