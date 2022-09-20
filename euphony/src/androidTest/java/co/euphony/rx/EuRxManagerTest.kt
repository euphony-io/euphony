import androidx.test.ext.junit.runners.AndroidJUnit4
import co.euphony.rx.EuRxManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EuRxManagerTest {

    @Test(expected = Test.None::class) /* no exception expected */
    fun withExceptionHandlingNoErrorOccurs() {
        val rxManager = EuRxManager.getInstance()
        rxManager.listen()
    }

    @Test
    fun testListenWithTimeout() {
        val rxManager = EuRxManager.getInstance()
        rxManager.listen(1000)
        assertEquals(EuRxManager.RxManagerStatus.RUNNING, rxManager.status)

        Thread.sleep(1100)
        assertEquals(EuRxManager.RxManagerStatus.STOP, rxManager.status)
    }
}