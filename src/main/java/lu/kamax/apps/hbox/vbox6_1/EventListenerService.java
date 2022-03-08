/*
 * Hyperbox - Virtual Infrastructure Manager
 * Copyright (C) 2022 Max Dor
 *
 * https://apps.kamax.lu/hyperbox
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lu.kamax.apps.hbox.vbox6_1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_6_1.IEvent;
import org.virtualbox_6_1.IEventListener;
import org.virtualbox_6_1.VBoxEventType;
import org.virtualbox_6_1.VirtualBoxManager;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Objects;

public class EventListenerService implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final VirtualBoxManager mgr;

    public EventListenerService(VirtualBoxManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public void run() {
        IEventListener el = mgr.getVBox().getEventSource().createListener();
        mgr.getVBox().getEventSource().registerListener(el, Collections.singletonList(VBoxEventType.Any), false);

        while (!Thread.currentThread().isInterrupted()) {
            mgr.waitForEvents(0L);
            IEvent rawEvent = mgr.getVBox().getEventSource().getEvent(el, 1000);
            if (Objects.isNull(rawEvent)) {
                continue;
            }

            try {
                log.info("Got an event from Virtualbox: {} - {}", rawEvent.getClass().getName(), rawEvent.getType());
            } finally {
                mgr.getVBox().getEventSource().eventProcessed(el,rawEvent);
                rawEvent.releaseRemote();
            }
        }

        mgr.getVBox().getEventSource().unregisterListener(el);
        el.releaseRemote();

        log.info("VBox Event Listener stopped");
    }

}
