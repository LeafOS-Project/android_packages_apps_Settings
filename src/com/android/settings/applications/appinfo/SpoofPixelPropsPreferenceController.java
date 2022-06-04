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
package com.android.settings.applications.appinfo;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import java.util.Collections;
import java.util.List;

public class SpoofPixelPropsPreferenceController extends AppInfoPreferenceControllerSwitch implements
        Preference.OnPreferenceChangeListener  {

    private SwitchPreference mPreference;

    public SpoofPixelPropsPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public void updateState(Preference preference) {
        mPreference = (SwitchPreference)preference;
        super.updateState(preference);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "spoof_pixel_props");
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
        String packages = Settings.Secure.getString(mContext.getContentResolver(), getPreferenceKey(),
            "com.google.android.gms");
        return List.of(packages.split(";")).contains();
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        String packageName = mParent.getPackageInfo().packageName;
        String packages = Settings.Secure.getString(mContext.getContentResolver(), getPreferenceKey(),
            "com.google.android.gms");
        List<String> packageList = List.of(packages.split(";"));
        if (isChecked && !packageList.contains(packageName)) {
            packageList.add(packageName);
        } else if (!isChecked) {
            packageList.remove(packageName);
        }
        packages = packageList.stream().collect(Collectors.joining("; "));
        Settings.Secure.putString(mContext.getContentResolver(), getPreferenceKey(),
            packages);
        return true;
    }
}
