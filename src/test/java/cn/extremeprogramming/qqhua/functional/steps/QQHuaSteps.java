package cn.extremeprogramming.qqhua.functional.steps;

import cucumber.api.java.en.Given;

public class QQHuaSteps extends AbstractSteps {
    @Given("user opens home page")
    public void user_opens_home_page() {
        executeGet("/");
    }
}
