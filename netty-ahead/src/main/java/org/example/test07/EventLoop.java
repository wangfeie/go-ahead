package org.example.test07;

/**
 * 抽象 BioEventLoop、NioEventLoop 的一个接口
 * EventLoop 需要有 register() 被调用，让 SingleThreadEventLoop 去实现，由于 EventLoopGroup 中得方法都将由 EventLoop 最终实现，所以直接使用 EventLoop 继承 EventLoopGroup 中得方法
 * <p>
 * 这样，EventLoop 就不必定义方法，方法都定义在 EventLoopGroup 中，但是在 NioEventLoopGroup、SingleThreadEventLoop 中都可以得到实现，
 * 如果你创建的是 NioEventLoopGroup 的对象，那么你调用register 方法逻辑就会来到 NioEventLoopGroup 类中的 next 方法中，该方法为你返回一个 EventLoop 对象，然后再执行该对象的 register 方法。
 * 如果你创建的是 NioEventLoop 的对象，那么调用该方法就会直接为你注册客户端连接。
 * <p>
 * NioEventLoop 这个类在保持了 EventLoop 接口身份的同时，只要创建了它的对象，实际上在它背后就创建了一个单线程执行器。然后单线程执行器和 NioEventLoop 一起合作，处理来自客户端的 IO 事件。
 */
public interface EventLoop extends EventExecutor, EventLoopGroup {


}
