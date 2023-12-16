package org.example.test10;

import java.util.ArrayList;

/**
 * @Author: PP-jessica
 * @Description:这是一个被观察者
 */
public class YoungPeople {

    /**
     * @Author: PP-jessica
     * @Description:这个集合用来存储所有的观察者
     */
    private final ArrayList<Listener> listeners = new ArrayList<Listener>();


    /**
     * @Author: PP-jessica
     * @Description:把观察者添加到被观察者的集合中
     */
    public YoungPeople addListener(Listener listener) {
        listeners.add(listener);
        return this;
    }


    /**
     * @Author: PP-jessica
     * @Description:去工作的方法
     */
    public void toWork() {
        System.out.println("被观察者--孩子：假期结束，孩子要返程上班了");
        for (Listener listener : listeners) {
            listener.doSomething();
        }
    }
}