package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/10.
 * 增加字段  file_size force_upgrade 2016/10-24
 */
public class AppNewVersionRDO implements Serializable {
    private Boolean needed;
    private Integer version;
    @SerializedName("version_name")
    private String versionName;
    @SerializedName("upgrade_time")
    private Long upgradeTime;
    @SerializedName("release_time")
    private Long releaseTime;
    private String description;
    private String url;
    private String file_size; //文件大小
    private boolean force_upgrade;//是否升级

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public boolean isForce_upgrade() {
        return force_upgrade;
    }

    public void setForce_upgrade(boolean force_upgrade) {
        this.force_upgrade = force_upgrade;
    }

    public Boolean getNeeded() {
        return needed;
    }

    public void setNeeded(Boolean needed) {
        this.needed = needed;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Long getUpgradeTime() {
        return upgradeTime;
    }

    public void setUpgradeTime(Long upgradeTime) {
        this.upgradeTime = upgradeTime;
    }

    public Long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
