package com.techg.entity;

import jakarta.persistence.*;

/** Small key/value table for website-wide settings such as the hero image. */
@Entity
@Table(name = "site_settings")
public class SiteSetting {

    public static final String HERO_IMAGE = "hero_image";
    public static final String DEFAULT_HERO_IMAGE = "/images/hero.png";

    @Id
    @Column(name = "setting_key", length = 100)
    private String key;

    @Column(name = "setting_value", length = 1000)
    private String value;

    public SiteSetting() {}
    public SiteSetting(String key, String value) { this.key = key; this.value = value; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
