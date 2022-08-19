/*----------------------------------------------------------------------
	FILE        : CountdownScheduler.java
	AUTHOR      : JavaApp1-Jun-2022 Group
	LAST UPDATE : 13.08.2022

	CountdownScheduler class

	Copyleft (c) 1993 by C and System Programmers Association (CSD)
	All Rights Free
-----------------------------------------------------------------------*/
package org.csystem.app;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public abstract class CountDownScheduler {
    private final long m_millisInFuture;
    private final long m_interval;
    private final TimerTask m_timerTask;
    private final Timer m_timer;

    private TimerTask createTimerTask()
    {
        return new TimerTask(){
            private long m_value;
            public void run()
            {
                var millisUntilFinished = m_millisInFuture - m_value;
                onTick(millisUntilFinished);
                m_value += m_interval;

                if (m_value < m_millisInFuture)
                    return;
                onFinish();
                m_timer.cancel();
            }
        };
    }
    protected CountDownScheduler(long millisInFuture, long interval){
        this(millisInFuture, interval, MILLISECONDS);
    }

    protected CountDownScheduler(long durationInFuture, long interval, TimeUnit timeUnit){
        m_millisInFuture = timeUnit == MILLISECONDS ? durationInFuture : MILLISECONDS.convert(durationInFuture, timeUnit);
        m_interval = timeUnit == MILLISECONDS ? interval : MILLISECONDS.convert(interval, timeUnit);
        m_timer = new Timer();
        m_timerTask = createTimerTask();
    }

    protected abstract void onTick(long millisUntilFinished);

    protected abstract void onFinish();

    public final CountDownScheduler start()
    {
        m_timer.scheduleAtFixedRate(m_timerTask, 0, m_interval);
        return this;
    }

    public final void cancel()
    {
        m_timer.cancel();
    }

}

class CountDownSchedulerTest{
    public static void run(){
        var scheduler = new CountDownScheduler(10000, 1000){
            private int m_count;
            protected void onTick(long millisUntilFinished)
            {
                ++m_count;
                Console.write("%02d\r", millisUntilFinished / 1000);
            }

            protected void onFinish()
            {
                Console.writeLine("00");
                Console.writeLine("Count: %d", m_count);
                Console.writeLine("Geri sayım tamamlandı!");
            }
        }.start();


    }

}
