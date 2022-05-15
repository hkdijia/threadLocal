package com.gotkx.threadlocal.extract;

import com.gotkx.threadlocal.extract.dto.WorkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author HuangKai
 * @date 2022/5/15 17:48
 */

@RestController
@Slf4j
public class TestController {

    @Resource
    private WorkServiceImpl workService;

    @RequestMapping("/test")
    public void doWork(){
        String logMark = "异步逻辑抽象化";
        WorkSubmitTask<WorkDto> workSubmitTask = new WorkSubmitTask<>(workService, 4, logMark);
        workSubmitTask.sumbitJob(new WorkDto());
        workSubmitTask.takeResults(new ArrayList<>());
    }

}
