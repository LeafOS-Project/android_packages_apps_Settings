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

package org.leafos.settings.qs;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class QSPanelSettings extends DashboardFragment {

    private static final String TAG = "QSPanelSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultValue(getContext(), Settings.Secure.QQS_NUM_COLUMNS, 5);
        setDefaultValue(getContext(), Settings.Secure.QQS_NUM_COLUMNS_LANDSCAPE, 6);
        setDefaultValue(getContext(), Settings.Secure.QS_NUM_COLUMNS, 4);
        setDefaultValue(getContext(), Settings.Secure.QS_NUM_COLUMNS_LANDSCAPE, 6);
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.qs_panel_settings;
    }

    private void setDefaultValue(Context context, String key, int def) {
        int currentValue = Settings.Secure.getIntForUser(
            context.getContentResolver(), key, -1, UserHandle.USER_CURRENT);
        if (currentValue == -1) {
            Settings.Secure.putIntForUser(context.getContentResolver(), key, def, UserHandle.USER_CURRENT);
        }
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_panel_settings);
}
