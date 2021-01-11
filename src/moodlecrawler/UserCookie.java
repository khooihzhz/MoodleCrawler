package moodlecrawler;

import org.openqa.selenium.Cookie;

import java.util.*;

public final class UserCookie {
    private LinkedHashMap<String, String> courseMap = new LinkedHashMap<>();
    private LinkedHashMap<String, String> selectedCourseMap = new LinkedHashMap<>();
    private Set<Cookie> userCookie;
    private final static UserCookie INSTANCE = new UserCookie();

    private UserCookie(){}

    public static UserCookie getInstance()
    {
        return INSTANCE;
    }

    public void setUserCookie(Set <Cookie> cookie)
    {
        this.userCookie = cookie;
    }

    public Set <Cookie> getUserCookie()
    {
        return this.userCookie;
    }

    public void setCourseMap(LinkedHashMap <String, String> courseMap)
    {
        this.courseMap = courseMap;
    }

    public LinkedHashMap <String, String> getCourseMap()
    {
        return this.courseMap;
    }

    public void setSelectedCourseMap(LinkedHashMap<String, String> selectedCourseMap) {
        this.selectedCourseMap = selectedCourseMap;
    }

    public LinkedHashMap<String, String> getSelectedCourseMap() {
        return this.selectedCourseMap;
    }
}
