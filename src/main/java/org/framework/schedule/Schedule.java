package org.framework.schedule;

import org.framework.annotations.Scheduled;

import java.lang.reflect.Method;
import java.util.*;

public class Schedule {
    public Schedule(List<List<Object>> listOfBeanList) {

        // Iterate through beans and schedule methods based on annotations
        for (List<Object> beans : listOfBeanList) {
            for (Object bean : beans) {
                for (Method method : bean.getClass().getMethods()) {
                    if (method.isAnnotationPresent(Scheduled.class)) {
                        Scheduled scheduled = method.getAnnotation(Scheduled.class);

                        // Handle fixedRate scheduling
                        if (scheduled.fixedRate() > 0) {
                            scheduleAtFixedRate(bean, method, scheduled.fixedRate());
                        }
                        // Handle cron scheduling only if fixedRate is not specified
                        else if (!scheduled.cron().isEmpty()) {
                            scheduleWithCron(bean, method, scheduled.cron());
                        } else {
                            System.out.println("No valid scheduling parameters found.");
                        }
                    }
                }
            }
        }
    }

    private void scheduleAtFixedRate(Object bean, Method method, long fixedRate) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, fixedRate);
    }

    private void scheduleWithCron(Object bean, Method method, String cron) {
        Timer timer = new Timer();
        int[] cronArray = Arrays.stream(cron.split(" "))
                .mapToInt(Integer::parseInt)
                .toArray();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, cronArray[0] * 1000L + cronArray[1] * 60 * 1000L);
    }
}


