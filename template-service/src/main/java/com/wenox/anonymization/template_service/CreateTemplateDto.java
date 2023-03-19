package com.wenox.anonymization.template_service;

import lombok.ToString;

@ToString
public class CreateTemplateDto {

  private String title;

  private String description;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
