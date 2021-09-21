package com.rudraksha.rudrakshashakti.Pojo;

import java.util.List;

public class ExpertDetails {
    List<String> services;

    public ExpertDetails(List<String> services) {
        this.services = services;
    }

    public ExpertDetails() {
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
