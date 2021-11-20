/*
 * Copyright (C) 2021 Yet Another AOSP Project
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

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.search.SearchIndexable;

import org.leafos.settings.preferences.colorpicker.ColorPickerPreference;

import java.lang.Math;

@SearchIndexable
public class MonetSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String ACCURATE_SHADES_KEY = "monet_engine_accurate_shades";
    private static final String WALLPAPER_KEY = "monet_engine_use_wallpaper_color";
    private static final String COLOR_KEY = "monet_engine_color_override";
    private static final String CHROMA_KEY = "monet_engine_chroma_factor";
    private static final String LINEAR_LIGHTNESS_KEY = "monet_engine_linear_lightness";
    private static final String WHITE_LUMINANCE_KEY = "monet_engine_white_luminance";

    SwitchPreference mAccurateShades;
    SwitchPreference mUseWall;
    ColorPickerPreference mColorOvr;
    SeekBarPreference mChroma;
    SwitchPreference mLinearLightness;
    SeekBarPreference mWhiteLuminance;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.monet_engine_settings);

        mAccurateShades = findPreference(ACCURATE_SHADES_KEY);
        boolean accurateShades = Settings.Secure.getInt(resolver, ACCURATE_SHADES_KEY, 1) != 0;
        mAccurateShades.setChecked(accurateShades);
        mAccurateShades.setOnPreferenceChangeListener(this);

        mUseWall = findPreference(WALLPAPER_KEY);
        mColorOvr = findPreference(COLOR_KEY);
        String color = Settings.Secure.getString(resolver, COLOR_KEY);
        boolean useWall = color == null || color.isEmpty();
        mUseWall.setChecked(useWall);
        mColorOvr.setEnabled(!useWall);
        if (!useWall) mColorOvr.setNewPreviewColor(
                ColorPickerPreference.convertToColorInt(color));
        mUseWall.setOnPreferenceChangeListener(this);
        mColorOvr.setOnPreferenceChangeListener(this);

        mChroma = findPreference(CHROMA_KEY);
        float chroma = Settings.Secure.getFloat(resolver, CHROMA_KEY, 1) * 100;
        mChroma.setProgress(Math.round(chroma));
        mChroma.setOnPreferenceChangeListener(this);

        mLinearLightness = findPreference(LINEAR_LIGHTNESS_KEY);
        boolean linearLightness = Settings.Secure.getInt(resolver, LINEAR_LIGHTNESS_KEY, 0) != 0;
        mLinearLightness.setChecked(linearLightness);
        mLinearLightness.setOnPreferenceChangeListener(this);

        mWhiteLuminance = findPreference(WHITE_LUMINANCE_KEY);
        int whiteLuminance = Settings.Secure.getInt(resolver, WHITE_LUMINANCE_KEY, 425);
        mWhiteLuminance.setProgress(whiteLuminance);
        mWhiteLuminance.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mAccurateShades) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver, ACCURATE_SHADES_KEY, value ? 1 : 0);
            return true;
        } else if (preference == mUseWall) {
            boolean value = (Boolean) newValue;
            mColorOvr.setEnabled(!value);
            if (value) Settings.Secure.putString(resolver, COLOR_KEY, "");
            return true;
        } else if (preference == mColorOvr) {
            int value = (Integer) newValue;
            Settings.Secure.putString(resolver, COLOR_KEY,
                    ColorPickerPreference.convertToRGB(value));
            return true;
        } else if (preference == mChroma) {
            int value = (Integer) newValue;
            Settings.Secure.putFloat(resolver, CHROMA_KEY, value / 100f);
            return true;
        } else if (preference == mLinearLightness) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(resolver, LINEAR_LIGHTNESS_KEY, value ? 1 : 0);
            return true;
        } else if (preference == mWhiteLuminance) {
            int value = (Integer) newValue;
            Settings.Secure.putInt(resolver, WHITE_LUMINANCE_KEY, value);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return 0;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.monet_engine_settings);
}
