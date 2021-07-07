package liyihuan.app.android.mypic.location;

import liyihuan.app.android.ipc.ServiceId;

/**
 * @ClassName: ILocation
 * @Description: java类作用描述
 * @Author: liyihuan
 * @Date: 2021/7/1 22:10
 */
@ServiceId("LocationManager")
public interface ILocation {

    Location getLocation();
}
