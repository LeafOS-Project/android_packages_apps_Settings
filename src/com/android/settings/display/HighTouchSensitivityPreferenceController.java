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
package com.android.settings.display;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;

import java.util.NoSuchElementException;

import vendor.lineage.touch.V1_0.IGloveMode;

public class HighTouchSensitivityPreferenceController extends TogglePreferenceController implements
        Preference.OnPreferenceChangeListener  {

    private IGloveMode mGloveMode;
    private Preference mPreference;

    public static final String GLOVE_MODE_ENABLE = "high_touch_sensitivity_enable";

    public HighTouchSensitivityPreferenceController(Context context, String key) {
        super(context, key);

        try {
            mGloveMode = IGloveMode.getService();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }
    }

    @Override
    public void updateState(Preference preference) {
        mPreference = preference;
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return mGloveMode != null ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), GLOVE_MODE_ENABLE);
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
    public boolean isChecked() {
        return Settings.System.getInt(mContext.getContentResolver(), GLOVE_MODE_ENABLE, 0) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        try {
            mGloveMode.setEnabled(isChecked);
            Settings.System.putInt(mContext.getContentResolver(), GLOVE_MODE_ENABLE, isChecked ? 1 : 0);
            return true;
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void enableStatic(boolean enable) {
        try {
            IGloveMode gloveMode = IGloveMode.getService();
            gloveMode.setEnabled(enable);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }
    }
}
