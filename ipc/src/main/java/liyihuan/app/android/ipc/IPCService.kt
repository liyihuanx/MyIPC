package liyihuan.app.android.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.gson.Gson
import liyihuan.app.android.ipc.model.Parameters
import liyihuan.app.android.ipc.model.Request
import liyihuan.app.android.ipc.model.Response

/**
 * @ClassName: IPCService
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/1 20:47
 */
abstract class IPCService : Service() {

    var gson = Gson()


    override fun onBind(p0: Intent?): IBinder? {
        return object : AIPCService.Stub() {
            override fun send(request: Request): Response? {
                val restoreParameters = restoreParameters(request.parameters)
                val methodName = request.methodName
                val serviceId = request.serviceId

                val findMethod =
                    IPCHelp.instance.findMethod(serviceId, methodName, restoreParameters)

                when (request.type) {
                    Request.GET_INSTANCE -> {

                        return try {
                            //getInstance 加 【*】表示为可变参数
                            val instanceObject = findMethod?.invoke(null, *restoreParameters)

                            //getInstance
                            IPCHelp.instance.putInstanceObject(serviceId, instanceObject!!)
                            Response(null, true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Response(null, false)
                        }
                    }

                    Request.GET_METHOD -> {
                        return try {
                            val instanceObject = IPCHelp.instance.getInstanceObject(serviceId)
                            val invoke = findMethod?.invoke(instanceObject, *restoreParameters)
                            Response(gson.toJson(invoke), true)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Response(null, false)
                        }

                    }

                }
                return null
            }
        }
    }


    protected open fun restoreParameters(parameters: Array<Parameters>): Array<Any?> {
        val objects = arrayOfNulls<Any>(parameters.size)
        parameters.forEachIndexed { i, it ->
            try {
                objects[i] = gson.fromJson(
                    it.value,
                    Class.forName(it.type)
                )
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return objects
    }

    class IPCService0 : IPCService()

    class IPCService1 : IPCService()

    class IPCService2 : IPCService()
}