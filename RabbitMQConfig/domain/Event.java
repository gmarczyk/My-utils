package com.scheduler.shared.event.domain.event;

import java.io.Serializable;

/**
 * Required by classes of events to ensure proper operation of the event architecture.
 * Provides Serializable interface, which is forced for objects to be serialized.
 */
public interface Event extends Serializable {

    // empty

}
