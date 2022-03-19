# casdoor-spring-boot-example

This is an example on how to use `casdoor-spring-boot-starter` in SpringBoot project. We will show you the steps below.

## What you need

The Casdoor should be deployed.

You can refer to the Casdoor official documentation for the [install guide](https://casdoor.org/docs/basic/server-installation). Please deploy your Casdoor instance in **production mode**.

After a successful deployment, you need to ensure:

- Open your favorite browser and visit **http://localhost:8000**, you will see the login page of Casdoor.
- Input `admin` and `123` to test login functionality is working fine.

## Quickstart

### Include the dependency

Add ```casdoor-spring-boot-starter``` to the Spring Boot project.

For Apache Maven:

```Maven
<!-- https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter -->
<dependency>
    <groupId>org.casbin</groupId>
    <artifactId>casdoor-spring-boot-starter</artifactId>
    <version>1.x.y</version>
</dependency>
```

For Gradle:

```gradle
// https://mvnrepository.com/artifact/org.casbin/casdoor-spring-boot-starter
implementation group: 'org.casbin', name: 'casdoor-spring-boot-starter', version: '1.x.y'
```

### Configure your properties

Initialization requires 6 parameters, which are all string type.

| Name (in order)  | Must | Description                                         |
|------------------|------|-----------------------------------------------------|
| endpoint         | Yes  | Casdoor Server Url, such as `http://localhost:8000` |
| clientId         | Yes  | Application.client_id                               |
| clientSecret     | Yes  | Application.client_secret                           |
| jwtPublicKey     | Yes  | The public key for the Casdoor application's cert   |
| organizationName | Yes  | Application.organization                            |
| applicationName  | No   | Application.name                                    |

You can use Java properties or YAML files to init as below.

For properties:

```properties
casdoor.endpoint = http://localhost:8000
casdoor.clientId = <client-id>
casdoor.clientSecret = <client-secret>
casdoor.jwtPublicKey = <jwt-public-key>
casdoor.organizationName = built-in
casdoor.applicationName = app-built-in
```

For yaml:

```yaml
casdoor:
  endpoint: http://localhost:8000
  client-id: <client-id>
  client-secret: <client-secret>
  jwt-public-key: <jwt-public-key>
  organization-name: built-in
  application-name: app-built-in
```

### Get the Service and use


Now provide 5 services: `CasdoorAuthService`, `CasdoorUserService`, `CasdoorEmailService`, `CasdoorSmsService` and `CasdoorResourceService`.

You can create them as below in SpringBoot project.

```java
@Resource
private CasdoorAuthService casdoorAuthService;
```

When you need the authentication who access your app, you can send the target url and redirect to the login page provided by Casdoor.

Please be sure that you have added the callback url (e.g. http://localhost:8080/login) in application configuration in advance.

```java
@RequestMapping("toLogin")
public String toLogin() {
    return "redirect:" + casdoorAuthService.getSigninUrl("http://localhost:8080/login");
}
```

After Casdoor verification passed, it will be redirected to your application with code and state.

You can get the code and call `getOAuthToken` method, then parse out jwt token.

`CasdoorUser` contains the basic information about the user provided by Casdoor, you can use it as a keyword to set the session in your application.

```java
@RequestMapping("login")
public String login(String code, String state, HttpServletRequest request) {
    String token = "";
    CasdoorUser user = null;
    try {
        token = casdoorAuthService.getOAuthToken(code, state);
        user = casdoorAuthService.parseJwtToken(token);
    } catch (CasdoorAuthException e) {
        e.printStackTrace();
    }
    HttpSession session = request.getSession();
    session.setAttribute("casdoorUser", user);
    return "redirect:/";
}
```

Examples of APIs are shown below.

- CasdoorAuthService
    - `String token = casdoorAuthService.getOAuthToken(code, "app-built-in");`
    - `CasdoorUser casdoorUser = casdoorAuthService.parseJwtToken(token);`
- CasdoorUserService
    - `CasdoorUser casdoorUser = casdoorUserService.getUser("admin");`
    - `CasdoorUser casdoorUser = casdoorUserService.getUserByEmail("admin@example.com");`
    - `CasdoorUser[] casdoorUsers = casdoorUserService.getUsers();`
    - `CasdoorUser[] casdoorUsers = casdoorUserService.getSortedUsers("created_time", 5);`
    - `int count = casdoorUserService.getUserCount("0");`
    - `CasdoorResponse response = casdoorUserService.addUser(user);`
    - `CasdoorResponse response = casdoorUserService.updateUser(user);`
    - `CasdoorResponse response = casdoorUserService.deleteUser(user);`
- CasdoorEmailService
    - `CasdoorResponse response = casdoorEmailService.sendEmail(title, content, sender, receiver);`
- CasdoorSmsService
    - `CasdoorResponse response = casdoorSmsService.sendSms(randomCode(), receiver);`
- CasdoorResourceService
    - `CasdoorResponse response = casdoorResourceService.uploadResource(user, tag, parent, fullFilePath, file);`
    - `CasdoorResponse response = casdoorResourceService.deleteResource(file.getName());`

## What's more

You can explore the following projects/docs to learn more about the integration of Java with Casdoor.

- [casdoor-java-sdk](https://github.com/casdoor/casdoor-java-sdk)
- [casdoor-spring-boot-starter](https://github.com/casdoor/casdoor-spring-boot-starter)
- [casdoor-spring-boot-security-example](https://casdoor.org/docs/integration/spring-security)
- [casdoor-spring-boot-shiro-example](https://github.com/casdoor/casdoor-spring-boot-shiro-example)
