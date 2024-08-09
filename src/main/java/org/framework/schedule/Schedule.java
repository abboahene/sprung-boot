package org.framework.schedule;

import org.framework.annotations.EventListener;
import org.framework.annotations.Scheduled;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class Schedule {
    private static Timer timer = new Timer();
    public Schedule(List<List<Object>> listOfBeanList) {
            for (List<Object> beans : listOfBeanList) {
                for (Object bean : beans) {
                    for (Method method : bean.getClass().getMethods()) {
                        if (method.isAnnotationPresent(Scheduled.class)) {
                            Scheduled scheduled = method.getAnnotation(Scheduled.class);
                            int[] cron = Arrays.stream(scheduled.cron().split(" "))
                                    .mapToInt(Integer::parseInt)
                                            .toArray();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        method.invoke(method.getDeclaringClass().newInstance());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }, 0, cron[0]* 1000L + cron[1] *60*1000L);

                        }
                    }
                }
            }
    }

}
