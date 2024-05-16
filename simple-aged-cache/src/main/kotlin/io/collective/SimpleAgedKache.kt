package io.collective

import java.time.Clock
import java.time.Instant

class SimpleAgedKache(private val clock: Clock = Clock.systemUTC()) {
    private var newestEntry: ExpirableEntryLinkedList? = null
    private var count = 0

    inner class ExpirableEntryLinkedList(
        val key: Any,
        val value: Any,
        val expirationTime: Instant,
        var next: ExpirableEntryLinkedList? = null
    )

    fun put(key: Any, value: Any, retentionInMillis: Int) {
        val expirationTime = clock.instant().plusMillis(retentionInMillis.toLong())
        val newEntry = ExpirableEntryLinkedList(key, value, expirationTime)

        newEntry.next = newestEntry
        newestEntry = newEntry
    }

    fun size(): Int {
        val rightNow = clock.instant()
        count = 0

        if (newestEntry == null || newestEntry!!.expirationTime.isBefore(rightNow)) {
            newestEntry = null
            return count
        }

        var iterEntry = newestEntry
        var prevEntry: ExpirableEntryLinkedList? = null

        while (iterEntry != null) {
            if (iterEntry.expirationTime.isAfter(rightNow)) {
                count++
                prevEntry = iterEntry
                iterEntry = iterEntry.next
            } else {
                if (prevEntry != null) {
                    prevEntry.next = null
                }
                break
            }
        }

        return count
    }

    fun isEmpty(): Boolean = size() == 0

    fun get(key: Any): Any? {
        val rightNow = clock.instant()

        if (newestEntry == null || newestEntry!!.expirationTime.isBefore(rightNow)) {
            newestEntry = null
            return null
        }

        var iterEntry = newestEntry
        var prevEntry: ExpirableEntryLinkedList? = null

        while (iterEntry != null) {
            if (iterEntry.key == key) {
                if (iterEntry.expirationTime.isAfter(rightNow)) {
                    return iterEntry.value
                } else {
                    if (prevEntry != null) {
                        prevEntry.next = null
                    }
                    break
                }
            }
            prevEntry = iterEntry
            iterEntry = iterEntry.next
        }

        return null
    }
}