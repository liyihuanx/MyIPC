package liyihuan.app.android.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import liyihuan.app.android.ipc.model.Parameters
import liyihuan.app.android.ipc.model.Request
import liyihuan.app.android.ipc.model.Response
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * @ClassName: IPCHelp
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/1 21:13
 */
class IPCHelp private constructor() {
    // 线程安全的懒汉式
//    companion object {
//        private var instance: IPCHelp? = null
//            get() {
//                if (field == null) {
//                    field = IPCHelp()
//                }
//                return field
//            }
//        @Synchronized
//        fun get(): IPCHelp {
//            return instance!!
//        }
//    }

    // 双重锁
    companion object {
        val instance: IPCHelp by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            IPCHelp()
        }
    }

    // 服务表
    private val mServices = ConcurrentHashMap<String, Class<*>>()

    // 方法表
    private val mMethods = ConcurrentHashMap<Class<*>, ConcurrentHashMap<String, Method>>()

    // 单例保存
    private val mInstance = ConcurrentHashMap<String, Any>()

    // Binder遥控器保存
    private val binders: ConcurrentHashMap<Class<out IPCService?>, AIPCService> =
        ConcurrentHashMap()

    /**
     * 注册一个服务，服务端用
     */
    fun registerService(clazz: Class<*>) {
        //1.服务id 与 class的表
        val serviceId = clazz.getAnnotation(ServiceId::class.java)
            ?: throw RuntimeException("必须使用ServiceId注解的服务才能注册！")
        val value = serviceId.value
        mServices[value] = clazz

        //2. clazz 和 method
        var methodMap = mMethods[clazz]
        if (methodMap == null) {
            methodMap = ConcurrentHashMap()
            mMethods[clazz] = methodMap
        }

        val methods = clazz.methods
        methods.forEach {
            // 拼接一个方法名
            val methodName = StringBuilder(it.name)
            methodName.append("(")

            if (it.parameterTypes.isNotEmpty()) {
                it.parameterTypes.forEachIndexed { index, parameter ->
                    if (index == 0) {
                        methodName.append(parameter.name)
                    } else {
                        methodName.append(",").append(parameter.name)
                    }
                }
            }

            methodName.append(")")

            methodMap[methodName.toString()] = it
        }

        printHashMap()

    }


    /**
     * 绑定一个服务，客户端用
     */
    fun connectService(context: Context, packageName: String?, service: Class<out IPCService?>) {
        val intent: Intent
        if (!TextUtils.isEmpty(packageName)) {
            //跨app的绑定
            intent = Intent()
            intent.setClassName(packageName!!, service.name)
        } else {
            intent = Intent(context, service)
        }
        context.bindService(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                    // 客户端拿到服务端遥控器
                    val binder = AIPCService.Stub.asInterface(p1)
                    // 连接成功后做保存
                    binders[service] = binder

                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                    binders.remove(service)
                }
            },
            Context.BIND_AUTO_CREATE
        )
    }


    /**
     * 客户端发送一个请求给Binder
     */
    fun send(
        type: Int, service: Class<out IPCService?>, serviceId: String,
        methodName: String?, parameters: Array<Any>?
    ): Response {
        val request = Request(type, serviceId, methodName, makeParameters(parameters))
        val binder: AIPCService = binders[service]!!
        return try {
            binder.send(request)
        } catch (e: RemoteException) {
            e.printStackTrace()
            //也可以把null变成错误信息
            Response(null, false)
        }
    }


    fun putInstanceObject(serviceId: String, instance: Any) {
        mInstance[serviceId] = instance
    }

    fun getInstanceObject(serviceId: String): Any? {
        return mInstance[serviceId]
    }

    private fun makeParameters(objects: Array<Any>?): Array<Parameters?>? {
        val parameters: Array<Parameters?>
        if (objects != null) {
            parameters = arrayOfNulls<Parameters>(objects.size)
            for (i in objects.indices) {
                parameters[i] = Parameters(
                    objects[i].javaClass.name,
                    Gson().toJson(objects[i])
                )
            }
        } else {
            parameters = arrayOfNulls<Parameters>(0)
        }
        return parameters
    }


    fun findMethod(
        serviceId: String,
        methodName: String,
        restoreParameters: Array<Any?>
    ): Method? {
        val clazz = mServices[serviceId]
        val concurrentHashMap = mMethods[clazz]

        val builder = java.lang.StringBuilder(methodName)
        builder.append("(")
        if (restoreParameters.isNotEmpty()) {
            builder.append(restoreParameters[0]!!.javaClass.name)
        }
        for (i in 1 until restoreParameters.size) {
            builder.append(",").append(restoreParameters[i]!!.javaClass.name)
        }
        builder.append(")")

        return concurrentHashMap?.get(builder.toString())
    }


    /**
     * 打印一下两个HashMap
     */
    private fun printHashMap() {
        val entries: Set<Map.Entry<String, Class<*>>> = mServices.entries
        for ((key, value1) in entries) {
            Log.d("QWER", "服务表:$key = $value1")
        }

        val entrySet: Set<Map.Entry<Class<*>, ConcurrentHashMap<String, Method>>> =
            mMethods.entries
        for ((key, value1) in entrySet) {
            Log.d("QWER", "方法表：$key")
            for ((key1) in value1) {
                Log.d("QWER", " $key1")
            }
        }
    }
}