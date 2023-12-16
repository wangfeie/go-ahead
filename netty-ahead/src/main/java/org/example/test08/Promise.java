package org.example.test08;

import java.util.concurrent.Future;

/**
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/13 19:17
 */
public interface Promise<V> extends Runnable, Future<V> {

}
