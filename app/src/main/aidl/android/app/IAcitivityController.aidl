// IAcitivityController.aidl
package android.app;

// Declare any non-default types here with import statements
import android.content.Intent;

interface IAcitivityController {
   boolean activityStarting(in Intent intent,String pkg);
}
