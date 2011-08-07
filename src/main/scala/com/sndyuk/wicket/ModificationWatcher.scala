package com.sndyuk.wicket

import java.util.ArrayList
import java.util.Iterator
import java.util.Map
import java.util.Set
import java.util.concurrent.ConcurrentHashMap

import org.apache.wicket.util.listener.ChangeListenerSet
import org.apache.wicket.util.listener.IChangeListener
import org.apache.wicket.util.time.Duration
import org.apache.wicket.util.time.Time
import org.apache.wicket.util.watch.IModifiable
import org.apache.wicket.util.watch.IModificationWatcher

class ModificationWatcher extends IModificationWatcher {
  class Entry {
    val listeners = new ChangeListenerSet
    var lastModifiedTime: Time = null
    var modifiable: IModifiable = null
  }

  private val modifiableToEntry = new ConcurrentHashMap[IModifiable, Entry]

  override def add(modifiable: IModifiable, listener: IChangeListener) = {

    val entry = modifiableToEntry.get(modifiable)
    if (entry == null) {
      val lastModifiedTime = modifiable.lastModifiedTime
      if (lastModifiedTime != null) {
        val newEntry = new Entry

        newEntry.modifiable = modifiable
        newEntry.lastModifiedTime = lastModifiedTime
        newEntry.listeners.add(listener)

        modifiableToEntry.put(modifiable, newEntry)
      }

      true

    } else {

      entry.listeners.add(listener)
    }
  }

  override def destroy = {}

  override def getEntries = {
    modifiableToEntry.keySet
  }

  override def remove(modifiable: IModifiable) = {
    val entry = modifiableToEntry.remove(modifiable)
    if (entry != null) {
      entry.modifiable
    }
    null
  }

  override def start(pollFrequency: Duration) = {
    val iterator = new ArrayList[Entry](modifiableToEntry.values()).iterator()
    while (iterator.hasNext()) {
      val entry = iterator.next

      val modifiableLastModified = entry.modifiable.lastModifiedTime
      if ((modifiableLastModified != null)
        && modifiableLastModified.after(entry.lastModifiedTime)) {
        entry.listeners.notifyListeners
        entry.lastModifiedTime = modifiableLastModified
      }
    }
  }
}
