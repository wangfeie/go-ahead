package org.example.test10;

/**
 * @Author: PP-jessica
 * @Description:这是一个具体的观察者
 */
public class Mother implements Listener{
    @Override
    public void doSomething() {
        System.out.println("观察者--母亲：对坐在车里的孩子挥手，祝他一路顺风");
    }
}