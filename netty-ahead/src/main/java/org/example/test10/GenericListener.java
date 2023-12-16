package org.example.test10;

/**
 * 通用监视器的接口
 */
public interface GenericListener<P extends Promise<?>> {

    void operationComplete(P promise) throws Exception;


}
