package liyihuan.app.android.ipc

import android.content.Context
import liyihuan.app.android.ipc.model.Request
import java.lang.reflect.Proxy

/**
 * @ClassName: IPC
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/1 22:10
 */
object IPC {
    /**
     * 给服务使用，注册一个服务
     */
    @JvmStatic
    fun register(clazz: Class<*>) {
        IPCHelp.instance.registerService(clazz)
    }

    /**
     * 给客户端用
     * packageName 跨app使用
     */
    @JvmStatic
    fun connect(context: Context, service: Class<out IPCService?>, packageName: String? = null) {
        IPCHelp.instance.connectService(context, packageName, service)
    }

    /**
     * 动态代理获取instance
     * 传入需要代理类的interface
     */
    @JvmStatic
    open fun <T> getInstanceWithName(
        service: Class<out IPCService?>,
        classType: Class<T>, methodName: String?, parameters: Array<Any>? = null
    ): T? {
        if (!classType.isInterface) {
            //抛异常！
            throw Exception("不能传非interface的Class")
        }
        // 获取传入类的注解
        val serviceId = classType.getAnnotation(ServiceId::class.java)

        // 先自己调用获取到代理类的实例
        val response = IPCHelp.instance.send(
            Request.GET_INSTANCE, service, serviceId.value, methodName,
            parameters
        )
        // 获取成功了就通过动态代理去调用其他方法
        return if (response.isSuccess) {
            Proxy.newProxyInstance(
                classType.classLoader, arrayOf(classType),
                IPCInvocationHandler(service, serviceId.value)
            ) as T
        } else null
    }

}
