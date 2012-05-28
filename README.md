Mock contexts
=============

This extension starts mock contexts for your tests. Currently it supports ConversationContext, ViewScopedContext and additionally FacesContext.

Usage
-----

Just add impl module to classpath and run test either from IDE or maven.

    <dependency>
        <groupid>pl.com.it-crowd.mock-contexts-extension</groupid>
        <artifactid>mock-contexts-extension-api</artifactid>
        <version>0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupid>pl.com.it-crowd.mock-contexts-extension</groupid>
        <artifactid>mock-contexts-extension-impl</artifactid>
        <version>0.1-SNAPSHOT</version>
        <scope>runtime</scope>
    </dependency>

Now you can annotate your test method with @ConversationScopeRequired or @ViewScopeRequired to activate those contexts.

If your tests depend on FacesContext.getCurrentInstance() then you can provide mock that will be registered as current instance:

    @FacesContextRequired
    @Test
    public void facesContextNr1Availabile1()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Assert.assertNotNull(facesContext);
        Assert.assertSame(mock, facesContext);
        Assert.assertEquals(attributes, facesContext.getAttributes());
    }

    @MockFacesContextProducer
    public FacesContext mockFacesContext()
    {
        if (mock == null) {
            mock = mock(FacesContext.class);
        }
        return mock;
    }