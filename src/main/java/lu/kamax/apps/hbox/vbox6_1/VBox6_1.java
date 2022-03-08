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

import lu.kamax.apps.hbox.vbox.Hypervisor;
import lu.kamax.apps.hbox.vbox.VMachine;
import lu.kamax.apps.hbox.vbox.VBoxWebSrv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtualbox_6_1.VirtualBoxManager;

import javax.xml.ws.WebServiceException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

public class VBox6_1 implements Hypervisor {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final VBoxWebSrv webSvc;
    private VirtualBoxManager mgr;
    private Thread eventMgr;

    public VBox6_1(VBoxWebSrv webSvc) {
        this.webSvc = webSvc;
    }

    public void start() {
        webSvc.start();

        mgr = VirtualBoxManager.createInstance(null);
        log.info("Connecting to VirtualBox");
        mgr.connect("http://localhost:" + webSvc.getPort(), "", "");
        log.info("Connected to VirtualBox");

        eventMgr = new Thread(new EventListenerService(mgr));
        eventMgr.setDaemon(true);
        eventMgr.start();
    }

    public void stop() {
        try {
            eventMgr.interrupt();
            eventMgr.join(5000);
        } catch (InterruptedException e) {
            // nothing to be done
        }

        try {
            mgr.disconnect();
        } catch (WebServiceException e) {
            // already disconnected
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        try {
            mgr.cleanup();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            mgr = null;
        }

        try {
            webSvc.stop();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return !mgr.getVBox().getVersion().isEmpty();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "virtualbox";
    }

    @Override
    public String getTypeId() {
        return "ws";
    }

    @Override
    public String getVersion() {
        return mgr.getVBox().getVersion();
    }

    @Override
    public String getRevision() {
        return mgr.getVBox().getRevision().toString();
    }

    @Override
    public List<VMachine> geMachines() {
        return mgr.getVBox()
                .getMachines()
                .stream()
                .map(iVM -> new VBoxMachine(mgr, iVM))
                .collect(Collectors.toList());
    }

    @Override
    public VMachine getMachine(String idOrName) {
        return new VBoxMachine(mgr, mgr.getVBox().findMachine(idOrName));
    }

}
