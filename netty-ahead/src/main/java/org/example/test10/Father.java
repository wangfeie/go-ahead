package org.example.test10;

/**
 * @Author: PP-jessica
 * @Description:这是一个具体的观察者
 */
public class Father implements Listener{
    @Override
    public void doSomething() {
        System.out.println("观察者--父亲：给孩子买点橘子，留给孩子一个苍老的背影");
    }
}