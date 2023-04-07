package com.wr.client;


import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * @author : WangRui
 * @date : 2023/4/7
 */

@Component
public class OpenStackClient {

    @Value("${openstack.keystone.url}")
    private  String authUrl;
    @Value("${openstack.keystone.project}")
    private  String projectId;
    @Value("${openstack.keystone.username}")
    private  String username;
    @Value("${openstack.keystone.password}")
    private  String password;


    public Server createCluster(ServerCreate request) {
        // Create OpenStack4j client
        OSClientV3 os = OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();

        // Create server
        ServerCreate serverCreate = Builders.server()
                .name(request.getName())
                .flavor(request.getFlavorRef())
                .image(request.getImageRef())
                .build();
        Server server = os.compute().servers().boot(serverCreate);

        // Wait for server to become active
        server = os.compute().servers().waitForServerStatus(server.getId(), Server.Status.ACTIVE, 60000, TimeUnit.NANOSECONDS);

        // Print server details
        System.out.println("Server created: " + server);
        return server;
    }

    public ActionResponse startCluster(String serverId) {
        OSClientV3 os = OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
        return os.compute().servers().action(serverId, Action.START);
    }

    public ActionResponse pauseCluster(String serverId) {
        OSClientV3 os = OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
       return os.compute().servers().action(serverId, Action.PAUSE);
    }

    public ActionResponse suspendCluster(String serverId) {
        OSClientV3 os = OSFactory.builderV3()
                .endpoint(authUrl)
                .credentials(username, password)
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();

        return os.compute().servers().action(serverId, Action.SUSPEND);

    }}
