package br.com.gestorreservas.Shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Shared {

    public static final void putIntegerSet(final Context context, final String key, final Set<Integer> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, value.stream().map(Object::toString).collect(Collectors.toSet()));
        editor.commit();
    }

    public static final Set<Integer> getIntegerSet(final Context context, final String key) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getStringSet(key, new HashSet<>())
                .stream()
                .map( v -> Integer.valueOf(v))
                .collect(Collectors.toSet());
    }
}
