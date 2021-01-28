/*
 * Copyright (C) 2016 The CyanogenMod project
 *               2017-2021 The LineageOS project
 *               2022 The LibreMobileOS Foundation
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

package com.android.settings.leafos.buttons;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;

import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.leafos.buttons.preference.*;
import com.android.settings.leafos.utils.DeviceUtils;
import com.android.settings.leafos.utils.TelephonyUtils;
import com.android.settingslib.search.SearchIndexable;

import static com.android.internal.util.leafos.DeviceKeysConstants.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.android.settings.leafos.preference.CustomDialogPreference;

@SearchIndexable
public class ButtonSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_BACK_WAKE_SCREEN = "back_wake_screen";
    private static final String KEY_CAMERA_LAUNCH = "camera_launch";
    private static final String KEY_CAMERA_SLEEP_ON_RELEASE = "camera_sleep_on_release";
    private static final String KEY_CAMERA_WAKE_SCREEN = "camera_wake_screen";
    private static final String KEY_BACK_LONG_PRESS = "hardware_keys_back_long_press";
    private static final String KEY_HOME_LONG_PRESS = "hardware_keys_home_long_press";
    private static final String KEY_HOME_DOUBLE_TAP = "hardware_keys_home_double_tap";
    private static final String KEY_HOME_WAKE_SCREEN = "home_wake_screen";
    private static final String KEY_MENU_PRESS = "hardware_keys_menu_press";
    private static final String KEY_MENU_LONG_PRESS = "hardware_keys_menu_long_press";
    private static final String KEY_MENU_WAKE_SCREEN = "menu_wake_screen";
    private static final String KEY_ASSIST_PRESS = "hardware_keys_assist_press";
    private static final String KEY_ASSIST_LONG_PRESS = "hardware_keys_assist_long_press";
    private static final String KEY_ASSIST_WAKE_SCREEN = "assist_wake_screen";
    private static final String KEY_APP_SWITCH_PRESS = "hardware_keys_app_switch_press";
    private static final String KEY_APP_SWITCH_LONG_PRESS = "hardware_keys_app_switch_long_press";
    private static final String KEY_APP_SWITCH_WAKE_SCREEN = "app_switch_wake_screen";
    private static final String KEY_VOLUME_KEY_CURSOR_CONTROL = "volume_key_cursor_control";
    private static final String KEY_SWAP_VOLUME_BUTTONS = "swap_volume_buttons";
    private static final String KEY_VOLUME_PANEL_ON_LEFT = "volume_panel_on_left";
    private static final String KEY_VOLUME_WAKE_SCREEN = "volume_wake_screen";
    private static final String KEY_VOLUME_ANSWER_CALL = "volume_answer_call";
    private static final String KEY_DISABLE_NAV_KEYS = "disable_nav_keys";
    private static final String KEY_NAVIGATION_ARROW_KEYS = "navigation_bar_menu_arrow_keys";
    private static final String KEY_NAVIGATION_BACK_LONG_PRESS = "navigation_back_long_press";
    private static final String KEY_NAVIGATION_HOME_LONG_PRESS = "navigation_home_long_press";
    private static final String KEY_NAVIGATION_HOME_DOUBLE_TAP = "navigation_home_double_tap";
    private static final String KEY_NAVIGATION_APP_SWITCH_LONG_PRESS =
            "navigation_app_switch_long_press";
    private static final String KEY_EDGE_LONG_SWIPE = "navigation_bar_edge_long_swipe";
    private static final String KEY_POWER_END_CALL = "power_end_call";
    private static final String KEY_HOME_ANSWER_CALL = "home_answer_call";
    private static final String KEY_VOLUME_MUSIC_CONTROLS = "volbtn_music_controls";

    private static final String CATEGORY_POWER = "power_key";
    private static final String CATEGORY_HOME = "home_key";
    private static final String CATEGORY_BACK = "back_key";
    private static final String CATEGORY_MENU = "menu_key";
    private static final String CATEGORY_ASSIST = "assist_key";
    private static final String CATEGORY_APPSWITCH = "app_switch_key";
    private static final String CATEGORY_CAMERA = "camera_key";
    private static final String CATEGORY_VOLUME = "volume_keys";
    private static final String CATEGORY_NAVBAR = "navigation_bar_category";
    private static final String CATEGORY_EXTRAS = "extras_category";

    private ListPreference mBackLongPressAction;
    private ListPreference mHomeLongPressAction;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mMenuPressAction;
    private ListPreference mMenuLongPressAction;
    private ListPreference mAssistPressAction;
    private ListPreference mAssistLongPressAction;
    private ListPreference mAppSwitchPressAction;
    private ListPreference mAppSwitchLongPressAction;
    private SwitchPreference mCameraWakeScreen;
    private SwitchPreference mCameraSleepOnRelease;
    private SwitchPreference mCameraLaunch;
    private ListPreference mVolumeKeyCursorControl;
    private SwitchPreference mVolumeWakeScreen;
    private SwitchPreference mVolumeMusicControls;
    private SwitchPreference mSwapVolumeButtons;
    private SwitchPreference mVolumePanelOnLeft;
    private SwitchPreference mNavigationArrowKeys;
    private ListPreference mNavigationBackLongPressAction;
    private ListPreference mNavigationHomeLongPressAction;
    private ListPreference mNavigationHomeDoubleTapAction;
    private ListPreference mNavigationAppSwitchLongPressAction;
    private ListPreference mEdgeLongSwipeAction;
    private SwitchPreference mPowerEndCall;
    private SwitchPreference mHomeAnswerCall;

    private PreferenceCategory mNavigationPreferencesCat;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.button_settings);

        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final boolean hasPowerKey = DeviceUtils.hasPowerKey();
        final boolean hasHomeKey = DeviceUtils.hasHomeKey(getActivity());
        final boolean hasBackKey = DeviceUtils.hasBackKey(getActivity());
        final boolean hasMenuKey = DeviceUtils.hasMenuKey(getActivity());
        final boolean hasAssistKey = DeviceUtils.hasAssistKey(getActivity());
        final boolean hasAppSwitchKey = DeviceUtils.hasAppSwitchKey(getActivity());
        final boolean hasCameraKey = DeviceUtils.hasCameraKey(getActivity());
        final boolean hasVolumeKeys = DeviceUtils.hasVolumeKeys(getActivity());

        final boolean showHomeWake = DeviceUtils.canWakeUsingHomeKey(getActivity());
        final boolean showBackWake = DeviceUtils.canWakeUsingBackKey(getActivity());
        final boolean showMenuWake = DeviceUtils.canWakeUsingMenuKey(getActivity());
        final boolean showAssistWake = DeviceUtils.canWakeUsingAssistKey(getActivity());
        final boolean showAppSwitchWake = DeviceUtils.canWakeUsingAppSwitchKey(getActivity());
        final boolean showCameraWake = DeviceUtils.canWakeUsingCameraKey(getActivity());
        final boolean showVolumeWake = DeviceUtils.canWakeUsingVolumeKeys(getActivity());

        boolean hasAnyBindableKey = false;
        final PreferenceCategory powerCategory = prefScreen.findPreference(CATEGORY_POWER);
        final PreferenceCategory homeCategory = prefScreen.findPreference(CATEGORY_HOME);
        final PreferenceCategory backCategory = prefScreen.findPreference(CATEGORY_BACK);
        final PreferenceCategory menuCategory = prefScreen.findPreference(CATEGORY_MENU);
        final PreferenceCategory assistCategory = prefScreen.findPreference(CATEGORY_ASSIST);
        final PreferenceCategory appSwitchCategory = prefScreen.findPreference(CATEGORY_APPSWITCH);
        final PreferenceCategory volumeCategory = prefScreen.findPreference(CATEGORY_VOLUME);
        final PreferenceCategory cameraCategory = prefScreen.findPreference(CATEGORY_CAMERA);
        final PreferenceCategory extrasCategory = prefScreen.findPreference(CATEGORY_EXTRAS);

        // Power button ends calls.
        mPowerEndCall = findPreference(KEY_POWER_END_CALL);

        // Home button answers calls.
        mHomeAnswerCall = findPreference(KEY_HOME_ANSWER_CALL);

        mHandler = new Handler();

        mNavigationPreferencesCat = findPreference(CATEGORY_NAVBAR);

        Action defaultBackLongPressAction = Action.fromIntSafe(res.getInteger(
                com.android.internal.R.integer.config_longPressOnBackBehavior));
        Action defaultHomeLongPressAction = Action.fromIntSafe(res.getInteger(
                com.android.internal.R.integer.config_longPressOnHomeBehavior));
        Action defaultHomeDoubleTapAction = Action.fromIntSafe(res.getInteger(
                com.android.internal.R.integer.config_doubleTapOnHomeBehavior));
        Action defaultAppSwitchLongPressAction = Action.fromIntSafe(res.getInteger(
                com.android.internal.R.integer.config_longPressOnAppSwitchBehavior));
        Action backLongPressAction = Action.fromSettings(resolver,
                Settings.System.KEY_BACK_LONG_PRESS_ACTION,
                defaultBackLongPressAction);
        Action homeLongPressAction = Action.fromSettings(resolver,
                Settings.System.KEY_HOME_LONG_PRESS_ACTION,
                defaultHomeLongPressAction);
        Action homeDoubleTapAction = Action.fromSettings(resolver,
                Settings.System.KEY_HOME_DOUBLE_TAP_ACTION,
                defaultHomeDoubleTapAction);
        Action appSwitchLongPressAction = Action.fromSettings(resolver,
                Settings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION,
                defaultAppSwitchLongPressAction);
        Action edgeLongSwipeAction = Action.fromSettings(resolver,
                Settings.System.KEY_EDGE_LONG_SWIPE_ACTION,
                Action.NOTHING);

        // Navigation bar arrow keys while typing
        mNavigationArrowKeys = findPreference(KEY_NAVIGATION_ARROW_KEYS);

        // Navigation bar back long press
        mNavigationBackLongPressAction = initList(KEY_NAVIGATION_BACK_LONG_PRESS,
                backLongPressAction);

        // Navigation bar home long press
        mNavigationHomeLongPressAction = initList(KEY_NAVIGATION_HOME_LONG_PRESS,
                homeLongPressAction);

        // Navigation bar home double tap
        mNavigationHomeDoubleTapAction = initList(KEY_NAVIGATION_HOME_DOUBLE_TAP,
                homeDoubleTapAction);

        // Navigation bar app switch long press
        mNavigationAppSwitchLongPressAction = initList(KEY_NAVIGATION_APP_SWITCH_LONG_PRESS,
                appSwitchLongPressAction);

        // Edge long swipe gesture
        mEdgeLongSwipeAction = initList(KEY_EDGE_LONG_SWIPE, edgeLongSwipeAction);

        if (hasPowerKey) {
            if (!TelephonyUtils.isVoiceCapable(getActivity())) {
                powerCategory.removePreference(mPowerEndCall);
                mPowerEndCall = null;
            }
        }
        if (!hasPowerKey || powerCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(powerCategory);
        }

        if (hasHomeKey) {
            if (!showHomeWake) {
                homeCategory.removePreference(findPreference(KEY_HOME_WAKE_SCREEN));
            }

            if (!TelephonyUtils.isVoiceCapable(getActivity())) {
                homeCategory.removePreference(mHomeAnswerCall);
                mHomeAnswerCall = null;
            }

            mHomeLongPressAction = initList(KEY_HOME_LONG_PRESS, homeLongPressAction);
            mHomeDoubleTapAction = initList(KEY_HOME_DOUBLE_TAP, homeDoubleTapAction);

            hasAnyBindableKey = true;
        }
        if (!hasHomeKey || homeCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(homeCategory);
        }

        if (hasBackKey) {
            if (!showBackWake) {
                backCategory.removePreference(findPreference(KEY_BACK_WAKE_SCREEN));
            }

            mBackLongPressAction = initList(KEY_BACK_LONG_PRESS, backLongPressAction);

            hasAnyBindableKey = true;
        }
        if (!hasBackKey || backCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(backCategory);
        }

        if (hasMenuKey) {
            if (!showMenuWake) {
                menuCategory.removePreference(findPreference(KEY_MENU_WAKE_SCREEN));
            }

            Action pressAction = Action.fromSettings(resolver,
                    Settings.System.KEY_MENU_ACTION, Action.MENU);
            mMenuPressAction = initList(KEY_MENU_PRESS, pressAction);

            Action longPressAction = Action.fromSettings(resolver,
                        Settings.System.KEY_MENU_LONG_PRESS_ACTION,
                        hasAssistKey ? Action.NOTHING : Action.APP_SWITCH);
            mMenuLongPressAction = initList(KEY_MENU_LONG_PRESS, longPressAction);

            hasAnyBindableKey = true;
        }
        if (!hasMenuKey || menuCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(menuCategory);
        }

        if (hasAssistKey) {
            if (!showAssistWake) {
                assistCategory.removePreference(findPreference(KEY_ASSIST_WAKE_SCREEN));
            }

            Action pressAction = Action.fromSettings(resolver,
                    Settings.System.KEY_ASSIST_ACTION, Action.SEARCH);
            mAssistPressAction = initList(KEY_ASSIST_PRESS, pressAction);

            Action longPressAction = Action.fromSettings(resolver,
                    Settings.System.KEY_ASSIST_LONG_PRESS_ACTION, Action.VOICE_SEARCH);
            mAssistLongPressAction = initList(KEY_ASSIST_LONG_PRESS, longPressAction);

            hasAnyBindableKey = true;
        }
        if (!hasAssistKey || assistCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(assistCategory);
        }

        if (hasAppSwitchKey) {
            if (!showAppSwitchWake) {
                appSwitchCategory.removePreference(findPreference(KEY_APP_SWITCH_WAKE_SCREEN));
            }

            Action pressAction = Action.fromSettings(resolver,
                    Settings.System.KEY_APP_SWITCH_ACTION, Action.APP_SWITCH);
            mAppSwitchPressAction = initList(KEY_APP_SWITCH_PRESS, pressAction);

            mAppSwitchLongPressAction = initList(KEY_APP_SWITCH_LONG_PRESS, appSwitchLongPressAction);

            hasAnyBindableKey = true;
        }
        if (!hasAppSwitchKey || appSwitchCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(appSwitchCategory);
        }

        if (hasCameraKey) {
            mCameraWakeScreen = findPreference(KEY_CAMERA_WAKE_SCREEN);
            mCameraSleepOnRelease = findPreference(KEY_CAMERA_SLEEP_ON_RELEASE);
            mCameraLaunch = findPreference(KEY_CAMERA_LAUNCH);

            if (!showCameraWake) {
                prefScreen.removePreference(mCameraWakeScreen);
            }
            // Only show 'Camera sleep on release' if the device has a focus key
            if (res.getBoolean(com.android.internal.R.bool.config_singleStageCameraKey)) {
                prefScreen.removePreference(mCameraSleepOnRelease);
            }
        }
        if (!hasCameraKey || cameraCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(cameraCategory);
        }

        if (hasVolumeKeys) {
            if (!showVolumeWake) {
                volumeCategory.removePreference(findPreference(KEY_VOLUME_WAKE_SCREEN));
            }

            if (!TelephonyUtils.isVoiceCapable(getActivity())) {
                volumeCategory.removePreference(findPreference(KEY_VOLUME_ANSWER_CALL));
            }

            int cursorControlAction = Settings.System.getInt(resolver,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL, 0);
            mVolumeKeyCursorControl = initList(KEY_VOLUME_KEY_CURSOR_CONTROL,
                    cursorControlAction);

            int swapVolumeKeys = Settings.System.getInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, 0);
            mSwapVolumeButtons = prefScreen.findPreference(KEY_SWAP_VOLUME_BUTTONS);
            if (mSwapVolumeButtons != null) {
                mSwapVolumeButtons.setChecked(swapVolumeKeys > 0);
            }

            final boolean volumePanelOnLeft = Settings.Secure.getIntForUser(
                    getContentResolver(), Settings.Secure.VOLUME_PANEL_ON_LEFT, 0,
                    UserHandle.USER_CURRENT) != 0;
            mVolumePanelOnLeft = prefScreen.findPreference(KEY_VOLUME_PANEL_ON_LEFT);
            if (mVolumePanelOnLeft != null) {
                mVolumePanelOnLeft.setChecked(volumePanelOnLeft);
            }
        }
        if (!hasVolumeKeys || volumeCategory.getPreferenceCount() == 0) {
            prefScreen.removePreference(volumeCategory);
        }

        // Only show the navigation bar category on devices that have a navigation bar
        // or support disabling the hardware keys
        if (!hasNavigationBar() && !isKeyDisablerSupported(getActivity())) {
            prefScreen.removePreference(mNavigationPreferencesCat);
        }

        if (mCameraWakeScreen != null) {
            if (mCameraSleepOnRelease != null && !res.getBoolean(
                    com.android.internal.R.bool.config_singleStageCameraKey)) {
                mCameraSleepOnRelease.setDependency(KEY_CAMERA_WAKE_SCREEN);
            }
        }

        mVolumeWakeScreen = findPreference(KEY_VOLUME_WAKE_SCREEN);
        mVolumeMusicControls = findPreference(KEY_VOLUME_MUSIC_CONTROLS);

        if (mVolumeWakeScreen != null) {
            if (mVolumeMusicControls != null) {
                mVolumeMusicControls.setDependency(KEY_VOLUME_WAKE_SCREEN);
                mVolumeWakeScreen.setDisableDependentsState(true);
            }
        }

        // Override key actions on Go devices in order to hide any unsupported features
        if (ActivityManager.isLowRamDeviceStatic()) {
            String[] actionEntriesGo = res.getStringArray(R.array.hardware_keys_action_entries_go);
            String[] actionValuesGo = res.getStringArray(R.array.hardware_keys_action_values_go);

            if (hasBackKey) {
                mBackLongPressAction.setEntries(actionEntriesGo);
                mBackLongPressAction.setEntryValues(actionValuesGo);
            }

            if (hasHomeKey) {
                mHomeLongPressAction.setEntries(actionEntriesGo);
                mHomeLongPressAction.setEntryValues(actionValuesGo);

                mHomeDoubleTapAction.setEntries(actionEntriesGo);
                mHomeDoubleTapAction.setEntryValues(actionValuesGo);
            }

            if (hasMenuKey) {
                mMenuPressAction.setEntries(actionEntriesGo);
                mMenuPressAction.setEntryValues(actionValuesGo);

                mMenuLongPressAction.setEntries(actionEntriesGo);
                mMenuLongPressAction.setEntryValues(actionValuesGo);
            }

            if (hasAssistKey) {
                mAssistPressAction.setEntries(actionEntriesGo);
                mAssistPressAction.setEntryValues(actionValuesGo);

                mAssistLongPressAction.setEntries(actionEntriesGo);
                mAssistLongPressAction.setEntryValues(actionValuesGo);
            }

            if (hasAppSwitchKey) {
                mAppSwitchPressAction.setEntries(actionEntriesGo);
                mAppSwitchPressAction.setEntryValues(actionValuesGo);

                mAppSwitchLongPressAction.setEntries(actionEntriesGo);
                mAppSwitchLongPressAction.setEntryValues(actionValuesGo);
            }

            mNavigationBackLongPressAction.setEntries(actionEntriesGo);
            mNavigationBackLongPressAction.setEntryValues(actionValuesGo);

            mNavigationHomeLongPressAction.setEntries(actionEntriesGo);
            mNavigationHomeLongPressAction.setEntryValues(actionValuesGo);

            mNavigationHomeDoubleTapAction.setEntries(actionEntriesGo);
            mNavigationHomeDoubleTapAction.setEntryValues(actionValuesGo);

            mNavigationAppSwitchLongPressAction.setEntries(actionEntriesGo);
            mNavigationAppSwitchLongPressAction.setEntryValues(actionValuesGo);

            mEdgeLongSwipeAction.setEntries(actionEntriesGo);
            mEdgeLongSwipeAction.setEntryValues(actionValuesGo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Power button ends calls.
        if (mPowerEndCall != null) {
            final int incallPowerBehavior = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR,
                    Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT);
            final boolean powerButtonEndsCall =
                    (incallPowerBehavior == Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_HANGUP);
            mPowerEndCall.setChecked(powerButtonEndsCall);
        }

        // Home button answers calls.
        if (mHomeAnswerCall != null) {
            final int incallHomeBehavior = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.RING_HOME_BUTTON_BEHAVIOR,
                    Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_DEFAULT);
            final boolean homeButtonAnswersCall =
                (incallHomeBehavior == Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER);
            mHomeAnswerCall.setChecked(homeButtonAnswersCall);
        }
    }

    private ListPreference initList(String key, Action value) {
        return initList(key, value.ordinal());
    }

    private ListPreference initList(String key, int value) {
        ListPreference list = getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        Settings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    private void handleSystemListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        Settings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBackLongPressAction ||
                preference == mNavigationBackLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    Settings.System.KEY_BACK_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mHomeLongPressAction ||
                preference == mNavigationHomeLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    Settings.System.KEY_HOME_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mHomeDoubleTapAction ||
                preference == mNavigationHomeDoubleTapAction) {
            handleListChange((ListPreference) preference, newValue,
                    Settings.System.KEY_HOME_DOUBLE_TAP_ACTION);
            return true;
        } else if (preference == mMenuPressAction) {
            handleListChange(mMenuPressAction, newValue,
                    Settings.System.KEY_MENU_ACTION);
            return true;
        } else if (preference == mMenuLongPressAction) {
            handleListChange(mMenuLongPressAction, newValue,
                    Settings.System.KEY_MENU_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mAssistPressAction) {
            handleListChange(mAssistPressAction, newValue,
                    Settings.System.KEY_ASSIST_ACTION);
            return true;
        } else if (preference == mAssistLongPressAction) {
            handleListChange(mAssistLongPressAction, newValue,
                    Settings.System.KEY_ASSIST_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mAppSwitchPressAction) {
            handleListChange(mAppSwitchPressAction, newValue,
                    Settings.System.KEY_APP_SWITCH_ACTION);
            return true;
        } else if (preference == mAppSwitchLongPressAction ||
                preference == mNavigationAppSwitchLongPressAction) {
            handleListChange((ListPreference) preference, newValue,
                    Settings.System.KEY_APP_SWITCH_LONG_PRESS_ACTION);
            return true;
        } else if (preference == mVolumeKeyCursorControl) {
            handleSystemListChange(mVolumeKeyCursorControl, newValue,
                    Settings.System.VOLUME_KEY_CURSOR_CONTROL);
            return true;
        } else if (preference == mEdgeLongSwipeAction) {
            handleListChange(mEdgeLongSwipeAction, newValue,
                    Settings.System.KEY_EDGE_LONG_SWIPE_ACTION);
            return true;
        }
        return false;
    }

    private static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        try {
            IWindowManager windowManager = WindowManagerGlobal.getWindowManagerService();
            hasNavigationBar = windowManager.hasNavigationBar(Display.DEFAULT_DISPLAY);
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting navigation bar status");
        }
        return hasNavigationBar;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mSwapVolumeButtons) {
            int value;

            if (mSwapVolumeButtons.isChecked()) {
                /* The native inputflinger service uses the same logic of:
                 *   1 - the volume rocker is on one the sides, relative to the natural
                 *       orientation of the display (true for all phones and most tablets)
                 *   2 - the volume rocker is on the top or bottom, relative to the
                 *       natural orientation of the display (true for some tablets)
                 */
                value = getResources().getInteger(
                        R.integer.config_volumeRockerVsDisplayOrientation);
            } else {
                /* Disable the re-orient functionality */
                value = 0;
            }
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, value);
        } else if (preference == mVolumePanelOnLeft) {
            Settings.Secure.putIntForUser(getActivity().getContentResolver(),
                    Settings.Secure.VOLUME_PANEL_ON_LEFT,
                    mVolumePanelOnLeft.isChecked() ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mPowerEndCall) {
            handleTogglePowerButtonEndsCallPreferenceClick();
            return true;
        } else if (preference == mHomeAnswerCall) {
            handleToggleHomeButtonAnswersCallPreferenceClick();
            return true;
        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference.getKey() == null) {
            // Auto-key preferences that don't have a key, so the dialog can find them.
            preference.setKey(UUID.randomUUID().toString());
        }
        DialogFragment f = null;
        if (preference instanceof CustomDialogPreference) {
            f = CustomDialogPreference.CustomPreferenceDialogFragment
                    .newInstance(preference.getKey());
        } else {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), "dialog_preference");
        onDialogShowing();
    }

    private void handleTogglePowerButtonEndsCallPreferenceClick() {
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR, (mPowerEndCall.isChecked()
                        ? Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_HANGUP
                        : Settings.Secure.INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF));
    }

    private void handleToggleHomeButtonAnswersCallPreferenceClick() {
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.RING_HOME_BUTTON_BEHAVIOR, (mHomeAnswerCall.isChecked()
                        ? Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_ANSWER
                        : Settings.Secure.RING_HOME_BUTTON_BEHAVIOR_DO_NOTHING));
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.button_settings) {

        @Override
        public List<String> getNonIndexableKeys(Context context) {
            final List<String> result = super.getNonIndexableKeys(context);

            if (!TelephonyUtils.isVoiceCapable(context)) {
                result.add(KEY_POWER_END_CALL);
                result.add(KEY_HOME_ANSWER_CALL);
                result.add(KEY_VOLUME_ANSWER_CALL);
            }

            if (!DeviceUtils.hasBackKey(context)) {
                result.add(CATEGORY_BACK);
                result.add(KEY_BACK_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingHomeKey(context)) {
                result.add(KEY_BACK_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasHomeKey(context)) {
                result.add(CATEGORY_HOME);
                result.add(KEY_HOME_LONG_PRESS);
                result.add(KEY_HOME_DOUBLE_TAP);
                result.add(KEY_HOME_ANSWER_CALL);
                result.add(KEY_HOME_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingHomeKey(context)) {
                result.add(KEY_HOME_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasMenuKey(context)) {
                result.add(CATEGORY_MENU);
                result.add(KEY_MENU_PRESS);
                result.add(KEY_MENU_LONG_PRESS);
                result.add(KEY_MENU_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingMenuKey(context)) {
                result.add(KEY_MENU_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasAssistKey(context)) {
                result.add(CATEGORY_ASSIST);
                result.add(KEY_ASSIST_PRESS);
                result.add(KEY_ASSIST_LONG_PRESS);
                result.add(KEY_ASSIST_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingAssistKey(context)) {
                result.add(KEY_ASSIST_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasAppSwitchKey(context)) {
                result.add(CATEGORY_APPSWITCH);
                result.add(KEY_APP_SWITCH_PRESS);
                result.add(KEY_APP_SWITCH_LONG_PRESS);
                result.add(KEY_APP_SWITCH_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingAppSwitchKey(context)) {
                result.add(KEY_APP_SWITCH_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasCameraKey(context)) {
                result.add(CATEGORY_CAMERA);
                result.add(KEY_CAMERA_LAUNCH);
                result.add(KEY_CAMERA_SLEEP_ON_RELEASE);
                result.add(KEY_CAMERA_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingCameraKey(context)) {
                result.add(KEY_CAMERA_WAKE_SCREEN);
            }

            if (!DeviceUtils.hasVolumeKeys(context)) {
                result.add(CATEGORY_VOLUME);
                result.add(KEY_SWAP_VOLUME_BUTTONS);
                result.add(KEY_VOLUME_ANSWER_CALL);
                result.add(KEY_VOLUME_KEY_CURSOR_CONTROL);
                result.add(KEY_VOLUME_MUSIC_CONTROLS);
                result.add(KEY_VOLUME_PANEL_ON_LEFT);
                result.add(KEY_VOLUME_WAKE_SCREEN);
            } else if (!DeviceUtils.canWakeUsingVolumeKeys(context)) {
                result.add(KEY_VOLUME_WAKE_SCREEN);
            }

            if (hasNavigationBar()) {
                if (DeviceUtils.isEdgeToEdgeEnabled(context)) {
                    result.add(KEY_NAVIGATION_ARROW_KEYS);
                    result.add(KEY_NAVIGATION_HOME_LONG_PRESS);
                    result.add(KEY_NAVIGATION_HOME_DOUBLE_TAP);
                    result.add(KEY_NAVIGATION_APP_SWITCH_LONG_PRESS);
                } else if (DeviceUtils.isSwipeUpEnabled(context)) {
                    result.add(KEY_NAVIGATION_APP_SWITCH_LONG_PRESS);
                    result.add(KEY_EDGE_LONG_SWIPE);
                } else {
                    result.add(KEY_EDGE_LONG_SWIPE);
                }
            }
            return result;
        }
    };
}
