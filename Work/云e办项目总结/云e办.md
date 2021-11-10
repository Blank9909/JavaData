# 1.云e办

![image-20210926105324150](C:\Users\86134\Desktop\云e办\image\1.png)

## 1.1 登入的实现

### （1）创建项目

### （2） 配置文件

```java
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/yeb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 1234
    hikari:
      pool-name: DateHikariCP
      minimum-idle: 5
      idle-timeout: 180000
      maximum-pool-size: 10
      auto-commit: true
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

mybatis-plus:
  mapper-locations: classpath*:/mapper/*Mapper.xml
  type-aliases-package: com.yjx.server.pojo
  configuration:
    map-underscore-to-camel-case: false
logging:
  level:
    com:
      yjxxt:
        server:
          mapper: debug


jwt:
  # JWT存储的请求头
  tokenHeader: Authorization
  # JWT 加解密使用的密钥
  secret: yjxxt
  # JWT的超期限时间（60*60*24）
  expiration: 604800
  # JWT 负载中拿到开头
  tokenHead: Bearer
```

### （3）导入依赖springboot+mybatisplus+springsecurity+jwt

```java
<!--web 依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!--lombok 依赖-->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<!--mysql 依赖-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<!--mybatis-plus 依赖-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.3.1.tmp</version>
</dependency>
<!--security 依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!--JWT 依赖-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
```

### （4）admin实现UserDetails在securityconfig中配置userDetailService

```java
@Bean
public UserDetailsService userDetailsService(){
    return username->{
        Admin admin = adminService.queryAdminByUsername(username);
        if(admin!=null){
            admin.setRoles(roleService.getRoles(admin.getId()));
            return admin;
        }
        throw  new UsernameNotFoundException("用户名或密码不正确！");
    };
}
```

### （5）LoginController

在controller层调用service

```java
@PostMapping("/login")
@ApiOperation(value = "用户登入接口")
public RespBean login(@RequestBody AdminLoginParam adminLoginParam){
    return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword());
}
```

### （6）IAdminService

将controller层传来的username，password进行校验

```java
/**
 * 校验登录参数
 * @author ShiPanChao
 * @date 2021/9/22 21:02
 * @param username
 * @param password
 * @return com.yjx.server.pojo.RespBean
 */
public RespBean login(String username, String password){
    //校验用户名和密码
    if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
        return RespBean.error("用户名或密码为空!!!");
    }
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if(userDetails==null){
        return RespBean.error("用户不存在");
    }
    if (!userDetails.isEnabled()){
        return RespBean.error("账号被禁用，请联系管理员!");
    }
    if(!(passwordEncoder.matches(password,userDetails.getPassword()))){
        return RespBean.error("密码错误");
    }
    String token = jwtTokenUtil.generateToken(userDetails);
    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put("token", token);
    tokenMap.put("tokenHead", tokenHead);
    return RespBean.success("登录成功", tokenMap);
}
```

## 1.2 登入结合验证码

### （1）导入依赖

```java
<!-- google kaptcha依赖 -->
<dependency>
    <groupId>com.github.axet</groupId>
    <artifactId>kaptcha</artifactId>
    <version>0.0.9</version>
</dependency>
```

### （2）修改AdminLoginParam

```java
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "AdminLogin对象", description = "")
public class AdminLoginParam {

    @ApiModelProperty(value = "用户名",required = true)
    private String username;
    @ApiModelProperty(value = "密码",required = true)
    private String password;
    @ApiModelProperty(value = "验证码",required = true)
    private String code;
}
```

### （3）修改LoginController

```java
@PostMapping("/login")
@ApiOperation(value = "用户登入接口")
public RespBean login(@RequestBody AdminLoginParam adminLoginParam){
    return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword(),adminLoginParam.getCode());
}
```

### （4）修改IAdminService

```java
//校验验证码
if(StringUtils.isBlank(code)){
    return RespBean.error("请输入验证码!!!");
}
String code1 = (String) session.getAttribute("code");
if(code1==null || !code1.equals(code)){
    return RespBean.error("验证码过期或错误!!!");
}
session.removeAttribute("code");
```

## 1.3 接口文档

### （1）导入依赖

```java
<!-- swagger2 依赖 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.7.0</version>
</dependency>
<!-- Swagger第三方ui依赖 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>swagger-bootstrap-ui</artifactId>
    <version>1.9.6</version>
</dependency>
```

###       （2）加入注解

在类上加入@Api(tags = "用户登入")注解，在方法上加入@ApiOperation(value = "用户登入接口")

启动后访问http://localhost:8081/doc.html



![image-20210926113337464](C:\Users\86134\Desktop\云e办\image\2.png)

## 1.4 登入jwt

### （1）修改securityconfig

```java
//基于token，不需要session
.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
.and()
.authorizeRequests().antMatchers("/login","/logout").permitAll()
.anyRequest().authenticated();

 //添加jwt 登录授权过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //添加自定义未授权和未登录结果返回
        http.exceptionHandling()
            .accessDeniedHandler(restfulAccessDeniedHandler)
            .authenticationEntryPoint(restAuthenticationEntryPoint);
```

### （2）自定义过滤器JwtAuthenticationTokenFilter

```java
/**
 * @author ShiPanChao
 * @ProjectName yeb
 * @Description TODO Jwt登录授权过滤器
 * @time 2021/9/23 15:36
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private UserDetailsService userDetailsService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    /**
     * 1.token存在
     * 2.获取请求头
     * 3.获取token
     * 4.判断用户是否登入
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(tokenHeader);
        //token存在
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            String token=authHeader.substring(tokenHead.length()+1);
            String username = jwtTokenUtil.getUserNameFromToken(token);
            //token中存在用户名但未登录
            if (null!=username&& null== SecurityContextHolder.getContext().getAuthentication()) {
                //登录
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                //验证token是否有效，重新设置用户对象
                if (jwtTokenUtil.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
```

## 1.5 基于角色的权限控制

### （1）根据url判断所需的角色

```java
/**
 * @author ShiPanChao
 * @ProjectName yeb
 * @Description TODO 权限控制 根据请求url分析出请求所需角色
 * @time 2021/9/24 15:02
 */
@Component
public class CustomFilter implements FilterInvocationSecurityMetadataSource {

    @Resource
    private IMenuService menuService;

    AntPathMatcher antPathMatcher=new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        //获取请求的uri
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        //获取菜单
        List<Menu> menus = menuService.getAllMenusWithRole();
        for (Menu menu : menus) {
            if(antPathMatcher.match(menu.getUrl(),requestUrl)){
             String[] str= menu.getRoles().stream().map(Role::getName).toArray(String[]::new);
                return SecurityConfig.createList(str);
            }
        }
        //没用匹配的uri默认登入
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
```

### （2）判断登入用户所拥有的角色

```java
/**
 * @author ShiPanChao
 * @ProjectName yeb
 * @Description TODO 权限控制 判断用户角色
 * @time 2021/9/24 15:53
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {


    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : collection) {
            //当前url所需角色
            String needRole = configAttribute.getAttribute();
            //判断角色是否为登录即可访问的角色，此角色在CustomFilter中设置
            if ("ROLE_LOGIN".equals(needRole)) {
                //判断是否登录
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw  new AccessDeniedException("尚未登录，请登录!!!");
                } else {
                    return;
                }
            }
            //判断用户角色是否为url所需角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if(needRole.equals(authority.getAuthority())){
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足,请联系管理员");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
```

### （3）securityconfig

```java
//动态权限配置
http
.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
    @Override
    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
        object.setAccessDecisionManager(customUrlDecisionManager);
        object.setSecurityMetadataSource(customFilter);
        return object;
    }
})
```

## 1.6 Excel的导出

### （1）导入依赖

```java
<!--easy poi依赖-->
    <dependency>
        <groupId>cn.afterturn</groupId>
        <artifactId>easypoi-spring-boot-starter</artifactId>
        <version>4.1.3</version>
    </dependency>
</dependencies>
```

### （2）提供EmployeeController

```java
@ApiOperation(value = "导出员工数据")
@GetMapping(value = "/export",produces = "application/octet-stream")
public void exportEmployee(HttpServletResponse response){
    List<Employee> list = employeeService.getEmployee(null);
    ExportParams params = new ExportParams("员工表","员工表", ExcelType.HSSF);
    Workbook workbook = ExcelExportUtil.exportExcel(params, Employee.class, list);
    ServletOutputStream out = null;
    try {
        //流形式
        response.setHeader("content-type","application/octet-stream");
        //防止中文乱码
        response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode("员工表.xls","UTF-8"));
        out = response.getOutputStream();
        workbook.write(out);
    } catch (IOException e) {
        e.printStackTrace();
    }finally {
        if (null!=out){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### （3）EmployeeServiceImpl

提供获取所有员工的方法

```java
/**
 * 获取员工
 * @author ShiPanChao
 * @date 2021/9/24 21:33
 * @param id
 * @return java.util.List<com.yjx.server.pojo.Employee>
 */
@Override
public List<Employee> getEmployee(Integer id) {
    return employeeMapper.getEmployee(id);
}
```

### （4）在Employee类加入注解

```java
@ApiModelProperty(value = "员工编号")
@TableId(value = "id", type = IdType.AUTO)
private Integer id;

@ApiModelProperty(value = "员工姓名")
@Excel(name = "员工姓名")
private String name;

@ApiModelProperty(value = "性别")
@Excel(name = "性别")
private String gender;

@ApiModelProperty(value = "出生日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
@Excel(name = "出生日期",width = 20,format = "yyyy-MM-dd")
private LocalDate birthday;

@ApiModelProperty(value = "身份证号")
@Excel(name = "身份证号",width = 30)
private String idCard;

@ApiModelProperty(value = "婚姻状况")
@Excel(name = "婚姻状况")
private String wedlock;

@ApiModelProperty(value = "民族")
private Integer nationId;

@ApiModelProperty(value = "籍贯")
@Excel(name = "籍贯")
private String nativePlace;

@ApiModelProperty(value = "政治面貌")
private Integer politicId;

@ApiModelProperty(value = "邮箱")
@Excel(name = "邮箱",width = 30)
private String email;

@ApiModelProperty(value = "电话号码")
@Excel(name = "电话号码",width = 15)
private String phone;

@ApiModelProperty(value = "联系地址")
@Excel(name = "联系地址",width = 40)
private String address;

@ApiModelProperty(value = "所属部门")
private Integer departmentId;

@ApiModelProperty(value = "职称ID")
private Integer jobLevelId;

@ApiModelProperty(value = "职位ID")
private Integer posId;

@ApiModelProperty(value = "聘用形式")
@Excel(name = "聘用形式")
private String engageForm;

@ApiModelProperty(value = "最高学历")
@Excel(name = "最高学历")
private String tiptopDegree;

@ApiModelProperty(value = "所属专业")
@Excel(name = "所属专业",width = 20)
private String specialty;

@ApiModelProperty(value = "毕业院校")
@Excel(name = "毕业院校",width = 20)
private String school;

@ApiModelProperty(value = "入职日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
@Excel(name = "入职日期",width = 20,format = "yyyy-MM-dd")
private LocalDate beginDate;

@ApiModelProperty(value = "在职状态")
@Excel(name = "在职状态")
private String workState;

@ApiModelProperty(value = "工号")
@Excel(name = "工号")
private String workID;

@ApiModelProperty(value = "合同期限")
@Excel(name = "合同期限",suffix = "年")
private Double contractTerm;

@ApiModelProperty(value = "转正日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
@Excel(name = "转正日期",width = 20,format = "yyyy-MM-dd")
private LocalDate conversionTime;

@ApiModelProperty(value = "离职日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
private LocalDate notWorkDate;

@ApiModelProperty(value = "合同起始日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
@Excel(name = "合同起始日期",width = 20,format = "yyyy-MM-dd")
private LocalDate beginContract;

@ApiModelProperty(value = "合同终止日期")
@JsonFormat(pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
@Excel(name = "合同终止日期",width = 20,format = "yyyy-MM-dd")
private LocalDate endContract;

@ApiModelProperty(value = "工龄")
private Integer workAge;

@ApiModelProperty(value = "工资账套ID")
private Integer salaryId;

@ApiModelProperty(value = "民族")
@TableField(exist = false)
@ExcelEntity(name = "民族")
private Nation nation;

@ApiModelProperty(value = "政治面貌")
@TableField(exist = false)
@ExcelEntity(name = "政治面貌")
private PoliticsStatus politicsStatus;

@ApiModelProperty(value = "部门")
@TableField(exist = false)
@ExcelEntity(name = "部门")
private Department department;

@ApiModelProperty(value = "职称")
@TableField(exist = false)
@ExcelEntity(name = "职称")
private Joblevel joblevel;

@ApiModelProperty(value = "职位")
@TableField(exist = false)
@ExcelEntity(name = "职位")
private Position position;
```

### （5）在Nation类，PoliticsStatus类，Department类，Joblevel类，Position类name属性上加入@Excel(name = "民族")注解

```java
@Data
@EqualsAndHashCode(callSuper = false,of = "name")
@NoArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
@TableName("t_nation")
@ApiModel(value="Nation对象", description="")
public class Nation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "民族")
    @Excel(name = "民族")
    @NonNull
    private String name;

}
```

## 1.7 Excel导入

### （1）EmployeeController

```java
@ApiOperation(value = "导入员工数据")
@PostMapping("/import")
public RespBean importEmployee(MultipartFile file){
    ImportParams params = new ImportParams();
    //去掉标题行
    params.setTitleRows(1);
    List<Nation> nationList = nationService.list();
    List<PoliticsStatus> politicsStatusList = politicsStatusService.list();
    List<Department> departmentList = departmentService.list();
    List<Joblevel> joblevelList = joblevelService.list();
    List<Position> positionList = positionService.list();
    try {
        List<Employee> list = ExcelImportUtil.importExcel(file.getInputStream(), Employee.class, params);
        list.forEach(employee -> {
            //民族id
            employee.setNationId(nationList.get(nationList.indexOf(new Nation(employee.getNation().getName()))).getId());
            //政治面貌id
            employee.setPoliticId(politicsStatusList.get(politicsStatusList.indexOf(new PoliticsStatus(employee.getPoliticsStatus().getName()))).getId());
            //部门id
            employee.setDepartmentId(departmentList.get(departmentList.indexOf(new Department(employee.getDepartment().getName()))).getId());
            //职称id
            employee.setJobLevelId(joblevelList.get(joblevelList.indexOf(new Joblevel(employee.getJoblevel().getName()))).getId());
            //职位id
            employee.setPosId(positionList.get(positionList.indexOf(new Position(employee.getPosition().getName()))).getId());
        });
        if (employeeService.saveBatch(list)){
            return RespBean.success("导入成功!");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return RespBean.error("导入失败！");
}
```

