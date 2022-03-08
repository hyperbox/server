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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.Socket;
import java.util.Random;

public class NetUtil {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int MAX_PORT = 65535;

    public static boolean isPortAvailable(String host, int port) {
        Socket s = null;
        try {
            s = new Socket(host, port);
            return false; // port is not available since we could connect
        } catch (IOException e) {
            return true; // port is available
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (Throwable t) {
                    log.warn("Error while releasing port " + port, t);
                }
            }
        }
    }

    public static int getNextAvailablePort(String host, int start) throws IOException {
        while (start <= MAX_PORT) {
            log.info("Trying port " + start);
            if (isPortAvailable(host, start)) {
                return start;
            } else {
                start++;
            }
        }
        throw new IOException("No available port was found");
    }

    public static int getRandomAvailablePort(String host, int start, int tryAmount) throws IOException {
        int i = 0;
        Random rand = new Random();
        while (i < tryAmount) {
            int port = rand.nextInt((MAX_PORT - start) + 1) + start;
            log.info("Trying port " + start);
            if (isPortAvailable(host, port)) {
                return port;
            } else {
                tryAmount++;
            }
        }
        throw new IOException("Could not find available port after " + tryAmount + " tries");
    }

}
