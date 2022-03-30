package org.roda.core.data.v2.synchronization.bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@author João Gomes <jgomes@keep.pt>}.
 */
public class LastSynchronizationState implements Serializable {
  private static final long serialVersionUID = 1886330697587517966L;

  private String uuid;
  private String instance_id;
  private String to_date;
  private String from_date;
  private Map<String, List<String>> removedEntities;
  private Map<String, List<String>> issues;
  private int addedEntities;
  private int updatedEntities;


  public LastSynchronizationState() {
    removedEntities = new HashMap<>();
    issues = new HashMap<>();
  }

  public LastSynchronizationState(final String uuid, final String instance_id) {
    this.uuid = uuid;
    this.instance_id = instance_id;
    this.removedEntities = new HashMap<>();
    this.issues = new HashMap<>();
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getInstance_id() {
    return instance_id;
  }

  public void setInstance_id(String instance_id) {
    this.instance_id = instance_id;
  }

  public String getTo_date() {
    return to_date;
  }

  public void setTo_date(String to_date) {
    this.to_date = to_date;
  }

  public String getFrom_date() {
    return from_date;
  }

  public void setFrom_date(String from_date) {
    this.from_date = from_date;
  }

  public Map<String, List<String>> getRemovedEntities() {
    return removedEntities;
  }

  public void setRemovedEntities(Map<String, List<String>> removedEntities) {
    this.removedEntities = removedEntities;
  }

  public Map<String, List<String>> getIssues() {
    return issues;
  }

  public void setIssues(Map<String, List<String>> issues) {
    this.issues = issues;
  }

  public int getAddedEntities() {
    return addedEntities;
  }

  public void setAddedEntities(int addedEntities) {
    this.addedEntities = addedEntities;
  }

  public int getUpdatedEntities() {
    return updatedEntities;
  }

  public void setUpdatedEntities(int updatedEntities) {
    this.updatedEntities = updatedEntities;
  }

  public int countRemovedEntities() {
    int count = 0;
    for (Map.Entry<String, List<String>> entity : removedEntities.entrySet()) {
      count += entity.getValue().size();
    }
    return count;
  }
}
