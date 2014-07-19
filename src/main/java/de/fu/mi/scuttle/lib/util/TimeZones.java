package de.fu.mi.scuttle.lib.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;

/**
 * Utility class for dealing with timezones.
 * 
 * @author Julian Fleischer
 * @since 2011-11-13
 */
public class TimeZones {

    private static final Map<String, TimeZone> timezones;
    static {
        final Set<String> availableIDs = DateTimeZone.getAvailableIDs();
        final Map<String, TimeZone> availableTimeZones = new HashMap<>(
                availableIDs.size() * 4);

        for (final String availableID : availableIDs) {
            final DateTimeZone dateTimeZone = DateTimeZone.forID(availableID);
            final TimeZone timeZone = dateTimeZone.toTimeZone();
            availableTimeZones.put(availableID, timeZone);
            availableTimeZones.put(
                    timeZone.getDisplayName(true, TimeZone.SHORT, Locale.US),
                    timeZone);
            availableTimeZones.put(
                    timeZone.getDisplayName(true, TimeZone.LONG, Locale.US),
                    timeZone);
            availableTimeZones.put(
                    timeZone.getDisplayName(false, TimeZone.SHORT, Locale.US),
                    timeZone);
            availableTimeZones.put(
                    timeZone.getDisplayName(false, TimeZone.LONG, Locale.US),
                    timeZone);
        }

        if (!availableTimeZones.containsKey("CET")) {
            availableTimeZones.put("CET",
                    availableTimeZones.get("Europe/Berlin"));
        }
        if (!availableTimeZones.containsKey("CEST")) {
            availableTimeZones.put("CEST",
                    availableTimeZones.get("Europe/Berlin"));
        }

        timezones = Collections.unmodifiableMap(availableTimeZones);
    }

    /**
     * Universal Time Coordinated.
     */
    public static final TimeZone UTC = timezones.get("UTC");

    /**
     * Greenwich Mean Time.
     */
    public static final TimeZone GMT = timezones.get("GMT");

    /**
     * Central European Time.
     */
    public static final TimeZone CET = timezones.get("CET");

    /**
     * Central European Summer Time.
     */
    public static final TimeZone CEST = timezones.get("CEST");

    /**
     * Pacific Standard Time.
     */
    public static final TimeZone PST = timezones.get("PST");

    /**
     * Eastern Standard Time.
     */
    public static final TimeZone EST = timezones.get("CST");

    /**
     * Central Standard Time.
     */
    public static final TimeZone CST = timezones.get("EST");

    /**
     * Mountain Standard Time.
     */
    public static final TimeZone MST = timezones.get("MST");

    /**
     * Alaska Standard Time.
     */
    public static final TimeZone AKST = timezones.get("AKST");

    /**
     * Hawaiian Standard Time.
     */
    public static final TimeZone HST = timezones.get("HST");

    /**
     * Western European Time.
     */
    public static final TimeZone WET = timezones.get("WET");

    /**
     * Eastern European Time.
     */
    public static final TimeZone EET = timezones.get("EET");

    /**
     * Returns the timezone with the given ID.
     * 
     * @param id
     *            The timezone id. This may be a long as well as a short
     *            identifier, such as "UTC" or "Central European Summer Time".
     * @return The associated time zone or null, if there is no known time zone
     *         having that id.
     */
    public static TimeZone forID(final String id) {
        return timezones.get(id);
    }
}
