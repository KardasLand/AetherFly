package com.kardasland.aetherfly.wrappers;

public interface LocaleWrapper {

    /**
     * Translate time to locale
     * @param time time in seconds
     * @param compact
     * @return
     */

    String translateLocaleTime(long time, boolean compact);
}
