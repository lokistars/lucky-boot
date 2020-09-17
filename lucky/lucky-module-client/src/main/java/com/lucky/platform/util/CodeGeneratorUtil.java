/*
package com.lucky.platform.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Arrays;

*/
/**
 * MyBatis-Plus 自动生成器
 * @author 53276
 */
/*
public class CodeGeneratorUtil {

    private static final String[] str = {"t_user"};

    public static void main(String[] args) {
        System.out.println(String.join(",",str));
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        //当前的项目路径
        String projectPath = System.getProperty("user.dir");
        //文件输出目录
        System.out.println(projectPath);
        gc.setOutputDir(projectPath + "/src/main/java");
        //设置作者信息
        gc.setAuthor("Nuany");
        //是否打开资源管理器
        gc.setOpen(false);
        //是否覆盖
        gc.setFileOverride(false);
        //去掉Service I前缀
        gc.setServiceName("%sService");
        //设置主键自增
        gc.setIdType(IdType.AUTO);
        //设置日期类型
        gc.setDateType(DateType.ONLY_DATE);
        //实体属性 Swagger2 注解
        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/kdbase?useUnicode=true&characterEncoding=utf8&serverTimezone=Hongkong&zeroDateTimeBehavior=round&allowMultiQueries=true&useSSL=false");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("netinfo");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //包的名字
        pc.setModuleName("platform");
        pc.setParent("com.lucky");
        //实体类名字
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //设置要映射的表名
        strategy.setInclude(String.join(",",str));
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(false);
        //strategy.setTablePrefix(pc.getModuleName() + "_");
        //开启驼峰命令
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);
        //执行
        mpg.execute();
    }
}
*/
