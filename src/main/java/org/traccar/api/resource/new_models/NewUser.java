package org.traccar.api.resource.new_models;

import org.traccar.model.User;

public class NewUser extends NewBaseModel {

    String  email, administrator, disabled;

    public NewUser(User user) {
        name = user.getName();
        email = user.getEmail();
        if (user.getAdministrator())
            administrator = "YES";
        else administrator = "NO";
        if (user.getDisabled())
            disabled = "YES";
        else disabled = "NO";
    }

    public NewUser() {
    }


    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAdministrator() {
        return administrator;
    }

    public String getDisabled() {
        return disabled;
    }
}
