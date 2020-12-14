package ru.bellintegrator.smartcontrol.entity.user;
import java.util.Objects;

import ru.bellintegrator.smartcontrol.core.api.dictionary.ActionEnum;
import ru.bellintegrator.smartcontrol.core.api.dictionary.ObjectTypeEnum;
import ru.bellintegrator.smartcontrol.core.api.dictionary.StatusEnum;
import ru.bellintegrator.smartcontrol.utils.ApplianceUtils;

import java.sql.Timestamp;

public class Task{

    private long _id;
    private long _applianceId;
    private long _objectId;
    private ObjectTypeEnum _objectType;
    private StatusEnum _status;
    private ActionEnum _action;
    private String _content;
    private String _newConfig;
    private Timestamp _sysCreationDate;
    private Timestamp _sysUpdateDate;
    private ApplianceUtils applianceUtils = new ApplianceUtils();
    
    public Task(long applianceId, long objectId, ObjectTypeEnum objectType, ActionEnum action, String content) {
    		_applianceId = applianceId;
    		_objectId = objectId;
    		_objectType = objectType;
    		_action = action;
    		_content = content;
    		_status = StatusEnum.CREATED;
    		_sysCreationDate = new Timestamp(System.currentTimeMillis());
    }
    
    public Task(String url, long objectId, ObjectTypeEnum objectType, ActionEnum action, String content) {
		_applianceId = applianceUtils.getByURL(url).getId();
		_objectId = objectId;
		_objectType = objectType;
		_action = action;
		_content = content;
		_status = StatusEnum.CREATED;
		_sysCreationDate = new Timestamp(System.currentTimeMillis());
    }
    
    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public long getApplianceId() {
        return _applianceId;
    }

    public void setApplianceId(long id) {
        this._applianceId = id;
    }

    public ObjectTypeEnum getObjectType() {
        return _objectType;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public void setObjectType(ObjectTypeEnum objectType) {
        this._objectType = objectType;
    }

    public StatusEnum getStatus() {
        return _status;
    }

    public void setStatus(StatusEnum status) {
        this._status = status;
    }

    public ActionEnum getAction() {
        return _action;
    }

    public void setAction(ActionEnum action) {
        this._action = action;
    }

    public long getObjectId() {
        return _objectId;
    }

    public void setObjectId(long id) {
        this._objectId = id;
    }

    public String getNewConfig() {
        return _newConfig;
    }

    public void setNewConfig(String newConfig) {
        this._newConfig = newConfig;
    }

    public Timestamp getSysCreationDate() {
        return _sysCreationDate;
    }

    public void setSysCreationDate(Timestamp sysCreationDate) {
        this._sysCreationDate = sysCreationDate;
    }

    public Timestamp getSysUpdateDate() {
        return _sysUpdateDate;
    }

    public void setSysUpdateDate(Timestamp sysUpdateDate) {
        this._sysUpdateDate = sysUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(_id, task._id) &&
                Objects.equals(_applianceId, task._applianceId) &&
                Objects.equals(_objectId, task._objectId) &&
                Objects.equals(_objectType, task._objectType) &&
                Objects.equals(_content, task._content) &&
                Objects.equals(_status, task._status) &&
                Objects.equals(_action, task._action) &&
                Objects.equals(_sysCreationDate, task._sysCreationDate) &&
                Objects.equals(_sysUpdateDate, task._sysUpdateDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _applianceId, _objectId, _objectType, _content, _status, _action, _sysCreationDate, _sysUpdateDate);
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id=" + _id +
                ", _applianceId=" + _applianceId +
                ", _objectId=" + _objectId +
                ", _content='" + _content + '\'' +
                ", _objectType=" + _objectType +
                ", _status=" + _status +
                ", _action=" + _action +
                ", _newConfig='" + _newConfig + '\'' +
                ", _sysCreationDate=" + _sysCreationDate +
                ", _sysUpdateDate=" + _sysUpdateDate +
                '}';
    }
}
