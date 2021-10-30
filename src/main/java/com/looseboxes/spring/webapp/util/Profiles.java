package com.looseboxes.spring.webapp.util;

import org.springframework.core.env.Environment;

/**
 * @author hp
 */
public final class Profiles {
    public static final String PRODUCTION = "prod";
    public static final String DEVELOPMENT = "dev";
    
    private Profiles() {}
    
    public static boolean isProductionProfile(Environment env) {
        return isProfileActive(env, PRODUCTION);
    }

    public static boolean isDevelopmentProfile(Environment env) {
        return isProfileActive(env, DEVELOPMENT);
    }

    public static boolean isAnyProfileActive(Environment env, String... profiles) {
        final String [] activeProfiles = env.getActiveProfiles();
        for(String activeProfile : activeProfiles) {
            if(containsIgnoreCase(activeProfile, profiles)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isProfileActive(Environment env, String profile) {
        final String [] activeProfiles = env.getActiveProfiles();
        return containsIgnoreCase(profile, activeProfiles);
    }

    private static boolean containsIgnoreCase(String s, String... arr) {
        for(String e : arr) {
            if(e.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
