import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Process
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.euphony.rx.EuRxManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EuRxManagerTest {

    private val rxManager: EuRxManager = EuRxManager()
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    public fun testListenWithContext() {
        val permissionStatus = appContext.checkPermission(
            RECORD_AUDIO,
            Process.myPid(),
            Process.myUid()
        )
        val result = rxManager.listen(appContext)
        assertEquals(permissionStatus == PERMISSION_GRANTED, result)
    }
}