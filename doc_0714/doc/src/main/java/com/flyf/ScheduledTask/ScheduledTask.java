package com.flyf.ScheduledTask;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flyf.entity.Code;
import com.flyf.entity.MyFile;
import com.flyf.service.CodeService;
import com.flyf.service.MyFileService;
import com.flyf.service.QiniuService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Slf4j
@Component
public class ScheduledTask implements ApplicationContextAware {

    @Autowired
    CodeService codeService;
    @Autowired
    MyFileService myFileService;
    @Autowired
    QiniuService qiniuService;
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return context;
    }
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    @SneakyThrows
    @Scheduled(cron="0 0 12 * * ?")
    //cron="0 0/1 * * * ?"
    //cron="0 0 12 * * ?"
    public void testOne() {
        log.info("每天中午12点执行一次");
        clearcode();
        clearfile();
    }



    public void clearcode()  throws Exception {
        CodeService codeService = (CodeService) this.getBean("codeService");
        codeService.list().forEach(System.out::println);
        List<Code> list = codeService.list();
        for (Code code : list) {
            int days = code.getDays();
            if(days!=0) {
                Long id = code.getId();
                java.sql.Date time = code.getTime();
                //API中提供了getInstance方法用来创建对象。所以定义一个Calendar对象就应该为：
                Calendar cal = Calendar.getInstance();
                cal.setTime(time);//设置起时间
                cal.add(Calendar.DATE, days);//增加N天
                java.sql.Date endTime = new java.sql.Date(cal.getTime().getTime());

                java.sql.Date nowTime = new java.sql.Date(System.currentTimeMillis());
                boolean effectiveDate = isEffectiveDate(nowTime, time, endTime);
                if (effectiveDate) {
                    System.out.println("当前时间在范围内");
                } else {
                    System.out.println("当前时间不在范围内");
                    QueryWrapper<Code> wrapper = new QueryWrapper<>();
                    wrapper.eq("id", id);
                    codeService.remove(wrapper);
                }
            }
        }
    }
    public static boolean isEffectiveDate(java.sql.Date nowTime, java.sql.Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public void clearfile()  throws Exception {
        MyFileService myFileService1 = (MyFileService)this.getBean("myFileService");
        // myFileService.list().forEach(System.out::println);
        List<MyFile> list = myFileService1.list();
        for (MyFile myFile : list) {
            int recycle = myFile.getRecycle();
            if(recycle==1) {
                Long id = myFile.getId();
                java.sql.Date recycletime = myFile.getRecycletime();
                java.sql.Date nowtime = new java.sql.Date(System.currentTimeMillis());
                long day=7 - (nowtime.getTime()-recycletime.getTime())/(24*60*60*1000);
                if (day>=0) {
                    System.out.println("当前时间在范围内");
                } else {
                    System.out.println("当前时间不在范围内");
                    QueryWrapper<MyFile> wrapper = new QueryWrapper<>();
                    wrapper.eq("id", id);
                    //删除七牛云上的文件
                    List<MyFile> list1 = null;
                    list1 = myFileService.list(wrapper);
                    String address = list1.get(0).getAddress();
                    File tempFile= new File(address);
                    String fileName=tempFile.getName();
                    System.out.println("......................"+address);
                    System.out.println("......................"+fileName);
                    qiniuService.delete(fileName);
                    //删除数据库表里的一条记录
                    myFileService1.remove(wrapper);

                }
            }
        }
    }
}