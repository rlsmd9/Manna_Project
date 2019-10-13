package com.example.manna_project;

public class DBData {
    String latest_version_code; // 최신 버전 코드
    String latest_version_name; // 최신 버전 코드명
    String minimum_version_code; // 최소 버전 코드
    String minimum_version_name; // 최소 버전 코드명
    String force_update_message; // 업데이트 강요 메시지
    String optional_update_message; // 선택적 업데이트 메시지

    public DBData() {
    }

    public DBData(String latest_version_code, String latest_version_name
                    , String minimum_version_code, String minimum_version_name
                    , String force_update_message, String optional_update_message) {

        this.latest_version_code = latest_version_code;
        this.latest_version_name = latest_version_name;
        this.minimum_version_code = minimum_version_code;
        this.minimum_version_name = minimum_version_name;
        this.force_update_message = force_update_message;
        this.optional_update_message = optional_update_message;
    }

    public void setLatest_version_code(String latest_version_code) {
        this.latest_version_code = latest_version_code;
    }

    public void setLatest_version_name(String latest_version_name) {
        this.latest_version_name = latest_version_name;
    }

    public void setMinimum_version_code(String minimum_version_code) {
        this.minimum_version_code = minimum_version_code;
    }

    public void setMinimum_version_name(String minimum_version_name) {
        this.minimum_version_name = minimum_version_name;
    }

    public void setForce_update_message(String force_update_message) {
        this.force_update_message = force_update_message;
    }

    public void setOptional_update_message(String optional_update_message) {
        this.optional_update_message = optional_update_message;
    }

    public String getLatest_version_code() {
        return latest_version_code;
    }

    public String getLatest_version_name() {
        return latest_version_name;
    }

    public String getMinimum_version_code() {
        return minimum_version_code;
    }

    public String getMinimum_version_name() {
        return minimum_version_name;
    }

    public String getForce_update_message() {
        return force_update_message;
    }

    public String getOptional_update_message() {
        return optional_update_message;
    }
}
