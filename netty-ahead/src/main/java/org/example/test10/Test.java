
package org.example.test10;

/**
 * 观察者模式测试
 */
public class Test {

    public static void main(String[] args) {
        //创建两个观察者
        Father father = new Father();
        Mother mother = new Mother();

        //创建一个被观察者
        YoungPeople youngPeople = new YoungPeople();

        //观察者开始观察被观察者
        youngPeople.addListener(father).addListener(mother);

        //被观察者开始要去上班了
        youngPeople.toWork();
    }
}