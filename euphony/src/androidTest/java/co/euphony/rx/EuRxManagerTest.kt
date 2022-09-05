import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.euphony.rx.EuRxManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EuRxManagerTest {

    private val rxManager: EuRxManager = EuRxManager()
    private val context : Context =  ApplicationProvider.getApplicationContext<Context>()

    @Test
    public fun testListen() {
        val permissionStatus = ContextCompat.checkSelfPermission(context,
            RECORD_AUDIO
        )
        val result = rxManager.listen()
        assertEquals(permissionStatus == PERMISSION_GRANTED, result)
    }
}