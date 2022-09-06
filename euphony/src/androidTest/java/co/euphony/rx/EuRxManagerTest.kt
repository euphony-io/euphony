import androidx.test.ext.junit.runners.AndroidJUnit4
import co.euphony.rx.EuRxManager
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EuRxManagerTest {

    @Test
    public fun testListen() {

        @Test(expected = Test.None::class) /* no exception expected */
        fun withExceptionHandlingNoErrorOccurs() {
            val rxManager = EuRxManager()
            rxManager.listen()
        }
    }
}