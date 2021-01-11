package moodlecrawler;



import org.openqa.selenium.Cookie;

import java.util.Set;

public final class UserCookie {
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


}
