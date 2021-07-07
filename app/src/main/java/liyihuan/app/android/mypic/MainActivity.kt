package liyihuan.app.android.mypic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import liyihuan.app.android.ipc.IPC
import liyihuan.app.android.ipc.IPCService
import liyihuan.app.android.mypic.location.ILocation

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, GpsService::class.java))

        IPC.connect(this, IPCService.IPCService0::class.java)

        tvGet.setOnClickListener {

            //代理对象
            val location = IPC.getInstanceWithName(
                IPCService.IPCService0::class.java,
                ILocation::class.java, "getDefault"
            )


            Toast.makeText(this, "当前位置:" + location?.location, Toast.LENGTH_LONG).show()
        }
    }
}