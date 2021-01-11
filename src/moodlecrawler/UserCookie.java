package moodlecrawler;

import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class UserCookie {
    private HashMap<String, String> courseMap = new HashMap<String, String>();

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

    public void setCourseMap(HashMap <String, String> courseMap)
    {
        this.courseMap = courseMap;
    }


    public HashMap <String, String> getCourseMap()
    {
        return this.courseMap;
    }

}
