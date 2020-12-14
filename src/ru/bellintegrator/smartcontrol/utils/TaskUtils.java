package ru.bellintegrator.smartcontrol.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ru.bellintegrator.smartcontrol.core.api.dictionary.ActionEnum;
import ru.bellintegrator.smartcontrol.core.api.dictionary.ObjectTypeEnum;
import ru.bellintegrator.smartcontrol.core.api.dictionary.StatusEnum;
import ru.bellintegrator.smartcontrol.entity.user.Task;

public class TaskUtils {

	// ------- STUB --------
	private static Task singleTask;
	
    public Task persistNewTask(long applianceId, long objectId, ObjectTypeEnum objectType, ActionEnum action, String content) {
    	singleTask = new Task(applianceId, objectId, objectType, action, content);
    	return singleTask;

    }

    public Task updateTask(Task task) {
    		
    		ActionEnum ae = task.getAction();
    		if (ae != null) {
    			singleTask.setAction(ae);
    		}
    		
    		Long applianceId = task.getApplianceId();
    		if (applianceId != null) {
    			singleTask.setApplianceId(applianceId);
    		}
    		
    		String content = task.getContent();
    		if(content != null) {
    			singleTask.setContent(content);
    		}
    		
    		String newConfig = task.getNewConfig();
    		if (newConfig != null) {
    			singleTask.setNewConfig(task.getNewConfig());
    		}
    		
    		Long objectId = task.getObjectId();
    		if (objectId != null) {
    			singleTask.setObjectId(objectId);
    		}
    		
    		ObjectTypeEnum ote = task.getObjectType();
    		if (ote != null) {
    			singleTask.setObjectType(task.getObjectType());
    		}
    		
    		StatusEnum se = task.getStatus();
    		if (se != null) {
    			singleTask.setStatus(se);
    		}
    		
    		Timestamp ts = task.getSysCreationDate();
    		if (ts !=null) {
    			singleTask.setSysCreationDate(ts);
    		}
    		
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    		singleTask.setSysUpdateDate(timestamp);
        
    		return task;
    }
    
    /*
     * В очереди храним id тасковю Этот метод поможет получить экземпляр.
     */
    public Task getById(long id) {
    		return singleTask;
    }
    
    /*
     * При инициализации ApplianceService запрашиваем задачи на Appliance со Status = QUEUE
     */
    public List<Task> getByApplianceId(long id, StatusEnum se) {
    		List<Task> list = new ArrayList<>();
    		
    		singleTask.setStatus(se); //----- STUB
    		list.add(singleTask);
    		return list;
    }
    
}
