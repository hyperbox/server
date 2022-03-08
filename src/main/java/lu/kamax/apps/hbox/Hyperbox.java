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

package lu.kamax.apps.hbox;

import lu.kamax.apps.hbox.vbox.Hypervisor;
import lu.kamax.apps.hbox.vbox.VBoxWebSrv;
import lu.kamax.apps.hbox.vbox6_1.VBox6_1;
import org.apache.commons.lang3.StringUtils;

public class Hyperbox {

    private Hypervisor hypervisor;

    public void start() {
        VBoxWebSrv vboxSrv = new VBoxWebSrv();
        String version = vboxSrv.getVersion();

        if (StringUtils.startsWith(version, "6.0.")) version = "6.0";
        if (StringUtils.startsWith(version, "6.1.")) version = "6.1";

        if (StringUtils.equals("6.1", version)) {
            hypervisor = new VBox6_1(vboxSrv);
            hypervisor.start();
            return;
        }

        throw new HyperboxException("No hypervisor found");
    }

    public void stop() {
        hypervisor.stop();
    }

    public Hypervisor hypervisor() {
        return hypervisor;
    }

}
