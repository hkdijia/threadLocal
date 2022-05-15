package com.gotkx.threadlocal.extract;

import com.gotkx.threadlocal.extract.dto.WorkDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务处理逻辑
 * @author HuangKai
 * @date 2022/5/15 17:49
 */

@Slf4j
@Service
public class WorkServiceImpl implements WorkService<WorkDto>{

    @Override
    public List<WorkDto> query(WorkDto workDto, Object... args) {
        return null;
    }

    @Override
    public WorkDto clone(WorkDto workDto) {
        return null;
    }
}
