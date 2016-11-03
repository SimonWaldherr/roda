package org.roda.core.data.v2.ip;

import java.io.Serializable;

public class AIPLink implements Serializable {
  private static final long serialVersionUID = 6222816570283361558L;
  private String aipId;

  public AIPLink(String aipId) {
    super();
    this.aipId = aipId;
  }

  public String getAipId() {
    return aipId;
  }

  public void setAipId(String aipId) {
    this.aipId = aipId;
  }

  @Override
  public String toString() {
    return "AIPLink [aipId=" + aipId + "]";
  }
}
