package ua.com.smiddle.SecurityEchoServer.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ua.com.smiddle.common.model.BaseEntity;
import ua.com.smiddle.logger.entity.LogDTO;

import javax.persistence.*;

/**
 * Added by A.Osadchuk on 01.11.2016 at 15:55.
 * Project: SmiddleELearning
 */
@Entity
@Table(name = "TEST_LOGS"
        , indexes = {
        @Index(name = "IDX_ADM_LOGS_LOG_DATE", columnList = "LOG_DATE"),
        @Index(name = "IDX_ADM_LOGS_TYPE", columnList = "TYPE"),
        @Index(name = "IDX_ADM_LOGS_ACTION", columnList = "ACTION")}
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "id", "cid"})
public class Log extends BaseEntity implements LogDTO {
    public static final long serialVersionUID = -1L;
    @Column(name = "LOG_DATE")
    private long dateTime;
    @Column(name = "MODULE_NAME", nullable = false, length = 60)
    private String moduleName;
    @Column(name = "CID")
    private long cid;
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = true, length = 20)
    private LogDTO.LogTypes type;
    @Column(name = "ACTION", nullable = true)
    private String action;
    @Lob
    @Column(name = "MESSAGE", nullable = false)
    private String message;


    //Constructors
    public Log() {
    }

    public Log(long dateTime, String moduleName, long cid, LogDTO.LogTypes type, String action, String message) {
        this.dateTime = dateTime;
        this.moduleName = moduleName;
        this.cid = cid;
        this.type = type;
        this.action = action;
        this.message = message;
    }


    //Getters & setters
    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public LogDTO.LogTypes getType() {
        return type;
    }

    public void setType(LogDTO.LogTypes type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public LogDTO addCid(long cid) {
        this.cid = cid;
        return this;
    }

    @Override
    public LogDTO addType(LogTypes type) {
        this.type = type;
        return this;
    }

    @Override
    public LogDTO addAction(String action) {
        this.action = action;
        return this;
    }

    @Override
    public LogDTO addDateTime(long dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    @Override
    public LogDTO addComponentName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    @Override
    public LogDTO addMessage(String message) {
        this.message = message;
        return this;
    }

    //Methods
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Log{");
        sb.append("dateTime=").append(dateTime);
        sb.append(", moduleName='").append(moduleName).append('\'');
        sb.append(", cid=").append(cid);
        sb.append(", type=").append(type);
        sb.append(", action=").append(action);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
