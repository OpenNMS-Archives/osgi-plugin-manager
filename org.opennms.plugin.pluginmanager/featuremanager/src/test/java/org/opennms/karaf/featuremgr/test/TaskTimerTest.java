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

package org.opennms.karaf.featuremgr.test;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.opennms.karaf.featuremgr.TaskTimer;
import org.opennms.karaf.featuremgr.TaskTimer.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTimerTest {
	private static final Logger LOG = LoggerFactory.getLogger(TaskTimerTest.class);

	@Test
	public void test() {
		//retryInterval interval (ms) before retrying unsuccessful download of manifests
		Integer retryInterval=20;

		// retryNumber  number of retrys if unsuccessful -1= forever until successful 
		Integer retryNumber=-1;

		// updateInterval  long term update interval (ms) before attempting to reload config. -1= only try on startup 
		Integer updateInterval=-1;

		AtomicInteger count = new AtomicInteger(100);

		TaskTimer timer= new TaskTimer();

		ScheduledTask task = new ScheduledTask(){
			public boolean runScheduledTask(){
				int c = count.decrementAndGet();
				LOG.debug("running scheduled task. count="+c);
				if(c< -10) {
					LOG.debug("trying to stop");
					timer.stopSchedule();
				}
				if(c < 0) return true;
				return false;
			}
		};

		timer.setRetryInterval(retryInterval);
		timer.setRetryNumber(retryNumber);
		timer.setTask(task);
		timer.setUpdateInterval(updateInterval);

		timer.startSchedule();

		// wait before stopping test
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		LOG.debug("test timeout reached scheduleIsRunning="+timer.getScheduleIsRunning());

		timer.stopSchedule();
		LOG.debug("test finished");
	}

}
