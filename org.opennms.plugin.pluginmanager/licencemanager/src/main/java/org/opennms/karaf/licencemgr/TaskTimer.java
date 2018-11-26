/*
 * Copyright 2014 OpenNMS Group Inc., Entimoss ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opennms.karaf.licencemgr;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTimer {
	private static final Logger LOG = LoggerFactory.getLogger(TaskTimer.class);

	/**
	 * if runScheduledTask returns true, the task was successful and retries will not be attempted
	 */
	public static interface ScheduledTask{
		public boolean runScheduledTask();
	}
	
	private AtomicBoolean scheduleIsRunning = new AtomicBoolean(false); 

	private ScheduledTask task = null;

	private Thread m_schedule=null;

	//retryInterval interval (ms) before retrying after unsuccessful task
	private Integer retryInterval=0;

	// retryNumber  number of retrys if unsuccessful -1= forever until successful 
	private Integer retryNumber=0;

	// updateInterval  long term update interval (ms) before re attempting task. -1= only one try on startup 
	private Integer updateInterval=-1;


	public ScheduledTask getTask() {
		return task;
	}

	public void setTask(ScheduledTask task) {
		this.task = task;
	}

	public Integer getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(Integer retryInterval) {
		this.retryInterval = retryInterval;
	}

	public Integer getRetryNumber() {
		return retryNumber;
	}

	public void setRetryNumber(Integer retryNumber) {
		this.retryNumber = retryNumber;
	}

	public Integer getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(Integer updateInterval) {
		this.updateInterval = updateInterval;
	}


	public synchronized void startSchedule(){
		if (m_schedule==null){
			if(retryInterval==null) throw new RuntimeException("retryInterval cannot be null");
			if(retryNumber==null) throw new RuntimeException("retryNumber cannot be null");
			if(updateInterval==null) throw new RuntimeException("updateInterval cannot be null");
			m_schedule = new Thread(new TaskRunner(retryInterval,retryNumber,updateInterval,task));
			m_schedule.start();
			scheduleIsRunning.set(true);
			LOG.info("task schedule started: retryInterval="+retryInterval
					+ ", retryNumber="+retryNumber
					+ ", updateInterval="+updateInterval);
		}
	}

	public synchronized void stopSchedule(){
		if (m_schedule!=null){
			m_schedule.interrupt();
			m_schedule=null;
			LOG.info("task schedule stopped");
		}
	}

	public boolean getScheduleIsRunning() {
		return scheduleIsRunning.get();
	}

	private class TaskRunner implements Runnable {
		// retryInterval interval (ms) before retrying after unsuccessful task
		private int retryInterval=0;

		// retryNumber  number of retrys if unsuccessful -1= forever until successful 
		private int retryNumber=0;

		// updateInterval  long term update interval (ms) before re attempting task. -1= only one try on startup 
		private int updateInterval=0;

		private ScheduledTask task=null;

		public TaskRunner(int retryInterval,int retryNumber, int updateInterval, ScheduledTask task){
			super();
			if(task==null) throw new RuntimeException("ScheduledTask task cannot be null");
			this.retryInterval=retryInterval;
			this.retryNumber=retryNumber;
			this.updateInterval=updateInterval;
			this.task=task;
		}

		public void run() {
			try {
				int retrys=0;
				int sleeptime=0;
				while (!Thread.currentThread().isInterrupted()) {

					LOG.debug("trying to run task");
					boolean success = false;
					try{
						success= task.runScheduledTask();
					} catch(Exception e){
						LOG.error("exception thrown when running scheduled task. Cancelling schedule",e);
						throw new InterruptedException();
					}

					if(!success && retryNumber<0){
						sleeptime = retryInterval;
						LOG.debug("retryNumber set to "+retryNumber+ " (continuous retrys until success) sleeping for retryInterval="+sleeptime);
					} else 	if(!success && retrys<retryNumber) {
						sleeptime = retryInterval;
						LOG.debug("failed retry "+retrys+ " sleeping for retryInterval="+sleeptime);
						retrys++;
					} else {
						if(updateInterval<0) {
							LOG.debug("updateInterval="+updateInterval+" only running task once at startup");
							throw new InterruptedException();
						}
						sleeptime = updateInterval;
						retrys=0;
						LOG.debug("sleeping for updateInterval="+sleeptime);
					}

					Thread.sleep(sleeptime);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			LOG.debug("schedule interrupted");
			scheduleIsRunning.set(false);
		}
	}

}
