package io.collective;

import java.time.Clock;
import java.time.Instant;

public class SimpleAgedCache {
    Clock clock = Clock.systemUTC();
    ExpirableEntryLinkedList newestEntry;

    public SimpleAgedCache() {}

    public SimpleAgedCache(Clock clock) {
        this();
        this.clock = clock;
    }

    class ExpirableEntryLinkedList {
        Object key;
        Object value;
        Instant expirationTime;
        ExpirableEntryLinkedList next;

        ExpirableEntryLinkedList(Object key, Object value, Instant expirationTime) {
            this.key = key;
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }

    public void put(Object key, Object value, int retentionInMillis) {
        Instant expirationTime = clock.instant().plusMillis(retentionInMillis);
        ExpirableEntryLinkedList newEntry = new ExpirableEntryLinkedList(key, value, expirationTime);

        if (newestEntry != null) {
            newEntry.next = newestEntry;
        }
        newestEntry = newEntry;
    }

    public int size() {
        // Get the current Instant
        Instant rightNow = clock.instant();

        // Define a variable to hold the count of elements that are not expired
        int count = 0;

        // If the newestEntry is null or expired return 0
        if (newestEntry == null || newestEntry.expirationTime.isBefore(rightNow)) {
            newestEntry = null;
            return count;
        }

        ExpirableEntryLinkedList iterEntry = newestEntry;
        ExpirableEntryLinkedList prevEntry = null;

        // Iterate through the linked list and count the number of elements that are not expired
        while (iterEntry != null) {
            if (iterEntry.expirationTime.isAfter(rightNow)) {
                count++;
                prevEntry = iterEntry;
                iterEntry = iterEntry.next;
            } else {
                // The element is expired
                // Set the oldest unexpired element.next to null
                if (prevEntry != null) {
                    prevEntry.next = null;
                }
                break;
            }
        }
        // Return the count
        return count;
    }

    public boolean isEmpty() {
        // If size is 0 it is empty return true
        return size() == 0;
    }

    public Object get(Object key) {
        // Get the current Instant
        Instant rightNow = clock.instant();

        // If the newestEntry is null or expired return null
        if (newestEntry == null || newestEntry.expirationTime.isBefore(rightNow)) {
            newestEntry = null;
            return null;
        }

        ExpirableEntryLinkedList iterEntry = newestEntry;
        ExpirableEntryLinkedList prevEntry = null;

        // Iterate through the linked list and return the value of the key if it is not expired
        while (iterEntry != null) {
            if (iterEntry.key.equals(key)) {
                if (iterEntry.expirationTime.isAfter(rightNow)) {
                    return iterEntry.value;
                } else {
                    // The element is expired
                    // Set the oldest unexpired element.next to null
                    if (prevEntry != null) {
                        prevEntry.next = null;
                    }
                    break;
                }
            }
            prevEntry = iterEntry;
            iterEntry = iterEntry.next;
        }
        return null;
    }
}