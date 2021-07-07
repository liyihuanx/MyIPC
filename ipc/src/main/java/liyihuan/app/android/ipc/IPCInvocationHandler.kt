package liyihuan.app.android.ipc

import com.google.gson.Gson
import liyihuan.app.android.ipc.model.Request
import liyihuan.app.android.ipc.model.Response
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * @ClassName: IPCInvocationHandler
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/6 21:22
 */
class IPCInvocationHandler(private val service: Class<out IPCService?>, private val serviceId: String) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, parameters: Array<Any>?): Any? {
        /**
         * 向服务器发起执行method的请求
         */
        /**
         * 向服务器发起执行method的请求
         */
        val response: Response = IPCHelp.instance
            .send(Request.GET_METHOD, service, serviceId, method!!.name, parameters)
        if (response.isSuccess) {
            //方法返回值
            val returnType = method?.returnType
            if (returnType != Void.TYPE && returnType != Void::class.java) {
                //方法执行后的返回值， json数据
                val source: String = response.source
                return Gson().fromJson(source, returnType)
            }
        }
        return null
    }

}