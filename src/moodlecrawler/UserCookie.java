package moodlecrawler;

import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class UserCookie {
    private List<String> courseList = new ArrayList<>();
    private List<String> courseNameList = new ArrayList<>(); // store course names

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

    public void setCourseList(List <String> courseList)
    {
        this.courseList = courseList;
    }

    public void setCourseNameList(List <String> courseNameList)
    {
        this.courseNameList = courseNameList;
    }

    public List<String> getCourseList()
    {
        return this.courseList;
    }

    public List<String> getCourseNameList()
    {
        return this.courseNameList;
    }

}
