package org.test.partI;


import org.framework.annotations.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {
//    app.name= customerApplication
//    app.version= 1.0
//    app.server.url="http://server.com"
//    app.server.name="server name"
//    app.user.firstname="Firstname"
//    app.user.lastname="Lastname"
//    app.user.username="username"
//    app.user.password="892484"
//    app.countries= Ghana, USA, Canada

    private String name;
    private double version;
    private int number;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "(AppConfigProperties)" + '\n' +
                "App Name=" + name + '\n' +
                "App Version=" + version + '\n'+
                "App Number=" + number + '\n';
    }


}
