package com.oselan.commons.exceptions;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.helpers.CyclicBuffer;

public class BufferedSMTPAppender extends SMTPAppender {

	private int bufferSizeTrigger = 20; // Maximum number of events to buffer

	private long intervalTrigger = 1440; // Time interval for flushing in minutes

	private int bufferMaxCapacity = 50; // Maximum number of events to send at one time.

	public int getBufferSizeTrigger() {
		return bufferSizeTrigger;
	}

	public void setBufferSizeTrigger(int bufferSizeTrigger) {
		this.bufferSizeTrigger = bufferSizeTrigger;
	}

	public long getIntervalTrigger() {
		return intervalTrigger;
	}

	public void setIntervalTrigger(long intervalTrigger) {
		this.intervalTrigger = intervalTrigger;
	}

	public int getBufferMaxCapacity() {
		return bufferMaxCapacity;
	}

	public void setBufferMaxCapacity(int bufferMaxCapacity) {
		this.bufferMaxCapacity = bufferMaxCapacity;
	}

	private CyclicBuffer<ILoggingEvent> cyclicBuffer;
	private ConcurrentLinkedQueue<ILoggingEvent> buffer = new ConcurrentLinkedQueue<ILoggingEvent>();
	private Timer flushTimer;
	private AtomicBoolean timerFlushed = new AtomicBoolean(false);

	@Override
	public void start() {
		super.start();
		flushTimer = new Timer(true);
		long intervalTriggerMs = TimeUnit.MILLISECONDS.convert(intervalTrigger, TimeUnit.MINUTES);
		flushTimer.schedule(new FlushTask(), intervalTriggerMs, intervalTriggerMs);
	}

	@Override
	public void stop() {
		super.stop();
		flushTimer.cancel();
	}

	@Override
	protected void subAppend(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent event) {

		buffer.add(event);
		this.cyclicBuffer = cb;
		if (buffer.size() >= bufferSizeTrigger) {
			sendBufferInternal();
		}
	}

	private synchronized void sendBufferInternal() {
		if (buffer.size() == 0)
			return;
		ILoggingEvent firstEvent = buffer.peek();
		ILoggingEvent event;
		/// copy events to the cyclic buffer.
		while ((event = buffer.poll()) != null && cyclicBuffer.length() <= bufferMaxCapacity)
			super.subAppend(cyclicBuffer, event);
		// clone the CyclicBuffer before sending out asynchronously
		final CyclicBuffer<ILoggingEvent> cbClone = new CyclicBuffer<ILoggingEvent>(cyclicBuffer);
		cyclicBuffer.clear();

		if (isAsynchronousSending()) {
			// perform actual sending asynchronously
			Runnable senderRunnable = new Runnable() {

				@Override
				public void run() {
					sendBuffer(cbClone, firstEvent);
				}
			};
			context.getScheduledExecutorService().execute(senderRunnable);
		} else {
			// synchronous sending
			sendBuffer(cbClone, event);

		}
	}

	@Override
	protected void sendBuffer(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent lastEventObject) {
		// only send if cb events exceed buffer size.
		if (cb.length() >= bufferSizeTrigger || timerFlushed.get()) {
			super.sendBuffer(cb, lastEventObject);
			timerFlushed.compareAndSet(true, false);
		}
	}

	private class FlushTask extends TimerTask {
		@Override
		public void run() {
			// only send if not sending already eventhough sendbuffers is sync
			if (cyclicBuffer != null && buffer.size() > 0 && timerFlushed.compareAndSet(false, true)) {
				sendBufferInternal();
			}

		}
	}
}