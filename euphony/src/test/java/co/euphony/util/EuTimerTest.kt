package co.euphony.util

import co.euphony.util.EuTimer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EuTimerTest {

    private val infiniteLoopJob: Thread = Thread {
        try {
            while (true) {
                println(System.currentTimeMillis())
                Thread.sleep(500)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Before
    fun setUp() {
        infiniteLoopJob.start()
    }

    @After
    fun finishTest() {
        if (infiniteLoopJob.isAlive) {
            infiniteLoopJob.interrupt()
        }
    }

    @Test
    fun testStart() {
        val euTimer = EuTimer(infiniteLoopJob) {}
        euTimer.start(2000L)
        assertEquals(true, infiniteLoopJob.isAlive)

        Thread.sleep(2100L)
        assertEquals(false, infiniteLoopJob.isAlive)
    }
}