package ua.com.smiddle.SecurityEchoServer.core.component;

import org.springframework.stereotype.Component;
import ua.com.smiddle.logger.settings.DebugLevelSource;

/**
 * @author ksa on 2/19/18.
 * @project SecurityEchoServer
 */
@Component
public class SettngsStub implements DebugLevelSource {
    @Override
    public int debugLevel() {
        return 3;
    }
}
