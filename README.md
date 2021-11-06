# casdoor-spring-boot-example

This is an example on how to use `casdoor-java-sdk` in SpringBoot project. We will show you the steps below.

## Step1. Deploy Casdoor 

Firstly, the Casdoor should be deployed. 

You can refer to the Casdoor official documentation for the [install guide](https://casdoor.org/docs/basic/installation).

After a successful deployment, you need to ensure:

- The Casdoor server is successfully running on http://localhost:8000.
- Open your favorite browser and visit http://localhost:7001, you will see the login page of Casdoor.
- Input `admin` and `123` to test login functionality is working fine.

Then you can quickly implement a casdoor based login page in your own app with the following steps.

## Step2. Import casdoor-java-sdk

You can import the casdoor-java-sdk with  maven or gradle.

```xml
<dependency>
    <groupId>org.casbin</groupId>
    <artifactId>casdoor-java-sdk</artifactId>
    <version>1.x.y</version>
</dependency>
```

## Step3. Init Config

Initialization requires 6 parameters, which are all string type.

| Name (in order)  | Must | Description                                         |
| ---------------- | ---- | --------------------------------------------------- |
| endpoint         | Yes  | Casdoor Server Url, such as `http://localhost:8000` |
| clientId         | Yes  | Application.client_id                               |
| clientSecret     | Yes  | Application.client_secret                           |
| jwtSecret        | Yes  | Same as Casdoor JWT secret.                         |
| organizationName | Yes  | Application.organization                            |
| applicationName  | Yes  | Application.name

You can use Java properties files to init as below.

```properties
casdoor.endpoint = http://localhost:8000
casdoor.clientId = 874e3e05e58d50148c65
casdoor.clientSecret = 41510b84c7267ad2e4d2b51096b7f11dc9c5fdc8
casdoor.jwtSecret = CasdoorSecret
casdoor.organizationName = built-in
casdoor.applicationName = app-built-in
```

Or YAML files as below.

```yaml
casdoor:
  endpoint: http://localhost:8000
  client-id: 874e3e05e58d50148c65
  client-secret: 41510b84c7267ad2e4d2b51096b7f11dc9c5fdc8
  jwt-secret: CasdoorSecret
  organization-name: built-in
  application-name: app-built-in
```

Then create `CasdoorSdkProperties` class, declare member variables corresponding to the configuration and provide getter and setter.

```java
@Data
@Component
@ConfigurationProperties(prefix = "casdoor")
public class CasdoorSdkProperties {
    private String endpoint;
    private String clientId;
    private String clientSecret;
    private String jwtSecret;
    private String organizationName;
    private String applicationName;
}
```

Create `CasdoorSdkConfig` class, init `CasdoorConfig` with the instance of CasdoorSdkProperties.

```java
@Configuration
public class CasdoorSdkConfig {

    @Resource
    private CasdoorSdkProperties casdoorSdkProperties;

    @Bean
    public CasdoorConfig getCasdoorConfig() {
        return new CasdoorConfig(
                casdoorSdkProperties.getEndpoint(),
                casdoorSdkProperties.getClientId(),
                casdoorSdkProperties.getClientSecret(),
                casdoorSdkProperties.getJwtSecret(),
                casdoorSdkProperties.getOrganizationName(),
                casdoorSdkProperties.getApplicationName()
        );
    }
}
```

When SpringBoot Application starts, the configuration in Java properties or YAML is automatically injected into the member variables in `CasdoorSdkProperties` class. 

So you can use `getCasdoorConfig` method in `CasdoorSdkConfig` class to get `CasdoorConfig` instance anywhere.

## Step4. Redirect to the login page

When you need the authentication who access your app, you can send the target url and redirect to the login page provided by Casdoor.

Please be sure that you have added the callback url (e.g. http://localhost:8080/login) in application configuration in advance.

```java
@RequestMapping("toLogin")
public String toLogin() {
    CasdoorConfig casdoorConfig = casdoorSdkConfig.getCasdoorConfig();
    String targetUrl = String.format("%s/login/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s&scope=read&state=%s",
            "http://localhost:7001", casdoorConfig.getClientId(),
            "http://localhost:8080/login", casdoorConfig.getApplicationName());
    return "redirect:" + targetUrl;
}
```

## Step5. Get token and parse

After Casdoor verification passed, it will be redirected to your application with code and state.

You can get the code and call `getOAuthToken` method, then parse out jwt token.

`CasdoorUser` contains the basic information about the user provided by Casdoor, you can use it as a keyword to set the session in your application.

```java
@RequestMapping("login")
public String login(String code, String state, HttpServletRequest request) {
    CasdoorAuthService casdoorAuthService = new CasdoorAuthService(casdoorSdkConfig.getCasdoorConfig());
    String token = "";
    CasdoorUser user = null;
    try {
        token = casdoorAuthService.getOAuthToken(code, state);
        user = casdoorAuthService.parseJwtToken(token);
    } catch (OAuthSystemException | OAuthProblemException | ParseException | InvocationTargetException | IllegalAccessException e) {
        e.printStackTrace();
    }
    HttpSession session = request.getSession();
    session.setAttribute("casdoorUser", user);
    return "redirect:/";
}
```

