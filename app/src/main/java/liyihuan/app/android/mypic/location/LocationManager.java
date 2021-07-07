package liyihuan.app.android.mypic.location;


import liyihuan.app.android.ipc.ServiceId;

@ServiceId("LocationManager")
public class LocationManager {

    private static final LocationManager ourInstance = new LocationManager();

    public static LocationManager getDefault() {
        return ourInstance;
    }

    private LocationManager() {
    }

    private Location location;


    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

}

