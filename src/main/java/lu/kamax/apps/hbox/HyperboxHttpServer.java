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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

public class HyperboxHttpServer {

    public static void main(String... args) {
        HyperboxHttpServer app = HyperboxHttpServer.create();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.start();
    }

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static HyperboxHttpServer create() {
        return new HyperboxHttpServer();
    }

    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    private final Gson g;
    private final Hyperbox h;
    private final Undertow u;

    public HyperboxHttpServer() {
        g = new GsonBuilder().disableHtmlEscaping().create();
        h = new Hyperbox();

        u = Undertow.builder().addHttpListener(9456, null, Handlers.routing()
                .get("/api/v0/info", new BlockingHandler(exchange -> {
                    JsonObject hypInfo = new JsonObject();
                    hypInfo.addProperty("name", h.hypervisor().getName());
                    hypInfo.addProperty("type", h.hypervisor().getTypeId());
                    hypInfo.addProperty("version", h.hypervisor().getVersion());
                    hypInfo.addProperty("revision", h.hypervisor().getRevision());
                    JsonObject info = new JsonObject();
                    info.add("hypervisor", hypInfo);

                    exchange.setStatusCode(200);
                    exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(g.toJson(info), StandardCharsets.UTF_8);
                }))
                .get("/api/v0/machine/", new BlockingHandler(exchange -> {
                    JsonArray vms = new JsonArray();
                    h.hypervisor().geMachines().forEach(vm -> {
                        JsonObject vmIO = new JsonObject();
                        vmIO.addProperty("id", vm.getId());
                        vmIO.addProperty("name", vm.getName());
                        vms.add(vmIO);
                    });
                    JsonObject payload = new JsonObject();
                    payload.add("items", vms);

                    exchange.setStatusCode(200);
                    exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(g.toJson(payload), StandardCharsets.UTF_8);
                }))
                .put("/api/v0/machine/{id}/do/powerOn", new BlockingHandler(exchange -> {
                    String vmId = exchange.getQueryParameters().get("id").getFirst();
                    Integer code = h.hypervisor().getMachine(vmId).powerOn();

                    JsonObject result = new JsonObject();
                    result.addProperty("code", code);
                    JsonObject payload = new JsonObject();
                    payload.add("result", result);

                    exchange.setStatusCode(200);
                    exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(g.toJson(payload), StandardCharsets.UTF_8);
                }))
                .put("/api/v0/machine/{id}/do/powerOff", new BlockingHandler(exchange -> {
                    String vmId = exchange.getQueryParameters().get("id").getFirst();
                    Integer code = h.hypervisor().getMachine(vmId).powerOff();

                    JsonObject result = new JsonObject();
                    result.addProperty("code", code);
                    JsonObject payload = new JsonObject();
                    payload.add("result", result);

                    exchange.setStatusCode(200);
                    exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(g.toJson(payload), StandardCharsets.UTF_8);
                }))
                .put("/api/v0/machine/{id}/do/saveState", new BlockingHandler(exchange -> {
                    String vmId = exchange.getQueryParameters().get("id").getFirst();
                    Integer code = h.hypervisor().getMachine(vmId).saveState();

                    JsonObject result = new JsonObject();
                    result.addProperty("code", code);
                    JsonObject payload = new JsonObject();
                    payload.add("result", result);

                    exchange.setStatusCode(200);
                    exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(g.toJson(payload), StandardCharsets.UTF_8);
                }))
        ).build();
    }

    public void start() {
        h.start();
        u.start();
        log.info("--- Hyperbox started ---");
    }

    public void stop() {
        try {
            u.stop();
        } catch (Throwable t) {
            // nothing to do
        }

        try {
            h.stop();
        } catch (Throwable t) {
            // nothing to do
        }
        log.info("--- Hyperbox stopped ---");
    }

}
