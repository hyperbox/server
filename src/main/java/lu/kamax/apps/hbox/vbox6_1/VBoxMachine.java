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

import lu.kamax.apps.hbox.vbox.VMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_6_1.*;

import java.lang.invoke.MethodHandles;

public class VBoxMachine implements VMachine {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final VirtualBoxManager mgr;
    private final IMachine vm;

    public VBoxMachine(VirtualBoxManager mgr, IMachine vm) {
        this.mgr = mgr;
        this.vm = vm;
    }

    @Override
    public String getId() {
        return vm.getId();
    }

    @Override
    public String getName() {
        return vm.getName();
    }

    @Override
    public Integer powerOn() {
        ISession session = mgr.getSessionObject();
        try {
            IProgress p = vm.launchVMProcess(session, "headless", null);
            while (!p.getCompleted() && !p.getCanceled()) {
                try {
                    log.info("Power On progress: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
                            p.getPercent(),
                            p.getTimeRemaining(),
                            p.getCompleted(),
                            p.getCanceled());
                    synchronized (this) {
                        wait(500);
                    }
                } catch (InterruptedException e) {
                    //
                }
            }

            log.info("Power On completed: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
                    p.getPercent(),
                    p.getTimeRemaining(),
                    p.getCompleted(),
                    p.getCanceled());

            return p.getResultCode();
        } finally {
            if (session.getState().equals(SessionState.Locked)) {
                session.unlockMachine();
            }
            session.releaseRemote();
        }
    }

    @Override
    public Integer powerOff() {
        ISession session = mgr.getSessionObject();
        try {
            vm.lockMachine(session, LockType.Shared);
            IProgress p = session.getConsole().powerDown();
            while (!p.getCompleted() && !p.getCanceled()) {
                try {
                    log.info("Saving state progress: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
                      p.getPercent(),
                      p.getTimeRemaining(),
                      p.getCompleted(),
                      p.getCanceled());
                    synchronized (this) {
                        wait(500);
                    }
                } catch (InterruptedException e) {
                    //
                }
            }

            log.info("Power On completed: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
              p.getPercent(),
              p.getTimeRemaining(),
              p.getCompleted(),
              p.getCanceled());

            return p.getResultCode();
        } finally {
            if (session.getState().equals(SessionState.Locked)) {
                session.unlockMachine();
            }
            session.releaseRemote();
        }
    }

    @Override
    public Integer saveState() {
        ISession session = mgr.getSessionObject();
        try {
            vm.lockMachine(session, LockType.Shared);
            IProgress p = session.getMachine().saveState();
            while (!p.getCompleted() && !p.getCanceled()) {
                try {
                    log.info("Saving state progress: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
                            p.getPercent(),
                            p.getTimeRemaining(),
                            p.getCompleted(),
                            p.getCanceled());
                    synchronized (this) {
                        wait(500);
                    }
                } catch (InterruptedException e) {
                    //
                }
            }

            log.info("Power On completed: {}% - Time remaining: {} - Completed: {} - Cancelled: {}",
                    p.getPercent(),
                    p.getTimeRemaining(),
                    p.getCompleted(),
                    p.getCanceled());

            return p.getResultCode();
        } finally {
            if (session.getState().equals(SessionState.Locked)) {
                session.unlockMachine();
            }
            session.releaseRemote();
        }
    }

}
