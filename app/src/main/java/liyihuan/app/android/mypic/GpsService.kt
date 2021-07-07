package liyihuan.app.android.mypic

import android.app.Service
import android.content.Intent
import android.os.IBinder
import liyihuan.app.android.ipc.IPC
import liyihuan.app.android.mypic.location.Location
import liyihuan.app.android.mypic.location.LocationManager

/**
 * @ClassName: GpsService
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/1 22:12
 */
public class GpsService : Service(){
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()

        //定位
        LocationManager.getDefault().location = Location("岳麓区天之道", 1.1, 2.2)



        IPC.register(LocationManager::class.java)
    }

}