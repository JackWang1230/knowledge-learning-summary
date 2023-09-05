package com.wr.controller;

import com.wr.client.OpenStackClient;
import com.wr.enums.StatusCode;
import com.wr.module.ResponseData;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * OpenStack Controller
 * @author : WangRui
 * @date : 2023/4/7
 */

@RestController
@RequestMapping("clusters")
public class OpenstackController {

    @Resource
    OpenStackClient openStackClient;

    /**
     * 创建集群
     * @param clusterRequest
     * @return
     */
    @PostMapping
    public ResponseData<Server> createCluster(@RequestBody ServerCreate clusterRequest){

        Server cluster = openStackClient.createCluster(clusterRequest);

        return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), cluster);
    }


    /**
     * 启动节点
     * @param serverId 节点id
     * @return 返回结果
     */
    @PostMapping("/{serverId}/start")
    public ResponseData<?> startCluster(@PathVariable String serverId) {
        ActionResponse actionResponse = openStackClient.startCluster(serverId);
        if (actionResponse.isSuccess()){
            return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), null);
        } else {
            return new ResponseData<>(StatusCode.FAIL.getCode(),StatusCode.FAIL.getMessage(), actionResponse.getFault());
        }

    }

    /**
     * 停止节点
     * @param serverId 节点id
     * @return 返回结果
     */
    @PostMapping("/{serverId}/pause")
    public ResponseData<?> pauseCluster(@PathVariable String serverId) {
        ActionResponse actionResponse = openStackClient.pauseCluster(serverId);
        if (actionResponse.isSuccess()){
            return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), null);
        } else {
            return new ResponseData<>(StatusCode.FAIL.getCode(),StatusCode.FAIL.getMessage(), actionResponse.getFault());
        }

    }

    /**
     * 挂起节点
     * @param serverId 节点id
     * @return 返回结果
     */
    @PostMapping("/{serverId}/suspend")
    public ResponseData<?> suspendCluster(@PathVariable String serverId) {
        ActionResponse actionResponse = openStackClient.suspendCluster(serverId);
        if (actionResponse.isSuccess()){
            return new ResponseData<>(StatusCode.SUCCESS.getCode(),StatusCode.SUCCESS.getMessage(), null);
        } else {
            return new ResponseData<>(StatusCode.FAIL.getCode(),StatusCode.FAIL.getMessage(), actionResponse.getFault());
        }
    }
}
