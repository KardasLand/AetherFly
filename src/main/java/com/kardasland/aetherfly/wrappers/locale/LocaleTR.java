package com.kardasland.aetherfly.wrappers.locale;


import com.kardasland.aetherfly.wrappers.LocaleWrapper;

import java.util.concurrent.TimeUnit;

public class LocaleTR implements LocaleWrapper {
    @Override
    public String translateLocaleTime(long time, boolean compact) {
        int day = (int) TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - (day * 24L);
        long minute = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long second = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);
        if (compact){
            return (hours <= 9 ? "0" + hours : hours)  + ":" + (minute <= 9 ? "0" + minute : minute) + ":" + second + " ";
        }else {
            return (hours == 0 ? "" : hours + " saat ") + (minute == 0 ? "" : minute + " dakika ") + second + " saniye";
        }
    }
}
