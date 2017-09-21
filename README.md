Mock contexts
=============

Forked from https://github.com/it-crowd/mock-contexts-extension

This project helps to fix "No active contexts for scope type ViewScoped / ConversationScoped" exception which occures when arquillian tests ViewScoped and ConversationScoped controllers.

JSF version: 2.2, tested on Wildfly 10


Usage
-----

Annotate test methods with @ViewScopeRequired and @ConversationScopeRequired

Look to SampleTest.java for usage example:

    @ViewScopeRequired
    @Test
    public void viewScopedBeanTest()
    {
        Assert.assertEquals(0, viewScopedComponent.getIndex());
        viewScopedComponent.setIndex(1);
        Assert.assertEquals(1, viewScopedComponent.getIndex());
    }


Test coverage
-------------

At this moment only SampleTest passes (I don't need remaining functionality)


Integration
-----

Download, build with "mvn clean install" and include in your project

    <dependency>
        <groupid>pl.itcrowd.mock-contexts-extension</groupid>
        <artifactid>mock-contexts-extension-api</artifactid>
        <version>1.0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupid>pl.itcrowd.mock-contexts-extension</groupid>
        <artifactid>mock-contexts-extension-impl</artifactid>
        <version>1.0.1-SNAPSHOT</version>
        <scope>runtime</scope>
    </dependency>