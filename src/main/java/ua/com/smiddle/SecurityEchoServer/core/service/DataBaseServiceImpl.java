package ua.com.smiddle.SecurityEchoServer.core.service;

import org.springframework.stereotype.Service;
import ua.com.smiddle.common.services.service.impl.CommonEntityServiceImpl;
import ua.com.smiddle.logger.entity.LogDTO;

/**
 * @author ksa on 2/19/18.
 * @project SecurityEchoServer
 */
@Service
public class DataBaseServiceImpl extends CommonEntityServiceImpl implements DataBaseService {
    @Override
    public void insertLog(LogDTO log) {
        System.out.println(log);
    }
}
