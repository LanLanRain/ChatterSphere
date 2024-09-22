package com.lb.live.user.provider.rpc;

import com.lb.live.user.interfaces.rpc.IUserRpc;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Override
    public String test() {
        System.out.println("This is a dubbo test.");
        return "success";
    }
}
