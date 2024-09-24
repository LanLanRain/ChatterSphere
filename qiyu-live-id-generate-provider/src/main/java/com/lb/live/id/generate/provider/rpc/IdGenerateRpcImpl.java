package com.lb.live.id.generate.provider.rpc;

import com.lb.live.id.generate.interfaces.IdGenerateRpc;
import com.lb.live.id.generate.provider.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author RainSoul
 * @create 2024-09-24
 */
@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    private IdGenerateService idGenerateService;

    @Override
    public Long getSeqId(Integer id) {
        return idGenerateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return idGenerateService.getUnSeqId(id);
    }
}
