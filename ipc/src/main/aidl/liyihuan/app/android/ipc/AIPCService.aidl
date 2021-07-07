// AIPCService.aidl
package liyihuan.app.android.ipc;

import liyihuan.app.android.ipc.model.Request;
import liyihuan.app.android.ipc.model.Response;


interface AIPCService {

    Response send(in Request request);

}
